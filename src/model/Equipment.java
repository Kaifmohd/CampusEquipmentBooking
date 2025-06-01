package src.model;

public class Equipment {
    private int equipmentId;
    private String type1;
    private String condition1;
    private String location;
    private String availability;

    public Equipment() {}

    public Equipment(int equipmentId, String type1, String condition1, String location, String availability) {
        this.equipmentId = equipmentId;
        this.type1 = type1;
        this.condition1 = condition1;
        this.location = location;
        this.availability = availability;
    }

    public int getEquipmentId() { return equipmentId; }
    public void setEquipmentId(int equipmentId) { this.equipmentId = equipmentId; }

    public String getType1() { return type1; }
    public void setType1(String type1) { this.type1 = type1; }

    public String getCondition1() { return condition1; }
    public void setCondition1(String condition1) { this.condition1 = condition1; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    @Override
    public String toString() {
        return type1 + " [" + location + "] - " + condition1;
    }
}

