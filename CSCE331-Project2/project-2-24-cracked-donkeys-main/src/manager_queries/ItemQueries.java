package manager_queries;

import data_classes.Item_Edit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ItemQueries {
    // use to access our postgressql database and perform insertions and accesses
    private static final String url = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce331_24";
    private static final String user = "csce331_24";
    private static final String password = "cracked.donkey";
    private static Connection connection;

    private static final int seasonal_entree_min = 100;
    private static final int seasonal_entree_max = 199;
    private static final int seasonal_drink_min = 200;
    private static final int seasonal_drink_max = 299;
    private static final int seasonal_appetizer_min = 300;
    private static final int seasonal_appetizer_max = 399;
    private static final int[][] item_ids = {
                                            {seasonal_entree_min, seasonal_entree_max},
                                            {seasonal_drink_min, seasonal_drink_max},
                                            {seasonal_appetizer_min, seasonal_appetizer_max}
                                            };

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
            JOptionPane.showMessageDialog(null, "Error connecting to database:\n\n" + e.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Adds a new item to the database.
     * <p>
     * This method inserts a new item into the `item` table in the PostgreSQL database. The item's details 
     * (ID, name, and additional price) are provided as input. The function retrieves the next available item ID 
     * from the database and uses it in the insertion. If the ID retrieval fails, an exception is thrown, 
     * and the transaction is aborted.
     * 
     * @param item An `Item_Edit` object containing the item's details (name and additional price) to be added.
     * @throws SQLException If there's an error connecting to the database or performing the insert operation.
     * 
     */
    public static void addItem(Item_Edit item) {
        try {
            connect();

            // Get starting item id;
            int next_item_id = getNextItemID(item.item_type);

            // if didn't correctly retrieve an ID
            if (next_item_id < 0) {
                throw new SQLException("Couldn't retrieve current ID's");
            }

            // SQL statement
            String itemInsertSQL = "INSERT INTO item (id, name, add_price) VALUES (?, ?, ?)";

            // Insert item
            try (var itemStmt = connection.prepareStatement(itemInsertSQL)) {
                itemStmt.setInt(1, next_item_id);
                itemStmt.setString(2, item.name);
                itemStmt.setDouble(3, item.additionalPrice);
                int rowsAdded = itemStmt.executeUpdate();

                if (rowsAdded == 1) {
                    System.out.println("Item successfully added");
                }
                else {
                    System.out.println("Error adding item");
                }
            }
        }
        // catch and print errors if necessary
        catch (SQLException ex) {
            System.out.println("Error adding item: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error adding item:\n\n" + ex.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Sets the ID of an item to its negative counterpart based on the item name.
     * 
     * @param itemName - The name of the item whose ID needs to be updated
     * @return boolean - Returns true if the operation is successful, otherwise false
     */
    public static boolean removeItem(String itemName) {
        connect(); // Ensure database connection is established
        
        String selectSQL = "SELECT id FROM item WHERE name = ?";
        String updateSQL = "UPDATE item SET id = ? WHERE name = ?";
        
        try {
            // Retrieve the current ID of the item based on the item name
            try (var selectStmt = connection.prepareStatement(selectSQL)) {
                selectStmt.setString(1, itemName);
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int currentID = rs.getInt("id");
                    int negativeID = currentID;
                    if (currentID > 0) {
                        negativeID = -currentID;
                    }

                    // Update the ID to its negative counterpart
                    try (var updateStmt = connection.prepareStatement(updateSQL)) {
                        updateStmt.setInt(1, negativeID);
                        updateStmt.setString(2, itemName);
                        int rowsAffected = updateStmt.executeUpdate();

                        return rowsAffected > 0; // Return true if the update was successful
                    }
                } else {
                    System.out.println("Item not found with name: " + itemName);
                    JOptionPane.showMessageDialog(null, "Item not found with name: " + itemName, "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error while setting negative ID: " + e.getMessage());
            JOptionPane.showMessageDialog(null, "Error removing item:\n\n" + e.getLocalizedMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return false; // Return false if the operation failed
    }


    /**
     * Updates the add_price of a specific item in the database.
     * <p>
     * This method connects to the PostgreSQL database and updates the `add_price` of an existing item 
     * in the `item` table where the `name` and current `add_price` match the given parameters. 
     * 
     * @param item An `Item_Edit` object containing the item's details (name and additional price) to be reset.
     * @throws SQLException If an error occurs while connecting to the database or executing the update statement.
     */
    public static void updateItemAddPrice(Item_Edit item) {
        // Connect to the database
        try {
            connect();

            // SQL update statement
            String updateSQL = "UPDATE item SET add_price = ? WHERE name = ? AND id > 0";

            // Prepare the statement and execute it
            try (var updateStmt = connection.prepareStatement(updateSQL)) {
                // Set the new add_price and match the item by its name and current add_price
                updateStmt.setDouble(1, item.additionalPrice);
                updateStmt.setString(2, item.name);

                // Execute the update statement
                int rowsUpdated = updateStmt.executeUpdate();

                // Provide feedback
                if (rowsUpdated > 0) {
                    System.out.println("Item price updated successfully.");
                } else {
                    System.out.println("No matching item was found for the given criteria.");
                    JOptionPane.showMessageDialog(null, "Couldn't find item: " + item.name, "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error updating item price: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error updating item price:\n\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Returns next avaliable item id
     * 
     * @return next item id
     */
    public static int getNextItemID(int item_type) {

        try {
            // create a statement object
            // Statement stmt = connection.createStatement();

            connect();
            // create an SQL statement

            String sqlStatement = "SELECT id FROM item WHERE id BETWEEN ? AND ? ORDER BY id DESC LIMIT 1";
            // send statement to DBMS
            try (var itemIDStmt = connection.prepareStatement(sqlStatement)) {
                // Set the new add_price and match the item by its name and current add_price
                itemIDStmt.setInt(1, item_ids[item_type][0]);
                itemIDStmt.setInt(2, item_ids[item_type][1]);

                ResultSet result = itemIDStmt.executeQuery();

                if (result.next()) {
                    return result.getInt("id") + 1;
                } else {
                    return  item_ids[item_type][0];
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Error getting next item ID:\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        // if error return -2
        return -2;
    }

    /**
     * Retrieves the name and additional price of all items in the `item` table
     * and returns the results in a 2D array.
     * <p>
     * This method connects to the PostgreSQL database, retrieves all rows from the `item` table,
     * and returns a 2D array where each row contains the item's name and additional price.
     * 
     * @return A 2D array where the first column is the name and the second column is the additional price.
     *         Returns null if an error occurs.
     */
    public static String[][] displayItems() {
        // Connect to the database
        try {
            connect();

            // SQL query to count the total number of rows in the item table
            String countSQL = "SELECT COUNT(*) AS row_count FROM item where id > 0";
            int rowCount = 0;

            // Prepare and execute the count query to determine array size
            try (var countStmt = connection.prepareStatement(countSQL);
                var countResultSet = countStmt.executeQuery()) {
                if (countResultSet.next()) {
                    rowCount = countResultSet.getInt("row_count");
                }
            }

            // If no rows found, return empty array
            if (rowCount == 0) {
                return new String[0][0];
            }

            // SQL query to select name and add_price from item table
            String selectSQL = "SELECT * FROM item WHERE id > 0";

            // Prepare and execute the select query
            try (var selectStmt = connection.prepareStatement(selectSQL);
                var resultSet = selectStmt.executeQuery()) {

                // Create a 2D array to store the results
                String[][] itemsArray = new String[rowCount][3];
                int row = 0;

                // Iterate through the result set and store each item's name and additional price in the array
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String addPrice = String.valueOf(resultSet.getDouble("add_price"));

                    itemsArray[row][0] = String.valueOf(id);
                    itemsArray[row][1] = name;
                    itemsArray[row][2] = addPrice;
                    row++;
                }

                return itemsArray;  // Return the populated 2D array

            } catch (SQLException ex) {
                System.out.println("Error retrieving items: " + ex.getMessage());
                JOptionPane.showMessageDialog(null, "Error retrieving items:\n\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            System.out.println("Error connecting to the database: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Error displaying items:\n\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }

        return null;  // Return null if there was an error
    }

    public static void main(String args[]) {

    }

}
