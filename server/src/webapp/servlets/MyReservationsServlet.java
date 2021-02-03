package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.boat.BoatType;
import engine.member.Member;
import engine.reservation.Reservation;
import webapp.utils.ServletUtils;
import webapp.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "MyReservationsServlet", urlPatterns = {"/myReservations"})
public class MyReservationsServlet extends HttpServlet {
    private List<ReservationDetails> reservationDetailsList = new ArrayList<>();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            String userId = SessionUtils.getUserId(request);
            Member member = engine.findMemberByID(userId);
            Gson gson = new Gson();
            BufferedReader reader = request.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            RequestData requestData = gson.fromJson(jsonString, RequestData.class);
            List<Reservation> reservationList = null;

            switch (requestData.getRequestType()) {
                case "past":
                    reservationList = member.getPastWeekReservationList();
                    break;
                case "next":
                    reservationList = member.getNextWeekReservations();
                    break;
                case "day":
                    reservationList = member.getSpecificDateReservations(
                            LocalDate.now().plusDays(Integer.parseInt(requestData.getDay())));
                    break;
            }

            if (reservationList.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                parseReservationDetails(reservationList, engine);
                String jsonResponse = gson.toJson(reservationDetailsList);
                out.print(jsonResponse);
                out.flush();
                response.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }

    private void parseReservationDetails(List<Reservation> reservationList, Engine engine) {
        for (Reservation reservation : reservationList) {
            String reservator = engine.findMemberByID(reservation.getReservator()).getName();
            String date = reservation.getActivityDate().getDayOfWeek().getDisplayName(
                    TextStyle.FULL, Locale.getDefault()) + " " + reservation.getActivityDate() + ")";;
            String activity = reservation.getActivity().getName() + "\n" +
                    reservation.getActivity().getStartTime() + "-" + reservation.getActivity().getEndTime();
            String boatTypes = parseSelectedBoatTypes(reservation.getBoatTypes());
            String boatCrew = parseCrewMembers(
                    engine.findMemberListByIDList(reservation.getBoatCrew().getCrewMembers()),
                    engine.findMemberByID(reservation.getBoatCrew().getCoxswain()));
            String status = reservation.isConfirmed() ? "Confirmed" : "Unconfirmed";
            String creationDate = reservation.getCreationDate().toString();
            ReservationDetails reservationDetails = new ReservationDetails(
                    reservator,
                    date,
                    activity,
                    boatTypes,
                    boatCrew,
                    status,
                    creationDate
            );

            reservationDetailsList.add(reservationDetails);
        }
    }

    private static class RequestData {
        String requestType;
        String day;

        public String getRequestType() {
            return requestType;
        }

        public String getDay() {
            return day;
        }
    }

    private static class ReservationDetails {
        String reservator;
        String date;
        String activity;
        String boatTypes;
        String boatCrew;
        String status;
        String creationDate;

        public ReservationDetails(String reservator, String date, String activity, String boatTypes, String boatCrew,
                                  String status, String creationDate) {
            this.reservator = reservator;
            this.date = date;
            this.activity = activity;
            this.boatTypes = boatTypes;
            this.boatCrew = boatCrew;
            this.status = status;
            this.creationDate = creationDate;
        }
    }

    private String parseSelectedBoatTypes(Set<BoatType> boatTypes) {
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
        int i;

        for (i = 0; i < crewMembers.size(); i++) {
            Member member = crewMembers.get(i);

            memberNames.append(member.getName());
            if (i != crewMembers.size() - 1) {
                memberNames.append(", ");
                if (i % 2 == 0 && i != 0) {
                    memberNames.append("\n");
                }
            }
        }

        if (i % 2 == 0) {
            memberNames.append("\n");
        }

        memberNames.append("Coxswain: ");
        memberNames.append(coxswain == null ? "none" : coxswain.getName());

        return memberNames.toString();
    }
}
