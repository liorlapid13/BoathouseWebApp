package engine.boat;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class BoatCrew implements Serializable {
    private List<String> crewMembers;
    private String coxswain;

    public BoatCrew(List<String> crewMembers, String coxswain) {
        this.crewMembers = crewMembers;
        this.coxswain = coxswain;
    }
    public BoatCrew() { }

    public List<String> getCrewMembers() {
        return crewMembers;
    }

    public void setCrewMembers(List<String> crewMembers) {
        this.crewMembers = crewMembers;
    }

    public String getCoxswain() {
        return coxswain;
    }

    public void setCoxswain(String coxswain) {
        this.coxswain = coxswain;
    }

    public int size() {
        int crewSize = crewMembers.size();

        if (coxswain != null) {
            crewSize++;
        }

        return crewSize;
    }

    public boolean contains(String id) {
        boolean idIsCoxswain = false;

        if (coxswain != null) {
            idIsCoxswain = coxswain.equals(id);
        }

        return crewMembers.contains(id) || idIsCoxswain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoatCrew)) return false;
        BoatCrew boatCrew = (BoatCrew) o;
        boolean listsEqual = true;

        if (crewMembers.size() == boatCrew.crewMembers.size()) {
            for (int i = 0; i < crewMembers.size(); i++) {
                if (!crewMembers.get(i).equals(boatCrew.crewMembers.get(i))) {
                    listsEqual = false;
                    break;
                }
            }
        }

        return listsEqual && Objects.equals(coxswain, boatCrew.coxswain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(crewMembers, coxswain);
    }
}
