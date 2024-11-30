package manager_queries;

import data_classes.Employee;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.util.ArrayList;

public class EmployeeQueries {

    // use to access our postgressql database and perform insertions and accesses
    private static final String url = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce331_24";
    private static final String user = "csce331_24";
    private static final String password = "cracked.donkey";
    private static Connection connection;

    /**
     * Connect to the PostgreSQL database
     * 
     * @throws SQLException
     */
    public static void connect() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Couldn't connect to database:\n\n" + e.getLocalizedMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void addEmployee(Employee employee) {
        try {
            connect();

            // Get starting employee id;
            int next_employee_id = getNextEmployeeID();

            // if didn't correctly retrieve an ID
            if (next_employee_id < 0) {
                throw new SQLException("Couldn't retrieve current ID's");
            }

            // SQL statement
            String employeeInsertSQL = "INSERT INTO employee (id, name, start_day, job_position, access_level, manager_id) VALUES (?, ?, ?, ?, ?, ?);";

            // Insert employee
            try (var employeeStmt = connection.prepareStatement(employeeInsertSQL)) {
                employeeStmt.setInt(1, next_employee_id);
                employeeStmt.setString(2, employee.name);
                employeeStmt.setDate(3, employee.startDay);
                employeeStmt.setString(4, employee.jobPosition);
                employeeStmt.setInt(5, employee.accessLevel);
                employeeStmt.setInt(6, employee.managerId);

                int rowsAdded = employeeStmt.executeUpdate();
                // System.out.println(employeeStmt + ";");

                // int rowsAdded = 1;

                if (rowsAdded == 1) {
                    System.out.println("Employee successfully added");
                }
                else {
                    System.out.println("Error adding employee");
                    JOptionPane.showMessageDialog(null, "Error adding employee", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        // catch and print errors if necessary
        catch (SQLException ex) {
            System.out.println("Error adding employee: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error adding employee:\n\n" + ex.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Removes an employee from the database based on their name.
     * <p>
     * This method connects to the PostgreSQL database and deletes a row from the `employee` table where
     * the `name` match is provided. If no matching employee is found, no rows are deleted.
     * 
     * @param name The name of the employee to be removed.
     * @throws SQLException If an error occurs while connecting to the database or executing the delete statement.
     */
    public static void removeEmployee(String name) {
        // Connect to the database
        try {
            connect();

            // SQL delete statement
            String removeSQL = "UPDATE employee SET access_level = -1 where NAME = ?";

            // Prepare the statement and execute it
            try (var deleteStmt = connection.prepareStatement(removeSQL)) {
                // Set the values for the parameters
                deleteStmt.setString(1, name);

                // Execute the delete statement
                int affectedRows = deleteStmt.executeUpdate();

                // Provide feedback
                if (affectedRows > 0) {
                    System.out.println("Employee successfully removed.");
                } else {
                    System.out.println("No employee matching the given criteria was found.");
                    JOptionPane.showMessageDialog(null, "Couldn't find employee: " + name, "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error removing employee: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error removing employee:\n\n" + ex.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Updates an existing employee's job position, access level, and employee ID in the database.
     * Finds the employee by their name and start date.
     * 
     * @param updatedEmployee The Employee object with updated fields.
     * @throws SQLException If the update operation fails.
     */
    public static void editEmployee(Employee updatedEmployee) {
        connect(); // Ensure the connection to the database is established

        String updateSQL = "UPDATE employee SET job_position = ?, access_level = ?, manager_id = ? WHERE name = ? AND start_day = ?";

        try (var stmt = connection.prepareStatement(updateSQL)) {
            stmt.setString(1, updatedEmployee.jobPosition);       // Update job position
            stmt.setInt(2, updatedEmployee.accessLevel);          // Update access level
            stmt.setInt(3, updatedEmployee.managerId);            // Update employee id (managerId)
            stmt.setString(4, updatedEmployee.name);              // Find by name
            stmt.setDate(5, updatedEmployee.startDay);            // Find by start date
            
            int affectedRows = stmt.executeUpdate();              // Execute update statement
            
            if (affectedRows > 0) {
                System.out.println("Employee details updated successfully.");
            } else {
                System.out.println("No employee found with the given name and start date.");
                JOptionPane.showMessageDialog(null, "Couldn't find employee: " + updatedEmployee.name + " (started " + updatedEmployee.startDay.toString() + ")", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            System.out.println("Error updating employee: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error updating employee:\n\n" + e.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Retrieves all active employees (access level != -1) from the database,
     * and returns their details as a 2D array (excluding the employee ID).
     * 
     * @return A 2D array containing each employee's name, start date, job position, access level, and manager ID.
     * @throws SQLException If the query fails.
     */
    public static String[][] displayEmployees() {
        connect(); // Ensure the connection to the database is established

        String query = "SELECT * FROM employee WHERE access_level != -1";
        ArrayList<String[]> employeeList = new ArrayList<>();

        try (var stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            // Iterate over the ResultSet and add each employee to the list
            while (rs.next()) {
                int employeeId = rs.getInt("id");
                String name = rs.getString("name");
                String startDay = rs.getDate("start_day").toString(); // Convert Date to String
                String jobPosition = rs.getString("job_position");
                int accessLevel = rs.getInt("access_level");
                int managerId = rs.getInt("manager_id");

                // Add employee's details to the list
                employeeList.add(new String[]{String.valueOf(employeeId), name, startDay, jobPosition, String.valueOf(accessLevel), String.valueOf(managerId)});
            }

        } catch (SQLException e) {
            System.out.println("Error fetching employees: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error fetching employees:\n\n" + e.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        // Convert the ArrayList to a 2D array
        String[][] employeeArray = new String[employeeList.size()][5];
        for (int i = 0; i < employeeList.size(); i++) {
            employeeArray[i] = employeeList.get(i);
        }

        return employeeArray;
    }

    /**
     * Retrieves the next available employee ID in the database.
     * 
     * This method queries the `employee` table to find the highest `id` value 
     * and increments it by 1 to return the next available ID for inserting a new employee.
     * If there are no records in the `employee` table, the function returns -1, 
     * indicating that the table is empty and this would be the first employee.
     * 
     * In case of an error during the database query, the method returns -2.
     * 
     * @return The next available employee ID.
     *         Returns -1 if no employees exist in the database.
     *         Returns -2 if there is an error in executing the query.
     * 
     * @throws SQLException If the database access error occurs during statement execution.
     */
    public static int getNextEmployeeID() {
        try {
            // create a statement object
            Statement stmt = connection.createStatement();
            // create an SQL statement
            String sqlStatement = "SELECT id FROM employee ORDER BY id DESC LIMIT 1";
            // send statement to DBMS
            ResultSet result = stmt.executeQuery(sqlStatement);
            if (result.next()) {
                return result.getInt("id") + 1;
            } else {
                // if nonexistent return -1;
                return -1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Error getting next employee ID:\n\n" + e.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        // if error return -2
        return -2;
    }

    
    /**
     * Get Employee by ID
     * @param employeeId the ID of the employee
     * @return the Employee object or null if not found
     */
    public static Employee getEmployeeById(int employeeId) {
        connect();

        String selectSQL = "SELECT id, name, start_day, job_position, access_level, manager_id FROM employee WHERE id = ?";
        
        try (var pstmt = connection.prepareStatement(selectSQL)) {
            pstmt.setInt(1, employeeId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    Date startDay = rs.getDate("start_day");
                    String jobPosition = rs.getString("job_position");
                    int accessLevel = rs.getInt("access_level");
                    int managerId = rs.getInt("manager_id");

                    // Return the found employee object
                    return new Employee(id, name, startDay, jobPosition, accessLevel, managerId);
                } else {
                    System.out.println("No employee found with ID: " + employeeId);
                    JOptionPane.showMessageDialog(null, "Couldn't find employee with ID: " + employeeId, "Database Error", JOptionPane.ERROR_MESSAGE);
                    return null;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Error finding employee:\n\n" + e.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }


    public static void main(String[] args) {
        // long millis = System.currentTimeMillis();  
        // Date curr_date = new Date(millis);  
        // Employee e = new Employee("Billy Bob", curr_date, "Test Job", 3, 1);
        // addEmployee(e);
        // Employee e2 = new Employee("Billy Bob", curr_date, "Big Manager", 3, 1);
        // editEmployee(e2);
        // removeEmployee("Billy Bob");
        // var x = getEmployeeById(3);
        // System.out.println(x.id + " " + x.name + " " + x.startDay + " " + x.jobPosition + " " + x.accessLevel + " " + x.managerId);
        // var y = getEmployeeById(14);

        
    }
}