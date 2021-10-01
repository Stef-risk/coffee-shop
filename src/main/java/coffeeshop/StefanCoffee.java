package coffeeshop;

import coffeeshop.beverages.Beverage;
import coffeeshop.dao.SalesMySql;
import coffeeshop.service.factory.BeverageFactory;
import coffeeshop.service.factory.StefanBeverageFactory;
import coffeeshop.service.commandpattern.DataRemoteControl;
import coffeeshop.service.commandpattern.command.DatabaseLoggingCommand;
import coffeeshop.service.prettyprints.PrettyPrint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class StefanCoffee {
    public static void main(String[] args) {
        BeverageFactory factory = new StefanBeverageFactory();          //get the factory and prettyPrintObject
        PrettyPrint prettyPrint = PrettyPrint.getInstance(factory);

        DataRemoteControl dao = new DataRemoteControl();              //here we set the remote control
        dao.setCommand(new DatabaseLoggingCommand(new SalesMySql()));


        prettyPrint.openingPrint();     //This will print out welcome,menu and instruction

        Scanner scanner = new Scanner(System.in);
        while (true) {          //The main function of the order system
            System.out.println("May I take your order please:(q to quit)");

            System.out.println("Your base beverage:");

            String bev = scanner.nextLine().strip();
            if (bev.equals("q")) break;
            Beverage beverage = factory.getBeverage(bev);       //get the base beverage first


            System.out.println("Any condiments:");
            String[] cons = scanner.nextLine().strip().split(" ");      //then get the condiments
            ArrayList<String> condiments = new ArrayList<>();
            condiments.addAll(Arrays.asList(cons));

            beverage = factory.addCondiments(condiments, beverage);

            System.out.println("Here's your order:");       //print cost and the beverage info
            prettyPrint.printBeverage(beverage);
            prettyPrint.printCost(beverage);

            //When an order is made and checked out
            dao.orderMade(bev, condiments.toString(), beverage.cost());
        }

        scanner.close();
        System.out.println("Bye Bye !");
    }
}
