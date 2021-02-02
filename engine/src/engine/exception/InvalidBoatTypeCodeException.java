package engine.exception;

public class InvalidBoatTypeCodeException extends Exception {
    String boatCode;
    String message;

    public InvalidBoatTypeCodeException(String boatCode) {
        this.boatCode = boatCode;
        this.message = boatCode + " is not a valid boat code";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
