package webapp.servlets.xml;

import com.google.gson.Gson;
import engine.Engine;
import engine.exception.XmlException;
import engine.member.Member;
import engine.reservation.Reservation;
import webapp.common.ReservationData;
import webapp.constants.Constants;
import webapp.utils.ServletUtils;
import webapp.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "ExportDataServlet", urlPatterns = "/exportData")
public class ExportDataServlet extends HttpServlet {
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
            RequestData requestData = gson.fromJson(jsonString,RequestData.class);
            String xmlString;


            try{
                switch (requestData.typeOfData) {
                    case "activities":
                        xmlString = engine.exportActivities();
                        break;
                    case "boats":
                        xmlString = engine.exportBoats();
                        break;
                    case "members":
                        xmlString = engine.exportMembers();
                        break;
                    default:
                        xmlString = null;
                }
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(xmlString);
                out.flush();
            }
            catch (XmlException e){
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.print(e.getMessage());
                out.flush();
            }

        }
    }

    private static class RequestData {
        String typeOfData;
    }

}
