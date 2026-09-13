import java.sql.*;
import java.io.*;
import javax.xml.transform.Result;
import java.util.Scanner;

public class Transaction
{
    public static void main(String[]args) throws ClassNotFoundException, SQLException
    {
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        String user = "root";
        String password = "123456789";
        String withdrawQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
        String depositQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Drivers Loaded Successfully");

        }
        catch (ClassNotFoundException e)
        {
            System.out.println("Failed to load Driver " + e.getMessage());
        }

        try
        {
         Connection con = DriverManager.getConnection(url,user,password);

         System.out.println("Connected to database successfully");
         con.setAutoCommit(false);

         try {
             PreparedStatement withdrawStatement = con.prepareStatement(withdrawQuery);
             PreparedStatement depositStatement = con.prepareStatement(depositQuery);

             withdrawStatement.setDouble(1, 800.00);
             withdrawStatement.setString(2, "account123");

             depositStatement.setDouble(1, 1000.00);
             depositStatement.setString(2, "account456");

             withdrawStatement.executeUpdate();
             depositStatement.executeUpdate();
             con.commit();

             System.out.println("Transaction Successful");
         }
         catch (SQLException e)
         {
             con.rollback();
         }

        }
        catch (SQLException e)
        {
            System.out.println("Failed to connect to database " + e.getMessage());
        }

    }
}