import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MultipleConcurrentTransactions {
    private static final int NUM_ACCOUNTS = 10;

    public static void main(String[] args) {
        // Create bank accounts
        List<BankAccount> accounts = new ArrayList<>(NUM_ACCOUNTS);
        for (int i = 1; i <= NUM_ACCOUNTS; i++) {
            Random rand = new Random();
            accounts.add(new BankAccount(i, rand.nextDouble(1000, 20000)));
        }

        // Create transaction system
        TransactionSystem transactionSystem = new TransactionSystem(accounts);

        // Create 1000 threads for transactions simulation
        Thread[] randomThreads = new Thread[1000]; // Array to hold all threads

        for (int i = 0; i < 1000; i++){
            Random rand = new Random();
            Thread randThread = new Thread(() -> {
                int account1 = rand.nextInt(1, NUM_ACCOUNTS);
                int account2 = rand.nextInt(1, NUM_ACCOUNTS);
                while (account1 == account2){ // making sure no invalid transactions
                    account2 = rand.nextInt(1, NUM_ACCOUNTS);
                }
                transactionSystem.transfer(account1, account2, rand.nextDouble(1000));
            }, "Thread " + (i+1));
            randomThreads[i] = randThread;
        }

        // Start threads with random delays to simulate real life transaction system
        for (Thread thread : randomThreads) {
            Random rand = new Random();
            try {
                Thread.sleep(rand.nextInt(50)); // Random delay of 0-50 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            thread.start();
        }

        // Wait for all threads to complete
        try {
            for (Thread thread : randomThreads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            System.err.println("Error: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        // Print final balances and transaction history
        transactionSystem.printAccountBalances();
    }
}
