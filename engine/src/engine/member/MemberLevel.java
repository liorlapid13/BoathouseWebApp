package engine.member;

import engine.exception.OutOfEnumRangeException;

import java.io.Serializable;

public enum MemberLevel implements Serializable {
    BEGINNER, INTERMEDIATE, ADVANCED;

    public static MemberLevel of(int value) throws OutOfEnumRangeException {
        switch (value) {
            case 1:
                return BEGINNER;
            case 2:
                return INTERMEDIATE;
            case 3:
                return ADVANCED;
            default:
                throw new OutOfEnumRangeException(1, 3, "MemberLevel");
        }
    }

    public static String memberLevelToString(MemberLevel level) {
        switch (level) {
            case BEGINNER:
                return "Beginner";
            case INTERMEDIATE:
                return "Intermediate";
            case ADVANCED:
                return "Advanced";
            default:
                return "";
        }
    }
}


