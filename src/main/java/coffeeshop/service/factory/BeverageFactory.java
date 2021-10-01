package coffeeshop.service.factory;

import coffeeshop.beverages.Beverage;

import java.util.ArrayList;
import java.util.HashMap;

public interface BeverageFactory {      //get all the beverages and condiments of a specific store

    //finally I decide to use hash map to store name and their price
    HashMap<String,Double> getBeverages();
    HashMap<String,Double> getCondiments();

    //to get the base beverage
    Beverage getBeverage(String bev);
    //decorate the base beverage with condiments
    Beverage addCondiments(ArrayList<String> con,Beverage baseBev);
}
