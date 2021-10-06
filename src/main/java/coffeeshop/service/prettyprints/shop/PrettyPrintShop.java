package coffeeshop.service.prettyprints.shop;

import coffeeshop.merchandise.beverages.Beverage;
import coffeeshop.service.factory.BeverageFactory;

import java.util.LinkedHashMap;

/**
 * Do the printing work
 */
public class PrettyPrintShop {

    /**
     * Singleton
     */
    private static volatile PrettyPrintShop prettyPrintShop;

    /**
     * Get all the available beverages from factory
     */
    BeverageFactory factory;

    private PrettyPrintShop(BeverageFactory factory) {
        this.factory = factory;
    }

    /**
     * Lazy instantiation singleton
     * @param factory
     * @return prettyPrint
     */
    public static PrettyPrintShop getInstance(BeverageFactory factory) {       //give the factory when creating
        if (prettyPrintShop == null) {
            synchronized (PrettyPrintShop.class) {
                prettyPrintShop = new PrettyPrintShop(factory);
            }
        }
        return prettyPrintShop;
    }

    /**
     * Print welcome, menu and instruction
     */
    public void openingPrint() {
        printWelcome();
        printMenu();
        printInstruction();
    }

    public void printWelcome() {
        System.out.println("----------------------------------------------");
        System.out.println("-------This is Stefan's Cafe,Welcome!---------");
        System.out.println("----------------------------------------------");
    }
    public void printMenu() {
        System.out.println("");
        System.out.println("-----------------------------------------------");
        System.out.println("Here's our menu:");
        System.out.println("Beverages:" + factory.getBeverages());
        System.out.println("Condiments:" + factory.getCondiments());
        System.out.println("-----------------------------------------------");
    }

    public void printInstruction() {
        System.out.println("------You can enter your base beverage and the condiments to make your drink.-------");
    }

    /**
     * Take the string of the description, put them into a LinkedHashMap and print without duplicates
     * @param bev
     */
    public void printBeverage(Beverage bev) {
        LinkedHashMap<String,Integer> des=new LinkedHashMap<>();
        for(String piece:bev.getDescription().split(",")) {
            if(des.containsKey(piece)) {
                des.computeIfPresent(piece,(k,v)->v+1);
            } else {
                des.put(piece,1);
            }
        }

        //Print the hashMap
        StringBuilder bevInfo= new StringBuilder();
        for(String piece:des.keySet()) {
            if(des.get(piece)==1) {
                bevInfo.append(piece);
                bevInfo.append(",");
            } else {
                bevInfo.append(piece).append(" x ").append(des.get(piece)).append(",");
            }
        }

        System.out.println(bevInfo.substring(0,bevInfo.length()-1));
    }

    /**
     * Print out the beverage's cost
     * @param bev
     */
    public void printCost(Beverage bev) {
        System.out.printf("Total cost : $%.2f%n",bev.cost());
    }

}
