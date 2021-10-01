package coffeeshop.service.commandpattern.command;

public interface Command {
    void execute(String base, String condiments, double price);
}
