package coffeeshop.dao;

import java.io.Serializable;
import java.util.Date;

public class SalesDAO implements Serializable {
    private int id;
    private String baseBev;
    private String condiments;
    private Date saleTime;
    private double price;

    public SalesDAO() {
        baseBev="Mocha";
        condiments="Milk";
        saleTime=new Date();
        price=1.99;
    }

    public SalesDAO(int id, String baseBev, String condiments, Date saleTime, double price) {
        this.id = id;
        this.baseBev = baseBev;
        this.condiments = condiments;
        this.saleTime = saleTime;
        this.price = price;
    }

    public SalesDAO(String baseBev, String condiments, Date saleTime, double price) {
        this.baseBev = baseBev;
        this.condiments = condiments;
        this.saleTime = saleTime;
        this.price = price;
    }

    public SalesDAO(String baseBev, String condiments, double price) {
        this.baseBev = baseBev;
        this.condiments = condiments;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseBev() {
        return baseBev;
    }

    public void setBaseBev(String baseBev) {
        this.baseBev = baseBev;
    }

    public String getCondiments() {
        return condiments;
    }

    public void setCondiments(String condiments) {
        this.condiments = condiments;
    }

    public Date getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(Date saleTime) {
        this.saleTime = saleTime;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "\nSalesDAO{" +
                "id=" + id +
                ", baseBev='" + baseBev + '\'' +
                ", condiments='" + condiments + '\'' +
                ", SaleTime=" + saleTime +
                ", price=" + price +
                '}';
    }
}
