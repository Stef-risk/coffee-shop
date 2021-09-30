package coffeeshop.beverages;

public class Mocha extends Beverage{

    public Mocha() {
        this.description="Mocha";
    }

    @Override
    public double cost() {
        return 1.89;
    }
}
