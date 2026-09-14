import java.sql.*;
import java.util.Scanner;

public class ko
{
    public static void main(String[]args) throws ClassNotFoundException, SQLException
    {
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        String user = "root";
        String password = "123456789";


        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

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

            String query = "Insert INTO employees(id,name,job_title,salary) VALUES(?,?,?,?)";
            PreparedStatement pstmt = con.prepareStatement(query);
            Scanner sc = new Scanner(System.in);

            while(true)
            {
                System.out.print("Enter ID: ");
                String id = sc.nextLine();

                System.out.print("Enter Name: ");
                String name = sc.nextLine();

                System.out.print("Enter Job Title: ");
                String job_title = sc.nextLine();

                System.out.print("Enter Salary: ");
                double salary = sc.nextDouble();

                pstmt.setString(1,id);
                pstmt.setString(2,name);
                pstmt.setString(3,job_title);
                pstmt.setDouble(4,salary);
                pstmt.executeUpdate();

                System.out.println("Add more values Y/N");
                String decision = sc.nextLine();

                if(decision.toUpperCase().equals("N"))
                {
                    break;
                }
            }
            int[]  batchResult = pstmt.executeBatch();
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