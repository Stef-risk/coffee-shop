package coffeeshop.service.prettyprints;

import coffeeshop.merchandise.beverages.Beverage;
import coffeeshop.service.factory.BeverageFactory;

import java.util.LinkedHashMap;

public class PrettyPrint {

    //Use singleton on PrettyPrint here
    private static volatile PrettyPrint prettyPrint;

    //I want to use factory pattern here to get all the beverages we can make
    BeverageFactory factory;

    private PrettyPrint(BeverageFactory factory) {
        this.factory = factory;
    }

    public static PrettyPrint getInstance(BeverageFactory factory) {       //give the factory when creating
        if (prettyPrint == null) {
            synchronized (PrettyPrint.class) {
                prettyPrint = new PrettyPrint(factory);
            }
        }
        return prettyPrint;
    }

    public void openingPrint() {
        //put together welcome, menu and instruction
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

    public void printBeverage(Beverage bev) {
        //Take the string of the description, put them into a LinkedHashMap and print without duplicates
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

    public void printCost(Beverage bev) {
        System.out.printf("Total cost : $%.2f%n",bev.cost());
    }

}
