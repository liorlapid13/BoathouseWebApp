package webapp.servlets;


import com.google.gson.Gson;
import engine.Engine;
import engine.assignment.Assignment;
import engine.reservation.Reservation;
import engine.reservation.ReservationViewFilter;
import webapp.common.AssignmentData;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "AllAssignmentsServlet", urlPatterns = {"/allAssignments"})
public class AllAssignmentsServlet extends HttpServlet {
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
            RequestData requestData = gson.fromJson(jsonString, RequestData.class);
            List<AssignmentData> assignmentDataList = new ArrayList<>();
            List<Assignment> assignmentList;

            switch (requestData.requestType) {
                case "next":
                    assignmentList = engine.getNextWeekAssignments();
                    break;
                case "day":
                    assignmentList = engine.getSpecificDateAssignments(
                            LocalDate.now().plusDays(Integer.parseInt(requestData.daysFromToday)));
                    break;
                default:
                    assignmentList = new ArrayList<>();
            }

            if (assignmentList.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                AssignmentData.parseAssignments(assignmentList, assignmentDataList, engine);
                String jsonResponse = gson.toJson(assignmentDataList);
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    private static class RequestData {
        String requestType;
        String daysFromToday;
    }
}