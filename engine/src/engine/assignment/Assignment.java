package engine.assignment;

import engine.boat.Boat;
import engine.reservation.Reservation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

@XmlRootElement
public class Assignment implements Serializable {
    Reservation assignedReservation;
    Boat assignedBoat;

    public Assignment() { }

    public Assignment(Reservation assignedReservation, Boat assignedBoat) {
        this.assignedReservation = assignedReservation;
        this.assignedBoat = assignedBoat;
    }

    public Reservation getAssignedReservation() {
        return assignedReservation;
    }
    public Boat getAssignedBoat() {
        return assignedBoat;
    }
    @XmlElement
    public void setAssignedReservation(Reservation assignedReservation) {
        this.assignedReservation = assignedReservation;
    }

    @XmlElement
    public void setAssignedBoat(Boat assignedBoat) {
        this.assignedBoat = assignedBoat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Assignment)) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(assignedReservation, that.assignedReservation) &&
                Objects.equals(assignedBoat, that.assignedBoat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignedReservation, assignedBoat);
    }
}
