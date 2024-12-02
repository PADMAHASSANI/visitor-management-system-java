import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

// Visitor Class
class Visitor {
    private String name;
    private String contactInfo;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    public Visitor(String name, String contactInfo) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.checkInTime = LocalDateTime.now();
    }

    public String getName() {
        return name;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public LocalDateTime getCheckOutTime() {
        return checkOutTime;
    }

    public void checkOut() {
        this.checkOutTime = LocalDateTime.now();
    }

    public Object[] toTableRow() {
        return new Object[]{
                name,
                contactInfo,
                checkInTime,
                checkOutTime == null ? "Currently Inside" : checkOutTime.toString()
        };
    }

    public String toLogEntry() {
        return "Visitor: " + name + ", Contact: " + contactInfo + ", Checked-in: " + checkInTime +
                (checkOutTime != null ? ", Checked-out: " + checkOutTime : ", Currently inside");
    }
}

// Visitor Management System
class VisitorManagementSystem {
    private List<Visitor> visitors;
    private DefaultTableModel tableModel;
    private List<String> activityLog;

    public VisitorManagementSystem(DefaultTableModel tableModel) {
        visitors = new ArrayList<>();
        this.tableModel = tableModel;
        activityLog = new ArrayList<>();
    }

    public void checkInVisitor(String name, String contactInfo) {
        Visitor visitor = new Visitor(name, contactInfo);
        visitors.add(visitor);
        tableModel.addRow(visitor.toTableRow());
        activityLog.add(visitor.toLogEntry());
    }

    public void checkOutVisitor(String name) {
        for (Visitor visitor : visitors) {
            if (visitor.getName().equals(name) && visitor.getCheckOutTime() == null) {
                visitor.checkOut();
                updateTableData();
                activityLog.add(visitor.toLogEntry());
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Visitor not found or already checked out.");
    }

    private void updateTableData() {
        tableModel.setRowCount(0); // Clear existing data
        for (Visitor visitor : visitors) {
            tableModel.addRow(visitor.toTableRow());
        }
    }

    public List<String> getVisitorLog() {
        return activityLog;
    }

    public List<Visitor> getCurrentVisitors() {
        List<Visitor> currentVisitors = new ArrayList<>();
        for (Visitor visitor : visitors) {
            if (visitor.getCheckOutTime() == null) {
                currentVisitors.add(visitor);
            }
        }
        return currentVisitors;
    }
}

// Main Class
public class Main extends JFrame {
    private VisitorManagementSystem vms;
    private JTextField nameField;
    private JTextField contactField;
    private DefaultTableModel tableModel;

    public Main() {
        setTitle("Visitor Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(173, 216, 230)); // Light Blue Background

        setLayout(new BorderLayout());

        // Table for visitor data
        String[] columnNames = {"Name", "Contact Info", "Check-In Time", "Check-Out Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable visitorTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(visitorTable);
        visitorTable.setFillsViewportHeight(true);
        visitorTable.setRowHeight(30);
        visitorTable.setFont(new Font("Arial", Font.PLAIN, 14));
        add(scrollPane, BorderLayout.CENTER);

        // Control Panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(7, 1, 10, 10));
        controlPanel.setBackground(new Color(220, 220, 220));

        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        nameField = new JTextField(20);
        nameField.setFont(new Font("Arial", Font.PLAIN, 18));

        JLabel contactLabel = new JLabel("Contact:");
        contactLabel.setFont(new Font("Arial", Font.BOLD, 18));
        contactField = new JTextField(20);
        contactField.setFont(new Font("Arial", Font.PLAIN, 18));

        JButton checkInButton = new JButton("Check-In Visitor");
        JButton checkOutButton = new JButton("Check-Out Visitor");
        JButton showCurrentVisitorsButton = new JButton("Show Current Visitors");
        JButton showVisitorLogButton = new JButton("Show Visitor Log");
        JButton exitButton = new JButton("Exit");

        // Set button styles
        Font buttonFont = new Font("Arial", Font.BOLD, 16);
        checkInButton.setFont(buttonFont);
        checkOutButton.setFont(buttonFont);
        showCurrentVisitorsButton.setFont(buttonFont);
        showVisitorLogButton.setFont(buttonFont);
        exitButton.setFont(buttonFont);

        checkInButton.setBackground(Color.GREEN);
        checkInButton.setForeground(Color.BLACK);

        checkOutButton.setBackground(Color.ORANGE);
        checkOutButton.setForeground(Color.BLACK);

        showCurrentVisitorsButton.setBackground(Color.GRAY);
        showCurrentVisitorsButton.setForeground(Color.BLACK);

        showVisitorLogButton.setBackground(Color.CYAN);
        showVisitorLogButton.setForeground(Color.BLACK);

        exitButton.setBackground(Color.RED);
        exitButton.setForeground(Color.BLACK);

        // Add components to control panel
        controlPanel.add(nameLabel);
        controlPanel.add(nameField);
        controlPanel.add(contactLabel);
        controlPanel.add(contactField);
        controlPanel.add(checkInButton);
        controlPanel.add(checkOutButton);
        controlPanel.add(showCurrentVisitorsButton);
        controlPanel.add(showVisitorLogButton);
        controlPanel.add(exitButton);

        add(controlPanel, BorderLayout.EAST);

        // Initialize Visitor Management System
        vms = new VisitorManagementSystem(tableModel);

        // Button Actions
        checkInButton.addActionListener(e -> {
            String name = nameField.getText();
            String contact = contactField.getText();
            if (!name.isEmpty() && !contact.isEmpty()) {
                vms.checkInVisitor(name, contact);
                nameField.setText("");
                contactField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Name and Contact cannot be empty.");
            }
        });

        checkOutButton.addActionListener(e -> {
            String name = nameField.getText();
            if (!name.isEmpty()) {
                vms.checkOutVisitor(name);
                nameField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Name cannot be empty.");
            }
        });

        showCurrentVisitorsButton.addActionListener(e -> {
            List<Visitor> currentVisitors = vms.getCurrentVisitors();
            StringBuilder message = new StringBuilder("Current Visitors:\n");
            for (Visitor visitor : currentVisitors) {
                message.append(visitor.toLogEntry()).append("\n");
            }
            JOptionPane.showMessageDialog(this, message.toString());
        });

        showVisitorLogButton.addActionListener(e -> {
            List<String> logEntries = vms.getVisitorLog();
            StringBuilder message = new StringBuilder("Visitor Log:\n");
            for (String logEntry : logEntries) {
                message.append(logEntry).append("\n");
            }
            JOptionPane.showMessageDialog(this, message.toString());
        });

        exitButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
