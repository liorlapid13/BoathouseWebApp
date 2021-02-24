package engine.notification;

import engine.member.Member;

import java.time.LocalDateTime;

public class Notification implements Comparable<Notification> {
    private LocalDateTime dateTime;
    private String message;
    private Member creator;

    public Notification(LocalDateTime dateTime, String message,  Member creator) {
        this.dateTime = dateTime;
        this.message = message;
        this.creator = creator;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getMessage() {
        return message;
    }

    public Member getCreator() {
        return creator;
    }

    @Override
    public int compareTo(Notification other) {
        if (this.getDateTime().isAfter(other.getDateTime())) {
            return 1;
        } else if (this.getDateTime().isBefore(other.getDateTime())) {
            return -1;
        } else {
            return 0;
        }
    }
}