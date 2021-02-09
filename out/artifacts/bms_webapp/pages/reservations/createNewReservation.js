let selectedDay;
let selectedActivity;
let selectedBoatTypes = [];
let selectedBoatCrew = [];
let selectedCoxswain;
let coxswainSelected;
let selectedReservator;
let selectedManualTime;

let activityList;
let memberList;

let selectDayButtonEl;
let selectActivityButtonEl;
let selectBoatTypesButtonEl;
let selectBoatCrewButtonEl;
let selectReservatorButtonEl;
let createReservationButtonEl;

let maxMembersInCrew;
let doesReservationNeedCoxswain;

let modal;
let modalBody;
let modalTitle;
let finalModal;
let finalModalBody;
let finalModalTitle;


window.addEventListener('load', () => {
    initializeModals();
    const daysDropDownMenu = document.getElementById('daysDropDownMenu');
    const dropDownOptions = daysDropDownMenu.getElementsByTagName('option');
    initializeDaysDropDownMenu(dropDownOptions);
    setupEventHandlers();
});

function setupEventHandlers() {
    selectDayButtonEl = document.getElementById('buttonSelectDay');
    selectDayButtonEl.addEventListener('click', handleDaySelection);
    selectActivityButtonEl = document.getElementById('buttonSelectActivity');
    selectActivityButtonEl.addEventListener('click', handleActivitySelection);
    selectBoatTypesButtonEl = document.getElementById('buttonSelectBoatTypes');
    selectBoatTypesButtonEl.addEventListener('click', handleBoatTypesSelection);
    selectBoatCrewButtonEl = document.getElementById('buttonSelectBoatCrew');
    selectBoatCrewButtonEl.addEventListener('click', handleBoatCrewSelection);
    selectReservatorButtonEl = document.getElementById('buttonSelectReservator');
    selectReservatorButtonEl.addEventListener('click', handleReservatorSelection);
    createReservationButtonEl = document.getElementById('buttonCreateReservation');
    createReservationButtonEl.addEventListener('click', handleReservationCreation);
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() => {
        hideModal(modal);
    });
    const finalModalRedirectButtonEl = document.getElementById('redirectButton');
    finalModalRedirectButtonEl.addEventListener('click', () => {
        hideModal(finalModal);
        window.location.href = "myReservations.html";
    });
}

async function handleDaySelection(event) {
    selectDayButtonEl.disabled = true;
    selectActivityButtonEl.disabled = false;
    const daysDropDownMenuEl = document.getElementById("daysDropDownMenu");
    daysDropDownMenuEl.disabled = true;
    const dropDownOptions = daysDropDownMenuEl.getElementsByTagName('option');
    selectedDay = dropDownOptions[daysDropDownMenuEl.value - 1].textContent;

    const response = await fetch('../../activities', {
        method: 'get',
    });

    if (response.status === STATUS_OK) {
        const activityDropDownMenuEl = document.getElementById('activityDropDownMenu');
        activityList = await response.json();
        for (let i = 0; i < activityList.length; i++) {
            activityDropDownMenuEl.appendChild(createActivityOption(activityList[i], i));
        }
    } else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "There are no available activities for this day"
        showModal(modal);
        selectActivityButtonEl.textContent = "Select Time";
        const manualActivityEl = document.getElementById('manualActivity');
        manualActivityEl.style.display = "block";
    }
}

function handleActivitySelection(event) {
    const activitiesMenuEl = document.getElementById("activityDropDownMenu");
    activitiesMenuEl.disabled = true;
    if (activitiesMenuEl.childNodes.length > 1) {
        selectedActivity = activityList[document.getElementById('activityDropDownMenu').value];
        selectActivityButtonEl.disabled = true;
        selectBoatTypesButtonEl.disabled = false;
    } else {
        const startTimeHours = document.getElementById('startTimeHours');
        const startTimeMinutes = document.getElementById('startTimeMinutes');
        const endTimeHours = document.getElementById('endTimeHours');
        const endTimeMinutes = document.getElementById('endTimeMinutes');
        if (checkManualTime(startTimeHours.value, startTimeMinutes.value, endTimeHours.value, endTimeMinutes.value)) {
            selectedActivity = null;
            selectedManualTime = startTimeHours.value + ":" + startTimeMinutes.value + "-" +  endTimeHours.value + ":" + endTimeMinutes.value;
            startTimeHours.disabled = true;
            startTimeMinutes.disabled = true;
            endTimeHours.disabled = true;
            endTimeMinutes.disabled = true;
            selectActivityButtonEl.disabled = true;
            selectBoatTypesButtonEl.disabled = false;
        } else {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "Start time must be before end time!"
            showModal(modal);
        }
    }
}

