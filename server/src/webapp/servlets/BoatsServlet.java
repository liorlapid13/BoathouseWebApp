package webapp.servlets;


import com.google.gson.Gson;
import engine.Engine;
import engine.boat.Boat;
import webapp.common.BoatData;
import webapp.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "BoatsServlet", urlPatterns = "/boats")
public class BoatsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getAllBoats(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doesBoatHasFutureAssignments(req,resp);
    }

    protected void getAllBoats(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Gson gson = new Gson();
            List<Boat> boats = engine.getBoatList();
            if (boats.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                List<BoatData> boatsData = parseBoats(boats);
                String jsonResponse = gson.toJson(boatsData);
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    private List<BoatData> parseBoats(List<Boat> members) {
        List<BoatData> boatDataList = new ArrayList<>();
        for (Boat boat : members) {
            boatDataList.add(new BoatData(boat));
        }

        return boatDataList;
    }

    protected void doesBoatHasFutureAssignments(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        BoatData boat = gson.fromJson(jsonString, BoatData.class);
        if(engine.doesBoatHaveFutureAssignments(engine.findBoatByID(boat.getId()))){
            resp.setStatus(HttpServletResponse.SC_FOUND);
        }
        else{
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

}