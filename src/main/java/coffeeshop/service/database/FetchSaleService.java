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
     * fetch the sale records of a specific date
     * @param date
     * @return
     */
    List<SalesDAO> getSaleRecordsByDate(String date);

    /**
     * fetch the sale records of a specific bev on specific date
     * @param bev
     * @param date
     * @return
     */
    List<SalesDAO> getSaleRecordsByBevAndDate(String bev, Date date);

    /**
     * get total sales of the coffee house
     * @return
     */
    Double getAllSales();

    /**
     * get the sales of the coffee house on a specific date
     * @param date
     * @return
     */
    Double getSalesByDate(String date);

}
