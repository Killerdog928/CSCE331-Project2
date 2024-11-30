package components;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.function.Supplier;
import javax.swing.*;

public class Tabs extends JPanel {
    private JPanel navbar;
    private JPanel tabsPanel;
    private JPanel panel;
    private JPanel defaultPanel;

    private ArrayList<JButton> tabButtons;
    private ArrayList<Supplier<JPanel>> tabPanelSuppliers;

    private int currentTabIndex;

    public Tabs(JPanel defaultPanel) {
        super(new BorderLayout());

        this.navbar = new JPanel(new BorderLayout());
        this.tabsPanel = new JPanel();
        this.panel = new JPanel(new BorderLayout());
        this.defaultPanel = defaultPanel;

        this.tabButtons = new ArrayList<>();
        this.tabPanelSuppliers = new ArrayList<>();

        this.navbar.add(this.tabsPanel, BorderLayout.WEST);
        this.add(navbar, BorderLayout.NORTH);
        this.add(panel, BorderLayout.CENTER);
    }

    public int addTab(String name, Supplier<JPanel> panelSupplier) {
        JButton button = new JButton(name);
        int index = this.tabButtons.size();

        button.addActionListener(e -> this.switchTab(index));

        this.tabButtons.add(button);
        this.tabPanelSuppliers.add(panelSupplier);

        this.tabsPanel.add(button);

        return index;
    }

    public void switchTab(int index) {
        JPanel panel = (index < 0) ? this.defaultPanel : this.tabPanelSuppliers.get(index).get();
        
        if (panel != null) {
            this.tabButtons.get(this.currentTabIndex).setEnabled(true);
            this.tabButtons.get(index).setEnabled(false);

            this.panel.removeAll();
            this.panel.add(panel, BorderLayout.CENTER);
    
            this.currentTabIndex = index;

            this.revalidate();
            this.repaint();
        }
    }

    public JPanel getNavbar() {
        return this.navbar;
    }
}
