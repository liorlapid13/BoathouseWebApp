package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.boat.Boat;
import engine.reservation.Reservation;
import webapp.common.BoatData;
import webapp.common.ReservationData;
import webapp.utils.ServerUtils;
import webapp.utils.ServletUtils;

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

@WebServlet(name = "MergeReservationsServlet", urlPatterns = {"/mergeReservationsServlet"})
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
            Gson gson = new Gson();
            BufferedReader reader = req.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            RequestData requestData = gson.fromJson(jsonString, RequestData.class);
            Reservation originalReservation = engine.findReservationByID(requestData.reservation.getId());
            Reservation reservationToMerge = engine.findReservationByID(requestData.reservationToMerge.getId());
            Reservation updatedReservation = engine.combineReservations(originalReservation, reservationToMerge,
                    requestData.assignCoxswain);
            if (updatedReservation != null) {
                String jsonResponse = gson.toJson(updatedReservation);
                resp.setStatus(HttpServletResponse.SC_OK);
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
