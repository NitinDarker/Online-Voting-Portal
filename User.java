package CIA2;

import java.io.*;
import java.util.UUID;

public class User {
    private final UUID userId;
    private final String username, email, password;
    private boolean hasVoted;
    private static final String USERS_FILE = "./users.txt";
    private static final String DELIMITER = ",";
    // USERS_FILE Format: UUID,email,username,password,hasVoted

    public User(String email, String username, String password) {
        if (email == null || email.trim().isEmpty() ||
                username == null || username.trim().isEmpty() ||
                password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Email, username, and password cannot be null or empty.");
        }
        this.userId = UUID.randomUUID();
        this.username = username.trim();
        this.email = email.trim();
        this.password = password; // Hash the password before storing
        this.hasVoted = false;
    }

    public User(UUID userId, String username, String email, String password, boolean hasVoted) {
        this.userId = userId;
        this.username = username.trim();
        this.email = email.trim();
        this.password = password;
        this.hasVoted = hasVoted;
    }

    public UUID getUserID() {return userId;}
    public String getUsername() {return username;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public boolean hasVoted() {return hasVoted;}
    public void changeVote() {this.hasVoted = true;}

    public void saveUser() {
        // File format: userId,username,password,email,hasVoted
        String userDataLine = userId + DELIMITER +
                email + DELIMITER +
                username + DELIMITER +
                password + DELIMITER +
                hasVoted;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE, true))) {
            writer.write(userDataLine);
            writer.newLine();
            System.out.println("User " + username + " saved successfully.");
        } catch (IOException e) {
            System.err.println("An error occurred while writing user data to " + USERS_FILE + ": " + e.getMessage());
        }
    }

    public static User findUser(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.err.println("Search username cannot be null or empty.");
            return null;
        }

        String searchUsername = username.trim();

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue; // Skip empty lines
                }
                try {
                    // Try to parse the line into a User object
                    User currentUser = User.fromFileString(line);
                    if (currentUser.getUsername().equalsIgnoreCase(searchUsername)) {
                        System.out.println("User found: " + currentUser.getUsername());
                        return currentUser; // Found the user, return the object
                    }
                } catch (IllegalArgumentException e) {
                     System.err.println("Skipping empty line during search.");
                }
            }
            System.out.println("User '" + username + "' not found.");
            return null;

        } catch (IOException e) {
            System.err.println("An error occurred while reading user data from " + USERS_FILE + ": " + e.getMessage());
            return null;
        }
    }

    private static User fromFileString(String line) throws IllegalArgumentException {
        // Converts to user object from a line from users.txt
        if (line == null || line.trim().isEmpty()) {
            throw new IllegalArgumentException("Cannot parse empty or null line.");
        }

        String[] parts = line.split(DELIMITER);

        try {
            UUID userId = UUID.fromString(parts[0]);
            String email = parts[1];
            String username = parts[2];
            String password = parts[3];
            boolean hasVoted = Boolean.parseBoolean(parts[4]);

            return new User(userId, username, email, password, hasVoted);

        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing line: " + line + " - " + e.getMessage());
            throw new IllegalArgumentException("Error parsing line data.", e);
        }
    }
}
