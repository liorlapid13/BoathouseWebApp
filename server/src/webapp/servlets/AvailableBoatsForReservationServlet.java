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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "AvailableBoatsForReservationServlet", urlPatterns = {"/availableBoatsForReservation"})
public class AvailableBoatsForReservationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getAvailableBoatsForReservation(req, resp);
    }

    protected void getAvailableBoatsForReservation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Gson gson = new Gson();
            BufferedReader reader = req.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            ReservationData reservationData = gson.fromJson(jsonString, ReservationData.class);
            Reservation reservation = engine.findReservationByID(reservationData.getId());
            List<Boat> availableBoats = engine.getBoatsForReservation(reservation);
            if (availableBoats.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
            } else {
                List<BoatData> boatDataList = parseAvailableBoats(availableBoats);
                String jsonResponse = gson.toJson(boatDataList);
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    private List<BoatData> parseAvailableBoats(List<Boat> boats) {
        List<BoatData> boatDataList = new ArrayList<>();
        for (Boat boat : boats) {
            boatDataList.add(new BoatData(boat));
        }

        return boatDataList;
    }
}
