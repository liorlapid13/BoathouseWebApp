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

function calculateMaxBoatTypesCapacity(selectedBoatTypes) {
    let maxMembersInCrew = 0;
    for (let i = 0; i < selectedBoatTypes.length; i++) {
        let currentBoatType = selectedBoatTypes[i].charAt(0);
        if (currentBoatType > maxMembersInCrew) {
            maxMembersInCrew = parseInt(currentBoatType);
        }
    }

    return maxMembersInCrew;
}

function doBoatTypesNeedCoxswain(selectedBoatTypes) {
    for (let i = 0; i < selectedBoatTypes.length; i++) {
        if (selectedBoatTypes[i].includes("+")) {
            return true;
        }
    }

    return false;
}

function initializeDaysDropDownMenu(dropDownOptions) {
    const options = {weekday: 'long', year: 'numeric', month: 'numeric', day: 'numeric'};
    let date = new Date();
    let i;
    for (i = 1; i <= 7; i++) {
        date.setDate(date.getDate() + 1);
        let isoDate = new Date(date.getTime() - (date.getTimezoneOffset() * 60000)).toISOString();
        let isoSplit = isoDate.split("T", 2);
        let modifyDate = date.toLocaleDateString("en-US", options);
        let modifyDateSplit = modifyDate.split(" ", 2);
        modifyDateSplit[0] = modifyDateSplit[0].concat(" ",isoSplit[0]);
        dropDownOptions[i - 1].textContent = modifyDateSplit[0]
    }
}

function createGreenCheckIcon(){
    const newCheckIcon =document.createElement("i")
    newCheckIcon.setAttribute("aria-hidden","true");
    newCheckIcon.classList.add("fa","fa-check");
    newCheckIcon.style.color = "green";
    return newCheckIcon;
}

function createRedXIcon(){
    const newCheckIcon =document.createElement("i")
    newCheckIcon.setAttribute("aria-hidden","true");
    newCheckIcon.classList.add("fa", "fa-times");
    newCheckIcon.style.color = "red";
    return newCheckIcon;
}
function addIconDependentOnCondition(parentEl,condition){
    if(condition){
        parentEl.appendChild(createGreenCheckIcon())
    }
    else{
        parentEl.appendChild(createRedXIcon());
    }
}