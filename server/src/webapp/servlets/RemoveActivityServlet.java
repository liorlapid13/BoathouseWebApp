package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import webapp.common.ActivityData;
import webapp.utils.ServerUtils;
import webapp.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "RemoveActivityServlet", urlPatterns = {"/removeActivity"})
public class RemoveActivityServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        Gson gson = new Gson();
        BufferedReader reader = request.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        ActivityData activityToRemove = gson.fromJson(jsonString, ActivityData.class);
        engine.removeActivity(engine.findActivity(activityToRemove.getName(), activityToRemove.getTime()));
        response.setStatus(HttpServletResponse.SC_OK);
        ServerUtils.saveSystemState(getServletContext());
    }


}
