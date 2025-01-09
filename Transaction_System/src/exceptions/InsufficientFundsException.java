package exceptions;

public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(int accountId, double requested, double available) {
        super(String.format("Account %d: Insufficient funds. Requested: $%.2f, Available: $%.2f",
                accountId, requested, available));
    }
}
