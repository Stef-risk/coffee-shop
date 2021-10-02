package coffeeshop.service.database.impl;

import coffeeshop.dao.SalesDAO;
import coffeeshop.service.database.WriteSaleService;
import coffeeshop.service.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Date;

@Component
public class WriteSaleServiceImpl implements WriteSaleService {
    @Override
    public void writeToDatabase(String baseBev, String condiments, double price) {
        SqlSession session= MyBatisUtil.getSqlSession();
        SalesDAO salesDAO=new SalesDAO(baseBev,condiments,new Date(),price);
        session.insert("SaleSpace.insertSale",salesDAO);
        session.commit();
        session.close();
    }
}
