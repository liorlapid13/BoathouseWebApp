package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import webapp.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "BoatAvailabilityServlet", urlPatterns = {"/boatAvailability"})
public class BoatAvailabilityServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        checkBoatAvailability(req, resp);
    }

    protected void checkBoatAvailability(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        RequestData requestData = gson.fromJson(jsonString, RequestData.class);
        if (requestData.id == null) {
            if (engine.doesBoatNameExist(requestData.name)) {
                resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        } else {
            if (engine.doesBoatSerialNumberExist(requestData.id)) {
                resp.setStatus(HttpServletResponse.SC_SEE_OTHER);
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }

    private static class RequestData {
        String id;
        String name;
    }
}
