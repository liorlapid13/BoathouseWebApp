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
import engine.xml.handler.XMLHandler;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@XmlRootElement
public class Engine {
    private List<Member> loggedInMembers;
    private List<Member> memberList;
    private List<Boat> boatList;
    private List<Reservation> reservationList;
    private List<Assignment> assignmentList;
    private List<WeeklyActivity> weeklyActivities;
    private XMLHandler xmlHandler;

    public Engine() {
        loggedInMembers = new ArrayList<>();
        memberList = new ArrayList<>();
        boatList = new ArrayList<>();
        reservationList = new ArrayList<>();
        weeklyActivities = new ArrayList<>();
        assignmentList = new ArrayList<>();
    }

    @XmlElementWrapper
    @XmlElement(name="Member")
    public void setMemberList(List<Member> memberList) {
        this.memberList = memberList;
    }

    @XmlElementWrapper
    @XmlElement(name="Boat")
    public void setBoatList(List<Boat> boatList) {
        this.boatList = boatList;
    }

    @XmlElementWrapper
    @XmlElement(name="Reservation")
    public void setReservationList(List<Reservation> reservationList) {
        this.reservationList = reservationList;
    }

    @XmlElementWrapper
    @XmlElement(name="Assignment")
    public void setAssignmentList(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }

    @XmlElementWrapper
    @XmlElement(name="Activity")
    public void setWeeklyActivities(List<WeeklyActivity> weeklyActivities) {
        this.weeklyActivities = weeklyActivities;
    }

    public List<Member> getMemberList() {
        return memberList;
    }

    public List<WeeklyActivity> getWeeklyActivities() { return weeklyActivities; }

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    public List<Boat> getBoatList() { return boatList;}

    public List<Reservation> getReservationList() {
        return reservationList;
    }

    public void createXmlHandler() {
        xmlHandler = new XMLHandler(this);
    }

    public Member findAndLoginMember(String email, String password) throws MemberAlreadyLoggedInException {
        Member loggedInMember = null;

        for (Member member : memberList) {
            if (member.getEmail().equalsIgnoreCase(email)) {
                if (member.getPassword().equalsIgnoreCase(password)) {
                    if (!loggedInMembers.contains(member)) {
                        loggedInMembers.add(member);
                        loggedInMember = member;
                    }
                    else {
                        throw new MemberAlreadyLoggedInException();
                    }
                }

                break; // User has been found, loop can be ended
            }
        }

        return loggedInMember;
    }

    public boolean isMemberLoggedIn(String id) {
        boolean result = false;

        for (Member member : loggedInMembers) {
            if (member.getSerialNumber().equals(id)) {
                result = true;
            }
        }

        return result;
    }

    public void logoutMember(String id) {
        loggedInMembers.remove(findMemberByID(id));
    }

    public Member updateMemberName(String name, String id) {
        Member member = findMemberByID(id);

        member.setName(name);
        return member;
    }

    public Member updateMemberPhoneNumber(String phoneNumber, String id) {
        Member member = findMemberByID(id);

        member.setPhoneNumber(phoneNumber);
        return member;
    }

    public Member updateMemberEmail(String email, String id) throws EmailAlreadyExistsException {
        Member member = findMemberByID(id);

        if (member.getEmail().equals(email) || isEmailAvailable(email)) {
            member.setEmail(email);
        }

        return member;
    }

    public Member updateMemberPassword(String password, String id) {
        Member member = findMemberByID(id);

        member.setPassword(password);
        return member;
    }

    private void updateMemberPrivateBoat(boolean hasPrivateBoat, String boatId, String memberId) {
        Member member = findMemberByID(memberId);

        if (member.isHasBoat() && !hasPrivateBoat) {
            member.removePrivateBoat();
        } else if (!member.isHasBoat() && hasPrivateBoat) {
            member.addPrivateBoat(boatId);
        } else if (member.isHasBoat() && hasPrivateBoat && !member.getPrivateBoatSerialNumber().equals(boatId)) {
            member.removePrivateBoat();
            member.addPrivateBoat(boatId);
        }
    }

    public void addMemberToList(Member newMember) {
        memberList.add(newMember);
    }

    public void removeMemberByListIndex(int memberIndex) {
        memberList.remove(memberIndex);
    }

    public void removeMember(Member memberToRemove) {
        memberList.remove(memberToRemove);
    }

    public boolean isEmailAvailable(String email) throws EmailAlreadyExistsException {
        for (Member memberInList : this.memberList) {
            if (memberInList.getEmail().equalsIgnoreCase(email)) {
                throw new EmailAlreadyExistsException();
            }
        }

        return true;
    }

    public void addBoatToList(Boat newBoat) { boatList.add(newBoat); }

