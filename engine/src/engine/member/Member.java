package engine.member;

import engine.activity.WeeklyActivity;
import engine.reservation.Reservation;
import engine.adapter.LocalDateTimeAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@XmlRootElement
public class Member implements Serializable {
    public static final int MINIMUM_AGE = 18;
    public static final int MAXIMUM_AGE = 90;

    private String serialNumber;
    private String name;
    private int age;
    private String details;
    private LocalDateTime registrationDate;
    private MemberLevel level;
    private LocalDateTime membershipExpirationDate;
    private boolean hasBoat;
    private String privateBoatSerialNumber;
    private String phoneNumber;
    private String email;
    private String password;
    private boolean isManager;
    private List<Reservation> reservationList;

    public Member() {
        reservationList = new ArrayList<>();
    }

    public Member(String serialNumber, String name, int age, String details, MemberLevel level, boolean hasBoat,
                  String privateBoatSerialNumber, String phoneNumber, String email, String password,
                  boolean isManager, LocalDateTime registrationDate, LocalDateTime membershipExpirationDate) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.age = age;
        this.details = details;
        this.registrationDate = registrationDate;
        this.level = level;
        this.membershipExpirationDate = membershipExpirationDate;
        this.hasBoat = hasBoat;
        this.privateBoatSerialNumber = this.hasBoat ? privateBoatSerialNumber : null;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
        this.isManager = isManager;
        this.reservationList = new ArrayList<>();
    }

    @XmlAttribute
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @XmlJavaTypeAdapter(value = LocalDateTimeAdapter.class)
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    @XmlTransient
    public void setReservationList(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }

    public String getSerialNumber() { return serialNumber; }

    public String getName() {
        return name;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    @XmlAttribute
    public void setEmail(String email) {
        this.email = email;
    }

    @XmlAttribute
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    @XmlAttribute
    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isManager() {
        return isManager;
    }

    public String getPrivateBoatSerialNumber() { return privateBoatSerialNumber; }

    public List<Reservation> getReservationList() {
        return reservationList;
    }

    public List<Reservation> getPastWeekReservationList() {
        List<Reservation> pastWeekReservationList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate oneWeekAgo = today.minusDays(7);

        for (Reservation reservation : reservationList) {
            if (reservation.getActivityDate().isBefore(today) && reservation.getActivityDate().isAfter(oneWeekAgo)) {
                pastWeekReservationList.add(reservation);
            }
        }

        return pastWeekReservationList;
    }

    public List<Reservation> getFutureReservationList() {
        List<Reservation> futureReservationList = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalTime timeNow = LocalTime.now();

        for (Reservation reservation : reservationList) {
            if (reservation.getActivityDate().isAfter(today) ||
                    (reservation.getActivityDate().equals(today) &&
                            reservation.getWeeklyActivity().getStartTime().isAfter(timeNow))) {
                futureReservationList.add(reservation);
            }
        }

        return futureReservationList;
    }

    public List<Reservation> getNextWeekReservations() {
        LocalDate today = LocalDate.now(), todayPlusEightDays = today.plusDays(8);
        List<Reservation> reservations = this.reservationList.stream()
                .filter(reservation -> reservation.getActivityDate().isAfter(today) &&
                        reservation.getActivityDate().isBefore(todayPlusEightDays))
                .collect(Collectors.toList());

        return reservations;
    }

    public List<Reservation> getSpecificDateReservations(LocalDate date) {
        List<Reservation> reservations = this.reservationList.stream()
                .filter(reservation -> reservation.getActivityDate().equals(date))
                .collect(Collectors.toList());

        return reservations;
    }

    public List<Reservation> getEditableReservationList() {
        List<Reservation> futureReservations = getFutureReservationList();
        List<Reservation> futureUnconfirmedReservations = new ArrayList<>();

        for (Reservation reservation : futureReservations) {
            if (!reservation.isConfirmed()) {
                futureUnconfirmedReservations.add(reservation);
            }
        }

        return futureUnconfirmedReservations;
    }

    @XmlAttribute
    public void setManager(boolean isManager) {
        this.isManager = isManager;
    }

    @XmlAttribute
    public void setHasBoat(boolean hasBoat) {
        this.hasBoat = hasBoat;
    }

    @XmlAttribute
    public void setPrivateBoatSerialNumber(String privateBoatSerialNumber) {
        this.privateBoatSerialNumber = privateBoatSerialNumber;
    }

    public boolean isHasBoat() {
        return hasBoat;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    @XmlAttribute
    public void setAge(int age) {
        this.age = age;
    }

    @XmlAttribute
    public void setDetails(String details) {
        this.details = details;
    }

    @XmlAttribute
    public void setLevel(MemberLevel level) {
        this.level = level;
    }

    @XmlJavaTypeAdapter(value = LocalDateTimeAdapter.class)
    public void setMembershipExpirationDate(LocalDateTime membershipExpirationDate) {
        this.membershipExpirationDate = membershipExpirationDate;
    }

    public String getDetails() {
        return details;
    }

    public MemberLevel getLevel() {
        return level;
    }

    public LocalDateTime getMembershipExpirationDate() {
        return membershipExpirationDate;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void removeReservation(Reservation reservation) {
        reservationList.remove(reservation);
    }

    public void addReservation(Reservation reservation) {
        reservationList.add(reservation);
    }

    public static boolean isValidAge(int age) {
        return age >= MINIMUM_AGE && age <= MAXIMUM_AGE;
    }

    public boolean isValidMembershipExpirationDate(LocalDateTime newExpirationDate) {
        return this.registrationDate.isBefore(newExpirationDate);
    }

    public void removePrivateBoat() {
        hasBoat = false;
        privateBoatSerialNumber = null;
    }

    public void addPrivateBoat(String boatId) {
        hasBoat = true;
        privateBoatSerialNumber = boatId;
    }

    public boolean isActivityTimeAndDateAvailable(WeeklyActivity activity, LocalDate date) {
        List<Reservation> reservations = getFutureReservationList();
        boolean isAvailable = true;

        for (Reservation reservation : reservations) {
            if (reservation.getActivityDate().equals(date)) {
                WeeklyActivity reservationActivity = reservation.getWeeklyActivity();
                LocalTime activityStartTime = reservationActivity.getStartTime();
                LocalTime activityEndTime = reservationActivity.getEndTime();

                if (activity.getStartTime().isBefore(activityStartTime) &&
                        activity.getEndTime().isAfter(activityStartTime)) {
                    isAvailable = false;
                }
                else if (activity.getStartTime().isAfter(activityStartTime) &&
                        activity.getStartTime().isBefore(activityEndTime)) {
                    isAvailable = false;
                }
                else if (activity.getStartTime().equals(activityStartTime) &&
                        activity.getEndTime().equals(activityEndTime)) {
                    isAvailable = false;
                }
            }

            if (!isAvailable) {
                break;
            }
        }

        return isAvailable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Member member = (Member) o;
        return serialNumber == member.serialNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }

    public List<Reservation> getPastConfirmedReservations() {
        return this.reservationList.stream()
                .filter(reservation -> reservation.getActivityDate().isBefore(LocalDate.now()))
                .filter(reservation -> reservation.isConfirmed())
                .collect(Collectors.toList());
    }
}
