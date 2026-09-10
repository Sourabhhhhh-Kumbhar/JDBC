import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Timestamp;

/*
 * HotelReservationSystem
 * -----------------------
 * A simple console-based CRUD app for managing hotel room reservations
 * using a MySQL database (via JDBC).
 *
 * Menu-driven: the user picks an option, and the program talks to the
 * "reservations" table in the "hotel_db" database to create, read,
 * update, or delete reservation records.
 */
public class HotelReservationSystem {

    // --- Database connection details ---
    // NOTE: In a real-world app, never hardcode credentials in source code.
    // They'd normally come from a config file or environment variable so
    // they aren't exposed if the code is shared/pushed to GitHub, etc.
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";
    private static final String username = "root";
    private static final String password = "123456789";

    public static void main(String[] args) {

        // Load the MySQL JDBC driver so DriverManager knows how to
        // connect to a MySQL database. Required before getConnection() works.
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found: " + e.getMessage());
            return; // no point continuing without the driver
        }

        // Scanner is created ONCE outside the loop (not every iteration)
        // to avoid wasting resources and to keep input state consistent.
        Scanner scanner = new Scanner(System.in);
        Connection connection = null;

        try {
            // Open one connection and reuse it for the whole session,
            // instead of opening/closing a new connection per action.
            connection = DriverManager.getConnection(url, username, password);

            while (true) {
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservations");
                System.out.println("5. Delete Reservations");
                System.out.println("0. Exit");
                System.out.print("Choose an option: ");

                // If the user types something that isn't a number,
                // nextInt() throws an exception -- worth knowing about,
                // though not fixed here to keep the structure the same.
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        reserveRoom(connection, scanner);
                        break;
                    case 2:
                        viewReservations(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scanner);
                        break;
                    case 4:
                        updateReservation(connection, scanner);
                        break;
                    case 5:
                        deleteReservation(connection, scanner);
                        break;
                    case 0:
                        exit();
                        return; // connection gets closed in the finally block below
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        } catch (InterruptedException e) {
            // Thrown by Thread.sleep() inside exit(); restore the
            // interrupt flag instead of swallowing it silently.
            Thread.currentThread().interrupt();
        } finally {
            // Always close the connection and scanner when the program ends,
            // whether it exited normally or via an exception.
            // Leaving a Connection open leaks a DB resource for as long
            // as the JVM runs.
            scanner.close();
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }

    /*
     * Inserts a new reservation into the database.
     * Uses PreparedStatement instead of string concatenation to prevent
     * SQL Injection -- e.g. a guest name containing a quote (like "O'Brien")
     * or malicious SQL text would break/exploit a plain Statement query.
     */
    private static void reserveRoom(Connection connection, Scanner scanner) {
        try {
            // Clear any leftover newline from a previous nextInt() call,
            // then read the full line so names with spaces work correctly.
            if (scanner.hasNextLine()) scanner.nextLine();

            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room number: ");
            int roomNumber = scanner.nextInt();

            System.out.print("Enter contact number: ");
            String contactNumber = scanner.next();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number) " +
                    "VALUES (?, ?, ?)";

            // try-with-resources: the PreparedStatement auto-closes even
            // if an exception happens, so we don't leak DB resources.
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, guestName);
                statement.setInt(2, roomNumber);
                statement.setString(3, contactNumber);

                int affectedRows = statement.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Reservation successful!");
                } else {
                    System.out.println("Reservation failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Reads and prints every reservation in the table in a simple
     * text-based table format.
     */
    private static void viewReservations(Connection connection) throws SQLException {
        String sql = "SELECT reservation_id, guest_name, room_number, contact_number, reservation_date FROM reservations";

        // Statement (not PreparedStatement) is fine here since there are
        // no user-supplied values in this query -- nothing to inject.
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            System.out.println("Current Reservations:");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
            System.out.println("| Reservation ID | Guest           | Room Number   | Contact Number       | Reservation Date        |");
            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");

            while (resultSet.next()) {
                int reservationId = resultSet.getInt("reservation_id");
                String guestName = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contactNumber = resultSet.getString("contact_number");

                // getTimestamp() can return null if the column is NULL in
                // the DB -- calling .toString() on null would throw an NPE,
                // so we guard against that here.
                Timestamp ts = resultSet.getTimestamp("reservation_date");
                String reservationDate = (ts != null) ? ts.toString() : "N/A";

                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s   |%n",
                        reservationId, guestName, roomNumber, contactNumber, reservationDate);
            }

            System.out.println("+----------------+-----------------+---------------+----------------------+-------------------------+");
        }
    }

    /*
     * Looks up a room number given a reservation ID and guest name.
     * Both are treated as "credentials" here (a lightweight identity check),
     * so we require both to match before revealing the room number.
     */
    private static void getRoomNumber(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = scanner.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scanner.next();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = ? AND guest_name = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, reservationId);
                statement.setString(2, guestName);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        int roomNumber = resultSet.getInt("room_number");
                        System.out.println("Room number for Reservation ID " + reservationId +
                                " and Guest " + guestName + " is: " + roomNumber);
                    } else {
                        System.out.println("Reservation not found for the given ID and guest name.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Updates an existing reservation's guest name, room number, and
     * contact number, after confirming the reservation ID actually exists.
     */
    private static void updateReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to update: ");
            int reservationId = scanner.nextInt();
            scanner.nextLine(); // Consume the leftover newline character

            // Check existence first so we can give a clear message instead
            // of silently updating 0 rows.
            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String newGuestName = scanner.nextLine();
            System.out.print("Enter new room number: ");
            int newRoomNumber = scanner.nextInt();
            System.out.print("Enter new contact number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE reservations SET guest_name = ?, room_number = ?, " +
                    "contact_number = ? WHERE reservation_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, newGuestName);
                statement.setInt(2, newRoomNumber);
                statement.setString(3, newContactNumber);
                statement.setInt(4, reservationId);

                int affectedRows = statement.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Reservation updated successfully!");
                } else {
                    System.out.println("Reservation update failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Deletes a reservation by ID, after confirming it exists.
     */
    private static void deleteReservation(Connection connection, Scanner scanner) {
        try {
            System.out.print("Enter reservation ID to delete: ");
            int reservationId = scanner.nextInt();

            if (!reservationExists(connection, reservationId)) {
                System.out.println("Reservation not found for the given ID.");
                return;
            }

            String sql = "DELETE FROM reservations WHERE reservation_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, reservationId);

                int affectedRows = statement.executeUpdate();

                if (affectedRows > 0) {
                    System.out.println("Reservation deleted successfully!");
                } else {
                    System.out.println("Reservation deletion failed.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
     * Helper method used by update/delete to check whether a given
     * reservation ID exists in the table before acting on it.
     * Avoids duplicating the "does this ID exist?" query logic in
     * multiple places.
     */
    private static boolean reservationExists(Connection connection, int reservationId) {
        try {
            String sql = "SELECT reservation_id FROM reservations WHERE reservation_id = ?";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, reservationId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next(); // true if a row was found
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // treat DB errors as "not found" so callers don't crash
        }
    }

    /*
     * Small farewell animation when the user exits the program.
     * Purely cosmetic -- doesn't affect functionality.
     */
    public static void exit() throws InterruptedException {
        System.out.print("Exiting System");
        int i = 5;
        while (i != 0) {
            System.out.print(".");
            Thread.sleep(1000);
            i--;
        }
        System.out.println();
        System.out.println("ThankYou For Using Hotel Reservation System!!!");
    }
}