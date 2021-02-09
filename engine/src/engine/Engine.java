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
public class Engine implements BMSEngine {
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

    @Override
    public List<Member> getMemberList() {
        return memberList;
    }

    @Override
    public List<WeeklyActivity> getWeeklyActivities() { return weeklyActivities; }

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    @Override
    public List<Boat> getBoatList() { return boatList;}

    public List<Reservation> getReservationList() {
        return reservationList;
    }

    public void createXmlHandler() {
        xmlHandler = new XMLHandler(this);
    }

    @Override
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

    @Override
    public boolean isMemberLoggedIn(String id) {
        boolean result = false;

        for (Member member : loggedInMembers) {
            if (member.getSerialNumber().equals(id)) {
                result = true;
            }
        }

        return result;
    }

    @Override
    public void logoutMember(String id) {
        loggedInMembers.remove(findMemberByID(id));
    }

    @Override
    public Member updateMemberName(String name, String id) {
        Member member = findMemberByID(id);

        member.setName(name);
        return member;
    }

    @Override
    public Member updateMemberPhoneNumber(String phoneNumber, String id) {
        Member member = findMemberByID(id);

        member.setPhoneNumber(phoneNumber);
        return member;
    }

    @Override
    public Member updateMemberEmail(String email, String id) throws EmailAlreadyExistsException {
        Member member = findMemberByID(id);

        if (member.getEmail().equals(email) || isEmailAvailable(email)) {
            member.setEmail(email);
        }

        return member;
    }

    @Override
    public Member updateMemberPassword(String password, String id) {
        Member member = findMemberByID(id);

        member.setPassword(password);
        return member;
    }

    @Override
    public void addMemberToList(Member newMember) {
        memberList.add(newMember);
    }

    @Override
    public void removeMemberByListIndex(int memberIndex) {
        memberList.remove(memberIndex);
    }

    @Override
    public boolean isEmailAvailable(String email) throws EmailAlreadyExistsException {
        for (Member memberInList : this.memberList) {
            if (memberInList.getEmail().equalsIgnoreCase(email)) {
                throw new EmailAlreadyExistsException();
            }
        }

        return true;
    }

    @Override
    public void addBoatToList(Boat newBoat) { boatList.add(newBoat); }

