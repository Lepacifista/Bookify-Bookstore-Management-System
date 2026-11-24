package bookstore.gui;
import javax.swing.*;
import java.awt.*;
import bookstore.dao.BookDAO;
import bookstore.dao.CustomerDAO;
import bookstore.dao.SaleDAO;

public class MainFrame extends JFrame {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public MainFrame() {
        this.setTitle("BOOKIFY");
        this.setSize(1100, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        // TOP HEADER
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setPreferredSize(new Dimension(0, 70));

        JLabel titleLabel = new JLabel("  BOOKSTORE MANAGEMENT SYSTEM");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        headerPanel.add(titleLabel, BorderLayout.WEST);

        this.add(headerPanel, BorderLayout.NORTH);

        // SIDEBAR
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        sidebarPanel.setLayout(new GridLayout(7, 1, 5, 5));

        JButton dashboardButton = new JButton("Dashboard");
        dashboardButton.addActionListener(e ->
                cardLayout.show(contentPanel, "Dashboard")
        );
        JButton booksButton = new JButton("Books");
        booksButton.addActionListener(e ->
                cardLayout.show(contentPanel, "Books")
        );
        JButton customersButton = new JButton("Customers");
        customersButton.addActionListener(e ->
                cardLayout.show(contentPanel, "Customers")
        );
        JButton suppliersButton = new JButton("Suppliers");
        suppliersButton.addActionListener(e ->
                cardLayout.show(contentPanel, "Suppliers")
        );
        JButton salesButton = new JButton("Sales");
        salesButton.addActionListener(e ->
                cardLayout.show(contentPanel, "Sales")
        );
        JButton reportsButton = new JButton("Reports");
        reportsButton.addActionListener(e ->
                cardLayout.show(contentPanel, "Reports")
        );
        JButton logoutButton = new JButton("Logout");

        sidebarPanel.add(dashboardButton);
        sidebarPanel.add(booksButton);
        sidebarPanel.add(customersButton);
        sidebarPanel.add(suppliersButton);
        sidebarPanel.add(salesButton);
        sidebarPanel.add(reportsButton);
        sidebarPanel.add(logoutButton);

        this.add(sidebarPanel, BorderLayout.WEST);

        // CONTENT AREA
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // DASHBOARD
        JPanel dashboardPanel = new JPanel(new BorderLayout(10, 20));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // WELCOME LABEL
        JLabel welcomeLabel = new JLabel("Welcome to the Bookify Dashboard");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 26));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        dashboardPanel.add(welcomeLabel, BorderLayout.NORTH);

        // STATISTICS PANEL
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));

        JLabel booksLabel = new JLabel("Total Books: 0");
        JLabel customersLabel = new JLabel("Total Customers: 0");
        JLabel salesLabel = new JLabel("Total Sales: 0");
        JLabel revenueLabel = new JLabel("Total Revenue: KSh 0.00");

        // REFRESH DASHBOARD STATISTICS
        JButton refreshStatsButton = new JButton("Refresh Statistics");

        Runnable refreshStats = () -> {
            BookDAO bookDAO = new BookDAO();
            CustomerDAO customerDAO = new CustomerDAO();
            SaleDAO saleDAO = new SaleDAO();

            int totalBooks = bookDAO.getAllBooks().size();
            int totalCustomers = customerDAO.getAllCustomers().size();
            int totalSales = saleDAO.getTotalSales();
            double totalRevenue = saleDAO.getTotalRevenue();

            booksLabel.setText("Total Books: " + totalBooks);
            customersLabel.setText("Total Customers: " + totalCustomers);
            salesLabel.setText("Total Sales: " + totalSales);
            revenueLabel.setText(String.format("Total Revenue: KSh %.2f", totalRevenue));
        };

        // Load statistics when the Dashboard opens
        refreshStats.run();

        refreshStatsButton.addActionListener(e -> refreshStats.run());

        dashboardPanel.add(refreshStatsButton, BorderLayout.SOUTH);

        JLabel[] labels = {booksLabel, customersLabel, salesLabel, revenueLabel};

        for (JLabel label : labels) {
            label.setFont(new Font("Arial", Font.BOLD, 20));
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
            statsPanel.add(label);
        }

        dashboardPanel.add(statsPanel, BorderLayout.CENTER);
        contentPanel.add(dashboardPanel, "Dashboard");

        BookPanel bookPanel = new BookPanel();
        contentPanel.add(bookPanel, "Books");

        CustomerPanel customerPanel = new CustomerPanel();
        contentPanel.add(customerPanel, "Customers");

        SupplierPanel supplierPanel = new SupplierPanel();
        contentPanel.add(supplierPanel, "Suppliers");

        SalesPanel salesPanel = new SalesPanel();
        contentPanel.add(salesPanel, "Sales");

        ReportsPanel reportsPanel = new ReportsPanel();
        contentPanel.add(reportsPanel, "Reports");

        this.add(contentPanel, BorderLayout.CENTER);

        // LOGOUT
        logoutButton.addActionListener(e -> {
            this.dispose();

            new LoginFrame();
        });

        this.setVisible(true);
    }
}