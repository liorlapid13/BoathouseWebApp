package engine.xml.handler;

import engine.activity.WeeklyActivity;
import engine.boat.Boat;
import engine.boat.BoatType;
import engine.exception.EmailAlreadyExistsException;
import engine.exception.XmlException;
import engine.jaxb.generated.*;
import engine.member.Member;
import engine.member.MemberLevel;
import engine.Engine;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class XMLHandler {
    private Engine bmsEngine;

    public XMLHandler(Engine bmsEngine) {
        this.bmsEngine = bmsEngine;
    }

    public Member createMemberFromGeneratedMember(engine.jaxb.generated.Member generatedMember) {
        MemberLevel level = generatedMember.getLevel() != null ?
                MemberLevel.valueOf(generatedMember.getLevel().toString()) : MemberLevel.BEGINNER;
        LocalDateTime joined = generatedMember.getJoined() != null ?
                generatedMember.getJoined().toGregorianCalendar().toZonedDateTime().toLocalDateTime() :
                LocalDateTime.now();
        LocalDateTime membershipExpiration = generatedMember.getMembershipExpiration() != null ?
                generatedMember.getMembershipExpiration().toGregorianCalendar().toZonedDateTime().toLocalDateTime() :
                LocalDateTime.now().plusYears(1);
        int age = generatedMember.getAge() != null ? generatedMember.getAge() : 18;
        boolean isManager = generatedMember.isManager() != null ? generatedMember.isManager() : false;
        boolean hasPrivateBoat = generatedMember.isHasPrivateBoat() != null ? generatedMember.isHasPrivateBoat() : false;
        String privateBoatSerialNumber = hasPrivateBoat ? generatedMember.getPrivateBoatId() : null;
        String serialNumber = generatedMember.getId();
        String name = generatedMember.getName();
        String phoneNumber = generatedMember.getPhone() != null ? generatedMember.getPhone() : "0";
        String email = generatedMember.getEmail();
        String password = generatedMember.getPassword();
        String details = generatedMember.getComments();

        return new Member(serialNumber, name, age, details, level, hasPrivateBoat, privateBoatSerialNumber,
                phoneNumber, email, password, isManager, joined, membershipExpiration);
    }

    public Boat createBoatFromGeneratedBoat(engine.jaxb.generated.Boat generatedBoat) {
        String serialNumber = generatedBoat.getId();
        String name = generatedBoat.getName();
        BoatType boatType = translateGeneratedBoatType(generatedBoat.getType());
        boolean isPrivate = generatedBoat.isPrivate() != null ? generatedBoat.isPrivate() : false;
        boolean isWide = generatedBoat.isWide() != null ? generatedBoat.isWide() : false;
        boolean hasCoxswain = generatedBoat.isHasCoxswain() != null ? generatedBoat.isHasCoxswain() : false;
        boolean isCoastal = generatedBoat.isCostal() != null ? generatedBoat.isCostal() : false;
        boolean isDisabled = generatedBoat.isOutOfOrder() != null ? generatedBoat.isOutOfOrder() : false;

        return new Boat(serialNumber, name, boatType, isWide, isPrivate, isCoastal, hasCoxswain, isDisabled);
    }

    public WeeklyActivity createActivityFromGeneratedActivity(Timeframe generatedActivity) {
        String name = generatedActivity.getName();
        LocalTime startTime = LocalTime.parse(generatedActivity.getStartTime());
        LocalTime endTime = LocalTime.parse(generatedActivity.getEndTime());
        BoatType boatType =generatedActivity.getBoatType() != null ?
                translateGeneratedBoatType(generatedActivity.getBoatType()) : null;

        return new WeeklyActivity(name, startTime, endTime, boatType);
    }

    public engine.jaxb.generated.Member createGeneratedMemberFromMember(
            Member member) throws DatatypeConfigurationException {
        engine.jaxb.generated.Member newGeneratedMember = new engine.jaxb.generated.Member();
        initializeGeneratedMember(newGeneratedMember, member);

        return newGeneratedMember;
    }

    public engine.jaxb.generated.Boat createGeneratedBoatFromBoat(Boat boat) {
        engine.jaxb.generated.Boat newGeneratedBoat = new engine.jaxb.generated.Boat();
        initializeGeneratedBoat(newGeneratedBoat, boat);

        return newGeneratedBoat;
    }

    public Timeframe createGeneratedActivityFromActivity(WeeklyActivity activity) {
        Timeframe newGeneratedActivity = new Timeframe();
        initializeGeneratedActivity(newGeneratedActivity, activity);

        return newGeneratedActivity;
    }

    private void initializeGeneratedMember(engine.jaxb.generated.Member newGeneratedMember,
                                           Member member) throws DatatypeConfigurationException {
        DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();

        newGeneratedMember.setId(member.getSerialNumber());
        newGeneratedMember.setName(member.getName());
        newGeneratedMember.setAge(member.getAge());
        newGeneratedMember.setComments(member.getDetails());
        newGeneratedMember.setLevel(RowingLevel.valueOf(member.getLevel().toString()));
        newGeneratedMember.setJoined(dataTypeFactory.newXMLGregorianCalendar(
                member.getRegistrationDate().format(DateTimeFormatter.ISO_DATE_TIME)));
        newGeneratedMember.setMembershipExpiration(dataTypeFactory.newXMLGregorianCalendar(
             member.getMembershipExpirationDate().format(DateTimeFormatter.ISO_DATE_TIME)));
        newGeneratedMember.setHasPrivateBoat(member.isHasBoat());
        newGeneratedMember.setPrivateBoatId(member.getPrivateBoatSerialNumber());
        newGeneratedMember.setPhone(member.getPhoneNumber());
        newGeneratedMember.setEmail(member.getEmail());
        newGeneratedMember.setPassword(member.getPassword());
        newGeneratedMember.setManager(member.isManager());
    }

    private void initializeGeneratedBoat(engine.jaxb.generated.Boat newGeneratedBoat, Boat boat) {
        newGeneratedBoat.setId(boat.getSerialNumber());
        newGeneratedBoat.setName(boat.getName());
        newGeneratedBoat.setType(translateBoatType(boat.getBoatType()));
        newGeneratedBoat.setHasCoxswain(boat.isHasCoxswain());
        newGeneratedBoat.setWide(boat.isWide());
        newGeneratedBoat.setCostal(boat.isCoastal());
        newGeneratedBoat.setPrivate(boat.isPrivate());
        newGeneratedBoat.setOutOfOrder(boat.isDisabled());
    }

    private void initializeGeneratedActivity(Timeframe newGeneratedActivity, WeeklyActivity activity) {
        newGeneratedActivity.setName(activity.getName());
        newGeneratedActivity.setStartTime(activity.getStartTime().toString());
        newGeneratedActivity.setEndTime(activity.getEndTime().toString());
        if (activity.getBoatTypeRestriction() != null) {
            newGeneratedActivity.setBoatType(translateBoatType(activity.getBoatTypeRestriction()));
        }
        else {
            newGeneratedActivity.setBoatType(null);
        }
    }

    public BoatType translateGeneratedBoatType(engine.jaxb.generated.BoatType generatedBoatType) {
        BoatType boatType = null;

        switch (generatedBoatType) {
            case SINGLE:
                boatType = BoatType.SINGLE;
                break;
            case DOUBLE:
                boatType = BoatType.DOUBLE_DOUBLE_PADDLE;
                break;
            case COXED_DOUBLE:
                boatType = BoatType.DOUBLE_DOUBLE_PADDLE_WITH_COXSWAIN;
                break;
            case PAIR:
                boatType = BoatType.DOUBLE_SINGLE_PADDLE;
                break;
            case COXED_PAIR:
                boatType = BoatType.DOUBLE_SINGLE_PADDLE_WITH_COXSWAIN;
                break;
            case FOUR:
                boatType = BoatType.QUAD_SINGLE_PADDLE;
                break;
            case COXED_FOUR:
                boatType = BoatType.QUAD_SINGLE_PADDLE_WITH_COXSWAIN;
                break;
            case QUAD:
                boatType = BoatType.QUAD_DOUBLE_PADDLE;
                break;
            case COXED_QUAD:
                boatType = BoatType.QUAD_DOUBLE_PADDLE_WITH_COXSWAIN;
                break;
            case EIGHT:
                boatType = BoatType.OCT_SINGLE_PADDLE_WITH_COXSWAIN;
                break;
            case OCTUPLE:
                boatType = BoatType.OCT_DOUBLE_PADDLE_WITH_COXSWAIN;
                break;
        }

        return boatType;
    }

    public engine.jaxb.generated.BoatType translateBoatType(BoatType boatType) {
        engine.jaxb.generated.BoatType generatedBoatType = null;

        switch (boatType) {
            case SINGLE:
                generatedBoatType = engine.jaxb.generated.BoatType.SINGLE;
                break;
            case DOUBLE_DOUBLE_PADDLE:
                generatedBoatType = engine.jaxb.generated.BoatType.DOUBLE;
                break;
            case DOUBLE_DOUBLE_PADDLE_WITH_COXSWAIN:
                generatedBoatType = engine.jaxb.generated.BoatType.COXED_DOUBLE;
                break;
            case DOUBLE_SINGLE_PADDLE:
                generatedBoatType = engine.jaxb.generated.BoatType.PAIR;
                break;
            case DOUBLE_SINGLE_PADDLE_WITH_COXSWAIN:
                generatedBoatType = engine.jaxb.generated.BoatType.COXED_PAIR;
                break;
            case QUAD_SINGLE_PADDLE:
                generatedBoatType = engine.jaxb.generated.BoatType.FOUR;
                break;
            case QUAD_SINGLE_PADDLE_WITH_COXSWAIN:
                generatedBoatType = engine.jaxb.generated.BoatType.COXED_FOUR;
                break;
            case QUAD_DOUBLE_PADDLE:
                generatedBoatType = engine.jaxb.generated.BoatType.QUAD;
                break;
            case QUAD_DOUBLE_PADDLE_WITH_COXSWAIN:
                generatedBoatType = engine.jaxb.generated.BoatType.COXED_QUAD;
                break;
            case OCT_SINGLE_PADDLE_WITH_COXSWAIN:
                generatedBoatType = engine.jaxb.generated.BoatType.EIGHT;
                break;
            case OCT_DOUBLE_PADDLE_WITH_COXSWAIN:
                generatedBoatType = engine.jaxb.generated.BoatType.OCTUPLE;
                break;
        }

        return generatedBoatType;
    }

    private boolean checkValidMembers(List<engine.jaxb.generated.Member> generatedMembers) {
        for (int i = 0; i < generatedMembers.size(); i++) {
            for (int j = i + 1; j < generatedMembers.size(); j++) {
                if (generatedMembers.get(i).getId().equalsIgnoreCase(generatedMembers.get(j).getId()) ||
                        generatedMembers.get(i).getEmail().equalsIgnoreCase(generatedMembers.get(j).getEmail())) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkValidBoats(List<engine.jaxb.generated.Boat> generatedBoats) {
        for (int i = 0; i < generatedBoats.size(); i++) {
            for (int j = i + 1; j < generatedBoats.size(); j++) {
                if (generatedBoats.get(i).getId().equalsIgnoreCase(generatedBoats.get(j).getId()) ||
                        generatedBoats.get(i).getName().equalsIgnoreCase(generatedBoats.get(j).getName())) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean checkValidActivities(List<Timeframe> generatedActivities) {
        for (int i = 0; i < generatedActivities.size(); i++) {
            for (int j = i + 1; j < generatedActivities.size(); j++) {
                if (generatedActivities.get(i).getName().equalsIgnoreCase(generatedActivities.get(j).getName()) &&
                        generatedActivities.get(i).getStartTime().equals(generatedActivities.get(j).getStartTime()) &&
                        generatedActivities.get(i).getEndTime().equals(generatedActivities.get(j).getEndTime())) {
                    return false;
                }
            }
        }

        return true;
    }

    private Members loadMembersFromXmlString(String membersXmlString) throws XmlException {
        Members members = null;

        try {
            StringReader stringReader = new StringReader(membersXmlString);
            JAXBContext jc = JAXBContext.newInstance(Members.class);
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(getClass().getClassLoader().getResource("members.xsd"));
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            unmarshaller.setSchema(schema);
            members = (Members)unmarshaller.unmarshal(stringReader);
        } catch (JAXBException e) {
            throw new XmlException("Error: There are either members without required fields (ID, name, email, password) " +
                    "or input does not match the field types");
        } catch (SAXException e) {
            throw new XmlException("Error parsing resources");
        }

        return members;
    }

    private Boats loadBoatsFromXmlString(String boatsXmlString) throws XmlException {
        Boats boats = null;

        try {
            StringReader stringReader = new StringReader(boatsXmlString);
            JAXBContext jc = JAXBContext.newInstance(Boats.class);
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(getClass().getClassLoader().getResource("boats.xsd"));
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            unmarshaller.setSchema(schema);
            boats = (Boats)unmarshaller.unmarshal(stringReader);
        } catch (JAXBException e) {
            throw new XmlException("Error: Either the boats are without required fields (ID, name, type) " +
                    "or the input does not match the field types");
        }
        catch (SAXException e) {
            throw new XmlException("Error parsing resources");
        }

        return boats;
    }

    private Activities loadActivitiesFromXmlString(String activitiesXmlString) throws XmlException {
        Activities activities = null;

        try {
            StringReader stringReader = new StringReader(activitiesXmlString);
            JAXBContext jc = JAXBContext.newInstance(Activities.class);
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(getClass().getClassLoader().getResource("activities.xsd"));
            Unmarshaller unmarshaller = jc.createUnmarshaller();

            unmarshaller.setSchema(schema);
            activities = (Activities)unmarshaller.unmarshal(stringReader);
        } catch (JAXBException e) {
            throw new XmlException("Error: Either the activities are without required fields (name, startTime, endTime) " +
                    "or the input does not match the field types");
        }
        catch (SAXException e) {
            throw new XmlException("Error parsing resources");
        }

        return activities;
    }

    private boolean checkGeneratedMember(engine.jaxb.generated.Member member, boolean override) throws XmlException {
        if (member.getId().isEmpty()) {
            throw new XmlException("Error: member ID is missing");
        }

        if (member.getName().isEmpty()) {
            throw new XmlException("Error: member name is missing");
        }

        if (member.getPassword().isEmpty()) {
            throw new XmlException("Error: member password is missing");
        }

        String emailRegex = "^[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        if (!member.getEmail().matches(emailRegex)) {
            throw new XmlException("Error: invalid email");
        }

        String phoneNumberRegex = "^\\d+(-\\d+)*$";

        if (member.getPhone() != null) {
            if (!member.getPhone().matches(phoneNumberRegex)) {
                throw new XmlException("Error: invalid phone number");
            }
        }

        if (!override) {
            try {
                bmsEngine.isEmailAvailable(member.getEmail());
            }
            catch (EmailAlreadyExistsException e) {
                throw new XmlException(e.getMessage());
            }

            if (bmsEngine.doesMemberSerialNumberExist(member.getId())) {
                throw new XmlException("Error: member ID already exists");
            }
        }

        if (member.getJoined() != null && member.getMembershipExpiration() != null) {
            if (!bmsEngine.isValidJoinAndExpirationDates(member.getJoined(), member.getMembershipExpiration())) {
                throw new XmlException("Error: join date must be before membership expiration date");
            }
        }

        if ((member.isHasPrivateBoat() != null)) {
            if (member.isHasPrivateBoat()) {
                if (member.getPrivateBoatId() != null && !member.getPrivateBoatId().isEmpty()) {
                    if (!bmsEngine.doesBoatSerialNumberExist(member.getPrivateBoatId())){
                        throw new XmlException("Error: boat serial number doesn't exist");
                    }
                }
                else {
                    throw new XmlException("Error: private boat ID missing");
                }
            }
        }

        return true;
    }

    private boolean checkGeneratedBoat(engine.jaxb.generated.Boat boat, boolean override) throws XmlException {
        if (boat.getName().isEmpty()) {
            throw new XmlException("Error: boat name is missing");
        }

        if (boat.getId().isEmpty()) {
            throw new XmlException("Error: boat ID is missing");
        }

        if (!override) {
            if (bmsEngine.doesBoatSerialNumberExist(boat.getId())){
                throw new XmlException("Error: boat ID already exists");
            }

            if (bmsEngine.doesBoatNameExist(boat.getName())){
                throw new XmlException("Error: boat name already exists");
            }
        }

        if (boat.isHasCoxswain() != null) {
            BoatType boatType = translateGeneratedBoatType(boat.getType());

            if (BoatType.doesBoatNeedCoxswain(boatType)) {
                if (!boat.isHasCoxswain()) {
                    throw new XmlException("Error: boat type requires coxswain but boat coxswain field is 'false'");
                }
            }
            else {
                if (boat.isHasCoxswain()) {
                    throw new XmlException("Error: boat type does not require a coxswain but boat coxswain field is 'true'");
                }
            }
        }

        return true;
    }

    private boolean checkGeneratedActivity(engine.jaxb.generated.Timeframe activity, boolean override) throws XmlException {
        LocalTime startTime, endTime;

        try {
            startTime = LocalTime.parse(activity.getStartTime());
        } catch (DateTimeParseException e) {
            throw new XmlException("Error: invalid start time");
        }

        try {
            endTime = LocalTime.parse(activity.getEndTime());
        } catch (DateTimeParseException e) {
            throw new XmlException("Error: invalid end time");
        }

        if (!WeeklyActivity.isValidActivityTime(startTime, endTime)) {
            throw new XmlException("Error: start time is after end time");
        }

        if (!override) {
            if (bmsEngine.doesActivityExist(activity.getName(), startTime, endTime)) {
                throw new XmlException("Error: activity already exists");
            }
        }

        return true;
    }

    public List<Member> createMemberListFromGeneratedMembers(List<engine.jaxb.generated.Member> generatedMembers,
                                                             boolean override, StringBuilder importErrorsString)
            throws XmlException {
        List<Member> memberList = new ArrayList<>();
        boolean isValidGeneratedMember = false;
        boolean isValidGeneratedMembers = checkValidMembers(generatedMembers);
        int membersAddedCounter = 0;

        if (isValidGeneratedMembers) {
            engine.member.Member newMember;
            for (int i = 0; i < generatedMembers.size() ; i++) {
                try {
                    isValidGeneratedMember = checkGeneratedMember(generatedMembers.get(i), override);
                } catch (XmlException e) {
                    importErrorsString.append(e.getMessage() + "\nMember number: " + (i+1) + " is not added\n");
                }

                if (isValidGeneratedMember) {
                    newMember = createMemberFromGeneratedMember(generatedMembers.get(i));
                    memberList.add(newMember);
                    membersAddedCounter++;
                    isValidGeneratedMember = false;
                }
            }

            if (!importErrorsString.toString().isEmpty()) {
                importErrorsString.append(
                        membersAddedCounter + " from " + generatedMembers.size() + " members successfully imported\n");
            }
        }
        else {
            throw new XmlException(
                    "The file is invalid, there are 2 members with the same ID/Email, import members from XML cancelled");
        }

        return memberList;
    }

    public List<Boat> createBoatListFromGeneratedBoats(List<engine.jaxb.generated.Boat> generatedBoats, boolean override,
                                                       StringBuilder importErrorsString) throws XmlException {
        List<Boat> boatList = new ArrayList<>();
        boolean isValidGeneratedBoat = false;
        boolean isValidGeneratedBoats = checkValidBoats(generatedBoats);
        int boatsAddedCounter = 0;

        if (isValidGeneratedBoats) {
            for (int i = 0; i < generatedBoats.size() ; i++) {
                Boat newBoat;

                try {
                    isValidGeneratedBoat = checkGeneratedBoat(generatedBoats.get(i), override);
                }
                catch (XmlException e) {
                    importErrorsString.append(e.getMessage() + "\nBoat number: " + (i+1) + " is not added\n");
                }

                if (isValidGeneratedBoat) {
                    newBoat = createBoatFromGeneratedBoat(generatedBoats.get(i));
                    boatList.add(newBoat);
                    boatsAddedCounter++;
                    isValidGeneratedBoat = false;
                }
            }

            if (!importErrorsString.toString().isEmpty()) {
                importErrorsString.append(
                        boatsAddedCounter + " from " + generatedBoats.size() + " boats successfully imported\n");
            }
        }
        else {
            throw new XmlException("The file is invalid, there are 2 boats with the same ID, import boats from XML canceled");
        }

        return boatList;
    }

    public List<WeeklyActivity> createActivityListFromGeneratedActivities(List<Timeframe> generatedActivities,
                                                                          boolean override, StringBuilder importErrorsString)
            throws XmlException {
        List<WeeklyActivity> activityList = new ArrayList<>();
        boolean isValidGeneratedActivity = false;
        boolean isValidGeneratedActivities = checkValidActivities(generatedActivities);
        int activitiesAddedCounter = 0;

        if (isValidGeneratedActivities) {
            for (int i = 0; i < generatedActivities.size() ; i++) {
                WeeklyActivity newActivity;

                try {
                    isValidGeneratedActivity = checkGeneratedActivity(generatedActivities.get(i), override);
                }
                catch (XmlException e) {
                    importErrorsString.append(e.getMessage() + "\nActivity number: " + (i+1) + " is not added\n");
                }

                if (isValidGeneratedActivity) {
                    newActivity = createActivityFromGeneratedActivity(generatedActivities.get(i));
                    activityList.add(newActivity);
                    activitiesAddedCounter++;
                    isValidGeneratedActivity = false;
                }
            }

            if (!importErrorsString.toString().isEmpty()) {
                importErrorsString.append(
                        activitiesAddedCounter + " from " + generatedActivities.size() + " activities successfully imported\n");
            }
        }
        else {
            throw new XmlException("The file is invalid, there are 2 activities with the same timeframe, " +
                    "import activities from XML canceled");
        }

        return activityList;
    }

    public void createGeneratedMembersFromMemberList(List<engine.jaxb.generated.Member> generatedMembers,
                                                     List<Member> memberList) throws DatatypeConfigurationException{
        for (engine.member.Member member : memberList) {
            engine.jaxb.generated.Member newGeneratedMember = createGeneratedMemberFromMember(member);
            generatedMembers.add(newGeneratedMember);
        }
    }

    public void createGeneratedBoatsFromBoatList(List<engine.jaxb.generated.Boat> generatedBoats,
                                                 List<Boat> boatList) {
        for (engine.boat.Boat boat : boatList) {
            engine.jaxb.generated.Boat newGeneratedBoat = createGeneratedBoatFromBoat(boat);
            generatedBoats.add(newGeneratedBoat);
        }
    }

    public void createGeneratedActivitiesFromActivityList(List<Timeframe> generatedActivities,
                                                          List<WeeklyActivity> activityList) {
        for (WeeklyActivity activity : activityList) {
            engine.jaxb.generated.Timeframe newGeneratedActivity = createGeneratedActivityFromActivity(activity);
            generatedActivities.add(newGeneratedActivity);
        }
    }

    public String createMembersXmlString(List<Member> memberList)
            throws DatatypeConfigurationException, JAXBException {
        Members generatedMembers = new Members();

        createGeneratedMembersFromMemberList(generatedMembers.getMember(), memberList);
        JAXBContext jaxbContext = JAXBContext.newInstance(Members.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter stringWriter = new StringWriter();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(generatedMembers, stringWriter);

        return stringWriter.toString();
    }

    public String createBoatsXmlString(List<Boat> boatList) throws JAXBException {
        Boats generatedBoats = new Boats();

        createGeneratedBoatsFromBoatList(generatedBoats.getBoat(), boatList);
        JAXBContext jaxbContext = JAXBContext.newInstance(Boats.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter stringWriter = new StringWriter();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(generatedBoats, stringWriter);

        return stringWriter.toString();
    }

    public String createActivitiesXmlString(List<WeeklyActivity> activityList) throws JAXBException {
        Activities generatedActivities = new Activities();

        createGeneratedActivitiesFromActivityList(generatedActivities.getTimeframe(), activityList);
        JAXBContext jaxbContext = JAXBContext.newInstance(Activities.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        StringWriter stringWriter = new StringWriter();

        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(generatedActivities, stringWriter);

        return stringWriter.toString();
    }

    public List<Member> generateMemberListFromXmlString(String membersXmlString, boolean isOverride,
                                                        StringBuilder importErrorsString) throws XmlException {
        Members members = loadMembersFromXmlString(membersXmlString);

        return createMemberListFromGeneratedMembers(members.getMember(), isOverride, importErrorsString);
    }


    public List<Boat> generateBoatListFromXmlString(String boatsXmlString, boolean isOverride,
                                                    StringBuilder importErrorsString) throws XmlException {
        Boats boats = loadBoatsFromXmlString(boatsXmlString);

        return createBoatListFromGeneratedBoats(boats.getBoat(), isOverride, importErrorsString);
    }

    public List<WeeklyActivity> generateActivitiesListFromXmlString(String activitiesXmlString, boolean isOverride,
                                                                    StringBuilder importErrorsString)
            throws XmlException {
        Activities activities = loadActivitiesFromXmlString(activitiesXmlString);

        return createActivityListFromGeneratedActivities(activities.getTimeframe(), isOverride, importErrorsString);
    }
}