async function handleBoatTypesSelection(event) {
    const boatTypesTableBodyEl = document.getElementById("boatTypesTableBody");
    const allCheckBoxes = boatTypesTableBodyEl.getElementsByTagName("input");
    let boxesChecked = 0;
    for (let i = 0; i < allCheckBoxes.length; i++) {
        if (allCheckBoxes[i].checked === true) {
            selectedBoatTypes.push(allCheckBoxes[i].id);
            boxesChecked++;
        }
    }

    if (boxesChecked === 0) {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select at least one boat type!"
        showModal(modal);
        return;
    } else {
        selectBoatTypesButtonEl.disabled = true;
        selectBoatCrewButtonEl.disabled = false;
        disableBoatTypeCheckBoxes();
    }

    maxMembersInCrew = calculateMaxBoatTypesCapacity(selectedBoatTypes);

    const data = {
        activity: selectedActivity,
        date: selectedDay,
        manualTime: selectedManualTime
    }

    const response = await fetch('../../membersForReservation', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if (response.status === STATUS_OK) {
        const memberTableBodyEl = document.getElementById('memberTableBody');
        memberList = await response.json();
        for (let i = 0; i < memberList.length; i++) {
            memberTableBodyEl.appendChild(buildMemberTableEntry(memberList[i], i));
        }

        doesReservationNeedCoxswain = doBoatTypesNeedCoxswain(selectedBoatTypes);
        if (!doesReservationNeedCoxswain) {
            disableCoxswainCheckBoxes();
        }
    } else {
        finalModalTitle.textContent = "Pay Attention!";
        finalModalBody.textContent = "There are no available members for this activity, returning to my reservations";
        showModal(finalModal);
    }
}

function handleBoatCrewSelection(event) {
    const boatCrewTableBodyEl = document.getElementById('memberTableBody');
    const crewCheckBoxes = document.getElementById("memberTableBody").getElementsByClassName("crewCheckBox");
    let membersSelected = 0;
    for (let i = 0; i < crewCheckBoxes.length; i++) {
        if (crewCheckBoxes[i].checked === true) {
            membersSelected++;
            selectedBoatCrew.push(memberList[i]);
            if (membersSelected === maxMembersInCrew) {
                break;
            }
        }
    }

    if (membersSelected === 0) {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select at least one crew member!"
        showModal(modal);
        return;
    } else {
        selectReservatorButtonEl.disabled = false;
        selectBoatCrewButtonEl.disabled = true;
        disableCrewCheckBoxes();
    }

    coxswainSelected = false;
    if (doesReservationNeedCoxswain) {
        disableCoxswainCheckBoxes();
        const coxswainCheckBoxes = boatCrewTableBodyEl.getElementsByClassName("coxswainCheckBox");
        for (let i = 0; i < coxswainCheckBoxes.length; i++) {
            if (coxswainCheckBoxes[i].checked === true) {
                selectedCoxswain = memberList[i];
                coxswainSelected = true;
                break;
            }
        }
    }

    if (coxswainSelected) {
        membersSelected++;
    }

    const reservatorTableBodyEl = document.getElementById('reservatorTableBody');
    for (let i = 0; i < membersSelected; i++) {
        let member = (i === membersSelected - 1 && coxswainSelected) ? selectedCoxswain : selectedBoatCrew[i];
        reservatorTableBodyEl.appendChild(buildReservatorTableEntry(member, i));
    }
}

function handleReservatorSelection(event) {
    const checkBoxes = document.getElementById('reservatorTableBody').getElementsByTagName("input");
    let reservatorSelected = false;
    for (let i = 0; i < checkBoxes.length; i++) {
        if (checkBoxes[i].checked === true) {
            selectedReservator = (i === checkBoxes.length - 1 && coxswainSelected) ? selectedCoxswain : selectedBoatCrew[i];
            reservatorSelected = true;
            break;
        }
    }

    if (reservatorSelected) {
        selectReservatorButtonEl.disabled = true;
        createReservationButtonEl.disabled = false;
        disableReservatorCheckBoxes();
    } else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select a reservator!"
        showModal(modal);
    }
}

