package webapp.common;

import engine.notification.Notification;
import webapp.utils.ServerUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationData {
    String dateTime;
    String message;
    MemberData creator;

    public NotificationData(Notification notification) {
        LocalDateTime dateTime = notification.getDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.dateTime = dateTime.format(formatter);
        this.message = notification.getMessage();
        this.creator = new MemberData(notification.getCreator());
    }
}
