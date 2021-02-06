package webapp.common;

import engine.boat.BoatType;
import engine.member.Member;
import engine.reservation.Reservation;

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ReservationData {
    //String id;
    private String date;
    private ActivityData activity;
    private String[] boatTypes;
    private MemberData reservator;
    private MemberData[] boatCrew;
    private MemberData coxswain;
    private String coxswainSelected;
    private String status;
    private String creationDate;

    public ReservationData(Reservation reservation, List<Member> crewMembers, Member coxswain) {
        //String id = reservation.getId();
        String date = reservation.getActivityDate().getDayOfWeek().getDisplayName(
                TextStyle.FULL, Locale.getDefault()) + " " + reservation.getActivityDate();
        String activity = reservation.getActivity().getName() + "\n" +
                reservation.getActivity().getStartTime() + "-" + reservation.getActivity().getEndTime();
        String boatTypes = parseBoatTypes(reservation.getBoatTypes());
        String boatCrew = parseCrewMembers(crewMembers, coxswain);
        String status = reservation.isConfirmed() ? "Confirmed" : "Unconfirmed";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String creationDate = reservation.getCreationDate().format(formatter);
    }

    public String getDate() {
        return date;
    }

    public ActivityData getActivity() {
        return activity;
    }

    public String[] getBoatTypes() {
        return boatTypes;
    }

    public MemberData getReservator() {
        return reservator;
    }

    public MemberData[] getBoatCrew() {
        return boatCrew;
    }

    public MemberData getCoxswain() {
        return coxswain;
    }

    public String getCoxswainSelected() {
        return coxswainSelected;
    }

    private String parseBoatTypes(Set<BoatType> boatTypes) {
        StringBuilder boatTypesString = new StringBuilder();
        int setSize = boatTypes.size();

        for (BoatType boatType : boatTypes) {
            boatTypesString.append(BoatType.boatTypeToBoatCode(boatType));
            if (--setSize != 0) {
                boatTypesString.append(", ");
            }
        }

        return boatTypesString.toString();
    }

    private String parseCrewMembers(List<Member> crewMembers, Member coxswain) {
        StringBuilder memberNames = new StringBuilder();

        for (int i = 0; i < crewMembers.size(); i++) {
            Member member = crewMembers.get(i);

            memberNames.append(member.getName());
            if (i != crewMembers.size() - 1) {
                memberNames.append(", ");
                if (i % 2 != 0) {
                    memberNames.append("\n");
                }
            }
        }

        memberNames.append("\n");
        memberNames.append("Coxswain: ");
        memberNames.append(coxswainSelected = coxswain == null ? "none" : coxswain.getName());

        return memberNames.toString();
    }
}
