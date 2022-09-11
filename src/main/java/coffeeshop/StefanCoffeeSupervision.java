package coffeeshop;

import coffeeshop.service.database.FetchSaleService;
import coffeeshop.service.database.impl.FetchSaleServiceImpl;
import coffeeshop.service.prettyprints.supervision.PrettyPrintSupervision;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.util.Date;
import java.util.Scanner;

@ComponentScan
public class StefanCoffeeSupervision {
    public static void main(String[] args) {
        PrettyPrintSupervision prettyPrintSupervision = PrettyPrintSupervision.getInstance();

        prettyPrintSupervision.openingPrint();
        Scanner scanner = new Scanner(System.in);
        ApplicationContext context = new AnnotationConfigApplicationContext(StefanCoffeeSupervision.class);
        FetchSaleService fetchSaleService = context.getBean(FetchSaleServiceImpl.class);

        Date date=new Date();
        String today=String.format("%tF%n",date);

        while (true) {
            String choice=scanner.nextLine().trim();
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
                    prettyPrintSupervision.saleRecordsPrint(fetchSaleService.getSaleRecordsByDate(today));
                    break;
                case 3:
                    prettyPrintSupervision.salesPrint(fetchSaleService.getAllSales());
                    break;
                case 4:
                    prettyPrintSupervision.salesPrint(fetchSaleService.getSalesByDate(today));
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
    }
}
