package engine.chat;

public class MessageEntry implements Comparable<MessageEntry> {
    private final String message;
    private final String username;
    private final long timestamp;

    public MessageEntry(String message, String username) {
        this.message = message;
        this.username = username;
        this.timestamp = System.currentTimeMillis();
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int compareTo(MessageEntry o) {
        if (o == null) {
            return 1;
        }

        return (int)(this.getTimestamp() - o.getTimestamp());
    }
}

