let editDateButtonEl;
let confirmDateButtonEl;
let editActivityButtonEl;
let confirmActivityButtonEl;
let editBoatTypesButtonEl;
let confirmBoatTypesButtonEl;
let editBoatCrewButtonEl;
let confirmBoatCrewButtonEl;
let cancelButtonEl;

let reservation;
let selectedDate;
let selectedActivity;
let selectedBoatTypes = [];
let selectedBoatCrew = [];

let dateEdited = false;
let activityEdited = false;
let boatTypesEdited = false;
let boatCrewEdited = false;

let modal;
let modalBody;
let modalTitle;
let finalModal;
let finalModalBody;
let finalModalTitle;

const CONFIRM = "Confirm";
const STATUS_OK = 200;
window.addEventListener('load', () => {
    initializeReservationData();
    initializeModals();
    setupEventHandlers();
});

function setupEventHandlers() {
    editDateButtonEl = document.getElementById('buttonEditDate');
    editDateButtonEl.addEventListener('click', handleEditDate);
    confirmDateButtonEl = document.getElementById('buttonConfirmDate');
    confirmDateButtonEl.addEventListener('click', handleConfirmDate);
    editActivityButtonEl = document.getElementById('buttonEditActivity');
    editActivityButtonEl.addEventListener('click', handleEditActivity);
    confirmActivityButtonEl = document.getElementById('buttonConfirmActivity');
    confirmActivityButtonEl.addEventListener('click', handleConfirmActivity);
    editBoatTypesButtonEl = document.getElementById('buttonEditBoatTypes');
    editBoatTypesButtonEl.addEventListener('click', handleEditBoatTypes);
    confirmBoatTypesButtonEl = document.getElementById('buttonConfirmBoatTypes');
    confirmBoatTypesButtonEl.addEventListener('click', handleConfirmBoatTypes);
    editBoatCrewButtonEl = document.getElementById('buttonEditBoatCrew');
    editBoatCrewButtonEl.addEventListener('click', handleEditBoatCrew);
    confirmBoatCrewButtonEl = document.getElementById('buttonConfirmBoatCrew');
    confirmBoatCrewButtonEl.addEventListener('click', handleConfirmBoatCrew);

    cancelButtonEl = document.getElementById('buttonCancel');
    cancelButtonEl.addEventListener('click', handleCancel);
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

function handleEditDate(event) {
    dateEdited = true;
    editDateButtonEl.disabled = true;
    confirmDateButtonEl.textContent = CONFIRM;
    initializeDaysDropDownMenu();
    const daysDropDownMenuEl = document.getElementById('daysDropDownMenu');
    daysDropDownMenuEl.disabled = false;
}

async function handleConfirmDate(event) {
    if (dateEdited) {
        const daysDropDownMenuEl = document.getElementById('daysDropDownMenu');
        const dropDownOptions = daysDropDownMenuEl.getElementsByTagName('option');
        selectedDate = dropDownOptions[daysDropDownMenuEl.value - 1].textContent;
        if (selectedDate !== reservation.date) {
            let isBoatCrewAvailable = await checkIfBoatCrewIsAvailable();
            if (!isBoatCrewAvailable) {
                modalTitle.textContent = "Pay Attention!" ;
                modalBody.textContent = "One or more of the crew members are not available on this day, please try again!"
                showModal(modal);
                return;
            }
        }
        daysDropDownMenuEl.disabled = true;
    } else {
        selectedDate = reservation.date;
    }

    confirmDateButtonEl.disabled = true;
    editActivityButtonEl.disabled = false;
    confirmActivityButtonEl.disabled = false;
}

async function checkIfBoatCrewIsAvailable() {
    const data = {
        date: selectedDate,
        activity: reservation.activity,
        boatCrew: reservation.boatCrew,
        coxswain: reservation.coxswain,
        coxswainSelected: reservation.coxswainSelected
    }

    const response = await fetch('../../boatCrewAvailability', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    return response.status === STATUS_OK;
}

async function handleEditActivity(event) {
    activityEdited = true;
    editActivityButtonEl.disabled = true;
    confirmActivityButtonEl.textContent = CONFIRM;

    const data = {
        day: selectedDate
    }

    const response = await fetch('../../activities', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
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
        const manualActivity = document.getElementById('manualActivity');
        manualActivity.style.display = "block";
    }

    const activityDropDownMenuEl = document.getElementById('activityDropDownMenu');
    activityDropDownMenuEl.disabled = false;
}

function handleConfirmActivity(event) {
    confirmActivityButtonEl.disabled = true;
    editBoatTypesButtonEl.disabled = false;
    confirmBoatTypesButtonEl.disabled = false;
    if (activityEdited) {
        const activityDropDownMenuEl = document.getElementById('activityDropDownMenu');
        selectedActivity = activityDropDownMenuEl.value;
        activityDropDownMenuEl.disaled = true;
    } else {
        selectedActivity = reservation.activity;
    }
}

function handleEditBoatTypes(event) {
    boatTypesEdited = true;
    editBoatTypesButtonEl.disabled = true;
    confirmBoatTypesButtonEl.textContent = CONFIRM;
    enableBoatTypeCheckBoxes();
}

function handleConfirmBoatTypes(event) {
    if (boatTypesEdited) {
        const boatTypesTableBodyEl = document.getElementById('boatTypesTableBody');
        const boatTypeCheckBoxes = boatTypesTableBodyEl.getElementsByTagName("input");
        let boxesChecked = 0;
        for (let i = 0; i < boatTypeCheckBoxes.length; i++) {
            if (boatTypeCheckBoxes[i].checked === true) {
                selectedBoatTypes.push(boatTypeCheckBoxes[i].id);
                boxesChecked++;
            }
        }

        if (boxesChecked === 0) {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "You must select at least one boat type!"
            showModal(modal);
            return;
        }
    } else {
        selectedBoatTypes = reservation.boatTypes;
    }

    confirmBoatTypesButtonEl.disabled = true;
    editBoatCrewButtonEl.disabled = false;
    confirmBoatCrewButtonEl.disabled = false;
    disableBoatTypeCheckBoxes();
    checkIfBoatCrewMustBeReselected();
}

function checkIfBoatCrewMustBeReselected() {

}

function handleEditBoatCrew(event) {
    boatCrewEdited = true;
    editBoatCrewButtonEl.disabled = true;
    confirmBoatCrewButtonEl.textContent = CONFIRM;
}

function handleConfirmBoatCrew(event) {

}



function handleCancel(event) {
    // TODO: Cancel edit reservation
}

function initializeReservationData() {
    const currentDateEl = document.getElementById('currentDate');
    const currentActivityEl = document.getElementById('currentActivity');
    const currentBoatTypesEl = document.getElementById('currentBoatTypes');
    const currentBoatCrewEl = document.getElementById('currentBoatCrew');
    reservation = JSON.parse(sessionStorage.getItem('reservationToEdit'));

    currentDateEl.textContent = reservation.date;
    currentActivityEl.textContent = reservation.activity.name + "\n" + reservation.activity.time + "\n" +
        reservation.activity.restriction;
    currentBoatTypesEl.textContent = reservation.boatTypes;
    currentBoatCrewEl.textContent = parseBoatCrew(reservation.boatCrew, reservation.coxswain, reservation.coxswainSelected);
}

function enableBoatTypeCheckBoxes() {
    const boatTypesTableBodyEl = document.getElementById('boatTypesTableBody');
    const checkBoxes = boatTypesTableBodyEl.getElementsByTagName('input');
    for (let i = 0; i < checkBoxes.length; i++) {
        checkBoxes[i].disabled = false;
    }
}

function disableBoatTypeCheckBoxes() {
    const boatTypesTableBodyEl = document.getElementById('boatTypesTableBody');
    const checkBoxes = boatTypesTableBodyEl.getElementsByTagName('input');
    for (let i = 0; i < checkBoxes.length; i++) {
        checkBoxes[i].disabled = true;
    }
}

function initializeDaysDropDownMenu() {
    const daysDropDownMenu = document.getElementById('daysDropDownMenu');
    const dropDownOptions = daysDropDownMenu.getElementsByTagName('option');
    const options = { weekday: 'long', year: 'numeric', month: 'numeric', day: 'numeric' };

    let day = new Date();
    let i;
    for (i=1; i<=7; i++) {
        day.setDate(day.getDate() + 1);
        dropDownOptions[i-1].textContent = day.toLocaleDateString(undefined, options);
    }
}

function initializeModals() {
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
    finalModal = document.getElementById("finalModal");
    finalModalBody = document.getElementById("finalModalBody");
    finalModalTitle = document.getElementById("finalModalLabel");
}