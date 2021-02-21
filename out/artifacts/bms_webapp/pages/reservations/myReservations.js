let currentSelectedOption;
let currentSelectedDay;
let removeReservationButtonEl;
let editReservationButtonEl;
let reservationList;
let modal;
let modalBody;
let modalTitle;

window.addEventListener('load', () => {
    const daysDropDownMenu = document.getElementById('daysDropDownMenu');
    const dropDownOptions = daysDropDownMenu.getElementsByTagName('option');
    initializeDaysDropDownMenu(dropDownOptions);
    initializeModal();
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
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() =>{
        hideModal(modal);
    });

}
function initializeModal(){
    modal = document.getElementById("Modal");
    modalBody = document.querySelector(".modal-body");
    modalTitle = document.getElementById("ModalLabel");
}

async function handlePastReservations(event) {
    removeReservationButtonEl.disabled = true;
    editReservationButtonEl.disabled = true;
    currentSelectedOption = "past";
    const data = {
        requestType: "past",
        daysFromToday : null
    }
    getSelectedReservations(data);
}

async function handleNextWeekReservations(event) {
    removeReservationButtonEl.disabled = false;
    editReservationButtonEl.disabled = false;
    currentSelectedOption = "next";
    const data = {
        requestType: "next",
        daysFromToday : null
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
        daysFromToday : currentSelectedDay
    }
    getSelectedReservations(data);
}

async function handleRemoveReservationRequest(event) {
    const tableBodyEl = document.querySelector("#tableBody");
    let checkedCheckBox = findCheckedCheckBox(getAllCheckBoxes());
    if (checkedCheckBox !== -1) {
        const allTableRowEl = tableBodyEl.getElementsByTagName("tr");
        const reservationStatus =
            (allTableRowEl[checkedCheckBox].getElementsByClassName("reservationStatus"))[0];
        if (reservationStatus.textContent === "Confirmed") {
            if (!window.confirm("This reservation has an assignment, are you sure you want to remove it?")) {
                return;
            }
        }

        const response = await fetch('../../removeReservation', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(reservationList[checkedCheckBox])
        });

        if (response.status === STATUS_OK) {
            modalTitle.textContent = "" ;
            modalBody.textContent = "Reservation removed successfully"
            showModal(modal);
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
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select a reservation to remove"
        showModal(modal);
    }
}

async function handleEditReservationRequest(event) {
    const tableBodyEl = document.querySelector("#tableBody");
    let checkedCheckBox = findCheckedCheckBox(getAllCheckBoxes());
    if (checkedCheckBox !== -1) {
        const allTableRowEl = tableBodyEl.getElementsByTagName("tr");
        const reservationStatus = (allTableRowEl[checkedCheckBox].getElementsByClassName("reservationStatus"))[0];
        if (reservationStatus.textContent === "Unconfirmed") {
            const reservationToEdit = JSON.parse(sessionStorage.getItem('reservationList'))[checkedCheckBox];
            sessionStorage.setItem(RESERVATION_TO_EDIT, JSON.stringify(reservationToEdit));
            window.location.href = "editReservation.html";
        } else {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "You can only edit unconfirmed reservations"
            showModal(modal);
        }
    } else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select a reservation to edit"
        showModal(modal);
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
        reservationList = await response.json();
        sessionStorage.setItem('reservationList', JSON.stringify(reservationList));
        for(let i = 0; i < reservationList.length; i++) {
            reservationTableBody.appendChild(buildReservationTableEntry(reservationList[i]));
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

function getAllCheckBoxes() {
    const tableBodyEl = document.querySelector("#tableBody");
    return tableBodyEl.getElementsByTagName("input");
}

function checkAllCheckBoxes() {
    const allCheckBoxes = getAllCheckBoxes();
    for (let i = 0; i < allCheckBoxes.length; i++) {
        if(allCheckBoxes[i].id != this.id && allCheckBoxes[i].checked === true) {
            allCheckBoxes[i].checked = false;
        }
    }
}