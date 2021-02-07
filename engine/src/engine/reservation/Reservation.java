package engine.reservation;

import engine.activity.WeeklyActivity;
import engine.boat.BoatCrew;
import engine.boat.BoatType;
import engine.member.Member;
import engine.adapter.LocalDateAdapter;
import engine.adapter.LocalDateTimeAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@XmlRootElement
public class Reservation implements Serializable {
    public static int counter = 0;

    private String id;
    private String reservationCreator;
    private LocalDateTime creationDate;
    private String reservator;
    private WeeklyActivity weeklyActivity;
    private LocalDate activityDate;
    private Set<BoatType> boatTypes;
    private BoatCrew boatCrew;
    private boolean isConfirmed;

    public Reservation() {
        boatTypes = new HashSet<>();
    }

    public Reservation(String reservationCreator, String reservator, WeeklyActivity weeklyActivity,
                       Set<BoatType> boatTypes, LocalDate activityDate, BoatCrew boatCrew) {
        this.id = String.valueOf(++counter);
        this.reservationCreator = reservationCreator;
        this.creationDate = LocalDateTime.now();
        this.reservator = reservator;
        this.weeklyActivity = weeklyActivity;
        this.activityDate = activityDate;
        this.boatTypes = boatTypes;
        this.boatCrew = boatCrew;
        this.isConfirmed = false;
    }

    public String getId() {
        return id;
    }

    @XmlAttribute
    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute
    public void setReservationCreator(String reservationCreator) {
        this.reservationCreator = reservationCreator;
    }

    @XmlJavaTypeAdapter(value = LocalDateTimeAdapter.class)
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public WeeklyActivity getWeeklyActivity() {
        return weeklyActivity;
    }

    public String getReservationCreator() {
        return reservationCreator;
    }

    public String getReservator() {
        return reservator;
    }

    @XmlAttribute
    public void setReservator(String reservator) { this.reservator = reservator; }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    @XmlAttribute
    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public BoatCrew getBoatCrew() {
        return boatCrew;
    }

    @XmlElement
    public void setBoatCrew(BoatCrew boatCrew) {
        this.boatCrew = boatCrew;
    }

    public Set<BoatType> getBoatTypes() {
        return boatTypes;
    }

    @XmlElement
    public void setBoatTypes(Set<BoatType> boatTypes) {
        this.boatTypes = boatTypes;
    }

    public boolean isReservator(String memberId) {
        return this.reservator.equals(memberId);
    }

    public void removeMember(Member member) {
        if (boatCrew.getCrewMembers().contains(member.getSerialNumber())) {
            removeCrewMember(member);
        }
        else {
            removeCoxswain();
        }
    }

    public void removeCrewMember(Member member) {
        boatCrew.getCrewMembers().remove(member.getSerialNumber());
    }

    public String removeCoxswain() {
        String coxswain = boatCrew.getCoxswain();

        if (coxswain != null) {
            boatCrew.setCoxswain(null);
        }

        return coxswain;
    }

    public void addCrewMember(Member member) {
        if (member != null) {
            this.boatCrew.getCrewMembers().add(member.getSerialNumber());
        }
    }

    public void addCoxswain(Member coxswain) {
        if (coxswain != null) {
            this.boatCrew.setCoxswain(coxswain.getSerialNumber());
        }
    }

    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public void setActivityDate(LocalDate date) {
        if (date != null) {
            this.activityDate = date;
        }
    }

    @XmlElement
    public void setWeeklyActivity(WeeklyActivity weeklyActivity) {
        this.weeklyActivity = weeklyActivity;
    }

    public Reservation myClone() {
        Set<BoatType> boatTypes = new HashSet<>(this.boatTypes);
        List<String> crewMembers = new ArrayList<>(this.boatCrew.getCrewMembers());
        String coxswain = this.getBoatCrew().getCoxswain();
        BoatCrew boatCrew = new BoatCrew(crewMembers, coxswain);
        String reservator = this.reservator;
        String reservationCreator = this.reservationCreator;
        WeeklyActivity activity = this.weeklyActivity;
        LocalDate activityDate = this.activityDate;

        return new Reservation(reservationCreator, reservator, activity, boatTypes, activityDate, boatCrew);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reservation)) return false;
        Reservation that = (Reservation) o;
        return isConfirmed == that.isConfirmed &&
                Objects.equals(reservationCreator, that.reservationCreator) &&
                Objects.equals(reservator, that.reservator) &&
                Objects.equals(creationDate, that.creationDate) &&
                Objects.equals(weeklyActivity, that.weeklyActivity) &&
                Objects.equals(activityDate, that.activityDate) &&
                Objects.equals(boatTypes, that.boatTypes) &&
                Objects.equals(boatCrew, that.boatCrew);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationCreator, reservator, creationDate, weeklyActivity,
                activityDate, boatTypes, boatCrew, isConfirmed);
    }

    public int getSpaceInCrew() {
        int maxBoatTypeCapacity = BoatType.getMaxCapacityFromSet(boatTypes);

        for (BoatType boatType : boatTypes) {
            if (BoatType.getMaxCapacity(boatType) == maxBoatTypeCapacity &&
                    BoatType.doesBoatNeedCoxswain(boatType)) {
                maxBoatTypeCapacity++;
                break;
            }
        }

        return maxBoatTypeCapacity - boatCrew.size();
    }
}
