package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.boat.BoatCrew;
import engine.boat.BoatType;
import engine.exception.InvalidBoatTypeCodeException;
import engine.reservation.Reservation;
import webapp.constants.Constants;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "CreateReservationServlet", urlPatterns = "/createReservation")
public class CreateReservationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            String userId = SessionUtils.getUserId(req);
            Gson gson = new Gson();
            BufferedReader reader = req.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            ReservationData reservationData = gson.fromJson(jsonString, ReservationData.class);

            LocalDate date = LocalDate.now().plusDays(Integer.parseInt(reservationData.day));
            WeeklyActivity activity;
            if (reservationData.activity == null) {
                activity = (WeeklyActivity)getServletContext().getAttribute(Constants.DUMMY_ACTIVITY);
            } else {
                activity = engine.findActivity(reservationData.activity.name, reservationData.activity.time);
            }
            Set<BoatType> boatTypes = new HashSet<>();
            try {
                for (int i = 0; i < reservationData.boatTypes.length; i++) {
                    boatTypes.add(BoatType.boatCodeToBoatType(reservationData.boatTypes[i]));
                }

                List<String> crewMembers = new ArrayList<>();
                for (int i = 0; i < reservationData.crewMembers.length; i++) {
                    crewMembers.add(reservationData.crewMembers[i].id);
                }
                String coxswain = null;
                if (reservationData.coxswainSelected.equals("true")) {
                    coxswain = reservationData.coxswain.id;
                }
                BoatCrew crew = new BoatCrew(crewMembers, coxswain);
                Reservation reservation = new Reservation(userId, reservationData.reservator.id, activity, boatTypes, date, crew);
                engine.publishNewReservation(reservation, false);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (InvalidBoatTypeCodeException e) {
                resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.print(e.getMessage());
                out.flush();
            }

        }
    }

    private static class ReservationData {
        String day;
        ActivitiesServlet.ActivityData activity;
        String[] boatTypes;
        MembersForReservationServlet.MemberData reservator;
        MembersForReservationServlet.MemberData[] crewMembers;
        MembersForReservationServlet.MemberData coxswain;
        String coxswainSelected;
    }
}
