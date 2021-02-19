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

@WebServlet(name = "PrivateBoatAvailabilityServlet", urlPatterns = {"/privateBoatAvailability"})
public class PrivateBoatAvailabilityServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        checkPrivateBoatAvailability(req, resp);
    }

    protected void checkPrivateBoatAvailability(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        RequestData requestData = gson.fromJson(jsonString, RequestData.class);
        String boatId = requestData.boatId;
        if (engine.isBoatPrivate(boatId) && !engine.doesBoatBelongToMember(boatId)) {
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }

    private static class RequestData {
        String boatId;
    }
}

