package views.manager_panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.function.BiFunction;
import javax.swing.*;

public class TimeChart extends JPanel {
    private JPanel topBar;
    private JPanel chartPanel;

    BiFunction<Date, Date, JPanel> plotFunction;

    @SuppressWarnings("deprecation")
    public TimeChart(String title, BiFunction<Date, Date, JPanel> plotFunction) {
        super(new BorderLayout());
        this.topBar = new JPanel(new BorderLayout());
        this.chartPanel = new JPanel(new BorderLayout());
        JPanel datePickerPanel = new JPanel();

        JTextField startDate = new JTextField("2023-10-16");
        JTextField endDate = new JTextField("2024-10-16");

        this.plotFunction = plotFunction;

        JButton plotButton = new JButton("Generate");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        plotButton.addActionListener(e -> {
            try {
                this.plot(new Date(dateFormatter.parse(startDate.getText()).getTime()), new Date(dateFormatter.parse(endDate.getText()).getTime()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Please use the format yyyy-MM-dd.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        datePickerPanel.add(new JLabel("Date range:"));
        datePickerPanel.add(startDate);
        datePickerPanel.add(new JLabel("to"));
        datePickerPanel.add(endDate);

        this.topBar.add(new JLabel(title), BorderLayout.NORTH);
        this.topBar.add(datePickerPanel, BorderLayout.WEST);
        this.topBar.add(plotButton, BorderLayout.EAST);

        this.add(topBar, BorderLayout.NORTH);
        this.add(chartPanel, BorderLayout.CENTER);

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    public void plot(Date startDate, Date endDate) {

        this.chartPanel.removeAll();

        this.chartPanel.add(this.plotFunction.apply(startDate, endDate), BorderLayout.CENTER);

        this.revalidate();
        this.repaint();
    }
}
