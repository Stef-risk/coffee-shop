package coffeeshop.service.factory;

import coffeeshop.beverages.Beverage;
import coffeeshop.beverages.Espresso;
import coffeeshop.beverages.Mocha;
import coffeeshop.beverages.Water;
import coffeeshop.decorators.Milk;
import coffeeshop.decorators.Whip;

import java.util.ArrayList;
import java.util.HashMap;

public class StefanBeverageFactory implements BeverageFactory {
    @Override
    public HashMap<String, Double> getBeverages() {
        HashMap<String, Double> steBeverages = new HashMap<>();

        steBeverages.put("Mocha", new Mocha().cost());
        steBeverages.put("Espresso", new Espresso().cost());

        return steBeverages;
    }

    @Override
    public HashMap<String, Double> getCondiments() {
        HashMap<String, Double> steCondiments = new HashMap<>();

        steCondiments.put("Milk", new Milk(null).getPrice());
        steCondiments.put("Whip", new Whip(null).getPrice());

        return steCondiments;
    }

    @Override
    public Beverage getBeverage(String bev) {
        switch (bev) {
            case "Mocha":
                return new Mocha();
            case "Espresso":
                return new Espresso();
        }

        return new Water();
    }

    @Override
    public Beverage addCondiments(ArrayList<String> condiments, Beverage baseBev) {

        Beverage beverage = baseBev;
        for (String condiment : condiments) {
            switch (condiment) {
                case "Milk":
                    beverage = new Milk(beverage);
                    continue;
                case "Whip":
                    beverage = new Whip(beverage);
                    continue;
            }
        }

        return beverage;
    }
}
