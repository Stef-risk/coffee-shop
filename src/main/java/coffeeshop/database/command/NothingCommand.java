package coffeeshop.database.command;

public class NothingCommand implements Command{
    @Override
    public void execute(String base, String condiments, double price) {
        ;
    }
}
