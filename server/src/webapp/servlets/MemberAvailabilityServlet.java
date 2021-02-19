package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.exception.EmailAlreadyExistsException;
import webapp.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "MemberAvailabilityServlet", urlPatterns = {"/memberAvailability"})
public class MemberAvailabilityServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        checkMemberAvailability(req, resp);
    }

    protected void checkMemberAvailability(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        RequestData requestData = gson.fromJson(jsonString, RequestData.class);
        if (requestData.id == null) {
            try {
                engine.isEmailAvailable(requestData.email);
                resp.setStatus(HttpServletResponse.SC_OK);
            } catch (EmailAlreadyExistsException e) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            }
        } else {
            if (engine.doesMemberSerialNumberExist(requestData.id)) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
            }
        }
    }

    private static class RequestData {
        String id;
        String email;
    }
}
