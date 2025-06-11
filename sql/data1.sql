DROP DATABASE IF EXISTS campus_db1;
CREATE DATABASE campus_db1;
USE campus_db1;

CREATE TABLE Users (
                       user_id INT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(100),
                       role ENUM('Student', 'Admin', 'Technician')
);

CREATE TABLE Equipment (
                           equipment_id INT AUTO_INCREMENT PRIMARY KEY,
                           type1 VARCHAR(50),
                           condition1 VARCHAR(50),
                           location VARCHAR(100),
                           availability ENUM('Available', 'Unavailable')
);

CREATE TABLE Bookings (
                          booking_id INT AUTO_INCREMENT PRIMARY KEY,
                          equipment_id INT,
                          user_id INT,
                          date_from DATE,
                          date_to DATE,
                          status1 ENUM('Assigned', 'Returned'),
                          FOREIGN KEY (equipment_id) REFERENCES Equipment(equipment_id),
                          FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE MaintenanceLogs (
                                 log_id INT AUTO_INCREMENT PRIMARY KEY,
                                 equipment_id INT,
                                 technician_id INT,
                                 date1 DATE,
                                 description1 TEXT,
                                 FOREIGN KEY (equipment_id) REFERENCES Equipment(equipment_id),
                                 FOREIGN KEY (technician_id) REFERENCES Users(user_id)
);

CREATE TABLE UsageCounter (
                              equipment_id INT PRIMARY KEY,
                              usage_count INT,
                              FOREIGN KEY (equipment_id) REFERENCES Equipment(equipment_id)
);

CREATE TABLE OverdueAlerts (
                               alert_id INT AUTO_INCREMENT PRIMARY KEY,
                               booking_id INT,
                               alert_message TEXT,
                               FOREIGN KEY (booking_id) REFERENCES Bookings(booking_id)
);

-- Sample Data
INSERT INTO Users (user_id, name, role) VALUES
                                            (1, 'Kaif', 'Student'),
                                            (2, 'Zarana', 'Student'),
                                            (3, 'Charlie', 'Admin'),
                                            (4, 'David', 'Technician'),
                                            (5, 'Eve', 'Technician');

INSERT INTO Equipment (equipment_id, type1, condition1, location, availability) VALUES
                                                                                    (1, 'Laptop', 'Good', 'Lab A', 'Available'),
                                                                                    (2, 'Projector', 'Needs Maintenance', 'Room B', 'Unavailable'),
                                                                                    (3, 'Camera', 'Good', 'Media Center', 'Available'),
                                                                                    (4, 'Laptop', 'Fair', 'Lab B', 'Available'),
                                                                                    (5, 'Microphone', 'Good', 'Auditorium', 'Available');

INSERT INTO Bookings (equipment_id, user_id, date_from, date_to, status1) VALUES
                                                                              (1, 1, '2025-06-02', '2025-06-04', 'Assigned'),
                                                                              (2, 2, '2025-06-03', '2025-06-05', 'Assigned'),
                                                                              (3, 1, '2025-06-01', '2025-06-02', 'Returned');

INSERT INTO MaintenanceLogs (log_id, equipment_id, technician_id, date1, description1) VALUES
                                                                                           (1, 2, 4, '2025-05-20', 'Lens calibration'),
                                                                                           (2, 2, 5, '2025-05-28', 'Power issue fixed');

INSERT INTO UsageCounter (equipment_id, usage_count) VALUES
                                                         (1, 5), (2, 15), (3, 2), (4, 10), (5, 1);

-- Trigger for overdue returns
DELIMITER $$
CREATE TRIGGER trg_check_overdue_return
    AFTER UPDATE ON Bookings
    FOR EACH ROW
BEGIN
    IF NEW.status1 = 'Returned' AND NEW.date_to < CURDATE() THEN
        INSERT INTO OverdueAlerts (booking_id, alert_message)
        VALUES (NEW.booking_id, CONCAT('Booking ', NEW.booking_id, ' was returned late.'));
END IF;
END $$
DELIMITER ;

-- Stored Procedures
DELIMITER $$
CREATE PROCEDURE AssignTechnician(
    IN tech_id INT,
    IN equip_id INT,
    IN log_text TEXT
)
BEGIN
INSERT INTO MaintenanceLogs (equipment_id, technician_id, date1, description1)
VALUES (equip_id, tech_id, CURDATE(), log_text);

UPDATE Equipment
SET availability = 'Unavailable', condition1 = 'Needs Maintenance'
WHERE equipment_id = equip_id;
END $$

DELIMITER $$
CREATE PROCEDURE SafeBookEquipment(
    IN in_equipment_id INT,
    IN in_user_id INT,
    IN in_date_from DATE,
    IN in_date_to DATE,
    OUT result_message VARCHAR(100)
)
BEGIN
    DECLARE avail VARCHAR(20);
    DECLARE conflict_count INT;

START TRANSACTION;

-- Step 1: Check availability
SELECT availability INTO avail
FROM Equipment
WHERE equipment_id = in_equipment_id FOR UPDATE;

-- Step 2: Check for booking conflicts
SELECT COUNT(*) INTO conflict_count
FROM Bookings
WHERE equipment_id = in_equipment_id AND status1 = 'Assigned'
  AND (in_date_from BETWEEN date_from AND date_to OR in_date_to BETWEEN date_from AND date_to);

-- Step 3: Decision
IF avail = 'Available' AND conflict_count = 0 THEN
        INSERT INTO Bookings (equipment_id, user_id, date_from, date_to, status1)
        VALUES (in_equipment_id, in_user_id, in_date_from, in_date_to, 'Assigned');
COMMIT;
SET result_message = 'Booking successful!';
ELSE
        ROLLBACK;
        SET result_message = 'Booking failed: equipment unavailable or already booked.';
END IF;
END$$

DELIMITER ;

DELIMITER $$
CREATE PROCEDURE ReturnEquipment(
    IN in_booking_id INT
)
BEGIN
    DECLARE equip_id INT;

START TRANSACTION;

UPDATE Bookings
SET status1 = 'Returned'
WHERE booking_id = in_booking_id;

SELECT equipment_id INTO equip_id FROM Bookings WHERE booking_id = in_booking_id;

UPDATE UsageCounter
SET usage_count = usage_count + 1
WHERE equipment_id = equip_id;

COMMIT;
END $$

CREATE PROCEDURE CleanupOldOverdueAlerts()
BEGIN
START TRANSACTION;
DELETE FROM OverdueAlerts
WHERE booking_id IN (
    SELECT booking_id FROM Bookings
    WHERE DATEDIFF(CURDATE(), date_to) > 7
);
COMMIT;
END $$
DELIMITER ;



DROP PROCEDURE IF EXISTS ExtendBookingPeriod;
DELIMITER //

CREATE PROCEDURE ExtendBookingPeriod (
    IN bookingId INT,
    IN newDateTo DATE,
    OUT result_message VARCHAR(100)
)
BEGIN
    DECLARE equipmentId INT;
    DECLARE currentDateFrom DATE;
    DECLARE conflictCount INT DEFAULT 0;

START TRANSACTION;

SELECT equipment_id, date_from INTO equipmentId, currentDateFrom
FROM Bookings
WHERE booking_id = bookingId;

SELECT COUNT(*) INTO conflictCount
FROM Bookings
WHERE equipment_id = equipmentId
  AND booking_id != bookingId
      AND date_from <= newDateTo
      AND date_to >= currentDateFrom;

IF conflictCount > 0 THEN
        ROLLBACK;
        SET result_message = 'Extension failed: conflicting booking exists.';
ELSE
UPDATE Bookings
SET date_to = newDateTo
WHERE booking_id = bookingId;
COMMIT;
SET result_message = 'Booking extended successfully!';
END IF;
END;
//

DELIMITER ;


