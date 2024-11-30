package views;

import data_classes.Employee;
import data_classes.Item_Edit;
import data_classes.SellableEdit;
import views.manager_panels.TimeChart;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * The ManagerGui class creates the GUI for managing staff, inventory, and sales,
 * and it interacts with a SQL database to display recent sales data.
 */
public class ManagerGUI extends JPanel {



    public static boolean runZReport = false;

    /**
     * Constructs a new managerGui instance.
     * <p>
     * This GUI consists of several panels:
     * - A staff panel at the top with buttons for different staff members.
     * - A main panel divided into an upper and lower panel.
     * <p>
     * The upper panel contains:
     * - A trends panel with a button to show store trends.
     * - A sales summary panel with buttons to display sales summaries for different time periods (Day, Week, Month, Year).
     * - A recent sales panel that displays the last 3 sales from the database.
     * <p>
     * The lower panel contains:
     * - An inventory items panel with buttons for editing item prices and inventory, and labels for inventory callouts, low stock items, and over-ordered items.
     * - A flex space and notes panel with buttons for hiring and firing employees, and a section for notes.
     * <p>
     * Each button has an associated ActionListener to handle user interactions, such as displaying messages or updating labels.
     * <p>
     * The frame is set to exit the program when closed and is made visible upon construction.
     *
     * @param conn the database connection to be used for retrieving sales data
     */
    public ManagerGUI(Connection conn) {
        // main frame
        this.setLayout(new BorderLayout());

        // Staff panel at the top
        JPanel staffPanel = new JPanel(new GridLayout(1, 1));
        staffPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JButton viewEmployeesButton = new JButton("View Employees");

        viewEmployeesButton.addActionListener(e -> View_Employee());

        staffPanel.add(viewEmployeesButton);

        this.add(staffPanel, BorderLayout.NORTH);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(2, 1, 5, 5));

        // Upper panel for product usage, sales report, and recent sales
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new GridLayout(1, 3, 5, 5));

        // Product usage chart panel
        upperPanel.add(new TimeChart("Product Usage Chart", (startDate, endDate) -> this.getInventoryReport(new Date(startDate.getTime()), new Date(endDate.getTime()))));

        // Sales report panel
        upperPanel.add(new TimeChart("Sales Report", (startDate, endDate) -> this.getSalesReport(new Date(startDate.getTime()), new Date(endDate.getTime()))));

        // Recent sales panel
        JPanel recentSalesPanel = new JPanel();
        recentSalesPanel.setLayout(new BorderLayout());
        recentSalesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel recentSalesLabel = new JLabel("Recent Sales", SwingConstants.CENTER);

// Get the last three sales
        String lastThreeSales = manager_queries.SalesReportQueries.lastThreeSales();
        String[] salesSplit = lastThreeSales.split("\n");

// Prepare the HTML formatted text
        StringBuilder htmlSales = new StringBuilder("<html>");
        for (int i = 1; i < salesSplit.length; i++) {
            htmlSales.append(salesSplit[i]).append("<br>");
        }
        htmlSales.append("</html>");

// Set the HTML formatted text to the label
        recentSalesLabel.setText(htmlSales.toString());
        recentSalesLabel.setForeground(Color.BLACK);

// Add the label to the panel
        recentSalesPanel.add(recentSalesLabel, BorderLayout.CENTER); // Use CENTER to position it properly

// Add the recent sales panel to the upper panel
        upperPanel.add(recentSalesPanel);
        mainPanel.add(upperPanel);

