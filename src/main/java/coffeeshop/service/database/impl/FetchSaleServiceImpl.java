package coffeeshop.service.database.impl;

import coffeeshop.dao.SalesDAO;
import coffeeshop.service.database.FetchSaleService;
import coffeeshop.service.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;

import java.util.Date;
import java.util.List;

public class FetchSaleServiceImpl implements FetchSaleService {

    @Override
    public List<SalesDAO> getAllSaleRecords() {
        SqlSession session= MyBatisUtil.getSqlSession();
        List<SalesDAO> salesDAOList=session.selectList("SaleSpace.getAllSaleRecords");
        session.commit();
        session.close();
        return salesDAOList;
    }

    @Override
    public SalesDAO getSaleRecordById(int id) {
        SqlSession session=MyBatisUtil.getSqlSession();
        SalesDAO salesDAO=session.selectOne("SaleSpace.getSaleRecordById");
        session.commit();
        session.close();
        return salesDAO;
    }

    @Override
    public double getAllSales() {
        SqlSession session=MyBatisUtil.getSqlSession();
        double sales=session.selectOne("SaleSpace.getSales");
        session.commit();
        session.close();
        return sales;
    }

    @Override
    public double getSalesByDate(Date date) {
        SqlSession session=MyBatisUtil.getSqlSession();
        double sales=session.selectOne("SaleSpace.getSalesByDate",date);
        session.commit();
        session.close();
        return sales;
    }
}
