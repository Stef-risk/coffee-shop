package coffeeshop.decorators;

import coffeeshop.beverages.Beverage;

public class Milk extends Condiment {

    public Milk(Beverage beverage) {
        super(beverage);
    }

    @Override
    public double getPrice() {
        return .19;
    }

    @Override
    public String getDescription() {            //make description abstract so that all condiments should also override the des
        return beverage.getDescription()+",Milk";
    }

    @Override
    public double cost() {
        return this.getPrice()+ beverage.cost();
    }
}
