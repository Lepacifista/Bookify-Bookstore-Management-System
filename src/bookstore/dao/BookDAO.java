package bookstore.dao;
import bookstore.database.DatabaseConnection;
import bookstore.model.Book;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {
    // Get all books from the database
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();

        String sql = "SELECT * FROM books";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ){
            while(resultSet.next()){
                Book book = new Book();

                book.setBookId(resultSet.getInt("book_id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setCategory(resultSet.getString("category"));
                book.setPrice(resultSet.getDouble("price"));
                book.setQuantity(resultSet.getInt("quantity"));
                book.setSupplierId(resultSet.getInt("supplier_id"));

                books.add(book);
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }

        return books;
    }

    public boolean addBook(Book book) {
        String sql = """
            INSERT INTO books
            (title, author, category, price, quantity, supplier_id)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setDouble(4, book.getPrice());
            statement.setInt(5, book.getQuantity());
            statement.setInt(6, book.getSupplierId());

            statement.executeUpdate();

            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBook(Book book) {
        String sql = """
            UPDATE books
            SET title = ?, author = ?, category = ?,
                price = ?, quantity = ?, supplier_id = ?
            WHERE book_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getCategory());
            statement.setDouble(4, book.getPrice());
            statement.setInt(5, book.getQuantity());
            statement.setInt(6, book.getSupplierId());
            statement.setInt(7, book.getBookId());

            statement.executeUpdate();

            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";

        try (
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setInt(1, bookId);
            statement.executeUpdate();

            return true;
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public int getLowStockCount() {
        String sql = """
            SELECT COUNT(*) AS low_stock_count
            FROM books
            WHERE quantity <= 5
            """;

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ){
            if(resultSet.next()){
                return resultSet.getInt("low_stock_count");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return 0;
    }

    public List<Book> getLowStockBooks() {
        List<Book> books = new ArrayList<>();

        String sql = """
            SELECT * FROM books
            WHERE quantity <= 5
            ORDER BY quantity ASC
            """;

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ){
            while(resultSet.next()){
                Book book = new Book();

                book.setBookId(resultSet.getInt("book_id"));
                book.setTitle(resultSet.getString("title"));
                book.setAuthor(resultSet.getString("author"));
                book.setCategory(resultSet.getString("category"));
                book.setPrice(resultSet.getDouble("price"));
                book.setQuantity(resultSet.getInt("quantity"));
                book.setSupplierId(resultSet.getInt("supplier_id"));

                books.add(book);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return books;
    }
}