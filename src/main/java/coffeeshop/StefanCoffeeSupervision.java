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
        PrettyPrintSupervision prettyPrintSupervision = PrettyPrintSupervision.getInstance();

        prettyPrintSupervision.openingPrint();
        Scanner scanner = new Scanner(System.in);
        ApplicationContext context = new AnnotationConfigApplicationContext(StefanCoffeeSupervision.class);
        FetchSaleService fetchSaleService = context.getBean(FetchSaleServiceImpl.class);

        while (true) {
            String choice=scanner.nextLine().strip();
            if(choice.equals("q")) {
                break;
            }
            int function=0;
            try{
                function=Integer.parseInt(choice);
            } catch (Exception e) {
                e.printStackTrace();
            }
            switch (function) {
                case 1:
                    prettyPrintSupervision.saleRecordsPrint(fetchSaleService.getAllSaleRecords());
                    break;
                case 2:
//                    prettyPrintSupervision.salesPrint();
                case 3:
                    prettyPrintSupervision.salesPrint(fetchSaleService.getAllSales());
                    break;
                case 4:
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}
