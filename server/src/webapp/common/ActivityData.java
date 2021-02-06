package webapp.common;

import engine.activity.WeeklyActivity;
import engine.boat.BoatType;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

public class ActivityData {
    private String name;
    private String date;
    private String time;
    private String restriction;
    // TODO: Anymore fields? (for create activity)

    public ActivityData(WeeklyActivity activity, int daysFromToday) {
        String name = activity.getName();
        LocalDate activityDate = LocalDate.now().plusDays(daysFromToday);
        String date = activityDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + activityDate;
        String time = activity.getStartTime() + "-" + activity.getEndTime();
        String restriction = BoatType.boatTypeToBoatCode(activity.getBoatTypeRestriction());
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getRestriction() {
        return restriction;
    }
}
