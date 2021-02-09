package webapp.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import engine.Engine;
import engine.activity.WeeklyActivity;
import engine.boat.BoatCrew;
import engine.boat.BoatType;
import engine.reservation.Reservation;
import webapp.common.ActivityData;
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
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@WebServlet(name = "CreateReservationServlet", urlPatterns = "/createReservation")
public class CreateReservationServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Engine engine = ServletUtils.getEngine(getServletContext());
        String userId = SessionUtils.getUserId(req);
        Gson gson = new Gson();
        BufferedReader reader = req.getReader();
        String jsonString = reader.lines().collect(Collectors.joining());
        ReservationData reservationData = gson.fromJson(jsonString, ReservationData.class);
        WeeklyActivity activity;
        if (reservationData.getActivity() == null) {
            activity = (WeeklyActivity)getServletContext().getAttribute(Constants.DUMMY_ACTIVITY);
        } else {
            activity = engine.findActivity(reservationData.getActivity().getName(),
                    reservationData.getActivity().getTime());
        }

        Reservation reservation = reservationData.createReservation(userId, activity);
        engine.publishNewReservation(reservation, false);
        ServerUtils.saveSystemState(getServletContext());
        resp.setStatus(HttpServletResponse.SC_OK);
    }

}
