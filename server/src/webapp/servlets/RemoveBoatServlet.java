package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
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
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet(name = "RemoveBoatServlet", urlPatterns = {"/removeBoat"})
public class RemoveBoatServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Gson gson = new Gson();
            BufferedReader reader = request.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            removeBoatRequestData requestData = gson.fromJson(jsonString, removeBoatRequestData.class);
            String boatToRemoveId = requestData.boatToRemove.getId();

            if(requestData.boatHasFutureAssignment){
                engine.removeBoatFromFutureAssignments(engine.findBoatByID(boatToRemoveId));
            }
            engine.removeBoat(engine.findBoatByID(boatToRemoveId));
            response.setStatus(HttpServletResponse.SC_OK);
            ServerUtils.saveSystemState(getServletContext());
        }
    }
    private static class removeBoatRequestData {
        BoatData boatToRemove;
        boolean boatHasFutureAssignment;
    }

}



