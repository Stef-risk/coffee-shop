package coffeeshop.merchandise.beverages;

public class Water extends Beverage{

    public Water() {
        description="water";
    }

    @Override
    public double cost() {
        return 0;
    }
}
