package manager_queries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;

public class ZReportQueries {
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
     * Retrieve Z Report for the current day
     * 
     * @return Map containing the daily totals from the Z Report
     */
    public static Map<String, Object> getZReport() {
        connect();
        Map<String, Object> report = new HashMap<>();
        
        // Query to get total sales and total sales value for the day
        String query = "SELECT \n" +
               "    COUNT(s.id) AS \"Total Sales\", \n" +
               "    SUM(s.total_price) AS \"Total Sales Value\" \n" +
               "FROM \n" +
               "    sale s \n" +
               "WHERE \n" +
               "    DATE(s.order_date) = CURRENT_DATE;";


        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int totalSales = rs.getInt("Total Sales");
                float totalSalesValue = rs.getFloat("Total Sales Value");

                report.put("Total Sales", totalSales);
                report.put("Total Sales Value", totalSalesValue);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving Z Report: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error getting Z report:\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return report;
    }

    public static void main(String args[]) {
        Map<String, Object> report = getZReport();

        // Print the Z Report
        System.out.printf("Total Sales: %d%n", report.get("Total Sales"));
        System.out.printf("Total Sales Value: %.2f%n", report.get("Total Sales Value"));
    }
}
