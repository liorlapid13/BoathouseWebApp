package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.exception.XmlException;
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

@WebServlet(name = "ImportDataServlet", urlPatterns = "/importData")
public class ImportDataServlet extends HttpServlet {
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
            RequestData requestData = gson.fromJson(jsonString, RequestData.class);

            try{
                switch (requestData.typeOfData) {
                    case "activities":
                        engine.importActivities(requestData.xmlString,requestData.override);
                        break;
                    case "boats":
                        engine.importBoats(requestData.xmlString,requestData.override);
                        break;
                    case "members":
                        engine.importMembers(requestData.xmlString,requestData.override);
                        break;
                }
                ServerUtils.saveSystemState(getServletContext());
                response.setStatus(HttpServletResponse.SC_OK);
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
        String xmlString;
        boolean override;
    }

}
