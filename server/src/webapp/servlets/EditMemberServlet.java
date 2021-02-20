package webapp.servlets;


import com.google.gson.Gson;
import engine.Engine;
import engine.exception.OutOfEnumRangeException;
import engine.member.MemberLevel;
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
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@WebServlet(name = "EditMemberServlet", urlPatterns = "/editMember")
public class EditMemberServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        editMember(req, resp);
    }

    protected void editMember(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        MemberData memberData = gson.fromJson(jsonString, MemberData.class);
        MemberLevel level;
        try {
            level = MemberLevel.of(Integer.parseInt(memberData.getLevel()));
        } catch (OutOfEnumRangeException e) {
            level = MemberLevel.BEGINNER;
        }
        LocalDateTime expirationDate = ServerUtils.parseDateTime(memberData.getExpirationDate());
        engine.editMember(memberData.getId(), memberData.getName(), memberData.getEmail(), memberData.getPassword(),
                Integer.parseInt(memberData.getAge()), memberData.getDetails(), memberData.getPhoneNumber(), level,
                memberData.isManager(), memberData.isHasBoat(), memberData.getPrivateBoatId(), expirationDate);
        ServerUtils.saveSystemState(getServletContext());
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
