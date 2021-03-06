package Cart;

import Product.ProductModel;
import User.UserModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/")
public class CatalogServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        DataSource dataSource = (DataSource) req.getServletContext().getAttribute("datasource");

        UserModel user = (UserModel) req.getSession().getAttribute("user");
        if (user == null || user.getName()=="") {

            req.setAttribute("user_login", "1");
            req.getRequestDispatcher("/index.jsp");
        }

        String query = "SELECT * FROM product";

        List<ProductModel> productModels = new ArrayList<>();

        try(Connection connection = dataSource.getConnection(); PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                String s = resultSet.getString(2);
                int id = resultSet.getInt("id");
                String image = resultSet.getString(3);
                int price = resultSet.getInt(4);
                productModels.add(new ProductModel(id, s, image,price));
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
        req.setAttribute("products", productModels);
        req.getRequestDispatcher("/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}

