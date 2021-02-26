package webapp.servlets.chat;

import com.google.gson.Gson;
import engine.chat.ChatManager;
import engine.chat.MessageEntry;
import webapp.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "ChatServlet", urlPatterns = "/chat")
public class ChatServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getChat(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        getChat(req, resp);
    }

    private void getChat(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            ChatManager chatManager = ServletUtils.getChatManager(getServletContext());
            Gson gson = new Gson();
            BufferedReader reader = req.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            int chatVersion = gson.fromJson(jsonString, Integer.class);
            int chatManagerVersion = chatManager.getVersion();
            if (chatVersion != chatManagerVersion) {
                List<MessageEntry> chatEntries = chatManager.getMessages(chatVersion);
                MessagesAndVersion messagesAndVersion = new MessagesAndVersion(chatEntries, chatManagerVersion);
                String jsonResponse = gson.toJson(messagesAndVersion);
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            }
        }
    }

    private static class MessagesAndVersion {
        final private List<MessageEntry> entries;
        final private int version;

        public MessagesAndVersion(List<MessageEntry> entries, int version) {
            this.entries = entries;
            this.version = version;
        }
    }
}
