package webapp.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.assignment.Assignment;
import engine.boat.BoatCrew;
import engine.boat.BoatType;
import engine.reservation.Reservation;
import webapp.common.ActivityData;
import webapp.common.AssignmentData;
import webapp.common.ReservationData;
import webapp.constants.Constants;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "CreateAssignmentServlet", urlPatterns = "/createAssignment")
public class CreateAssignmentServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userId = SessionUtils.getUserId(req);
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        AssignmentData assignmentData = gson.fromJson(jsonString, AssignmentData.class);
        Assignment assignment = assignmentData.createAssignment(engine);
        engine.addAssignment(assignment);
        engine.newAssignmentNotification(assignment, userId);
        ServerUtils.saveSystemState(getServletContext());
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
