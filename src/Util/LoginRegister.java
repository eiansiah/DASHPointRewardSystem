package Util;

import java.sql.SQLException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class LoginRegister {

    private static String id;
    private static String password;
    private static String role;

    private static final String idFormat = "^[C|S]\\d{5}";
    private static int passwordLength = 8;

    public static String LoginRegisterController() {

        boolean login = false;

        System.out.println("\n1. Login");
        System.out.println("2. Register As Customer");
        System.out.println("3. Exit");

        System.out.println();

        int choice;

        while (true) {
            Scanner input = new Scanner(System.in);
            try {
                System.out.print("Enter choice: ");

                choice = Integer.parseInt(input.nextLine());

                switch (choice) {
                    case 1:
                        login = true;
                        break;
                    case 2:
                        login = false;
                        break;
                    case 3:
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Sorry! Invalid choice. Please try again.");
                        break;
                }

                if (choice >= 1 && choice <= 3) {
                    break;
                }
            } catch (Exception e) {
                System.out.println("Sorry! Invalid input. Please try again.");
            }
        }

        if (login) {
            role = LoginProcess();

            while (role.equals("false")) {
                System.out.println("Sorry! Invalid ID or password. Please try again.");
                System.out.println();

                role = LoginProcess();
            }
        } else {

            RegisterProcess();
            System.out.println("This is your User ID: " + id);
            System.out.println("Please remember it.");
            System.out.println();

            //Can add accept policy
        }

        GeneralFunction.clearScreen();

        return id;
    }

    public static String LoginProcess() {
        System.out.println();
        System.out.println("Welcome to Login Page");
        System.out.println("----------------------");

        getLoginInfo();

        System.out.println("Loading...");

        try {
            return SaveLoadData.SLDLoginRegister.loginValidation(id, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void getLoginInfo() {
        do {
            Scanner scan = new Scanner(System.in);

            System.out.print("ID: ");
            id = scan.nextLine();

            System.out.print("Password: ");
            password = scan.nextLine();

            System.out.println();
        } while (!checkFormat(id, password));
    }

    private static boolean checkFormat(String id, String password) {
        if (!Pattern.matches(idFormat, id)) {
            System.out.println("Sorry! Invalid User ID format. Please try again.");
            return false;
        } else if (password.length() < passwordLength) {
            System.out.println("Sorry! Your password must be at least 8 characters. Please try again.");
            return false;
        }

        return true;
    }

    private static void RegisterProcess() {
        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.println("Welcome to Registration Page");
        System.out.println("-----------------------------");

        String userName;
        do {
            System.out.print("Enter your name: ");
            userName = scanner.nextLine();
            if (!isValidInput(userName)) {
                System.out.println("Sorry! Empty field! Please enter a valid name.");
            } else if (!userName.matches("[a-zA-Z\\s]+")) {
                System.out.println("Sorry! Invalid name. Please enter a valid name containing only letters.");
            } else {
                break;
            }
        } while (true);

        String contactNumber;
        do {
            System.out.print("Enter your contact number: ");
            contactNumber = scanner.nextLine();
            if (!isValidInput(contactNumber)) {
                System.out.println("Sorry! Empty field! Please enter a valid contact number.");
            } else if (!isValidContactNumber(contactNumber)) {
                System.out.println("Sorry! Invalid contact number. Please enter a number starting with '01' and containing 10 to 11 digits.");
            }
        } while (!isValidContactNumber(contactNumber) || !isValidInput(contactNumber));

        String email;
        do {
            System.out.print("Enter your email: ");
            email = scanner.nextLine();
            if (!isValidInput(email)) {
                System.out.println("Sorry! Empty field! Please enter a valid email.");
            } else if (!isValidEmail(email)) {
                System.out.println("Sorry! Invalid email format. Please try again.");
            } else if (SaveLoadData.SLDLoginRegister.registerEmailDuplication(email)) {
                System.out.println("Sorry! Email is already used. Please enter another one.");
            } else {
                break;
            }
        } while (true);

        String password;
        do {
            System.out.print("Enter your password (minimum 8 characters): ");
            password = scanner.nextLine();

            if (!isValidPassword(password)) {
                System.out.println("Sorry! Password must have minimum 8 characters. Please try again.");
            }
        } while (!isValidPassword(password));

        String confirmPassword;
        do {
            System.out.print("Confirm your password: ");
            confirmPassword = scanner.nextLine();

            if (!password.equals(confirmPassword)) {
                System.out.println("Sorry! Passwords do not match. Please try again.");
            }
        } while (!password.equals(confirmPassword) || !isValidPassword(password));

        System.out.println();
        System.out.println("Name: " + userName);
        System.out.println("Contact Number: " + contactNumber);
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);

        System.out.println();

        System.out.println("Registered Successfully!");
        System.out.println("Creating new account...");
        System.out.println();
        id = SaveLoadData.SLDLoginRegister.registerEntry(userName, contactNumber, email, password);

    }

    // Email Validation
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }

        String emailRegex = "^(?:[a-zA-Z]+\\d*|[a-zA-Z]*)@[a-zA-Z]+\\.[a-zA-Z]+$";
        return email.matches(emailRegex);
    }

    // Check if contact number starts with 01
    public static boolean isValidContactNumber(String contactNumber) {
        return contactNumber.matches("^01\\d{8,9}$");
    }

    // Check if there are empty input
    public static boolean isValidInput(String input) {
        return input != null && !input.isEmpty();
    }

    // Check if password has minimum 8 characters
    public static boolean isValidPassword(String password) {
        return password.length() >= 8;
    }
}
