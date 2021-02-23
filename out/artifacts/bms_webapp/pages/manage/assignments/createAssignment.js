let reservationList;
let selectedReservation;
let selectedBoat;

let currentSelectedOption;
let currentSelectedDay;

let modal;
let modalBody;
let modalTitle;
let finalModal;
let finalModalBody;
let finalModalTitle;

window.addEventListener('load', () => {
    const daysDropDownMenu = document.getElementById('daysDropDownMenu');
    const dropDownOptions = daysDropDownMenu.getElementsByTagName('option');
    initializeDaysDropDownMenu(dropDownOptions);
    initializeModals();
    setupEventHandlers();
});

function setupEventHandlers() {
    const nextWeekReservationsButtonEl = document.getElementById('buttonNextWeekReservations');
    nextWeekReservationsButtonEl.addEventListener('click', handleNextWeekReservations);
    const specificDayReservationsButtonEl = document.getElementById('buttonSpecificDayReservations');
    specificDayReservationsButtonEl.addEventListener('click', handleSpecificDayReservations);
    const selectReservationButtonEl = document.getElementById('buttonSelectReservation');
    selectReservationButtonEl.addEventListener('click', handleSelectReservation);
    const selectBoatButtonEl = document.getElementById('buttonSelectBoat');
    selectBoatButtonEl.addEventListener('click', handleCreateAssignment);
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() =>{
        hideModal(modal);
    });
    const finalModalRedirectButtonEl = document.getElementById('redirectButton');
    finalModalRedirectButtonEl.addEventListener('click', () => {
        hideModal(finalModal);
        window.location.href = "assignments.html";
    });
}

async function handleNextWeekReservations(event) {
    currentSelectedOption = "next";
    const data = {
        filterIndex: 1,
        requestType: "next",
        daysFromToday : null
    }
    getSelectedReservations(data);
}

async function handleSpecificDayReservations(event) {
    currentSelectedOption = "day";
    currentSelectedDay = document.getElementById('daysDropDownMenu').value;
    const data = {
        filterIndex: 1,
        requestType: "day",
        daysFromToday : currentSelectedDay
    }
    getSelectedReservations(data);
}

async function getSelectedReservations(data) {
    const alertPopup = document.getElementById("reservationAlertText");
    alertPopup.style.background = "";
    alertPopup.textContent = "";
    const response = await fetch('../../../allReservations', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });
    const reservationTableBody = document.getElementById('reservationTableBody');
    while (reservationTableBody.firstChild) {
        reservationTableBody.removeChild(reservationTableBody.firstChild);
    }

    const selectReservationButtonEl = document.getElementById('buttonSelectReservation');
    if (response.status === STATUS_OK) {
        reservationList = await response.json();
        sessionStorage.setItem(RESERVATION_LIST, JSON.stringify(reservationList));
        for(let i = 0; i < reservationList.length; i++) {
            reservationTableBody.appendChild(buildReservationTableEntry(reservationList[i]));
        }
        selectReservationButtonEl.disabled = false;
    } else {
        noReservationsAlert();
        selectReservationButtonEl.disabled = true;
    }
}

async function handleSelectReservation(event) {
    const selectBoatButtonEl = document.getElementById('buttonSelectBoat');
    let checkedCheckBox = findCheckedCheckBox(getAllReservationCheckBoxes());
    if (checkedCheckBox !== -1) {
        selectedReservation = reservationList[checkedCheckBox];
        const alertPopup = document.getElementById("boatsAlertText");
        alertPopup.style.background = "";
        alertPopup.textContent = "";
        const response = await fetch('../../../availableBoatsForReservation', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(selectedReservation)
        });

        if (response.status === STATUS_OK) {
            boatList = await response.json();
            const boatsTableBodyEl = document.getElementById('boatsTableBody');
            for (let i = 0; i < boatList.length; i++) {
                boatsTableBodyEl.appendChild(buildBoatTableEntry(boatList[i]));
            }
            selectBoatButtonEl.disabled = false;
        } else {
            noBoatsAlert();
            selectBoatButtonEl.disabled = true;
        }
    } else {
        modalTitle.textContent = "Pay Attention!";
        modalBody.textContent = "You must select a reservation"
        showModal(modal);
        selectBoatButtonEl.disabled = true;
    }
}

async function handleCreateAssignment(event) {
    let checkedCheckBox = findCheckedCheckBox(getAllBoatCheckBoxes());
    if (checkedCheckBox !== -1) {
        selectedBoat = boatList[checkedCheckBox];
        const data = {
            reservation: selectedReservation,
            boat: selectedBoat
        }

        const response = await fetch('../../createAssignment', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });

        if (response.status === STATUS_OK) {
            finalModalTitle.textContent = "";
            finalModalBody.textContent = "Assignment successfully created!";
            finalModalBody.style.color = "green";
        } else {
            finalModalTitle.textContent = "Pay Attention!";
            finalModalBody.textContent = "Error: Failed to create assignment";
            finalModalBody.style.color = "red";
        }
        showModal(finalModal);
    } else {
        modalTitle.textContent = "Pay Attention!";
        modalBody.textContent = "You must select a boat"
        showModal(modal);
    }
}

function getAllReservationCheckBoxes() {
    const reservationTableBodyEl = document.getElementById("reservationTableBody");
    return reservationTableBodyEl.getElementsByTagName("input");
}

function getAllBoatCheckBoxes() {
    const reservationTableBodyEl = document.getElementById("boatsTableBody");
    return reservationTableBodyEl.getElementsByTagName("input");
}

function noReservationsAlert() {
    const alertPopup = document.getElementById("reservationAlertText");
    alertPopup.style.background ="white";
    alertPopup.textContent = "No reservations to display";
}

function noBoatsAlert() {
    const alertPopup = document.getElementById("boatsAlertText");
    alertPopup.style.background = "white";
    alertPopup.textContent = "No boats to display";
}

function initializeModals() {
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
    finalModal = document.getElementById("finalModal");
    finalModalBody = document.getElementById("finalModalBody");
    finalModalTitle = document.getElementById("finalModalLabel");
}