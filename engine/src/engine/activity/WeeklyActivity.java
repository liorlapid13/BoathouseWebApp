package engine.activity;

import engine.boat.BoatType;
import engine.adapter.LocalTimeAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalTime;
import java.util.Objects;

@XmlRootElement
public class WeeklyActivity implements Serializable {
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private BoatType boatTypeRestriction;


    public WeeklyActivity(String name, LocalTime startTime, LocalTime endTime, BoatType boatTypeRestriction) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.boatTypeRestriction = boatTypeRestriction;
    }
    public WeeklyActivity() { }

    public String getName() {
        return name;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public BoatType getBoatTypeRestriction() {
        return boatTypeRestriction;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    @XmlJavaTypeAdapter(value = LocalTimeAdapter.class)
    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    @XmlJavaTypeAdapter(value = LocalTimeAdapter.class)
    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @XmlAttribute
    public void setBoatTypeRestriction(BoatType boatTypeRestriction) {
        this.boatTypeRestriction = boatTypeRestriction;
    }

    public WeeklyActivity myClone(){
        return new WeeklyActivity(name, startTime, endTime, boatTypeRestriction);
    }

    public void setActivityTime(LocalTime startTime, LocalTime endTime) {
        setStartTime(startTime);
        setEndTime(endTime);
    }

    public static boolean isValidActivityTime(LocalTime startTime, LocalTime endTime) {
        return endTime.isAfter(startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WeeklyActivity)) return false;
        WeeklyActivity that = (WeeklyActivity) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(startTime, that.startTime) &&
                Objects.equals(endTime, that.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, startTime, endTime);
    }
}
