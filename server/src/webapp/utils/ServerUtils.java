package webapp.utils;

import engine.Engine;
import engine.boat.BoatCrew;
import engine.boat.BoatType;
import webapp.common.MemberData;

import javax.servlet.ServletContext;
import javax.swing.text.DateFormatter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ServerUtils {
    public static void saveSystemState(ServletContext servletContext) {
        try {
            Engine engine = ServletUtils.getEngine(servletContext);
            File file = new File("system_state.xml");
            JAXBContext jaxbContext = JAXBContext.newInstance(Engine.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(engine, file);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public static LocalTime parseStartTime(String time) {
        String[] times = time.split("-", 2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

        return LocalTime.parse(times[0], formatter);
    }

    public static LocalTime parseEndTime(String time) {
        String[] times = time.split("-", 2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");

        return LocalTime.parse(times[1], formatter);
    }

    public static LocalDate parseDate(String date) {
        String[] dates = date.split(" ", 2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return LocalDate.parse(dates[1], formatter);
    }

    public static BoatCrew createBoatCrew(MemberData[] boatCrew, MemberData coxswain, boolean coxswainSelected) {
        String coxswainId = coxswainSelected ? coxswain.getId() : null;
        List<String> crewMembers = new ArrayList<>();
        for (MemberData member : boatCrew) {
            crewMembers.add(member.getId());
        }

        return new BoatCrew(crewMembers, coxswainId);
    }

    public static Set<BoatType> parseBoatTypes(String[] boatTypes) {
        Set<BoatType> boatTypesSet = new HashSet<>();
        for (int i = 0; i < boatTypes.length; i++) {
            boatTypesSet.add(BoatType.boatCodeToBoatType(boatTypes[i]));
        }

        return boatTypesSet;
    }

    public static LocalDateTime parseDateTime(String expirationDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return LocalDate.parse(expirationDate, formatter).atStartOfDay();
    }
}
