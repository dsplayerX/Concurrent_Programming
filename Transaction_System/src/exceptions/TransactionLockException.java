package exceptions;

public class TransactionLockException extends Exception {
    public TransactionLockException(String message) {
        super(message);
    }
}