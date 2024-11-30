package manager_queries;

import data_classes.SellableEdit;

import java.sql.*;

public class SellableQueries {
    //allow us to add new sellable types
    //these sellables are generated as follows:
    //first digit of the id is the sellable type
    //second digit is a unique identifier for the sellable with the same type and contents, so we'll look at all other sellables with the other 4 digits the same and set up the new sellable with the next highest second digit
    //third digit is the number of entrees
    //fourth digit is the number of sides
    //fifth digit is the number of appetizers
    //sellables only has id, name, price and is_active
    private static final String url = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce331_24";
    private static final String user = "csce331_24";
    private static final String password = "cracked.donkey";
    private static Connection connection;

    public static void connect() {
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void add_sellable(SellableEdit sellable) {
        try {
            connect();
            String query = "SELECT id FROM sellable WHERE id/10000 = ? AND id%1000 = ? ORDER BY id DESC";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, sellable.sellableType);
            stmt.setInt(2, (1000 * sellable.numEntrees + 10 * sellable.numSides + sellable.numAppetizers));
            ResultSet rs = stmt.executeQuery();

            int identifier = 0;
            if (rs.next()) {
                identifier = (rs.getInt("id") % 1000) / 100 + 1;
            }
            int new_id = 10000 * sellable.sellableType + 1000 * identifier + 100 * sellable.numEntrees + 10 * sellable.numSides + sellable.numAppetizers;

            query = "INSERT INTO sellable (id, name, price, is_active) VALUES (?, ?, ?, true)";
            PreparedStatement insertStmt = connection.prepareStatement(query);
            insertStmt.setInt(1, new_id);
            insertStmt.setString(2, sellable.name);
            insertStmt.setDouble(3, sellable.price);
            insertStmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void edit_sellable_price(String name, double price) {
        try {
            connect();
            String query = "UPDATE sellable SET price = " + price + " WHERE name = '" + name + "'";
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //remove sellable by setting is_active to false
    public static void remove_sellable(String name) {
        try {
            connect();
            String query = "UPDATE sellable SET is_active = false WHERE name = '" + name + "'";
            connection.createStatement().execute(query);
            //set the sellable ID to negative to indicate that it is inactive
            query = "UPDATE sellable SET id = -id WHERE name = '" + name + "'";
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[][] display_sellables() {
        //display the id, name, price, is_active, type, numEntrees, numSides, numAppetizers for each sellable
        try {
            connect();
            String query = "SELECT * FROM sellable";
            ResultSet rs = connection.createStatement().executeQuery(query);
            int num_rows = 0;
            while (rs.next()) {
                num_rows++;
            }
            String[][] result = new String[num_rows][8];
            rs = connection.createStatement().executeQuery(query);
            int i = 0;
            while (rs.next()) {
                result[i][0] = Integer.toString(rs.getInt("id"));
                result[i][1] = rs.getString("name");
                result[i][2] = Double.toString(rs.getDouble("price"));
                result[i][3] = Boolean.toString(rs.getBoolean("is_active"));
                result[i][4] = Integer.toString((rs.getInt("id") % 1000) / 100);
                result[i][5] = Integer.toString((rs.getInt("id") % 100) / 10);
                result[i][6] = Integer.toString(rs.getInt("id") % 10);
                if (rs.getInt("id") < 100 && rs.getInt("id") > 0) {
                    //manually set [i][4], [i][5], and [i][6] based on their type
                    int id = rs.getInt("id");
                    if (id < 4) {
                        //bowl, plate, bigger plate
                        result[i][4] = Integer.toString(id);
                        result[i][5] = "2";
                        result[i][6] = "0";
                    }
                    if (id >= 4 && id < 7) {
                        //drinks
                        result[i][4] = "0";
                        result[i][5] = "0";
                        result[i][6] = "0";
                    }
                    if (id >= 7 && id < 10) {
                        //entrees
                        result[i][4] = Integer.toString(id - 6);
                        result[i][5] = "0";
                        result[i][6] = "0";
                    }
                    if (id >= 10 && id < 12) {
                        //sides
                        result[i][4] = "0";
                        result[i][5] = Integer.toString(id - 9);
                        result[i][6] = "0";
                    }
                    if (id >= 12) {
                        //appetizers
                        result[i][4] = "0";
                        result[i][5] = "0";
                        result[i][6] = "1";
                    }
                }
                i++;
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //reactivate a sellable by setting is_active to true
    public static void reactivate_sellable(String name) {
        try {
            connect();
            String query = "UPDATE sellable SET is_active = true WHERE name = '" + name + "'";
            connection.createStatement().execute(query);
            //set the sellable ID to positive to indicate that it is active
            query = "UPDATE sellable SET id = -id WHERE name = '" + name + "'";
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
