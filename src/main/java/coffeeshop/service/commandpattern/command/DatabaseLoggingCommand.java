package coffeeshop.service.commandpattern.command;

import coffeeshop.service.database.WriteSaleService;
import coffeeshop.service.database.impl.WriteSaleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseLoggingCommand implements Command{
    @Autowired
    WriteSaleServiceImpl writeSaleService;

    @Override
    public void execute(String base, String condiments, double price) {
        writeSaleService.writeToDatabase(base, condiments, price);
    }
}
