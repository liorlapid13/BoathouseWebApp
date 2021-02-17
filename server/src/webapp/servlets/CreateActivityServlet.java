package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.reservation.Reservation;
import webapp.common.ActivityData;
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
import java.time.LocalTime;
import java.util.stream.Collectors;

@WebServlet(name = "CreateActivityServlet", urlPatterns = "/createActivity")
public class CreateActivityServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Gson gson = new Gson();
            BufferedReader reader = req.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            ActivityData activityData = gson.fromJson(jsonString, ActivityData.class);
            LocalTime startTime = ServerUtils.parseStartTime(activityData.getTime());
            LocalTime endTime = ServerUtils.parseEndTime(activityData.getTime());
            if(!engine.doesActivityExist(activityData.getName(),startTime,endTime)){
                WeeklyActivity newActivity = activityData.createWeeklyActivity();
                engine.addWeeklyActivityToList(newActivity);
                ServerUtils.saveSystemState(getServletContext());
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else{
                resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.print("Identical activity already exists,activity has not been added");
                out.flush();
            }
        }

    }
}