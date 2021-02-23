package webapp.common;

import engine.Engine;
import engine.assignment.Assignment;
import engine.boat.Boat;
import engine.member.Member;
import engine.reservation.Reservation;

import java.util.List;

public class AssignmentData {
    private ReservationData reservation;
    private BoatData boat;

    public AssignmentData(ReservationData reservation, BoatData boat) {
        this.reservation = reservation;
        this.boat = boat;
    }

    public ReservationData getReservation() {
        return reservation;
    }

    public BoatData getBoat() {
        return boat;
    }

    public Assignment createAssignment(Engine engine) {
        Reservation reservation = engine.findReservationByID(this.reservation.getId());
        Boat boat = engine.findBoatByID(this.boat.getId());

        return new Assignment(reservation, boat);
    }

    public static void parseAssignments(List<Assignment> assignmentList, List<AssignmentData> assignmentDataList, Engine engine) {
        for (Assignment assignment : assignmentList) {
            assignmentDataList.add(parseAssignment(assignment, engine));
        }
    }

    public static AssignmentData parseAssignment(Assignment assignment, Engine engine) {
        ReservationData reservation = ReservationData.parseReservation(assignment.getAssignedReservation(), engine);
        BoatData boat = new BoatData(assignment.getAssignedBoat());

        return new AssignmentData(reservation, boat);
    }
}
