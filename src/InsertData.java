import java.sql.*;

public class InsertData
{
    public static void main(String[] args) throws ClassNotFoundException
    {
        //Database URl
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        //Database Credentials
        String username = "root";
        String password = "123456789";
        //A Query
        String query = "INSERT INTO employees(id, name, job_title, salary) VALUES(5, 'Bella', 'React Developer', 40000)";

        //Loading the Drivers
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Drivers Loaded Successfully!!!"); // if try block exceute successfully then drivers are loaded
        }
        catch(ClassNotFoundException e)
        {
            System.out.println(e.getMessage());

            System.out.println("Fail to load Driver"); // if not then this will tell that its failed.
        }

        //Establishing the Connection
        try
        {
            Connection con = DriverManager.getConnection(url, username, password);

            System.out.println("Connection Established Successfully!!!");

            //Creating Statement to execute sql query
            Statement stmt = con.createStatement();
            int rowsaffected = stmt.executeUpdate(query); //use executeQuery if u want to retrive data use executeupdate if u want to insert data

            if(rowsaffected > 0)
            {
                System.out.println("Insert Successful!!!" + rowsaffected + " row(s) affected");
            }else{
                System.out.println("Insert Failed!!!");
            }


            stmt.close();
            con.close();
            System.out.println();
            System.out.println("Connection Closed Successfully!!!");

        }
        catch (SQLException e)
        {
            System.out.println(e.getMessage());

            System.out.println("Connection Failed!!!");
        }
    }
}
