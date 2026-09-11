import java.sql.*;
import java.util.Scanner;

/*
 * PreparedInsert
 * ---------------
 * Takes employee details from the user via console input, then inserts
 * them into the "employees" table using a PreparedStatement.
 * PreparedStatement is preferred over a plain Statement because:
 *   1. It prevents SQL Injection (values are sent separately from the
 *      query text, not concatenated into it).
 *   2. It's faster for repeated queries since the DB can reuse the
 *      compiled query plan.
 *   3. It handles type conversion for you (setInt, setString, etc.)
 *      instead of manually building quoted strings.
 */
public class PreparedScanner {
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

        // Declared OUTSIDE any try block so they're visible everywhere
        // in main() -- these will hold the user's input.
        int id;
        String name;
        String job_title;
        double salary;

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

        // Reading user input is separate from the driver-loading logic,
        // so it's pulled out into its own block for clarity. Scanner
        // doesn't throw a checked exception, so no try/catch needed here.
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter ID: ");
        id = sc.nextInt();
        sc.nextLine(); // consume leftover newline so nextLine() below works correctly

        System.out.print("Enter Name: ");
        name = sc.nextLine();

        System.out.print("Enter Job Title: ");
        job_title = sc.nextLine();

        System.out.print("Enter Salary: ");
        salary = sc.nextDouble();

        // try-with-resources: both Connection and PreparedStatement
        // implement AutoCloseable, so Java automatically calls their
        // close() methods when this block ends -- even if an exception
        // is thrown partway through. This avoids leaking DB connections.
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = con.prepareStatement(query)) {

            System.out.println("Connection Established Successfully");

            // Bind values to the four placeholders in order (1-indexed,
            // not 0-indexed like arrays).
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, job_title);
            preparedStatement.setDouble(4, salary); // salary is a double, so setDouble (not setInt)

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
            // (e.g. same id already exists), wrong column names, etc.
            System.out.println(e.getMessage());
        }
    }
}