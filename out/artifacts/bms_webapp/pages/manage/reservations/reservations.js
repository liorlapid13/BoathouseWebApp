let reservationList;

let currentSelectedOption;
let currentSelectedDay;
let removeReservationButtonEl;
let editReservationButtonEl;

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
function handleNextWeekReservations(){
    removeReservationButtonEl.disabled = false;
    editReservationButtonEl.disabled = false;
    currentSelectedOption = "next";
    const data = {
        filterIndex: checkedFilter(),
        requestType: "next",
        daysFromToday : null
    }
    getSelectedReservations(data);

}

function handleSpecificDayReservations(){
    removeReservationButtonEl.disabled = false;
    editReservationButtonEl.disabled = false;
    currentSelectedOption = "day";
    currentSelectedDay = document.getElementById('daysDropDownMenu').value;
    const data = {
        filterIndex: checkedFilter(),
        requestType: "day",
        daysFromToday : currentSelectedDay
    }
    getSelectedReservations(data);

}
async function getSelectedReservations(data) {
    const alertPopup = document.getElementById("alertText");
    alertPopup.style.background = "";
    alertPopup.textContent = "";
    const response = await fetch('../../../allReservations', {
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
        sessionStorage.setItem(ALL_RESERVATIONS_LIST, JSON.stringify(reservationList));
        for(let i = 0; i < reservationList.length; i++) {
            reservationTableBody.appendChild(buildReservationTableEntry(reservationList[i]));
        }
    } else {
        noReservationsAlert();
    }
}

async function handleRemoveReservationRequest() {
    const reservationTableBodyEl = document.getElementById('tableBody');
    let checkedCheckBox = findCheckedCheckBox(getAllCheckBoxes());
    if (checkedCheckBox !== -1) {
        const reservationToRemove = reservationList[checkedCheckBox];

        const response = await fetch('../../../removeReservation', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(reservationToRemove)
        });

        if (response.status === STATUS_OK) {
            modalTitle.textContent = "";
            modalBody.style.color = "green";
            modalBody.textContent = "Reservation removed successfully"
            showModal(modal);
            // TODO - remove only the selected reservation instead?
            while (reservationTableBodyEl.firstChild) {
                reservationTableBodyEl.removeChild(reservationTableBodyEl.firstChild);
            }
            // TODO - initializeReservationTable?
        }
    }
    else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select a reservation to remove"
        showModal(modal);
    }
}

function handleEditReservationRequest() {
    let checkedCheckBox = findCheckedCheckBox(getAllCheckBoxes());
    if (checkedCheckBox !== -1) {
        const reservationToEdit = reservationList[checkedCheckBox];
        sessionStorage.setItem(RESERVATION_TO_MANAGER_EDIT, JSON.stringify(reservationToEdit));
        window.location.href = "#"; // TODO - add link to manager-edit reservation
    } else {
        modalTitle.textContent = "Pay Attention!";
        modalBody.textContent = "You must select a reservation to edit!"
        showModal(modal);
    }
}

function initializeModal() {
    modal = document.getElementById("Modal");
    modalBody = document.querySelector(".modal-body");
    modalTitle = document.getElementById("ModalLabel");
}

function activeOptions() {
    document.getElementById('buttonSpecificDayReservations').disabled = false;
    document.getElementById('buttonNextWeekReservations').disabled = false;
}

function noReservationsAlert() {
    const alertPopup = document.getElementById("alertText");
    alertPopup.style.background ="white";
    alertPopup.textContent = "No reservations to display";
    removeReservationButtonEl.disabled = true;
    editReservationButtonEl.disabled = true;
}

function checkedFilter() {
    const allFilters = document.getElementById("reservationsFilters").getElementsByTagName('input');
    for(let i=0; i<allFilters.length; i++){
        if(allFilters[i].checked === true){
            return i;
        }
    }
}

function getAllCheckBoxes() {
    const reservationTableBodyEl = document.getElementById("tableBody");
    return reservationTableBodyEl.getElementsByTagName("input");
}