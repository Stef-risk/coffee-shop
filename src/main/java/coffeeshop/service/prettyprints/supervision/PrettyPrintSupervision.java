package coffeeshop.service.prettyprints.supervision;

import coffeeshop.dao.SalesDAO;

import java.util.List;

public class PrettyPrintSupervision {
    private static PrettyPrintSupervision prettyPrintSupervision;

    private PrettyPrintSupervision() {
    }

    public static PrettyPrintSupervision getInstance() {
        if(prettyPrintSupervision==null) {
            synchronized (PrettyPrintSupervision.class) {
                prettyPrintSupervision=new PrettyPrintSupervision();
            }
        }
        return prettyPrintSupervision;
    }

    /**
     * Opening print for the coffee house supervision system
     */
    public void openingPrint() {
        printWelcome();
        printMenu();
    }
    private void printWelcome() {
        System.out.println("Welcome to Stefan Coffee Supervision System !!!");
    }
    private void printMenu() {
        System.out.println("Here are your choices:\n1.fetch all sale records\n2.fetch today's sale records\n3.fetch all sales" +
                "\n4.fetch today's sales\n(q to quit)");
    }

    /**
     * Print out all the sale records
     * @param salesDAOList
     */
    public void saleRecordsPrint(List<SalesDAO> salesDAOList) {
        if(salesDAOList.size()==0) {
            System.out.println("No sale records today until now.");
            return;
        }
        System.out.println("Here are the sale records:");
        System.out.println(salesDAOList);
    }

    /**
     * Print out the sales amount
     * @param sales
     */
    public void salesPrint(Double sales) {
        if(sales==null) {
            System.out.println("No sale records today until now.");
            return;
        }
        System.out.println("Your sales : $"+sales);
    }
}
