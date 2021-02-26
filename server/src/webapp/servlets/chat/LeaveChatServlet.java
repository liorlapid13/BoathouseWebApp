package webapp.servlets.chat;

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
import java.io.IOException;

@WebServlet(name = "LeaveChatServlet", urlPatterns = "/leaveChat")
public class LeaveChatServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        leaveChat(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        leaveChat(req, resp);
    }

    protected void leaveChat(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
        Member member = engine.findMemberByID(SessionUtils.getUserId(req));
        String username = member.getName() + member.getSerialNumber();
        if (chatManager.isUserExists(username)) {
            chatManager.removeUser(username);
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
