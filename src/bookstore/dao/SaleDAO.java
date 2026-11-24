package bookstore.dao;
import bookstore.database.DatabaseConnection;
import bookstore.model.Sale;
import bookstore.model.SaleItem;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class SaleDAO {
    public int completeSale(Sale sale, List<SaleItem> items) {
        String saleSql = """
                INSERT INTO sales
                (customer_id, total)
                VALUES (?, ?)
                """;

        String itemSql = """
                INSERT INTO sale_items
                (sale_id, book_id, quantity, price)
                VALUES (?, ?, ?, ?)
                """;

        String stockSql = """
                UPDATE books
                SET quantity = quantity - ?
                WHERE book_id = ?
                AND quantity >= ?
                """;

        Connection connection = null;

        try{
            connection = DatabaseConnection.getConnection();

            // Start transaction
            connection.setAutoCommit(false);

            int saleId;

            // 1. Save the sale
            try(PreparedStatement saleStatement = connection.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)){
                saleStatement.setInt(1, sale.getCustomerId());
                saleStatement.setDouble(2, sale.getTotal());

                saleStatement.executeUpdate();

                ResultSet keys = saleStatement.getGeneratedKeys();

                if(!keys.next()) {
                    throw new SQLException("Could not get the sale ID.");
                }

                saleId = keys.getInt(1);
            }

            // 2. Save sale items and update stock
            try(
                    PreparedStatement itemStatement = connection.prepareStatement(itemSql);
                    PreparedStatement stockStatement = connection.prepareStatement(stockSql)
            ){
                for(SaleItem item : items){
                    // Save sale item
                    itemStatement.setInt(1, saleId);
                    itemStatement.setInt(2, item.getBookId());
                    itemStatement.setInt(3, item.getQuantity());
                    itemStatement.setDouble(4, item.getPrice());
                    itemStatement.executeUpdate();

                    // Reduce book stock
                    stockStatement.setInt(1, item.getQuantity());
                    stockStatement.setInt(2, item.getBookId());
                    stockStatement.setInt(3, item.getQuantity());

                    int updatedRows = stockStatement.executeUpdate();

                    if(updatedRows != 1){
                        throw new SQLException("Not enough stock for book ID: " + item.getBookId());
                    }
                }
            }

            // 3. Save everything
            connection.commit();

            return saleId;

        }catch(SQLException e){
            // Undo changes if something goes wrong
            if(connection != null){
                try {
                    connection.rollback();

                }catch(SQLException rollbackException){
                    rollbackException.printStackTrace();
                }
            }
            e.printStackTrace();

            return -1;

        }finally{
            if(connection != null){
                try{
                    connection.setAutoCommit(true);
                    connection.close();

                }catch(SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Object[]> getSalesHistory() {
        List<Object[]> sales = new ArrayList<>();

        String sql = """
            SELECT s.sale_id, c.name, s.sale_date, s.total
            FROM sales s
            LEFT JOIN customers c
            ON s.customer_id = c.customer_id
            ORDER BY s.sale_id DESC
            """;

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ){
            while(resultSet.next()){
                Object[] row = {
                        resultSet.getInt("sale_id"),
                        resultSet.getString("name"),
                        resultSet.getTimestamp("sale_date"),
                        resultSet.getDouble("total")
                };

                sales.add(row);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return sales;
    }

    public double getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total), 0) AS revenue FROM sales";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ){
            if(resultSet.next()){
                return resultSet.getDouble("revenue");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return 0.0;
    }

    public int getTotalSales(){
        String sql = "SELECT COUNT(*) AS total_sales FROM sales";

        try(
                Connection connection = DatabaseConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql);
                ResultSet resultSet = statement.executeQuery()
        ){
            if(resultSet.next()){
                return resultSet.getInt("total_sales");
            }

        }catch(SQLException e){
            e.printStackTrace();
        }

        return 0;
    }

    public List<Object[]> getSalesByDate(String date) {
        List<Object[]> sales = new ArrayList<>();

        String sql = "SELECT s.sale_id, c.name, s.sale_date, s.total " +
                "FROM sales s " +
                "LEFT JOIN customers c ON s.customer_id = c.customer_id " +
                "WHERE DATE(s.sale_date) = ? " +
                "ORDER BY s.sale_date DESC";

        try(Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)){
            pstmt.setString(1, date);

            ResultSet rs = pstmt.executeQuery();

            while(rs.next()){
                Object[] row = {
                        rs.getInt("sale_id"),
                        rs.getString("name"),
                        rs.getTimestamp("sale_date"),
                        rs.getDouble("total")
                };

                sales.add(row);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }

        return sales;
    }
}

