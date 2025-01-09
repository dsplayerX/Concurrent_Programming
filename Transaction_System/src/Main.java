import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Create bank accounts
        List<BankAccount> accounts = new ArrayList<>(3);
        for (int i = 1; i <= 3; i++) {
            accounts.add(new BankAccount(i, 10000));
        }

        // Create transaction system
        TransactionSystem transactionSystem = new TransactionSystem(accounts);

        Thread[] threads = new Thread[7]; // Array to hold all threads

        // Create threads for transactions
        Thread thread1 = new Thread(() -> {
            transactionSystem.transfer(1, 2, 1000);
        }, "Thread 1");
        threads[0] = thread1;

        Thread thread2 = new Thread(() -> {
            transactionSystem.transfer(2, 3, 2000);
        }, "Thread 2");
        threads[1] = thread2;

        Thread thread3 = new Thread(() -> {
            transactionSystem.transfer(3, 1, 10000);
        }, "Thread 3");
        threads[2] = thread3;

        // Create thread to check account balances
        Thread thread4 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + " " + transactionSystem.getAccountBalance(1));
            System.out.println(Thread.currentThread().getName() + " " + transactionSystem.getAccountBalance(3));
        }, "Thread 4");
        threads[3] = thread4;

        // Invalid transactions for demo
        Thread thread5 = new Thread(() -> transactionSystem.transfer(3, 1, 100000), "Thread 5"); // Insufficient funds
        threads[4] = thread5;

        Thread thread6 = new Thread(() -> transactionSystem.transfer(1, 999, 1000), "Thread 6"); // Invalid account
        threads[5] = thread6;

        Thread thread7 = new Thread(() -> transactionSystem.transfer(1, 2, -1000), "Thread 7"); // Invalid transfer amount
        threads[6] = thread7;

         // Start threads
        for
        (Thread thread : threads) {
            Random rand = new Random();
            try {
                Thread.sleep(rand.nextInt(500)); // Random delay of 0-1 seconds to simulate processing time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            thread.start();
        }

        // Wait for all threads to complete
        try {
            for (Thread thread : threads) {
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
