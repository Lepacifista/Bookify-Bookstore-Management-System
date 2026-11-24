package bookstore.gui;
import bookstore.dao.BookDAO;
import bookstore.dao.SupplierDAO;
import bookstore.model.Book;
import bookstore.model.Supplier;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;

public class BookPanel extends JPanel {
    private JTable bookTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public BookPanel() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // TOP PANEL
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));

        // TITLE
        JLabel titleLabel = new JLabel("Book Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        topPanel.add(titleLabel, BorderLayout.NORTH);

        // TABLE
        String[] columns = {
                "ID",
                "Title",
                "Author",
                "Category",
                "Price",
                "Quantity",
                "Supplier ID"
        };

        tableModel = new DefaultTableModel(columns, 0);

        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(25);

        sorter = new TableRowSorter<>(tableModel);
        bookTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(bookTable);

        this.add(scrollPane, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout(10, 10));
        JLabel searchLabel = new JLabel("Search Books:");

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(250, 35));

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        topPanel.add(searchPanel, BorderLayout.CENTER);
        this.add(topPanel, BorderLayout.NORTH);

        searchField.getDocument().addDocumentListener(
                new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        filterBooks(searchField.getText());
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        filterBooks(searchField.getText());
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        filterBooks(searchField.getText());
                    }
                }
        );

        // BUTTONS
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add Book");
        addButton.addActionListener(e -> showAddBookDialog());
        JButton updateButton = new JButton("Update Book");
        updateButton.addActionListener(e -> showUpdateBookDialog());
        JButton deleteButton = new JButton("Delete Book");
        deleteButton.addActionListener(e -> deleteSelectedBook());
        JButton refreshButton = new JButton("Refresh");
        JButton restockButton = new JButton("Restock Book");
        restockButton.addActionListener(e -> restockSelectedBook());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(restockButton);

        this.add(buttonPanel, BorderLayout.SOUTH);

        // LOAD BOOKS
        loadBooks();

        // REFRESH BUTTON
        refreshButton.addActionListener(e -> loadBooks());
    }

    private void loadBooks() {
        tableModel.setRowCount(0);

        BookDAO bookDAO = new BookDAO();

        List<Book> books = bookDAO.getAllBooks();

        for(Book book : books){
            Object[] row = {
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getPrice(),
                    book.getQuantity(),
                    book.getSupplierId()
            };

            tableModel.addRow(row);
        }
    }

    private void showAddBookDialog() {
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField quantityField = new JTextField();

        JComboBox<Supplier> supplierComboBox = new JComboBox<>();

        SupplierDAO supplierDAO = new SupplierDAO();

        List<Supplier> suppliers = supplierDAO.getAllSuppliers();

        for(Supplier supplier : suppliers){
            supplierComboBox.addItem(supplier);
        }

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        panel.add(new JLabel("Title:"));
        panel.add(titleField);

        panel.add(new JLabel("Author:"));
        panel.add(authorField);

        panel.add(new JLabel("Category:"));
        panel.add(categoryField);

        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        panel.add(new JLabel("Supplier:"));
        panel.add(supplierComboBox);

        int result = JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Add New Book",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if(result == JOptionPane.OK_OPTION){
            try{
                String title = titleField.getText();
                String author = authorField.getText();
                String category = categoryField.getText();
                double price = Double.parseDouble(priceField.getText());
                int quantity = Integer.parseInt(quantityField.getText());

                Supplier selectedSupplier = (Supplier) supplierComboBox.getSelectedItem();

                if(title.isEmpty() || author.isEmpty()){
                    JOptionPane.showMessageDialog(this, "Title and author are required.");

                    return;
                }

                if(selectedSupplier == null){
                    JOptionPane.showMessageDialog(this, "Please select a supplier.");

                    return;
                }

                int supplierId = selectedSupplier.getSupplierId();

                Book book = new Book(
                                title,
                                author,
                                category,
                                price,
                                quantity,
                                supplierId
                        );

                BookDAO bookDAO = new BookDAO();

                if(bookDAO.addBook(book)){
                    JOptionPane.showMessageDialog(this, "Book added successfully!");

                    loadBooks();
                }
                else{
                    JOptionPane.showMessageDialog(this, "Failed to add book.");
                }

            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this, "Price and quantity must be valid numbers.");
            }
        }
    }

    private void showUpdateBookDialog() {
        int selectedRow = bookTable.getSelectedRow();
        int modelRow = bookTable.convertRowIndexToModel(selectedRow);

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select a book to update.");

            return;
        }

        int bookId = (int) tableModel.getValueAt(selectedRow, 0);

        String title = tableModel.getValueAt(modelRow, 1).toString();
        String author = tableModel.getValueAt(modelRow, 2).toString();
        String category = tableModel.getValueAt(modelRow, 3).toString();
        String price = tableModel.getValueAt(modelRow, 4).toString();
        String quantity = tableModel.getValueAt(modelRow, 5).toString();
        String supplierId = tableModel.getValueAt(modelRow, 6).toString();

        JTextField titleField = new JTextField(title);
        JTextField authorField = new JTextField(author);
        JTextField categoryField = new JTextField(category);
        JTextField priceField = new JTextField(price);
        JTextField quantityField = new JTextField(quantity);
        JTextField supplierField = new JTextField(supplierId);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));

        panel.add(new JLabel("Title:"));
        panel.add(titleField);

        panel.add(new JLabel("Author:"));
        panel.add(authorField);

        panel.add(new JLabel("Category:"));
        panel.add(categoryField);

        panel.add(new JLabel("Price:"));
        panel.add(priceField);

        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);

        panel.add(new JLabel("Supplier ID:"));
        panel.add(supplierField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Book",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if(result == JOptionPane.OK_OPTION){
            try{
                String newTitle = titleField.getText();
                String newAuthor = authorField.getText();
                String newCategory = categoryField.getText();

                double newPrice = Double.parseDouble(priceField.getText());

                int newQuantity = Integer.parseInt(quantityField.getText());

                int newSupplierId = Integer.parseInt(supplierField.getText());

                if(newTitle.isEmpty() || newAuthor.isEmpty()){
                    JOptionPane.showMessageDialog(this, "Title and author are required.");

                    return;
                }

                Book book = new Book(
                        bookId,
                        newTitle,
                        newAuthor,
                        newCategory,
                        newPrice,
                        newQuantity,
                        newSupplierId
                );

                BookDAO bookDAO = new BookDAO();

                if(bookDAO.updateBook(book)){
                    JOptionPane.showMessageDialog(this, "Book updated successfully!");

                    loadBooks();

                }
                else{
                    JOptionPane.showMessageDialog(this, "Failed to update book.");
                }

            }
            catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(this, "Price, quantity and supplier ID must be valid numbers.");
            }
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select a book to delete.");

            return;
        }

        int modelRow = bookTable.convertRowIndexToModel(selectedRow);
        int bookId = (int) tableModel.getValueAt(modelRow, 0);

        String bookTitle = tableModel.getValueAt(modelRow, 1).toString();

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete \"" + bookTitle + "\"?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if(confirmation == JOptionPane.YES_OPTION){
            BookDAO bookDAO = new BookDAO();

            if (bookDAO.deleteBook(bookId)) {
                JOptionPane.showMessageDialog(this, "Book deleted successfully!");

                loadBooks();

            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete book.");
            }
        }
    }

    private void filterBooks(String searchText) {
        if(searchText.trim().isEmpty()){
            sorter.setRowFilter(null);
        }
        else{
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(searchText), 1, 2, 3));
        }
    }

    private void restockSelectedBook() {
        int selectedRow = bookTable.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select a book to restock.");
            return;
        }

        int modelRow = bookTable.convertRowIndexToModel(selectedRow);
        int bookId = (int) tableModel.getValueAt(modelRow, 0);
        String title = tableModel.getValueAt(modelRow, 1).toString();
        String input = JOptionPane.showInputDialog(this, "Enter the number of copies to add for: " + title);

        if(input == null || input.trim().isEmpty()){
            return;
        }

        try{
            int quantityToAdd = Integer.parseInt(input.trim());

            if(quantityToAdd <= 0){
                JOptionPane.showMessageDialog(this, "Please enter a quantity greater than zero.");
                return;
            }

            int currentQuantity = Integer.parseInt(tableModel.getValueAt(modelRow, 5).toString());
            int newQuantity = currentQuantity + quantityToAdd;

            BookDAO bookDAO = new BookDAO();

            Book book = bookDAO.getAllBooks()
                    .stream()
                    .filter(b -> b.getBookId() == bookId)
                    .findFirst()
                    .orElse(null);

            if(book != null){
                book.setQuantity(newQuantity);

                boolean success = bookDAO.updateBook(book);

                if(success){
                    JOptionPane.showMessageDialog(this, "Book restocked successfully!");
                    loadBooks();
                }
                else{
                    JOptionPane.showMessageDialog(this, "Failed to restock the book.");
                }
            }

        }
        catch(NumberFormatException ex){
            JOptionPane.showMessageDialog(this, "Please enter a valid whole number.");
        }
    }
}