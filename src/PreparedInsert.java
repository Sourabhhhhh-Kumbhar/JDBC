import java.sql.*;

/*
 * PreparedInsert
 * ---------------
 * Demonstrates inserting a new row into the "employee" table using a
 * PreparedStatement. PreparedStatement is preferred over a plain Statement
 * because:
 *   1. It prevents SQL Injection (values are sent separately from the
 *      query text, not concatenated into it).
 *   2. It's faster for repeated queries since the DB can reuse the
 *      compiled query plan.
 *   3. It handles type conversion for you (setInt, setString, etc.)
 *      instead of manually building quoted strings.
 */
public class PreparedInsert {
    public static void main(String[] args) {

        // --- Database connection details ---
        // In a real project these would come from a config file / env
        // variables instead of being hardcoded, so credentials aren't
        // exposed in source control.
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        String user = "root";
        String password = "123456789";

        // SQL INSERT statement with placeholders (?) for each column
        // value. The actual values get bound in later via setX() calls
        // instead of being pasted directly into the string.
        String query = "INSERT INTO employees (id, name, job_title, salary) VALUES(?,?,?,?)";

        // Load the MySQL JDBC driver class so DriverManager knows how to
        // talk to a MySQL database. Must happen before getConnection().
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connected to the Database");
        } catch (ClassNotFoundException e) {
            // Thrown if the MySQL connector .jar isn't on the classpath.
            System.out.println(e.getMessage());
            return; // no point continuing without the driver
        }

        // try-with-resources: both Connection and PreparedStatement
        // implement AutoCloseable, so Java automatically calls their
        // close() methods when this block ends -- even if an exception
        // is thrown partway through. This avoids leaking DB connections.
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            System.out.println("Connection Established Successfully");

            // Bind values to the four placeholders in order (1-indexed,
            // not 0-indexed like arrays).
            preparedStatement.setInt(1, 4);                          // id
            preparedStatement.setString(2, "Pranita");                // name
            preparedStatement.setString(3, "Backend Developer");      // job_title
            preparedStatement.setInt(4, 100000);                      // salary

            // executeUpdate() is used (not executeQuery()) because this
            // is an INSERT/UPDATE/DELETE -- it returns the number of
            // rows affected instead of a ResultSet.
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Data Inserted Successfully");
            } else {
                System.out.println("Data Insert Failed");
            }

            System.out.println();
            System.out.println("Connection Closed Successfully");

        } catch (SQLException e) {
            // Covers connection failures, duplicate primary key errors
            // (e.g. id = 4 already exists), wrong column names, etc.
            System.out.println(e.getMessage());
        }
    }
}