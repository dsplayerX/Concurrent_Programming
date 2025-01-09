package exceptions;

public class AccountNotFoundException extends Exception {

    public AccountNotFoundException(int accountId) {
        super(String.format("Account %d not found in the system", accountId));
    }
}
