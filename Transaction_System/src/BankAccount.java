import exceptions.InsufficientFundsException;
import exceptions.InvalidTransactionException;
import exceptions.TransactionLockException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A bank account with a balance that can be deposited to and withdrawn from.
 */
public class BankAccount {
    private final int id;
    private double balance;
    private final ReentrantLock lock; // Lock for account operations
    private final ReentrantReadWriteLock rwLock; // Lock for balance read/write operations
    private final List<Transaction> transactionHistory; // List of transactions for this account
    private final ReentrantReadWriteLock historyLock; // Lock for transaction history read/write operations

    /**
     * Create a new bank account with an initial balance.
     *
     * @param id the account ID
     * @param initialBalance the initial balance
     * @throws IllegalArgumentException if the initial balance is negative
     */
    public BankAccount(int id, double initialBalance) {
        if (initialBalance < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        this.id = id;
        this.balance = initialBalance;
        this.lock = new ReentrantLock(true);
        this.rwLock = new ReentrantReadWriteLock(true);
        this.transactionHistory = new ArrayList<>();
        this.historyLock = new ReentrantReadWriteLock(true);
    }

    /**
     * Get the account ID.
     *
     * @return the account ID
     */
    public int getId() {
        return id;
    }

    /**
     * Get the current balance of the account.
     *
     * @return the current balance
     */
    public double getBalance() {
        rwLock.readLock().lock();
        try {
            return balance;
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Get the transaction history of the account.
     *
     * @return a list of transactions
     */
    public List<Transaction> getTransactionHistory() {
        historyLock.readLock().lock();
        try {
            return new ArrayList<>(transactionHistory);
        } finally {
            historyLock.readLock().unlock();
        }
    }

    /**
     * Add a transaction to the account's history.
     *
     * @param transaction the transaction to add
     */
    public void addTransaction(Transaction transaction) {
        historyLock.writeLock().lock();
        try {
            transactionHistory.add(transaction);
        } finally {
            historyLock.writeLock().unlock();
        }
    }

    /**
     * Deposit funds into the account.
     *
     * @param amount the amount to deposit
     * @throws InvalidTransactionException if the amount is not positive
     */
    public void deposit(double amount) throws InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Deposit amount must be positive");
        }

        rwLock.writeLock().lock();
        try {
            balance += amount;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Withdraw funds from the account.
     *
     * @param amount the amount to withdraw
     * @throws InsufficientFundsException if the account has insufficient funds
     * @throws InvalidTransactionException if the amount is not positive
     */
    public void withdraw(double amount) throws InsufficientFundsException, InvalidTransactionException {
        if (amount <= 0) {
            throw new InvalidTransactionException("Withdrawal amount must be positive");
        }

        rwLock.writeLock().lock();
        try {
            if (balance >= amount) {
                balance -= amount;
            } else {
                throw new InsufficientFundsException(id, amount, balance);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Lock the account for a transaction.
     *
     * @throws TransactionLockException if the lock cannot be acquired
     */
    public void lock() throws TransactionLockException {
        try {
            if (!lock.tryLock(5, TimeUnit.SECONDS)) {
                throw new TransactionLockException("Unable to acquire lock for account " + id);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TransactionLockException("Lock acquisition interrupted for account " + id);
        }
    }

    /**
     * Unlock the account after a transaction.
     */
    public void unlock() {
        lock.unlock();
    }
}
