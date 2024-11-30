package manager_queries;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Timestamp;

public class SalesReportQueries {
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
        }
    }

        /**
     * Retrieve sales report for a specified date range.
     * 
     * @param startDate - Start date for the report
     * @param endDate   - End date for the report
     * @return Map<String, Integer> containing the sales report
     */
    public static Map<String, Integer> getReport(Date startDate, Date endDate) {
        connect(); // Assume this method opens a connection to the database
        Map<String, Integer> report = new HashMap<>();
        String query = "SELECT \n" +
                    "    i.name AS \"Item Name\", \n" +
                    "    COUNT(DISTINCT s.id) AS \"Total Sales\" \n" +
                    "FROM \n" +
                    "    sale s \n" +
                    "JOIN \n" +
                    "    sold_sellable ss ON s.id = ss.sale_id \n" +
                    "JOIN \n" +
                    "    sold_item si ON ss.id = si.sold_sellable_id \n" +
                    "JOIN \n" +
                    "    item i ON si.item_id = i.id \n" +
                    "WHERE \n" +
                    "    s.order_date BETWEEN ? AND ? \n" +
                    "GROUP BY \n" +
                    "    i.name \n" +
                    "ORDER BY \n" +
                    "    \"Total Sales\" DESC;";

        String query2 = "SELECT \n" +
                        "    se.name AS \"Drink Size\", \n" +
                        "    COUNT(s.id) AS \"Number of Sales\" \n" +
                        "FROM \n" +
                        "    sale s \n" +
                        "JOIN \n" +
                        "    sold_sellable ss ON s.id = ss.sale_id \n" +
                        "JOIN \n" +
                        "    sellable se ON ss.sellable_id = se.id \n" +
                        "WHERE \n" +
                        "    se.name IN ('Small Drink', 'Medium Drink', 'Large Drink') \n" +
                        "    AND s.order_date BETWEEN ? AND ? \n" +
                        "GROUP BY \n" +
                        "    se.name \n" +
                        "ORDER BY \n" +
                        "    se.name;";
        try {
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setDate(1, startDate);
                stmt.setDate(2, endDate);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String itemName = rs.getString("Item Name");
                    int totalSales = rs.getInt("Total Sales");

                    report.put(itemName, totalSales);
                }
            } 
            try (PreparedStatement stmt = connection.prepareStatement(query2)) {
                stmt.setDate(1, startDate);
                stmt.setDate(2, endDate);

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String itemName = rs.getString("Drink Size");
                    int totalSales = rs.getInt("Number of Sales");

                    report.put(itemName, totalSales);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while retrieving report: " + e.getMessage());
        }

        return report;
    }


    /**
     * Retrieve the last 3 sales and return a formatted string
     *
     * @return formatted string of the last 3 sales
     */
    public static String lastThreeSales() {
        connect();
        String query = "SELECT id, total_price, order_date FROM sale ORDER BY order_date DESC LIMIT 3";
        StringBuilder result = new StringBuilder("Last 3 Sales:\n");

        try (var stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            List<String> salesData = new ArrayList<>();

            while (rs.next()) {
                int saleId = rs.getInt("id");
                float totalPrice = rs.getFloat("total_price");
                Timestamp orderDate = rs.getTimestamp("order_date");

                // Format each sale information and add to the list
                salesData.add(String.format("Sale ID: %d | Total: $%.2f | Date: %s", 
                        saleId, totalPrice, orderDate.toString()));
            }

            // Join all sales data with newline characters
            result.append(String.join("\n", salesData));

        } catch (SQLException e) {
            System.out.println("Error retrieving sales: " + e.getMessage());
        }

        return result.toString();
    }


    public static void main(String args[]) {
        // // Example usage: Replace with actual dates as needed
        // Date startDate = Date.valueOf("2024-01-01");
        // Date endDate = Date.valueOf("2024-12-31");
        // Map<String, Integer> report = getReport(startDate, endDate);
        // // Map<String, Integer> report2 = getReport2(startDate, endDate);

        // // Print the report
        // report.forEach((itemId, details) -> {
        //     System.out.printf("Item Name: %s, Details: %d%n", itemId, details);
        // });

        // System.out.println("done");
    }
}