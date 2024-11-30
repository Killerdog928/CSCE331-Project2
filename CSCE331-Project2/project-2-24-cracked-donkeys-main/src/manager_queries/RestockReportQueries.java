package manager_queries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;

public class RestockReportQueries {
    // Use to access our PostgreSQL database and perform insertions and accesses
    private static final String url = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce331_24";
    private static final String user = "csce331_24";
    private static final String password = "cracked.donkey";
    private static Connection connection;

    /**
     * Connect to the PostgreSQL database
     */
    public static void connect() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the database.");
        } catch (SQLException e) {
            System.out.println("Connection failure: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error connecting to database:\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Retrieve restock report for items that need to be restocked
     * 
     * @return Map containing items that need restocking and how much
     */
    public static Map<String, Integer> getRestockReport() {
        connect();
        Map<String, Integer> restockReport = new HashMap<>();
        
        // Query to find items that need restocking
        String query = "SELECT \n" +
               "    i.name AS \"Item Name\", \n" +
               "    i.amt AS \"Current Amount\", \n" +
               "    i.restock_amt AS \"Restock Amount\", \n" +
               "    (i.restock_amt - i.amt) AS \"Amount Needed\" \n" +
               "FROM \n" +
               "    inventory i \n" +
               "WHERE \n" +
               "    i.amt <= (i.restock_amt * 0.60)  -- Condition for restocking \n";


        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String itemName = rs.getString("Item Name");
                int amountNeeded = rs.getInt("Amount Needed");

                restockReport.put(itemName, amountNeeded);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving restock report: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error getting restock report:\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return restockReport;
    }

    public static void main(String args[]) {
        Map<String, Integer> report = getRestockReport();

        // Print the restock report
        if (report.isEmpty()) {
            System.out.println("No items need to be restocked.");
        } else {
            System.out.println("Items that need restocking:");
            for (Map.Entry<String, Integer> entry : report.entrySet()) {
                System.out.printf("Item: %s, Amount Needed: %d%n", entry.getKey(), entry.getValue());
            }
        }
    }
}

