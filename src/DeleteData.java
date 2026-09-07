import java.sql.*;

public class DeleteData
{
    public static void main(String[] args) throws ClassNotFoundException, SQLException
    {
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        String user = "root";
        String password = "123456789";

        String query = "DELETE FROM employees WHERE id = 4";

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
                System.out.println("Row Deleted!!!!" + rowsAffected + "Rows Affected Successfully!!!!");
            }
            else
            {
                System.out.println("Delete Failed!!!");
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
