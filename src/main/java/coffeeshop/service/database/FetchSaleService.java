package coffeeshop.service.database;

import coffeeshop.dao.SalesDAO;

import java.util.Date;
import java.util.List;

public interface FetchSaleService {
    /**
     * fetch all sale records from database
     * @return
     */
    List<SalesDAO> getAllSaleRecords();

    /**
     * fetch a single sale records from database by id
     * @param id
     * @return
     */
    SalesDAO getSaleRecordById(int id);

    /**
     * get total sales of the coffee house
     * @return
     */
    double getAllSales();

    /**
     * get the sales of the coffee house on a specific date
     * @param date
     * @return
     */
    double getSalesByDate(Date date);
}
