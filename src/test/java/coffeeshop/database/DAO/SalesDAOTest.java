package coffeeshop.database.DAO;

import coffeeshop.dao.SalesDAO;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.Date;
import java.util.List;

public class SalesDAOTest {

    private SalesDAO salesDAO = new SalesDAO();

    public SqlSession getSession() throws IOException {
        Reader reader = Resources.getResourceAsReader("SqlMapConfig.xml");
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
        return sqlSessionFactory.openSession();
    }

    @Test
    public void testMybatisInsert() throws IOException {
        SqlSession session = getSession();

        int id = session.insert("SaleSpace.insertSale", salesDAO);
        System.out.println("record inserted successfully\nnew sale id:" + id);
        session.commit();
        session.close();
    }

    @Test
    public void testMybatisSelect() throws IOException {
        SqlSession session = getSession();

        List<SalesDAO> salesDAOList = session.selectList("SaleSpace.getAll");
        System.out.println(salesDAOList);

        SalesDAO no10DAO = session.selectOne("SaleSpace.getById", 10);
        System.out.println(no10DAO);
    }

    @Test
    public void testMybatisUpdate() throws IOException {
        SqlSession session=getSession();

        SalesDAO salesDAO1=new SalesDAO(4,"Espresso","Milk,Milk",new Date(),2.37);
        int dao=session.update("SaleSpace.update",salesDAO1);
        session.commit();
        session.close();
    }

    @Test
    public void testMybatisDelete() throws IOException {
        SqlSession session=getSession();

        session.delete("SaleSpace.deleteById",13);
        session.commit();
        session.close();
    }

}