package manager_queries;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import java.util.ArrayList;

public class Inventory_queries {
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
     * Add a new inventory item to the database
     */
    public static void addInventoryItem(String name, int amt, int restock_amt) {
        try {
            // Connect and start a transaction
            connect();
            connection.setAutoCommit(false); // Start transaction

            // Get the last ID
            String getIDSQL = "SELECT id FROM inventory ORDER BY id DESC LIMIT 1;";
            try (PreparedStatement stmt = connection.prepareStatement(getIDSQL);
                ResultSet rs = stmt.executeQuery()) {
                int lastId = 1; // Default value if no ID is found
                if (rs.next()) {
                    lastId = rs.getInt("id") + 1; // Increment the ID to get the next one
                }

                // Insert new item into the inventory table
                String insertItemSQL = "INSERT INTO inventory (id, name, amt, restock_amt) VALUES (?, ?, ?, ?);";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertItemSQL)) {
                    insertStmt.setInt(1, lastId); // Set the new ID
                    insertStmt.setString(2, name); // Set the name
                    insertStmt.setInt(3, amt); // Set the amount
                    insertStmt.setInt(4, restock_amt); // Set the restock amount

                    // Execute the insert statement
                    insertStmt.executeUpdate();
                    connection.commit(); // Commit the transaction
                    // System.out.println("Item added: " + name);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error adding inventory item:\n\n" + e.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            try {
                if (connection != null) {
                    connection.rollback(); // Rollback in case of error
                }
            } catch (SQLException rollbackEx) {
                System.out.println("Rollback failed: " + rollbackEx.getMessage());
            }
        } finally {
            resetAutoCommit();
        }
    }

    /**
     * View the entire inventory
     */
    public static String[][] viewInventory() {
        ArrayList<String[]> inventoryList = new ArrayList<>();
        try {
            connect();
            String viewSQL = "SELECT * FROM inventory;";
            try (PreparedStatement stmt = connection.prepareStatement(viewSQL);
                ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    // Create a row array with columns: id, name, amt, restock_amt
                    String[] row = new String[4];
                    row[0] = String.valueOf(rs.getInt("id"));
                    row[1] = rs.getString("name");
                    row[2] = String.valueOf(rs.getInt("amt"));
                    row[3] = String.valueOf(rs.getInt("restock_amt"));
                    inventoryList.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error fetching inventory:\n\n" + e.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // Convert the list to a 2D array
        String[][] inventoryArray = new String[inventoryList.size()][4];
        return inventoryList.toArray(inventoryArray);
    }

    /**
     * View items that need restocking
     */
    public static String[][] getInventoryRestock() {
        ArrayList<String[]> restockList = new ArrayList<>();
        try {
            connect();
            String restockSQL = "SELECT * FROM inventory WHERE amt < restock_amt;";
            try (PreparedStatement stmt = connection.prepareStatement(restockSQL);
                ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    // Create a row array with columns: id, name, amt, restock_amt
                    String[] row = new String[4];
                    row[0] = String.valueOf(rs.getInt("id"));
                    row[1] = rs.getString("name");
                    row[2] = String.valueOf(rs.getInt("amt"));
                    row[3] = String.valueOf(rs.getInt("restock_amt"));
                    restockList.add(row);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error fetching restock report:\n\n" + e.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        // Convert the list to a 2D array
        String[][] restockArray = new String[restockList.size()][4];
        return restockList.toArray(restockArray);
    }

    /**
     * Update the inventory amount based on item name
     */
    public static void editInventoryAmount(String name, int newAmount) {
        try {
            connect();
            // Step 1: Find the ID based on the name
            String findIdSQL = "SELECT id FROM inventory WHERE name = ?;";
            try (PreparedStatement findStmt = connection.prepareStatement(findIdSQL)) {
                findStmt.setString(1, name);
                ResultSet rs = findStmt.executeQuery();

                if (rs.next()) {
                    int id = rs.getInt("id"); // Get the ID of the item

                    // Step 2: Update the inventory amount for the found ID
                    String updateSQL = "UPDATE inventory SET amt = ? WHERE id = ?;";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateSQL)) {
                        updateStmt.setInt(1, newAmount);
                        updateStmt.setInt(2, id);

                        int rowsAffected = updateStmt.executeUpdate();
                        if (rowsAffected > 0) {
                            System.out.println("Inventory updated successfully for item: " + name);
                        } else {
                            System.out.println("Failed to update inventory for item: " + name);
                        }
                    }
                } else {
                    System.out.println("Item with name '" + name + "' not found.");
                    JOptionPane.showMessageDialog(null, "Item with name '" + name + "' not found.", "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error editing inventory:\n\n" + e.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Reset auto-commit mode
     */
    private static void resetAutoCommit() {
        try {
            if (connection != null) {
                connection.setAutoCommit(true); // Reset auto-commit mode
            }
        } catch (SQLException e) {
            System.out.println("Error resetting auto-commit: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error resetting auto-commit:\n\n" + e.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Connect to the database
        connect();
    
        // Test case 1: Add inventory items
        System.out.println("Adding new inventory items...");
        addInventoryItem("Apple", 50, 10);  // Add 50 apples, restock when below 10
        addInventoryItem("Banana", 20, 5);  // Add 20 bananas, restock when below 5
        addInventoryItem("Orange", 0, 15);  // Add 0 oranges, restock when below 15
        addInventoryItem("Grapes", 30, 20); // Add 30 grapes, restock when below 20
        addInventoryItem("Watermelon", 10, 5); // Add 10 watermelons, restock when below 5
        System.out.println("Items added successfully.\n");
    
        // Test case 2: View the entire inventory
        System.out.println("Viewing the entire inventory:");
        String[][] inventory = viewInventory();
        for (String[] item : inventory) {
            System.out.println(String.join(", ", item));
        }
        System.out.println("Total items in inventory: " + inventory.length + "\n");
    
        // Test case 3: View items that need restocking
        System.out.println("Viewing items that need restocking:");
        String[][] restockItems = getInventoryRestock();
        for (String[] item : restockItems) {
            System.out.println(String.join(", ", item));
        }
        System.out.println("Total items that need restocking: " + restockItems.length + "\n");
    
        // Test case 4: Edit inventory amount for existing items
        System.out.println("Updating inventory amount for 'Apple'...");
        editInventoryAmount("Apple", 75); // Update the amount of "Apple" to 75
    
        System.out.println("Updating inventory amount for 'Orange'...");
        editInventoryAmount("Orange", 50); // Update the amount of "Orange" to 50
    
        // Test case 5: View the updated inventory
        System.out.println("Viewing the updated inventory:");
        inventory = viewInventory();
        for (String[] item : inventory) {
            System.out.println(String.join(", ", item));
        }
        System.out.println("Total items in inventory after updates: " + inventory.length + "\n");
    
        // Test case 6: View the updated restocking list
        System.out.println("Viewing updated items that need restocking:");
        restockItems = getInventoryRestock();
        for (String[] item : restockItems) {
            System.out.println(String.join(", ", item));
        }
        System.out.println("Total items that still need restocking: " + restockItems.length);
    }
    
}
