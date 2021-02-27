package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.member.Member;
import webapp.common.ActivityData;
import webapp.common.MemberData;
import webapp.constants.Constants;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "MembersForReservationServlet", urlPatterns = {"/membersForReservation"})
public class MembersForReservationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req,resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            String userId = SessionUtils.getUserId(req);
            Member member = engine.findMemberByID(SessionUtils.getUserId(req));
            Gson gson = new Gson();
            BufferedReader reader = req.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            RequestData requestData = gson.fromJson(jsonString, RequestData.class);
            WeeklyActivity activity;
            if (requestData.activity == null) {
                LocalTime startTime = ServerUtils.parseStartTime(requestData.manualTime);
                LocalTime endTime = ServerUtils.parseEndTime(requestData.manualTime);
                activity = new WeeklyActivity(member.getName() + "'s activity", startTime, endTime, null);
                getServletContext().setAttribute(Constants.DUMMY_ACTIVITY + userId, activity);
            } else {
                activity = engine.findActivity(requestData.activity.getName(), requestData.activity.getTime());
            }
            LocalDate date = ServerUtils.parseDate(requestData.date);
            List<Member> availableMembers =
                    engine.findAvailableMembersForReservation(activity, date);
            if (availableMembers.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                List<MemberData> memberDataList = parseAvailableMembers(availableMembers);
                String jsonResponse = gson.toJson(memberDataList);
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    private List<MemberData> parseAvailableMembers(List<Member> members) {
        List<MemberData> memberDataList = new ArrayList<>();
        for (Member member : members) {
            memberDataList.add(new MemberData(member));
        }

        return memberDataList;
    }

    private static class RequestData {
        ActivityData activity;
        String date;
        String manualTime;
    }
}
