package webapp.servlets.chat;

import com.google.gson.Gson;
import engine.Engine;
import engine.chat.ChatManager;
import engine.member.Member;
import webapp.utils.ServletUtils;
import webapp.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.stream.Collectors;

@WebServlet(name = "SendMessageServlet", urlPatterns = {"/sendMessage"})
public class SendMessageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        sendMessage(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        sendMessage(req, resp);
    }

    protected void sendMessage(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        Member member = engine.findMemberByID(SessionUtils.getUserId(req));
        String username = member.getName() + member.getSerialNumber();
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        String message = gson.fromJson(jsonString, String.class);
        if (message != null && !message.trim().isEmpty()) {
            chatManager.addMessage(message, username);
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }
}
