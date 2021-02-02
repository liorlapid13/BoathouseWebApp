package engine.exception;

public class OutOfEnumRangeException extends Exception {
    private String message;

    public OutOfEnumRangeException(int minimum, int maximum, String enumName) {
        this.message = "Enum: " + enumName + "'s integer values are " + minimum + "-" + maximum;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
