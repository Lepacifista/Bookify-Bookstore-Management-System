package bookstore.gui;
import bookstore.dao.CustomerDAO;
import bookstore.model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {
    private JTable customerTable;
    private DefaultTableModel tableModel;

    public CustomerPanel() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // TITLE
        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        this.add(titleLabel, BorderLayout.NORTH);

        // TABLE
        String[] columns = {
                "ID",
                "Name",
                "Phone",
                "Email"
        };

        tableModel = new DefaultTableModel(columns, 0);

        customerTable = new JTable(tableModel);
        customerTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(customerTable);

        this.add(scrollPane, BorderLayout.CENTER);

        // BUTTONS
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Customer");
        JButton updateButton = new JButton("Update Customer");
        JButton deleteButton = new JButton("Delete Customer");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);

        this.add(buttonPanel, BorderLayout.SOUTH);

        // LOAD CUSTOMERS
        loadCustomers();

        // BUTTON ACTIONS
        refreshButton.addActionListener(e -> loadCustomers());
        addButton.addActionListener(e -> showAddCustomerDialog());
        updateButton.addActionListener(e -> showUpdateCustomerDialog());
        deleteButton.addActionListener(e -> deleteSelectedCustomer());
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);

        CustomerDAO customerDAO = new CustomerDAO();

        List<Customer> customers = customerDAO.getAllCustomers();

        for(Customer customer : customers){
            Object[] row = {
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getEmail()
            };

            tableModel.addRow(row);
        }
    }

    private void showAddCustomerDialog() {
        JTextField nameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Add New Customer",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if(result == JOptionPane.OK_OPTION){
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();

            if(name.isEmpty()){
                JOptionPane.showMessageDialog(this, "Customer name is required.");

                return;
            }

            Customer customer = new Customer(name, phone, email);
            CustomerDAO customerDAO = new CustomerDAO();

            if(customerDAO.addCustomer(customer)){
                JOptionPane.showMessageDialog(this, "Customer added successfully!");

                loadCustomers();
            }
            else{
                JOptionPane.showMessageDialog(this, "Failed to add customer.");
            }
        }
    }

    private void showUpdateCustomerDialog() {
        int selectedRow = customerTable.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select a customer to update.");

            return;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        String name = tableModel.getValueAt(selectedRow, 1).toString();
        String phone = tableModel.getValueAt(selectedRow, 2).toString();
        String email = tableModel.getValueAt(selectedRow, 3).toString();

        JTextField nameField = new JTextField(name);
        JTextField phoneField = new JTextField(phone);
        JTextField emailField = new JTextField(email);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        panel.add(new JLabel("Name:"));
        panel.add(nameField);

        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);

        panel.add(new JLabel("Email:"));
        panel.add(emailField);

        int result = JOptionPane.showConfirmDialog(
                        this,
                        panel,
                        "Update Customer",
                        JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE
                );

        if(result == JOptionPane.OK_OPTION){
            String newName = nameField.getText();
            String newPhone = phoneField.getText();
            String newEmail = emailField.getText();

            if(newName.isEmpty()){
                JOptionPane.showMessageDialog(this, "Customer name is required.");

                return;
            }

            Customer customer = new Customer(
                            customerId,
                            newName,
                            newPhone,
                            newEmail
                    );

            CustomerDAO customerDAO = new CustomerDAO();

            if(customerDAO.updateCustomer(customer)){
                JOptionPane.showMessageDialog(this, "Customer updated successfully!");

                loadCustomers();
            }
            else{

                JOptionPane.showMessageDialog(this, "Failed to update customer.");
            }
        }
    }

    private void deleteSelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.");

            return;
        }

        int customerId = (int) tableModel.getValueAt(selectedRow, 0);
        String customerName = tableModel.getValueAt(selectedRow, 1).toString();

        int confirmation = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to delete \""
                                + customerName + "\"?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                );

        if(confirmation == JOptionPane.YES_OPTION){
            CustomerDAO customerDAO = new CustomerDAO();

            if(customerDAO.deleteCustomer(customerId)){
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!");

                loadCustomers();
            }
            else{
                JOptionPane.showMessageDialog(this, "Failed to delete customer.");
            }
        }
    }
}