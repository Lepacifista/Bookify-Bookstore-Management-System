package bookstore.dao;
import bookstore.database.DatabaseConnection;
import bookstore.model.Supplier;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDAO {
    public List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();

        String sql = "SELECT * FROM suppliers";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ){
            while(resultSet.next()){
                Supplier supplier = new Supplier();

                supplier.setSupplierId(resultSet.getInt("supplier_id"));
                supplier.setName(resultSet.getString("name"));
                supplier.setPhone(resultSet.getString("phone"));
                supplier.setEmail(resultSet.getString("email"));
                supplier.setAddress(resultSet.getString("address"));

                suppliers.add(supplier);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return suppliers;
    }

    public boolean addSupplier(Supplier supplier) {
        String sql = """
                INSERT INTO suppliers
                (name, phone, email, address)
                VALUES (?, ?, ?, ?)
                """;

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getPhone());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getAddress());

            statement.executeUpdate();

            return true;
        }catch(SQLException e){
            e.printStackTrace();

            return false;
        }
    }

    public boolean updateSupplier(Supplier supplier) {
        String sql = """
                UPDATE suppliers
                SET name = ?, phone = ?, email = ?, address = ?
                WHERE supplier_id = ?
                """;

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setString(1, supplier.getName());
            statement.setString(2, supplier.getPhone());
            statement.setString(3, supplier.getEmail());
            statement.setString(4, supplier.getAddress());
            statement.setInt(5, supplier.getSupplierId());

            statement.executeUpdate();

            return true;
        }catch(SQLException e){
            e.printStackTrace();

            return false;
        }
    }

    public boolean deleteSupplier(int supplierId) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ){
            statement.setInt(1, supplierId);
            statement.executeUpdate();

            return true;
        }catch(SQLException e){
            e.printStackTrace();

            return false;
        }
    }
}