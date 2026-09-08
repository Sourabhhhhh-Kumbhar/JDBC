import java.sql.*;

public class UpdateData
{
    public static void main(String[] args) throws ClassNotFoundException, SQLException
    {
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        String user = "root";
        String password = "123456789";

        String query = "UPDATE employees\n" +
                "SET job_title = 'Full Stack Developer', salary = 70000 WHERE id = 5;\n";

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Driver Loaded Successfully");
        }

        catch(ClassNotFoundException e)
        {
            System.out.println(e.getMessage());

            System.out.println("Failed To Load Driver");
        }

        try
        {
            Connection con = DriverManager.getConnection(url, user, password);

            System.out.println("Connection Established!!!!");

            Statement stmt = con.createStatement();

            int rowsAffected = stmt.executeUpdate(query);

            if(rowsAffected > 0)
            {
                System.out.println("Update Successful!!!!" + rowsAffected + "Rows Affected Successfully!!!!");
            }
            else
            {
                System.out.println("Update Failed!!!");
            }

            stmt.close();
            con.close();
            System.out.println();
            System.out.println("Connection Closed!!!!");
        }

        catch(SQLException e)
        {
            System.out.println(e.getMessage());

            System.out.println("Failed To Connect!!!!");
        }
    }
}
