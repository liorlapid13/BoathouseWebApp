package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.member.Member;
import engine.notification.Notification;
import webapp.common.NotificationData;
import webapp.utils.ServletUtils;
import webapp.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "NotificationsServlet", urlPatterns = "/notifications")
public class NotificationsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try (PrintWriter out = resp.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            String userId = SessionUtils.getUserId(req);
            Member member = engine.findMemberByID(userId);
            List<Notification> notifications = member.getNotifications();
            member.clearNotifications();
            List<NotificationData> notificationDataList = new ArrayList<>();
            for (Notification notification : notifications) {
                notificationDataList.add(new NotificationData(notification));
            }
            Gson gson = new Gson();
            String jsonResponse = gson.toJson(notificationDataList);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.print(jsonResponse);
            out.flush();
        }
    }
}
