package coffeeshop.database.command;

public interface Command {
    void execute(String base, String condiments, double price);
}
