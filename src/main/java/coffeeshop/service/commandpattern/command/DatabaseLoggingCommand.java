package coffeeshop.service.commandpattern.command;

import coffeeshop.service.database.WriteSaleService;

public class DatabaseLoggingCommand implements Command{
    WriteSaleService writeSaleService;

    @Override
    public void execute(String base, String condiments, double price) {
        writeSaleService.writeToDatabase(base, condiments, price);
    }
}
