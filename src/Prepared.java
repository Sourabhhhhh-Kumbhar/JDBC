import java.sql.*;

public class Prepared{
    public static void main(String[] args) {
        // --- Connection details ---
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        String user = "root";
        String password = "123456789";

        // Query has TWO placeholders (?) — one for name, one for job_title.
        // Both must be set before executeQuery(), or you get a SQLException.
        String query = "SELECT * FROM employees WHERE name = ? AND job_title = ?";

        // Load the MySQL JDBC driver class so DriverManager can find it.
        // Must be the driver class, not just any package under com.mysql.cj.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connected to the Database");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return; // no point continuing without the driver
        }

        // try-with-resources: Connection, PreparedStatement, and ResultSet
        // all get closed automatically when the block ends -- even if an
        // exception is thrown -- so you don't need manual close() calls.
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            System.out.println("Connection Established Successfully");

            // Fill in both placeholders. PreparedStatement also protects
            // against SQL injection since values are sent separately from
            // the query text, not concatenated into it.
            preparedStatement.setString(1, "Sourabh");
            preparedStatement.setString(2, "Software Engineer"); // <-- set your actual job title here

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String name = resultSet.getString("name");
                    String jobTitle = resultSet.getString("job_title");
                    int salary = resultSet.getInt("salary");

                    System.out.println("ID: " + id);
                    System.out.println("Name: " + name);
                    System.out.println("Job Title: " + jobTitle);
                    System.out.println("Salary: " + salary);
                }
            }

            System.out.println();
            System.out.println("Connection Closed Successfully");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}