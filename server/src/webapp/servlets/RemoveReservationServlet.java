package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.member.Member;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "RemoveReservationServlet", urlPatterns = {"/removeReservation"})
public class RemoveReservationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userId = SessionUtils.getUserId(request);
        Gson gson = new Gson();
        BufferedReader reader = request.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        ReservationData reservationData = gson.fromJson(jsonString, ReservationData.class);
        Reservation reservationToRemove = engine.findReservationByID(reservationData.getId());
        engine.removeReservation(reservationToRemove, false, userId);
        engine.removeReservationNotification(reservationToRemove, userId);
        response.setStatus(HttpServletResponse.SC_OK);
        ServerUtils.saveSystemState(getServletContext());
    }

}
