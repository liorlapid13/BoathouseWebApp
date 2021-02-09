package webapp.servlets;


import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.member.Member;
import webapp.common.ActivityData;
import webapp.common.MemberData;
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

@WebServlet(name = "MembersServlet", urlPatterns = "/members")
public class MembersServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getAllMembers(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doesMemberHasFutureReservation(req,resp);
    }

    protected void getAllMembers(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Gson gson = new Gson();
            List<Member> members = engine.getMemberList();
            if (members.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                List<MemberData> membersData = parseWeeklyActivities(members);
                String jsonResponse = gson.toJson(membersData);
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    private List<MemberData> parseWeeklyActivities(List<Member> members) {
        List<MemberData> memberDataList = new ArrayList<>();
        for (Member member : members) {
            memberDataList.add(new MemberData(member));
        }

        return memberDataList;
    }

    protected void doesMemberHasFutureReservation(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        MemberData member = gson.fromJson(jsonString, MemberData.class);
        if(engine.doesMemberHaveFutureReservation(engine.findMemberByID(member.getId()))){
            resp.setStatus(HttpServletResponse.SC_FOUND);
        }
        else{
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

}