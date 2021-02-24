package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.boat.BoatType;
import webapp.common.BoatData;
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
import java.util.stream.Collectors;

@WebServlet(name = "EditBoatServlet", urlPatterns = "/editBoat")
public class EditBoatServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        editBoat(req, resp);
    }

    protected void editBoat(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userId = SessionUtils.getUserId(req);
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        BoatData boatData = gson.fromJson(jsonString, BoatData.class);
        engine.editBoat(boatData.getId(), boatData.getName(), BoatType.boatCodeToBoatType(boatData.getBoatType()),
                boatData.isCoastal(), boatData.isPrivate(), boatData.isDisabled(), userId);
        ServerUtils.saveSystemState(getServletContext());
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
