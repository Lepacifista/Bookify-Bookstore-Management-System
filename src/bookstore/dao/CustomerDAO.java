package bookstore.dao;
import bookstore.database.DatabaseConnection;
import bookstore.model.Customer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT * FROM customers";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ){
            while(resultSet.next()){
                Customer customer = new Customer();

                customer.setCustomerId(resultSet.getInt("customer_id"));
                customer.setName(resultSet.getString("name"));
                customer.setPhone(resultSet.getString("phone"));
                customer.setEmail(resultSet.getString("email"));

                customers.add(customer);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return customers;
    }

    public boolean addCustomer(Customer customer) {
        String sql = """
                INSERT INTO customers
                (name, phone, email)
                VALUES (?, ?, ?)
                """;

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getPhone());
            statement.setString(3, customer.getEmail());

            statement.executeUpdate();

            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCustomer(Customer customer) {
        String sql = """
                UPDATE customers
                SET name = ?, phone = ?, email = ?
                WHERE customer_id = ?
                """;

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, customer.getName());
            statement.setString(2, customer.getPhone());
            statement.setString(3, customer.getEmail());
            statement.setInt(4, customer.getCustomerId());

            statement.executeUpdate();

            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCustomer(int customerId) {
        String sql = "DELETE FROM customers WHERE customer_id = ?";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setInt(1, customerId);

            statement.executeUpdate();

            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}