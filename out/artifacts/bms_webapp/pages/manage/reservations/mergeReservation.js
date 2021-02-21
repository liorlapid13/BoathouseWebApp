let reservationToEdit;
let mergeableReservations;

let modal;
let modalBody;
let modalTitle;
let finalModal;
let finalModalBody;
let finalModalTitle;

window.addEventListener('load', () => {
    initializeReservationData();
    initializeMergeableReservations();
    initializeModals();
    setupEventHandlers();
});

function setupEventHandlers() {
    const confirmButtonEl = document.getElementById('buttonConfirm');
    confirmButtonEl.addEventListener('click', handleMergeReservations);
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() =>{
        hideModal(modal);
    });
    const finalModalRedirectButtonEl = document.getElementById('redirectButton');
    finalModalRedirectButtonEl.addEventListener('click', () => {
        hideModal(finalModal);
        window.location.href = "managerEditReservation.html";
    });
}

async function handleMergeReservations(event) {
    let checkedCheckBox = findCheckedCheckBox(getAllCheckBoxes());
    if (checkedCheckBox !== -1) {
        let assignCoxswain = checkIfCoxswainIsAssignable(mergeableReservations[checkedCheckBox]);
        const data = {
            reservation: reservationToEdit,
            reservationToMerge: mergeableReservations[checkedCheckBox],
            assignCoxswain: assignCoxswain
        }

        const response = await fetch('../../../mergeReservations', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });

        if (response.status === STATUS_OK) {
            let mergedReservation = await response.json();
            sessionStorage.setItem(RESERVATION_TO_MANAGER_EDIT, JSON.stringify(mergedReservation));
            finalModalTitle.textContent = "";
            finalModalBody.style.color ="green";
            finalModalBody.textContent = "Reservations merged successfully";
        } else {
            finalModalTitle.textContent = "Pay Attention!";
            finalModalBody.textContent = "Failed to merge reservations";
        }
        showModal(finalModal);
    } else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select a reservation to merge!"
        showModal(modal);
    }
}

function checkIfCoxswainIsAssignable(reservationToMerge) {
    let spaceInCrew =getSpaceInCrew(reservationToEdit.boatTypes,getBoatCrewSize(reservationToEdit));
    let assignCoxswain;
    if(doBoatTypesNeedCoxswain(reservationToEdit.boatTypes) && reservationToEdit.coxswain === undefined)
    {
        if(getBoatCrewSize(reservationToMerge) !== spaceInCrew){
            assignCoxswain = window.confirm("Do you want a coxswain in the new reservation? " +
                "(The coxswain will be assigned automatically)");
        }
        else{
            assignCoxswain = true;
        }
    }
    else{
        assignCoxswain = false;
    }
    return assignCoxswain;
}

async function initializeMergeableReservations() {
    const response = await fetch('../../../mergeableReservations', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(reservationToEdit)
    });

    if (response.status === STATUS_OK) {
        mergeableReservations = await response.json();
        const mergeableReservationsTableBodyEl = document.getElementById("mergeableReservationsTableBody");
        for (let i = 0; i < mergeableReservations.length; i++) {
            mergeableReservationsTableBodyEl.appendChild(buildReservationTableEntry(mergeableReservations[i]));
        }
    } else {
        finalModalTitle.textContent = "Pay Attention!";
        finalModalBody.textContent = "There are no mergeable reservations!";
        showModal(finalModal);
    }
}

function initializeReservationData() {
    reservationToEdit = JSON.parse(sessionStorage.getItem(RESERVATION_TO_MANAGER_EDIT));
    const reservationTableBodyEl = document.getElementById('reservationTableBody');
    reservationTableBodyEl.appendChild(buildShortReservationTableEntry(reservationToEdit));
}

function initializeModals() {
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
    finalModal = document.getElementById("finalModal");
    finalModalBody = document.getElementById("finalModalBody");
    finalModalTitle = document.getElementById("finalModalLabel");
}

function getAllCheckBoxes() {
    const tableBodyEl = document.getElementById('mergeableReservationsTableBody');
    return tableBodyEl.getElementsByTagName('input');
}