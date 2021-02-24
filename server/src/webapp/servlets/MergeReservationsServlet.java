package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.boat.Boat;
import engine.member.Member;
import engine.reservation.Reservation;
import webapp.common.BoatData;
import webapp.common.ReservationData;
import webapp.utils.ServerUtils;
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
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "MergeReservationsServlet", urlPatterns = {"/mergeReservations"})
public class MergeReservationsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        mergeReservations(req, resp);
    }

    protected void mergeReservations(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            String userId = SessionUtils.getUserId(req);
            Gson gson = new Gson();
            BufferedReader reader = req.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            RequestData requestData = gson.fromJson(jsonString, RequestData.class);
            Reservation originalReservation = engine.findReservationByID(requestData.reservation.getId());
            Reservation reservationToMerge = engine.findReservationByID(requestData.reservationToMerge.getId());
            engine.editReservationNotification(originalReservation, userId);
            engine.editReservationNotification(reservationToMerge, userId);
            Reservation updatedReservation = engine.combineReservations(originalReservation, reservationToMerge,
                    requestData.assignCoxswain, userId);
            if (updatedReservation != null) {
                String jsonResponse = gson.toJson(ReservationData.parseReservation(updatedReservation, engine));
                resp.setStatus(HttpServletResponse.SC_OK);
                ServerUtils.saveSystemState(getServletContext());
                out.print(jsonResponse);
                out.flush();
            } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }
        }
    }

    private static class RequestData {
        ReservationData reservation;
        ReservationData reservationToMerge;
        boolean assignCoxswain;
    }
}
