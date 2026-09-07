import java.sql.*;

public class RetriveData
{
    public static void main(String[] args) throws ClassNotFoundException
    {
        //Database URl
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        //Database Credentials
        String username = "root";
        String password = "123456789";
        //A Query
        String query = "select * from employees";

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

            // ADD THESE TWO LINES
            //System.out.println("URL Java is using: " + con.getMetaData().getURL());
            //System.out.println("Database Java is using: " + con.getCatalog());

            //Creating Statement to execute sql query
            Statement stmt = con.createStatement();

            //Using ResultSet to store the Result got from the execution of the query
            ResultSet rs = stmt.executeQuery(query);

            //To get the data that is stored in rs we use while loop
            while(rs.next())
            {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String job_title = rs.getString("job_title");
                double salary = rs.getDouble("salary");
                //Now the data is stored in the java variables

                //Just for formating so that the result looks good
                System.out.println();
                System.out.println("==========================");

                //Now just Print the data
                System.out.println("ID: " + id);
                System.out.println("Name: " + name);
                System.out.println("Job Title: " + job_title);
                System.out.println("Salary: " + salary);
            }

            rs.close();
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
