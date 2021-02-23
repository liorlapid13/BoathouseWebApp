let reservationToEdit;

let modal;
let modalBody;
let modalTitle;
let finalModal;
let finalModalBody;
let finalModalTitle;

window.addEventListener('load', () => {
    initializeModals();
    initializeBoatTypeCheckBoxes();
    setupEventHandlers();
});

function setupEventHandlers() {
    const confirmButtonEl = document.getElementById('buttonConfirm');
    confirmButtonEl.addEventListener('click', handleConfirm);
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

async function handleConfirm(event) {
    const boatTypesTableBodyEl = document.getElementById('boatTypesTableBody');
    const boatTypeCheckBoxes = boatTypesTableBodyEl.getElementsByTagName('input');
    let boxesChecked = 0;
    let selectedBoatTypes = [];
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
    } else {
        if (areBoatTypeArraysEqual(reservationToEdit.boatTypes, selectedBoatTypes)) {
            modalTitle.textContent = "" ;
            modalBody.textContent = "These are the current reservation boat types"
            showModal(modal);
            return;
        }

        let boatCrewSize = reservationToEdit.boatCrew.length;
        let maxMembersInCrew = calculateMaxBoatTypesCapacity(selectedBoatTypes);
        let doSelectedBoatTypesNeedCoxswain = doBoatTypesNeedCoxswain(selectedBoatTypes);
        if (maxMembersInCrew >= boatCrewSize && (doSelectedBoatTypesNeedCoxswain ||  !reservationToEdit.coxswainSelected)) {
            const data = {
                reservation: reservationToEdit,
                boatTypes: selectedBoatTypes
            }

            const response = await fetch('../../../editReservationBoatTypes', {
                method: 'post',
                headers: new Headers({
                    'Content-Type': 'application/json;charset=utf-8'
                }),
                body: JSON.stringify(data)
            });

            if (response.status === STATUS_OK) {
                let updatedReservation = await response.json();
                sessionStorage.setItem(RESERVATION_TO_MANAGER_EDIT, JSON.stringify(updatedReservation));
                finalModalTitle.textContent = "";
                finalModalBody.textContent = "Boat types successfully changed";
                finalModalBody.style.color = "green";
            } else {
                finalModalTitle.textContent = "Pay Attention!";
                finalModalBody.textContent = "Error: Boat types have not been changed";
                finalModalBody.style.color = "red";
            }
            showModal(finalModal);
        } else {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent =
                "The number of crew members cannot exceed the biggest boat type selected. " +
                "If none of the selected boat types require a coxswain, there must be no coxswain in the reservation, " +
                "please try again";
            modalBody.style.color = "red";
            showModal(modal);
        }
    }
}

function areBoatTypeArraysEqual(boatTypes, newBoatTypes) {
    if (boatTypes.length !== newBoatTypes.length) {
        return false;
    }

    const sortedBoatTypes = boatTypes.concat().sort();
    const sortedNewBoatTypes = newBoatTypes.concat().sort();
    for (let i = 0; i < sortedBoatTypes; i++) {
        if (sortedBoatTypes[i] !== sortedNewBoatTypes[i]) {
            return false;
        }
    }

    return true;
}

function initializeBoatTypeCheckBoxes() {
    reservationToEdit = JSON.parse(sessionStorage.getItem(RESERVATION_TO_MANAGER_EDIT));
    for (let i = 0; i < reservationToEdit.boatTypes.length; i++) {
        document.getElementById(reservationToEdit.boatTypes[i]).checked = true;
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