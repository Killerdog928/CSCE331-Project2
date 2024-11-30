import components.Tabs;
import data_classes.Employee;
import manager_queries.EmployeeQueries;
import views.ManagerGUI;
import views.StorefrontGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class App extends JFrame {
    /* Database credentials */
    // TODO: Get these credentials out of the repo
    private static final String url = "jdbc:postgresql://csce-315-db.engr.tamu.edu/csce331_24";
    private static final String user = "csce331_24";
    private static final String password = "cracked.donkey";

    private static final boolean start_in_cashier_view = true;

    private static final int required_level_for_cashier = Employee.cashier_access_level;
    private static final int required_level_for_manager = Employee.manager_access_level;

    private final JLabel authInfo;

    private final JButton signOutButton;

    private Connection databaseConnection;
    
    private Employee currentUser;

    private final Tabs tabs;

    /**
     * Constructs a new instance of the App class.
     * Initializes the main components of the application, including the top bar and view panel.
     * Sets the size and layout of the main window and its components.
     * Centers the window on the screen and makes it visible.
     */
    public App() {
        super("Cracked Donkey POS - NOT A VIRUS");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);

        this.databaseConnection = null;

        this.tabs = new Tabs(new JPanel());

        this.tabs.addTab("Cashier View", () -> this.cashierView());
        this.tabs.addTab("Manager View", () -> this.managerView());

        this.currentUser = null;

        JPanel rightPanel = new JPanel();

        this.authInfo = new JLabel("Not signed in");
        this.signOutButton = new JButton("Sign Out");
        this.signOutButton.addActionListener(e -> this.signOut());

        rightPanel.add(this.authInfo);
        rightPanel.add(this.signOutButton);

        this.tabs.getNavbar().add(rightPanel, BorderLayout.EAST);
    }

    /**
     * The main method serves as the entry point for the application.
     * It creates an instance of the App class and calls its run method.
     * After running the application, it displays a message dialog indicating
     * that the database has been opened successfully.
     *
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        try {
            new App().run();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e, "Unhandled Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Establishes a connection to the database and initializes the user interface.
     * <p>
     * This method performs the following steps:
     * 1. Displays a loading message indicating that a connection to the database is being established.
     * 2. Attempts to connect to the PostgreSQL database using the provided credentials.
     * 3. If the connection is successful, removes the loading message.
     * 4. If the connection fails, displays an error message and terminates the application.
     * 5. Initializes the view switcher panel with buttons for "Cashier View" and "Manager View".
     * 6. Adds action listeners to the buttons to switch between views.
     * 7. Adds the view switcher panel to the top bar and sets up the main layout.
     * 8. Sets the initial view to the cashier view.
     * 9. Revalidates and repaints the UI components to reflect the changes.
     */
    public void run() {
        try {
            JLabel loadingLabel = new JLabel("Connecting to database...", JLabel.CENTER);
            this.add(loadingLabel, BorderLayout.CENTER);
            this.refresh();

            Class.forName("org.postgresql.Driver");
            this.databaseConnection = DriverManager.getConnection(App.url, App.user, App.password);

            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent windowEvent) {
                    if (App.this.databaseConnection != null) {
                        try {
                            App.this.databaseConnection.close();
                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(null, "Couldn't close database connection:\n\n" + e.getLocalizedMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    System.exit(0);
                }
            });

            this.remove(loadingLabel);
        } catch (SQLException | ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Couldn't connect to database:\n\n" + e.getLocalizedMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.add(this.tabs);

        if (App.start_in_cashier_view) {
            this.tabs.switchTab(0);;
        } else {
            this.tabs.switchTab(1);;
        }
    }

    /**
     * Returns the access level of the current user.
     *
     * @return the access level of the current user
     */
    private int getCurrentAccessLevel() {
        return (this.currentUser != null) ? this.currentUser.accessLevel : -1;
    }

    /**
     * Authenticates the user with the specified required access level.
     *
     * @param requiredAccessLevel The required access level for the user.
     * @return The authenticated Employee object if the user has the required access level, otherwise null.
     */
    private Employee authenticateUser(int requiredAccessLevel) {
        String id = JOptionPane.showInputDialog(null, "Please enter your employee ID", "Sign In", JOptionPane.QUESTION_MESSAGE);

        if (id == null || id.isEmpty()) {
            return null;
        }

        Employee user = EmployeeQueries.getEmployeeById(Integer.parseInt(id));

        return user;
    }

    /**
     * Signs in the user with the specified required access level.
     * If the current access level is already greater than or equal to the required access level,
     * the method returns without performing any action.
     * Otherwise, the method prompts the user to authenticate and continues to do so until a user with
     * the required access level is authenticated.
     * Once a user with the required access level is authenticated, the method sets the currentUser
     * field to the authenticated user and updates the authInfo text to display the signed-in user's name.
     *
     * @param requiredAccessLevel the minimum access level required for the user to sign in
     */
    private boolean signIn(int requiredAccessLevel) {
        if (this.getCurrentAccessLevel() >= requiredAccessLevel) {
            return true;
        }

        while (true) {
            Employee user = this.authenticateUser(requiredAccessLevel);
            if (user == null) {
                return false;
            } else if (user.accessLevel >= requiredAccessLevel) {
                this.currentUser = user;
                this.authInfo.setText("Signed in as " + this.currentUser.name + ", access level " + this.currentUser.accessLevel);
                this.signOutButton.setEnabled(true);
                return true;
            }
        }
    }

    /**
     * Signs out the current user and clears the elevated user.
     * After signing out, the user is considered not logged in.
     */
    private void signOut() {
        this.currentUser = null;
        this.authInfo.setText("Not signed in");
        this.signOutButton.setEnabled(false);
        this.tabs.switchTab(-1);
    }


    /**
     * Refreshes the view panel and packs the frame.
     */
    private void refresh() {
        this.revalidate();
        this.repaint();
    }

    private JPanel cashierView() {
        if (this.signIn(App.required_level_for_cashier)) {
            return new StorefrontGUI(this.databaseConnection);
        } else {
            return null;
        }
    }

    private JPanel managerView() {
        if (this.signIn(App.required_level_for_manager)) {
            return new ManagerGUI(databaseConnection);
        } else {
            return null;
        }
    }

}
