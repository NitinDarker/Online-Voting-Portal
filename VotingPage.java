package CIA2;

import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class VotingPage {
    private static User currentUser;
    private static final String CANDIDATES_FILE = "./candidates.txt";
    private static final String VOTES_FILE = "./votes.txt";
    private static final String USERS_FILE = "./users.txt";
    private static final LocalDate startDate = LocalDate.of(2025, 5, 1);
    private static final LocalDate endDate = LocalDate.of(2025, 5, 30);


    public static void mainPage(User currentUser) {
        VotingPage.currentUser = currentUser;
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nWhat do you want to do?");
            System.out.println("1. Display the list of candidates");
            System.out.println("2. Place your vote");
            System.out.println("3. Display the voting results");
            System.out.println("4. Exit");
            System.out.print("> ");

            while (!sc.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                System.out.print("> ");
                sc.next();
            }
            int choice2 = sc.nextInt();
            switch (choice2) {
                case 1 -> getCandidatesList();
                case 2 -> placeVote();
                case 3 -> getResults();
                case 4 -> {
                    System.out.println("Exiting the voting portal. Thank you!");
                    return;
                }
                default -> System.out.println("Invalid input!");
            }
        }
    }

    private static void getCandidatesList() {
        // Format: candidateID,name,party
        try (BufferedReader reader = new BufferedReader(new FileReader(CANDIDATES_FILE))) {
            System.out.println("\n***********************************************");
            System.out.printf("%-4s | %-20s | %-6s", "ID", "Candidate Name", "Party Name\n");
            System.out.println("------------------------------------------------");

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                   continue;
                }

                String[] parts = line.split(",");

                try {
                    if (parts.length < 3) {
                        System.err.println("Skipping malformed line in candidates file: " + line);
                        continue;
                    }
                    String candidateId = (parts[0].trim());
                    String candidate = parts[1];
                    String party = parts[2];

                    System.out.printf("%-4s | %-20s | %-15s\n", candidateId, candidate, party);
                    Thread.sleep(200);
                } catch (IllegalArgumentException e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                } catch (InterruptedException e) {
                    System.out.println("Current thread was interrupted!" + e.getMessage());
                    Thread.currentThread().interrupt();
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.err.println("Error parsing line (not enough parts): " + line + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading candidate data from " + CANDIDATES_FILE + ": " + e.getMessage());
            return;
        }
        System.out.println("******************************************\n");
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            System.out.println("Sorry! The current thread was interrupted!");
        }
        System.out.println("What would you like to do now?");
        System.out.println("1. Place your vote");
        System.out.println("2. Get voting results");
        System.out.println("3. Exit");
        System.out.print("> ");

        Scanner sc = new Scanner(System.in);
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input! Please enter a number.");
            System.out.print("> ");
            sc.next();
        }
        int choice3 = sc.nextInt();
        switch (choice3) {
            case 1 -> placeVote();
            case 2 -> getResults();
            case 3 -> {
                System.out.println("Exiting the voting portal. Thank you!");
                return;
            }
            default -> System.out.println("Invalid input!");
        }
    }

    private static void getResults() {
        LocalDate currentDate = LocalDate.now();
//        if (currentDate.isBefore(endDate)) {
//            System.out.println("Results will be visible after the Voting Period ends!");
//            return;
//        }
        Map<Integer, String> candidateNames = new HashMap<>();
        Map<Integer, String> candidateParties = new HashMap<>();
        Map<Integer, Integer> voteCounts = new HashMap<>();
        String[] winner = new String[3];
        int maxVotes = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(CANDIDATES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    try {
                        int candidateId = Integer.parseInt(parts[0].trim());
                        String name = parts[1].trim();
                        String party = parts[2].trim();
                        candidateNames.put(candidateId, name);
                        candidateParties.put(candidateId, party);
                        voteCounts.put(candidateId, 0); // Initialize vote count to 0
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing candidate ID in candidates file, line: \"" + line + "\" - " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed line in candidates file: \"" + line + "\" - Expected 3 parts (ID,Name,Party)");
                }
            }
        } catch (IOException e) {
            System.err.println("An error occurred while reading candidate data from " + CANDIDATES_FILE + ": " + e.getMessage());
            System.out.println("Cannot display results without candidate data.");
            return;
        }

        if (candidateNames.isEmpty()) {
            System.out.println("\nNo candidates found in the system. Cannot display results.");
            return;
        }

        // Votes file format: userid,candidateId,time
        try (BufferedReader reader = new BufferedReader(new FileReader(VOTES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    try {
                        int candidateId = Integer.parseInt(parts[1].trim());
                        if (voteCounts.containsKey(candidateId)) {
                            int votes = voteCounts.get(candidateId) + 1;
                            voteCounts.put(candidateId, votes);
                            if (votes > maxVotes) {
                                maxVotes = votes;
                                winner[0] = Integer.toBinaryString(candidateId);
                                winner[1] = candidateNames.get(candidateId);
                                winner[2] = candidateParties.get(candidateId);
                            }
                        } else {
                            System.err.println("Warning: Vote recorded for an unlisted candidate ID: " + candidateId + " in votes file, line: \"" + line + "\"");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing candidate ID in votes file, line: \"" + line + "\" - " + e.getMessage());
                    }
                } else {
                    System.err.println("Skipping malformed line in votes file: \"" + line + "\" - Expected at least 2 parts (UserID,CandidateID)");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("No votes have been cast yet or votes file ("+VOTES_FILE+") not found. Displaying candidates with 0 votes.");
        }
        catch (IOException e) {
            System.err.println("An error occurred while reading votes data from " + VOTES_FILE + ": " + e.getMessage());
            System.out.println("Results may be incomplete.");
        }

        System.out.println("\n*****************************************");
        System.out.println("            VOTING RESULTS             ");
        System.out.println("*****************************************");
        System.out.printf("%-4s | %-20s | %-15s | %-5s%n", "ID", "Candidate Name", "Party Name", "Votes");
        System.out.println("--------------------------------------------------");

        List<Integer> sortedCandidateIds = new ArrayList<>(candidateNames.keySet());
        Collections.sort(sortedCandidateIds); // Sort by ID

        for (int candidateId : sortedCandidateIds) {
            String name = candidateNames.getOrDefault(candidateId, "N/A");
            String party = candidateParties.getOrDefault(candidateId, "N/A");
            int votes = voteCounts.getOrDefault(candidateId, 0);

            System.out.printf("%-4d | %-20s | %-15s | %-5d%n", candidateId, name, party, votes);
            try {
                Thread.sleep(200); // Small delay for readability
            } catch (InterruptedException e) {
                System.err.println("Display sleep interrupted.");
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("\nWinner: ");
        System.out.printf("%-4s | %-20s | %-15s | %-5d%n", winner[0], winner[1], winner[2], maxVotes);
        System.out.println("*****************************************\n");

        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            System.out.println("Sorry! Something has occurred on our end!");
            Thread.currentThread().interrupt();
        }

        System.out.println("What would you like to do now?");
        System.out.println("1. Display list of candidates");
        System.out.println("2. Place your vote");
        System.out.println("3. Return to Main Menu");
        System.out.print("> ");

        Scanner sc_results = new Scanner(System.in);
        while (!sc_results.hasNextInt()) {
            System.out.println("Invalid input! Please enter a number.");
            System.out.print("> ");
            sc_results.next();
        }
        int choice = sc_results.nextInt();
        sc_results.nextLine();

        switch (choice) {
            case 1 -> getCandidatesList();
            case 2 -> placeVote();
            case 3 -> {
                System.out.println("Returning to main menu...");
                return;
            }
            default -> {
                System.out.println("Invalid input! Returning to main menu.");
                return;
            }
        }
    }

    private static void placeVote() {
        LocalDate currentDate = LocalDate.now();
//        if (currentDate.isBefore(startDate)) {
//            System.out.println("Voting period is not started yet!");
//        }
//        if (currentDate.isAfter(endDate)) {
//            System.out.println("Voting Period has ended!");
//            return;
//        }

        Scanner sc = new Scanner(System.in);
        if (currentUser.hasVoted()) {
            System.out.println("You have already voted.");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("Sorry! Something has occurred on our end!");
            }
            return;
        }

        System.out.println("Enter ID of the candidate you would like to vote for: ");
        System.out.print("> ");

        while (!sc.hasNextInt()) {
            System.out.println("Invalid input! Please enter a number.");
            System.out.print("> ");
            sc.next();
        }
        int candidateID = sc.nextInt();
        if (!isValidCandidate(candidateID)) {
            System.out.println("Error: Candidate with ID " + candidateID + " not found.");
            System.out.println("Your vote was not recorded.");
            return;
        }

        long timestamp = Instant.now().toEpochMilli();

        String voteRecord = currentUser.getUserID() + "," + candidateID + "," + timestamp;

        try (BufferedWriter voteWriter = new BufferedWriter(new FileWriter(VOTES_FILE, true))) {
            voteWriter.write(voteRecord);
            voteWriter.newLine();
            System.out.println("Your vote for candidate " + candidateID + " has been successfully recorded.");
            Thread.sleep(1000);
        } catch (IOException e) {
            System.err.println("Error writing vote to votes file: " + e.getMessage());
            System.out.println("Your vote could not be fully processed. Please contact support.");
            return;
        } catch (InterruptedException e) {
            System.out.println("Something interrupted the server!");
        }

        currentUser.changeVote();
        updateUserFileStatus();

        System.out.println("Your voting status has been updated.\n");
        System.out.println("What would you like to do now?");
        System.out.println("1. Get Results");
        System.out.println("2. Exit");
        System.out.print("> ");
        int choice5 = sc.nextInt();
        switch (choice5) {
            case 1 -> getResults();
            case 2 -> {
                System.out.println("Exiting the voting portal. Thank you!");
                return;
            }
        }
    }

    private static boolean isValidCandidate(int candidateID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CANDIDATES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                String[] parts = line.split(",");
                try {
                    int fileCandidateID = Integer.parseInt(parts[0].trim());
                    if (fileCandidateID == candidateID) {
                        return true;
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing line");
                }
            }
        } catch (IOException e) {
            System.err.println("Error: Candidates file not found: " + CANDIDATES_FILE);
            System.err.println("Cannot validate candidate IDs.");
        }
        return false;
    }

    // Updates the users.txt file
    private static void updateUserFileStatus() {
        List<String> fileLines = new ArrayList<>();
        boolean userFoundAndUpdated = false;

        try (BufferedReader fileReader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = fileReader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }

                // To check if this line contains our user
                String[] parts = line.split(","); // Get the current user
                UUID userId = UUID.fromString(parts[0]); // parse the uuid
                if (userId.equals(currentUser.getUserID())) {
                    String updatedLine = parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3] + "," + true;
                    fileLines.add(updatedLine);
                    userFoundAndUpdated = true;
                } else {
                    fileLines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: User file not found: " + USERS_FILE);
            System.err.println("Cannot update user voting status in the file.");
            return;
        } catch (IOException e) {
            System.err.println("Error reading user file: " + e.getMessage());
            System.err.println("Cannot update user voting status in the file.");
            return;
        }

        // If the user was found and their line updated in memory, write all lines back to the file
        if (userFoundAndUpdated) {
            try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(USERS_FILE, false))) {
                for (String line : fileLines) {
                    fileWriter.write(line);
                    fileWriter.newLine();
                }
                System.out.println("User file successfully updated with new voting status.");
            } catch (IOException e) {
                System.err.println("Error writing updated user file: " + e.getMessage());
                System.out.println("Warning: Your voting status might not be permanently saved due to a file error.");
            }
        } else {
            System.err.println("User with username '" + currentUser.getUsername() + "' who just voted was not found in " + USERS_FILE);
            System.err.println("Voting status not updated in file.");
        }
    }
}



