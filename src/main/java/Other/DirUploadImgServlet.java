package Other;

import User.UserModel;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.sql.DataSource;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

@WebServlet(urlPatterns = "/upload")
@MultipartConfig
public class DirUploadImgServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        DataSource dataSource = (DataSource) request.getServletContext().getAttribute("datasource");
        UserModel user = (UserModel) request.getSession().getAttribute("user");

        String uploadDir = "/home/anna/JavaServletExample/image/";
        Part file = request.getPart("file");

        String images =
                UUID.randomUUID().toString() +
                        "-" +
                        file.getSubmittedFileName();

        IOUtils.copyLarge(
                file.getInputStream(),
                new FileOutputStream(uploadDir +
                        images
                )
        );
        request.setAttribute("images", images);
        System.out.println(images);
        System.out.println(user.getEmail());
        String sql = "update user set image = ? where email = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, images);
            statement.setString(2, user.getEmail());
            System.out.println(statement.executeUpdate());

        } catch (SQLException e) {
            System.out.println(e);
        }
        user.setImage(images);
        request.getSession().setAttribute("user", user);
        response.sendRedirect("/profile");
    }
}