    @Override
    public boolean doesBoatNameExist(String name) {
        for (Boat boat : boatList) {
            if (boat.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }

    @Override
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

    @Override
    public void removeBoatByListIndex(int boatListIndex) {
        boatList.remove(boatListIndex);
    }

    private List<Assignment> getBoatFutureAssignments(Boat boat) {
        LocalDate today = LocalDate.now();

        return this.assignmentList.stream()
                .filter(assignment -> assignment.getAssignedBoat().equals(boat))
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().isAfter(today))
                .collect(Collectors.toList());
    }

    @Override
    public boolean doesBoatHaveFutureAssignments(Boat boatCopy) {
        Boat boat = findBoatByID(boatCopy.getSerialNumber());

        return getBoatFutureAssignments(boat).size() != 0;
    }

    @Override
    public void removeBoatFromFutureAssignments(Boat boat) {
        List<Assignment> assignments = getBoatFutureAssignments(findBoatByID(boat.getSerialNumber()));

        for (Assignment assignment : assignments) {
            assignment.getAssignedReservation().setConfirmed(false);
            assignmentList.remove(assignment);
        }
    }

    @Override
    public boolean isBoatPrivate(String boatSerialNumber) {
        for (Boat boat : boatList) {
            if (boat.getSerialNumber().equals(boatSerialNumber)) {
                return boat.isPrivate();
            }
        }

        return false;
    }

    @Override
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

    @Override
    public boolean doesBoatSerialNumberExist(String serialNumber) {
        for (Boat boat : boatList) {
            if (boat.getSerialNumber().equalsIgnoreCase(serialNumber)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean doesMemberSerialNumberExist(String serialNumber) {
        for (Member member : memberList) {
            if (member.getSerialNumber().equalsIgnoreCase(serialNumber)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addWeeklyActivityToList(WeeklyActivity newWeeklyActivity) {
        if (!this.weeklyActivities.contains(newWeeklyActivity)) {
            weeklyActivities.add(newWeeklyActivity);
        }
    }

    @Override
    public void removeActivityByListIndex(int activityListIndex) {
        weeklyActivities.remove(activityListIndex);
    }

    public void removeActivity(WeeklyActivity activityToRemove) { weeklyActivities.remove(activityToRemove);}

    @Override
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

    @Override
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

    private Assignment findAssignment(Reservation reservation) {
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

    @Override
    public Reservation removeCoxswainFromReservation(Reservation reservation) {
        Member coxswain = findMemberByID(reservation.removeCoxswain());

        if (coxswain != null) {
            coxswain.removeReservation(reservation);
        }

        return reservation;
    }

    @Override
    public Reservation addCrewMemberToReservation(Reservation reservation, String memberId) {
        Member member = findMemberByID(memberId);

        reservation.addCrewMember(member);
        member.addReservation(reservation);

        return reservation;
    }

    @Override
    public Reservation addCoxswainToReservation(Reservation reservation, String coxswainId) {
        Member coxswain = findMemberByID(coxswainId);

        reservation.addCoxswain(coxswain);
        coxswain.addReservation(reservation);

        return reservation;
    }

    @Override
    public void updateMemberAge(int age, String id) { findMemberByID(id).setAge(age); }

    @Override
    public void updateMemberDetails(String details, String id) { findMemberByID(id).setDetails(details); }

    @Override
    public void updateMemberLevel(MemberLevel level, String id) { findMemberByID(id).setLevel(level); }

    @Override
    public void updateMemberExpirationDate(LocalDateTime expirationDate, String id) {
        findMemberByID(id).setMembershipExpirationDate(expirationDate);
    }

    @Override
    public void updateBoatName(String newBoatName, Boat boatToEdit) {
        Boat boat = findBoatByID(boatToEdit.getSerialNumber());

        boat.setName(newBoatName);
    }

    @Override
    public void updateBoatStatus(boolean isDisabled, Boat boatToEdit) {
        Boat boat = findBoatByID(boatToEdit.getSerialNumber());

        boat.setDisabled(isDisabled);
    }

    @Override
    public void updateBoatOwnershipStatus(boolean isPrivate, Boat boatToEdit) {
        Boat boat = findBoatByID(boatToEdit.getSerialNumber());

        boat.setPrivate(isPrivate);
    }

    @Override
    public void updateBoatCoastalStatus(boolean isCoastal, Boat boatToEdit) {
        Boat boat = findBoatByID(boatToEdit.getSerialNumber());

        boat.setCoastal(isCoastal);
    }

    @Override
    public void updateBoatType(BoatType boatType, Boat boatToEdit) {
        Boat boat = findBoatByID(boatToEdit.getSerialNumber());

        boat.setBoatType(boatType);
    }

    @Override
    public void updateMemberManagerialStatus(boolean isManger, String id) { findMemberByID(id).setManager(isManger); }

    @Override
    public void removePrivateBoat(Member member) {
        findMemberByID(member.getSerialNumber()).removePrivateBoat();
    }

    @Override
    public void addPrivateBoat(Member member, String boatId) {
        findMemberByID(member.getSerialNumber()).addPrivateBoat(boatId);
    }

    @Override
    public void editReservationActivityDate(Reservation reservation, LocalDate date) {
        reservation.setActivityDate(date);
    }

    @Override
    public void editReservationActivity(Reservation reservation, WeeklyActivity activityCopy) {
        WeeklyActivity activity = getOriginalActivityReference(activityCopy);
        reservation.setWeeklyActivity(activity);
    }

    @Override
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

    @Override
    public boolean doesMemberHaveFutureReservation(Member member) {
        return findMemberByID(member.getSerialNumber()).getFutureReservationList().size() != 0;
    }

    @Override
    public void updateReservationCoxswain(Reservation reservation, String coxswain) {
        removeCoxswainFromReservation(reservation);
        addCoxswainToReservation(reservation, coxswain);
    }

    @Override
    public void updateReservationReservator(Reservation reservation, String reservator) {
        reservation.setReservator(reservator);
    }

    @Override
    public Reservation updateReservationCrewMembers(Reservation reservation, List<String> updatedCrewMembers) {
        List<String> currentCrewMembers = reservation.getBoatCrew().getCrewMembers();
        List<String> membersToRemove = new ArrayList<>();

        for (String memberId : currentCrewMembers) {
            boolean memberInUpdatedCrew = false;

            for (String updatedCrewMemberId : updatedCrewMembers) {
                if (updatedCrewMemberId.equals(memberId)) {
                    memberInUpdatedCrew = true;
                    break;
                }
            }

            if (!memberInUpdatedCrew) {
                membersToRemove.add(memberId);
            }
        }

        for (String memberIdToRemove : membersToRemove) {
            removeCrewMemberFromReservation(findMemberByID(memberIdToRemove), reservation);
        }

        return reservation;
    }

    @Override
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

    @Override
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

    @Override
    public List<Reservation> getFutureUnconfirmedReservations() {
        return this.reservationList.stream()
                .filter(reservation -> !reservation.isConfirmed())
                .filter(reservation -> reservation.getActivityDate().isAfter(LocalDate.now()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Assignment> getTodayAssignments() {
        return getSpecificDateAssignments(LocalDate.now());
    }

    @Override
    public List<Assignment> getSpecificDateAssignments(LocalDate date) {
        return this.assignmentList.stream()
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public List<Assignment> getNextWeekAssignments() {
        LocalDate today = LocalDate.now(), todayPlusEightDays = today.plusDays(8);

        return this.assignmentList.stream()
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().isAfter(today))
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().isBefore(todayPlusEightDays))
                .collect(Collectors.toList());
    }

    @Override
    public List<Assignment> getFutureAssignments() {
        LocalDate today = LocalDate.now();

        return this.assignmentList.stream()
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().isAfter(today))
                .collect(Collectors.toList());
    }

    @Override
    public List<Assignment> getPastAssignments() {
        LocalDate today = LocalDate.now();

        return this.assignmentList.stream()
                .filter(assignment -> assignment.getAssignedReservation().getActivityDate().isBefore(today))
                .collect(Collectors.toList());
    }

    @Override
    public void removeAssignment(Assignment assignmentCopy, boolean override) {
        Assignment assignment = getOriginalAssignmentReference(assignmentCopy);

        assignment.getAssignedReservation().setConfirmed(false);

        if (!override) {
            this.assignmentList.remove(assignment);
        }
    }

    @Override
    public void addAssignment(Assignment assignment) {
        Boat originalBoatReference = findBoatByID(assignment.getAssignedBoat().getSerialNumber());
        Reservation assignedReservation = assignment.getAssignedReservation();

        assignment.setAssignedBoat(originalBoatReference);
        assignment.setAssignedReservation(assignedReservation);
        assignedReservation.setConfirmed(true);
        assignmentList.add(assignment);
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public List<Boat> getBoatsForActivity(WeeklyActivity activity, LocalDate activityDate) {
        return boatList.stream()
                .filter(boat -> !boat.isDisabled())
                .filter(boat -> !boat.isPrivate())
                .filter(boat -> isBoatAvailableForActivity(boat, activity, activityDate))
                .collect(Collectors.toList());
    }

    @Override
    public void splitReservation(Reservation originalReservation, Reservation newReservation, BoatCrew newBoatCrew) {
        List<Member> newCrewMembers = findMemberListByIDList(newBoatCrew.getCrewMembers());

        for (Member crewMember : newCrewMembers) {
            crewMember.removeReservation(originalReservation);
            crewMember.addReservation(newReservation);
        }

        Member newCoxswain = findMemberByID(newBoatCrew.getCoxswain());

        if (newCoxswain != null) {
            newCoxswain.removeReservation(originalReservation);
            newCoxswain.addReservation(newReservation);
        }

        originalReservation.setBoatCrew(newReservation.getBoatCrew());
        newReservation.setBoatCrew(newBoatCrew);
        newReservation.setReservator(newBoatCrew.getCrewMembers().get(0));
        if (!originalReservation.getBoatCrew().getCrewMembers().contains(originalReservation.getReservator())) {
            originalReservation.setReservator(originalReservation.getBoatCrew().getCrewMembers().get(0));
        }

        reservationList.add(newReservation);
    }

    @Override
    public void updateReservationBoatTypes(Reservation reservation, Set<BoatType> boatTypes) {
        reservation.setBoatTypes(boatTypes);
    }

    public boolean isValidJoinAndExpirationDates(XMLGregorianCalendar joined, XMLGregorianCalendar membershipExpiration) {
        return joined.toGregorianCalendar().toZonedDateTime().toLocalDate().isBefore(
                membershipExpiration.toGregorianCalendar().toZonedDateTime().toLocalDate());
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public String exportMembers() throws XmlException {
        try {
            /*decryptPasswords();*/
            String membersXmlString = xmlHandler.createMembersXmlString(memberList);
            /*encryptPasswords();*/

            return membersXmlString;
        } catch (JAXBException | DatatypeConfigurationException e) {
            throw new XmlException("Error: Failed to export members to xml");
        }
    }

    @Override
    public String exportBoats() throws XmlException {
        try {
            return xmlHandler.createBoatsXmlString(boatList);
        } catch (JAXBException e) {
            throw new XmlException("Error: Failed to export boats to xml");
        }
    }

    @Override
    public String exportActivities() throws XmlException {
        try {
            return xmlHandler.createActivitiesXmlString(weeklyActivities);
        } catch (JAXBException e) {
            throw new XmlException("Error: Failed to export activities to xml");
        }
    }

    @Override
    public void importMembers(String membersXmlString, boolean isOverride) throws XmlException {
        StringBuilder importErrorsString = new StringBuilder();
        List<Member> members = xmlHandler.generateMemberListFromXmlString(membersXmlString, isOverride, importErrorsString);

        /*try {
            for (Member member : members) {
                member.setPassword(cryptor.encrypt(member.getPassword()));
            }
        } catch (CryptorException e) {
            throw new XmlException("Error: Failed to encrypt member passwords");
        }
*/
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

    @Override
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

    @Override
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

        if (!reservation.getBoatCrew().getCoxswain().equals(boatCrew.getCoxswain())) {
            updateReservationCoxswain(reservation, boatCrew.getCoxswain());
        }

        if (!reservation.getReservator().equals(reservatorId)) {
            updateReservationReservator(reservation, reservatorId);
        }
    }

    public void removeMember(Member memberToRemove) {
        memberList.remove(memberToRemove);
    }

   /* public void encryptPasswords() throws CryptorException {
        Cryptor cryptor = new Cryptor();

        for (Member member : memberList) {
            member.setPassword(cryptor.encrypt(member.getPassword()));
        }
    }

    public void decryptPasswords() throws CryptorException {
        Cryptor cryptor = new Cryptor();

        for (Member member : memberList) {
            member.setPassword(cryptor.decrypt(member.getPassword()));
        }
    }*/
}