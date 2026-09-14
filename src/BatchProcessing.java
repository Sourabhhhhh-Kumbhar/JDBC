import java.sql.*;
import java.util.Scanner;

public class BatchProcessing
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
            con.setAutoCommit(false);

            Statement stmt = con.createStatement();
            stmt.addBatch("INSERT INTO employees(id,name, job_title, salary) VALUES(8,'Kranti', 'Executive', 85400)");
            stmt.addBatch("INSERT INTO employees(id,name, job_title, salary) VALUES(9,'Ritika', 'HR Traine', 83400)");
            stmt.addBatch("INSERT INTO employees(id,name, job_title, salary) VALUES(10,'Subhdra', 'Executive Manager', 95400)");
            int[] batchResult = stmt.executeBatch();

            con.commit();
            System.out.println("Batch Process Completed");

        }
        catch(SQLException e)
        {
            System.out.println("Connection Failed" + e.getMessage());
            return;
        }
    }
}