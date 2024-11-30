package manager_queries;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Inventory_History_queries {
    // Connection details
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
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println("Connection error: " + e.getMessage());
        }
    }

    /**
     * Query the inventory history between two timestamps
     */
    public static ArrayList<String[]> getInventoryHistoryBetween(Date startTimestamp, Date endTimestamp) {
        ArrayList<String[]> historyList = new ArrayList<>();
        try {
            connect();
            String querySQL = "SELECT name, sum(amt_decremented) as amt FROM inventory_history WHERE timestamp BETWEEN ? AND ? GROUP BY name;";
            try (PreparedStatement stmt = connection.prepareStatement(querySQL)) {
                stmt.setDate(1, startTimestamp);
                stmt.setDate(2, endTimestamp);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String[] row = new String[4];
                        // row[0] = String.valueOf(rs.getInt("id"));
                        row[0] = rs.getString("name");
                        row[1] = String.valueOf(rs.getInt("amt"));
                        // row[3] = rs.getString("timestamp");
                        historyList.add(row);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error fetching inventory history: " + e.getMessage());
        }
        return historyList;
    }

    // public static void main(String[] args) {
    //     // Connect to the database
    //     connect();

    //     // Test case: Query inventory history between two timestamps
    //     String startTimestamp = "2022-01-20 00:00:00";
    //     String endTimestamp = "2022-02-01 23:59:59";

    //     System.out.println("Querying inventory history between " + startTimestamp + " and " + endTimestamp);
    //     ArrayList<String[]> history = getInventoryHistoryBetween(startTimestamp, endTimestamp);

    //     for (String[] entry : history) {
    //         System.out.println(String.join(", ", entry));
    //     }

    //     System.out.println("Total entries between the timestamps: " + history.size());
    // }
}
