package coffeeshop.service.commandpattern.command;

import coffeeshop.service.database.WriteSaleService;
import org.springframework.beans.factory.annotation.Autowired;

public class DatabaseLoggingCommand implements Command{
    @Autowired
    WriteSaleService writeSaleService;

    @Override
    public void execute(String base, String condiments, double price) {
        writeSaleService.writeToDatabase(base, condiments, price);
    }
}
