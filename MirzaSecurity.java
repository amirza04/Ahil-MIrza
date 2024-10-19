package application;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class MirzaSecurity {

    // Example malware hashes (MD5). In practice, load this from a database or file.
    private static final Set<String> KNOWN_MALWARE_HASHES = new HashSet<>();

    static {
        // Simulated known malware hashes
        KNOWN_MALWARE_HASHES.add("e99a18c428cb38d5f260853678922e03"); // Example hash
        KNOWN_MALWARE_HASHES.add("9e107d9d372bb6826bd81d3542a419d6"); // Another example hash
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to Mirza Security - Basic Malware Protection");
        System.out.println("1. Scan Directory for Malware");
        System.out.println("2. Monitor Directory for Changes");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline left-over

        if (choice == 1) {
            System.out.println("Enter the directory path to scan:");
            String directoryPath = scanner.nextLine();
            File directory = new File(directoryPath);
            if (directory.isDirectory()) {
                scanDirectory(directory);
            } else {
                System.out.println("Invalid directory path.");
            }
        } else if (choice == 2) {
            System.out.println("Enter the directory path to monitor:");
            String directoryPath = scanner.nextLine();
            monitorDirectory(directoryPath);
        } else {
            System.out.println("Invalid choice.");
        }
    }

    // Method to scan a directory for potential malware based on known hashes
    private static void scanDirectory(File directory) {
        try {
            Files.walkFileTree(directory.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        String hash = calculateFileHash(file.toFile());
                        if (KNOWN_MALWARE_HASHES.contains(hash)) {
                            System.out.println("WARNING: Malware detected in file: " + file.getFileName());
                        } else {
                            System.out.println("File safe: " + file.getFileName());
                        }
                    } catch (Exception e) {
                        System.out.println("Error scanning file: " + file.getFileName());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            System.out.println("Error scanning directory.");
            e.printStackTrace();
        }
    }

    // Method to calculate the MD5 hash of a file
    private static String calculateFileHash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] hashBytes = digest.digest(fileBytes);

        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // Method to monitor a directory for file changes
    private static void monitorDirectory(String pathStr) {
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(pathStr);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("Monitoring directory: " + pathStr);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    System.out.println("Event kind: " + kind + ". File affected: " + event.context());
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
