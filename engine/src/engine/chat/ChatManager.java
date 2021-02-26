package engine.chat;

import java.util.*;

public class ChatManager {
    private final List<MessageEntry> messages;
    private final Set<String> usernames;

    public ChatManager() {
        messages = new ArrayList<>();
        usernames = new HashSet<>();
    }

    public void addMessage(String message, String username) {
        messages.add(new MessageEntry(message, username));
    }

    public  List<MessageEntry> getMessages(int offset) {
        if (offset < 0 || offset > messages.size()) {
            offset = 0;
        }
        return messages.subList(offset, messages.size());
    }

    public int getVersion() {
        return messages.size();
    }

    public void addUser(String username) {
        usernames.add(username);
    }

    public void removeUser(String username) {
        usernames.remove(username);
    }

    public Set<String> getUsers() {
        return Collections.unmodifiableSet(usernames);
    }

    public boolean isUserExists(String username) {
        return usernames.contains(username);
    }
}
