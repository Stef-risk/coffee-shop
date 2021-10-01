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
        return null;
    }

    @Override
    public double getAllSales() {
        return 0;
    }

    @Override
    public double getSalesByDate(Date date) {
        return 0;
    }
}
