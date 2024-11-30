package manager_queries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import java.util.HashMap;
import java.util.Map;

public class XReportQueries {
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
     * Retrieve X Report for the current day, showing sales per hour
     * 
     * @return Map containing the hourly sales report
     */
    public static Map<String, Map<String, Object>> getXReport() {
        connect();
        Map<String, Map<String, Object>> report = new HashMap<>();
        
        String query = "SELECT \n" +
               "    DATE_TRUNC('hour', s.order_date) AS \"Hour\", \n" +
               "    COUNT(s.id) AS \"Total Sales\", \n" +
               "    SUM(s.total_price) AS \"Total Sales Value\" \n" +
               "FROM \n" +
               "    sale s \n" +
               "WHERE \n" +
               "    DATE(s.order_date) = CURRENT_DATE \n" +
               "GROUP BY \n" +
               "    \"Hour\" \n" +
               "ORDER BY \n" +
               "    \"Hour\";";


        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String hour = rs.getTimestamp("Hour").toLocalDateTime().toLocalTime().toString(); // Get hour as string
                int totalSales = rs.getInt("Total Sales");
                float totalSalesValue = rs.getFloat("Total Sales Value");

                Map<String, Object> hourDetails = new HashMap<>();
                hourDetails.put("Total Sales", totalSales);
                hourDetails.put("Total Sales Value", totalSalesValue);

                report.put(hour, hourDetails);
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving X Report: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error getting X report:\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return report;
    }

    public static void main(String args[]) {
        Map<String, Map<String, Object>> report = getXReport();

        // Print the X Report
        System.out.printf("%-15s %-15s %-20s%n", "Hour", "Total Sales", "Total Sales Value");
        System.out.println("-----------------------------------------------------------");
        report.forEach((hour, details) -> {
            System.out.printf("%-15s %-15d %-20.2f%n", hour, details.get("Total Sales"), details.get("Total Sales Value"));
        });
    }
}
