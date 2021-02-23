package webapp.common;

import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.boat.BoatCrew;
import engine.boat.BoatType;
import engine.member.Member;
import engine.reservation.Reservation;
import webapp.utils.ServerUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

public class ReservationData {
    private MemberData reservationCreator;
    private String id;
    private String date;
    private ActivityData activity;
    private String[] boatTypes;
    private MemberData reservator;
    private MemberData[] boatCrew;
    private MemberData coxswain;
    private boolean coxswainSelected;
    private String status;
    private String creationDate;

    public ReservationData(Reservation reservation, Member reservationCreator, Member reservator,
                           List<Member> crewMembers, boolean coxswainSelected, Member coxswain) {
        this.reservationCreator = parseMember(reservationCreator);
        this.id = reservation.getId();
        this.date = reservation.getActivityDate().getDayOfWeek().getDisplayName(
                TextStyle.FULL, Locale.ENGLISH) + ", " + reservation.getActivityDate();
        this.activity = new ActivityData(reservation.getWeeklyActivity());
        this.boatTypes = parseBoatTypes(reservation.getBoatTypes());
        this.reservator = parseMember(reservator);
        this.boatCrew = parseCrewMembers(crewMembers);
        this.coxswainSelected = coxswainSelected;
        this.coxswain = coxswainSelected ? parseMember(coxswain) : null;
        this.status = reservation.isConfirmed() ? "Confirmed" : "Unconfirmed";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.creationDate = reservation.getCreationDate().format(formatter);
    }

    public String getId() {
        return id;
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

    public boolean getCoxswainSelected() {
        return coxswainSelected;
    }

    public String getStatus() {
        return status;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public MemberData getReservationCreator() {
        return reservationCreator;
    }

    public boolean isCoxswainSelected() {
        return coxswainSelected;
    }

    private String[] parseBoatTypes(Set<BoatType> boatTypes) {
        int setSize = boatTypes.size();
        int index = 0;
        String[] boatTypesArray = new String[setSize];

        for (BoatType boatType : boatTypes) {
            boatTypesArray[index] = BoatType.boatTypeToBoatCode(boatType);
            index++;
        }

        return boatTypesArray;
    }

    private Set<BoatType> parseBoatTypes(String[] boatTypes) {
        Set<BoatType> boatTypesSet = new HashSet<>();
        for (int i = 0; i < boatTypes.length; i++) {
            boatTypesSet.add(BoatType.boatCodeToBoatType(boatTypes[i]));
        }

        return boatTypesSet;
    }

    private MemberData[] parseCrewMembers(List<Member> crewMembers) {
        MemberData[] members = new MemberData[crewMembers.size()];

        for (int i = 0; i < crewMembers.size(); i++) {
            members[i] = parseMember(crewMembers.get(i));
        }

        return members;
    }

    private MemberData parseMember(Member member) {
        return new MemberData(member);
    }

    public Reservation createReservation(String reservationCreator, WeeklyActivity weeklyActivity) {
        Set<BoatType> boatTypesSet = parseBoatTypes(boatTypes);
        LocalDate activityDate = ServerUtils.parseDate(date);
        List<String> crewMembers = ServerUtils.parseCrewMembers(boatCrew);
        String coxswainId = coxswainSelected ? coxswain.getId() : null;
        BoatCrew parsedBoatCrew = new BoatCrew(crewMembers, coxswainId);

        return new Reservation(reservationCreator, reservator.getId(), weeklyActivity, boatTypesSet,
                activityDate, parsedBoatCrew);
    }

    public static void parseReservations(List<Reservation> reservationList,
                                         List<ReservationData> reservationDataList, Engine engine) {
        for (Reservation reservation : reservationList) {
            reservationDataList.add(parseReservation(reservation, engine));
        }
    }

    public static ReservationData parseReservation(Reservation reservation, Engine engine) {
        Member reservationCreator = engine.findMemberByID(reservation.getReservationCreator());
        Member reservator = engine.findMemberByID(reservation.getReservator());
        List<Member> crewMembers = engine.findMemberListByIDList(reservation.getBoatCrew().getCrewMembers());
        Member coxswain = engine.findMemberByID(reservation.getBoatCrew().getCoxswain());
        boolean coxswainSelected = coxswain != null;

        return new ReservationData(reservation, reservationCreator, reservator, crewMembers, coxswainSelected, coxswain);
    }
}