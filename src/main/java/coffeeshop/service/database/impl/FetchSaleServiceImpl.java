package coffeeshop.service.database.impl;

import coffeeshop.dao.SalesDAO;
import coffeeshop.service.database.FetchSaleService;
import coffeeshop.service.util.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
    public List<SalesDAO> getSaleRecordsByDate(String date) {
        SqlSession session=MyBatisUtil.getSqlSession();
        List<SalesDAO> salesDAOList=session.selectList("SaleSpace.getSaleRecordsByDate",date);
        session.commit();
        session.close();
        return salesDAOList;
    }

    @Override
    public Double getAllSales() {
        SqlSession session=MyBatisUtil.getSqlSession();
        double sales=session.selectOne("SaleSpace.getAllSales");
        session.commit();
        session.close();
        return sales;
    }

    @Override
    public Double getSalesByDate(String date) {
        SqlSession session=MyBatisUtil.getSqlSession();
        Double sales=null;
        try{
            sales = session.selectOne("SaleSpace.getSalesByDate",date);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        session.commit();
        session.close();
        return sales;
    }
}
