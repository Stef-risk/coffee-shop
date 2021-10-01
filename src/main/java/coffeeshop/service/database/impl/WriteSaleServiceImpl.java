package coffeeshop.service.database.impl;

import coffeeshop.dao.SalesDAO;
import coffeeshop.service.database.WriteSaleService;
import coffeeshop.service.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;

public class WriteSaleServiceImpl implements WriteSaleService {
    @Override
    public void writeToDatabase(String baseBev, String condiments, double price) {
        SqlSession session= MyBatisUtil.getSqlSession();
        SalesDAO salesDAO=new SalesDAO(baseBev,condiments,new Date(),price);
        session.insert("SaleSpace.insert",salesDAO);
        session.commit();
        session.close();
    }
}
