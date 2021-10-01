package coffeeshop.database.DAO;

import java.sql.*;

public class WriteToDatabase {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con= DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/coffeeshop","root","elderflower7");

            Statement stmt=con.createStatement();
            stmt.execute("insert into sales(base_bev,condiments,sale_time,price) value('Mocha','Milk',now(),1.99)");

            PreparedStatement pstmt=con.prepareStatement("insert into sales(base_bev,condiments,sale_time,price)" +
                    " value(?,?,now(),?)");
            pstmt.setString(1,"Espresso");
            pstmt.setString(2,"Milk,Milk");
            pstmt.setString(3,String.valueOf(2.37));

            pstmt.execute();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

    }
}
