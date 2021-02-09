package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import webapp.common.ActivityData;
import webapp.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ActivitiesServlet", urlPatterns = "/activities")
public class ActivitiesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getAllActivities(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    protected void getAllActivities(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Gson gson = new Gson();
            List<WeeklyActivity> activities = engine.getWeeklyActivities();
            if (activities.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                List<ActivityData> activitiesData = parseWeeklyActivities(activities);
                String jsonResponse = gson.toJson(activitiesData);
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    private List<ActivityData> parseWeeklyActivities(List<WeeklyActivity> activities) {
        List<ActivityData> activityDataList = new ArrayList<>();
        for (WeeklyActivity activity : activities) {
            activityDataList.add(new ActivityData(activity));
        }

        return activityDataList;
    }
}
