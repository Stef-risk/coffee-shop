package coffeeshop;

import coffeeshop.service.database.FetchSaleService;
import coffeeshop.service.database.impl.FetchSaleServiceImpl;
import coffeeshop.service.prettyprints.supervision.PrettyPrintSupervision;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Scanner;

@ComponentScan
public class StefanCoffeeSupervision {
    public static void main(String[] args) {
        PrettyPrintSupervision prettyPrintSupervision= PrettyPrintSupervision.getInstance();

        prettyPrintSupervision.openingPrint();
        Scanner scanner=new Scanner(System.in);
        ApplicationContext context=new AnnotationConfigApplicationContext(StefanCoffeeSupervision.class);
        FetchSaleService fetchSaleService=context.getBean(FetchSaleServiceImpl.class);

        while(true) {
            int choice=scanner.nextInt();
            switch (choice) {
                case 1:
                case 2:
                case 3:
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}
