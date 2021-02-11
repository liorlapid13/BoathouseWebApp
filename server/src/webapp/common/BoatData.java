package webapp.common;

import engine.boat.Boat;
import engine.boat.BoatType;

public class BoatData {

    private String id;
    private String name;
    private String boatType;
    private boolean isWide;
    private boolean isPrivate;
    private boolean isCoastal;
    private boolean isDisabled;

    public BoatData(Boat boat){
        this.id = boat.getSerialNumber();
        this.name = boat.getName();
        this.boatType = BoatType.boatTypeToBoatCode(boat.getBoatType());
        this.isWide = boat.isWide();
        this.isPrivate = boat.isPrivate();
        this.isCoastal = boat.isCoastal();
        this.isDisabled = boat.isDisabled();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBoatType() {
        return boatType;
    }

    public boolean isWide() {
        return isWide;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isCoastal() {
        return isCoastal;
    }

    public boolean isDisabled() {
        return isDisabled;
    }
}
