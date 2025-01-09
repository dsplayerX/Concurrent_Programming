import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

public class FloorBathroom {

    private static final int NUM_STALLS = 6;  // Number of bathroom stalls
    private static final int NUM_EMPLOYEES = 100; // Total number of users
    private static final Semaphore stallAccess = new Semaphore(NUM_STALLS, true); // to manage stall access
    private static final ConcurrentHashMap<Integer, Boolean> availableStalls = new ConcurrentHashMap<>();
    // to track which stalls are available (true = available, false = occupied)

    static class BathroomUser implements Runnable {

        @Override
        public void run() {
            String threadName = Thread.currentThread().getName();
            int stallId = -1;

            try {
                System.out.println(threadName + " is waiting for a stall.");
                stallAccess.acquire(); // Try to acquire a stall

                // Find and lock an available stall
                for (int i = 0; i < NUM_STALLS; i++) {
                    if (availableStalls.get(i)) { // Check if the stall is available
                        stallId = i;
                        availableStalls.put(i, false); // Mark stall as occupied
                        System.out.println(threadName + " has entered stall " + (stallId + 1) + ".");
                        break;
                    }
                }

                displayAvailableStalls(); // Display the status of available stalls

                useBathroomStall(); // Simulate using the bathroom

            } catch (InterruptedException e) {
                System.out.println(threadName + " was interrupted.");
            } finally {
                if (stallId != -1) {
                    // Release the stall
                    availableStalls.put(stallId, true); // Mark stall as available
                    System.out.println(threadName + " has left stall " + (stallId + 1) + ".");
                    displayAvailableStalls(); // Display the status of available stalls
                }
                stallAccess.release(); // Release the semaphore for others
            }
        }

        private void useBathroomStall() {
            Random rand = new Random();
            try {
                Thread.sleep(rand.nextInt(3000) + 2000); // Simulate using the stall for 2-5 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void displayAvailableStalls() {
        StringBuilder status = new StringBuilder();
        availableStalls.forEach((stallId, isAvailable) -> {
            if (isAvailable) {
                status.append(stallId + 1).append(" "); // Stall IDs are 1-indexed for user readability
            }
        });
        System.out.println("Available stalls: " + status.toString().trim());
    }

    public static void main(String[] args) {

        // Validate number of stalls and users
        if (NUM_STALLS <= 0) {
            throw new IllegalArgumentException("Number of stalls must be greater than 0. Provided: " + NUM_STALLS);
        }
        if (NUM_EMPLOYEES <= 0) {
            throw new IllegalArgumentException(
                    "Number of bathroom users must be greater than 0. Provided: " + NUM_EMPLOYEES);
        }

        Thread[] users = new Thread[NUM_EMPLOYEES];

        // Initialize the stalls
        for (int i = 0; i < NUM_STALLS; i++) {
            availableStalls.put(i, true); // Each stall is marked as available initially
        }

        // Create BathroomUser threads
        Random random = new Random();
        for (int i = 0; i < NUM_EMPLOYEES; i++) {
            users[i] = new Thread(new BathroomUser(), "Employee " + (i + 1));

            // Start threads at random intervals
            try {
                Thread.sleep(random.nextInt(1000)); // Random delay of 0-1 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            users[i].start();
        }

        // Wait for all threads to finish
        for (Thread user : users) {
            try {
                user.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("All users have finished using the bathroom stalls.");
    }
}