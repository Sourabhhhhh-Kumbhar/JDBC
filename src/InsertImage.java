import javax.xml.transform.Result;
import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class InsertImage
{
    public static void main(String[] args) throws ClassNotFoundException
    {
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        String username = "root";
        String password = "123456789";
        String image_path = "C:/Users/soura/Downloads/wallhaven-lydkg2_1920x1080.png";
        String query = "INSERT INTO image_table(image_data) VALUES(?)";

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Drivers Loaded Successfully");
        }
        catch(ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        try
        {
            Connection con = DriverManager.getConnection(url,username,password);

            System.out.println("Connected to the Database");

            FileInputStream fileInputStream = new FileInputStream(image_path);

            byte[] imageData = new byte[fileInputStream.available()];

            fileInputStream.read(imageData);

            PreparedStatement preparedStatement = con.prepareStatement(query);

            preparedStatement.setBytes(1, imageData);

            int affectedRows = preparedStatement.executeUpdate();

            if(affectedRows > 0)
            {
                System.out.println("Image Inserted Successfully");
            }
            else
            {
                System.out.println("Failed to insert image");
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
            return;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}