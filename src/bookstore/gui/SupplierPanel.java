package bookstore.gui;
import bookstore.dao.SupplierDAO;
import bookstore.model.Supplier;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SupplierPanel extends JPanel {
    private JTable supplierTable;
    private DefaultTableModel tableModel;

    public SupplierPanel() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // TITLE
        JLabel titleLabel = new JLabel("Supplier Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

       this.add(titleLabel, BorderLayout.NORTH);

        // TABLE
        String[] columns = {
                "ID",
                "Name",
                "Phone",
                "Email",
                "Address"
        };

        tableModel = new DefaultTableModel(columns, 0);
        supplierTable = new JTable(tableModel);
        supplierTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(supplierTable);

        this.add(scrollPane, BorderLayout.CENTER);

        // BUTTONS
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("Add Supplier");
        JButton updateButton = new JButton("Update Supplier");
        JButton deleteButton = new JButton("Delete Supplier");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        this.add(buttonPanel, BorderLayout.SOUTH);

        // LOAD DATA
        loadSuppliers();

        // BUTTON ACTIONS
        refreshButton.addActionListener(e -> loadSuppliers());
        addButton.addActionListener(e -> showAddSupplierDialog());
        updateButton.addActionListener(e -> showUpdateSupplierDialog());
        deleteButton.addActionListener(e -> deleteSelectedSupplier());
    }

    private void loadSuppliers() {
        tableModel.setRowCount(0);

        SupplierDAO supplierDAO = new SupplierDAO();

        List<Supplier> suppliers = supplierDAO.getAllSuppliers();

        for(Supplier supplier : suppliers){
            Object[] row = {
                    supplier.getSupplierId(),
                    supplier.getName(),
                    supplier.getPhone(),
                    supplier.getEmail(),
                    supplier.getAddress()
            };

            tableModel.addRow(row);
        }
    }

    private void showAddSupplierDialog() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField addressField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        panel.add(new JLabel("Address:"));
        panel.add(addressField);

        int result = JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Add New Supplier",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if(result == JOptionPane.OK_OPTION){
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            String address = addressField.getText();

            if(name.isEmpty()){
                JOptionPane.showMessageDialog(this, "Supplier name is required.");

                return;
            }

            Supplier supplier = new Supplier(
                            name,
                            phone,
                            email,
                            address
                    );

            SupplierDAO supplierDAO = new SupplierDAO();

            if(supplierDAO.addSupplier(supplier)){
                JOptionPane.showMessageDialog(this, "Supplier added successfully!");

                loadSuppliers();

            }
            else{
                JOptionPane.showMessageDialog(this, "Failed to add supplier.");
            }
        }
    }

    private void showUpdateSupplierDialog() {
        int selectedRow = supplierTable.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select a supplier to update.");

            return;
        }

        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = tableModel.getValueAt(selectedRow, 1).toString();
        String phone = tableModel.getValueAt(selectedRow, 2).toString();
        String email = tableModel.getValueAt(selectedRow, 3).toString();
        String address = tableModel.getValueAt(selectedRow, 4).toString();

        JTextField nameField = new JTextField(name);
        JTextField phoneField = new JTextField(phone);
        JTextField emailField = new JTextField(email);
        JTextField addressField = new JTextField(address);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        panel.add(new JLabel("Address:"));
        panel.add(addressField);

        int result = JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Update Supplier",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if(result == JOptionPane.OK_OPTION){
            String newName = nameField.getText();
            String newPhone = phoneField.getText();
            String newEmail = emailField.getText();
            String newAddress = addressField.getText();

            if(newName.isEmpty()){
                JOptionPane.showMessageDialog(this, "Supplier name is required.");

                return;
            }

            Supplier supplier = new Supplier(
                            supplierId,
                            newName,
                            newPhone,
                            newEmail,
                            newAddress
                    );

            SupplierDAO supplierDAO = new SupplierDAO();

            if(supplierDAO.updateSupplier(supplier)){
                JOptionPane.showMessageDialog(this, "Supplier updated successfully!");

                loadSuppliers();
            }
            else{
                JOptionPane.showMessageDialog(this, "Failed to update supplier.");
            }
        }
    }

    private void deleteSelectedSupplier() {
        int selectedRow = supplierTable.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete.");

            return;
        }

        int supplierId = (int) tableModel.getValueAt(selectedRow, 0);
        String supplierName = tableModel.getValueAt(selectedRow, 1).toString();

        int confirmation = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete \""
                                + supplierName + "\"?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if(confirmation == JOptionPane.YES_OPTION){
            SupplierDAO supplierDAO = new SupplierDAO();

            if(supplierDAO.deleteSupplier(supplierId)){
                JOptionPane.showMessageDialog(this, "Supplier deleted successfully!");

                loadSuppliers();
            }
            else{
                JOptionPane.showMessageDialog(this, "Failed to delete supplier.");
            }
        }
    }
}