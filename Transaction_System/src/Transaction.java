/**
 * Represents a transaction between two accounts.
 */
public class Transaction {

    private final int fromAccountId;
    private final int toAccountId;
    private final double amount;
    private final long timestamp;
    private final boolean isReversed;

    /**
     * Create a new transaction.
     *
     * @param fromAccountId the ID of the account the money is coming from
     * @param toAccountId   the ID of the account the money is going to
     * @param amount        the amount of money being transferred
     * @param isReversed    true if the transaction is a reversal, false otherwise
     */
    public Transaction(int fromAccountId, int toAccountId, double amount, boolean isReversed) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
        this.timestamp = System.currentTimeMillis();
        this.isReversed = isReversed;
    }

    @Override
    public String toString() {
        String status = isReversed ? "REV:" : "TRX:";
        return String.format("%s $%.2f | from Account %d | to Account %d | %tc",
                status, amount, fromAccountId, toAccountId, timestamp);
    }
}
