package Util;

import dashpointrewardsystem.RedeemableItem;

import java.util.ArrayList;
import java.util.Scanner;

public class GeneralFunction {

    public static class color {

        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";
    }

    public static void displayWelcomeScreen() {
        String[] dash = (""
                + "\n" + color.ANSI_RED
                + "██████╗░░█████╗░░██████╗██╗░░██╗\n" + color.ANSI_GREEN
                + "██╔══██╗██╔══██╗██╔════╝██║░░██║\n" + color.ANSI_YELLOW
                + "██║░░██║███████║╚█████╗░███████║\n" + color.ANSI_BLUE
                + "██║░░██║██╔══██║░╚═══██╗██╔══██║\n" + color.ANSI_PURPLE
                + "██████╔╝██║░░██║██████╔╝██║░░██║\n" + color.ANSI_CYAN
                + "╚═════╝░╚═╝░░╚═╝╚═════╝░╚═╝░░╚═╝" + color.ANSI_RESET).split("\n");

        for (String text : dash) {
            System.out.println(text);
            sleep(.25);
        }
    }

    public static void displayItemAllData(ArrayList<RedeemableItem> itemData) {
        String[] headings = new String[]{"ItemCode", "Name", "Category", "Points Required", "Quantity", "Availability", "Min Tier"};
        System.out.println();
        for (int i = 0; i < 125; i++) {
            System.out.print("_");
        }
        System.out.print("\n| ");
        for (int i = 0; i < headings.length; i++) {
            if (i != 1) {
                System.out.printf("%-15s ", headings[i]);
            } else {
                System.out.printf("%-25s ", headings[i]);
            }
        }
        System.out.print("|");
        System.out.println();
        System.out.print("|");
        for (int i = 0; i < 123; i++) {
            System.out.print("-");
        }
        System.out.print("|");
        System.out.println();

        for (RedeemableItem item : itemData) {
            System.out.print("| ");
            System.out.printf("%-15s ", item.getItemCode());
            System.out.printf("%-25s ", item.getRewardName());
            System.out.printf("%-15s ", item.getItemCategory());
            System.out.printf("%-15s ", item.getPtRequired());
            System.out.printf("%-15s ", item.getStockAmt());
            System.out.printf("%-15s ", item.getAvailability());
            System.out.printf("%-15s ", item.getMinimumTier());

            System.out.print("|");
            System.out.println();
        }
        System.out.print("|");
        for (int i = 0; i < 123; i++) {
            System.out.print("_");
        }
        System.out.print("|");
        System.out.println("\n");
    }

    public static void displayItemQuantityAndRelatedData(ArrayList<RedeemableItem> itemData) {
        String[] headings = new String[]{"ItemCode", "Name", "Quantity"};
        System.out.println();
        for (int i = 0; i < 61; i++) {
            System.out.print("_");
        }
        System.out.print("\n| ");

        for (int i = 0; i < headings.length; i++) {
            if (i != 1) {
                System.out.printf("%-15s ", headings[i]);
            } else {
                System.out.printf("%-25s ", headings[i]);
            }
        }

        System.out.print("|");
        System.out.println();
        System.out.print("|");
        for (int i = 0; i < 59; i++) {
            System.out.print("-");
        }
        System.out.print("|");
        System.out.println();

        for (RedeemableItem item : itemData) {
            System.out.print("| ");
            System.out.printf("%-15s ", item.getItemCode());
            System.out.printf("%-25s ", item.getRewardName());
            System.out.printf("%-15s ", item.getStockAmt());

            System.out.print("|");
            System.out.println();
        }
        System.out.print("|");
        for (int i = 0; i < 59; i++) {
            System.out.print("_");
        }
        System.out.print("|");
        System.out.println("\n");
    }

    public static void displayItemOnShelfStatusAndRelatedData(ArrayList<RedeemableItem> itemData) {
        String[] headings = new String[]{"ItemCode", "Name", "Availability"};
        System.out.println();
        for (int i = 0; i < 61; i++) {
            System.out.print("_");
        }
        System.out.print("\n| ");
        for (int i = 0; i < headings.length; i++) {
            if (i != 1) {
                System.out.printf("%-15s ", headings[i]);
            } else {
                System.out.printf("%-25s ", headings[i]);
            }
        }

        System.out.print("|");
        System.out.println();
        System.out.print("|");
        for (int i = 0; i < 59; i++) {
            System.out.print("-");
        }
        System.out.print("|");
        System.out.println();

        for (RedeemableItem item : itemData) {
            System.out.print("| ");
            System.out.printf("%-15s ", item.getItemCode());
            System.out.printf("%-25s ", item.getRewardName());
            System.out.printf("%-15s ", item.getAvailability());

            System.out.print("|");
            System.out.println();
        }
        System.out.print("|");
        for (int i = 0; i < 59; i++) {
            System.out.print("_");
        }
        System.out.print("|");
        System.out.println("\n");
    }

    public static void enterToContinue() {
        System.out.println();
        System.out.print("Enter to continue");

        Scanner input = new Scanner(System.in);
        input.nextLine();
    }

    /**
     * Pause execution
     *
     * @param seconds allow decimal
     */
    public static void sleep(double seconds) {
        try {
            Thread.sleep((long) (1000 * seconds));
        } catch (InterruptedException e) {
        }
    }

    public static void clearScreen() {
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}
