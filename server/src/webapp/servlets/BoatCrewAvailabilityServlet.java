package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.boat.BoatCrew;
import webapp.common.ActivityData;
import webapp.common.MemberData;
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
import java.time.LocalDate;
import java.util.stream.Collectors;

@WebServlet(name = "BoatCrewAvailabilityServlet", urlPatterns = {"/boatCrewAvailability"})
public class BoatCrewAvailabilityServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        RequestData requestData = gson.fromJson(jsonString, RequestData.class);
        BoatCrew boatCrew = ServerUtils.createBoatCrew(
                requestData.boatCrew, requestData.coxswain, requestData.coxswainSelected);
        WeeklyActivity activity = requestData.activity.createWeeklyActivity();
        LocalDate date = ServerUtils.parseDate(requestData.date);
        if (engine.isBoatCrewAvailableForActivity(boatCrew, activity, date)) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
        }
    }

    private static class RequestData {
        String date;
        ActivityData activity;
        MemberData[] boatCrew;
        MemberData coxswain;
        boolean coxswainSelected;
    }
}
