package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.boat.BoatCrew;
import engine.reservation.Reservation;
import webapp.common.MemberData;
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


@WebServlet(name = "SplitReservationServlet", urlPatterns = {"/splitReservation"})
public class SplitReservationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            String userId = SessionUtils.getUserId(req);
            Gson gson = new Gson();
            BufferedReader reader = req.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            RequestData requestData = gson.fromJson(jsonString, RequestData.class);
            Reservation reservation = engine.findReservationByID(requestData.reservation.getId());
            List<String> crewMembers = ServerUtils.parseCrewMembers(requestData.crew);
            String coxswain = requestData.coxswain != null ? requestData.coxswain.getId() : null;
            BoatCrew boatCrew = new BoatCrew(crewMembers, coxswain);
            engine.editReservationNotification(reservation, userId);
            Reservation updatedReservation = engine.splitReservation(reservation, boatCrew);
            if (updatedReservation != null) {
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
        MemberData[] crew;
        MemberData coxswain;
    }
}