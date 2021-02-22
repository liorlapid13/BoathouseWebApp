let reservationToEdit;
let currentCrewSize;
let currentCrewHasCoxswain;
let newCrew = [];
let newCrewSize = 0;
let newCrewCoxswain = null;
let newCrewCoxswainSelected = false;

let currentCrewTable;
let newCrewTable;

let modal;
let modalBody;
let modalTitle;
let finalModal;
let finalModalBody;
let finalModalTitle;

window.addEventListener('load', () => {
    initializeCurrentCrewTable();
    initializeModals();
    setupEventHandlers();
});

function setupEventHandlers() {
    const confirmButtonEl = document.getElementById('buttonConfirm');
    confirmButtonEl.addEventListener('click', handleSplitReservation);
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

function handleMoveToCrew(event) {
    let selectedIndex = parseInt(this.id.charAt(this.id.length - 1)) - 1;
    disableTableRow(selectedIndex);
    newCrewSize++;
    if (selectedIndex === reservationToEdit.boatCrew.length) {
        newCrew.push(reservationToEdit.coxswain);
        currentCrewHasCoxswain = false;
    } else {
        newCrew.push(reservationToEdit.boatCrew[selectedIndex]);
        --currentCrewSize;
    }
    checkCurrentSplitState();

    const tableEntryEl = document.createElement("tr");
    const roleEl = document.createElement("td");
    const nameEl = document.createElement("td");
    const idEl = document.createElement("id");
    roleEl.classList.add("font-bold");
    roleEl.textContent = "Crew Member";
    nameEl.classList.add("font-bold");
    nameEl.textContent = newCrew[newCrewSize - 1].name;
    idEl.classList.add("font-bold");
    idEl.textContent = newCrew[newCrewSize - 1].id;
    tableEntryEl.appendChild(roleEl);
    tableEntryEl.appendChild(nameEl);
    tableEntryEl.appendChild(idEl);
    newCrewTable.appendChild(tableEntryEl);
}

function handleMoveToCoxswain(event) {
    disableAllMoveToCoxswainButtons();
    let selectedIndex = parseInt(this.id.charAt(this.id.length - 1)) - 1;
    disableTableRow(selectedIndex);
    newCrewCoxswainSelected = true;
    if (selectedIndex === reservationToEdit.boatCrew.length) {
        newCrewCoxswain = reservationToEdit.coxswain;
        currentCrewHasCoxswain = false;
    } else {
        newCrewCoxswain = reservationToEdit.boatCrew[selectedIndex];
        --currentCrewSize;
    }
    checkCurrentSplitState();

    const tableEntryEl = document.createElement("tr");
    const roleEl = document.createElement("td");
    const nameEl = document.createElement("td");
    const idEl = document.createElement("id");
    roleEl.classList.add("font-bold");
    roleEl.textContent = "Coxswain";
    nameEl.classList.add("font-bold");
    nameEl.textContent = newCrewCoxswain.name;
    idEl.classList.add("font-bold");
    idEl.textContent = newCrewCoxswain.id;
    tableEntryEl.appendChild(roleEl);
    tableEntryEl.appendChild(nameEl);
    tableEntryEl.appendChild(idEl);
    newCrewTable.appendChild(tableEntryEl);
}

function checkCurrentSplitState() {
    if (currentCrewSize === 1) {
        disableCrewMemberRows();
        if (!currentCrewHasCoxswain) {
            modalTitle.textContent = "Pay Attention!";
            modalBody.textContent = "You cannot move anymore members to the new crew";
            showModal(modal);
        }
    }

    if (newCrewSize > 0) {
        const confirmButton = document.getElementById('buttonConfirm');
        confirmButton.disabled = false;
    }
}

async function handleSplitReservation(event) {
    const data = {
        reservation: reservationToEdit,
        crew: newCrew,
        coxswain: newCrewCoxswain
    }

    const response = await fetch('../../../splitReservation', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if (response.status === STATUS_OK) {
        let originalReservation = await response.json();
        sessionStorage.setItem(RESERVATION_TO_MANAGER_EDIT, JSON.stringify(originalReservation));
        finalModalTitle.textContent = "";
        finalModalBody.style.color ="green";
        finalModalBody.textContent = "Reservation split successfully";
    } else {
        finalModalTitle.textContent = "Pay Attention!";
        finalModalBody.textContent = "Failed to split reservation";
    }
    showModal(finalModal);
}

function disableTableRow(index) {
    let tableRows = currentCrewTable.getElementsByTagName("tr");
    tableRows[index].disabled = true;
    tableRows[index].style.pointerEvents = "none";
    tableRows[index].style.background = "gainsboro";
    let moveToCrewButton = tableRows[index].getElementsByClassName("moveToCrewButton")[0];
    moveToCrewButton.classList.remove("btn-darkblue");
    moveToCrewButton.classList.add("btn-gray");
    let moveToCoxswainButton = tableRows[index].getElementsByClassName("moveToCoxswainButton")[0];
    moveToCoxswainButton.classList.remove("btn-darkblue");
    moveToCoxswainButton.classList.add("btn-gray");
}

function disableCrewMemberRows() {
    let tableRows = currentCrewTable.getElementsByTagName("tr");
    for (let i = 0; i < tableRows.length; i++) {
        if (i < reservationToEdit.boatCrew.length && !tableRows[i].disabled) {
            disableTableRow(i);
        }
    }
}

function disableAllMoveToCoxswainButtons() {
    let moveToCoxswainButtons = currentCrewTable.getElementsByClassName("moveToCoxswainButton");
    for (let i = 0; i < moveToCoxswainButtons.length; i++) {
        moveToCoxswainButtons[i].disabled = true;
    }
}

function initializeCurrentCrewTable() {
    reservationToEdit = JSON.parse(sessionStorage.getItem(RESERVATION_TO_MANAGER_EDIT));
    currentCrewTable = document.getElementById('currentCrewTableBody');
    newCrewTable = document.getElementById('newCrewTableBody');
    let i;
    for (i = 0; i < reservationToEdit.boatCrew.length; i++) {
        currentCrewTable.appendChild(buildCrewMemberTableEntry(reservationToEdit.boatCrew[i], false, i+1));
    }
    currentCrewSize = i;
    currentCrewHasCoxswain = reservationToEdit.coxswainSelected;
    if (currentCrewHasCoxswain) {
        currentCrewTable.appendChild(buildCrewMemberTableEntry(reservationToEdit.coxswain, true, i+1));
    }

    if (currentCrewSize === 1 || (currentCrewSize === 2 && !currentCrewHasCoxswain)) {
        disableAllMoveToCoxswainButtons();
    }
}

function buildCrewMemberTableEntry(member, isCoxswain, index) {
    const tableEntryEl = document.createElement("tr");
    const roleEl = document.createElement("td");
    const nameAndIdEl = document.createElement("td");
    const moveCrewEl = document.createElement("td");
    const moveCoxswainEl = document.createElement("td");
    const moveCrewButtonEl = document.createElement("button");
    const moveCoxswainButtonEl = document.createElement("button");

    roleEl.classList.add("font-bold");
    roleEl.textContent = isCoxswain ? "Coxswain" : "Crew Member";
    nameAndIdEl.classList.add("font-bold");
    nameAndIdEl.textContent = member.name + ", " + member.id;

    moveCrewButtonEl.setAttribute("id", "buttonMoveCrew" + index);
    moveCrewButtonEl.setAttribute("type", "button");
    moveCrewButtonEl.classList.add("btn-darkblue", "moveToCrewButton");
    moveCrewButtonEl.textContent = "Move to Crew";
    moveCrewButtonEl.addEventListener('click',handleMoveToCrew)
    moveCrewEl.appendChild(moveCrewButtonEl);

    moveCoxswainButtonEl.setAttribute("id", "buttonMoveCoxswain" + index);
    moveCoxswainButtonEl.setAttribute("type", "button");
    moveCoxswainButtonEl.classList.add("btn-darkblue", "moveToCoxswainButton");
    moveCoxswainButtonEl.textContent = "Move to Coxswain";
    moveCoxswainButtonEl.addEventListener('click',handleMoveToCoxswain);
    moveCoxswainEl.appendChild(moveCoxswainButtonEl);

    tableEntryEl.appendChild(roleEl);
    tableEntryEl.appendChild(nameAndIdEl);
    tableEntryEl.appendChild(moveCrewEl);
    tableEntryEl.appendChild(moveCoxswainEl);

    return tableEntryEl;
}

function initializeModals() {
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
    finalModal = document.getElementById("finalModal");
    finalModalBody = document.getElementById("finalModalBody");
    finalModalTitle = document.getElementById("finalModalLabel");
}