package webapp.servlets.chat;

import com.google.gson.Gson;
import engine.chat.ChatManager;
import webapp.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

@WebServlet(name = "UsersListServlet", urlPatterns = "/users")
public class UsersListServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        getUsersList(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getUsersList(req, resp);
    }

    protected void getUsersList(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            Gson gson = new Gson();
            ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
            Set<String> usersList = chatManager.getUsers();
            String json = gson.toJson(usersList);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.println(json);
            out.flush();
        }
    }
}
