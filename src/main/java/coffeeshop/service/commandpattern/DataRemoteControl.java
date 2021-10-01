package coffeeshop.service.commandpattern;

import coffeeshop.service.commandpattern.command.Command;
import coffeeshop.service.commandpattern.command.NothingCommand;

public class DataRemoteControl {
    Command command=new NothingCommand();

    public void setCommand(Command command) {
        this.command = command;
    }

    public void orderMade(String base,String condiments,double price) {
        command.execute(base, condiments, price);
    }
}
