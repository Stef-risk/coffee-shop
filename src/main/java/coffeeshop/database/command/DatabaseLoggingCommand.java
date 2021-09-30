package coffeeshop.database.command;

import coffeeshop.database.DAO.SalesDAO;

public class DatabaseLoggingCommand implements Command{

    SalesDAO salesDAO;

    public DatabaseLoggingCommand(SalesDAO salesDAO) {
        this.salesDAO = salesDAO;
    }

    @Override
    public void execute(String base, String condiments, double price) {
        salesDAO.writeToSales(base, condiments, price);
    }
}
