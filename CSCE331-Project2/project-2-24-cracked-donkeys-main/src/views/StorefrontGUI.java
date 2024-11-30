package views;

import data_classes.Item;
import data_classes.Sale;
import data_classes.Sellable;
import employee_queries.SaleQueries;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;
import java.util.*;

public class StorefrontGUI extends JPanel {
    private final List<OrderItem> allOrders = new ArrayList<>();
    private final Vector<String> currentOrder = new Vector<>();
    private final Map<String, Integer> itemCounts = new HashMap<>();
    private final List<JButton> itemButtons = new ArrayList<>();
    private final JLabel entreeLabel = new JLabel("Entrees remaining: 0");
    private final JLabel sideLabel = new JLabel("Sides remaining: 0");
    private final JLabel appetizerLabel = new JLabel("Appetizers remaining: 0");
    Map<Integer, Double> itemPrices = new HashMap<>();
    Map<Integer, Double> sellablePrices = new HashMap<>();
    Map<String, Integer> itemIdMap = new HashMap<>();
    private Sale currentSale;
    private int numEntrees, numSides, numAppetizers;
    private String currentType = "";
    private JButton selectedButton, selectedOrderTypeButton;
    private final Connection conn;

    public StorefrontGUI(Connection conn) {
        this.conn = conn;
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        itemPrices = fetchItemPrices(conn);
        sellablePrices = fetchSellablePrices(conn);

        // Set up labels panel
        JPanel labelsPanel = new JPanel(new FlowLayout());
        labelsPanel.add(entreeLabel);
        labelsPanel.add(sideLabel);
        labelsPanel.add(appetizerLabel);

        // Add labels panel to main panel
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(labelsPanel, gbc);

        // Button panels
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        mainPanel.add(createButtonPanel(), gbc);

        gbc.gridy = 2;
        mainPanel.add(createOrderTypePanel(), gbc);

        gbc.gridy = 3;
        mainPanel.add(createDrinkPanel(), gbc);

        gbc.gridy = 4;
        mainPanel.add(createRareOrderTypePanel(), gbc);

        gbc.gridy = 5;
        mainPanel.add(createSubmissionPanel(), gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private Map<Integer, Double> fetchItemPrices(Connection conn) {
        Map<Integer, Double> prices = new HashMap<>();
        try (Statement stmt = conn.createStatement(); ResultSet result = stmt.executeQuery("SELECT * FROM item")) {
            while (result.next()) {
                prices.put(result.getInt("id"), result.getDouble("add_price"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        return prices;
    }

    private Map<Integer, Double> fetchSellablePrices(Connection conn) {
        Map<Integer, Double> prices = new HashMap<>();
        try (Statement stmt = conn.createStatement(); ResultSet result = stmt.executeQuery("SELECT * FROM sellable")) {
            while (result.next()) {
                prices.put(result.getInt("id"), result.getDouble("price"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }
        return prices;
    }


    private List<String> fetchItemsByCategory(Connection conn, int minId, int maxId) {
        List<String> items = new ArrayList<>();
        String query = "SELECT name FROM item WHERE id BETWEEN ? AND ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, minId);
            pstmt.setInt(2, maxId);

            try (ResultSet result = pstmt.executeQuery()) {
                while (result.next()) {
                    items.add(result.getString("name"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }

        return items;
    }

    private List<String> fetchSellablesByCategory(Connection conn, int minId, int maxId) {
        List<String> sellables = new ArrayList<>();
        String query = "SELECT name FROM sellable WHERE id BETWEEN ? AND ?";

        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, minId);
            pstmt.setInt(2, maxId);

            try (ResultSet result = pstmt.executeQuery()) {
                while (result.next()) {
                    sellables.add(result.getString("name"));
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.toString());
        }

        return sellables;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 5, 10, 10)); // 3 columns with spacing of 10
        List<String> entrees = fetchItemsByCategory(conn, 1, 12);
        entrees.addAll(fetchItemsByCategory(conn, 100, 199));
        List<String> sides = fetchItemsByCategory(conn, 13, 16);
        sides.addAll(fetchItemsByCategory(conn, 200, 299));
        List<String> appetizers = fetchItemsByCategory(conn, 17, 20);
        appetizers.addAll(fetchItemsByCategory(conn, 300, 399));

        // Add entree buttons to the panel
        for (String item : entrees) {
            itemCounts.put(item, 0); // Initialize count to 0
            JButton button = createStyledButton(item, true);
            panel.add(button);
        }

        // Add side buttons to the panel
        for (String item : sides) {
            itemCounts.put(item, 0); // Initialize count to 0
            JButton button = createStyledButton(item, false);
            panel.add(button);
        }

        return panel;
    }


    private JButton createStyledButton(String name, boolean isEntree) {
        JButton button = new JButton(name + " (0)");
        styleButton(button);
        button.addActionListener(e -> handleButtonAction(button, name, isEntree));
        itemButtons.add(button);
        return button;
    }

    private void handleButtonAction(JButton button, String name, boolean isEntree) {
        if (selectedButton != button) {
            resetSelectedButton();
            button.setBackground(Color.YELLOW);
            selectedButton = button;
        }

        if (isEntree && numEntrees > 0) {
            numEntrees--;
            currentOrder.add(name);
            itemCounts.put(name, itemCounts.get(name) + 1); // Increment count
            updateLabel(entreeLabel, numEntrees);
        } else if (!isEntree && numSides > 0) {
            numSides--;
            currentOrder.add(name);
            itemCounts.put(name, itemCounts.get(name) + 1); // Increment count
            updateLabel(sideLabel, numSides);
        } else if (!isEntree && numAppetizers > 0) {
            numAppetizers--;
            currentOrder.add(name);
            itemCounts.put(name, itemCounts.get(name) + 1); // Increment count
            updateLabel(appetizerLabel, numAppetizers);
        }

        // Update the button text to show the new count
        button.setText(name + " (" + itemCounts.get(name) + ")");

    }

    private void resetSelectedButton() {
        if (selectedButton != null) {
            selectedButton.setBackground(Color.LIGHT_GRAY);
        }
    }

    private void updateLabel(JLabel label, int count) {
        label.setText(label.getText().split(":")[0] + ": " + count);
    }

    private JPanel createOrderTypePanel() {
        JPanel panel = new JPanel(new FlowLayout());
        //order types
        List<String> orderTypes = fetchSellablesByCategory(conn, 1, 3);
        //append id 12
        orderTypes.addAll(fetchSellablesByCategory(conn, 12, 12));
        //append id 1000 to 1999
        orderTypes.addAll(fetchSellablesByCategory(conn, 10000, 19999));

        for (String type : orderTypes) {
            JButton button = new JButton(type);
            styleButton(button);
            button.addActionListener(e -> handleOrderTypeSelection(button, type));
            panel.add(button);
        }
        return panel;
    }

    //add a new panel for s/m/l entree and m/l side
    private JPanel createRareOrderTypePanel() {
        JPanel panel = new JPanel(new FlowLayout());
        //order types
        List<String> orderTypes = fetchSellablesByCategory(conn, 7, 11);
        //append id 3000 to 3999
        orderTypes.addAll(fetchSellablesByCategory(conn, 30000, 39999));

        for (String type : orderTypes) {
            JButton button = new JButton(type);
            styleButton(button);
            button.addActionListener(e -> handleRareOrderTypeSelection(button, type));
            panel.add(button);
        }
        return panel;
    }

    private void handleOrderTypeSelection(JButton button, String type) {
        resetSelectedOrderTypeButton();
        button.setBackground(Color.YELLOW);
        selectedOrderTypeButton = button;
        setOrderBasedOnType(type);
    }

    private void resetSelectedOrderTypeButton() {
        if (selectedOrderTypeButton != null) {
            selectedOrderTypeButton.setBackground(Color.LIGHT_GRAY);
        }
    }

    private void setOrderBasedOnType(String type) {
        switch (type) {
            case "Bowl":
                setOrder(1, 2, 0, "Bowl");
                break;
            case "Plate":
                setOrder(2, 2, 0, "Plate");
                break;
            case "Bigger Plate":
                setOrder(3, 2, 0, "Bigger Plate");
                break;
            case "Appetizer":
                setOrder(0, 0, 1, "Appetizer");
                break;
        }
    }

    private void setRareOrderBasedOnType(String type) {
        switch (type) {
            case "Small Entree":
                setOrder(1, 0, 0, "Small Entree");
                break;
            case "Medium Entree":
                setOrder(2, 0, 0, "Medium Entree");
                break;
            case "Large Entree":
                setOrder(3, 0, 0, "Large Entree");
                break;
            case "Medium Side":
                setOrder(0, 1, 0, "Medium Side");
                break;
            case "Large Side":
                setOrder(0, 2, 0, "Large Side");
                break;
        }
    }

    private void setDrinkBasedOnType(String type) {
        switch (type) {
            case "Small Drink":
                setOrder(0, 0, 0, "Small Drink");
                break;
            case "Medium Drink":
                setOrder(0, 0, 0, "Medium Drink");
                break;
            case "Large Drink":
                setOrder(0, 0, 0, "Large Drink");
                break;
        }
    }

    private void handleDrinkOrderTypeSelection(JButton button, String type) {
        resetSelectedOrderTypeButton();
        button.setBackground(Color.YELLOW);
        selectedOrderTypeButton = button;
        setDrinkBasedOnType(type);
    }

    private void handleRareOrderTypeSelection(JButton button, String type) {
        resetSelectedOrderTypeButton();
        button.setBackground(Color.YELLOW);
        selectedOrderTypeButton = button;
        setRareOrderBasedOnType(type);
    }

    private JPanel createDrinkPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        List<String> drinkSizes = fetchSellablesByCategory(conn, 4, 6);
        //add id 2000 to 2999
        drinkSizes.addAll(fetchSellablesByCategory(conn, 20000, 29999));

        for (String size : drinkSizes) {
            JButton button = new JButton(size);
            styleButton(button);
            button.addActionListener(e -> handleDrinkOrderTypeSelection(button, size));
            panel.add(button);
        }
        return panel;
    }

    private JPanel createSubmissionPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        addSubmissionButton(panel, "Show Current Item", this::showCurrentItem, Color.getHSBColor(0.7f, 0.8f, 0.8f));
        addSubmissionButton(panel, "Submit Item", this::submitItem, Color.getHSBColor(0.5f, 0.8f, 0.9f));
        addSubmissionButton(panel, "Show All Items", this::showAllItems,  Color.getHSBColor(0.7f, 0.8f, 0.8f));
        addSubmissionButton(panel, "Submit Order", this::submitOrder,  Color.getHSBColor(0.3f, 0.8f, 0.9f));
        return panel;
    }

    private void addSubmissionButton(JPanel panel, String text, Runnable action, Color color) {
        JButton button = new JButton(text);
        styleButton(button);
        button.setBackground(color); // Set the button color
        button.setPreferredSize(new Dimension(200, 75)); // Make buttons wider
        button.addActionListener(e -> action.run());
        panel.add(button);
    }


    private void setOrder(int entrees, int sides, int appetizers, String type) {
        numEntrees = entrees;
        numSides = sides;
        numAppetizers = appetizers;
        currentType = type;
        updateLabels();
        currentOrder.clear();
    }

    private void updateLabels() {
        entreeLabel.setText("Entrees remaining: " + numEntrees);
        sideLabel.setText("Sides remaining: " + numSides);
        appetizerLabel.setText("Appetizers remaining: " + numAppetizers);
    }

    private void showCurrentItem() {
        String orderString = String.join("\n", currentOrder);
        JOptionPane.showMessageDialog(null, orderString.isEmpty() ? "Current item is empty!" : orderString);
    }

    private void submitItem() {
        if (!isOrderComplete()) {
            JOptionPane.showMessageDialog(null, "Item not complete. Please complete the item before submitting.");
            return;
        }

        List<Item> items = createItemsFromOrder();
        int sellableId = determineSellableId();

        if (sellableId == 0) {
            JOptionPane.showMessageDialog(null, "Invalid order type.");
            return;
        }

        double price = calculatePrice(items, sellableId);
        Sellable newSellable = new Sellable(sellableId, price, items);
        if (currentSale == null) {
            currentSale = new Sale(1, 0.0, new Timestamp(System.currentTimeMillis()), new ArrayList<>());
        }
        currentSale.sellables.add(newSellable);
        currentSale.totalPrice += newSellable.price;
        allOrders.add(new OrderItem(currentType, new ArrayList<>(currentOrder)));
        resetOrderState();

        JOptionPane.showMessageDialog(null, "Item submitted successfully!\n" + String.join("\n", currentOrder));
    }

    private double calculatePrice(List<Item> items, int sellableId) {
        double price = 0.0;
        for (Item item : items) {
            Double itemPrice = itemPrices.getOrDefault(item.itemId, 0.0);
            price += itemPrice * item.amount;
        }
        price += sellablePrices.getOrDefault(sellableId, 0.0);
        return price;
    }

    private void submitOrder() {
        if (currentSale != null && !currentSale.sellables.isEmpty()) {
            SaleQueries.insertSale(currentSale);
            currentSale = null;
            JOptionPane.showMessageDialog(null, "Order submitted successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "No items in the order!");
        }
    }

    private boolean isOrderComplete() {
        return (numEntrees == 0 && numSides == 0 && (numAppetizers == 0 || currentType.equals("Appetizer")));
    }

    private List<Item> createItemsFromOrder() {
        List<Item> items = new ArrayList<>();
        for (String item : currentOrder) {
            Integer id = itemIdMap.get(item);
            if (id != null) {
                items.add(new Item(id, 0, 1));
            }
        }
        return items;
    }

    private int determineSellableId() {
        //just use fetchitemsbycategory to get the sellable id
        List<String> sellables = fetchSellablesByCategory(conn, 1, 99);
        for (String sellable : sellables) {
            if (sellable.equals(currentType)) {
                return sellables.indexOf(sellable) + 1;
            }
        }
        return 0;
    }

    private void resetOrderState() {
        currentOrder.clear();
        numEntrees = 0;
        numSides = 0;
        numAppetizers = 0;
        currentType = "";

        // Reset item counts
        for (String item : itemCounts.keySet()) {
            itemCounts.put(item, 0);
        }

        // Reset button labels
        for (JButton button : itemButtons) {
            String itemName = button.getText().split(" \\(")[0]; // Extract the item name
            button.setText(itemName + " (0)"); // Reset the text to show 0
        }

        updateLabels();
        resetSelectedButton();
    }

    private void showAllItems() {
        StringBuilder allItems = new StringBuilder();
        for (OrderItem item : allOrders) {
            allItems.append(item.getType()).append(":\n").append(String.join(", ", item.getContents())).append("\n\n");
        }
        JOptionPane.showMessageDialog(null, allItems.length() == 0 ? "No items submitted!" : allItems.toString());
    }

    private void styleButton(JButton button) {
        button.setBackground(Color.LIGHT_GRAY);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(150, 75));
        button.setFont(new Font("Arial", Font.BOLD, 15));
    }

    static class OrderItem {
        private final String type;
        private final List<String> contents;

        public OrderItem(String type, List<String> contents) {
            this.type = type;
            this.contents = contents;
        }

        public String getType() {
            return type;
        }

        public List<String> getContents() {
            return contents;
        }
    }
}
