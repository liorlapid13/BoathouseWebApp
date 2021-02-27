package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import webapp.common.MemberData;
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
            String userId = SessionUtils.getUserId(request);
            Gson gson = new Gson();
            BufferedReader reader = request.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            RemovalRequestData removalRequestData = gson.fromJson(jsonString, RemovalRequestData.class);
            String memberToRemoveId = removalRequestData.memberToRemove.getId();
            boolean isLoggedInMember = engine.isMemberLoggedIn(memberToRemoveId);

            if (!isLoggedInMember){
                if (removalRequestData.memberHasFutureReservation) {
                    engine.removeMemberFromFutureReservations(engine.findMemberByID(memberToRemoveId), userId);
                }
                engine.removeMember(engine.findMemberByID(memberToRemoveId));
                ServerUtils.saveSystemState(getServletContext());
                response.setStatus(HttpServletResponse.SC_OK);
            }
            else{
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                out.println("This member is logged in, you cannot remove him");
            }
        }

    }

    private static class RemovalRequestData{
        MemberData memberToRemove;
        boolean memberHasFutureReservation;
    }

}