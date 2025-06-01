CREATE TABLE Users (
                       user_id INT PRIMARY KEY,
                       name VARCHAR(100),
                       role ENUM('Student', 'Admin', 'Technician')
);

CREATE TABLE Equipment (
                           equipment_id INT PRIMARY KEY,
                           type VARCHAR(50),
                           condition VARCHAR(50),
                           location VARCHAR(100),
                           availability ENUM('Available', 'Unavailable')
);

CREATE TABLE Bookings (
                          booking_id INT PRIMARY KEY,
                          equipment_id INT,
                          user_id INT,
                          date_from DATE,
                          date_to DATE,
                          status ENUM('Pending', 'Approved', 'Returned'),
                          FOREIGN KEY (equipment_id) REFERENCES Equipment(equipment_id),
                          FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

CREATE TABLE MaintenanceLogs (
                                 log_id INT PRIMARY KEY,
                                 equipment_id INT,
                                 technician_id INT,
                                 date DATE,
                                 description TEXT,
                                 FOREIGN KEY (equipment_id) REFERENCES Equipment(equipment_id),
                                 FOREIGN KEY (technician_id) REFERENCES Users(user_id)
);

CREATE TABLE UsageCounter (
                              equipment_id INT PRIMARY KEY,
                              usage_count INT,
                              FOREIGN KEY (equipment_id) REFERENCES Equipment(equipment_id)
);

CREATE TABLE OverdueAlerts (
                               alert_id INT PRIMARY KEY,
                               booking_id INT,
                               alert_message TEXT,
                               FOREIGN KEY (booking_id) REFERENCES Bookings(booking_id)
);
