package engine.boat;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Objects;

@XmlRootElement
public class Boat implements Serializable {
    private String serialNumber;
    private String name;
    private BoatType boatType;
    private boolean isWide;
    private boolean isPrivate;
    private boolean isCoastal;
    private boolean hasCoxswain;
    private boolean isDisabled;

    public Boat() { }

    public Boat(String serialNumber, String name, BoatType boatType, boolean isWide, boolean isPrivate,
                boolean isCoastal, boolean hasCoxswain, boolean isDisabled) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.boatType = boatType;
        this.isWide = isWide;
        this.isPrivate = isPrivate;
        this.hasCoxswain = hasCoxswain;
        this.isCoastal = isCoastal;
        this.isDisabled = isDisabled;
    }

    @XmlAttribute
    public void setWide(boolean wide) {
        isWide = wide;
    }

    @XmlAttribute
    public void setHasCoxswain(boolean hasCoxswain) {
        this.hasCoxswain = hasCoxswain;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public String getName() {
        return name;
    }

    public BoatType getBoatType() {
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

    public boolean isHasCoxswain() {
        return hasCoxswain;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    @XmlAttribute
    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute
    public void setBoatType(BoatType boatType) {
        this.boatType = boatType;
    }

    @XmlAttribute
    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    @XmlAttribute
    public void setCoastal(boolean isCoastal) {
        this.isCoastal = isCoastal;
    }

    @XmlAttribute
    public void setDisabled(boolean disabled) {
        this.isDisabled = disabled;
    }

    @XmlAttribute
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Boat)) return false;
        Boat boat = (Boat) o;
        return Objects.equals(serialNumber, boat.serialNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialNumber);
    }
}
