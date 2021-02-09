package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import webapp.common.ActivityData;
import webapp.common.MemberData;
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

@WebServlet(name = "RemoveMemberServlet", urlPatterns = {"/removeMember"})
public class RemoveMemberServlet extends HttpServlet {

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
            String memberToRemoveId = requestData.memberToRemove.getId();
            boolean isLoggedInMember = engine.isMemberLoggedIn(memberToRemoveId);

            if(!isLoggedInMember){
                if(requestData.memberHasFutureReservation){
                    engine.removeMemberFromFutureReservations(engine.findMemberByID(memberToRemoveId));
                }
                engine.removeMember(engine.findMemberByID(memberToRemoveId));
                response.setStatus(HttpServletResponse.SC_OK);
                ServerUtils.saveSystemState(getServletContext());
            }
            else{
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.println("This member is logged in,you cannot remove him");
            }
        }

    }

    private static class RequestData{
        MemberData memberToRemove;
        boolean memberHasFutureReservation;
    }

}