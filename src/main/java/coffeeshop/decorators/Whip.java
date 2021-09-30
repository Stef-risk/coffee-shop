package coffeeshop.decorators;

import coffeeshop.beverages.Beverage;

public class Whip extends Condiment {
    public Whip(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double getPrice() {
        return .19;
    }

    @Override
    public double cost() {
        return this.getPrice()+ beverage.cost();
    }

    @Override
    public String getDescription() {
        return beverage.getDescription()+",Whip";
    }
}
