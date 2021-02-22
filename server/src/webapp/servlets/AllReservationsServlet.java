package webapp.servlets;


import com.google.gson.Gson;
import engine.Engine;
import engine.reservation.Reservation;
import engine.reservation.ReservationViewFilter;
import webapp.common.ReservationData;
import webapp.utils.ServletUtils;

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
import java.util.List;
import java.util.stream.Collectors;

@WebServlet(name = "AllReservationsServlet", urlPatterns = {"/allReservations"})
public class AllReservationsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        processRequest(req, resp);
    }
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Engine engine = ServletUtils.getEngine(getServletContext());
            Gson gson = new Gson();
            BufferedReader reader = request.getReader();
            String jsonString = reader.lines().collect(Collectors.joining());
            RequestData requestData = gson.fromJson(jsonString, RequestData.class);
            List<ReservationData> reservationDataList = new ArrayList<>();
            List<Reservation> reservationList;

            switch (requestData.requestType) {
                case "next":
                    reservationList = engine.getNextWeekReservations(ReservationViewFilter.values()[requestData.filterIndex]);
                    break;
                case "day":
                    reservationList = engine.getSpecificDateReservations(
                            LocalDate.now().plusDays(Integer.parseInt(requestData.daysFromToday)),
                            ReservationViewFilter.values()[requestData.filterIndex]);
                    break;
                default:
                    reservationList = new ArrayList<>();
            }

            if (reservationList.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                ReservationData.parseReservations(reservationList, reservationDataList, engine);
                String jsonResponse = gson.toJson(reservationDataList);
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(jsonResponse);
                out.flush();
            }
        }
    }

    private static class RequestData {
        int filterIndex;
        String requestType;
        String daysFromToday;
    }
}