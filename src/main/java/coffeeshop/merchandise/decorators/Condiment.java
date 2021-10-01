package coffeeshop.merchandise.decorators;

import coffeeshop.merchandise.beverages.Beverage;

public abstract class Condiment extends Beverage {
    Beverage beverage;

    public Condiment(Beverage beverage) {
        this.beverage = beverage;
    }

    public abstract double getPrice();      //this is used to get the single condiment price

    public abstract String getDescription();
}
