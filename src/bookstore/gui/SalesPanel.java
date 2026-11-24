package bookstore.gui;
import bookstore.dao.BookDAO;
import bookstore.dao.CustomerDAO;
import bookstore.model.Book;
import bookstore.model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import bookstore.dao.SaleDAO;
import bookstore.model.Sale;
import bookstore.model.SaleItem;
import java.util.ArrayList;

public class SalesPanel extends JPanel {
    private JComboBox<Customer> customerComboBox;
    private JComboBox<Book> bookComboBox;
    private JTextField quantityField;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalLabel;
    private double total = 0.0;

    public SalesPanel() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // TITLE
        JLabel titleLabel = new JLabel("Sales / Point of Sale");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        this.add(titleLabel, BorderLayout.NORTH);

        // TOP FORM
        JPanel formPanel = new JPanel(new GridLayout(2, 4, 10, 10));

        customerComboBox = new JComboBox<>();
        bookComboBox = new JComboBox<>();
        quantityField = new JTextField();

        JButton addButton = new JButton("Add to Cart");

        formPanel.add(new JLabel("Customer:"));
        formPanel.add(customerComboBox);
        formPanel.add(new JLabel("Book:"));
        formPanel.add(bookComboBox);
        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);
        formPanel.add(new JLabel(""));
        formPanel.add(addButton);

        this.add(formPanel, BorderLayout.NORTH);

        // CART TABLE
        String[] columns = {
                "Book ID",
                "Book",
                "Price",
                "Quantity",
                "Subtotal"
        };

        cartTableModel = new DefaultTableModel(columns, 0);
        cartTable = new JTable(cartTableModel);
        cartTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(cartTable);

        this.add(scrollPane, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottomPanel = new JPanel(new BorderLayout());

        totalLabel = new JLabel("Total: KSh 0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 2));

        JButton removeButton = new JButton("Remove Item");
        JButton completeSaleButton = new JButton("Complete Sale");

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.add(removeButton);
        buttonsPanel.add(completeSaleButton);

        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(buttonsPanel, BorderLayout.EAST);

       this.add(bottomPanel, BorderLayout.SOUTH);

        // LOAD DATA
        loadCustomers();
        loadBooks();

        // BUTTON ACTIONS
        addButton.addActionListener(e -> addToCart());
        removeButton.addActionListener(e -> removeFromCart());
        completeSaleButton.addActionListener(e -> completeSale());
    }

    private void loadCustomers() {
        CustomerDAO customerDAO = new CustomerDAO();

        List<Customer> customers = customerDAO.getAllCustomers();

        for(Customer customer : customers){
            customerComboBox.addItem(customer);
        }
    }

    private void loadBooks() {
        bookComboBox.removeAllItems();

        BookDAO bookDAO = new BookDAO();

        List<Book> books = bookDAO.getAllBooks();

        for(Book book : books){
            bookComboBox.addItem(book);
        }
    }

    private void addToCart() {
        Book selectedBook = (Book) bookComboBox.getSelectedItem();

        if(selectedBook == null){
            JOptionPane.showMessageDialog(this, "Please select a book.");

            return;
        }

        try{
            int quantity = Integer.parseInt(quantityField.getText());

            if(quantity <= 0){
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");

                return;
            }

            // Calculate how many copies are already in the cart
            int quantityInCart = 0;

            for(int i = 0; i < cartTableModel.getRowCount(); i++){
                int cartBookId = Integer.parseInt(cartTableModel.getValueAt(i, 0).toString());

                if(cartBookId == selectedBook.getBookId()){
                    quantityInCart += Integer.parseInt(cartTableModel.getValueAt(i, 3).toString());
                }
            }

            // Check total quantity against available stock
            if(quantity + quantityInCart > selectedBook.getQuantity()){
                JOptionPane.showMessageDialog(
                        this,
                        "Not enough stock available.\n"
                                + "Available: " + selectedBook.getQuantity()
                                + "\nAlready in cart: " + quantityInCart
                );
                return;
            }

            double price = selectedBook.getPrice();
            double subtotal = price * quantity;

            Object[] row = {
                    selectedBook.getBookId(),
                    selectedBook.getTitle(),
                    price,
                    quantity,
                    subtotal
            };

            cartTableModel.addRow(row);

            total += subtotal;

            updateTotal();

            quantityField.setText("");
        }
        catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.");
        }
    }

    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select an item to remove.");

            return;
        }

        double subtotal = Double.parseDouble(cartTableModel.getValueAt(selectedRow, 4).toString());

        total -= subtotal;

        cartTableModel.removeRow(selectedRow);

        updateTotal();
    }

    private void updateTotal() {
        totalLabel.setText(String.format("Total: KSh %.2f", total));
    }

    private void completeSale() {
        // Check if cart is empty
        if(cartTableModel.getRowCount() == 0){
            JOptionPane.showMessageDialog(this, "The cart is empty.");

            return;
        }

        // Get selected customer
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();

        if(selectedCustomer == null){
            JOptionPane.showMessageDialog(this, "Please select a customer.");

            return;
        }

        // Confirm the sale
        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Complete this sale?\nTotal: KSh "
                        + String.format("%.2f", total),
                "Confirm Sale",
                JOptionPane.YES_NO_OPTION
        );

        if(confirmation != JOptionPane.YES_OPTION){
            return;
        }

        // Create the sale object
        Sale sale = new Sale(selectedCustomer.getCustomerId(), total);

        // Create a list of sale items
        List<SaleItem> items = new ArrayList<>();

        for(int i = 0; i < cartTableModel.getRowCount(); i++){
            int bookId = Integer.parseInt(cartTableModel.getValueAt(i, 0).toString());
            double price = Double.parseDouble(cartTableModel.getValueAt(i, 2).toString());
            int quantity = Integer.parseInt(cartTableModel.getValueAt(i, 3).toString());
            SaleItem item = new SaleItem(bookId, quantity, price);

            items.add(item);
        }

        // Save the sale in the database
        SaleDAO saleDAO = new SaleDAO();

        int saleId = saleDAO.completeSale(sale, items);

        if(saleId != -1){
            // Build the receipt
            StringBuilder receipt = new StringBuilder();

            receipt.append("================================\n");
            receipt.append("       BOOKSTORE RECEIPT\n");
            receipt.append("================================\n");

            receipt.append("Sale ID: ").append(saleId).append("\n");
            receipt.append("Customer: ").append(selectedCustomer.getName()).append("\n");

            receipt.append("--------------------------------\n");
            receipt.append("BOOKS PURCHASED\n");
            receipt.append("--------------------------------\n");

            for(int i = 0; i < cartTableModel.getRowCount(); i++){
                String bookName = cartTableModel.getValueAt(i, 1).toString();
                double price = Double.parseDouble(cartTableModel.getValueAt(i, 2).toString());
                int quantity = Integer.parseInt(cartTableModel.getValueAt(i, 3).toString());
                double subtotal = Double.parseDouble(cartTableModel.getValueAt(i, 4).toString());

                receipt.append(bookName).append("\n");

                receipt.append("  ")
                        .append(quantity)
                        .append(" x KSh ")
                        .append(String.format("%.2f", price))
                        .append(" = KSh ")
                        .append(String.format("%.2f", subtotal))
                        .append("\n\n");
            }

            receipt.append("--------------------------------\n");

            receipt.append("TOTAL: KSh ").append(String.format("%.2f", total)).append("\n");

            receipt.append("================================\n");
            receipt.append("       Thank you for shopping!\n");
            receipt.append("================================");

            // Display receipt with Print button
            JTextArea receiptArea = new JTextArea(receipt.toString());
            receiptArea.setEditable(false);
            receiptArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            receiptArea.setCaretPosition(0);

            JScrollPane receiptScrollPane = new JScrollPane(receiptArea);
            receiptScrollPane.setPreferredSize(new Dimension(450, 400));

            JButton printButton = new JButton("Print Receipt");
            JButton closeButton = new JButton("Close");

            JPanel receiptButtonPanel = new JPanel();
            receiptButtonPanel.add(printButton);
            receiptButtonPanel.add(closeButton);

            JDialog receiptDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Sales Receipt", true);
            receiptDialog.setLayout(new BorderLayout(10, 10));
            receiptDialog.add(receiptScrollPane, BorderLayout.CENTER);
            receiptDialog.add(receiptButtonPanel, BorderLayout.SOUTH);

            printButton.addActionListener(e -> {
                try{
                    boolean printed = receiptArea.print();

                    if(printed){
                        JOptionPane.showMessageDialog(receiptDialog, "Receipt printed successfully.");
                    }
                }
                catch(java.awt.print.PrinterException ex){
                    JOptionPane.showMessageDialog(receiptDialog, "Unable to print receipt: " + ex.getMessage());
                }
            });

            closeButton.addActionListener(e -> receiptDialog.dispose());

            receiptDialog.pack();
            receiptDialog.setLocationRelativeTo(this);
            receiptDialog.setVisible(true);

            // Clear cart after displaying receipt
            cartTableModel.setRowCount(0);

            total = 0.0;
            updateTotal();

            loadBooks();
        }
        else{
            JOptionPane.showMessageDialog(
                    this,
                    "Sale could not be completed.\n"
                            + "Please check the stock and try again."
            );
        }
    }
}