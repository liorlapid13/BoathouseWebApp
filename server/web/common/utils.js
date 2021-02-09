const STATUS_OK = 200;
function parseBoatCrew(boatCrew, coxswain, coxswainSelected) {
    let boatCrewString = "";
    for (let i = 0; i < boatCrew.length; i++) {
        boatCrewString = boatCrewString.concat(boatCrew[i].name);
        if (i !== boatCrew.length - 1) {
            boatCrewString = boatCrewString.concat(", ");
            if (i % 2 !== 0) {
                boatCrewString = boatCrewString.concat("\n");
            }
        }
    }

    boatCrewString = boatCrewString.concat("\n");
    boatCrewString = boatCrewString.concat("Coxswain: ");
    boatCrewString = boatCrewString.concat(coxswainSelected === "true" ? coxswain : "none");

    return boatCrewString;
}

function parseBoatTypes(boatTypes) {
    let boatTypesString = "";
    for (let i = 0; i < boatTypes.length; i++) {
        boatTypesString = boatTypesString.concat(boatTypes[i]);
        if (i !== boatTypes.length - 1) {
            boatTypesString = boatTypesString.concat(", ");
        }
    }

    return boatTypesString;
}

function createActivityOption(activity, index) {
    const activityOption = document.createElement('option');
    activityOption.value = index;
    activityOption.textContent = activity.name + ", " + activity.time;

    return activityOption;
}
function findCheckedCheckBox(allCheckBoxes) {
    for (let i = 0; i < allCheckBoxes.length; i++) {
        if (allCheckBoxes[i].checked === true) {
            return i;
        }
    }
    return -1;
}