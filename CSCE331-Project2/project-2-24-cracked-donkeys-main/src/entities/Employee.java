package entities;

import java.sql.Date;

class Employee {
    public final int id; // -1 if not yet stored in the database
    public String name;
    public Date startDay;
    public String jobPosition;
    public int accessLevel; // 0 for cashier, 1 for manager, -1 for inactive employee
    public int managerId;

    public Employee(int id, String name, Date startDay, String jobPosition, int accessLevel, int managerId) {
        this.id = id;
        this.name = name;
        this.startDay = startDay;
        this.jobPosition = jobPosition;
        this.accessLevel = accessLevel;
        this.managerId = managerId;
    }
}
