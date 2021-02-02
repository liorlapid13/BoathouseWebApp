package engine.exception;

public class XmlException extends Exception {
    private String message;

    public XmlException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
