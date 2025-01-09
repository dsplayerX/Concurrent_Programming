import exceptions.TransactionLockException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A system for transferring money between bank accounts.
 */
public class TransactionSystem {

    private final Map<Integer, BankAccount> accounts; // Map of account IDs to bank accounts

    /**
     * Create a new transaction system with a list of bank accounts.
     *
     * @param accountList the list of bank accounts
     */
    public TransactionSystem(List<BankAccount> accountList) {
        this.accounts = new ConcurrentHashMap<>(); // usingg ConcurrentHashMap for thread safety
        for (BankAccount account : accountList) {
            accounts.put(account.getId(), account);
        }
    }

    /**
     * Get the bank account with the given ID.
     *
     * @param accountId the account ID
     * @return the bank account with the given ID, or null if not found
     */
    private BankAccount getAccount(int accountId) {
        BankAccount account = accounts.get(accountId);
        if (account == null) {
            System.err.println(Thread.currentThread().getName() + " Account with ID " + accountId + " not found.");
            return null;
        }
        return account;
    }

    /**
     * Transfer money between two bank accounts.
     *
     * @param fromAccountId the ID of the account to transfer money from
     * @param toAccountId   the ID of the account to transfer money to
     * @param amount        the amount of money to transfer
     * @return true if the transfer was successful, false otherwise
     */
    public boolean transfer(int fromAccountId, int toAccountId, double amount) {
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + " Starting transfer of $" + String.format("%.2f", amount) + " from Account " + fromAccountId +
                " to Account " + toAccountId);

        BankAccount fromAccount = getAccount(fromAccountId);
        BankAccount toAccount = getAccount(toAccountId);

        // if either account not found return false
        if (fromAccount == null || toAccount == null) {
            System.err.println(threadName + " Error: Transfer aborted due to missing account(s).");
            return false;
        }

        // if the source and destination accounts are the same return false
        if (fromAccountId == toAccountId) {
            System.err.println(threadName + " Error: Source and destination accounts cannot be the same.");
            return false;
        }

        // if the amount is less than or equal to 0 return false
        if (amount <= 0) {
            System.err.println(threadName + " Error: Transfer amount must be positive.");
            return false;
        }

        // Lock the accounts in ascending order to prevent deadlocks
        BankAccount firstLock = accounts.get(Math.min(fromAccountId, toAccountId));
        BankAccount secondLock = accounts.get(Math.max(fromAccountId, toAccountId));

        boolean withdrawalSuccessful = false; // Track successful withdrawal
        boolean depositSuccessful = false; // Track successful deposit

        try {
            firstLock.lock();
            try {
                secondLock.lock();
                try {
                    try {
                        Transaction transaction = new Transaction(fromAccountId, toAccountId, amount, false);

                        // withdraw from the source, with
                        fromAccount.withdraw(amount);
                        withdrawalSuccessful = true;
                        fromAccount.addTransaction(transaction);

                        // deposit into the destination
                        toAccount.deposit(amount);
                        depositSuccessful = true;
                        toAccount.addTransaction(transaction);


                        System.out.println(threadName + " Transfer of $" + String.format("%.2f", amount) + " completed successfully.");
                        return true;
                    } catch (Exception e) {
                        System.err.println(threadName + " Error during transfer: " + e.getMessage());
                    }
                } finally {
                    secondLock.unlock(); // Release the second lock
                }
            } finally {
                firstLock.unlock(); // Release the first lock

                // if either the withdrawal or deposit was unsuccessful, rollback the transaction
                if (!withdrawalSuccessful || !depositSuccessful) {
                    reverseTransaction(fromAccount, toAccount, amount, withdrawalSuccessful, depositSuccessful);
                }
            }
        } catch (TransactionLockException e) {
            System.err.println(threadName + " Error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Roll back a transaction by reversing the deposit and withdrawal.
     *
     * @param fromAccount           the source account
     * @param toAccount             the destination account
     * @param amount                the amount of the transaction
     * @param withdrawalSuccessful  true if the withdrawal was successful, false otherwise
     * @param depositSuccessful     true if the deposit was successful, false otherwise
     */
    private void reverseTransaction(BankAccount fromAccount, BankAccount toAccount, double amount,
                          boolean withdrawalSuccessful, boolean depositSuccessful) {
        String threadName = Thread.currentThread().getName();

        try {
            // Reverse the deposit only if it was successful
            if (depositSuccessful) {
                toAccount.withdraw(amount);
                toAccount.addTransaction(new Transaction(toAccount.getId(), fromAccount.getId(), amount, true));
                System.out.println(
                        threadName + " Rolled back deposit of $" + amount + " from Account " + toAccount.getId());
            }

            // Reverse the withdrawal only if it was successful
            if (withdrawalSuccessful) {
                fromAccount.deposit(amount);
                fromAccount.addTransaction(new Transaction(fromAccount.getId(), toAccount.getId(), amount, true));
                System.out.println(
                        threadName + " Rolled back withdrawal of $" + amount + " to Account " + fromAccount.getId());
            }
        } catch (Exception e) {
            System.err.println(threadName + " Rollback failed: " + e.getMessage());
        }
    }

    /**
     * Get the balance of a bank account.
     *
     * @param accountId the ID of the account
     * @return a string representation of the account balance
     */
    public String getAccountBalance(int accountId) {
        BankAccount account = getAccount(accountId);
        if (account == null) {
            System.err.println(Thread.currentThread().getName() + " Account with ID " + accountId + " not found.");
            return "Account not found";
        }
        double balance = account.getBalance();
        return "Account " + accountId + " Balance: $" + balance;
    }

    /**
     * Print the balances and transaction history of all accounts.
     */
    public void printAccountBalances() {
        for (BankAccount account : accounts.values()) {
            System.out.println("\nAccount " + account.getId() + ": $" + String.format("%.2f", account.getBalance()));
            System.out.println("Transaction History:");
            for (Transaction transaction : account.getTransactionHistory()) {
                System.out.println("\t" + transaction);
            }
        }
    }
}
