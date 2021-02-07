package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.boat.BoatType;
import webapp.common.ActivityData;
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
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@WebServlet(name = "ActivitiesServlet", urlPatterns = "/activities")
public class ActivitiesServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getAllActivities(req, resp);
    }

    protected void getAllActivities(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Gson gson = new Gson();
            List<ActivityData> activitiesData = new ArrayList<>();
            List<WeeklyActivity> activities = engine.getWeeklyActivities();
            if (activities.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                parseActivitiesData(activities, activitiesData);
                String jsonResponse = gson.toJson(activitiesData);
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    private void parseActivitiesData(List<WeeklyActivity> activities, List<ActivityData> activitiesData) {
        for (WeeklyActivity activity : activities) {
            activitiesData.add(new ActivityData(activity));
        }
    }

    /*private static class RequestData {
        String day;
    }

    public static class ActivityData {
        String name;
        String date;
        String time;
        String restriction;

        public ActivityData(String name, String date, String time, String restriction) {
            this.name = name;
            this.date = date;
            this.time = time;
            this.restriction = restriction;
        }
    }*/
}
