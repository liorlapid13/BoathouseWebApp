package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.assignment.Assignment;
import engine.member.Member;
import engine.reservation.Reservation;
import webapp.common.AssignmentData;
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

@WebServlet(name = "RemoveAssignmentServlet", urlPatterns = {"/removeAssignment"})
public class RemoveAssignmentServlet extends HttpServlet {
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
        AssignmentData assignmentToRemove = gson.fromJson(jsonString, AssignmentData.class);
        Reservation reservation = engine.findReservationByID(assignmentToRemove.getReservation().getId());
        Assignment assignment = engine.findAssignment(reservation);
        if (assignment != null) {
            engine.removeAssignment(assignment, false);
            engine.removeAssignmentNotification(assignment, userId);
            response.setStatus(HttpServletResponse.SC_OK);
            ServerUtils.saveSystemState(getServletContext());
        } else {
            response.setStatus(HttpServletResponse.SC_SEE_OTHER);
        }
    }
}
