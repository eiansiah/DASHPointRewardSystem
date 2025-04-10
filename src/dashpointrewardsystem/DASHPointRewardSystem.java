package dashpointrewardsystem;

import Util.GeneralFunction;
import Util.LoginRegister;
import Util.SaveLoadData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

public class DASHPointRewardSystem {

    public static boolean restart = false;

    public static void main(String[] args) {

        System.out.println(GeneralFunction.color.ANSI_BLUE + "Disclaimer: You need to follow these step for the program to work");
        System.out.println("1. Go to File --> Project Properties --> Libraries");
        System.out.println("2. Add DB/mysql-connector-j-8.3.0/mysql-connector-j-8.3.0.jar to module path");
        System.out.println("3. Add DB/c3p0-0.9.5.5/lib/c3p0-0.9.5.5.jar to class path");
        System.out.println("4. Add DB/c3p0-0.9.5.5/lib/mchange-commons-java-0.2.19.jar to class path" + GeneralFunction.color.ANSI_RESET);

        SaveLoadData.startConnection();

        GeneralFunction.displayWelcomeScreen();

        while (true) {
            String UID = LoginRegister.LoginRegisterController();

            if (UID.charAt(0) == 'C') {
                ArrayList<String> customerData = SaveLoadData.SLDProfile.getCustomerProfile(UID);
                Customer customer = new Customer(customerData.get(0), customerData.get(1), customerData.get(2), customerData.get(3), LocalDate.parse(customerData.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")), customerData.get(5), Integer.parseInt(customerData.get(6)), customerData.get(7), Integer.parseInt(customerData.get(8)), LocalDate.parse(customerData.get(9), DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                ActionMenu.CustomerController.Handle(customer);
            } else {
                ArrayList<String> staffData = SaveLoadData.SLDStaff.getStaffProfile(UID);
                Staff staff = new Staff(staffData.get(0), staffData.get(1), staffData.get(2), staffData.get(3), LocalDate.parse(staffData.get(4), DateTimeFormatter.ofPattern("yyyy-MM-dd")), staffData.get(5));

                if (staff.getAccessLevel().equals("Normal")) {
                    ActionMenu.StaffController.Handle(staff);
                } else {
                    ActionMenu.TopManagementController.Handle(staff);
                }
            }

            if (restart) {
                restart = false;
            }

            GeneralFunction.clearScreen();
            System.out.println("Logging out...");
            System.out.println(""
                    + "═✿✿✿═════✿✿═════✿✿═════✿✿✿═\n"
                    + "════════════ ('\\../') ═════════\n"
                    + "════════════ (◕.◕) ════════════\n"
                    + "════════════ (,,)(,,) ══════════\n"
                    + "▀█▀.█▄█.█▀█.█▄.█.█▄▀　█▄█.█▀█.█─█\n"
                    + ".█.─█▀█.█▀█.█.▀█.█▀▄　─█.─█▄█.█▄█\n"
                    + "              ");
        }
    }

    private static class ActionMenu {

        public static class CustomerController {

            public static Customer customer;

            private static void Handle(Customer _customer) {
                customer = _customer;

                displayDashboard(_customer.getUsername());

                SaveLoadData.SLDPointAndItem.checkPointsValidity(_customer.getUserID());
                String tierMsg = SaveLoadData.SLDPointAndItem.checkTierValidity(_customer);

                if (tierMsg != null) {
                    System.out.println(tierMsg);
                }

                while (!restart) {
                    int action = displayMainMenu();
                    if (customer.getLoyaltyTier().equals("Gold")) {
                        if (action == 1) {
                            ItemRedemption.Handle();
                        } else if (action == 2) {
                            Profile.Handle();
                        } else if (action == 3) {
                            PointsPolicy.Handle();
                        } else if (action == 4) {
                            DashContact.Handle();
                        } else if (action == 5) {
                            EventRes.Handle();
                        } else {
                            if (confirmLogout()) {
                                break;
                            }
                        }
                    } else {
                        if (action == 1) {
                            ItemRedemption.Handle();
                        } else if (action == 2) {
                            Profile.Handle();
                        } else if (action == 3) {
                            DashContact.Handle();
                        } else if (action == 4) {
                            EventRes.Handle();
                        } else {
                            if (confirmLogout()) {
                                break;
                            }
                        }
                    }
                    GeneralFunction.clearScreen();
                }

            }

            private static class ItemRedemption {

                public static void Handle() {
                    //To retrieve the items
                    ArrayList<RedeemableItem> itemsList = new ArrayList<>();
                    itemsList = SaveLoadData.SLDPointAndItem.getAllItem();
                    while (true) {
                        GeneralFunction.clearScreen();
                        displayRedeemableItemMenu(customer.getLoyaltyTier(), itemsList);
                        System.out.println("\nYour Loyalty Tier\t: " + customer.getLoyaltyTier());
                        System.out.println("Your Balance Points\t: " + customer.getBalancePoint() + "pt(s).");
                        if ((customer.getBalancePoint() < 150 && customer.getLoyaltyTier().equals("Bronze")) || (customer.getBalancePoint() < 150 && customer.getLoyaltyTier().equals("Silver")) || (customer.getBalancePoint() < 100 && customer.getLoyaltyTier().equals("Gold"))) {
                            System.out.println("Sorry! There is nothing to be redeemed!\n");
                            GeneralFunction.enterToContinue();
                            break;
                        } else {
                            System.out.println("\nPoint Redemption\n**OUS means Out of Stock\n1. Redeem Item (Vouchers will be immediately applied to your current payment.)\n2. Back to Home\n");
                            int num = checkActionRedemption();
                            if (num == 1) {
                                redeem(itemsList, customer.getLoyaltyTier());
                            } else {
                                break;
                            }
                        }
                    }
                }

                //displayRedeemableItems
                private static void displayRedeemableItemMenu(String tier, ArrayList<RedeemableItem> itemsList) {
                    System.out.println();
                    for (int i = 0; i < 24; i++) {
                        System.out.print(" ");
                    }
                    System.out.println("~ Item Redemption ~\n");
                    System.out.println("Cash Voucher");
                    for (int i = 0; i < 68; i++) {
                        System.out.print("_");
                    }
                    System.out.print("\n");
                    System.out.printf("| %-10s %-25s %-18s %-9s|\n", "Item Code", "Item Name", "Points Required", "Qty");
                    System.out.print("|");
                    for (int i = 0; i < 66; i++) {
                        System.out.print("-");
                    }
                    System.out.print("|\n");
                    for (int i = 0; i < itemsList.size(); i++) {
                        if (customer.getLoyaltyTier().equals("Gold")) {
                            if (("Gold".equals(itemsList.get(i).getMinimumTier())) && ((itemsList.get(i).getItemCategory()).equals("Cash Voucher"))) {
                                displayRow(itemsList.get(i));
                            }
                        } else {
                            if (("Bronze".equals(itemsList.get(i).getMinimumTier())) && ((itemsList.get(i).getItemCategory()).equals("Cash Voucher"))) {
                                displayRow(itemsList.get(i));
                            }
                        }
                    }
                    System.out.print("|");
                    for (int i = 0; i < 66; i++) {
                        System.out.print("_");
                    }
                    System.out.print("|\n");

                    if (tier.equals("Gold") || tier.equals("Silver")) {
                        System.out.println("\nFood Voucher");
                        for (int i = 0; i < 68; i++) {
                            System.out.print("_");
                        }
                        System.out.print("\n");
                        System.out.printf("| %-10s %-25s %-18s %-9s|\n", "Item Code", "Item Name", "Points Required", "Qty");
                        System.out.print("|");
                        for (int i = 0; i < 66; i++) {
                            System.out.print("-");
                        }
                        System.out.print("|\n");
                        for (int i = 0; i < itemsList.size(); i++) {
                            if (("Silver".equals(itemsList.get(i).getMinimumTier())) && ((itemsList.get(i).getItemCategory()).equals("Food Voucher"))) {
                                displayRow(itemsList.get(i));
                            }
                        }
                        System.out.print("|");
                        for (int i = 0; i < 66; i++) {
                            System.out.print("_");
                        }
                        System.out.print("|\n");
                    }
                    if (tier.equals("Gold")) {
                        System.out.println("\nLimited Edition Merchandise");
                        for (int i = 0; i < 68; i++) {
                            System.out.print("_");
                        }
                        System.out.print("\n");
                        System.out.printf("| %-10s %-25s %-18s %-9s|\n", "Item Code", "Item Name", "Points Required", "Qty");
                        System.out.print("|");
                        for (int i = 0; i < 66; i++) {
                            System.out.print("-");
                        }
                        System.out.print("|\n");
                        for (int i = 0; i < itemsList.size(); i++) {
                            if (("Gold".equals(itemsList.get(i).getMinimumTier())) && ((itemsList.get(i).getItemCategory()).equals("Merchandise"))) {
                                displayRow(itemsList.get(i));
                            }
                        }
                        System.out.print("|");
                        for (int i = 0; i < 66; i++) {
                            System.out.print("_");
                        }
                        System.out.print("|\n");
                    }
                }

                private static void displayRow(RedeemableItem redeem) {
                    if (redeem.getStockAmt() == 0) {

                        System.out.printf("| %-10s %-25s %-18s %-9s|\n", redeem.getItemCode(), redeem.getRewardName(), redeem.getPtRequired(), "OUS");

                    } else {

                        System.out.printf("| %-10s %-25s %-18d %-9d|\n", redeem.getItemCode(), redeem.getRewardName(), redeem.getPtRequired(), redeem.getStockAmt());

                    }
                }

                //check action
                private static int checkActionRedemption() {
                    int num = 0;
                    do {
                        try {
                            System.out.print("Enter action: ");
                            Scanner input = new Scanner(System.in);
                            num = Integer.parseInt(input.nextLine());
                            if ((num != 1) && (num != 2)) {
                                System.out.println("Sorry! The actions that are available are only 1 and 2. Please select from the selection.");
                            }
                        } catch (Exception ex) {
                            System.out.println("Sorry! The action you entered is invalid. Please enter a valid number.");
                        }
                    } while ((num != 1) && (num != 2));
                    return num;
                }

                //redeem item
                private static void redeem(ArrayList<RedeemableItem> itemsList, String tier) {
                    Scanner input = new Scanner(System.in);
                    boolean validItemCode = false;
                    String itemCodeSelected = "";
                    //Check validity of Item Code
                    do {

                        System.out.print("Enter Item Code to be redeemed (or 'X' to exit): ");
                        itemCodeSelected = input.nextLine();
                        if ("X".equals(itemCodeSelected)) {
                            validItemCode = true;
                        }
                        for (int i = 0; i < itemsList.size(); i++) {
                            if (tier.equals("Bronze")) {
                                if ((tier.equals(itemsList.get(i).getMinimumTier())) && (itemCodeSelected.equals(itemsList.get(i).getItemCode())) && ((itemsList.get(i).getStockAmt()) > 0)) {
                                    validItemCode = true;
                                }
                            } else if (tier.equals("Silver")) {
                                if (((tier.equals(itemsList.get(i).getMinimumTier())) && (itemCodeSelected.equals(itemsList.get(i).getItemCode())) && ((itemsList.get(i).getStockAmt()) > 0)) || (("Bronze".equals(itemsList.get(i).getMinimumTier())) && (itemCodeSelected.equals(itemsList.get(i).getItemCode())) && ((itemsList.get(i).getStockAmt()) > 0))) {
                                    validItemCode = true;
                                }
                            } else {
                                if (((tier.equals(itemsList.get(i).getMinimumTier())) && (itemCodeSelected.equals(itemsList.get(i).getItemCode())) && ((itemsList.get(i).getStockAmt()) > 0)) || (("Silver".equals(itemsList.get(i).getMinimumTier())) && (itemCodeSelected.equals(itemsList.get(i).getItemCode())) && ((itemsList.get(i).getStockAmt()) > 0))) {
                                    validItemCode = true;
                                }
                            }
                        }

                        if (validItemCode == false) {
                            System.out.println("Sorry! The item code you entered does not exist. Please enter a valid item code or 'X' to exit.");
                        }
                    } while (validItemCode == false);
                    //Check Validity of Quantity
                    boolean validQty = false;
                    String itemQtyEntered = "";
                    if (!itemCodeSelected.equals("X")) {
                        do {
                            try {
                                System.out.print("Enter quantity for redemption (or 'X' to exit): ");
                                itemQtyEntered = input.nextLine();
                                if ("X".equals(itemQtyEntered)) {
                                    validQty = true;
                                }
                                if (Integer.valueOf(itemQtyEntered) >= 1) {
                                    for (int i = 0; i < itemsList.size(); i++) {
                                        if ((itemCodeSelected.equals(itemsList.get(i).getItemCode())) && ((itemsList.get(i).getStockAmt()) >= Integer.valueOf(itemQtyEntered))) {
                                            validQty = true;
                                            //TODO: UPDATE REDEEMED AMT
                                            if (checkSufficientPoint(itemsList.get(i).getPtRequired(), Integer.valueOf(itemQtyEntered))) {
                                                SaveLoadData.SLDPointAndItem.CustomerRedeemItem(customer.getUserID(), itemsList.get(i).getItemCode(), Integer.valueOf(itemQtyEntered));
                                                customer.setBalancePoint(customer.getBalancePoint() - (itemsList.get(i).getPtRequired() * Integer.valueOf(itemQtyEntered)));
                                                itemsList.get(i).setRedeemedAmt(itemsList.get(i).getRedeemedAmt() + Integer.valueOf(itemQtyEntered));
                                                itemsList.get(i).setStockAmt(itemsList.get(i).getStockAmt() - Integer.valueOf(itemQtyEntered));
                                                System.out.println("\nRedeemed Successfully!\nThank You! Your current balance amount of membership point is " + customer.getBalancePoint() + "pt(s).");
                                                System.out.println("You have redeemed " + itemQtyEntered + " x " + itemsList.get(i).getRewardName());
                                                GeneralFunction.enterToContinue();
                                                if (itemsList.get(i).getStockAmt() == 0) {
                                                    SaveLoadData.SLDPointAndItem.disableRedeemItem(itemsList.get(i).getItemCode(), false);
                                                }
                                            } else {
                                                validQty = false;
                                                System.out.println("Sorry! Insufficient Points! Please enter a valid quantity or 'X' to exit.");
                                            }
                                        } else if ((itemCodeSelected.equals(itemsList.get(i).getItemCode())) && ((itemsList.get(i).getStockAmt()) < Integer.valueOf(itemQtyEntered))) {
                                            System.out.println("Sorry! The stock amount of item is not sufficient. Please enter a valid quantity or 'X' to exit.");
                                        }
                                    }
                                } else {
                                    System.out.println("Sorry! The quantity entered must be more than 0. Please enter a valid quantity or 'X' to exit.");
                                }
                            } catch (Exception ex) {
                                System.out.println("Sorry! The quantity entered is invalid. Please enter a valid quantity or 'X' to exit.");
                            }
                        } while (validQty == false);
                    }
                }

                private static boolean checkSufficientPoint(int ptRequired, int qty) {
                    return customer.getBalancePoint() >= (ptRequired * qty);
                }

            }

            private static class Profile {

                public static void Handle() {
                    while (!restart) {
                        System.out.println();
                        System.out.println("\nProfile Menu: ");
                        System.out.println("1. View Profile");
                        System.out.println("2. Update Profile");
                        System.out.println("3. Delete Account");
                        System.out.println("4. Back");

                        System.out.println();

                        int choice;

                        Scanner input = new Scanner(System.in);
                        try {
                            System.out.print("Enter choice: ");

                            choice = Integer.parseInt(input.nextLine());

                            if (choice == 1) {
                                displayProfile();
                                GeneralFunction.clearScreen();
                            } else if (choice == 2) {
                                updateProfile();
                                GeneralFunction.clearScreen();
                            } else if (choice == 3) {
                                deleteAccount();
                                GeneralFunction.clearScreen();
                            } else if (choice == 4) {
                                break;
                            } else {
                                System.out.println("Sorry! Invalid choice. Please try again.");
                            }

                        } catch (Exception e) {
                            System.out.println("Sorry! Invalid input. Please try again.");
                        }

                    }
                }

                public static void displayProfile() {
                    System.out.println(customer.displayProfileInfo());
                    GeneralFunction.enterToContinue();
                }

                public static void deleteAccount() {
                    Scanner scanner = new Scanner(System.in);
                    String confirmation;

                    while (true) {
                        System.out.print("Are you sure you want to delete your account? (Y/N): ");
                        confirmation = scanner.nextLine().trim();

                        if (confirmation.equalsIgnoreCase("y")) {
                            SaveLoadData.SLDProfile.deleteCustomerAccount(customer.getUserID());
                            System.out.println("Account deleted successfully.");
                            System.out.println();
                            GeneralFunction.enterToContinue();
                            restart = true;
                            break;
                        } else if (confirmation.equalsIgnoreCase("n")) {
                            System.out.println("Account deletion cancelled.");
                            GeneralFunction.enterToContinue();
                            break;
                        } else {
                            System.out.println("Sorry! Invalid input. Please try again.");
                        }
                    }
                }

                public static void updateProfile() {

                    while (true) {
                        System.out.println("\nChoose what information you want to update:");
                        System.out.println("1. Change Name");
                        System.out.println("2. Change Email");
                        System.out.println("3. Change Contact Number");
                        System.out.println("4. Change Password");
                        System.out.println("5. Back");
                        System.out.println();

                        int choice;

                        Scanner input = new Scanner(System.in);

                        try {
                            System.out.print("Enter choice: ");
                            choice = Integer.parseInt(input.nextLine());

                            switch (choice) {
                                case 1:
                                    while (true) {
                                        System.out.print("Enter new name: ");
                                        String newName = input.nextLine().trim();
                                        if (!isValidInput(newName)) {
                                            System.out.println("Sorry! Name cannot be empty. Please enter a valid name.");
                                        } else if (!newName.matches("[a-zA-Z\\s]+")) {
                                            System.out.println("Sorry! Invalid name. Please enter a valid name containing only letters.");
                                        } else {
                                            customer.setUsername(newName);
                                            SaveLoadData.SLDProfile.changeCustomerName(customer.getUserID(), newName);
                                            System.out.println("Name updated successfully!");
                                            System.out.println();
                                            break; // Exit the loop if the name is updated successfully
                                        }
                                    }
                                    break;
                                case 2:
                                    while (true) {
                                        System.out.print("Enter new email: ");
                                        String newEmail = input.nextLine().trim();
                                        if (!isValidInput(newEmail)) {
                                            System.out.println("Sorry! Email cannot be empty. Please enter a valid email.");
                                        } else if (isValidEmail(newEmail)) {
                                            customer.setEmail(newEmail);
                                            SaveLoadData.SLDProfile.changeCustomerEmail(customer.getUserID(), newEmail);
                                            System.out.println("Email updated successfully!");
                                            System.out.println();
                                            break; // Exit the loop if the email is updated successfully
                                        } else {
                                            System.out.println("Sorry! Invalid email format. Please enter a valid email.");
                                        }
                                    }
                                    break;
                                case 3:
                                    while (true) {
                                        System.out.print("Enter new contact number: ");
                                        String newContactNumber = input.nextLine().trim();
                                        if (!isValidInput(newContactNumber)) {
                                            System.out.println("Sorry! Contact number cannot be empty. Please enter a valid contact number.");
                                        } else if (isValidPhoneNumber(newContactNumber)) {
                                            customer.setContactNum(newContactNumber);
                                            SaveLoadData.SLDProfile.changeCustomerContact(customer.getUserID(), newContactNumber);
                                            System.out.println("Contact Number updated successfully!");
                                            System.out.println();
                                            break; // Exit the loop if the contact number is updated successfully
                                        } else {
                                            System.out.println("Sorry! Invalid contact number format. Please enter a valid contact number starting with 01 and contains 10 to 11 digits.");
                                        }
                                    }
                                    break;
                                case 4:
                                    while (true) {
                                        System.out.print("Enter new password (minimum 8 characters): ");
                                        String newPassword = input.nextLine().trim();
                                        if (!isValidInput(newPassword)) {
                                            System.out.println("Sorry! Password cannot be empty. Please enter a valid password.");
                                        } else if (newPassword.length() >= 8) {
                                            customer.setPassword(newPassword);
                                            SaveLoadData.SLDProfile.changeCustomerPassword(customer.getUserID(), newPassword);
                                            System.out.println("Password changed successfully!");
                                            System.out.println();
                                            break; // Exit the loop if the password is updated successfully
                                        } else {
                                            System.out.println("Sorry! Password must have at least 8 characters. Please enter a valid password.");
                                        }
                                    }
                                    break;
                                case 5:
                                    return; // Exit the method if the user chooses to go back
                                default:
                                    System.out.println("Sorry! Invalid choice. Please enter a valid option.");
                            }
                        } catch (Exception e) {
                            System.out.println("Sorry! Invalid input. Please Try Again.");
                            input.nextLine();
                        }
                    }

                }

                // Check if there are empty input
                public static boolean isValidInput(String input) {
                    return input != null && !input.isEmpty();
                }

                // Method to validate email format using regex
                public static boolean isValidEmail(String newEmail) {
                    String emailRegex = "^(?:[a-zA-Z]+\\d*|[a-zA-Z]*)@[a-zA-Z]+\\.[a-zA-Z]+$";
                    return newEmail.matches(emailRegex);
                }

                // Method to validate contact number format using regex
                public static boolean isValidPhoneNumber(String newContactNumber) {
                    return newContactNumber.matches("^01\\d{8,9}$");
                }
            }

            private static class PointsPolicy {

                public static void Handle() {
                    GeneralFunction.clearScreen();
                    ArrayList<Policy> policyList = new ArrayList<>();
                    policyList = SaveLoadData.SLDPolicy.getAllItem();
                    displayPolicy(policyList);
                    GeneralFunction.enterToContinue();
                }

                public static void displayPolicy(ArrayList<Policy> policyList) {
                    GeneralFunction.clearScreen();
                    System.out.println("\nPoints Policy of DASH Fast Food Company:\n");
                    for (Policy value : policyList) {
                        System.out.print(value.toString());
                    }
                    System.out.print("\n");
                }

            }

            private static class DashContact {

                public static void Handle() {
                    GeneralFunction.clearScreen();
                    ArrayList<CompanyDetails> companyDetailsList = new ArrayList<>();
                    companyDetailsList = SaveLoadData.SLDContact.getAllItem();
                    displayCompanyDetails(companyDetailsList);
                    GeneralFunction.enterToContinue();
                }

                public static void displayCompanyDetails(ArrayList<CompanyDetails> companyDetailsList) {
                    System.out.println("\nContact US!\n");
                    for (CompanyDetails value : companyDetailsList) {
                        System.out.print(value.toString());
                    }
                    System.out.print("\n");
                }
            }

            private static class EventRes {

                public static void Handle() {
                    ArrayList<EventRoom> evtRmList = new ArrayList<>();
                    evtRmList = SaveLoadData.SLDEventRoom.getAllRoom();
                    ArrayList<EventRoomReservation> evtResList = new ArrayList<>();
                    EventRoomReservation.setNumOfReservation(0);
                    evtResList = SaveLoadData.SLDRecord.getAllEventRoomResRecord();
                    while (true) {
                        GeneralFunction.clearScreen();
                        displayEventRoom(evtRmList);
                        System.out.println("\nYour Loyalty Tier\t: " + customer.getLoyaltyTier());
                        System.out.println("Your Balance Points\t: " + customer.getBalancePoint() + "pt(s).");
                        if (customer.getBalancePoint() < 100 && customer.getLoyaltyTier().equals("Gold")) {
                            System.out.println("Sorry! you do not have sufficient points to reserve an event room!\n");
                            System.out.println("Event Room Reservation\n1. View Reservations\n2. Back to Home\n");
                            int num = checkActionReservationLow();
                            if (num == 1) {
                                viewReservationForMe(evtResList);
                            } else {
                                break;
                            }
                        } else {
                            System.out.println("\nEvent Room Reservation\n1. Make Reservation \n2. View Reservations\n3. Back to Home\n");
                            int num = checkActionReservation();
                            if (num == 1) {
                                reserve(evtRmList, evtResList);
                            } else if (num == 2) {
                                viewReservationForMe(evtResList);
                            } else {
                                break;
                            }
                        }
                    }
                }

                private static void displayEventRoom(ArrayList<EventRoom> evtRmList) {
                    System.out.println();
                    for (int i = 0; i < 37; i++) {
                        System.out.print(" ");
                    }
                    System.out.println("~ Event Room Reservation ~");
                    for (int i = 0; i < 102; i++) {
                        System.out.print("_");
                    }
                    System.out.print("\n");
                    System.out.printf("| %-10s %-30s %-20s %-15s %-20s|\n", "Room Num", "Room Name", "Room Type", "Pax Allowed", "Points Required");
                    System.out.print("|");
                    for (int i = 0; i < 100; i++) {
                        System.out.print("-");
                    }
                    System.out.print("|\n");
                    for (int i = 0; i < evtRmList.size(); i++) {
                        System.out.printf("| %-10s %-30s %-20s %-15d %-20d|\n", evtRmList.get(i).getRoomNum(), evtRmList.get(i).getRewardName(), evtRmList.get(i).getRoomType(), evtRmList.get(i).getPaxAllowed(), evtRmList.get(i).getPtRequired());
                    }
                    System.out.print("|");
                    for (int i = 0; i < 100; i++) {
                        System.out.print("_");
                    }
                    System.out.print("|\n");
                }

                private static int checkActionReservation() {
                    int num = 0;
                    do {
                        try {
                            System.out.print("Enter action: ");
                            Scanner input = new Scanner(System.in);
                            num = Integer.parseInt(input.nextLine());
                            if ((num != 1) && (num != 2) && (num != 3)) {
                                System.out.println("Sorry! The actions that are available are only 1, 2 and 3. Please select from the selection.");
                            }
                        } catch (Exception ex) {
                            System.out.println("Sorry! The action you entered is invalid. Please enter a valid number.");
                        }
                    } while ((num != 1) && (num != 2) && (num != 3));
                    return num;
                }

                private static int checkActionReservationLow() {
                    int num = 0;
                    do {
                        try {
                            System.out.print("Enter action: ");
                            Scanner input = new Scanner(System.in);
                            num = Integer.parseInt(input.nextLine());
                            if ((num != 1) && (num != 2)) {
                                System.out.println("Sorry! The actions that are available are only 1 and 2. Please select from the selection.");
                            }
                        } catch (Exception ex) {
                            System.out.println("Sorry! The action you entered is invalid. Please enter a valid number.");
                        }
                    } while ((num != 1) && (num != 2));
                    return num;
                }

                private static void reserve(ArrayList<EventRoom> evtRmList, ArrayList<EventRoomReservation> evtResList) {
                    Scanner input = new Scanner(System.in);
                    String selectedRoomNum;
                    String selectedDate = "";
                    String selectedPax = "0";
                    LocalDate selectedRealDate = LocalDate.now();
                    LocalDate todayDate = LocalDate.now();
                    boolean validRoomNum = false;
                    boolean validDate = false;
                    boolean validExtraPax = false;
                    do {
                        System.out.print("Enter Event Room Number (or 'X' to exit): ");
                        selectedRoomNum = input.nextLine();
                        if ("X".equals(selectedRoomNum)) {
                            validRoomNum = true;
                        }
                        for (int i = 0; i < evtRmList.size(); i++) {
                            if (selectedRoomNum.equals(evtRmList.get(i).getRoomNum()) && customer.getBalancePoint() >= evtRmList.get(i).getPtRequired()) {
                                validRoomNum = true;
                            }
                        }

                        if (validRoomNum == false) {
                            System.out.println("Sorry! The room number you entered does not exist. Please enter a valid item code or 'X' to exit.");
                        }
                    } while (validRoomNum == false);
                    if (!selectedRoomNum.equals("X")) {
                        do {
                            try {
                                System.out.print("Enter Date in yyyy-MM-dd (or 'X' to exit): ");
                                selectedDate = input.nextLine();
                                selectedRealDate = LocalDate.parse(selectedDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                if (selectedDate.equals("X")) {
                                    validDate = true;
                                } else if (selectedRealDate.isAfter(todayDate)) {
                                    validDate = true;
                                    if (!evtResList.isEmpty()) {
                                        for (int i = 0; i < evtResList.size(); i++) {
                                            if (selectedRoomNum.equals(((EventRoom) evtResList.get(i).getReward()).getRoomNum()) && selectedRealDate == evtResList.get(i).getReservationDate()) {
                                                validDate = false;
                                                System.out.println("Sorry! The room is unavailable. Please enter a valid date or 'X' to exit.");
                                            }
                                        }
                                    }
                                } else {
                                    System.out.println("Sorry! The date you entered is invalid. Please enter a valid date or 'X' to exit.");
                                }
                            } catch (Exception ex) {
                                System.out.println("Sorry! The date you entered is invalid. Please enter a valid date or 'X' to exit.");
                            }
                        } while (validDate == false);
                        if (!selectedDate.equals("X")) {
                            do {
                                try {
                                    System.out.print("Enter Extra Number of Pax Needed (or 'X' to exit): ");
                                    selectedPax = input.nextLine();
                                    if (selectedPax.equals("X")) {
                                        validExtraPax = true;
                                    } else if (Integer.valueOf(selectedPax) >= 0) {
                                        for (int i = 0; i < evtRmList.size(); i++) {
                                            if (selectedRoomNum.equals(evtRmList.get(i).getRoomNum()) && customer.getBalancePoint() >= (evtRmList.get(i).getPtRequired() + 60 * Integer.valueOf(selectedPax))) {
                                                validExtraPax = true;
                                            }
                                        }
                                    } else {
                                        System.out.println("Sorry! The extra number of pax you entered is invalid. Please enter a valid number or 'X' to exit.");
                                    }
                                } catch (Exception ex) {
                                    System.out.println("Sorry! The extra number of pax you entered is invalid. Please enter a valid number or 'X' to exit.");
                                }
                            } while (validExtraPax == false);
                            if (!selectedPax.equals("X")) {
                                //make reservation
                                for (int i = 0; i < evtRmList.size(); i++) {
                                    if (selectedRoomNum.equals(evtRmList.get(i).getRoomNum())) {
                                        //add to reservation array
                                        //reduce customer point
                                        customer.setBalancePoint(customer.getBalancePoint() - (evtRmList.get(i).getPtRequired() + 60 * Integer.valueOf(selectedPax)));
                                        if (!evtResList.isEmpty()) {
                                            evtResList.add(new EventRoomReservation((evtResList.get(evtResList.size() - 1).getRecordID() + 1), evtRmList.get(i), selectedRealDate, "Available", customer, Integer.valueOf(selectedPax)));
                                        } else {
                                            evtResList.add(new EventRoomReservation("1", evtRmList.get(i), selectedRealDate, "Available", customer, Integer.valueOf(selectedPax)));
                                        }

                                        //reduce customer point in db
                                        //add reservation record in db
                                        SaveLoadData.SLDEventRoom.customerReserveRoom(selectedRoomNum, selectedDate, customer.getUserID(), Integer.valueOf(selectedPax));
                                        System.out.println(selectedRoomNum + " booked successfully on " + selectedDate + "!");
                                        GeneralFunction.enterToContinue();
                                    }
                                }
                            }
                        }
                    }
                }

                private static void viewReservationForMe(ArrayList<EventRoomReservation> evtResList) {
                    while (true) {
                        GeneralFunction.clearScreen();
                        displayMyResRecordOnly(evtResList);
                        System.out.println("\nEvent Room Reservation Records\n1. Cancel Reservation\n2. Back to Event Room Reservation Page\nNote that no points will be refunded after cancellation of a reservation.");
                        int num = checkActionReservationRecords();
                        if (num == 2) {
                            break;
                        } else {
                            cancelReservationRecord(evtResList);
                        }
                    }
                }

                //check action
                private static int checkActionReservationRecords() {
                    int num = 0;
                    do {
                        try {
                            System.out.print("Enter action: ");
                            Scanner input = new Scanner(System.in);
                            num = Integer.parseInt(input.nextLine());
                            if ((num != 1) && (num != 2)) {
                                System.out.println("Sorry! The actions that are available are only 1 and 2. Please select from the selection.");
                            }
                        } catch (Exception ex) {
                            System.out.println("Sorry! The action you entered is invalid. Please enter a valid number.");
                        }
                    } while ((num != 1) && (num != 2));
                    return num;
                }

                private static void displayMyResRecordOnly(ArrayList<EventRoomReservation> evtResList) {
                    System.out.println();
                    for (int i = 0; i < 33; i++) {
                        System.out.print(" ");
                    }
                    System.out.println("~ Event Room Reservation Records ~");
                    for (int i = 0; i < 102; i++) {
                        System.out.print("_");
                    }
                    System.out.print("\n");
                    if (evtResList.size() > 0) {
                        for (int i = 0; i < evtResList.size(); i++) {
                            if (evtResList.get(i).getCustomer().getUserID().equals(customer.getUserID())) {
                                if (!evtResList.get(i).getReservationDate().isAfter(LocalDate.now())) {
                                    if (evtResList.get(i).getStatus().equals("Available")) {
                                        evtResList.get(i).setStatus("Expired");
                                        SaveLoadData.SLDRecord.expiredEventRoomResRecordSQL(evtResList.get(i).getRecordID());
                                    }
                                }
                                System.out.println(evtResList.get(i).toString());
                                System.out.println();
                            }
                        }
                        System.out.println(EventRoomReservation.getNumOfReservation() + " reservation record(s) found.");
                    } else {
                        System.out.println("No reservation record found!");
                    }
                }

                private static void cancelReservationRecord(ArrayList<EventRoomReservation> evtResList) {
                    //Enter a valid record id
                    boolean validRecordID = false;
                    String selectedRecordID;
                    do {
                        System.out.print("Enter Record ID to be cancelled (or 'X' to exit): ");
                        Scanner input = new Scanner(System.in);
                        selectedRecordID = input.nextLine();
                        if (selectedRecordID.equals("X")) {
                            break;
                        }
                        for (int i = 0; i < evtResList.size(); i++) {
                            if (evtResList.get(i).getRecordID().equals(selectedRecordID)) {
                                validRecordID = true;
                                //cancel from sql
                                SaveLoadData.SLDRecord.cancelEventRoomResRecordSQL(selectedRecordID);
                                evtResList.get(i).setStatus("Cancelled");
                                //display successful message
                                System.out.println("Record " + selectedRecordID + " cancelled Successfully!");
                                GeneralFunction.enterToContinue();
                            }
                        }
                        if (validRecordID == false) {
                            System.out.println("Sorry! The Record ID you entered is invalid. Please enter a valid number.");
                        }
                    } while (validRecordID == false);
                }
            }

            private static void displayDashboard(String name) {
                System.out.println("Logged in as customer.");
                System.out.println("Welcome back, " + name);
                System.out.println();
            }

            private static int displayMainMenu() {
                int action;
                if (customer.getLoyaltyTier().equals("Gold")) {
                    System.out.println("Home Page Menu");
                    System.out.println("1. Item Redemption");
                    System.out.println("2. Profile");
                    System.out.println("3. Policy");
                    System.out.println("4. DASH Contact");
                    System.out.println("5. Event Room Reservation");
                    System.out.println("6. Logout");

                    System.out.println();

                    while (true) {
                        try {
                            Scanner input = new Scanner(System.in);

                            System.out.print("Enter action: ");

                            action = Integer.parseInt(input.nextLine());

                            if (action >= 1 && action <= 6) {
                                break;
                            } else {
                                System.out.println("Sorry! Invalid action. Please try again.");
                            }
                        } catch (Exception e) {
                            System.out.println("Sorry! Invalid input. Please try again.");
                        }

                        //BUG: https://stackoverflow.com/questions/9146257/why-do-system-err-statements-get-printed-first-sometimes
                        //Avoid use err.println
                        System.out.println();
                    }
                } else {
                    System.out.println("Home Page Menu");
                    System.out.println("1. Item Redemption");
                    System.out.println("2. Profile");
                    System.out.println("3. Policy");
                    System.out.println("4. DASH Contact");
                    System.out.println("5. Logout");

                    System.out.println();

                    while (true) {
                        try {
                            Scanner input = new Scanner(System.in);

                            System.out.print("Enter action: ");

                            action = Integer.parseInt(input.nextLine());

                            if (action >= 1 && action <= 5) {
                                break;
                            } else {
                                System.out.println("Sorry! Invalid action. Please try again.");
                            }
                        } catch (Exception e) {
                            System.out.println("Sorry! Invalid input. Please try again.");
                        }

                        //BUG: https://stackoverflow.com/questions/9146257/why-do-system-err-statements-get-printed-first-sometimes
                        //Avoid use err.println
                        System.out.println();
                    }
                }
                return action;
            }
        }

        public static class StaffController {

            public static Staff staff;

            private static void Handle(Staff _staff) {
                staff = _staff;

                displayDashboard(staff.getUsername());

                while (true) {
                    int action = displayMainMenu();
                    if (action == 1) {
                        addPoints();
                    } else if (action == 2) {
                        displayStaffProfile();
                    } else if (action == 3) {
                        updateReservationStatus();
                    } else {
                        if (confirmLogout()) {
                            break;
                        }
                    }
                    GeneralFunction.clearScreen();
                }
            }

            private static void displayDashboard(String name) {
                System.out.println("Logged in as staff.");
                System.out.println("Welcome back, " + name);
                System.out.println();
            }

            private static int displayMainMenu() {

                int action;

                while (true) {
                    try {
                        System.out.println("\nHome Page Menu");
                        System.out.println("1. Add point");
                        System.out.println("2. View Profile");
                        System.out.println("3. Update Event Reservation Status");
                        System.out.println("4. Logout");

                        System.out.println();

                        Scanner input = new Scanner(System.in);

                        System.out.print("Enter action: ");

                        action = Integer.parseInt(input.nextLine());

                        if (action >= 1 && action <= 4) {
                            break;
                        } else {
                            System.out.println("Sorry! Invalid action. Please try again.");
                        }
                    } catch (Exception e) {
                        System.out.println("Sorry! Invalid input. Please try again.");
                    }

                    //BUG: https://stackoverflow.com/questions/9146257/why-do-system-err-statements-get-printed-first-sometimes
                    //Avoid use err.println
                    System.out.println();
                }

                return action;
            }

            public static void displayStaffProfile() {
                GeneralFunction.clearScreen();
                System.out.println("\nProfile");
                System.out.println(staff.displayProfileInfo());
                GeneralFunction.enterToContinue();
            }

            private static void displayTodayResRecordOnly(ArrayList<EventRoomReservation> evtResList) {
                System.out.print("\n");
                for (int i = 0; i < 33; i++) {
                    System.out.print(" ");
                }
                System.out.println("~ Event Room Reservation Records ~");
                for (int i = 0; i < 102; i++) {
                    System.out.print("_");
                }
                System.out.print("\n");

                for (int i = 0; i < evtResList.size(); i++) {
                    System.out.println(evtResList.get(i).toString());
                    System.out.println();
                }
                System.out.println(EventRoomReservation.getNumOfReservation() + " reservation record(s) found.");

            }
            private static int checkActionUpdateReservationRecords() {
                    int num = 0;
                    do {
                        try {
                            System.out.print("Enter action: ");
                            Scanner input = new Scanner(System.in);
                            num = Integer.parseInt(input.nextLine());
                            if ((num != 1) && (num != 2)) {
                                System.out.println("Sorry! The actions that are available are only 1 and 2. Please select from the selection.");
                            }
                        } catch (Exception ex) {
                            System.out.println("Sorry! The action you entered is invalid. Please enter a valid number.");
                        }
                    } while ((num != 1) && (num != 2));
                    return num;
                }
            public static void updateReservationStatus() {
                //display today reservation
                ArrayList<EventRoomReservation> evtResList = new ArrayList<>();
                EventRoomReservation.setNumOfRedemption(0);
                evtResList = SaveLoadData.SLDRecord.getEventRoomResToday();
                //let them choose
                int action;

                while (true) {
                    try {
                        if (evtResList.size() > 0) {
                            displayTodayResRecordOnly(evtResList);
                            System.out.println("\nUpdate Reservation");
                            System.out.println("1. Mark as Completed.");
                            System.out.println("2. Back to Home");

                            action=checkActionUpdateReservationRecords();

                            if (action == 1) {
                                Scanner input = new Scanner(System.in);
                                boolean validRecordID = false;
                                String selectedRecordID;
                                do {
                                    System.out.print("\nEnter Record ID to be updated (or 'X' to exit): ");
                                    selectedRecordID = input.nextLine();
                                    if (selectedRecordID.equals("X")) {
                                        break;
                                    }
                                    for (int i = 0; i < evtResList.size(); i++) {
                                        if (evtResList.get(i).getRecordID().equals(selectedRecordID)) {
                                            validRecordID = true;
                                            //update to completed
                                            SaveLoadData.SLDRecord.completedEventRoomResRecordSQL(selectedRecordID);
                                            evtResList.get(i).setStatus("Completed");
                                            evtResList.remove(i);
                                            //display successful message
                                            System.out.println("Record " + selectedRecordID + " updated successfully!");
                                            GeneralFunction.enterToContinue();
                                            GeneralFunction.clearScreen();
                                        }
                                    }
                                    if (validRecordID == false) {
                                        System.out.println("Sorry! The Record ID you entered is invalid. Please enter a valid number.");
                                    }
                                } while (validRecordID == false);
                            } else{
                                break;
                            }
                        } else {
                            GeneralFunction.clearScreen();
                            System.out.println("\nUpdate Reservation");
                            System.out.println("No available reservation today!");
                            System.out.println("1. Back to Home");

                            Scanner input = new Scanner(System.in);

                            System.out.print("Enter action: ");

                            action = Integer.parseInt(input.nextLine());
                            if (action == 1) {
                                break;
                            } else {
                                System.out.println("Sorry! Invalid action. Please try again.");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Sorry! Invalid input. Please try again.");
                    }
                }
                //back
            }

            public static void addPoints() {
                GeneralFunction.clearScreen();
                Scanner scan = new Scanner(System.in);

                while (true) {
                    System.out.print("Please enter Customer ID (or 'X' to exit): ");
                    String customerID = scan.nextLine();

                    if (Objects.equals(customerID, "X") || Objects.equals(customerID, "x")) {
                        break;
                    }

                    if (!Pattern.matches("^[C|S]\\d{5}", customerID)) {
                        System.out.println("Sorry! Invalid Customer ID. Please try again!\n");
                        continue;
                    }

                    try {
                        SaveLoadData.SLDProfile.getCustomerProfile(customerID);
                    } catch (Exception e) {
                        System.out.println("Sorry! Customer ID doesn't exists. Please try again!\n");
                        continue;
                    }

                    double newTransactionAmt = 0;
                    String transactionAmt;

                    while (true){
                        try{
                            System.out.print("Please enter transaction amount (or 'X' to exit): ");
                            transactionAmt = scan.nextLine();

                            if (transactionAmt.equals("X") || transactionAmt.equals("x")) {
                                break;
                            }else{
                                newTransactionAmt = Double.parseDouble(transactionAmt);

                                if(newTransactionAmt <= 0){
                                    System.out.println("Invalid amount. Please natural number greater than 0.");
                                }else{
                                    break;
                                }
                            }
                        }catch (Exception e){
                            System.out.println("Invalid input. Please natural number greater than 0.");
                        }
                    }

                    if (transactionAmt.equals("X") || transactionAmt.equals("x")) {
                        break;
                    }

                    int pointsEarned = (int) newTransactionAmt;

                    if (pointsEarned >= 200) {
                        pointsEarned += 100;
                    } else if (pointsEarned >= 100) {
                        pointsEarned += 50;
                    } else if (pointsEarned >= 50) {
                        pointsEarned += 25;
                    }

                    SaveLoadData.SLDPointAndItem.addPoint(customerID, staff.getUserID(), pointsEarned);

                    System.out.println("\nPoints updated successfully!\n");
                    GeneralFunction.enterToContinue();
                    break;
                }

            }
        }

        public static class TopManagementController {

            public static Staff staff;

            private static void Handle(Staff _staff) {
                staff = _staff;

                displayDashboard(staff.getUsername());

                while (true) {
                    int action = displayMainMenu();
                    if (action == 1) {
                        StaffManagement.Handle();
                    } else if (action == 2) {
                        ShowRecord.Handle();
                    } else if (action == 3) {
                        TrendReport.Handle();
                    } else if (action == 4) {
                        RewardManagement.Handle();
                    } else if (action == 5) {
                        UpdatePolicy.updatePolicy();
                    } else if (action == 6) {
                        UpdateCompanyDetails.updateDetails();
                    } else {
                        if (confirmLogout()) {
                            break;
                        }
                    }
                    GeneralFunction.clearScreen();
                }
            }

            private static class StaffManagement {

                private static ArrayList<Staff> staffList = new ArrayList<>();
                private static Scanner scanner = new Scanner(System.in);

                private static void Handle() {
                    staffList = SaveLoadData.SLDStaff.getStaffList();

                    while (true) {
                        try {
                            System.out.println("\nStaff Management System");
                            System.out.println("1. Add Staff");
                            System.out.println("2. Delete Staff");
                            System.out.println("3. View Staff");
                            System.out.println("4. Back");
                            System.out.println();
                            System.out.print("Enter choice: ");

                            int choice = Integer.parseInt(scanner.nextLine());

                            switch (choice) {
                                case 1:
                                    addStaff();
                                    GeneralFunction.clearScreen();
                                    break;
                                case 2:
                                    deleteStaff();
                                    GeneralFunction.clearScreen();
                                    break;
                                case 3:
                                    viewStaff();
                                    GeneralFunction.clearScreen();
                                    break;
                                case 4:
                                    return; // Exit the loop
                                default:
                                    System.out.println("Sorry! Invalid choice. Please try again.");
                            }
                        } catch (Exception e) {
                            System.out.println("Sorry! Invalid input. Please try again!!!");
                        }
                    }
                }

                private static void addStaff() {
                    System.out.println("\nAdding Staff");

                    String name;
                    do {
                        System.out.print("Enter name: ");
                        name = scanner.nextLine().trim();
                        if (name.isEmpty()) {
                            System.out.println("Sorry! Name cannot be empty. Please try again.");
                        } else if (!name.matches("[a-zA-Z\\s]+")) {
                            System.out.println("Sorry! Invalid name! Please enter a valid name containing only letters.");
                        }
                    } while (name.isEmpty() || !name.matches("[a-zA-Z\\s]+"));

                    // contact number with validation
                    String contact;
                    do {
                        System.out.print("Enter contact number: ");
                        contact = scanner.nextLine().trim();
                        if (!isValidContactNumber(contact)) {
                            System.out.println("Sorry! Invalid contact number. Please try again.");
                        }
                    } while (!isValidContactNumber(contact));

                    // password with validation
                    String password;
                    do {
                        System.out.print("Enter your password (minimum 8 characters): ");
                        password = scanner.nextLine();
                        if (password.length() < 8) {
                            System.out.println("Sorry! Password must be at least 8 characters long. Please try again.");
                        }
                    } while (password.length() < 8);

                    // access level with validation
                    String accessLevel;
                    do {
                        System.out.print("Enter access level (Normal, Top): ");
                        accessLevel = scanner.nextLine();

                        if (!(accessLevel.equals("Top") || accessLevel.equals("top") || accessLevel.equals("normal") || accessLevel.equals("Normal"))) {
                            System.out.println("Sorry! Invalid access level. Please enter Normal or Top.");
                        }
                    } while (!(accessLevel.equals("Top") || accessLevel.equals("top") || accessLevel.equals("normal") || accessLevel.equals("Normal")));

                    System.out.println("Adding...");
                    SaveLoadData.SLDStaff.createStaff(name, accessLevel, contact, password);
                    staffList = SaveLoadData.SLDStaff.getStaffList();
                    System.out.println("User added successfully.");
                    GeneralFunction.enterToContinue();
                    System.out.println();
                }

                public static boolean isValidContactNumber(String contactNumber) {
                    return contactNumber.matches("^01\\d{8,9}$");
                }

                private static void deleteStaff() {

                    if (staffList.isEmpty()) {
                        System.out.println("No staff to delete.");
                        GeneralFunction.enterToContinue();
                        return;
                    }

                    System.out.println("Enter the staff ID you want to delete:");
                    for (int i = 0; i < staffList.size(); i++) {
                        System.out.println(staffList.get(i).getUserID() + " " + staffList.get(i).getUsername());
                    }

                    boolean staffExists = false;
                    while (true) {
                        try {
                            System.out.print("Enter Staff ID: ");
                            String staffID = scanner.nextLine();

                            for (Staff staff : staffList) {
                                if (staff.getUserID().equals(staffID)) {
                                    staffExists = true;

                                    System.out.println("Processing...");
                                    SaveLoadData.SLDStaff.deleteStaffAccount(staffID);
                                    System.out.println("Staff deleted");
                                    GeneralFunction.enterToContinue();

                                    staffList.remove(staff);
                                    System.out.println();

                                    break;
                                }
                            }

                            if (staffExists) {
                                break;
                            } else {
                                System.out.println("Sorry! Staff doesn't exists. Please enter other ID.");
                            }
                        } catch (Exception e) {
                            System.out.println("Sorry! Invalid Staff ID. Please try again.");
                        }
                    }

                }

                private static void viewStaff() {
                    System.out.println("\nViewing Staff List");
                    if (staffList.isEmpty()) {
                        System.out.println("No users available.");
                        return;
                    }
                    System.out.println("\nList of Staff:");
                    for (int i = 0; i < 54; i++) {
                        System.out.print("_");
                    }
                    System.out.println();
                    System.out.printf("|%-10s %-25s %-15s|\n", "Staff ID", "Staff Name", "Contact Number");
                    System.out.print("|");
                    for (int i = 0; i < 52; i++) {
                        System.out.print("-");
                    }
                    System.out.println("|");
                    for (Staff staff : staffList) {
                        System.out.printf("|%-10s %-25s %-15s|\n", staff.getUserID(), staff.getUsername(), staff.getContactNum());
                    }
                    System.out.print("|");
                    for (int i = 0; i < 52; i++) {
                        System.out.print("_");
                    }
                    System.out.println("|\n");
                    GeneralFunction.enterToContinue();
                }

            }

            private static class ShowRecord {

                private static void Handle() {
                    ArrayList<ItemRedemption> redemptionRecordList = new ArrayList<>();
                    ArrayList<RewardPoint> rewardPointRecordList = new ArrayList<>();
                    ArrayList<EventRoomReservation> evtRoomResRecordList = new ArrayList<>();
                    PointRedemption[] ptRedemptionRecordList = new PointRedemption[100];

                    while (true) {
                        GeneralFunction.clearScreen();
                        System.out.println("\nRecord Management");
                        System.out.println("1. Item Redemption Records");
                        System.out.println("2. Reward Point Records");
                        System.out.println("3. Event Room Reservation Records");
                        System.out.println("4. Overall Point Redemption Records");
                        System.out.println("5. Back to Home");
                        int num = checkActionRecord();
                        if (num == 1) {
                            System.out.println("This might take some time. Please wait patiently...");
                            ItemRedemption.setNumOfItemRedemption(0);
                            redemptionRecordList = SaveLoadData.SLDRecord.getAllRedemptionRecord();
                            actionOnRedemptionRecord(redemptionRecordList);
                        } else if (num == 2) {
                            System.out.println("This might take some time. Please wait patiently...");
                            RewardPoint.setNumOfRewardPoint(0);
                            rewardPointRecordList = SaveLoadData.SLDRecord.getAllRewardPointRecord();
                            actionOnRewardPointRecord(rewardPointRecordList);
                        } else if (num == 3) {
                            System.out.println("This might take some time. Please wait patiently...");
                            EventRoomReservation.setNumOfReservation(0);
                            evtRoomResRecordList = SaveLoadData.SLDRecord.getAllEventRoomResRecord();
                            actionOnEventRoomResRecord(evtRoomResRecordList);
                        } else if (num == 4) {
                            System.out.println("This might take some time. Please wait patiently...");
                            PointRedemption.setNumOfRedemption(0);
                            ptRedemptionRecordList = SaveLoadData.SLDRecord.getOverallRedemptionRecord();
                            actionOnPtRedemptionRecord(ptRedemptionRecordList);
                        } else {
                            break;
                        }
                    }
                }

                //check action of record management
                private static int checkActionRecord() {
                    int num = 0;
                    do {
                        try {
                            System.out.print("Enter action: ");
                            Scanner input = new Scanner(System.in);
                            num = Integer.parseInt(input.nextLine());
                            if (num < 1 || num > 5) {
                                System.out.println("Sorry! The actions that are available are only 1, 2, 3, 4 and 5. Please select from the selection.");
                            }
                        } catch (Exception ex) {
                            System.out.println("Sorry! The action you entered is invalid. Please enter a valid number.");
                        }
                    } while (num < 1 || num > 5);
                    return num;
                }

                //display record
                private static void displayRedemptionRecord(ArrayList<ItemRedemption> redemptionRecordList, String arrange) {
                    System.out.println();
                    for (int i = 0; i < 55; i++) {
                        System.out.print(" ");
                    }
                    System.out.println("~ Item Redemption Records ~");
                    for (int i = 0; i < 139; i++) {
                        System.out.print("_");
                    }
                    if (redemptionRecordList.size() > 0) {
                        System.out.print("\n");
                        System.out.printf("| %-10s  %-15s  %-12s  %-30s  %-5s  %-5s  %-20s  %-23s  |\n", "Record ID", "Customer ID", "Item Code", "Item Name", "U/P", "Qty", "Total Pt Redeemed", "Date");
                        System.out.print("|");
                        for (int i = 0; i < 137; i++) {
                            System.out.print("-");
                        }
                        System.out.print("|\n");
                        if (arrange.equals("ASC")) {
                            for (int i = 0; i < redemptionRecordList.size(); i++) {

                                if ((redemptionRecordList.get(i).getReward()) instanceof RedeemableItem) {
                                    String redeemDate = (((ItemRedemption) redemptionRecordList.get(i)).getRedeemDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                    System.out.printf("| %-10s  %-15s  %-12s  %-30s  %-5d  %-5d  %-20d  %-23s  |\n", redemptionRecordList.get(i).getRecordID(), redemptionRecordList.get(i).getCustomer().getUserID(), ((RedeemableItem) redemptionRecordList.get(i).getReward()).getItemCode(), redemptionRecordList.get(i).getReward().getRewardName(), redemptionRecordList.get(i).getReward().getPtRequired(), ((ItemRedemption) redemptionRecordList.get(i)).getRedeemedQtyThisRecord(), ((ItemRedemption) redemptionRecordList.get(i)).calcPtRedeemed(), redeemDate);
                                }

                            }
                        } else {
                            for (int i = (redemptionRecordList.size() - 1); i >= 0; i--) {
                                if ((redemptionRecordList.get(i).getReward()) instanceof RedeemableItem && redemptionRecordList.get(i) != null) {
                                    String redeemDate = (((ItemRedemption) redemptionRecordList.get(i)).getRedeemDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                    System.out.printf("| %-10s  %-15s  %-12s  %-30s  %-5d  %-5d  %-20d  %-23s  |\n", redemptionRecordList.get(i).getRecordID(), redemptionRecordList.get(i).getCustomer().getUserID(), ((RedeemableItem) redemptionRecordList.get(i).getReward()).getItemCode(), redemptionRecordList.get(i).getReward().getRewardName(), redemptionRecordList.get(i).getReward().getPtRequired(), ((ItemRedemption) redemptionRecordList.get(i)).getRedeemedQtyThisRecord(), ((ItemRedemption) redemptionRecordList.get(i)).calcPtRedeemed(), redeemDate);
                                }
                            }
                        }
                        System.out.print("|");
                        for (int i = 0; i < 137; i++) {
                            System.out.print("_");
                        }
                        System.out.print("|\n");
                        System.out.println(ItemRedemption.getNumOfItemRedemption() + " item redemption record(s) found.");
                    } else {
                        System.out.println("No records found!");
                    }

                }

                private static void displayRewardPointRecord(ArrayList<RewardPoint> rewardPointRecordList, String arrange) {
                    System.out.println();
                    for (int i = 0; i < 32; i++) {
                        System.out.print(" ");
                    }
                    System.out.println("~ Reward Point Records ~");
                    for (int i = 0; i < 89; i++) {
                        System.out.print("_");
                    }
                    if (rewardPointRecordList.size() > 0) {
                        System.out.print("\n");
                        System.out.printf("| %-10s  %-15s  %-15s  %-15s %-22s  |\n", "Record ID", "Customer ID", "Staff ID", "Point Earned", "Date");
                        System.out.print("|");
                        for (int i = 0; i < 87; i++) {
                            System.out.print("-");
                        }
                        System.out.print("|\n");
                        if (arrange.equals("ASC")) {
                            for (int i = 0; i < rewardPointRecordList.size(); i++) {
                                String rewardPointDate = (rewardPointRecordList.get(i).getEarnDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                System.out.printf("| %-10s  %-15s  %-15s  %-15d %-22s  |\n", rewardPointRecordList.get(i).getRewardPtID(), rewardPointRecordList.get(i).getCustomer().getUserID(), rewardPointRecordList.get(i).getStaff().getUserID(), rewardPointRecordList.get(i).getPtEarned(), rewardPointDate);
                            }
                        } else {
                            for (int i = (rewardPointRecordList.size() - 1); i >= 0; i--) {
                                String rewardPointDate = (rewardPointRecordList.get(i).getEarnDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                System.out.printf("| %-10s  %-15s  %-15s  %-15d %-22s  |\n", rewardPointRecordList.get(i).getRewardPtID(), rewardPointRecordList.get(i).getCustomer().getUserID(), rewardPointRecordList.get(i).getStaff().getUserID(), rewardPointRecordList.get(i).getPtEarned(), rewardPointDate);
                            }
                        }
                        System.out.print("|");
                        for (int i = 0; i < 87; i++) {
                            System.out.print("_");
                        }
                        System.out.print("|\n");
                        System.out.println(RewardPoint.getNumOfRewardPoint() + " reward point record(s) found.");
                    } else {
                        System.out.println("No records found.");
                    }
                }

                private static void displayEventRoomResRecord(ArrayList<EventRoomReservation> evtRoomResList, String arrange) {
                    System.out.println();
                    for (int i = 0; i < 38; i++) {
                        System.out.print(" ");
                    }
                    System.out.println("~ Event Room Reservation Records ~");
                    for (int i = 0; i < 111; i++) {
                        System.out.print("_");
                    }
                    if (evtRoomResList.size() > 0) {
                        System.out.print("\n");
                        System.out.printf("| %-10s  %-15s  %-20s  %-8s  %-12s  %-15s  %-15s |\n", "Record ID", "Room Num.", "Date", "U/P", "Extra Pax", "Pt Redeemed", "CustomerID");
                        System.out.print("|");
                        for (int i = 0; i < 109; i++) {
                            System.out.print("-");
                        }
                        System.out.print("|\n");
                        if (arrange.equals("ASC")) {
                            for (int i = 0; i < evtRoomResList.size(); i++) {

                                if (evtRoomResList.get(i).getReward() instanceof EventRoom && evtRoomResList.get(i) != null) {
                                    String reservationDate = (((EventRoomReservation) evtRoomResList.get(i)).getReservationDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                    System.out.printf("| %-10s  %-15s  %-20s  %-8d  %-12d  %-15d  %-15s |\n", evtRoomResList.get(i).getRecordID(), ((EventRoom) evtRoomResList.get(i).getReward()).getRoomNum(), reservationDate, evtRoomResList.get(i).getReward().getPtRequired(), ((EventRoomReservation) evtRoomResList.get(i)).getNumOfExtraPax(), ((EventRoomReservation) evtRoomResList.get(i)).calcPtRedeemed(), evtRoomResList.get(i).getCustomer().getUserID());
                                }

                            }
                        } else {
                            for (int i = (evtRoomResList.size() - 1); i >= 0; i--) {

                                if (evtRoomResList.get(i).getReward() instanceof EventRoom && evtRoomResList.get(i) != null) {
                                    String reservationDate = (((EventRoomReservation) evtRoomResList.get(i)).getReservationDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                    System.out.printf("| %-10s  %-15s  %-20s  %-8d  %-12d  %-15d  %-15s |\n", evtRoomResList.get(i).getRecordID(), ((EventRoom) evtRoomResList.get(i).getReward()).getRoomNum(), reservationDate, evtRoomResList.get(i).getReward().getPtRequired(), ((EventRoomReservation) evtRoomResList.get(i)).getNumOfExtraPax(), ((EventRoomReservation) evtRoomResList.get(i)).calcPtRedeemed(), evtRoomResList.get(i).getCustomer().getUserID());
                                }

                            }
                        }
                        System.out.print("|");
                        for (int i = 0; i < 109; i++) {
                            System.out.print("_");
                        }
                        System.out.print("|\n");
                        System.out.println(EventRoomReservation.getNumOfReservation() + " reservation record(s) found.");
                    } else {
                        System.out.println("No records found.");
                    }
                }

                //Redemption Record
                private static void actionOnRedemptionRecord(ArrayList<ItemRedemption> redemptionRecordList) {
                    displayRedemptionRecord(redemptionRecordList, "ASC");
                    String arrange = "ASC";
                    while (true) {
                        System.out.println("\nItem Redemption Record");
                        System.out.println("1. Arrange in Ascending Order");
                        System.out.println("2. Arrange in Descending Order");
                        System.out.println("3. Delete Record");
                        System.out.println("4. Back to Record Management Menu");
                        int num = checkActionRedemption();
                        if (num == 4) {
                            break;
                        } else if (num == 1) {
                            arrange = "ASC";
                            System.out.println("Records arranged in ascending order successfully!");
                            GeneralFunction.enterToContinue();
                            GeneralFunction.clearScreen();
                        } else if (num == 2) {
                            arrange = "DESC";
                            System.out.println("Records arranged in descending order successfully!");
                            GeneralFunction.enterToContinue();
                            GeneralFunction.clearScreen();
                        } else {
                            delRedemptionRecord(redemptionRecordList);
                            GeneralFunction.clearScreen();
                        }
                        displayRedemptionRecord(redemptionRecordList, arrange);
                    }

                }

                private static int checkActionRedemption() {
                    int num = 0;
                    do {
                        try {
                            System.out.print("Enter action: ");
                            Scanner input = new Scanner(System.in);
                            num = Integer.parseInt(input.nextLine());
                            if ((num != 1) && (num != 2) && (num != 3) && (num != 4)) {
                                System.out.println("Sorry! The actions that are available are only 1, 2, 3 and 4. Please select from the selection.");
                            }
                        } catch (Exception ex) {
                            System.out.println("Sorry! The action you entered is invalid. Please enter a valid number.");
                        }
                    } while ((num != 1) && (num != 2) && (num != 3) && (num != 4));
                    return num;
                }

                private static void delRedemptionRecord(ArrayList<ItemRedemption> redemptionRecordList) {
                    //Enter a valid record id
                    boolean validRecordID = false;
                    String selectedRecordID;
                    do {
                        System.out.print("Enter Record ID to be deleted (or 'X' to exit): ");
                        Scanner input = new Scanner(System.in);
                        selectedRecordID = input.nextLine();
                        if (selectedRecordID.equals("X")) {
                            break;
                        }
                        for (int i = 0; i < redemptionRecordList.size(); i++) {
                            if (redemptionRecordList.get(i).getRecordID().equals(selectedRecordID)) {
                                validRecordID = true;
                                //del from sql
                                SaveLoadData.SLDRecord.delRedemptionRecordSQL(selectedRecordID);
                                //del from array
                                redemptionRecordList.remove(i);
                                ItemRedemption.setNumOfItemRedemption(ItemRedemption.getNumOfItemRedemption() - 1);
                                //display successful message
                                System.out.println("Record " + selectedRecordID + " deleted successfully!");
                                GeneralFunction.enterToContinue();
                            }
                        }
                        if (validRecordID == false) {
                            System.out.println("Sorry! The Record ID you entered is invalid. Please enter a valid number.");
                        }
                    } while (validRecordID == false);
                }

                //Reward Point Record
                private static void actionOnRewardPointRecord(ArrayList<RewardPoint> rewardPointRecordList) {
                    displayRewardPointRecord(rewardPointRecordList, "ASC");
                    String arrange = "ASC";
                    while (true) {
                        System.out.println("\nReward Point Record");
                        System.out.println("1. Arrange in Ascending Order");
                        System.out.println("2. Arrange in Descending Order");
                        System.out.println("3. Delete Record");
                        System.out.println("4. Back to Record Management Menu");
                        int num = checkActionRewardPointRecord();
                        if (num == 4) {
                            break;
                        } else if (num == 1) {
                            arrange = "ASC";
                            System.out.println("Records arranged in ascending order successfully!");
                            GeneralFunction.enterToContinue();
                            GeneralFunction.clearScreen();
                        } else if (num == 2) {
                            arrange = "DESC";
                            System.out.println("Records arranged in descending order successfully!");
                            GeneralFunction.enterToContinue();
                            GeneralFunction.clearScreen();
                        } else {
                            delRewardPointRecord(rewardPointRecordList);
                            GeneralFunction.clearScreen();
                        }
                        displayRewardPointRecord(rewardPointRecordList, arrange);
                    }

                }

                private static int checkActionRewardPointRecord() {
                    int num = 0;
                    do {
                        try {
                            System.out.print("Enter action: ");
                            Scanner input = new Scanner(System.in);
                            num = Integer.parseInt(input.nextLine());
                            if ((num != 1) && (num != 2) && (num != 3) && (num != 4)) {
                                System.out.println("Sorry! The actions that are available are only 1, 2, 3 and 4. Please select from the selection.");
                            }
                        } catch (Exception ex) {
                            System.out.println("Sorry! The action you entered is invalid. Please enter a valid number.");
                        }
                    } while ((num != 1) && (num != 2) && (num != 3) && (num != 4));
                    return num;
                }

                private static void delRewardPointRecord(ArrayList<RewardPoint> rewardPointRecordList) {
                    //Enter a valid record id
                    boolean validRecordID = false;
                    String selectedRecordID;
                    do {
                        System.out.print("Enter Record ID to be deleted (or 'X' to exit): ");
                        Scanner input = new Scanner(System.in);
                        selectedRecordID = input.nextLine();
                        if (selectedRecordID.equals("X")) {
                            break;
                        }
                        for (int i = 0; i < rewardPointRecordList.size(); i++) {
                            if (rewardPointRecordList.get(i).getRewardPtID().equals(selectedRecordID)) {
                                validRecordID = true;
                                //del from sql
                                SaveLoadData.SLDRecord.delRewardPointRecordSQL(selectedRecordID);
                                //del from array
                                rewardPointRecordList.remove(i);
                                RewardPoint.setNumOfRewardPoint(RewardPoint.getNumOfRewardPoint() - 1);
                                //display successful message
                                System.out.println("Record " + selectedRecordID + " deleted successfully!");
                                GeneralFunction.enterToContinue();
                            }
                        }
                        if (validRecordID == false) {
                            System.out.println("Sorry! The Record ID you entered is invalid. Please enter a valid number.");
                        }
                    } while (validRecordID == false);
                }

                //Event Room Record
                private static void actionOnEventRoomResRecord(ArrayList<EventRoomReservation> evtRoomResList) {
                    displayEventRoomResRecord(evtRoomResList, "ASC");
                    String arrange = "ASC";
                    while (true) {
                        System.out.println("\nEvent Room Reservation Record");
                        System.out.println("1. Arrange in Ascending Order");
                        System.out.println("2. Arrange in Descending Order");
                        System.out.println("3. Delete Record");
                        System.out.println("4. Back to Record Management Menu");
                        int num = checkActionEventRoomResRecord();
                        if (num == 4) {
                            break;
                        } else if (num == 1) {
                            arrange = "ASC";
                            System.out.println("Records arranged in ascending order successfully!");
                            GeneralFunction.enterToContinue();
                            GeneralFunction.clearScreen();
                        } else if (num == 2) {
                            arrange = "DESC";
                            System.out.println("Records arranged in descending order successfully!");
                            GeneralFunction.enterToContinue();
                            GeneralFunction.clearScreen();
                        } else {
                            delEventRoomResRecord(evtRoomResList);
                            GeneralFunction.clearScreen();
                        }
                        displayEventRoomResRecord(evtRoomResList, arrange);
                    }

                }

                private static int checkActionEventRoomResRecord() {
                    int num = 0;
                    do {
                        try {
                            System.out.print("Enter action: ");
                            Scanner input = new Scanner(System.in);
                            num = Integer.parseInt(input.nextLine());
                            if ((num != 1) && (num != 2) && (num != 3) && (num != 4)) {
                                System.out.println("Sorry! The actions that are available are only 1, 2, 3 and 4. Please select from the selection.");
                            }
                        } catch (Exception ex) {
                            System.out.println("Sorry! The action you entered is invalid. Please enter a valid number.");
                        }
                    } while ((num != 1) && (num != 2) && (num != 3) && (num != 4));
                    return num;
                }

                private static void delEventRoomResRecord(ArrayList<EventRoomReservation> evtRoomResList) {
                    //Enter a valid record id
                    boolean validRecordID = false;
                    String selectedRecordID;
                    do {
                        System.out.print("Enter Record ID to be deleted (or 'X' to exit): ");
                        Scanner input = new Scanner(System.in);
                        selectedRecordID = input.nextLine();
                        if (selectedRecordID.equals("X")) {
                            break;
                        }
                        for (int i = 0; i < evtRoomResList.size(); i++) {
                            if (evtRoomResList.get(i).getRecordID().equals(selectedRecordID)) {
                                validRecordID = true;
                                //del from sql
                                SaveLoadData.SLDRecord.delEventRoomResRecordSQL(selectedRecordID);
                                //del from array
                                evtRoomResList.remove(i);
                                EventRoomReservation.setNumOfReservation(EventRoomReservation.getNumOfReservation() - 1);
                                //display successful message
                                System.out.println("Record " + selectedRecordID + " deleted successfully!");
                                GeneralFunction.enterToContinue();
                            }
                        }
                        if (validRecordID == false) {
                            System.out.println("Sorry! The Record ID you entered is invalid. Please enter a valid number.");
                        }
                    } while (validRecordID == false);
                }

                //Overall
                private static void actionOnPtRedemptionRecord(PointRedemption[] ptRedemptionRecordList) {
                    displayPtRedemptionRecord(ptRedemptionRecordList, "ASC");
                    String arrange = "ASC";
                    while (true) {
                        System.out.println("\nOverall Point Redemption Record");
                        System.out.println("1. Arrange in Ascending Order");
                        System.out.println("2. Arrange in Descending Order");
                        System.out.println("3. Back to Record Management Menu");
                        int num = checkActionPtRedemption();
                        if (num == 3) {
                            break;
                        } else if (num == 1) {
                            arrange = "ASC";
                            System.out.println("Records arranged in ascending order successfully!");
                            GeneralFunction.enterToContinue();
                            GeneralFunction.clearScreen();
                        } else {
                            arrange = "DESC";
                            System.out.println("Records arranged in descending order successfully!");
                            GeneralFunction.enterToContinue();
                            GeneralFunction.clearScreen();
                        }
                        displayPtRedemptionRecord(ptRedemptionRecordList, arrange);
                    }

                }

                private static int checkActionPtRedemption() {
                    int num = 0;
                    do {
                        try {
                            System.out.print("Enter action: ");
                            Scanner input = new Scanner(System.in);
                            num = Integer.parseInt(input.nextLine());
                            if ((num != 1) && (num != 2) && (num != 3)) {
                                System.out.println("Sorry! The actions that are available are only 1, 2 and 3. Please select from the selection.");
                            }
                        } catch (Exception ex) {
                            System.out.println("Sorry! The action you entered is invalid. Please enter a valid number.");
                        }
                    } while ((num != 1) && (num != 2) && (num != 3));
                    return num;
                }

                private static void displayPtRedemptionRecord(PointRedemption[] ptRedemptionRecordList, String arrange) {
                    System.out.println();
                    for (int i = 0; i < 54; i++) {
                        System.out.print(" ");
                    }
                    System.out.println("~ Overall Redemption Records ~");
                    System.out.println("\nItem Redemption Record");
                    for (int i = 0; i < 139; i++) {
                        System.out.print("_");
                    }
                    System.out.print("\n");
                    System.out.printf("| %-10s  %-15s  %-12s  %-30s  %-5s  %-5s  %-20s  %-23s  |\n", "Record ID", "Customer ID", "Item Code", "Item Name", "U/P", "Qty", "Total Pt Redeemed", "Date");
                    System.out.print("|");
                    for (int i = 0; i < 137; i++) {
                        System.out.print("-");
                    }
                    System.out.print("|\n");
                    if (arrange.equals("ASC")) {
                        for (int i = 0; i < ptRedemptionRecordList.length; i++) {
                            if (ptRedemptionRecordList[i] != null) {
                                if ((ptRedemptionRecordList[i].getReward()) instanceof RedeemableItem) {
                                    String redeemDate = (((ItemRedemption) ptRedemptionRecordList[i]).getRedeemDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                    System.out.printf("| %-10s  %-15s  %-12s  %-30s  %-5d  %-5d  %-20d  %-23s  |\n", ptRedemptionRecordList[i].getRecordID(), ptRedemptionRecordList[i].getCustomer().getUserID(), ((RedeemableItem) ptRedemptionRecordList[i].getReward()).getItemCode(), ptRedemptionRecordList[i].getReward().getRewardName(), ptRedemptionRecordList[i].getReward().getPtRequired(), ((ItemRedemption) ptRedemptionRecordList[i]).getRedeemedQtyThisRecord(), ((ItemRedemption) ptRedemptionRecordList[i]).calcPtRedeemed(), redeemDate);
                                }
                            }
                        }
                    } else {
                        for (int i = (ptRedemptionRecordList.length - 1); i >= 0; i--) {
                            if (ptRedemptionRecordList[i] != null) {
                                if ((ptRedemptionRecordList[i].getReward()) instanceof RedeemableItem && ptRedemptionRecordList[i] != null) {
                                    String redeemDate = (((ItemRedemption) ptRedemptionRecordList[i]).getRedeemDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                    System.out.printf("| %-10s  %-15s  %-12s  %-30s  %-5d  %-5d  %-20d  %-23s  |\n", ptRedemptionRecordList[i].getRecordID(), ptRedemptionRecordList[i].getCustomer().getUserID(), ((RedeemableItem) ptRedemptionRecordList[i].getReward()).getItemCode(), ptRedemptionRecordList[i].getReward().getRewardName(), ptRedemptionRecordList[i].getReward().getPtRequired(), ((ItemRedemption) ptRedemptionRecordList[i]).getRedeemedQtyThisRecord(), ((ItemRedemption) ptRedemptionRecordList[i]).calcPtRedeemed(), redeemDate);
                                }
                            }
                        }
                    }
                    System.out.print("|");
                    for (int i = 0; i < 137; i++) {
                        System.out.print("_");
                    }
                    System.out.print("|\n");

                    System.out.println("\nEvent Room Reservation Record");
                    for (int i = 0; i < 111; i++) {
                        System.out.print("_");
                    }
                    System.out.print("\n");
                    System.out.printf("| %-10s  %-15s  %-20s  %-8s  %-12s  %-15s  %-15s |\n", "Record ID", "Room Num.", "Date", "U/P", "Extra Pax", "Pt Redeemed", "CustomerID");
                    System.out.print("|");
                    for (int i = 0; i < 109; i++) {
                        System.out.print("-");
                    }
                    System.out.print("|\n");
                    if (arrange.equals("ASC")) {
                        for (int i = 0; i < ptRedemptionRecordList.length; i++) {
                            if (ptRedemptionRecordList[i] != null) {
                                if (ptRedemptionRecordList[i].getReward() instanceof EventRoom && ptRedemptionRecordList[i] != null) {
                                    String reservationDate = (((EventRoomReservation) ptRedemptionRecordList[i]).getReservationDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                    System.out.printf("| %-10s  %-15s  %-20s  %-8d  %-12d  %-15d  %-15s |\n", ptRedemptionRecordList[i].getRecordID(), ((EventRoom) ptRedemptionRecordList[i].getReward()).getRoomNum(), reservationDate, ptRedemptionRecordList[i].getReward().getPtRequired(), ((EventRoomReservation) ptRedemptionRecordList[i]).getNumOfExtraPax(), ((EventRoomReservation) ptRedemptionRecordList[i]).calcPtRedeemed(), ptRedemptionRecordList[i].getCustomer().getUserID());
                                }
                            }
                        }
                    } else {
                        for (int i = (ptRedemptionRecordList.length - 1); i >= 0; i--) {
                            if (ptRedemptionRecordList[i] != null) {
                                if (ptRedemptionRecordList[i].getReward() instanceof EventRoom && ptRedemptionRecordList[i] != null) {
                                    String reservationDate = (((EventRoomReservation) ptRedemptionRecordList[i]).getReservationDate()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                                    System.out.printf("| %-10s  %-15s  %-20s  %-8d  %-12d  %-15d  %-15s |\n", ptRedemptionRecordList[i].getRecordID(), ((EventRoom) ptRedemptionRecordList[i].getReward()).getRoomNum(), reservationDate, ptRedemptionRecordList[i].getReward().getPtRequired(), ((EventRoomReservation) ptRedemptionRecordList[i]).getNumOfExtraPax(), ((EventRoomReservation) ptRedemptionRecordList[i]).calcPtRedeemed(), ptRedemptionRecordList[i].getCustomer().getUserID());
                                }
                            }
                        }
                    }
                    System.out.print("|");
                    for (int i = 0; i < 109; i++) {
                        System.out.print("_");
                    }
                    System.out.print("|\n");
                    System.out.println(PointRedemption.getNumOfRedemption() + " point redemption record(s) found.");
                }

            }

            private static class TrendReport {

                private static void Handle() {
                    while (true) {
                        int action = displayTrendReportMenu();

                        if (action == 1) {
                            showMonthlyReport();
                            GeneralFunction.clearScreen();
                        } else if (action == 2) {
                            showYearlyReport();
                            GeneralFunction.clearScreen();
                        } else {
                            break;
                        }
                    }
                }

                public static int displayTrendReportMenu() {
                    System.out.println("\nTrend Report Menu");
                    System.out.println("1. Monthly Report");
                    System.out.println("2. Yearly Report");
                    System.out.println("3. Back");

                    System.out.println();

                    int action;

                    while (true) {
                        try {
                            Scanner input = new Scanner(System.in);

                            System.out.print("Enter action: ");

                            action = Integer.parseInt(input.nextLine());

                            if (action >= 1 && action <= 3) {
                                break;
                            } else {
                                System.out.println("Sorry! Invalid action. Please try again.");
                            }
                        } catch (Exception e) {
                            System.out.println("Sorry! Invalid input. Please try again.");
                        }

                        //BUG: https://stackoverflow.com/questions/9146257/why-do-system-err-statements-get-printed-first-sometimes
                        //Avoid use err.println
                        System.out.println();
                    }

                    return action;
                }

                public static void showMonthlyReport() {
                    LocalDate date = LocalDate.now();

                    System.out.println("\nItem Redemption Report from " + date.minusMonths(1).getMonth() + "-" + date.minusMonths(1).getYear() + " to " + date.getMonth() + "-" + date.getYear());

                    displayItemReport(SaveLoadData.SLDReport.getMonthlyPointRedemptionData());

                    GeneralFunction.enterToContinue();
                }

                public static void showYearlyReport() {
                    LocalDate date = LocalDate.now();

                    System.out.println("\nItem Redemption Report from " + date.minusYears(1).getMonth() + "-" + date.minusYears(1).getYear() + " to " + date.getMonth() + "-" + date.getYear());

                    displayItemReport(SaveLoadData.SLDReport.getYearlyPointRedemptionData());

                    GeneralFunction.enterToContinue();
                }

                public static void displayItemReport(ArrayList<RedeemableItem> itemData) {
                    String[] Headings = new String[]{"ItemCode", "Name", "Category", "Points Required", "Quantity", "Availability", "Min Tier", "Total Redeemed"};
                    for (int i = 0; i < 140; i++) {
                        System.out.print("_");
                    }
                    System.out.println();
                    System.out.print("|");
                    for (int i = 0; i < Headings.length; i++) {
                        if (i != 1) {
                            System.out.printf("%-15s ", Headings[i]);
                        } else {
                            System.out.printf("%-25s ", Headings[i]);
                        }
                    }
                    System.out.print("|");
                    System.out.println();

                    System.out.print("|");
                    for (int i = 0; i < 138; i++) {
                        System.out.print("-");
                    }
                    System.out.print("|");
                    System.out.println();

                    for (RedeemableItem item : itemData) {
                        System.out.print("|");
                        System.out.printf("%-15s ", item.getItemCode());
                        System.out.printf("%-25s ", item.getRewardName());
                        System.out.printf("%-15s ", item.getItemCategory());
                        System.out.printf("%-15s ", item.getPtRequired());
                        System.out.printf("%-15s ", item.getStockAmt());
                        System.out.printf("%-15s ", item.getAvailability());
                        System.out.printf("%-15s ", item.getMinimumTier());
                        System.out.printf("%-15s ", item.getRedeemedAmt());

                        System.out.print("|");
                        System.out.println();
                    }
                    System.out.print("|");
                    for (int i = 0; i < 138; i++) {
                        System.out.print("_");
                    }
                    System.out.print("|");
                    System.out.println();
                }
            }

            private static class RewardManagement {

                private static void Handle() {
                    while (true) {
                        int action = displayRewardManagementMenu();

                        if (action == 1) {
                            showAllItem();
                            GeneralFunction.clearScreen();
                        } else if (action == 2) {
                            addNewItem();
                            GeneralFunction.clearScreen();
                        } else if (action == 3) {
                            addItemQuantity();
                            GeneralFunction.clearScreen();
                        } else if (action == 4) {
                            modifyItemInfo();
                            GeneralFunction.clearScreen();
                        } else if (action == 5) {
                            modifyOnShelfStatus();
                            GeneralFunction.clearScreen();
                        } else {
                            break;
                        }
                    }
                }

                private static int displayRewardManagementMenu() {
                    System.out.println("\nReward Management Menu");
                    System.out.println("1. Show Item Status");
                    System.out.println("2. Add New Item");
                    System.out.println("3. Add Item Quantity");
                    System.out.println("4. Modify Item Information");
                    System.out.println("5. Modify On-shelf Status");
                    System.out.println("6. Back");

                    System.out.println();

                    int action;

                    while (true) {
                        try {
                            Scanner input = new Scanner(System.in);

                            System.out.print("Enter action: ");

                            action = Integer.parseInt(input.nextLine());

                            if (action >= 1 && action <= 6) {
                                break;
                            } else {
                                System.out.println("Sorry! Invalid action. Please try again.");
                            }
                        } catch (Exception e) {
                            System.out.println("Sorry! Invalid input. Please try again.");
                        }

                        //BUG: https://stackoverflow.com/questions/9146257/why-do-system-err-statements-get-printed-first-sometimes
                        //Avoid use err.println
                        System.out.println();
                    }

                    return action;
                }

                private static void showAllItem() {
                    System.out.println("Show all Item");

                    GeneralFunction.displayItemAllData(SaveLoadData.SLDPointAndItem.getAllItemSortStatusTierCategory());

                    GeneralFunction.enterToContinue();
                }

                private static void addNewItem() {
                    System.out.print("Add new redeemable item");
                    System.out.println();

                    String name = getStaffInput_ItemName();
                    String category = getStaffInput_ItemCategory();
                    int points = getStaffInput_PointRequired();
                    int quantity = getStaffInput_ItemQuantity("Sorry! Please enter valid item quantity.");
                    String tier = getStaffInput_MinimumTier();

                    //input.nextLine(); // clear buffer
                    System.out.println("Adding...");
                    SaveLoadData.SLDPointAndItem.addRedeemItem(name, category, points, quantity, tier);
                    System.out.println("Done");
                    System.out.println();

                    GeneralFunction.enterToContinue();
                }

                private static void addItemQuantity() {
                    ArrayList<RedeemableItem> ItemData = SaveLoadData.SLDPointAndItem.getAllItemExcludeDisable();

                    GeneralFunction.displayItemQuantityAndRelatedData(ItemData);

                    String itemCode = getStaffInput_ItemCode(ItemData);
                    int quantity = getStaffInput_ItemQuantity("Sorry! Please enter item quantity needed to add.");

                    System.out.println("Updating...");
                    SaveLoadData.SLDPointAndItem.addRedeemStock(itemCode, quantity);
                    System.out.println("Done");
                    System.out.println();

                    GeneralFunction.enterToContinue();
                }

                private static void modifyItemInfo() {
                    ArrayList<RedeemableItem> ItemData = SaveLoadData.SLDPointAndItem.getAllItemSortStatusTierCategory();

                    GeneralFunction.displayItemAllData(ItemData);

                    String itemCode = getStaffInput_ItemCode(ItemData);

                    GeneralFunction.displayItemAllData(SaveLoadData.SLDPointAndItem.getSpecificItem(itemCode));

                    System.out.println("Enter new information");
                    String name = getStaffInput_ItemName();
                    String category = getStaffInput_ItemCategory();
                    int points = getStaffInput_PointRequired();
                    String tier = getStaffInput_MinimumTier();

                    System.out.println("Updating...");
                    SaveLoadData.SLDPointAndItem.modifyRedeemItem(itemCode, name, category, points, tier);
                    System.out.println("Done");
                    System.out.println();

                    GeneralFunction.enterToContinue();
                }

                private static void modifyOnShelfStatus() {
                    ArrayList<RedeemableItem> ItemData = SaveLoadData.SLDPointAndItem.getAllItem();

                    GeneralFunction.displayItemOnShelfStatusAndRelatedData(ItemData);

                    String itemCode = getStaffInput_ItemCode(ItemData);

                    System.out.println("Updating...");
                    for (RedeemableItem item : ItemData) {
                        if (itemCode.equals(item.getItemCode())) {
                            SaveLoadData.SLDPointAndItem.disableRedeemItem(itemCode, !item.getAvailability());
                        }
                    }
                    System.out.println("Done");
                    System.out.println();

                    GeneralFunction.enterToContinue();
                }

                //region Custom function for Input
                public static String getStaffInput_ItemCode(ArrayList<RedeemableItem> ItemData) {
                    Scanner input = new Scanner(System.in);

                    String _itemCode;

                    while (true) {
                        System.out.print("Item Code: ");
                        _itemCode = input.nextLine();

                        boolean itemExist = false;

                        for (RedeemableItem item : ItemData) {
                            if (_itemCode.equals(item.getItemCode())) {
                                itemExist = true;
                                break;
                            }
                        }

                        if (itemExist) {
                            break;
                        } else {
                            System.out.println("Sorry! Please enter valid item code.");
                        }
                    }

                    return _itemCode;
                }

                public static String getStaffInput_ItemName() {
                    Scanner input = new Scanner(System.in);

                    String _name;

                    while (true) {
                        System.out.print("Item name: ");
                        _name = input.nextLine();

                        if (_name.isEmpty()) {
                            System.out.println("Sorry! Please enter item name.");
                            System.out.println();
                            continue;
                        }
                        break;
                    }

                    return _name;
                }

                public static String getStaffInput_ItemCategory() {
                    Scanner input = new Scanner(System.in);

                    String _category;

                    while (true) {
                        System.out.print("Item category (Food Voucher, Cash Voucher, Merchandise): ");
                        _category = input.nextLine();

                        if (_category.isEmpty() || (!_category.equals("Food Voucher") && !_category.equals("Cash Voucher") && !_category.equals("Merchandise"))) {
                            System.out.println("Sorry! Please enter valid item category.");
                            System.out.println();
                        } else {
                            break;
                        }
                    }

                    return _category;
                }

                public static int getStaffInput_PointRequired() {
                    int _points;

                    while (true) {
                        try {
                            System.out.print("Point Required: ");
                            Scanner input = new Scanner(System.in);
                            _points = input.nextInt();

                            if (_points <= 0) {
                                System.out.println("Sorry! Please enter required points to redeem item.");
                                System.out.println();
                                continue;
                            }
                            break;
                        } catch (Exception e) {
                            System.out.println("Sorry! Please enter positive natural integer.");
                        }
                    }

                    return _points;
                }

                public static int getStaffInput_ItemQuantity(String err_msg) {
                    int _quantity;

                    while (true) {
                        try {
                            Scanner input = new Scanner(System.in);

                            System.out.print("Item Quantity: ");
                            _quantity = input.nextInt();

                            if (_quantity <= 0) {
                                System.out.println(err_msg);
                                System.out.println();
                                continue;
                            }
                            break;
                        } catch (Exception e) {
                            System.out.println("Sorry! Please enter positive natural integer.");
                        }
                    }

                    return _quantity;
                }

                public static String getStaffInput_MinimumTier() {
                    Scanner input = new Scanner(System.in);

                    String _minTier;

                    while (true) {
                        System.out.print("Minimum Tier: ");
                        _minTier = input.nextLine();

                        if (!(_minTier.equals("Bronze") || _minTier.equals("Silver") || _minTier.equals("Gold"))) {
                            System.out.println("Sorry! Please enter valid minimum tier to redeem item.");
                            System.out.println();
                            continue;
                        }
                        break;
                    }

                    return _minTier;
                }

                //endregion
            }

            private static class UpdatePolicy {

                public static void updatePolicy() {
                    Scanner scanner = new Scanner(System.in);
                    while (true) {

                        System.out.println("\nChoose what information you want to update:");
                        System.out.println("1. Update Policy Title");
                        System.out.println("2. Update Policy Content");
                        System.out.println("3. Add new policy");
                        System.out.println("4. Delete Policy");
                        System.out.println("5. Back");
                        System.out.println();

                        int choice;

                        Scanner input = new Scanner(System.in);

                        try {
                            System.out.print("Enter choice: ");

                            choice = input.nextInt();

                            if (choice == 1) {
                                System.out.print("Enter policy index: ");
                                String policyIndex = scanner.next();

                                scanner.nextLine();

                                System.out.print("Enter new Policy Title: ");
                                String newTitle = scanner.nextLine();

                                SaveLoadData.SLDPolicy.changePolicyTitle(policyIndex, newTitle);
                                System.out.println("Policy Title updated successfully!\n");
                                GeneralFunction.enterToContinue();
                                GeneralFunction.clearScreen();
                            } else if (choice == 2) {
                                System.out.print("Enter policy index: ");
                                String policyIndex = scanner.next();

                                scanner.nextLine();

                                System.out.print("Enter new Policy Content: ");
                                String newContent = scanner.nextLine();

                                SaveLoadData.SLDPolicy.changePolicyContent(policyIndex, newContent);
                                System.out.println("Policy Content updated successfully!\n");
                                GeneralFunction.enterToContinue();
                                GeneralFunction.clearScreen();
                            } else if (choice == 3) {
                                System.out.print("Enter new policy index: ");
                                String addIndex = scanner.next();

                                scanner.nextLine();

                                System.out.print("Enter new policy title: ");
                                String addTitle = scanner.nextLine();

                                System.out.print("Enter new Policy Content: ");
                                String addContent = scanner.nextLine();

                                SaveLoadData.SLDPolicy.addPointsPolicy(addIndex, addTitle, addContent, staff.getUserID());
                                System.out.println("New policy added successfully!\n");
                                GeneralFunction.enterToContinue();
                                GeneralFunction.clearScreen();
                            } else if (choice == 4) {

                                System.out.print("Enter policy index: ");
                                String deleteIndex = scanner.next();

                                scanner.nextLine();

                                boolean isValidInput = false;
                                while (!isValidInput) {
                                    try {
                                        System.out.print("Confirm delete policy? (yes/no): ");
                                        String confirmation = scanner.next();

                                        if (Objects.equals(confirmation, "yes")) {
                                            SaveLoadData.SLDPolicy.deletePointsPolicy(deleteIndex);
                                            System.out.println("\nPolicy deleted successfully!\n");
                                            GeneralFunction.enterToContinue();
                                            GeneralFunction.clearScreen();
                                            isValidInput = true; // Set to true to exit the loop
                                        } else if (Objects.equals(confirmation, "no")) {
                                            GeneralFunction.clearScreen();
                                            isValidInput = true; // Set to true to exit the loop
                                        } else {
                                            System.out.println("Sorry! Invalid choice! Please try again.\n");
                                        }
                                    } catch (Exception ex) {
                                        System.out.println("Sorry! Invalid choice! Please try again.\n");
                                        scanner.nextLine(); // Consume the remaining input
                                    }
                                }
                            } else if (choice == 5) {
                                break;
                            } else {
                                System.out.println("Sorry! Invalid choice.");
                            }

                        } catch (Exception e) {
                            System.out.println("Sorry! Invalid choice. Please Try Again.");

                        }
                    }
                }
            }

            private static class UpdateCompanyDetails {

                public static void updateDetails() {
                    Scanner scanner = new Scanner(System.in);
                    while (true) {

                        System.out.println("\nChoose what information you want to update:");
                        System.out.println("1. Update Company Details Title");
                        System.out.println("2. Update Company Details Content");
                        System.out.println("3. Add Company Details");
                        System.out.println("4. Delete Company Details");
                        System.out.println("5. Back");
                        System.out.println();

                        int choice;

                        Scanner input = new Scanner(System.in);

                        try {
                            System.out.print("Enter choice: ");

                            choice = input.nextInt();

                            if (choice == 1) {
                                System.out.print("Enter Company Details Index: ");
                                String cdIndex = scanner.next();

                                scanner.nextLine();

                                System.out.print("Enter new Company Details Title: ");
                                String cdNewTitle = scanner.nextLine();

                                SaveLoadData.SLDContact.changeCompanyDetailsTitle(cdIndex, cdNewTitle);
                                System.out.println("Company Details Title updated successfully!\n");
                                GeneralFunction.enterToContinue();
                                GeneralFunction.clearScreen();
                            } else if (choice == 2) {
                                System.out.print("Enter Company Details Index: ");
                                String cdIndex = scanner.next();

                                scanner.nextLine();

                                System.out.print("Enter new Company Details Content: ");
                                String newCdContent = scanner.nextLine();

                                SaveLoadData.SLDContact.changeCompanyDetailsContent(cdIndex, newCdContent);
                                System.out.println("Company Details Content updated successfully!\n");
                                GeneralFunction.enterToContinue();
                                GeneralFunction.clearScreen();
                            } else if (choice == 3) {
                                System.out.print("Enter new Company Details Index: ");
                                String addCdIndex = scanner.next();

                                scanner.nextLine();

                                System.out.print("Enter new Company Details Title: ");
                                String addCdTitle = scanner.nextLine();

                                System.out.print("Enter new Company Details Content: ");
                                String addCdContent = scanner.nextLine();

                                SaveLoadData.SLDContact.addCompanyDetails(addCdIndex, addCdTitle, addCdContent, staff.getUserID());
                                System.out.println("New Company Details added successfully!\n");
                                GeneralFunction.enterToContinue();
                                GeneralFunction.clearScreen();
                            } else if (choice == 4) {

                                System.out.print("Enter Company Details index: ");
                                String deleteCdIndex = scanner.next();

                                scanner.nextLine();

                                boolean isValidInput = false;
                                while (!isValidInput) {
                                    try {
                                        System.out.print("Confirm delete Company Details? (yes/no): ");
                                        String confirmation = scanner.next();

                                        if (Objects.equals(confirmation, "yes")) {
                                            SaveLoadData.SLDContact.deleteCompanyDetails(deleteCdIndex);
                                            System.out.println("\nCompany Details deleted successfully!\n");
                                            GeneralFunction.enterToContinue();
                                            GeneralFunction.clearScreen();
                                            isValidInput = true; // Set to true to exit the loop
                                        } else if (Objects.equals(confirmation, "no")) {
                                            GeneralFunction.clearScreen();
                                            isValidInput = true; // Set to true to exit the loop
                                        } else {
                                            System.out.println("Sorry! Invalid choice! Please try again.\n");
                                        }
                                    } catch (Exception ex) {
                                        System.out.println("Sorry! Invalid choice! Please try again.\n");
                                        scanner.nextLine(); // Consume the remaining input
                                    }
                                }
                            } else if (choice == 5) {
                                break;
                            } else {
                                System.out.println("Sorry! Invalid choice.");
                            }

                        } catch (Exception e) {
                            System.out.println("Sorry! Invalid choice. Please Try Again.");

                        }
                    }
                }
            }

            private static void displayDashboard(String name) {
                System.out.println("Logged in as top management.");
                System.out.println("Welcome back, " + name);
                System.out.println();
            }

            private static int displayMainMenu() {
                System.out.println("\nHome Page Menu");
                System.out.println("1. Staff Management");
                System.out.println("2. Show Records");
                System.out.println("3. Trend Report");
                System.out.println("4. Reward Management");
                System.out.println("5. Update Points Policy");
                System.out.println("6. Update Company Details");
                System.out.println("7. Logout");

                System.out.println();

                int action;

                while (true) {
                    try {
                        Scanner input = new Scanner(System.in);

                        System.out.print("Enter action: ");

                        action = Integer.parseInt(input.nextLine());

                        if (action >= 1 && action <= 7) {
                            break;
                        } else {
                            System.out.println("Sorry! Invalid action. Please try again.");
                        }
                    } catch (Exception e) {
                        System.out.println("Sorry! Invalid input. Please try again.");
                    }

                    //BUG: https://stackoverflow.com/questions/9146257/why-do-system-err-statements-get-printed-first-sometimes
                    //Avoid use err.println
                    System.out.println();
                }

                return action;
            }
        }

        private static boolean confirmLogout() {
            Scanner input = new Scanner(System.in);

            while (true) {
                System.out.print("Are you sure? (Y/N): ");

                String confirm = input.nextLine();

                if (confirm.equalsIgnoreCase("y")) {
                    return true;
                } else if (confirm.equalsIgnoreCase("n")) {
                    return false;
                } else {
                    System.out.println("Sorry! Invalid input. Please try again.");
                }
            }
        }
    }
}