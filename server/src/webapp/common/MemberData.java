package webapp.common;

import engine.exception.OutOfEnumRangeException;
import engine.member.Member;
import engine.member.MemberLevel;

import java.time.LocalDateTime;


public class MemberData {
    private String id;
    private String name;
    private String age;
    private String details;
    private String level;
    private boolean hasBoat;
    private String privateBoatId;
    private String phoneNumber;
    private String email;
    private String password;
    private boolean isManager;
    private String expirationDate;

    public MemberData(Member member) {
        this.id = member.getSerialNumber();
        this.name = member.getName();
        this.age = String.valueOf(member.getAge());
        this.details = member.getDetails();
        this.level = MemberLevel.memberLevelToString(member.getLevel());
        this.hasBoat = member.isHasBoat();
        this.privateBoatId = member.getPrivateBoatSerialNumber();
        this.phoneNumber = member.getPhoneNumber();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.isManager = member.isManager();
        this.expirationDate = member.getMembershipExpirationDate().toString();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAge() {
        return age;
    }

    public String getDetails() {
        return details;
    }

    public String getLevel() {
        return level;
    }

    public boolean isHasBoat() {
        return hasBoat;
    }

    public String getPrivateBoatId() {
        return privateBoatId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isManager() {
        return isManager;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public Member createMember() {
        MemberLevel memberLevel;
        try {
            memberLevel = MemberLevel.of(Integer.parseInt(level));
        } catch (OutOfEnumRangeException e) {
            memberLevel = MemberLevel.BEGINNER;
        }

        return new Member(id, name, Integer.parseInt(age), details, memberLevel, hasBoat, privateBoatId, phoneNumber, email, password,
                isManager, LocalDateTime.now(), LocalDateTime.now().plusYears(1));
    }
}