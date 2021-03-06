package webapp.servlets;

import com.google.gson.Gson;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.boat.BoatCrew;
import engine.boat.BoatType;
import webapp.common.ActivityData;
import webapp.common.MemberData;
import webapp.common.ReservationData;
import webapp.constants.Constants;
import webapp.utils.ServerUtils;
import webapp.utils.ServletUtils;
import webapp.utils.SessionUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "EditReservationServlet", urlPatterns = {"/editReservation"})
public class EditReservationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userId = SessionUtils.getUserId(req);
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        RequestData requestData = gson.fromJson(jsonString, RequestData.class);
        LocalDate date = ServerUtils.parseDate(requestData.date);
        WeeklyActivity activity;
        if (requestData.isDummyActivity) {
            activity = (WeeklyActivity)getServletContext().getAttribute(Constants.DUMMY_ACTIVITY + userId);
        } else {
            activity = engine.findActivity(requestData.activity.getName(), requestData.activity.getTime());
        }
        Set<BoatType> boatTypes = ServerUtils.parseBoatTypes(requestData.boatTypes);
        boolean coxswainSelected = requestData.coxswain != null;
        BoatCrew boatCrew = ServerUtils.createBoatCrew(requestData.boatCrew, requestData.coxswain, coxswainSelected);
        engine.editReservationNotification(engine.findReservationByID(requestData.reservation.getId()), userId);
        engine.editReservation(requestData.reservation.getId(), date, activity, boatTypes, boatCrew, requestData.reservator.getId());
        ServerUtils.saveSystemState(getServletContext());
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private static class RequestData {
        ReservationData reservation;
        String date;
        ActivityData activity;
        boolean isDummyActivity;
        String[] boatTypes;
        MemberData[] boatCrew;
        MemberData coxswain;
        MemberData reservator;
    }
}
