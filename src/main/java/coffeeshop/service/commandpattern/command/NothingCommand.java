package coffeeshop.service.commandpattern.command;

public class NothingCommand implements Command{
    @Override
    public void execute(String base, String condiments, double price) {
        ;
    }
}
