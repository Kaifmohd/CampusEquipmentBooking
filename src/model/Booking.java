package src.model;

import java.sql.Date;

public class Booking {
    private int bookingId;
    private int equipmentId;
    private int userId;
    private Date dateFrom;
    private Date dateTo;
    private String status1;

    public Booking() {}

    public Booking(int bookingId, int equipmentId, int userId, Date dateFrom, Date dateTo, String status1) {
        this.bookingId = bookingId;
        this.equipmentId = equipmentId;
        this.userId = userId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.status1 = status1;
    }

    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getEquipmentId() { return equipmentId; }
    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public Date getDateFrom() { return dateFrom; }
    public void setDateFrom(Date dateFrom) { this.dateFrom = dateFrom; }

    public Date getDateTo() { return dateTo; }
    public void setDateTo(Date dateTo) { this.dateTo = dateTo; }

    public String getStatus1() { return status1; }
    public void setStatus1(String status1) { this.status1 = status1; }

    @Override
    public String toString() {
        return "Booking #" + bookingId + " | User: " + userId + " | Equipment: " + equipmentId + " | " + status1;
    }
}

