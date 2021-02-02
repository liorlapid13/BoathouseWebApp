package engine.exception;

public class MemberAlreadyLoggedInException extends Exception {
    private String message;

    public MemberAlreadyLoggedInException() {
        this.message = "This member is already logged in";
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
