package webapp.servlets;


import com.google.gson.Gson;
import engine.Engine;
import engine.reservation.Reservation;
import webapp.common.ReservationData;
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

@WebServlet(name = "MergeableReservationsServlet", urlPatterns = {"/mergeableReservations"})
public class MergeableReservationsServlet  extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Gson gson = new Gson();
            BufferedReader reader = request.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            ReservationData reservationdata = gson.fromJson(jsonString, ReservationData.class);
            Reservation originalReservation = engine.findReservationByID(reservationdata.getId());
            List<Reservation> reservations = engine.getCombinableReservations(originalReservation,originalReservation.getSpaceInCrew());
            List<ReservationData> reservationDataList = new ArrayList<>();
            if (!reservations.isEmpty()) {
                ReservationData.parseReservationDetails(reservations, reservationDataList, engine);
                String jsonResponse = gson.toJson(reservationDataList);
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        }
    }
}
