package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.exception.EmailAlreadyExistsException;
import engine.member.Member;
import webapp.utils.ServerUtils;
import webapp.utils.ServletUtils;
import webapp.utils.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet(name = "EditPersonalDetailsServlet", urlPatterns = {"/editPersonalDetails"})
public class EditPersonalDetailsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        getPersonalDetails(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (PrintWriter out = response.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            String userId = SessionUtils.getUserId(request);
            Gson gson = new Gson();
            BufferedReader reader = request.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            MemberDetails memberDetails = gson.fromJson(jsonString, MemberDetails.class);
            String email = memberDetails.getEmail();
            String password = memberDetails.getPassword();
            String name = memberDetails.getName();
            String phoneNumber = memberDetails.getPhoneNumber();

            try {
                engine.updateMemberEmail(email, userId);
                engine.updateMemberPassword(password, userId);
                engine.updateMemberName(name, userId);
                engine.updateMemberPhoneNumber(phoneNumber, userId);
                response.setStatus(HttpServletResponse.SC_OK);
                ServerUtils.saveSystemState(getServletContext());
            } catch (EmailAlreadyExistsException e) {
                response.setStatus(HttpServletResponse.SC_SEE_OTHER);
                out.print(e.getMessage());
            }
        }
    }

    protected void getPersonalDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userId = SessionUtils.getUserId(request);
        Member member = engine.findMemberByID(userId);
        MemberDetails memberDetails = new MemberDetails(member);
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(memberDetails);
        try (PrintWriter out = response.getWriter()) {
            out.print(jsonResponse);
            out.flush();
        }
    }

    public static class MemberDetails {
        String email;
        String password;
        String name;
        String phoneNumber;

        public MemberDetails(Member member) {
            this.name = member.getName();
            this.email = member.getEmail();
            this.password = member.getPassword();
            this.phoneNumber = member.getPhoneNumber();
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }
}
