package coffeeshop.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SalesMySql {
    //This DAO is used for getting connection of the database
    private static final String driver="com.mysql.cj.jdbc.Driver";
    private static final String url="jdbc:mysql://localhost:3306/coffeeshop";
    private static final String user="root";
    private static final String pass="elderflower7";

    public Connection connection;

    public SalesMySql() {
        try {
            Class.forName(driver);
            connection= DriverManager.getConnection(url,user,pass);

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean writeToSales(String base,String condiments,double price) {

        boolean res=false;
        try {
            PreparedStatement stmt=connection.prepareStatement(
                    "insert into sales(base_bev,condiments,sale_time,price)" +
                    " value(?,?,now(),?)");

            stmt.setString(1,base);
            stmt.setString(2,condiments);
            stmt.setString(3, String.valueOf(price));
            res=stmt.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {

            return res;
        }
    }
}
