package coffeeshop.database;

import coffeeshop.database.command.Command;
import coffeeshop.database.command.NothingCommand;

public class DataRemoteControl {
    Command command=new NothingCommand();

    public void setCommand(Command command) {
        this.command = command;
    }

    public void orderMade(String base,String condiments,double price) {
        command.execute(base, condiments, price);
    }
}