// Lower panel for inventory and flex space
        JPanel lowerPanel = new JPanel();
        lowerPanel.setLayout(new GridLayout(1, 2, 5, 5));


        // Inventory items panel
        JPanel inventoryItemsPanel = new JPanel(new GridLayout(2, 1));
        inventoryItemsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JPanel ViewAddEditPanel = new JPanel(new GridLayout(6, 1));

        JButton Button1 = new JButton("View Menu Items");
        Button1.addActionListener(e -> View_MenuItem());
        ViewAddEditPanel.add(Button1);

        JButton Button2 = new JButton("Add/Edit Menu Items");
        Button2.addActionListener(e -> AddEdit_MenuItem());
        ViewAddEditPanel.add(Button2);

        JButton Button3 = new JButton("Remove Menu Items");
        Button3.addActionListener(e -> Remove_MenuItem());
        ViewAddEditPanel.add(Button3);

        JButton Button4 = new JButton("View Inventory Items");
        Button4.addActionListener(e -> View_InventoryItem());
        ViewAddEditPanel.add(Button4);

        JButton Button5 = new JButton("Add/Edit Inventory Items");
        Button5.addActionListener(e -> AddEdit_InventoryItem());
        ViewAddEditPanel.add(Button5);

        JButton Button6 = new JButton("Remove Inventory Items");
        Button6.addActionListener(e -> Remove_InventoryItem());
        ViewAddEditPanel.add(Button6);

        //add 4 new buttons: view sellables, add sellable, edit sellable price, remove sellable
        JButton Button7 = new JButton("View Sellables");
        Button7.addActionListener(e -> View_Sellable());
        ViewAddEditPanel.add(Button7);
        JButton Button8 = new JButton("Add Sellable");
        Button8.addActionListener(e -> Add_Sellable());
        ViewAddEditPanel.add(Button8);
        JButton Button9 = new JButton("Edit Sellable Price");
        Button9.addActionListener(e -> Edit_Sellable_Price());
        ViewAddEditPanel.add(Button9);
        JButton Button10 = new JButton("Remove Sellable");
        Button10.addActionListener(e -> Remove_Sellable());
        ViewAddEditPanel.add(Button10);
        JButton Button11 = new JButton("Reactivate Sellable");
        Button11.addActionListener(e -> Reactivate_Sellable());
        ViewAddEditPanel.add(Button11);

        JPanel OtherInfoPanel = new JPanel(new GridLayout(1, 3));

        JLabel row1 = new JLabel("Inventory Callouts");
        JLabel row2 = new JLabel("Low Stock Items");
        JLabel row3 = new JLabel("Over Ordered Items");

        OtherInfoPanel.add(row1);
        OtherInfoPanel.add(row2);
        OtherInfoPanel.add(row3);

        inventoryItemsPanel.add(ViewAddEditPanel);
        inventoryItemsPanel.add(OtherInfoPanel);

        lowerPanel.add(inventoryItemsPanel);

        // notes and other reports panel
        JPanel notesAndOtherReportsPanel = new JPanel(new GridLayout(1, 2, 0, 0));
        notesAndOtherReportsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        // notes panel
        JPanel notesPanel = new JPanel();
        notesPanel.setLayout(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel flexSpaceLabel = new JLabel("Notes", SwingConstants.CENTER);

        // Add Employee button
        JButton addEmployeeButton = new JButton("Add/Edit Employee");
        addEmployeeButton.addActionListener(e -> AddEdit_Employee());

        // Remove Employee button
        JButton removeEmployeeButton = new JButton("Remove Employee");
        removeEmployeeButton.addActionListener(e -> Remove_Employee());

        notesPanel.add(addEmployeeButton, BorderLayout.NORTH);
        notesPanel.add(removeEmployeeButton, BorderLayout.SOUTH);
        notesPanel.add(flexSpaceLabel, BorderLayout.CENTER);

        notesAndOtherReportsPanel.add(notesPanel);

        // other report panel
        JPanel otherReportPanel = new JPanel(new GridLayout(3, 1));
        otherReportPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JButton xReportButton = new JButton("X-Report");
        xReportButton.addActionListener(e -> generateOtherReport("X"));
        JButton zReportButton = new JButton("Z-Report");
        zReportButton.addActionListener(e -> generateOtherReport("Z"));
        JButton restockReportButton = new JButton("Restock Report");
        restockReportButton.addActionListener(e -> generateOtherReport("Restock"));

        otherReportPanel.add(xReportButton);
        otherReportPanel.add(zReportButton);
        otherReportPanel.add(restockReportButton);
        notesAndOtherReportsPanel.add(otherReportPanel);

        lowerPanel.add(notesAndOtherReportsPanel);
        mainPanel.add(lowerPanel);

        // Add main panel to the frame
        this.add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * view employees.
     * 
     * @throws Exception If an error occurs while trying to view employees
     */
    private void View_Employee() {
        try {
            // get itemsData
            String[][] itemsData = manager_queries.EmployeeQueries.displayEmployees();

            // Column names for the JTable
            String[] columnNames = {"Employee ID", "Name", "Start Day", "Job Position", "Access Level", "Manager ID"};

            // Create a JTable with the data from displayItems and column names
            JTable table = new JTable(itemsData, columnNames);
            table.setFillsViewportHeight(true);

            // Add table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);

            // Create a JPanel and add the scroll pane to it
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);

            // Create a JFrame to display the panel
            JFrame frame = new JFrame("Employee Table");
            frame.setSize(600, 450); // Set frame size

            // Add the panel to the frame
            frame.add(panel);

            // Make the frame visible
            frame.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error viewing employees: " + ex.getMessage());
        }
    }

    /**
     * add/edit an employee.
     * 
     * @throws Exception If an error occurs while trying to add/edit an employee
     */
    private void AddEdit_Employee() {
        try {
            EmployeeItem employeeToHandle = showMultiInputDialog_Employee();

            if (employeeToHandle != null) {
                boolean employeeExists = false;
                String[][] lst = manager_queries.EmployeeQueries.displayEmployees();

                // check if employee exists
                for (int i = 0; i < lst.length; i++) {
                    if (employeeToHandle.name.equals(lst[i][0])) {
                        employeeExists = true;
                        break;
                    }
                }

                if (employeeExists) {
                    manager_queries.EmployeeQueries.editEmployee(new Employee(employeeToHandle.name, employeeToHandle.startDay, employeeToHandle.jobPosition, employeeToHandle.accessLevel, employeeToHandle.managerId)); // edit employee
                    JOptionPane.showMessageDialog(null, "Edited employee");
                } else {
                    manager_queries.EmployeeQueries.addEmployee(new Employee(employeeToHandle.name, employeeToHandle.startDay, employeeToHandle.jobPosition, employeeToHandle.accessLevel, employeeToHandle.managerId)); // add employee
                    JOptionPane.showMessageDialog(null, "Added employee");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Action Cancelled");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error adding/editing employee: " + ex.getMessage());
        }
    }

    /**
     * remove an employee.
     * 
     * @throws Exception If an error occurs while trying to remove an employee
     */
    private void Remove_Employee() {
        try {
            String employeeToRemove = JOptionPane.showInputDialog("Enter employee to remove:");

            boolean employeeExists = false;
            String[][] lst = manager_queries.EmployeeQueries.displayEmployees();

            // check if employee exists
            for (int i = 0; i < lst.length; i++) {
                if (employeeToRemove.equals(lst[i][0])) {
                    employeeExists = true;
                    break;
                }
            }

            if (employeeExists) {
                manager_queries.EmployeeQueries.removeEmployee(employeeToRemove);
                JOptionPane.showMessageDialog(null, "Employee removed");
            } else {
                JOptionPane.showMessageDialog(null, "Employee not found");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error removing employee: " + ex.getMessage());
        }
    }

    // Method to view sellables
    private void View_Sellable() {
        try {
            // get itemsData
            String[][] itemsData = manager_queries.SellableQueries.display_sellables();

            // Column names for the JTable
            String[] columnNames = {"Sellable ID", "Name", "Price", "Is Active", "Number of Entrees", "Number of Sides", "Number of Appetizers"};

            // Create a JTable with the data from displayItems and column names
            JTable table = new JTable(itemsData, columnNames);
            table.setFillsViewportHeight(true);

            // Add table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);

            // Create a JPanel and add the scroll pane to it
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);

            // Create a JFrame to display the panel
            JFrame frame = new JFrame("Sellables Table");
            frame.setSize(600, 450); // Set frame size

            // Add the panel to the frame
            frame.add(panel);

            // Make the frame visible
            frame.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error viewing sellables: " + ex.getMessage());
        }
    }

    // Method to add a sellable
    private void Add_Sellable() {
        try {
            SellableItem sellableToHandle = showMultiInputDialog_Sellable();
            //take the info from here and add it to the database using SellableQueries.add_sellable
            if (sellableToHandle != null) {
                boolean sellableExists = false;
                String[][] lst = manager_queries.SellableQueries.display_sellables();
                manager_queries.SellableQueries.add_sellable(new SellableEdit(sellableToHandle.price, sellableToHandle.name, sellableToHandle.sellableType, sellableToHandle.numEntrees, sellableToHandle.numSides, sellableToHandle.numAppetizers)); // add sellable
                JOptionPane.showMessageDialog(null, "Added sellable");
            } else {
                JOptionPane.showMessageDialog(this, "Action Cancelled");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error adding sellable: " + ex.getMessage());
        }
    }

    private void Edit_Sellable_Price() {
        try {
            String sellable = showSellableNameInputDialog();
            //split it at the | character, first half is the name, second half is the price
            String[] sellableSplit = sellable.split("\\|");
            String sellableToHandle = sellableSplit[0];
            double newPrice = Double.parseDouble(sellableSplit[1]);
            if (sellableToHandle != null) {
                manager_queries.SellableQueries.edit_sellable_price(sellableToHandle, newPrice); // edit sellable price
                } else {
                JOptionPane.showMessageDialog(this, "Action Cancelled");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error updating sellable price: " + ex.getMessage());
        }
    }

    // Method to remove a sellable
    private void Remove_Sellable() {
        try {
            String sellableToRemove = JOptionPane.showInputDialog("Enter sellable to remove:");
            //removeSellable uses name as the identifier
            if (sellableToRemove != null) {
                manager_queries.SellableQueries.remove_sellable(sellableToRemove); // remove sellable
                JOptionPane.showMessageDialog(null, "Sellable removed");
            } else {
                JOptionPane.showMessageDialog(this, "Action Cancelled");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error removing sellable: " + ex.getMessage());
        }
    }

    //add a reactivate sellable method
    private void Reactivate_Sellable() {
        try {
            String sellableToReactivate = JOptionPane.showInputDialog("Enter sellable to reactivate:");
            //reactivateSellable uses name as the identifier
            if (sellableToReactivate != null) {
                manager_queries.SellableQueries.reactivate_sellable(sellableToReactivate); // reactivate sellable
                JOptionPane.showMessageDialog(null, "Sellable reactivated");
            } else {
                JOptionPane.showMessageDialog(this, "Action Cancelled");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error reactivating sellable: " + ex.getMessage());
        }
    }

    private JPanel getSalesReport(Date startDate, Date endDate) {
        Map<String, Integer> salesReport = manager_queries.SalesReportQueries.getReport(startDate, endDate);

        JPanel panel = new JPanel();

        // Check if salesReport is null or empty
        if (salesReport == null || salesReport.isEmpty()) {
            panel.add(new JLabel("No sales data available for the selected period."));
            return panel;
        }

        // Convert Map to 2D Object array for JTable
        Object[][] salesData = new Object[salesReport.size()][2];
        int index = 0;
        for (Map.Entry<String, Integer> entry : salesReport.entrySet()) {
            salesData[index][0] = entry.getKey();    // Item name
            salesData[index][1] = entry.getValue();  // Quantity sold
            index++;
        }

        // Column names for the JTable
        String[] columnNames = {"Item Name", "Quantity Sold"};

        // Create a JTable with the data and column names
        JTable table = new JTable(salesData, columnNames);
        table.setFillsViewportHeight(true);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane);
        return panel;
    }

    private JPanel getInventoryReport(Date startDate, Date endDate) {
        ArrayList<String[]> inventoryReport = manager_queries.Inventory_History_queries.getInventoryHistoryBetween(startDate, endDate);
        JPanel panel = new JPanel();

        // Check if inventoryReport is null or empty
        if (inventoryReport == null || inventoryReport.isEmpty()) {
            panel.add(new JLabel("No inventory data available for the selected period."));
            return panel;
        }

        // Convert ArrayList to 2D Object array for JTable
        Object[][] inventoryData = new Object[inventoryReport.size()][2];
        int index = 0;
        for (String[] entry : inventoryReport) {
            inventoryData[index][0] = entry[0];    // Item name
            inventoryData[index][1] = entry[1];  // Quantity sold
            index++;
        }

        // Column names for the JTable
        String[] columnNames = {"Inventory Item Name", "Quantity Used"};

        // Create a JTable with the data and column names
        JTable table = new JTable(inventoryData, columnNames);
        table.setFillsViewportHeight(true);

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane);
        return panel;
    }

    /**
     * generate and shows other reports on the GUI.
     *
     * @param type - type of report
     */
    private void generateOtherReport(String type) {
        if (type.equals("X")) {
            Map<String, Map<String, Object>> xReport = manager_queries.XReportQueries.getXReport();

            // Convert xReport data to a format suitable for JTable
            String[] columnNames = {"Hour", "Total Sales"};
            Object[][] itemsData = new Object[xReport.size()][2];

            int row = 0;
            for (Map.Entry<String, Map<String, Object>> entry : xReport.entrySet()) {
                String itemName = entry.getKey();
                Object additionalPrice = entry.getValue().get("Total Sales");

                itemsData[row][0] = itemName;
                itemsData[row][1] = additionalPrice;
                row++;
            }

            // Create a JTable with the data and column names
            JTable table = new JTable(itemsData, columnNames);
            table.setFillsViewportHeight(true);

            // Add table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);

            // Create a JPanel and add the scroll pane to it
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);

            // Create a JFrame to display the panel
            JFrame frame = new JFrame("X Report");
            frame.setSize(600, 450); // Set frame size

            // Add the panel to the frame
            frame.add(panel);

            // Make the frame visible
            frame.setVisible(true);
        }
        else if (type.equals("Z")) {
            Map<String, Object> zReport = manager_queries.ZReportQueries.getZReport();

            if (zReport == null) {
                JOptionPane.showMessageDialog(this, "No Z report data available.", "Z Report", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
    
            // Convert zReport data to a format suitable for JTable
            String[] columnNames = {"Information", "Value"};
            Object[][] zData = new Object[2][2]; // Two rows

            // Populate the data
            zData[0][0] = "Total Sales"; // Metric name
            zData[0][1] = zReport.get("Total Sales"); // Metric value
            if (runZReport) {
                zData[0][1] = 0;
            }

            zData[1][0] = "Total Sales Value"; // Metric name
            zData[1][1] = zReport.get("Total Sales Value"); // Metric value
            if (runZReport) {
                zData[1][1] = 0;
            }

            // Create a JTable with the data and column names
            JTable table = new JTable(zData, columnNames);
            table.setFillsViewportHeight(true);

            // Add table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);

            // Create a JPanel and add the scroll pane to it
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);

            // Create a JFrame to display the panel
            JFrame frame = new JFrame("Z Report");
            frame.setSize(600, 250); // Set frame size

            // Add the panel to the frame
            frame.add(panel);

            // Make the frame visible
            frame.setVisible(true);
            runZReport = true;
        }
        else if (type.equals("Restock")) {
            Map<String, Integer> restockReport = manager_queries.RestockReportQueries.getRestockReport();

            if (restockReport == null) {
                JOptionPane.showMessageDialog(this, "No restock data available.", "Restock Report", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
    
            // Convert Map to 2D Object array for JTable
        Object[][] restockData = new Object[restockReport.size()][2];
        int index = 0;
        for (Map.Entry<String, Integer> entry : restockReport.entrySet()) {
            restockData[index][0] = entry.getKey();    // Item name
            restockData[index][1] = entry.getValue();  // Quantity to restock
            index++;
        }

        // Column names for the JTable
        String[] columnNames = {"Item Name", "Quantity to Restock"};

        // Create a JTable with the data and column names
        JTable table = new JTable(restockData, columnNames);
        table.setFillsViewportHeight(true);

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        // Create a JPanel and add the scroll pane to it
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create a JFrame to display the panel
        JFrame frame = new JFrame("Restock Report");
        frame.setSize(600, 450); // Set frame size

        // Add the panel to the frame
        frame.add(panel);

        // Make the frame visible
        frame.setVisible(true);
        }
    }

    /**
     * view menu item on the GUI.
     *
     * @throws Exception If an error occurs while trying to view menu
     */
    private void View_MenuItem() {
        try {
            // get itemsData
            String[][] itemsData = manager_queries.ItemQueries.displayItems();

            // Column names for the JTable
            String[] columnNames = {"Item ID", "Item Name", "Additional Price"};

            // Create a JTable with the data from displayItems and column names
            JTable table = new JTable(itemsData, columnNames);
            table.setFillsViewportHeight(true);

            // Add table to a scroll pane
            JScrollPane scrollPane = new JScrollPane(table);

            // Create a JPanel and add the scroll pane to it
            JPanel panel = new JPanel();
            panel.setLayout(new BorderLayout());
            panel.add(scrollPane, BorderLayout.CENTER);

            // Create a JFrame to display the panel
            JFrame frame = new JFrame("Items Table");
            frame.setSize(600, 450); // Set frame size

            // Add the panel to the frame
            frame.add(panel);

            // Make the frame visible
            frame.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error viewing inventory item: " + ex.getMessage());
        }
    }

    /**
     * add/edit menu item on the GUI.
     *
     * @throws Exception If an error occurs while trying to add/edit menu
     */
    private void AddEdit_MenuItem() {
        try {
            MenuItem item = showTwoInputDialog_MenuItem();

            if (item != null) {
                boolean menuExists = false;
                String[][] lst = manager_queries.ItemQueries.displayItems();

                // check if menu item exists
                for (int i = 0; i < lst.length; i++) {
                    if (item.name.equals(lst[i][0])) {
                        menuExists = true;
                        break;
                    }
                }
                // edit/add depending on menuExists
                if (menuExists) {
                    manager_queries.ItemQueries.updateItemAddPrice(new Item_Edit(item.name, item.item_type, item.price)); // edit menu item
                    JOptionPane.showMessageDialog(null, "Edited menu item");
                } else {
                    manager_queries.ItemQueries.addItem(new Item_Edit(item.name, item.item_type, item.price)); // add menu item
                    JOptionPane.showMessageDialog(null, "Added menu item");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Action Cancelled");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error adding/editing menu item: " + ex.getMessage());
        }
    }

    /**
     * remove menu item on the GUI.
     *
     * @throws Exception If an error occurs while trying to remove menu item
     */
    private void Remove_MenuItem() {
        try {
            String menuToRemove = JOptionPane.showInputDialog("Enter menu item to remove:");

            boolean menuExists = false;
            String[][] lst = manager_queries.ItemQueries.displayItems();

            // check if menu item exists
            for (int i = 0; i < lst.length; i++) {
                if (menuToRemove.equals(lst[i][0])) {
                    menuExists = true;
                    break;
                }
            }

            if (menuExists) {
                manager_queries.ItemQueries.removeItem(menuToRemove);
                JOptionPane.showMessageDialog(null, "Menu removed");
            } else {
                JOptionPane.showMessageDialog(null, "Menu not found");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error removing menu item: " + ex.getMessage());
        }
    }

    /**
     * view inventory item on the GUI.
     *
     * @throws Exception If an error occurs while trying to view inventory item
     */
    private void View_InventoryItem() {
        try {

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error viewing inventory item: " + ex.getMessage());
        }

        // get itemsData
        String[][] itemsData = manager_queries.Inventory_queries.viewInventory();

        // Column names for the JTable
        String[] columnNames = {"Item ID", "Item Name", "Amount", "Restock Amount"};

        // Create a JTable with the data from displayItems and column names
        JTable table = new JTable(itemsData, columnNames);
        table.setFillsViewportHeight(true);

        // Add table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);

        // Create a JPanel and add the scroll pane to it
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Create a JFrame to display the panel
        JFrame frame = new JFrame("Items Table");
        frame.setSize(600, 450); // Set frame size

        // Add the panel to the frame
        frame.add(panel);

        // Make the frame visible
        frame.setVisible(true);
    }

    /**
     * add/edit inventory item on the GUI.
     *
     * @throws Exception If an error occurs while trying to add/edit inventory item
     */
    private void AddEdit_InventoryItem() {
        try {
            InventoryItem item = showTwoInputDialog_InventoryItem();

            if (item != null) {
                boolean itemExists = false;
                String[][] lst = manager_queries.Inventory_queries.viewInventory();

                // check if inventory item exists
                for (int i = 0; i < lst.length; i++) {
                    if (item.name.equals(lst[i][1])) {
                        itemExists = true;
                        break;
                    }
                }
                // edit/add depending on itemExists
                if (itemExists) {
                    manager_queries.Inventory_queries.editInventoryAmount(item.name, item.quantity); // edit inventory item
                    JOptionPane.showMessageDialog(null, "Edited inventory item");
                } else {
                    manager_queries.Inventory_queries.addInventoryItem(item.name, item.quantity, item.quantity + 5); // add inventory item
                    JOptionPane.showMessageDialog(null, "Added inventory item");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Action Cancelled");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error adding/editing inventory item: " + ex.getMessage());
        }
    }

    /**
     * remove inventory item on the GUI.
     *
     * @throws Exception If an error occurs while trying to remove inventory item
     */
    private void Remove_InventoryItem() {
        try {
            String itemToRemove = JOptionPane.showInputDialog("Enter inventory item to remove:");

            boolean itemExists = false;
            String[][] lst = manager_queries.Inventory_queries.viewInventory();

            // check if inventory item exists
            for (int i = 0; i < lst.length; i++) {
                if (itemToRemove.equals(lst[i][1])) {
                    itemExists = true;
                    break;
                }
            }
            // remove/stop depending on itemExists
            if (itemExists) {
                manager_queries.Inventory_queries.editInventoryAmount(itemToRemove, 0);
                JOptionPane.showMessageDialog(null, "Inventory item removed");
            } else {
                JOptionPane.showMessageDialog(null, "Inventory item not found");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error removing inventory item: " + ex.getMessage());
        }
    }

    /**
     * show two input pop up on the GUI.
     * 
     * @throws Exception If an error occurs with the inputs for menu item
     * 
     * @return MenuItem of the input
     * @throws Exception If an error occurs with the inputs
     */
    private MenuItem showTwoInputDialog_MenuItem() {
        // Create JPanel to hold input fields
        JPanel panel = new JPanel(new GridLayout(2, 2));

        // Create labels and text fields
        JLabel label1 = new JLabel("Menu Item Name to Add/Edit: ");
        JTextField textField1 = new JTextField();
        JLabel label2 = new JLabel("Item Type: ");
        JTextField textField2 = new JTextField();
        JLabel label3 = new JLabel("Price: ");
        JTextField textField3 = new JTextField();

        panel.add(label1);
        panel.add(textField1);
        panel.add(label2);
        panel.add(textField2);
        panel.add(label3);
        panel.add(textField3);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Two Inputs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && (!textField1.getText().isEmpty() && !textField2.getText().isEmpty() && !textField3.getText().isEmpty())) {
            try {
                return new MenuItem(textField1.getText(), Integer.valueOf(textField2.getText()), Double.valueOf(textField3.getText()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error, please input correct value types: " + ex.getMessage());
            }
        }
        return null;
    }
    /**
     * show two input pop up on the GUI.
     * 
     * @throws Exception If an error occurs with the inputs for inventory items
     * 
     * @return InventoryItem of the input
     */
    private InventoryItem showTwoInputDialog_InventoryItem() {
        // Create JPanel to hold input fields
        JPanel panel = new JPanel(new GridLayout(2, 2));

        // Create labels and text fields
        JLabel label1 = new JLabel("Inventory Item Name to Add/Edit: ");
        JTextField textField1 = new JTextField();
        JLabel label2 = new JLabel("Quantity: ");
        JTextField textField2 = new JTextField();

        panel.add(label1);
        panel.add(textField1);
        panel.add(label2);
        panel.add(textField2);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Two Inputs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && (!textField1.getText().isEmpty() && !textField2.getText().isEmpty())) {
            try {
                return new InventoryItem(textField1.getText(), Integer.valueOf(textField2.getText()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error, please input correct value types: " + ex.getMessage());
            }
        }
        return null;
    }
    /**
     * show two input pop up on the GUI.
     * 
     * @throws Exception If an error occurs with the inputs for employees
     * 
     * @return EmployeeItem of the input
     */
    private EmployeeItem showMultiInputDialog_Employee() {
        // Create JPanel to hold input fields
        JPanel panel = new JPanel(new GridLayout(5, 5));

        // Create labels and text fields
        JLabel label1 = new JLabel("Employee Name to Add/Edit: ");
        JTextField textField1 = new JTextField();
        JLabel label2 = new JLabel("Start Day (YYYY-MM-DD): ");
        JTextField textField2 = new JTextField();
        JLabel label3 = new JLabel("Job Position: ");
        JTextField textField3 = new JTextField();
        JLabel label4 = new JLabel("Access Level: ");
        JTextField textField4 = new JTextField();
        JLabel label5 = new JLabel("Manager ID: ");
        JTextField textField5 = new JTextField();

        panel.add(label1);
        panel.add(textField1);
        panel.add(label2);
        panel.add(textField2);
        panel.add(label3);
        panel.add(textField3);
        panel.add(label4);
        panel.add(textField4);
        panel.add(label5);
        panel.add(textField5);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Five Inputs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && (!textField1.getText().isEmpty() && !textField2.getText().isEmpty() && !textField3.getText().isEmpty() && !textField4.getText().isEmpty() && !textField5.getText().isEmpty())) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("YYYY-MM-DD");
            
            try {
                return new EmployeeItem(textField1.getText(), new Date(dateFormatter.parse(textField2.getText()).getTime()), textField3.getText(), Integer.valueOf(textField4.getText()), Integer.valueOf(textField5.getText()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error, please input correct value types: " + ex.getMessage());
            }
        }
        return null;
    }

    //showmultiinputdialog for sellable
    //takes in the sellable name, price, sellable type, number of entrees, number of sides, number of appetizers
    private SellableItem showMultiInputDialog_Sellable() {
        // Create JPanel to hold input fields
        JPanel panel = new JPanel(new GridLayout(6, 2));

        // Create labels and text fields
        JLabel label1 = new JLabel("Sellable Name to Add/Edit: ");
        JTextField textField1 = new JTextField();
        JLabel label2 = new JLabel("Price: ");
        JTextField textField2 = new JTextField();
        JLabel label3 = new JLabel("Sellable Type: ");
        JTextField textField3 = new JTextField();
        JLabel label4 = new JLabel("Number of Entrees: ");
        JTextField textField4 = new JTextField();
        JLabel label5 = new JLabel("Number of Sides: ");
        JTextField textField5 = new JTextField();
        JLabel label6 = new JLabel("Number of Appetizers: ");
        JTextField textField6 = new JTextField();

        panel.add(label1);
        panel.add(textField1);
        panel.add(label2);
        panel.add(textField2);
        panel.add(label3);
        panel.add(textField3);
        panel.add(label4);
        panel.add(textField4);
        panel.add(label5);
        panel.add(textField5);
        panel.add(label6);
        panel.add(textField6);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Six Inputs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && (!textField1.getText().isEmpty() && !textField2.getText().isEmpty() && !textField3.getText().isEmpty() && !textField4.getText().isEmpty() && !textField5.getText().isEmpty() && !textField6.getText().isEmpty())) {
            try {
                return new SellableItem(textField1.getText(), Double.valueOf(textField2.getText()), true, Integer.valueOf(textField3.getText()), Integer.valueOf(textField4.getText()), Integer.valueOf(textField5.getText()), Integer.valueOf(textField6.getText()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error, please input correct value types: " + ex.getMessage());
            }
        }
        return null;
    }

    //showSellableNameInputDialog returns a string containing the name of the sellable and the new price, divided by a |
    private String showSellableNameInputDialog() {
        // Create JPanel to hold input fields
        JPanel panel = new JPanel(new GridLayout(2, 2));

        // Create labels and text fields
        JLabel label1 = new JLabel("Sellable Name to Edit: ");
        JTextField textField1 = new JTextField();
        JLabel label2 = new JLabel("New Price: ");
        JTextField textField2 = new JTextField();

        panel.add(label1);
        panel.add(textField1);
        panel.add(label2);
        panel.add(textField2);

        // Show dialog
        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Two Inputs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION && (!textField1.getText().isEmpty() && !textField2.getText().isEmpty())) {
            try {
                return textField1.getText() + "|" + textField2.getText();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error, please input correct value types: " + ex.getMessage());
            }
        }
        return null;
    }

    // Classes
    public class MenuItem {
        public String name;
        public int item_type; // 0 for entree, 1 for drink, 2 for appetizer
        public double price;

        public MenuItem(String name, int item_type, double price) {
            this.name = name;
            this.item_type = item_type;
            this.price = price;
        }
    }

    public class InventoryItem {
        public String name;
        public int quantity;

        public InventoryItem(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }
    }

    public class EmployeeItem {
        public static final int cashier_access_level = 0;
        public static final int manager_access_level = 1;

        public int id = -1;
        public String name;
        public Date startDay;
        public String jobPosition;
        public int accessLevel; // 0 for cashier, 1 for manager, -1 for inactive employee
        public int managerId;

        public EmployeeItem(String name, Date startDay, String jobPosition, int accessLevel, int managerId) {
            this.name = name;
            this.startDay = startDay;
            this.jobPosition = jobPosition;
            this.accessLevel = accessLevel;
            this.managerId = managerId;
        }

        public EmployeeItem(int id, String name, Date startDay, String jobPosition, int accessLevel, int managerId) {
            this.id = id;
            this.name = name;
            this.startDay = startDay;
            this.jobPosition = jobPosition;
            this.accessLevel = accessLevel;
            this.managerId = managerId;
        }
    }

    public class SellableItem {
        public String name;
        public double price;
        public boolean isActive;
        public int sellableType;
        public int numEntrees;
        public int numSides;
        public int numAppetizers;

        public SellableItem(String name, double price, boolean isActive, int sellableType, int numEntrees, int numSides, int numAppetizers) {
            this.name = name;
            this.price = price;
            this.isActive = isActive;
            this.sellableType = sellableType;
            this.numEntrees = numEntrees;
            this.numSides = numSides;
            this.numAppetizers = numAppetizers;
        }
    }
}