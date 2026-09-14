import java.sql.*;
import java.util.Scanner;

public class BatchProcessingg
{
    public static void main(String[]args) throws ClassNotFoundException, SQLException
    {
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        String user = "root";
        String password = "123456789";


        try
        {
            Class.forName("com.mysql.cj.mysql.jdbc.Driver");

            System.out.println("Drivers Loaded Successfully");
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("Failed to load Driver" + e.getMessage());
        }

        try
        {
            Connection con = DriverManager.getConnection(url,user,password);

            System.out.println("Connected To Database");

        }
        catch(SQLException e)
        {
            System.out.println("Connection Failed" + e.getMessage());
            return;
        }
    }
}