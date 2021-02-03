const STATUS_OK = 200;
let optionSelected;
var removeReservationButtonEl = document.getElementById('buttonRemoveReservation');
var editReservationButtonEl = document.getElementById('buttonEditReservation');

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
    optionSelected = "past";
    const data = {
        requestType: "past",
        day: null
    }
    getSelectedReservations(data);
}

async function handleNextWeekReservations(event) {
    removeReservationButtonEl.disabled = false;
    editReservationButtonEl.disabled = false;
    optionSelected = "next";
    const data = {
        requestType: "next",
        day: null
    }
    getSelectedReservations(data);
}

async function handleSpecificDayReservations(event) {
    removeReservationButtonEl.disabled = false;
    editReservationButtonEl.disabled = false;
    optionSelected = "day";
    const selectedDayOfWeek = document.getElementById('daysDropDownMenu').value;
    const data = {
        requestType: "day",
        day: selectedDayOfWeek
    }
    getSelectedReservations(data);
}

async function handleRemoveReservationRequest(event) {
    const tableBodyEl = document.querySelector("#tableBody");
    const allCheckBoxes = tableBodyEl.getElementsByTagName("input");
    let boxChecked = false;
    for (let i = 0; i < allCheckBoxes.length; i++) {
        if(allCheckBoxes[i].checked === true) {
            boxChecked = true;
            break;
        }
    }

    if (boxChecked) {
        // allCheckBoxes[i].checked === true
        const data = {

        }

        const response = await fetch('../../deleteReservation', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });



    } else {
        // no box checked

    }
}

async function handleEditReservationRequest(event) {

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

        for(let i = 0; i < reservationList.length; i++) {
            reservationTableBody.appendChild(buildTableEntry(reservationList[i], i+1));
        }
    } else {
        const alertPopup = document.getElementById("alertText");
        alertPopup.style.background ="white";
        alertPopup.textContent = "No reservations to display";
        removeReservationButtonEl.disabled = true;
        editReservationButtonEl.disabled = true;
    }
}

function buildTableEntry(reservation, index) {
    const tableEntryEl = document.createElement("tr");
    const tableHeaderEl = document.createElement("th");
    const checkBoxEl = document.createElement("input");
    checkBoxEl.classList.add("form-check-input");
    checkBoxEl.setAttribute("type", "checkbox");
    checkBoxEl.setAttribute("id", "check" + index);
    checkBoxEl.addEventListener("change", checkAllBoxes);
    tableHeaderEl.setAttribute("scope", "row");
    tableHeaderEl.appendChild(checkBoxEl);
    tableEntryEl.appendChild(tableHeaderEl);
    appendTableData(tableEntryEl, reservation);

    return tableEntryEl;
}

function checkAllBoxes() {
    const tableBodyEl = document.querySelector("#tableBody");
    const allCheckBoxes = tableBodyEl.getElementsByTagName("input");
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