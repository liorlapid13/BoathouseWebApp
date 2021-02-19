package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.member.Member;
import webapp.common.BoatData;
import webapp.common.MemberData;
import webapp.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "CreateMemberServlet", urlPatterns = {"/createMember"})
public class CreateMemberServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        createMember(req, resp);
    }

    protected void createMember(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        MemberData memberData = gson.fromJson(jsonString, MemberData.class);
        Member member = memberData.createMember();
        engine.addMemberToList(member);
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
