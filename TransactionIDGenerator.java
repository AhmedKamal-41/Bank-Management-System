package application;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.UUID;

public class TransactionIDGenerator {

    public static String generateTransactionID(String accountNumber) {
        String uniqueString = accountNumber + Instant.now().toString() + UUID.randomUUID();
        return hashString(uniqueString);
    }

    private static String hashString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                hexString.append(String.format("%02x", b));
            }
            // Return only the first 16 characters of the hash for a shorter ID
            return hexString.substring(0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating transaction ID", e);
        }
    }

    public static void main(String[] args) {
        String accountNumber = "12345678";
        String transactionID = generateTransactionID(accountNumber);
        System.out.println("Generated Transaction ID: " + transactionID);
    }
}
