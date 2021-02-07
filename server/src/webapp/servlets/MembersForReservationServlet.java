package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.member.Member;
import webapp.common.ActivityData;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "membersForReservationServlet", urlPatterns = {"/membersForReservation"})
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
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Member member = engine.findMemberByID(SessionUtils.getUserId(req));
            Gson gson = new Gson();
            BufferedReader reader = req.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            RequestData requestData = gson.fromJson(jsonString, RequestData.class);
            WeeklyActivity activity;
            if (requestData.activity == null) {
                String[] activityTimes = requestData.manualTime.split("-", 2);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
                LocalTime startTime = LocalTime.parse(activityTimes[0], formatter);
                LocalTime endTime = LocalTime.parse(activityTimes[1], formatter);
                activity = new WeeklyActivity(member.getName() + "'s activity", startTime, endTime, null);
                getServletContext().setAttribute(Constants.DUMMY_ACTIVITY, activity);
            } else {
                activity = engine.findActivity(requestData.activity.getName(), requestData.activity.getTime());
            }
            List<MemberData> memberDataList = new ArrayList<>();
            List<Member> availMembers = engine.findAvailableMembersForReservation(activity, Integer.parseInt(requestData.day));
            if (availMembers.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else {
                for (Member availMember : availMembers) {
                    parseMemberData(availMember, memberDataList);
                }
                String jsonResponse = gson.toJson(memberDataList);
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    private void parseMemberData(Member member, List<MemberData> memberDataList) {
        String id = member.getSerialNumber();
        String name = member.getName();
        String email = member.getEmail();
        MemberData memberData = new MemberData(id, name, email);

        memberDataList.add(memberData);
    }

    private static class RequestData {
        ActivityData activity;
        String day;
        String manualTime;

        public RequestData(ActivityData activity, String day, String manualTime) {
            this.activity = activity;
            this.day = day;
            this.manualTime = manualTime;
        }
    }

    public static class MemberData {
        String id;
        String name;
        String email;

        public MemberData(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
    }
}
