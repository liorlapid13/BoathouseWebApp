package engine;

import engine.activity.WeeklyActivity;
import engine.assignment.Assignment;
import engine.boat.Boat;
import engine.boat.BoatCrew;
import engine.boat.BoatType;
import engine.member.Member;
import engine.member.MemberLevel;
import engine.reservation.Reservation;
import engine.reservation.ReservationViewFilter;
import engine.exception.EmailAlreadyExistsException;
import engine.exception.MemberAlreadyLoggedInException;
import engine.exception.XmlException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface BMSEngine {
    //Member connection related methods
    void logoutMember(String id);
    boolean isMemberLoggedIn(String id);
    Member findAndLoginMember(String email, String password) throws MemberAlreadyLoggedInException;

    //Member related methods
    void addMemberToList(Member newMember);
    void removeMemberByListIndex(int memberIndex);
    void removeMemberFromFutureReservations(Member member);
    void updateMemberAge(int age, String id);
    void updateMemberDetails(String details, String id);
    void updateMemberLevel(MemberLevel level, String id);
    void updateMemberExpirationDate(LocalDateTime expirationDate, String id);
    void removePrivateBoat(Member member);
    void addPrivateBoat(Member member, String boatId);
    void updateMemberManagerialStatus(boolean isManager, String id);
    boolean doesMemberSerialNumberExist(String serialNumber);
    boolean isEmailAvailable(String email) throws EmailAlreadyExistsException;
    boolean doesMemberHaveFutureReservation(Member member);
    Member findMemberByID(String id);
    Member updateMemberName(String name, String id);
    Member updateMemberPhoneNumber(String phoneNumber, String id);
    Member updateMemberEmail(String email, String id) throws EmailAlreadyExistsException;
    Member updateMemberPassword(String password, String id);
    List<Member> getMemberList();
    List<Member> findMemberListByIDList(List<String> idList);

    //Boat related methods
    void updateBoatType(BoatType boatType, Boat boatToEdit);
    void removeBoatFromFutureAssignments(Boat boat);
    void updateBoatOwnershipStatus(boolean isPrivate, Boat boatToEdit);
    void updateBoatStatus(boolean isDisabled, Boat boatToEdit);
    void updateBoatCoastalStatus(boolean isCoastal, Boat boatToEdit);
    void removeBoatByListIndex(int boatListIndex);
    void addBoatToList(Boat newBoat);
    void updateBoatName(String newBoatName, Boat boatToEdit);
    boolean isBoatAvailableForActivity(Boat boat, WeeklyActivity activity, LocalDate activityDate);
    boolean doesBoatNameExist(String name);
    boolean doesBoatHaveFutureAssignments(Boat boat);
    boolean doesBoatSerialNumberExist(String serialNumber);
    boolean isBoatPrivate(String boatSerialNumber);
    boolean doesBoatBelongToMember(String boatSerialNumber);
    Boat findBoatByID(String id);
    List<Boat> getBoatList();

    //Reservation related methods
    void publishNewReservation(Reservation reservation, boolean isReservationInList);
    void removeReservation(Reservation reservation, boolean override);
    void editReservationActivity(Reservation reservation, WeeklyActivity activity);
    void editReservationActivityDate(Reservation reservation, LocalDate date);
    void updateReservationBoatTypes(Reservation reservation, Set<BoatType> boatTypes);
    void splitReservation(Reservation originalReservation, Reservation newReservation, BoatCrew newBoatCrew);
    boolean isValidActivityForReservationBoatCrew(Reservation reservation, WeeklyActivity activity, LocalDate activityDate);
    List<Boat> getBoatsForReservation(Reservation reservation);
    Reservation combineReservations(Reservation destination, Reservation sourceReservation, boolean assignCoxswain);
    Reservation updateReservationCrewMembers(Reservation reservation, List<String> updatedCrewMembers);
    Reservation removeCoxswainFromReservation(Reservation reservation);
    Reservation addCoxswainToReservation(Reservation reservation, String coxswainId);
    Reservation addCrewMemberToReservation(Reservation reservation, String memberId);
    List<Reservation> getNextWeekReservations(ReservationViewFilter viewFilter);
    List<Reservation> getSpecificDateReservations(LocalDate date, ReservationViewFilter viewFilter);
    List<Reservation> getFutureUnconfirmedReservations();
    List<Reservation> getCombinableReservations(Reservation originalReservation, int maxCrewSize);


    //Activity related methods
    void addWeeklyActivityToList(WeeklyActivity newWeeklyActivity);
    void removeActivityByListIndex(int activityListIndex);
    boolean doesActivityExist(String activityName, LocalTime startTime, LocalTime endTime);
    List<Boat> getBoatsForActivity(WeeklyActivity activity, LocalDate activityDate);
    List<WeeklyActivity> getWeeklyActivities();

    //Assignment related methods
    void addAssignment(Assignment assignment);
    void removeAssignment(Assignment assignment, boolean override);
    List<Assignment> getNextWeekAssignments();
    List<Assignment> getSpecificDateAssignments(LocalDate date);
    List<Assignment> getTodayAssignments();
    List<Assignment> getFutureAssignments();
    List<Assignment> getPastAssignments();

    // Xml related methods
    void importMembers(String membersXmlString, boolean isOverride) throws XmlException;
    void importBoats(String boatsXmlString, boolean isOverride) throws XmlException;
    void importActivities(String activitiesXmlString, boolean isOverride) throws XmlException;
    String exportMembers() throws XmlException;
    String exportBoats() throws XmlException;
    String exportActivities() throws XmlException;
}
