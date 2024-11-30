package data_classes;

import java.sql.Date;

public class Employee {
    public static final int cashier_access_level = 1;
    public static final int manager_access_level = 2;

    public int id = -1;
    public String name;
    public Date startDay;
    public String jobPosition;
    public int accessLevel; // 0 for cashier, 1 for manager, -1 for inactive employee
    public int managerId;

    public Employee(String name, Date startDay, String jobPosition, int accessLevel, int managerId) {
        this.name = name;
        this.startDay = startDay;
        this.jobPosition = jobPosition;
        this.accessLevel = accessLevel;
        this.managerId = managerId;
    }

    public Employee(int id, String name, Date startDay, String jobPosition, int accessLevel, int managerId) {
        this.id = id;
        this.name = name;
        this.startDay = startDay;
        this.jobPosition = jobPosition;
        this.accessLevel = accessLevel;
        this.managerId = managerId;
    }
}
