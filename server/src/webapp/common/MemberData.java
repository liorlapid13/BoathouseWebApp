package webapp.common;

import engine.member.Member;

public class MemberData {
    private String id;
    private String name;
    private String email;
    // TODO: add all member fields (for create member)

    public MemberData(Member member) {
        String id = member.getSerialNumber();
        String name = member.getName();
        String email = member.getEmail();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
