package employee_queries;

import data_classes.Sale;
import data_classes.Sellable;
import data_classes.Item;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Statement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.*;
import javax.swing.JOptionPane;

public class SaleQueries {
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
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "Error connecting to database:\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Input sale into database
     * 
     * @param sale
     */
    public static void insertSale(Sale sale) {
        try {
            connect();
            // connection.setAutoCommit(false); // Start transaction

            // Get starting IDs
            int next_sale_id = getNextSaleID();
            int next_sold_sellable_id = getNextSoldSellableID();
            int next_sold_item_id = getNextSoldItemID();

            // if didn't correctly retrieve an ID
            if (next_sale_id < 0 || next_sold_sellable_id < 0 || next_sold_item_id < 0) {
                throw new SQLException("Couldn't retrieve current ID's");
            }

            // SQL statements
            String saleInsertSQL = "INSERT INTO sale (id, employee_id, total_price, order_date) VALUES (?, ?, ?, ?)";
            String sellableInsertSQL = "INSERT INTO sold_sellable (id, sale_id, sellable_id) VALUES (?, ?, ?)";
            String itemInsertSQL = "INSERT INTO sold_item (id, item_id, sold_sellable_id, amount) VALUES (?, ?, ?, ?)";

            // Insert sale
            try (var saleStmt = connection.prepareStatement(saleInsertSQL)) {
                saleStmt.setInt(1, next_sale_id);
                saleStmt.setInt(2, sale.employeeId);
                saleStmt.setDouble(3, sale.totalPrice);
                saleStmt.setTimestamp(4, sale.orderDate);
                // System.out.println(saleStmt + ";");
                saleStmt.executeUpdate();
            }

            // Insert each sellable and related items
            for (Sellable sellable : sale.sellables) {
                try (var sellableStmt = connection.prepareStatement(sellableInsertSQL)) {
                    sellableStmt.setInt(1, next_sold_sellable_id);
                    sellableStmt.setInt(2, next_sale_id);
                    sellableStmt.setInt(3, sellable.sellableId);
                    // System.out.println(sellableStmt + ";");
                    sellableStmt.executeUpdate();
                }

                for (Item item : sellable.items) {
                    try (var itemStmt = connection.prepareStatement(itemInsertSQL)) {
                        itemStmt.setInt(1, next_sold_item_id);
                        itemStmt.setInt(2, item.itemId);
                        itemStmt.setInt(3, next_sold_sellable_id);
                        itemStmt.setDouble(4, item.amount);
                        itemStmt.executeUpdate();
                        // System.out.println(itemStmt + ";");
                        next_sold_item_id++;
                    }
                }
                next_sold_sellable_id++;
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error inserting sale:\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            try {
                if (connection != null) {
                    connection.rollback(); // Rollback on failure
                    System.out.println("Transaction rolled back due to error.");
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            System.out.println(e.getMessage());
        }
        // } finally {
        //     try {
        //         if (connection != null) {
        //             connection.setAutoCommit(true); // Reset auto-commit mode
        //             connection.commit(); // Commit transaction
        //             connection.close(); // Close connection
        //         }
        //     } catch (SQLException e) {
        //         System.out.println(e.getMessage());
        //     }
        // }
    }

    /**
     * Returns next avaliable sale id
     * 
     * @return next sale id
     */
    public static int getNextSaleID() {

        try {
            // create a statement object
            Statement stmt = connection.createStatement();
            // create an SQL statement
            String sqlStatement = "SELECT id FROM sale ORDER BY id DESC LIMIT 1";
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
            JOptionPane.showMessageDialog(null, "Error getting next sale ID:\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        // if error return -2
        return -2;
    }

    /**
     * Returns next avaliable sold_sellable id
     * 
     * @return next sold_sellable id
     */
    public static int getNextSoldSellableID() {

        try {
            // create a statement object
            Statement stmt = connection.createStatement();
            // create an SQL statement
            String sqlStatement = "SELECT id FROM sold_sellable ORDER BY id DESC LIMIT 1";
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
            JOptionPane.showMessageDialog(null, "Error getting next SoldSellable ID:\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        // if error return -2
        return -2;
    }

    /**
     * Returns next avaliable sold_item id
     * 
     * @return next sold_item id
     */
    public static int getNextSoldItemID() {

        try {
            // create a statement object
            Statement stmt = connection.createStatement();
            // create an SQL statement
            String sqlStatement = "SELECT id FROM sold_item ORDER BY id DESC LIMIT 1";
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
            JOptionPane.showMessageDialog(null, "Error getting next SoldItem ID:\n\n" + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        // if error return -2
        return -2;
    }

    public static void main(String[] args) {

        // create items
        // Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
        // Item item1 = new Item(1, 0, 1);
        // Item item2 = new Item(13, 0, 1);
        // ArrayList<Item> firstItem = new ArrayList<Item>(2);
        // firstItem.add(item1);
        // firstItem.add(item2);

        // Item item3 = new Item(4, 0, 1);
        // Item item4 = new Item(5, 0, 1);
        // Item item5 = new Item(14, 0, 1);
        // ArrayList<Item> secondItem = new ArrayList<Item>(3);
        // secondItem.add(item3);
        // secondItem.add(item4);
        // secondItem.add(item5);

        // Sellable sell1 = new Sellable(1, 8.30, firstItem);
        // Sellable sell2 = new Sellable(2, 9.80, secondItem);

        // ArrayList<Sellable> sold_sell = new ArrayList<Sellable>(2);
        // sold_sell.add(sell1);
        // sold_sell.add(sell2);
        // Sale x = new Sale(6, 19.59, currentTimestamp, sold_sell);
        // insertSale(x);

    }
}
