package engine.boat;

import java.io.Serializable;
import java.util.Set;

public enum BoatType implements Serializable {
    SINGLE, DOUBLE_DOUBLE_PADDLE_WITH_COXSWAIN, DOUBLE_DOUBLE_PADDLE, DOUBLE_SINGLE_PADDLE_WITH_COXSWAIN,
    DOUBLE_SINGLE_PADDLE, QUAD_DOUBLE_PADDLE_WITH_COXSWAIN, QUAD_DOUBLE_PADDLE, QUAD_SINGLE_PADDLE_WITH_COXSWAIN,
    QUAD_SINGLE_PADDLE, OCT_DOUBLE_PADDLE_WITH_COXSWAIN, OCT_SINGLE_PADDLE_WITH_COXSWAIN;

    public static BoatType boatCodeToBoatType(String boatCode) {
        switch (boatCode) {
            case "1X":
                return SINGLE;
            case "2X+":
                return DOUBLE_DOUBLE_PADDLE_WITH_COXSWAIN;
            case "2X":
                return DOUBLE_DOUBLE_PADDLE;
            case "2+":
                return DOUBLE_SINGLE_PADDLE_WITH_COXSWAIN;
            case "2-":
                return DOUBLE_SINGLE_PADDLE;
            case "4X+":
                return QUAD_DOUBLE_PADDLE_WITH_COXSWAIN;
            case "4X":
                return QUAD_DOUBLE_PADDLE;
            case "4+":
                return QUAD_SINGLE_PADDLE_WITH_COXSWAIN;
            case "4-":
                return QUAD_SINGLE_PADDLE;
            case "8X+":
                return OCT_DOUBLE_PADDLE_WITH_COXSWAIN;
            case "8+":
                return OCT_SINGLE_PADDLE_WITH_COXSWAIN;
            default:
                return null;
        }
    }
    
    public static String boatTypeToBoatCode(BoatType boatType)  {
        String boatCode = null;

        if (boatType == null) {
            return "None";
        }

        switch (boatType) {
            case SINGLE:
                boatCode = "1X";
                break;
            case DOUBLE_DOUBLE_PADDLE_WITH_COXSWAIN:
                boatCode = "2X+";
                break;
            case DOUBLE_DOUBLE_PADDLE:
                boatCode = "2X";
                break;
            case DOUBLE_SINGLE_PADDLE_WITH_COXSWAIN:
                boatCode = "2+";
                break;
            case DOUBLE_SINGLE_PADDLE:
                boatCode = "2-";
                break;
            case QUAD_DOUBLE_PADDLE_WITH_COXSWAIN:
                boatCode = "4X+";
                break;
            case QUAD_DOUBLE_PADDLE:
                boatCode = "4X";
                break;
            case QUAD_SINGLE_PADDLE_WITH_COXSWAIN:
                boatCode = "4+";
                break;
            case QUAD_SINGLE_PADDLE:
                boatCode = "4-";
                break;
            case OCT_DOUBLE_PADDLE_WITH_COXSWAIN:
                boatCode = "8X+";
                break;
            case OCT_SINGLE_PADDLE_WITH_COXSWAIN:
                boatCode = "8+";
                break;
        }

        return boatCode;
    }

    public static int getMaxCapacity(BoatType boatType) {
        int maxCapacity = 0;

        switch (boatType) {
            case SINGLE:
                maxCapacity = 1;
                break;
            case DOUBLE_DOUBLE_PADDLE_WITH_COXSWAIN:
            case DOUBLE_DOUBLE_PADDLE:
            case DOUBLE_SINGLE_PADDLE_WITH_COXSWAIN:
            case DOUBLE_SINGLE_PADDLE:
                maxCapacity = 2;
                break;
            case QUAD_DOUBLE_PADDLE_WITH_COXSWAIN:
            case QUAD_DOUBLE_PADDLE:
            case QUAD_SINGLE_PADDLE_WITH_COXSWAIN:
            case QUAD_SINGLE_PADDLE:
                maxCapacity = 4;
                break;
            case OCT_DOUBLE_PADDLE_WITH_COXSWAIN:
            case OCT_SINGLE_PADDLE_WITH_COXSWAIN:
                maxCapacity = 8;
                break;
        }

        return maxCapacity;
    }

    public static int getMaxCapacityFromSet(Set<BoatType> boatTypesSet) {
        int maxCapacity = 0;

        for (BoatType boatType : boatTypesSet) {
            maxCapacity = Math.max(maxCapacity, getMaxCapacity(boatType));
        }

        return maxCapacity;
    }

    public static boolean doesBoatNeedCoxswain(BoatType boatType) {
        boolean needsCoxswain = false;

        switch (boatType) {
            case SINGLE:
            case DOUBLE_DOUBLE_PADDLE:
            case DOUBLE_SINGLE_PADDLE:
            case QUAD_DOUBLE_PADDLE:
            case QUAD_SINGLE_PADDLE:
                needsCoxswain = false;
                break;
            case DOUBLE_DOUBLE_PADDLE_WITH_COXSWAIN:
            case DOUBLE_SINGLE_PADDLE_WITH_COXSWAIN:
            case QUAD_DOUBLE_PADDLE_WITH_COXSWAIN:
            case QUAD_SINGLE_PADDLE_WITH_COXSWAIN:
            case OCT_DOUBLE_PADDLE_WITH_COXSWAIN:
            case OCT_SINGLE_PADDLE_WITH_COXSWAIN:
                needsCoxswain = true;
                break;
        }

        return needsCoxswain;
    }

    public static boolean doesBoatNeedCoxswainFromSet(Set<BoatType> boatTypesSet) {
        for (BoatType boatType : boatTypesSet) {
            if (doesBoatNeedCoxswain(boatType)) {
                return true;
            }
        }

        return false;
    }
}
