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
}

function handlePastReservations(event) {
    const data = {
        requestType: "past",
        day: null
    }
    getSelectedReservations(data);
}

function handleNextWeekReservations(event) {
    const data = {
        requestType: "next",
        day: null
    }
    getSelectedReservations(data);
}

function handleSpecificDayReservations(event) {
    const selectedDayOfWeek = document.getElementById('daysDropDownMenu').value;
    const data = {
        requestType: "day",
        day: selectedDayOfWeek
    }
    getSelectedReservations(data);
}

async function getSelectedReservations(data) {
    const response = await fetch('../../myReservation', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if (response.ok) {
        const reservationList = await response.json();
        const reservationTableBody = document.getElementById('tableBody');

        for(let i = 0; i < reservationList.length; i++) {
            reservationTableBody.appendChild(buildTableEntry(reservationList[i], i+1));
        }
    } else {
        window.alert("No reservations to display");
    }
}

function buildTableEntry(reservation, index) {
    const tableEntryEl = document.createElement("tr");
    const tableHeaderEl = document.createElement("th");
    const checkBoxEl = document.createElement("input");
    checkBoxEl.classList.add("form-check-input");
    checkBoxEl.setAttribute("type", "checkbox");
    checkBoxEl.setAttribute("id", "check" + index);
    tableHeaderEl.setAttribute("scope", "row");
    tableHeaderEl.appendChild(checkBoxEl);
    tableEntryEl.appendChild(tableHeaderEl);
    appendTableData(tableEntryEl, reservation);

    return tableEntryEl;
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