    public boolean doesBoatNameExist(String name) {
        for (Boat boat : boatList) {
            if (boat.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    public boolean doesActivityExist(String activityName, LocalTime startTime, LocalTime endTime) {
        for (WeeklyActivity weeklyActivity : this.weeklyActivities) {
            if (weeklyActivity.getName().equalsIgnoreCase(activityName) &&
                    weeklyActivity.getStartTime().equals(startTime) &&
                    weeklyActivity.getEndTime().equals(endTime)) {
                return true;
            }
        }

        return false;
    }

    public void removeBoatByListIndex(int boatListIndex) {
        boatList.remove(boatListIndex);
    }

    public void removeBoat(Boat boatToRemove) { boatList.remove(boatToRemove); }

    private List<Assignment> getBoatFutureAssignments(Boat boat) {
        LocalDate today = LocalDate.now();

        return this.assignmentList.stream()
                .filter(assignment -> assignment.getAssignedBoat().equals(boat))
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().isAfter(today))
                .collect(Collectors.toList());
    }

    public boolean doesBoatHaveFutureAssignments(Boat boatCopy) {
        Boat boat = findBoatByID(boatCopy.getSerialNumber());

        return getBoatFutureAssignments(boat).size() != 0;
    }

    public void removeBoatFromFutureAssignments(Boat boat) {
        List<Assignment> assignments = getBoatFutureAssignments(findBoatByID(boat.getSerialNumber()));

        for (Assignment assignment : assignments) {
            assignment.getAssignedReservation().setConfirmed(false);
            assignmentList.remove(assignment);
        }
    }

    public boolean isBoatPrivate(String boatSerialNumber) {
        for (Boat boat : boatList) {
            if (boat.getSerialNumber().equals(boatSerialNumber)) {
                return boat.isPrivate();
            }
        }

        return false;
    }

    public boolean doesBoatBelongToMember(String boatSerialNumber) {
        for (Member member : memberList) {
            if (member.getPrivateBoatSerialNumber() != null) {
                if (member.getPrivateBoatSerialNumber().equals(boatSerialNumber)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean doesBoatSerialNumberExist(String serialNumber) {
        for (Boat boat : boatList) {
            if (boat.getSerialNumber().equalsIgnoreCase(serialNumber)) {
                return true;
            }
        }

        return false;
    }

    public boolean doesMemberSerialNumberExist(String serialNumber) {
        for (Member member : memberList) {
            if (member.getSerialNumber().equalsIgnoreCase(serialNumber)) {
                return true;
            }
        }

        return false;
    }

    public void addWeeklyActivityToList(WeeklyActivity newWeeklyActivity) {
        if (!this.weeklyActivities.contains(newWeeklyActivity)) {
            weeklyActivities.add(newWeeklyActivity);
        }
    }

    public void removeActivityByListIndex(int activityListIndex) {
        weeklyActivities.remove(activityListIndex);
    }

    public void removeActivity(WeeklyActivity activityToRemove) { weeklyActivities.remove(activityToRemove);}

    public void publishNewReservation(Reservation reservation, boolean isReservationInList) {
        if (!isReservationInList) {
            // Add reservation to boathouse's reservation list
            this.reservationList.add(reservation);
        }

        // Add reservation to boat crew's reservation list
        BoatCrew crew = reservation.getBoatCrew();
        List<Member> crewMembers = findMemberListByIDList(crew.getCrewMembers());
        Member coxswain = findMemberByID(crew.getCoxswain());

        if (coxswain != null) {
            coxswain.addReservation(reservation);
        }

        for (Member member : crewMembers) {
            member.addReservation(reservation);
        }
    }

    public void removeReservation(Reservation reservation, boolean override) {
        if (reservation.isConfirmed()) {
            Assignment assignment = findAssignment(reservation);

            if (assignment != null) {
                removeAssignment(assignment, override);
            }
        }

        BoatCrew crew = reservation.getBoatCrew();
        Member coxswain = findMemberByID(crew.getCoxswain());
        List<Member> crewMembers = findMemberListByIDList(crew.getCrewMembers());

        if (coxswain != null) {
            coxswain.removeReservation(reservation);
        }

        for (Member member : crewMembers) {
            member.removeReservation(reservation);
        }

        if (!override) {
            reservationList.remove(reservation);
        }
    }

    public Assignment findAssignment(Reservation reservation) {
        for (Assignment assignment : this.assignmentList) {
            if (assignment.getAssignedReservation().equals(reservation)) {
                return assignment;
            }
        }

        return null;
    }

    private void removeMemberFromReservationBoatCrew(Reservation reservation, Member member) {
        BoatCrew boatCrew = reservation.getBoatCrew();

        if (boatCrew.getCrewMembers().contains(member.getSerialNumber())) {
            removeCrewMemberFromReservation(member, reservation);
        }
        else {
            removeCoxswainFromReservation(reservation);
        }
    }

    private void removeCrewMemberFromReservation(Member member, Reservation reservation) {
        member.removeReservation(reservation);
        reservation.removeCrewMember(member);
    }

    public Reservation removeCoxswainFromReservation(Reservation reservation) {
        Member coxswain = findMemberByID(reservation.removeCoxswain());

        if (coxswain != null) {
            coxswain.removeReservation(reservation);
        }

        return reservation;
    }

    public Reservation addCrewMemberToReservation(Reservation reservation, String memberId) {
        Member member = findMemberByID(memberId);

        reservation.addCrewMember(member);
        member.addReservation(reservation);

        return reservation;
    }

    public Reservation addCoxswainToReservation(Reservation reservation, String coxswainId) {
        Member coxswain = findMemberByID(coxswainId);

        reservation.addCoxswain(coxswain);
        coxswain.addReservation(reservation);

        return reservation;
    }

    public void updateMemberAge(int age, String id) { findMemberByID(id).setAge(age); }

    public void updateMemberDetails(String details, String id) { findMemberByID(id).setDetails(details); }

    public void updateMemberLevel(MemberLevel level, String id) { findMemberByID(id).setLevel(level); }

    public void updateMemberExpirationDate(LocalDateTime expirationDate, String id) {
        findMemberByID(id).setMembershipExpirationDate(expirationDate);
    }

    public void updateBoatName(String newBoatName, Boat boatToEdit) {
        Boat boat = findBoatByID(boatToEdit.getSerialNumber());

        boat.setName(newBoatName);
    }

    public void updateBoatDisabledStatus(boolean isDisabled, Boat boatToEdit) {
        Boat boat = findBoatByID(boatToEdit.getSerialNumber());

        if (!boat.isDisabled() && isDisabled) {
            removeBoatFromFutureAssignments(boat);
        }

        boat.setDisabled(isDisabled);
    }

    public void updateBoatPrivateStatus(boolean isPrivate, Boat boatToEdit) {
        Boat boat = findBoatByID(boatToEdit.getSerialNumber());

        boat.setPrivate(isPrivate);
    }

    public void updateBoatCoastalStatus(boolean isCoastal, Boat boatToEdit) {
        Boat boat = findBoatByID(boatToEdit.getSerialNumber());

        boat.setCoastal(isCoastal);
    }

    public void updateBoatType(BoatType boatType, Boat boatToEdit) {
        Boat boat = findBoatByID(boatToEdit.getSerialNumber());

        if (BoatType.doesBoatNeedCoxswain(boat.getBoatType()) != BoatType.doesBoatNeedCoxswain(boatType)) {
            removeBoatFromFutureAssignments(boat);
        }

        boat.setBoatType(boatType);
    }

    public void updateMemberManagerialStatus(boolean isManger, String id) { findMemberByID(id).setManager(isManger); }

    public void removePrivateBoat(Member member) {
        findMemberByID(member.getSerialNumber()).removePrivateBoat();
    }

    public void addPrivateBoat(Member member, String boatId) {
        findMemberByID(member.getSerialNumber()).addPrivateBoat(boatId);
    }

    public void editReservationActivityDate(Reservation reservation, LocalDate date) {
        reservation.setActivityDate(date);
    }

    public void editReservationActivity(Reservation reservation, WeeklyActivity activityCopy) {
        WeeklyActivity activity = getOriginalActivityReference(activityCopy);
        reservation.setWeeklyActivity(activity);
    }

    public void removeMemberFromFutureReservations(Member memberCopy) {
        Member member = findMemberByID(memberCopy.getSerialNumber());
        List<Reservation> futureReservations = member.getFutureReservationList();
        List<Assignment> assignmentsToRemove = new ArrayList<>();

        for (Assignment assignment : assignmentList) {
            if (isMemberPartOfAssignment(assignment, member)) {
                assignmentsToRemove.add(assignment);
            }
        }

        for (Assignment assignment : assignmentsToRemove) {
            removeAssignment(assignment, false);
        }

        for (Reservation reservation : futureReservations) {
            if (findMemberByID(reservation.getReservator()).equals(member)) {
                removeReservation(reservation, false);
            }
            else {
                removeMemberFromReservationBoatCrew(reservation, member);
            }
        }
    }

    private boolean isMemberPartOfAssignment(Assignment assignment, Member member) {
        return isMemberPartOfReservation(assignment.getAssignedReservation(), member);
    }

    private boolean isMemberPartOfReservation(Reservation reservation, Member member) {
        BoatCrew boatCrew = reservation.getBoatCrew();

        if (boatCrew.getCrewMembers().contains(member.getSerialNumber())) {
            return true;
        }
        else return member.getSerialNumber().equals(boatCrew.getCoxswain());
    }

    public boolean doesMemberHaveFutureReservation(Member member) {
        return findMemberByID(member.getSerialNumber()).getFutureReservationList().size() != 0;
    }

    public void updateReservationCoxswain(Reservation reservation, String coxswain) {
        removeCoxswainFromReservation(reservation);
        if (coxswain != null) {
            addCoxswainToReservation(reservation, coxswain);
        }
    }

    public void updateReservationReservator(Reservation reservation, String reservator) {
        reservation.setReservator(reservator);
    }

    public Reservation updateReservationCrewMembers(Reservation reservation, List<String> newCrewMembers) {
        List<String> currentCrewMembers = reservation.getBoatCrew().getCrewMembers();
        List<String> membersToRemove = new ArrayList<>();
        List<String> membersToAdd = new ArrayList<>();

        for (String currentCrewMember : currentCrewMembers) {
            if (!newCrewMembers.contains(currentCrewMember)) {
                membersToRemove.add(currentCrewMember);
            }
        }

        for (String newCrewMember : newCrewMembers) {
            if (!currentCrewMembers.contains(newCrewMember)) {
                membersToAdd.add(newCrewMember);
            }
        }

        for (String memberIdToRemove : membersToRemove) {
            removeCrewMemberFromReservation(findMemberByID(memberIdToRemove), reservation);
        }

        for (String memberIdToAdd : membersToAdd) {
            addCrewMemberToReservation(reservation, memberIdToAdd);
        }

        if (!currentCrewMembers.contains(reservation.getReservator())) {
            reservation.setReservator(null);
        }

        return reservation;
    }

    public List<Reservation> getNextWeekReservations(ReservationViewFilter viewFilter) {
        List<Reservation> reservations = null;
        LocalDate today = LocalDate.now(), todayPlusEightDays = today.plusDays(8);

        switch (viewFilter) {
            case ALL:
                reservations = this.reservationList.stream()
                        .filter(reservation -> reservation.getActivityDate().isAfter(today) &&
                        reservation.getActivityDate().isBefore(todayPlusEightDays))
                        .collect(Collectors.toList());
                break;
            case UNCONFIRMED_ONLY:
                reservations = this.reservationList.stream()
                        .filter(reservation -> reservation.getActivityDate().isAfter(today) &&
                                reservation.getActivityDate().isBefore(todayPlusEightDays))
                        .filter(reservation -> !reservation.isConfirmed())
                        .collect(Collectors.toList());
                break;
            case CONFIRMED_ONLY:
                reservations = this.reservationList.stream()
                        .filter(reservation -> reservation.getActivityDate().isAfter(today) &&
                                reservation.getActivityDate().isBefore(todayPlusEightDays))
                        .filter(reservation -> reservation.isConfirmed())
                        .collect(Collectors.toList());
                break;
        }

        return reservations;
    }

    public List<Reservation> getSpecificDateReservations(LocalDate date, ReservationViewFilter viewFilter) {
        List<Reservation> reservations = null;

        switch (viewFilter) {
            case ALL:
                reservations = this.reservationList.stream()
                        .filter(reservation -> reservation.getActivityDate().equals(date))
                        .collect(Collectors.toList());
                break;
            case UNCONFIRMED_ONLY:
                reservations = this.reservationList.stream()
                        .filter(reservation -> reservation.getActivityDate().equals(date))
                        .filter(reservation -> !reservation.isConfirmed())
                        .collect(Collectors.toList());
                break;
            case CONFIRMED_ONLY:
                reservations = this.reservationList.stream()
                        .filter(reservation -> reservation.getActivityDate().equals(date))
                        .filter(reservation -> reservation.isConfirmed())
                        .collect(Collectors.toList());
                break;
        }

        return reservations;
    }

    public List<Reservation> getFutureUnconfirmedReservations() {
        return this.reservationList.stream()
                .filter(reservation -> !reservation.isConfirmed())
                .filter(reservation -> reservation.getActivityDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
    }

    public List<Assignment> getTodayAssignments() {
        return getSpecificDateAssignments(LocalDate.now());
    }

    public List<Assignment> getSpecificDateAssignments(LocalDate date) {
        return this.assignmentList.stream()
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().equals(date))
                .collect(Collectors.toList());
    }

    public List<Assignment> getNextWeekAssignments() {
        LocalDate today = LocalDate.now(), todayPlusEightDays = today.plusDays(8);

        return this.assignmentList.stream()
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().isAfter(today))
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().isBefore(todayPlusEightDays))
                .collect(Collectors.toList());
    }

    public List<Assignment> getFutureAssignments() {
        LocalDate today = LocalDate.now();

        return this.assignmentList.stream()
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().isAfter(today))
                .collect(Collectors.toList());
    }

    public List<Assignment> getPastAssignments() {
        LocalDate today = LocalDate.now();

        return this.assignmentList.stream()
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().isBefore(today))
                .collect(Collectors.toList());
    }

    public void removeAssignment(Assignment assignment, boolean override) {
        assignment.getAssignedReservation().setConfirmed(false);

        if (!override) {
            this.assignmentList.remove(assignment);
        }
    }

    public void addAssignment(Assignment assignment) {
        assignment.getAssignedReservation().setConfirmed(true);
        assignmentList.add(assignment);
    }

    public List<Boat> getBoatsForReservation(Reservation reservation) {
        return boatList.stream()
                .filter(boat -> !boat.isDisabled())
                .filter(boat -> !boat.isPrivate())
                .filter(boat -> doesBoatMatchReservation(boat, reservation))
                .collect(Collectors.toList());
    }

    private boolean doesBoatMatchReservation(Boat boat, Reservation reservation) {
        return reservation.getBoatTypes().contains(boat.getBoatType()) &&
                isBoatAvailableForActivity(boat, reservation.getWeeklyActivity(), reservation.getActivityDate());
    }

    public boolean isBoatAvailableForActivity(Boat boat, WeeklyActivity activity, LocalDate activityDate) {
        List<Assignment> assignments = getSpecificDateAssignments(activityDate);

        for (Assignment assignment : assignments) {
            if (assignment.getAssignedBoat().equals(boat) &&
                    areActivitiesOverlapping(activity, assignment.getAssignedReservation().getWeeklyActivity())) {
                return false;
            }
        }

        return true;
    }

    private boolean areActivitiesOverlapping(WeeklyActivity activity, WeeklyActivity otherActivity) {
        LocalTime startTime = activity.getStartTime(),
                endTime = activity.getEndTime(),
                otherStartTime = otherActivity.getStartTime(),
                otherEndTime = otherActivity.getEndTime();

        return startTime.isBefore(otherEndTime) && endTime.isAfter(otherStartTime);
    }

    public Reservation combineReservations(Reservation destinationReservation, Reservation sourceReservation,
                                    boolean assignCoxswain) {
        int crewMembersToAdd = sourceReservation.getBoatCrew().getCrewMembers().size();

        if (assignCoxswain) {
            String coxswain;

            if (sourceReservation.getBoatCrew().getCoxswain() == null) {
                --crewMembersToAdd;
                coxswain = sourceReservation.getBoatCrew().getCrewMembers().get(crewMembersToAdd);
            }
            else {
                coxswain = sourceReservation.getBoatCrew().getCoxswain();
            }

            addCoxswainToReservation(destinationReservation, coxswain);
        }
        else {
            if (sourceReservation.getBoatCrew().getCoxswain() != null) {
                addCrewMemberToReservation(destinationReservation, sourceReservation.getBoatCrew().getCoxswain());
            }
        }

        for (int i = 0; i < crewMembersToAdd; i++) {
            addCrewMemberToReservation(destinationReservation, sourceReservation.getBoatCrew().getCrewMembers().get(i));
        }

        removeReservation(sourceReservation, false);

        return destinationReservation;
    }

    public List<Reservation> getCombinableReservations(Reservation originalReservation, int maxCrewSize) {
        WeeklyActivity activity = originalReservation.getWeeklyActivity();
        LocalDate activityDate = originalReservation.getActivityDate();

        List<Reservation> reservations = this.reservationList.stream()
                .filter(reservation -> reservation.getActivityDate().equals(activityDate))
                .filter(reservation -> reservation.getWeeklyActivity().equals(activity))
                .filter(reservation -> !reservation.isConfirmed())
                .filter(reservation -> reservation.getBoatCrew().size() <= maxCrewSize)
                .collect(Collectors.toList());

        reservations.remove(originalReservation);

        return reservations;
    }

    public List<Boat> getBoatsForActivity(WeeklyActivity activity, LocalDate activityDate) {
        return boatList.stream()
                .filter(boat -> !boat.isDisabled())
                .filter(boat -> !boat.isPrivate())
                .filter(boat -> isBoatAvailableForActivity(boat, activity, activityDate))
                .collect(Collectors.toList());
    }

    public Reservation splitReservation(Reservation originalReservation, BoatCrew newBoatCrew) {
        List<Member> newCrewMembers = findMemberListByIDList(newBoatCrew.getCrewMembers());

        for (Member crewMember : newCrewMembers) {
            originalReservation.removeMember(crewMember);
            crewMember.removeReservation(originalReservation);
        }

        Member newCoxswain = findMemberByID(newBoatCrew.getCoxswain());

        if (newCoxswain != null) {
            originalReservation.removeMember(newCoxswain);
            newCoxswain.removeReservation(originalReservation);
        }

        if (!originalReservation.getBoatCrew().getCrewMembers().contains(originalReservation.getReservator())) {
            originalReservation.setReservator(originalReservation.getBoatCrew().getCrewMembers().get(0));
        }

        Reservation newReservation = new Reservation(
                originalReservation.getReservationCreator(),
                newBoatCrew.getCrewMembers().get(0),
                originalReservation.getWeeklyActivity(),
                originalReservation.getBoatTypes(),
                originalReservation.getActivityDate(),
                newBoatCrew);

        reservationList.add(newReservation);

        for (Member crewMember : newCrewMembers) {
            crewMember.addReservation(newReservation);
        }

        if (newCoxswain != null) {
            newCoxswain.addReservation(newReservation);
        }

        return originalReservation;
    }

    public Reservation updateReservationBoatTypes(Reservation reservation, Set<BoatType> boatTypes) {
        if (!reservation.getBoatTypes().equals(boatTypes)) {
            reservation.setBoatTypes(boatTypes);
        }

        return reservation;
    }

    public boolean isValidJoinAndExpirationDates(XMLGregorianCalendar joined, XMLGregorianCalendar membershipExpiration) {
        return joined.toGregorianCalendar().toZonedDateTime().toLocalDate().isBefore(
                membershipExpiration.toGregorianCalendar().toZonedDateTime().toLocalDate());
    }

    public boolean isValidActivityForReservationBoatCrew(Reservation reservation, WeeklyActivity activity,
                                                         LocalDate activityDate) {
        List<Member> crewMembers = findMemberListByIDList(reservation.getBoatCrew().getCrewMembers());
        Member coxswain = findMemberByID(reservation.getBoatCrew().getCoxswain());
        boolean isValidActivity = true;

        for (Member member : crewMembers) {
            if (!member.isActivityTimeAndDateAvailable(activity, activityDate)) {
                isValidActivity = false;
                break;
            }
        }

        if (isValidActivity && coxswain != null) {
            if (!coxswain.isActivityTimeAndDateAvailable(activity, activityDate)) {
                isValidActivity = false;
            }
        }

        return isValidActivity;
    }

    private void appendToMemberList(List<Member> memberList) {
        this.memberList.addAll(memberList);
    }

    private void overrideMemberList(List<Member> memberList) {
        removeAllAssignments();
        removeAllReservation();
        removeAllMembers();
        this.memberList.addAll(memberList);
    }

    private void removeAllMembers() {
        memberList.removeAll(memberList);
        memberList.addAll(loggedInMembers);
    }

    private void removeAllAssignments() {
        for (Assignment assignment : assignmentList) {
            removeAssignment(assignment, true);
        }

        assignmentList.removeAll(assignmentList);
    }

    private void removeAllReservation() {
        for (Reservation reservation : reservationList) {
            removeReservation(reservation, true);
        }

        reservationList.removeAll(reservationList);
    }

    private void overrideBoatList(List<Boat> boatList) {
        removeAllPrivateBoatReferences();
        removeAllAssignments();
        removeAllReservation();
        removeAllBoats();
        this.boatList.addAll(boatList);
    }

    private void appendToBoatList(List<Boat> boatList) {
        this.boatList.addAll(boatList);
    }

    private void removeAllPrivateBoatReferences() {
        for (Member member : memberList) {
            if (member.isHasBoat()) {
                member.setHasBoat(false);
                member.setPrivateBoatSerialNumber(null);
            }
        }
    }

    private void removeAllBoats() {
        boatList.removeAll(boatList);
    }

    private void overrideActivityList(List<WeeklyActivity> activityList) {
        removeAllAssignments();
        removeAllReservation();
        removeAllActivities();
        weeklyActivities.addAll(activityList);
    }

    private void appendToActivityList(List<WeeklyActivity> activityList) {
        weeklyActivities.addAll(activityList);
    }

    private void removeAllActivities() {
        weeklyActivities.removeAll(weeklyActivities);
    }

    public Member findMemberByID(String id) {
        if (id != null) {
            for (Member member : memberList) {
                if (member.getSerialNumber().equals(id)) {
                    return member;
                }
            }
        }

        return null;
    }

    public Reservation findReservationByID(String id) {
        if (id != null) {
            for (Reservation reservation : reservationList) {
                if (reservation.getId().equals(id)) {
                    return reservation;
                }
            }
        }

        return null;
    }

    public Boat findBoatByID(String id) {
        if (id != null) {
            for (Boat boat : boatList) {
                if (boat.getSerialNumber().equals(id)) {
                    return boat;
                }
            }
        }

        return null;
    }

    public List<Member> findMemberListByIDList(List<String> idList) {
        List<Member> memberList = new ArrayList<>();

        for (String id : idList) {
            Member member = findMemberByID(id);

            if (member != null) {
                memberList.add(member);
            }
        }

        return memberList;
    }

    private void linkMemberReservations() {
        for (Reservation reservation : reservationList) {
            publishNewReservation(reservation, true);
        }
    }

    private void linkReservationActivitiesAndCounter() {
        int maxReservationId = 0, reservationId;

        for (Reservation reservation : reservationList) {
            reservationId = Integer.parseInt(reservation.getId());
            if (reservationId > maxReservationId) {
                maxReservationId = reservationId;
            }

            for (WeeklyActivity weeklyActivity : weeklyActivities) {
                if (reservation.getWeeklyActivity().equals(weeklyActivity)) {
                    reservation.setWeeklyActivity(weeklyActivity);
                    break;
                }
            }
        }

        Reservation.counter = maxReservationId;
    }

    private void linkAssignmentReservations() {
        for (Assignment assignment : assignmentList) {
            for (Reservation reservation : reservationList) {
                if (assignment.getAssignedReservation().equals(reservation)) {
                    assignment.setAssignedReservation(reservation);
                    break;
                }
            }
        }
    }

    private void linkAssignmentBoats() {
        for (Assignment assignment : assignmentList) {
            for (Boat boat : boatList) {
                if (assignment.getAssignedBoat().equals(boat)) {
                    assignment.setAssignedBoat(boat);
                    break;
                }
            }
        }
    }

    public void linkSystemObjectReferences() {
        linkReservationActivitiesAndCounter();
        linkMemberReservations();
        linkAssignmentBoats();
        linkAssignmentReservations();
    }

    private WeeklyActivity getOriginalActivityReference(WeeklyActivity activityCopy) {
        WeeklyActivity originalActivity = null;

        for (WeeklyActivity activity : weeklyActivities) {
            if (activity.equals(activityCopy)) {
                originalActivity = activity;
                break;
            }
        }

        return originalActivity;
    }

    private Assignment getOriginalAssignmentReference(Assignment assignmentCopy) {
        Assignment originalAssignment = null;

        for (Assignment assignment : assignmentList) {
            if (assignment.equals(assignmentCopy)) {
                originalAssignment = assignment;
                break;
            }
        }

        return originalAssignment;
    }

    public String exportMembers() throws XmlException {
        try {
            String membersXmlString = xmlHandler.createMembersXmlString(memberList);

            return membersXmlString;
        } catch (JAXBException | DatatypeConfigurationException e) {
            throw new XmlException("Error: Failed to export members to xml");
        }
    }

    public String exportBoats() throws XmlException {
        try {
            return xmlHandler.createBoatsXmlString(boatList);
        } catch (JAXBException e) {
            throw new XmlException("Error: Failed to export boats to xml");
        }
    }

    public String exportActivities() throws XmlException {
        try {
            return xmlHandler.createActivitiesXmlString(weeklyActivities);
        } catch (JAXBException e) {
            throw new XmlException("Error: Failed to export activities to xml");
        }
    }

    public void importMembers(String membersXmlString, boolean isOverride) throws XmlException {
        StringBuilder importErrorsString = new StringBuilder();
        List<Member> members = xmlHandler.generateMemberListFromXmlString(membersXmlString, isOverride, importErrorsString);

        if (isOverride) {
            overrideMemberList(members);
        }
        else {
            appendToMemberList(members);
        }

        if (!importErrorsString.toString().isEmpty()) {
            throw new XmlException(importErrorsString.toString());
        }
    }

    public void importBoats(String boatsXmlString, boolean isOverride) throws XmlException {
        StringBuilder importErrorsString = new StringBuilder();
        List<Boat> boats = xmlHandler.generateBoatListFromXmlString(boatsXmlString, isOverride, importErrorsString);

        if (isOverride) {
            overrideBoatList(boats);
        }
        else {
            appendToBoatList(boats);
        }

        if (!importErrorsString.toString().isEmpty()) {
            throw new XmlException(importErrorsString.toString());
        }
    }

    public void importActivities(String activitiesXmlString, boolean isOverride) throws XmlException {
        StringBuilder importErrorsString = new StringBuilder();
        List<WeeklyActivity> activities = xmlHandler.generateActivitiesListFromXmlString(
                activitiesXmlString, isOverride, importErrorsString);

        if (isOverride) {
            overrideActivityList(activities);
        }
        else {
            appendToActivityList(activities);
        }

        if (!importErrorsString.toString().isEmpty()) {
            throw new XmlException(importErrorsString.toString());
        }
    }

    public List<WeeklyActivity> getMemberAvailableActivities(String id, int daysFromToday) {
        Member member = findMemberByID(id);
        LocalDate date = LocalDate.now().plusDays(daysFromToday);
        List<WeeklyActivity> availableActivities = new ArrayList<>();

        for (WeeklyActivity weeklyActivity : weeklyActivities) {
            if (member.isActivityTimeAndDateAvailable(weeklyActivity, date)) {
                availableActivities.add(weeklyActivity);
            }
        }

        return availableActivities;
    }

    public List<Member> findAvailableMembersForReservation(WeeklyActivity activity, LocalDate date) {
        return memberList.stream()
                .filter(member -> member.isActivityTimeAndDateAvailable(activity, date))
                .collect(Collectors.toList());
    }

    public WeeklyActivity findActivity(String name, String time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
        String[] activityTimes = time.split("-", 2);
        LocalTime startTime = LocalTime.parse(activityTimes[0], formatter);
        LocalTime endTime = LocalTime.parse(activityTimes[1], formatter);

        for (WeeklyActivity weeklyActivity : weeklyActivities) {
            if (weeklyActivity.getName().equals(name)
                    && weeklyActivity.getStartTime().equals(startTime)
                    && weeklyActivity.getEndTime().equals(endTime)) {
                return weeklyActivity;
            }
        }

        return null;
    }

    public boolean isBoatCrewAvailableForActivity(BoatCrew boatCrew, WeeklyActivity activity, LocalDate date) {
        List<String> crewMembers = boatCrew.getCrewMembers();
        for (String memberId : crewMembers) {
            if (!findMemberByID(memberId).isActivityTimeAndDateAvailable(activity, date)) {
                return false;
            }
        }

        String coxswainId = boatCrew.getCoxswain();
        if (coxswainId != null) {
            return findMemberByID(coxswainId).isActivityTimeAndDateAvailable(activity, date);
        }

        return true;
    }

    public void editReservation(String reservationId, LocalDate date, WeeklyActivity activity, Set<BoatType> boatTypes,
                                BoatCrew boatCrew, String reservatorId) {
        Reservation reservation = findReservationByID(reservationId);
        if (!reservation.getActivityDate().equals(date)) {
            editReservationActivityDate(reservation, date);
        }

        if (!reservation.getWeeklyActivity().equals(activity)) {
            editReservationActivity(reservation, activity);
        }

        if (!reservation.getBoatTypes().equals(boatTypes)) {
            updateReservationBoatTypes(reservation, boatTypes);
        }

        if (!reservation.getBoatCrew().getCrewMembers().equals(boatCrew.getCrewMembers())) {
            updateReservationCrewMembers(reservation, boatCrew.getCrewMembers());
        }

        String coxswain = reservation.getBoatCrew().getCoxswain();
        String newCoxswain = boatCrew.getCoxswain();
        if (coxswain != null) {
            if (!coxswain.equals(newCoxswain)) {
                updateReservationCoxswain(reservation, newCoxswain);
            }
        } else if (newCoxswain != null) {
            updateReservationCoxswain(reservation, newCoxswain);
        }

        String reservator = reservation.getReservator();
        if (reservator == null || !reservation.getReservator().equals(reservatorId)) {
            updateReservationReservator(reservation, reservatorId);
        }
    }

    public void editBoat(String boatId, String name, BoatType boatType, boolean isCoastal, boolean isPrivate, boolean isDisabled) {
        Boat boat = findBoatByID(boatId);

        updateBoatName(name, boat);
        updateBoatType(boatType, boat);
        updateBoatCoastalStatus(isCoastal, boat);
        updateBoatPrivateStatus(isPrivate, boat);
        updateBoatDisabledStatus(isDisabled, boat);
    }

    public void editMember(String memberId, String name, String email, String password, int age, String details,
                           String phoneNumber, MemberLevel level, boolean isManager, boolean hasPrivateBoat,
                           String privateBoatId, LocalDateTime expirationDate) {
        Member member = findMemberByID(memberId);

        updateMemberName(name, memberId);
        try {
            updateMemberEmail(email, memberId);
        } catch (EmailAlreadyExistsException e) { }
        updateMemberPassword(password, memberId);
        updateMemberAge(age, memberId);
        updateMemberDetails(details, memberId);
        updateMemberPhoneNumber(phoneNumber, memberId);
        updateMemberLevel(level, memberId);
        updateMemberManagerialStatus(isManager, memberId);
        updateMemberPrivateBoat(hasPrivateBoat, privateBoatId, memberId);
        updateMemberExpirationDate(expirationDate, memberId);
    }
}