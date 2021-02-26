const STATUS_OK = 200;
const RESERVATION_LIST = "reservationList";
const ALL_ASSIGNMENTS_LIST = "assignmentList";
const RESERVATION_TO_EDIT = "reservationToEdit";
const ACTIVITY_TO_EDIT = "activityToEdit";
const BOAT_TO_EDIT = "boatToEdit";
const MEMBER_TO_EDIT = "memberToEdit";
const RESERVATION_TO_MANAGER_EDIT = "reservationToManagerEdit";
const ALL_RESERVATIONS_LIST = "allReservationsList";

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
    boatCrewString = boatCrewString.concat(coxswainSelected === true ? coxswain.name : "none");

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

function getMaxBoatTypeCapacity(boatType){
    return parseInt(boatType.charAt(0));
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

function doBoatTypesNeedCoxswain(boatTypes) {
    for (let i = 0; i < boatTypes.length; i++) {
        if (boatTypes[i].includes("+")) {
            return true;
        }
    }

    return false;
}

function doesBoatTypeNeedCoxswain(boatType) {
    return boatType.includes("+");
}

function getBoatCrewSize(reservation) {
    let crewSize = reservation.boatCrew.length;

    if(reservation.coxswain !== undefined){
        crewSize++;
    }
    return crewSize;
}

function getSpaceInCrew(boatTypes,crewSize) {
    let maxBoatTypeCapacity = calculateMaxBoatTypesCapacity(boatTypes)

    for (let i = 0; i < boatTypes.length; i++){
        if (getMaxBoatTypeCapacity(boatTypes[i]) === maxBoatTypeCapacity &&
            doesBoatTypeNeedCoxswain(boatTypes[i])) {
            maxBoatTypeCapacity++;
            break;
        }
    }

    return maxBoatTypeCapacity - crewSize;
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

function download(filename, text) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
}

function isMemberInBoatCrew(member, boatCrew, coxswain) {
    for (let i = 0; i < boatCrew.length; i++) {
        if (member.id === boatCrew[i].id) {
            return true;
        }
    }

    if (coxswain != null) {
        if (member.id === coxswain.id) {
            return true;
        }
    }

    return false;
}

function addOneMinute(startTime) {
    const startTimeSplit = startTime.split(":");
    let startTimeMinute = startTimeSplit[1];
    let minEndTimeMinutes;
    let minEndTimeHours;
    if(startTimeMinute === "59"){
        minEndTimeMinutes = "00";
        minEndTimeHours = (parseInt(startTimeSplit[0]) + 1).toString();
        if(parseInt(minEndTimeHours) < 10){
            minEndTimeHours = "0" + minEndTimeHours;
        }
    }
    else{
        minEndTimeMinutes =  (parseInt(startTimeMinute) + 1).toString();
        minEndTimeHours = startTimeSplit[0];
        if(parseInt(minEndTimeMinutes) < 10){
            minEndTimeMinutes = "0" + minEndTimeMinutes;
        }
    }

    return minEndTimeHours.concat(":",minEndTimeMinutes);
}

function buildReservationTableEntry(reservation) {
    const tableEntryEl = document.createElement("tr");
    const tableHeaderEl = document.createElement("th");
    const checkBoxEl = document.createElement("input");
    checkBoxEl.setAttribute('type', 'radio');
    checkBoxEl.setAttribute('name', 'reservationRadio');
    tableHeaderEl.setAttribute("scope", "row");
    tableHeaderEl.appendChild(checkBoxEl);
    tableEntryEl.appendChild(tableHeaderEl);
    appendReservationTableData(tableEntryEl, reservation);

    return tableEntryEl;
}

function buildShortReservationTableEntry(reservation) {
    const tableEntryEl = document.createElement("tr");
    const reservatorEl = document.createElement("td");
    const dateEl = document.createElement("td");
    const activityEl = document.createElement("td");
    const boatTypesEl = document.createElement("td");
    const boatCrewEl = document.createElement("td");

    reservatorEl.textContent = reservation.reservator.name;
    dateEl.textContent = reservation.date;
    activityEl.textContent = reservation.activity.name + "\n" + reservation.activity.time;
    boatTypesEl.textContent = parseBoatTypes(reservation.boatTypes);
    boatCrewEl.textContent = parseBoatCrew(reservation.boatCrew, reservation.coxswain, reservation.coxswainSelected);

    tableEntryEl.appendChild(reservatorEl);
    tableEntryEl.appendChild(dateEl);
    tableEntryEl.appendChild(activityEl);
    tableEntryEl.appendChild(boatTypesEl);
    tableEntryEl.appendChild(boatCrewEl);

    return tableEntryEl;
}

function appendReservationTableData(tableEntryEl, reservation) {
    const reservatorDataEl = document.createElement("td");
    const dateDataEl = document.createElement("td");
    const activityDataEl = document.createElement("td");
    const boatTypesDataEl = document.createElement("td");
    const boatCrewDataEl = document.createElement("td");
    const statusDataEl = document.createElement("td");
    const creationDateDataEl = document.createElement("td");

    statusDataEl.classList.add("reservationStatus");

    reservatorDataEl.textContent = reservation.reservator.name;
    dateDataEl.textContent = reservation.date;
    activityDataEl.textContent = reservation.activity.name + "\n" + reservation.activity.time;
    boatTypesDataEl.textContent = parseBoatTypes(reservation.boatTypes);
    boatCrewDataEl.textContent = parseBoatCrew(reservation.boatCrew, reservation.coxswain, reservation.coxswainSelected);
    statusDataEl.textContent = reservation.status;
    creationDateDataEl.textContent = reservation.creationDate;

    tableEntryEl.appendChild(reservatorDataEl);
    tableEntryEl.appendChild(dateDataEl);
    tableEntryEl.appendChild(activityDataEl);
    tableEntryEl.appendChild(boatTypesDataEl);
    tableEntryEl.appendChild(boatCrewDataEl);
    tableEntryEl.appendChild(statusDataEl);
    tableEntryEl.appendChild(creationDateDataEl);
}
