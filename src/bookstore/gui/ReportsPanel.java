package bookstore.gui;
import bookstore.dao.SaleDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import bookstore.dao.BookDAO;
import bookstore.model.Book;

public class ReportsPanel extends JPanel {
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JLabel revenueLabel;
    private JLabel salesCountLabel;
    private JLabel lowStockLabel;
    private JTextField dateField;
    private JButton filterButton;
    private JButton showAllButton;

    public ReportsPanel() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // TITLE
        JLabel titleLabel = new JLabel("Sales Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        this.add(titleLabel, BorderLayout.NORTH);

        // MAIN PANEL
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // SUMMARY CARDS
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 15, 15));

        JPanel revenueCard = new JPanel(new BorderLayout());
        revenueCard.setBorder(BorderFactory.createTitledBorder("Total Revenue"));

        revenueLabel = new JLabel("KSh 0.00");
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        revenueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        revenueCard.add(revenueLabel, BorderLayout.CENTER);

        JPanel salesCard = new JPanel(new BorderLayout());
        salesCard.setBorder(BorderFactory.createTitledBorder("Total Sales"));

        salesCountLabel = new JLabel("0");
        salesCountLabel.setFont(new Font("Arial", Font.BOLD, 24));
        salesCountLabel.setHorizontalAlignment(SwingConstants.CENTER);

        salesCard.add(salesCountLabel, BorderLayout.CENTER);

        summaryPanel.add(revenueCard);
        summaryPanel.add(salesCard);

        // LOW STOCK CARD
        JPanel lowStockCard = new JPanel(new BorderLayout());
        lowStockCard.setBorder(BorderFactory.createTitledBorder("Low-Stock Books"));

        lowStockLabel = new JLabel("0");
        lowStockLabel.setFont(new Font("Arial", Font.BOLD, 24));
        lowStockLabel.setHorizontalAlignment(SwingConstants.CENTER);

        lowStockCard.add(lowStockLabel, BorderLayout.CENTER);

        summaryPanel.add(lowStockCard);

        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        // SALES HISTORY TABLE
        String[] columns = {
                "Sale ID",
                "Customer",
                "Date and Time",
                "Total (KSh)"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(
                    int row, int column) {
                return false;
            }
        };

        salesTable = new JTable(tableModel);
        salesTable.setRowHeight(25);

        JPanel dateFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        dateField = new JTextField(10);
        filterButton = new JButton("Filter by Date");
        filterButton.addActionListener(e -> {
            String date = dateField.getText().trim();

            if(date.isEmpty()){
                JOptionPane.showMessageDialog(this, "Please enter a date.");
                return;
            }

            try{
                java.time.LocalDate.parse(date);
                loadSalesByDate(date);
            }
            catch(java.time.format.DateTimeParseException ex){
                JOptionPane.showMessageDialog(this, "Please enter the date in yyyy-MM-dd format.");
            }
        });
        showAllButton = new JButton("Show All Sales");
        showAllButton.addActionListener(e -> {
            dateField.setText("");
            loadReports();
        });

        dateFilterPanel.add(new JLabel("Select Date (yyyy-MM-dd):"));
        dateFilterPanel.add(dateField);
        dateFilterPanel.add(filterButton);
        dateFilterPanel.add(showAllButton);

        JPanel historyPanel = new JPanel(new BorderLayout(5, 5));

        historyPanel.add(dateFilterPanel, BorderLayout.NORTH);
        historyPanel.add(new JScrollPane(salesTable), BorderLayout.CENTER);

        mainPanel.add(historyPanel, BorderLayout.CENTER);

        // REFRESH BUTTON
        JButton refreshButton = new JButton("Refresh Reports");
        JButton lowStockButton = new JButton("View Low-Stock Books");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(lowStockButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        this.add(mainPanel, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> loadReports());
        lowStockButton.addActionListener(e -> showLowStockBooks());

        loadReports();
    }

    private void loadReports() {
        tableModel.setRowCount(0);

        SaleDAO saleDAO = new SaleDAO();

        // Load sales history
        List<Object[]> sales = saleDAO.getSalesHistory();

        for(Object[] sale : sales){
            tableModel.addRow(sale);
        }

        // Update summary
        double revenue = saleDAO.getTotalRevenue();
        int totalSales = saleDAO.getTotalSales();

        revenueLabel.setText(String.format("KSh %.2f", revenue));
        salesCountLabel.setText(String.valueOf(totalSales));

        // Update low-stock count
        BookDAO bookDAO = new BookDAO();

        int lowStockCount = bookDAO.getLowStockCount();
        lowStockLabel.setText(String.valueOf(lowStockCount));
    }

    private void loadSalesByDate(String date) {
        tableModel.setRowCount(0);

        SaleDAO saleDAO = new SaleDAO();

        List<Object[]> sales = saleDAO.getSalesByDate(date);

        for(Object[] sale : sales){
            tableModel.addRow(sale);
        }
    }

    private void showLowStockBooks() {
        BookDAO bookDAO = new BookDAO();

        List<Book> books = bookDAO.getLowStockBooks();

        String[] columns = {
                "Book ID",
                "Title",
                "Author",
                "Category",
                "Quantity"
        };

        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for(Book book : books){
            Object[] row = {
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getQuantity()
            };

            model.addRow(row);
        }

        JTable table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(700, 300));

        JOptionPane.showMessageDialog(
                this,
                scrollPane,
                "Books Requiring Restocking",
                JOptionPane.INFORMATION_MESSAGE
        );
    }
}