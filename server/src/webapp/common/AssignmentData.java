package webapp.common;

import engine.Engine;
import engine.assignment.Assignment;
import engine.boat.Boat;
import engine.reservation.Reservation;

public class AssignmentData {
    ReservationData reservation;
    BoatData boat;

    public AssignmentData(ReservationData reservation, BoatData boat) {
        this.reservation = reservation;
        this.boat = boat;
    }

    public Assignment createAssignment(Engine engine) {
        Reservation reservation = engine.findReservationByID(this.reservation.getId());
        Boat boat = engine.findBoatByID(this.boat.getId());

        return new Assignment(reservation, boat);
    }
}
