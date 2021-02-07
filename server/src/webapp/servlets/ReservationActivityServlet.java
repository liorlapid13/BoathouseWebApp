package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
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
import java.util.stream.Collectors;

@WebServlet(name = "ReservationActivityServlet", urlPatterns = {"/reservationActivity"})
public class ReservationActivityServlet extends HttpServlet {
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

        }
    }

    private static class RequestData {
        String day;
        ActivityData activity;
        String boatCrew;
    }
}
