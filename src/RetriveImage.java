import java.io.*;
import java.sql.*;

public class RetriveImage
{
    public static void main(String[] args)
    {
        String url = "jdbc:mysql://127.0.0.1:3306/mydatabase";
        String username = "root";
        String password = "123456789";
        String folder_path = "D:\\Snehal\\";
        String query = "SELECT image_data FROM image_table WHERE image_id = ?";

        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("Drivers Loaded Successfully");
        }
        catch(ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
            return;
        }
        try (Connection con = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = con.prepareStatement(query))
        {
            System.out.println("Connected to the Database");

            ps.setInt(1, 1);

            try (ResultSet rs = ps.executeQuery())
            {
                if(rs.next())
                {
                    byte[] image_data = rs.getBytes("image_data");

                    File folder = new File(folder_path);
                    if(!folder.exists())
                    {
                        if(!folder.mkdirs())
                        {
                            System.out.println("Could not create folder: " + folder_path);
                            return;
                        }
                    }

                    String image_path = folder_path + "extractedImage.jpg";

                    try (OutputStream out = new FileOutputStream(image_path))
                    {
                        out.write(image_data);
                    }

                    System.out.println("Image saved successfully: " + image_path);
                }
                else {
                    System.out.println("Image Not Found");
                }
            }
        }
        catch(SQLException e)
        {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
