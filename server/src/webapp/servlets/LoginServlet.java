package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.exception.MemberAlreadyLoggedInException;
import engine.member.Member;
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
import java.util.stream.Collectors;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processLoggedInCheck(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processLoginRequest(req, resp);
    }

    protected void processLoginRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
                    response.setStatus(HttpServletResponse.SC_OK);
                    out.print(Constants.HOME_PAGE_URL);
                    out.flush();

                } else {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    out.print("Incorrect email and/or password");
                }
            } catch (MemberAlreadyLoggedInException e) {
                response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.print(e.getMessage());
                out.flush();
            }
        }
    }

    private static class User {
        String email;
        String password;

        public String getEmail() {
            return email;
        }
        public String getPassword() {
            return password;
        }
    }

    protected void processLoggedInCheck(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try (PrintWriter out = response.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            String userId = SessionUtils.getUserId(request);
            if (userId != null) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(Constants.HOME_PAGE_URL);
                out.flush();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }
}
