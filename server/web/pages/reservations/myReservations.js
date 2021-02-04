const STATUS_OK = 200;
let currentSelectedOption;
let currentSelectedDay;
let removeReservationButtonEl;
let editReservationButtonEl;

window.addEventListener('load', () => {
    initializeDaysDropDownMenu();
    setupEventHandlers();
});

function setupEventHandlers() {
    const nextWeekReservationsButtonEl = document.getElementById('buttonNextWeekReservations');
    nextWeekReservationsButtonEl.addEventListener('click', handleNextWeekReservations);
    const pastWeekReservationsButtonEl = document.getElementById('buttonPastWeekReservations');
    pastWeekReservationsButtonEl.addEventListener('click', handlePastReservations);
    const specificDayReservationsButtonEl = document.getElementById('buttonSpecificDayReservations');
    specificDayReservationsButtonEl.addEventListener('click', handleSpecificDayReservations);
    removeReservationButtonEl = document.getElementById('buttonRemoveReservation');
    removeReservationButtonEl.addEventListener('click', handleRemoveReservationRequest);
    editReservationButtonEl = document.getElementById('buttonEditReservation');
    editReservationButtonEl.addEventListener('click', handleEditReservationRequest);
}

async function handlePastReservations(event) {
    removeReservationButtonEl.disabled = true;
    editReservationButtonEl.disabled = true;
    currentSelectedOption = "past";
    const data = {
        requestType: "past",
        day: null
    }
    getSelectedReservations(data);
}

async function handleNextWeekReservations(event) {
    removeReservationButtonEl.disabled = false;
    editReservationButtonEl.disabled = false;
    currentSelectedOption = "next";
    const data = {
        requestType: "next",
        day: null
    }
    getSelectedReservations(data);
}

async function handleSpecificDayReservations(event) {
    removeReservationButtonEl.disabled = false;
    editReservationButtonEl.disabled = false;
    currentSelectedOption = "day";
    currentSelectedDay = document.getElementById('daysDropDownMenu').value;
    const data = {
        requestType: "day",
        day: currentSelectedDay
    }
    getSelectedReservations(data);
}

async function handleRemoveReservationRequest(event) {
    const tableBodyEl = document.querySelector("#tableBody");
    let checkedCheckBox = findCheckedCheckBox();
    if (checkedCheckBox !== -1) {
        const allTableRowEl = tableBodyEl.getElementsByTagName("tr");
        const reservationStatus =
            (allTableRowEl[checkedCheckBox].getElementsByClassName("reservationStatus"))[0];
        if (reservationStatus.textContent === "Confirmed") {
            if (!window.confirm("This reservation has an assignment, are you sure you want to remove it?")) {
                return;
            }
        }

        const data = {
            requestType: currentSelectedOption,
            day: currentSelectedDay,
            index: checkedCheckBox
        }

        const response = await fetch('../../removeReservation', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });

        if (response.status === STATUS_OK) {
            // TODO: "Reservation removed successfully"
            const reservationList = JSON.parse(sessionStorage.getItem('reservationList'));
            reservationList.splice(checkedCheckBox, 1);
            allTableRowEl[checkedCheckBox].remove();
            if (!tableBodyEl.firstChild) {
                noReservationsAlert();
                sessionStorage.removeItem('reservationList');
            } else {
                sessionStorage.setItem('reservationList', JSON.stringify(reservationList));
            }
        }
    } else {
        // TODO: "You must select a reservation to remove"
    }
}

async function handleEditReservationRequest(event) {
    const tableBodyEl = document.querySelector("#tableBody");
    let checkedCheckBox = findCheckedCheckBox();
    if (checkedCheckBox !== -1) {
        const allTableRowEl = tableBodyEl.getElementsByTagName("tr");
        const reservationStatus = (allTableRowEl[checkedCheckBox].getElementsByClassName("reservationStatus"))[0];
        if (reservationStatus.textContent === "Unconfirmed") {
            const reservationToEdit = JSON.parse(sessionStorage.getItem('reservationList'))[checkedCheckBox];
            sessionStorage.setItem('reservationToEdit', reservationToEdit);
            window.location.href = "editReservation.html";
        } else {
            // TODO: "You can only edit unconfirmed reservations"
        }
    } else {
        // TODO: "You must select a reservation to remove"
    }
}

async function getSelectedReservations(data) {
    const alertPopup = document.getElementById("alertText");
    alertPopup.style.background = "";
    alertPopup.textContent = "";
    const response = await fetch('../../myReservations', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });
    const reservationTableBody = document.getElementById('tableBody');
    while (reservationTableBody.firstChild) {
        reservationTableBody.removeChild(reservationTableBody.firstChild);
    }

    if (response.status === STATUS_OK) {
        const reservationList = await response.json();
        sessionStorage.setItem('reservationList', JSON.stringify(reservationList));
        for(let i = 0; i < reservationList.length; i++) {
            reservationTableBody.appendChild(buildTableEntry(reservationList[i], i+1));
        }
    } else {
        noReservationsAlert();
    }
}

function noReservationsAlert() {
    const alertPopup = document.getElementById("alertText");
    alertPopup.style.background ="white";
    alertPopup.textContent = "No reservations to display";
    removeReservationButtonEl.disabled = true;
    editReservationButtonEl.disabled = true;
}

function buildTableEntry(reservation, index) {
    const tableEntryEl = document.createElement("tr");
    const tableHeaderEl = document.createElement("th");
    const checkBoxEl = document.createElement("input");
    checkBoxEl.classList.add("form-check-input");
    checkBoxEl.setAttribute("type", "checkbox");
    checkBoxEl.setAttribute("id", "check" + index);
    checkBoxEl.addEventListener("change", checkAllCheckBoxes);
    tableHeaderEl.setAttribute("scope", "row");
    tableHeaderEl.appendChild(checkBoxEl);
    tableEntryEl.appendChild(tableHeaderEl);
    appendTableData(tableEntryEl, reservation);

    return tableEntryEl;
}

function getAllCheckBoxes() {
    const tableBodyEl = document.querySelector("#tableBody");
    return tableBodyEl.getElementsByTagName("input");
}

function findCheckedCheckBox() {
    const allCheckBoxes = getAllCheckBoxes();
    for (let i = 0; i < allCheckBoxes.length; i++) {
        if (allCheckBoxes[i].checked === true) {
            return i;
        }
    }

    return -1;
}

function checkAllCheckBoxes() {
    const allCheckBoxes = getAllCheckBoxes();
    for (let i = 0; i < allCheckBoxes.length; i++) {
        if(allCheckBoxes[i].id != this.id && allCheckBoxes[i].checked === true) {
            allCheckBoxes[i].checked = false;
        }
    }
}

function appendTableData(tableEntryEl, reservation) {
    const reservatorDataEl = document.createElement("td");
    const dateDataEl = document.createElement("td");
    const activityDataEl = document.createElement("td");
    const boatTypesDataEl = document.createElement("td");
    const boatCrewDataEl = document.createElement("td");
    const statusDataEl = document.createElement("td");
    const creationDateDataEl = document.createElement("td");

    statusDataEl.classList.add("reservationStatus");

    reservatorDataEl.textContent = reservation.reservator;
    dateDataEl.textContent = reservation.date;
    activityDataEl.textContent = reservation.activity;
    boatTypesDataEl.textContent = reservation.boatTypes;
    boatCrewDataEl.textContent = reservation.boatCrew;
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