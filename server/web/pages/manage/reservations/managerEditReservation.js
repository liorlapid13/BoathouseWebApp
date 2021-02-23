let reservationToEdit;

let modal;
let modalBody;
let modalTitle;

window.addEventListener('load', () => {
    initializeReservationData();
    initializeAvailableBoats();
    initializeModal();
    setupEventHandlers();
});

function setupEventHandlers() {
    const splitButtonEl = document.getElementById('buttonSplit');
    splitButtonEl.addEventListener('click', handleSplitReservation);
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() =>{
        hideModal(modal);
    });
}

function handleSplitReservation(event) {
    if (reservationToEdit.boatCrew.length > 1 || reservationToEdit.coxswainSelected) {
        window.location.href="splitReservation.html";
    } else {
        modalTitle.textContent = "Pay Attention!";
        modalBody.textContent = "This reservation is unsplittable!"
        showModal(modal);
    }
}

function initializeReservationData() {
    reservationToEdit = JSON.parse(sessionStorage.getItem(RESERVATION_TO_MANAGER_EDIT));
    const reservationTableBodyEl = document.getElementById('reservationTableBody');
    reservationTableBodyEl.appendChild(buildShortReservationTableEntry(reservationToEdit));
}

async function initializeAvailableBoats() {
    const response = await fetch('../../../availableBoatsForActivity', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(reservationToEdit)
    });

    if (response.status === STATUS_OK) {
        let boatList = await response.json();
        const boatsTableBodyEl = document.getElementById('availableBoatsTableBody');
        for (let i = 0; i < boatList.length; i++) {
            boatsTableBodyEl.appendChild(buildBoatTableEntry(boatList[i]));
        }
    } else {
        const alertPopup = document.getElementById("alertText");
        alertPopup.style.background = "white";
        alertPopup.textContent = "No boats to display";
    }
}

function initializeModal() {
    modal = document.getElementById("Modal");
    modalBody = document.querySelector(".modal-body");
    modalTitle = document.getElementById("ModalLabel");
}
