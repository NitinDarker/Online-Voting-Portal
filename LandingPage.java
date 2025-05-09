package CIA2;

import java.util.Objects;
import java.util.Scanner;

public class LandingPage {
    public static void createNewUser() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your email-Id: ");
        String email = sc.next();
        String regex = "^[a-zA-Z0-9_+&*-]+(:?\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!email.matches(regex)) {
            System.err.println("Please enter a valid email address!");
            return;
        }
        System.out.print("Enter your new username: ");
        String username = sc.next();
        System.out.print("Enter a new password: ");
        String password = sc.next();
        if (User.findUser(username) != null) {
            System.err.println("This username is already taken!");
            return;
        }
        User newUser;
        try {
            newUser = new User(email, username, password);
        } catch (IllegalArgumentException iae) {
            System.out.println("Invalid Credentials!");
            System.out.println(iae.getMessage());
            return;
        }
        newUser.saveUser();
        loggedIn(newUser);
    }

    public static void loginPage() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your username: ");
        String username = sc.next();
        System.out.print("Enter your password: ");
        String password = sc.next();
        User currentUser = User.findUser(username);
        if (currentUser == null) {
            System.out.println("User does not exist!");
            return;
        }
        if (!Objects.equals(currentUser.getPassword(), password)) {
            System.out.println("Invalid Password!");
            return;
        }
        loggedIn(currentUser);
    }

    public static void loggedIn(User user) {
        try {
            Thread.sleep(500);
            System.out.println("\nYou are logged in...");
            System.out.println("Welcome " + user.getUsername() + "!");
            Thread.sleep(500);
            VotingPage.mainPage(user);
        } catch (InterruptedException e) {
            System.out.println("Sorry! Something has occurred on our end!");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n\n***************************************");
        System.out.println("Welcome to the Online Voting portal!");

        while (true) {
            System.out.println("\n1. New User");
            System.out.println("2. Existing User");
            System.out.println("3. Exit");
            System.out.print("> ");

            while (!sc.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                System.out.print("> ");
                sc.next();
            }
            int choice1 = sc.nextInt();
            switch (choice1) {
                case 1 -> createNewUser();
                case 2 -> loginPage();
                case 3 -> {
                    System.out.println("Exiting the portal. Thank you!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid input!");
            }
        }
    }
}
