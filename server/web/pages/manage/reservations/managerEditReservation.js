let reservationToEdit;

window.addEventListener('load', () => {
    initializeReservationData();
    initializeAvailableBoats();
});

function initializeReservationData() {
    reservationToEdit = JSON.parse(sessionStorage.getItem(RESERVATION_TO_MANAGER_EDIT));
    const reservationTableBodyEl = document.getElementById('reservationTableBody');
    reservationTableBodyEl.appendChild(buildShortReservationTableEntry(reservationToEdit));
}

async function initializeAvailableBoats() {
    const response = await fetch('../../../reservationAvailableBoats', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(reservationToEdit)
    });

    if (response.status === STATUS_OK) {
        const boatsTableBodyEl = document.getElementById('availableBoatsTableBody');
        let boatList = await response.json();
        for (let i = 0; i < boatList.length; i++) {
            boatsTableBodyEl.appendChild(buildBoatTableEntry(boatList[i]));
        }
    } else {
        const alertPopup = document.getElementById("alertText");
        alertPopup.style.background = "white";
        alertPopup.textContent = "No boats to display";
    }
}

function buildBoatTableEntry(boat) {
    const tableEntryEl = document.createElement("tr");
    const idEl = document.createElement("td");
    const nameEl = document.createElement("td");
    const boatTypeEl = document.createElement("td");

    idEl.textContent = boat.id;
    nameEl.textContent = boat.name;
    boatTypeEl.textContent = boat.boatType;

    tableEntryEl.appendChild(idEl);
    tableEntryEl.appendChild(nameEl);
    tableEntryEl.appendChild(boatTypeEl);

    return tableEntryEl;
}

