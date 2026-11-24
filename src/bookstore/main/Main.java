package bookstore.main;
import bookstore.gui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        new LoginFrame();
    }
}


//package bookstore.main;
//import bookstore.database.DatabaseConnection;
//import java.sql.Connection;
//import java.sql.SQLException;
//
//public class Main {
//    public static void main(String[] args) {
//        try{
//            Connection connection = DatabaseConnection.getConnection();
//            System.out.println("Database connected successfully!");
//            connection.close();
//        }catch(SQLException e){
//            System.out.println("Database connection failed!");
//            e.printStackTrace();
//        }
//    }
//}