package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.boat.BoatType;
import webapp.common.ActivityData;
import webapp.common.MemberData;
import webapp.common.ReservationData;
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
import java.time.LocalTime;
import java.util.stream.Collectors;

@WebServlet(name = "EditActivityServlet", urlPatterns = {"/editActivity"})
public class EditActivity extends HttpServlet {
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
            RequestData requestData = gson.fromJson(jsonString, RequestData.class);
            LocalTime startTime = ServerUtils.parseStartTime(requestData.newTime);
            LocalTime endTime = ServerUtils.parseEndTime(requestData.newTime);
            if(!engine.doesActivityExist(requestData.newName,startTime,endTime)){
                WeeklyActivity activityToEdit = engine.findActivity(requestData.selectedActivity.getName(),
                        requestData.selectedActivity.getTime());
                engine.removeActivity(activityToEdit);
                WeeklyActivity editedActivity = new WeeklyActivity(requestData.newName,startTime,endTime,
                        BoatType.boatCodeToBoatType(requestData.newRestriction));
                engine.addWeeklyActivityToList(editedActivity);
                ServerUtils.saveSystemState(getServletContext());
                resp.setStatus(HttpServletResponse.SC_OK);
            }
            else{
                resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.print("Identical activity already exists,activity has not been changed");
                out.flush();
            }
        }
    }

    private static class RequestData {
        ActivityData selectedActivity;
        String newName;
        String newTime;
        String newRestriction;
    }
}
