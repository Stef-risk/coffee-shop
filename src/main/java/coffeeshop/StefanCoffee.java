package coffeeshop;

import coffeeshop.merchandise.beverages.Beverage;
import coffeeshop.service.factory.BeverageFactory;
import coffeeshop.service.factory.impl.StefanBeverageFactory;
import coffeeshop.service.commandpattern.DataRemoteControl;
import coffeeshop.service.commandpattern.command.DatabaseLoggingCommand;
import coffeeshop.service.prettyprints.shop.PrettyPrintShop;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

@ComponentScan
public class StefanCoffee {
    public static void main(String[] args) {
        BeverageFactory factory = new StefanBeverageFactory();          //get the factory and prettyPrintObject
        PrettyPrintShop prettyPrintShop = PrettyPrintShop.getInstance(factory);

        //Get IOC Container
        ApplicationContext context=new AnnotationConfigApplicationContext(StefanCoffee.class);

        DataRemoteControl remoteControl = new DataRemoteControl();              //here we set the remote control
        DatabaseLoggingCommand databaseLoggingCommand=context.getBean(DatabaseLoggingCommand.class);
        remoteControl.setCommand(databaseLoggingCommand);

        prettyPrintShop.openingPrint();     //This will print out welcome,menu and instruction

        Scanner scanner = new Scanner(System.in);
        while (true) {          //The main function of the order system
            System.out.println("May I take your order please:(q to quit)");
            System.out.println("Your base beverage:");

            String bev = scanner.nextLine().strip();
            if (bev.equals("q")) break;
            Beverage beverage = factory.getBeverage(bev);       //get the base beverage first

            System.out.println("Any condiments:");
            String[] cons = scanner.nextLine().strip().split(" ");      //then get the condiments
            ArrayList<String> condiments = new ArrayList<>(Arrays.asList(cons));

            beverage = factory.addCondiments(condiments, beverage);

            System.out.println("Here's your order:");       //print cost and the beverage info
            prettyPrintShop.printBeverage(beverage);
            prettyPrintShop.printCost(beverage);

            //When an order is made and checked out
            remoteControl.orderMade(bev, condiments.toString(), beverage.cost());
        }
        scanner.close();
        System.out.println("Bye Bye !");
    }
}
