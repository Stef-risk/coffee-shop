package coffeeshop.service.commandpattern.command;

import coffeeshop.dao.SalesMySql;

public class DatabaseLoggingCommand implements Command{

    SalesMySql salesMySql;

    public DatabaseLoggingCommand(SalesMySql salesMySql) {
        this.salesMySql = salesMySql;
    }

    @Override
    public void execute(String base, String condiments, double price) {
        salesMySql.writeToSales(base, condiments, price);
    }
}
