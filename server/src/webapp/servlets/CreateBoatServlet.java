package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.boat.Boat;
import webapp.common.BoatData;
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

@WebServlet(name = "CreateBoatServlet", urlPatterns = {"/createBoat"})
public class CreateBoatServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        createBoat(req, resp);
    }

    protected void createBoat(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        BoatData boatData = gson.fromJson(jsonString, BoatData.class);
        Boat newBoat = boatData.createBoat();
        engine.addBoatToList(newBoat);
        ServerUtils.saveSystemState(getServletContext());
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
