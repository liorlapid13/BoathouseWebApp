package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.boat.BoatType;
import engine.reservation.Reservation;
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
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "EditReservationBoatTypesServlet", urlPatterns = {"/editReservationBoatTypes"})
public class EditReservationBoatTypesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        editReservationBoatTypes(req, resp);
    }

    protected void editReservationBoatTypes(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            String userId = SessionUtils.getUserId(req);
            Gson gson = new Gson();
            BufferedReader reader = req.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            RequestData requestData = gson.fromJson(jsonString, RequestData.class);
            Reservation reservation = engine.findReservationByID(requestData.reservation.getId());
            Set<BoatType> boatTypes = ServerUtils.parseBoatTypes(requestData.boatTypes);
            Reservation updatedReservation = engine.updateReservationBoatTypes(reservation, boatTypes);
            if (updatedReservation != null) {
                engine.editReservationNotification(updatedReservation, userId);
                ReservationData reservationData = ReservationData.parseReservation(updatedReservation, engine);
                String jsonResponse = gson.toJson(reservationData);
                ServerUtils.saveSystemState(getServletContext());
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            } else {
                resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
            }
        }
    }

    private static class RequestData {
        ReservationData reservation;
        String[] boatTypes;
    }
}

