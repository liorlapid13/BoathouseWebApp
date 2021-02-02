package engine.exception;

public class EmailAlreadyExistsException extends Exception {
    private String message;

    public EmailAlreadyExistsException() {
        this.message = "This email is not available";
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
