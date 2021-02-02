package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.exception.MemberAlreadyLoggedInException;
import engine.member.Member;
import webapp.constants.Constants;
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

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Gson gson = new Gson();
            BufferedReader reader = request.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            User user = gson.fromJson(jsonString, User.class);
            String userEmail = user.getEmail();
            String userPassword = user.getPassword();

            try {
                Member loggedInMember = engine.findAndLoginMember(userEmail, userPassword);
                if (loggedInMember != null) {
                    request.getSession(true).setAttribute(Constants.USERID, loggedInMember.getSerialNumber());
                    String redirectURL = loggedInMember.isManager() ?
                            Constants.MANAGER_PAGE_URL : Constants.MEMBER_PAGE_URL;
                    response.sendRedirect(redirectURL);
                } else {
                    out.print("Incorrect email and/or password");
                }
            } catch (MemberAlreadyLoggedInException e) {
                out.print(e.getMessage());
            }
        }
    }

    public static class User {
        String email;
        String password;

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }
    }
}
