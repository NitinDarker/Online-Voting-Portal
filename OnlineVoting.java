package CIA2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;

public class OnlineVoting {
    static User user;

    public static void votingPage(User currentUser) {
        user = currentUser;
        while (true) {
            System.out.println("\nWhat do you want to do?");
            System.out.println("1. Display the list of candidates");
            System.out.println("2. Place your vote");
            System.out.println("3. Display the voting results");
            System.out.println("3. Exit");
            System.out.print("> ");

            Scanner sc = new Scanner(System.in);
            while (!sc.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                System.out.print("> ");
                sc.next();
            }
            int choice2 = sc.nextInt();
            switch(choice2) {
                case 1 -> getCandidatesList();
                case 2 -> placeVote();
                case 3 -> getResults();
                case 4 -> {
                    System.out.println("Exiting the voting portal. Thank you!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid input!");
            }
        }
    }

    private static void getCandidatesList() {
        // Format: candidateID,name,party
        String CANDIDATES_FILE = "./CIA2/candidates.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(CANDIDATES_FILE))) {
            System.out.println(" ID |   Candidate Name  |  Party Name");

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    throw new IllegalArgumentException("Cannot parse empty line.");
                }

                String[] parts = line.split(",");

                try {
                    UUID candidateId = UUID.fromString(parts[0]);
                    String candidate = parts[1];
                    String party = parts[2];

                    System.out.println(candidateId + "  " + candidate + "  " + party);
                } catch (IllegalArgumentException e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading candidate data from " + CANDIDATES_FILE + ": " + e.getMessage());
            return;
        }
        System.out.println("What would you like to do now?");
        System.out.println("1. Place your vote");
        System.out.println("2. Get voting results");
        System.out.println("3. Exit");
    }

    private static void getResults() {

    }

    private static void placeVote() {

    }
}