async function handleReservationCreation(event) {
    const data = {
        reservationCreator: null,
        id: null,
        date: selectedDay,
        activity: selectedActivity,
        boatTypes: selectedBoatTypes,
        reservator: selectedReservator,
        boatCrew: selectedBoatCrew,
        coxswain: selectedCoxswain,
        coxswainSelected: coxswainSelected,
        status: null,
        creationDate: null
    }

    const response = await fetch('../../createReservation', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    finalModalTitle.textContent = "";
    finalModalBody.textContent = response.status === STATUS_OK ?
        "Reservation successfully created!" : "Failed to create reservation!";
    showModal(finalModal);
}

function buildMemberTableEntry(member,index) {
    const tableRowEl = document.createElement('tr');
    const tableHeaderEl = document.createElement('th');
    const tableDataEl1 = document.createElement('td');
    const tableDataEl2 = document.createElement('td');
    const checkBoxEl1 = document.createElement('input');
    const checkBoxEl2 = document.createElement('input');

    tableHeaderEl.setAttribute('scope', 'row');
    checkBoxEl1.setAttribute('type', 'checkbox');
    checkBoxEl1.classList.add('form-check-input', 'coxswainCheckBox');
    checkBoxEl1.setAttribute('id', 'check' + index);
    checkBoxEl1.addEventListener('change', checkCoxswainCheckBoxes);
    tableHeaderEl.appendChild(checkBoxEl1);
    checkBoxEl2.setAttribute('type', 'checkbox');
    checkBoxEl2.classList.add('form-check-input', 'crewCheckBox');
    checkBoxEl2.addEventListener('change', checkCrewCheckBoxes);
    tableDataEl1.appendChild(checkBoxEl2);
    tableDataEl2.textContent = member.id + ", " + member.name;
    tableRowEl.appendChild(tableHeaderEl);
    tableRowEl.appendChild(tableDataEl1);
    tableRowEl.appendChild(tableDataEl2);

    return tableRowEl;
}

function buildReservatorTableEntry(member, index) {
    const tableRowEl = document.createElement('tr');
    const tableHeaderEl = document.createElement('th');
    const tableDataEl = document.createElement('td');
    const checkBoxEl = document.createElement('input');

    tableHeaderEl.setAttribute('scope', 'row');
    checkBoxEl.setAttribute('type', 'radio');
    checkBoxEl.setAttribute('name', 'reservatorRadio')
    tableHeaderEl.appendChild(checkBoxEl);
    tableDataEl.textContent = member.id + ", " + member.name;
    tableRowEl.appendChild(tableHeaderEl);
    tableRowEl.appendChild(tableDataEl);

    return tableRowEl;
}

function disableReservatorCheckBoxes() {
    const checkBoxes = document.getElementById('reservatorTableBody').getElementsByTagName("input");
    for (let i = 0; i < checkBoxes.length; i++) {
        checkBoxes[i].disabled = true;
    }
}

function checkCoxswainCheckBoxes() {
    const crewCheckBoxes = document.getElementById("memberTableBody").getElementsByClassName("crewCheckBox");
    const coxswainCheckBoxes = document.getElementById("memberTableBody").getElementsByClassName("coxswainCheckBox");
    for (let i = 0; i < coxswainCheckBoxes.length; i++) {
        if (coxswainCheckBoxes[i].id === this.id) {
            crewCheckBoxes[i].disabled = coxswainCheckBoxes[i].checked === true;
        } else if (coxswainCheckBoxes[i].checked === true) {
            coxswainCheckBoxes[i].checked = false;
            crewCheckBoxes[i].disabled = false;
        }
    }
}

function disableCoxswainCheckBoxes() {
    const coxswainCheckBoxes = document.getElementById("memberTableBody")
        .getElementsByClassName("coxswainCheckBox");
    for (let i = 0; i < coxswainCheckBoxes.length; i++) {
        coxswainCheckBoxes[i].disabled = true;
    }
}

function checkCrewCheckBoxes() {
    const coxswainCheckBoxes = document.getElementById("memberTableBody").getElementsByClassName("coxswainCheckBox");
    const crewCheckBoxes = document.getElementById("memberTableBody").getElementsByClassName("crewCheckBox");
    let index = Array.from(crewCheckBoxes).indexOf(this);
    if (this.checked === true) {
        let checkedCheckBoxes = 0;
        for (let i = 0; i < crewCheckBoxes.length; i++) {
            if (crewCheckBoxes[i].checked === true) {
                coxswainCheckBoxes[i].disabled = true;
                checkedCheckBoxes++;
            }
        }

        if (checkedCheckBoxes === maxMembersInCrew + 1) {
            this.checked = false;
            coxswainCheckBoxes[index].disabled = false;
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "No space in crew, you cannot select more members! "
            showModal(modal);
        }
    } else {
        coxswainCheckBoxes[index].disabled = false;
    }
}

function disableCrewCheckBoxes() {
    const crewCheckBoxes = document.getElementById("memberTableBody")
        .getElementsByClassName("crewCheckBox");
    for (let i = 0; i < crewCheckBoxes.length; i++) {
        crewCheckBoxes[i].disabled = true;
    }
}

function disableBoatTypeCheckBoxes() {
    const boatTypesCheckBoxes = document.getElementById('boatTypesTableBody').getElementsByTagName('input');
    for (let i = 0; i < boatTypesCheckBoxes.length; i++) {
        boatTypesCheckBoxes[i].disabled = true;
    }
}

function checkManualTime(startTimeHours, startTimeMinutes, endTimeHours, endTimeMinutes) {
    let validTime = true;
    if (startTimeHours > endTimeHours) {
        validTime = false;
    } else if (startTimeHours === endTimeHours) {
        if (startTimeMinutes >= endTimeMinutes) {
            validTime = false;
        }
    }

    return validTime;
}

function initializeModals() {
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
    finalModal = document.getElementById("finalModal");
    finalModalBody = document.getElementById("finalModalBody");
    finalModalTitle = document.getElementById("finalModalLabel");
}

