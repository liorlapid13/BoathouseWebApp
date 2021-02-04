package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.member.Member;
import engine.reservation.Reservation;
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

@WebServlet(name = "EditReservationServlet", urlPatterns = {"/editReservation"})
public class EditReservationServlet extends HttpServlet {
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
            EditRequest editRequest = gson.fromJson(jsonString, EditRequest.class);

            List<Reservation> reservationList;
            switch (editRequest.requestType) {
                case "next":
                    reservationList = member.getNextWeekReservations();
                    break;
                case "day":
                    reservationList = member.getSpecificDateReservations(
                            LocalDate.now().plusDays(Integer.parseInt(editRequest.day)));
                    break;
                default:
                    reservationList = new ArrayList<>();
            }
        }
    }

    private static class EditRequest {
        String requestType;
        String day;
        String index;

    }
}
