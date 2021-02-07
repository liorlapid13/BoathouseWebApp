package webapp.common;

import engine.activity.WeeklyActivity;
import engine.boat.BoatType;
import webapp.utils.ServerUtils;

import java.time.LocalTime;

public class ActivityData {
    private String name;
    private String time;
    private String restriction;

    public ActivityData(WeeklyActivity activity) {
        this.name = activity.getName();
        this.time = activity.getStartTime() + "-" + activity.getEndTime();
        this.restriction = BoatType.boatTypeToBoatCode(activity.getBoatTypeRestriction());
    }

    public ActivityData(String name, String time, String restriction) {
        this.name = name;
        this.time = time;
        this.restriction = restriction;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getRestriction() {
        return restriction;
    }

    public WeeklyActivity createWeeklyActivity() {
        LocalTime startTime = ServerUtils.parseStartTime(time);
        LocalTime endTime = ServerUtils.parseEndTime(time);
        BoatType boatTypeRestriction = BoatType.boatCodeToBoatType(restriction);

        return new WeeklyActivity(name, startTime, endTime, boatTypeRestriction);
    }
}
