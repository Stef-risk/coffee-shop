package coffeeshop.service.database.impl;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class FetchSaleServiceImplTest {

    private FetchSaleServiceImpl fetchSaleService=new FetchSaleServiceImpl();

    @Test
    public void getAllSaleRecords() {
    }

    @Test
    public void getSaleRecordById() {
    }

    @Test
    public void getAllSales() {
        Double totalSale=fetchSaleService.getAllSales();
        System.out.println(totalSale);
    }

    @Test
    public void getSalesByDate() {
        Date date=new Date();
        String dateInfo=String.format("%tF%n",date);
        Double todaySale= fetchSaleService.getSalesByDate(dateInfo);
        System.out.println(todaySale);
    }

    @Test
    public void getSaleRecordsByBevAndDate() {
        System.out.println(fetchSaleService.getSaleRecordsByBevAndDate("Mocha",new Date()));
    }
}