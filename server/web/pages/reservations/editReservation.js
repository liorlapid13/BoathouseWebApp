let editDateButtonEl;
let confirmDateButtonEl;
let editActivityButtonEl;
let confirmActivityButtonEl;
let editBoatTypesButtonEl;
let confirmBoatTypesButtonEl;
let editBoatCrewButtonEl;
let confirmBoatCrewButtonEl;
let confirmReservatorButtonEl;
let cancelButtonEl;
let confirmButtonEl;

let reservation;
let selectedDate;
let selectedActivity;
let selectedBoatTypes = [];
let doSelectedBoatTypesNeedCoxswain;
let selectedBoatCrew = [];
let selectedCoxswain;
let selectedReservator;

let memberList;
let activityList;

let isDummyActivity = false;
let dateEdited = false;
let activityEdited = false;
let boatTypesEdited = false;
let boatCrewEdited = false;
let forceBoatCrewReselection = false;
let manuallyInsertedMembers = 0;

let modal;
let modalBody;
let modalTitle;
let finalModal;
let finalModalBody;
let finalModalTitle;

const CONFIRM = "Confirm";
window.addEventListener('load', () => {
    initializeReservationData();
    initializeModals();
    setupEventHandlers();
});

function setupEventHandlers() {
    editDateButtonEl = document.getElementById('buttonEditDate');
    editDateButtonEl.addEventListener('click', handleEditDate);
    confirmDateButtonEl = document.getElementById('buttonConfirmDate');
    confirmDateButtonEl.addEventListener('click', handleConfirmDate);
    editActivityButtonEl = document.getElementById('buttonEditActivity');
    editActivityButtonEl.addEventListener('click', handleEditActivity);
    confirmActivityButtonEl = document.getElementById('buttonConfirmActivity');
    confirmActivityButtonEl.addEventListener('click', handleConfirmActivity);
    editBoatTypesButtonEl = document.getElementById('buttonEditBoatTypes');
    editBoatTypesButtonEl.addEventListener('click', handleEditBoatTypes);
    confirmBoatTypesButtonEl = document.getElementById('buttonConfirmBoatTypes');
    confirmBoatTypesButtonEl.addEventListener('click', handleConfirmBoatTypes);
    editBoatCrewButtonEl = document.getElementById('buttonEditBoatCrew');
    editBoatCrewButtonEl.addEventListener('click', handleEditBoatCrew);
    confirmBoatCrewButtonEl = document.getElementById('buttonConfirmBoatCrew');
    confirmBoatCrewButtonEl.addEventListener('click', handleConfirmBoatCrew);
    confirmReservatorButtonEl = document.getElementById('buttonConfirmReservator');
    confirmReservatorButtonEl.addEventListener('click', handleConfirmReservator);
    cancelButtonEl = document.getElementById('buttonCancel');
    cancelButtonEl.addEventListener('click', handleCancel);
    confirmButtonEl = document.getElementById('buttonConfirm');
    confirmButtonEl.addEventListener('click', handleConfirm);
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() => {
        hideModal(modal);
    });
    const finalModalRedirectButtonEl = document.getElementById('redirectButton');
    finalModalRedirectButtonEl.addEventListener('click', () => {
        hideModal(finalModal);
        window.location.href = "myReservations.html";
    });
}

function handleEditDate(event) {
    dateEdited = true;
    editDateButtonEl.disabled = true;
    confirmDateButtonEl.textContent = CONFIRM;
    const daysDropDownMenuEl = document.getElementById('daysDropDownMenu');
    const dropDownOptions = daysDropDownMenuEl.getElementsByTagName('option');
    initializeDaysDropDownMenu(dropDownOptions);
    daysDropDownMenuEl.disabled = false;
}

async function handleConfirmDate(event) {
    if (dateEdited) {
        const daysDropDownMenuEl = document.getElementById('daysDropDownMenu');
        const dropDownOptions = daysDropDownMenuEl.getElementsByTagName('option');
        selectedDate = dropDownOptions[daysDropDownMenuEl.value - 1].textContent;
        if (selectedDate !== reservation.date) {
            let isBoatCrewAvailable = await checkIfBoatCrewIsAvailable(
                selectedDate, reservation.activity, reservation.boatCrew, reservation.coxswain, reservation.coxswainSelected);
            if (!isBoatCrewAvailable) {
                modalTitle.textContent = "Pay Attention!" ;
                modalBody.textContent = "One or more of the crew members are not available on this day, please try again!"
                showModal(modal);
                return;
            }
        } else {
            dateEdited = false;
        }
        daysDropDownMenuEl.disabled = true;
    } else {
        selectedDate = reservation.date;
    }

    confirmDateButtonEl.disabled = true;
    editActivityButtonEl.disabled = false;
    confirmActivityButtonEl.disabled = false;
}

async function handleEditActivity(event) {
    editActivityButtonEl.disabled = true;
    confirmActivityButtonEl.textContent = CONFIRM;

    const response = await fetch('../../activities', {
        method: 'get',
    });

    if (response.status === STATUS_OK) {
        activityEdited = true;
        const activityDropDownMenuEl = document.getElementById('activityDropDownMenu');
        activityList = await response.json();
        for (let i = 0; i < activityList.length; i++) {
            activityDropDownMenuEl.appendChild(createActivityOption(activityList[i], i));
        }
    } else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "There are no available activities, you cannot edit this reservation's activity"
        showModal(modal);
        selectedActivity = reservation.activity;
        confirmActivityButtonEl.disabled = true;
        editBoatTypesButtonEl.disabled = false;
        confirmBoatTypesButtonEl.disabled = false;
        return;
    }

    const activityDropDownMenuEl = document.getElementById('activityDropDownMenu');
    activityDropDownMenuEl.disabled = false;
}

async function handleConfirmActivity(event) {
    if (activityEdited) {
        if (selectedActivity !== reservation.activity) {
            let isBoatCrewAvailable = await checkIfBoatCrewIsAvailable(
                selectedDate, selectedActivity, reservation.boatCrew, reservation.coxswain, reservation.coxswainSelected);
            if (!isBoatCrewAvailable) {
                modalTitle.textContent = "Pay Attention!" ;
                modalBody.textContent = "One or more of the crew members are not available on this day, please try again!";
                showModal(modal);
                return;
            }
        } else {
            activityEdited = true;
        }
        const activityDropDownMenuEl = document.getElementById('activityDropDownMenu');
        selectedActivity = activityDropDownMenuEl.value;
        activityDropDownMenuEl.disabled = true;
    } else {
        selectedActivity = reservation.activity;
    }

    confirmActivityButtonEl.disabled = true;
    editBoatTypesButtonEl.disabled = false;
    confirmBoatTypesButtonEl.disabled = false;
}

function handleEditBoatTypes(event) {
    boatTypesEdited = true;
    editBoatTypesButtonEl.disabled = true;
    confirmBoatTypesButtonEl.textContent = CONFIRM;
    enableBoatTypeCheckBoxes();
}

function handleConfirmBoatTypes(event) {
    if (boatTypesEdited) {
        const boatTypesTableBodyEl = document.getElementById('boatTypesTableBody');
        const boatTypeCheckBoxes = boatTypesTableBodyEl.getElementsByTagName("input");
        let boxesChecked = 0;
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
            return;
        } else {
            let newMaxCapacity = calculateMaxBoatTypesCapacity(selectedBoatTypes);
            let boatCrewSize = reservation.boatCrew.length;
            doSelectedBoatTypesNeedCoxswain = doBoatTypesNeedCoxswain(selectedBoatTypes);
            if (newMaxCapacity < boatCrewSize || (!doSelectedBoatTypesNeedCoxswain && reservation.coxswainSelected)) {
                forceBoatCrewReselection = true;
            }
        }
    } else {
        selectedBoatTypes = reservation.boatTypes;
        doSelectedBoatTypesNeedCoxswain = doBoatTypesNeedCoxswain(selectedBoatTypes);
    }


    confirmBoatTypesButtonEl.disabled = true;
    confirmBoatCrewButtonEl.disabled = false;
    disableBoatTypeCheckBoxes();
    if (forceBoatCrewReselection) {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "The selected boat types require you to reselect the boat crew!"
        showModal(modal);
        handleEditBoatCrew();
    } else {
        editBoatCrewButtonEl.disabled = false;
    }
}

async function handleEditBoatCrew(event) {
    boatCrewEdited = true;
    editBoatCrewButtonEl.disabled = true;
    confirmBoatCrewButtonEl.textContent = CONFIRM;


    const response1 = await fetch('../../activities', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(selectedActivity)
    });

    let manualTime = null;
    let activity = selectedActivity;
    if (response1.status !== STATUS_OK) {
        manualTime = selectedActivity.time;
        activity = null;
        isDummyActivity = true;
    }

    const data2 = {
        activity: activity,
        date: selectedDate,
        manualTime: manualTime
    }

    const response2 = await fetch('../../membersForReservation', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data2)
    });

    const memberTableBodyEl = document.getElementById('memberTableBody');
    if (!dateEdited && !activityEdited) {
        for (let i = 0; i < reservation.boatCrew.length; i++) {
            memberTableBodyEl.appendChild(buildMemberTableEntry(reservation.boatCrew[i], i));
            manuallyInsertedMembers++;
        }

        if (reservation.coxswainSelected) {
            memberTableBodyEl.appendChild(buildMemberTableEntry(reservation.coxswain, i));
            manuallyInsertedMembers++;
        }
    }

    if (response2.status === STATUS_OK) {
        memberList = await response2.json();
        for (let i = manuallyInsertedMembers; i < manuallyInsertedMembers + memberList.length; i++) {
            memberTableBodyEl.appendChild(buildMemberTableEntry(memberList[i - manuallyInsertedMembers], i));
        }
    } else {
        if (manuallyInsertedMembers === 0) {
            if (forceBoatCrewReselection) {
                finalModalTitle.textContent = "Pay Attention!";
                finalModalBody.textContent = "Available members could not be found, cancelling edit activity";
                showModal(finalModal);
            } else {
                modalTitle.textContent = "Pay Attention!" ;
                modalBody.textContent = "There are no available members, boat crew cannot be edited";
                showModal(modal);
                boatCrewEdited = false;
                confirmBoatCrewButtonEl.disabled = true;
                selectedBoatCrew = reservation.boatCrew;
                selectedCoxswain = reservation.coxswain;
                cancelButtonEl.disabled = false;
                confirmButtonEl.disabled = false;
                return;
            }
        }
    }

    if (!doSelectedBoatTypesNeedCoxswain) {
        disableCoxswainCheckBoxes();
    }
}

function handleConfirmBoatCrew(event) {
    if (boatCrewEdited) {
        const boatCrewTableBodyEl = document.getElementById('memberTableBody');
        const crewCheckBoxes = document.getElementById("memberTableBody").getElementsByClassName("crewCheckBox");
        let membersSelected = 0;
        for (let i = 0; i < crewCheckBoxes.length; i++) {
            if (crewCheckBoxes[i].checked === true) {
                membersSelected++;
                if (i >= manuallyInsertedMembers) {
                    selectedBoatCrew.push(memberList[i - manuallyInsertedMembers]);
                } else if (i === manuallyInsertedMembers - 1 && reservation.coxswain !== null) {
                    selectedBoatCrew.push(reservation.coxswain);
                } else {
                    selectedBoatCrew.push(reservation.boatCrew[i]);
                }

                if (membersSelected === maxMembersInCrew) {
                    break;
                }
            }
        }

        if (membersSelected === 0) {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "You must select at least one crew member!"
            showModal(modal);
            return;
        } else {
            selectReservatorButtonEl.disabled = false;
            selectBoatCrewButtonEl.disabled = true;
            disableCrewCheckBoxes();
        }

        if (doSelectedBoatTypesNeedCoxswain) {
            disableCoxswainCheckBoxes();
            let coxswainSelected = false;
            const coxswainCheckBoxes = boatCrewTableBodyEl.getElementsByClassName("coxswainCheckBox");
            for (let i = 0; i < coxswainCheckBoxes.length; i++) {
                if (coxswainCheckBoxes[i].checked === true) {
                    selectedCoxswain = memberList[i];
                    coxswainSelected = true;
                    break;
                }
            }

            if (!coxswainSelected) {
                selectedCoxswain = null;
            }
        }

        const reservatorTableBodyEl = document.getElementById('reservatorTableBody');
        for (let i = 0; i < membersSelected; i++) {
            let member = (i === membersSelected - 1 && doSelectedBoatTypesNeedCoxswain) ? selectedCoxswain : selectedBoatCrew[i];
            reservatorTableBodyEl.appendChild(buildReservatorTableEntry(member, i));
        }

        confirmReservatorButtonEl.disabled = false;
    } else {
        selectedBoatCrew = reservation.boatCrew;
        selectedCoxswain = reservation.coxswain;
        selectedReservator = reservation.reservator;
        cancelButtonEl.disabled = false;
        confirmButtonEl.disabled = false;
    }

    confirmBoatCrewButtonEl.disabled = true;
}

function handleConfirmReservator(event) {
    const checkBoxes = document.getElementById('reservatorTableBody').getElementsByTagName("input");
    let reservatorSelectionFound = false;
    for (let i = 0; i < checkBoxes.length; i++) {
        if (checkBoxes[i].checked === true) {
            selectedReservator = (i === checkBoxes.length - 1 && coxswainSelected !== null) ? selectedCoxswain : selectedBoatCrew[i];
            reservatorSelectionFound = true;
            break;
        }
    }

    if (reservatorSelectionFound) {
        selectReservatorButtonEl.disabled = true;
        confirmButtonEl.disabled = false;
        cancelButtonEl.disabled = false;
        disableReservatorCheckBoxes();
    } else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select a reservator!"
        showModal(modal);
    }
}

function handleCancel(event) {
    finalModalTitle.textContent = "!";
    finalModalBody.textContent = "Reservation has not been changed";
    showModal(finalModal);
}

async function handleConfirm(event) {
    const data = {

    }


}

async function checkIfBoatCrewIsAvailable(date, activity, boatCrew, coxswain, coxswainSelected) {
    const data = {
        date: selectedDate,
        activity: reservation.activity,
        boatCrew: reservation.boatCrew,
        coxswain: reservation.coxswain,
        coxswainSelected: reservation.coxswainSelected
    }

    const response = await fetch('../../boatCrewAvailability', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    return response.status === STATUS_OK;
}

function initializeReservationData() {
    const currentDateEl = document.getElementById('currentDate');
    const currentActivityEl = document.getElementById('currentActivity');
    const currentBoatTypesEl = document.getElementById('currentBoatTypes');
    const currentBoatCrewEl = document.getElementById('currentBoatCrew');
    const currentReservatorEl = document.getElementById('currentReservator');
    reservation = JSON.parse(sessionStorage.getItem('reservationToEdit'));

    currentDateEl.textContent = reservation.date;
    currentActivityEl.textContent = reservation.activity.name + "\n" + reservation.activity.time + "\n" +
        reservation.activity.restriction;
    currentBoatTypesEl.textContent = parseBoatTypes(reservation.boatTypes);
    currentBoatCrewEl.textContent = parseBoatCrew(reservation.boatCrew, reservation.coxswain, reservation.coxswainSelected);
    currentReservatorEl.textContent = reservation.reservator.name;
}

function enableBoatTypeCheckBoxes() {
    const boatTypesTableBodyEl = document.getElementById('boatTypesTableBody');
    const checkBoxes = boatTypesTableBodyEl.getElementsByTagName('input');
    for (let i = 0; i < checkBoxes.length; i++) {
        checkBoxes[i].disabled = false;
    }
}

function disableBoatTypeCheckBoxes() {
    const boatTypesTableBodyEl = document.getElementById('boatTypesTableBody');
    const checkBoxes = boatTypesTableBodyEl.getElementsByTagName('input');
    for (let i = 0; i < checkBoxes.length; i++) {
        checkBoxes[i].disabled = true;
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

function buildMemberTableEntry(member,index) {
    const tableRowEl = document.createElement('tr');
    const tableHeaderEl = document.createElement('th');
    const tableDataEl1 = document.createElement('td');
    const tableDataEl2 = document.createElement('td');
    const checkBoxEl1 = document.createElement('input');
    const checkBoxEl2 = document.createElement('input');

    tableHeaderEl.setAttribute('scope', 'row');
    checkBoxEl1.setAttribute('type', 'checkbox');
    checkBoxEl1.classList.add('form-check-input', 'coxswainCheckBox');
    checkBoxEl1.setAttribute('id', 'check' + index);
    checkBoxEl1.addEventListener('change', checkCoxswainCheckBoxes);
    tableHeaderEl.appendChild(checkBoxEl1);
    checkBoxEl2.setAttribute('type', 'checkbox');
    checkBoxEl2.classList.add('form-check-input', 'crewCheckBox');
    checkBoxEl2.addEventListener('change', checkCrewCheckBoxes);
    tableDataEl1.appendChild(checkBoxEl2);
    tableDataEl2.textContent = member.id + ", " + member.name;
    tableRowEl.appendChild(tableHeaderEl);
    tableRowEl.appendChild(tableDataEl1);
    tableRowEl.appendChild(tableDataEl2);

    return tableRowEl;
}

function buildReservatorTableEntry(member, index) {
    const tableRowEl = document.createElement('tr');
    const tableHeaderEl = document.createElement('th');
    const tableDataEl = document.createElement('td');
    const checkBoxEl = document.createElement('input');

    tableHeaderEl.setAttribute('scope', 'row');
    checkBoxEl.setAttribute('type', 'radio');
    checkBoxEl.setAttribute('name', 'reservatorRadio')
    tableHeaderEl.appendChild(checkBoxEl);
    tableDataEl.textContent = member.id + ", " + member.name;
    tableRowEl.appendChild(tableHeaderEl);
    tableRowEl.appendChild(tableDataEl);

    return tableRowEl;
}

function checkCoxswainCheckBoxes() {
    const crewCheckBoxes = document.getElementById("memberTableBody").getElementsByClassName("crewCheckBox");
    const coxswainCheckBoxes = document.getElementById("memberTableBody").getElementsByClassName("coxswainCheckBox");
    for (let i = 0; i < coxswainCheckBoxes.length; i++) {
        if (coxswainCheckBoxes[i].id === this.id) {
            crewCheckBoxes[i].disabled = coxswainCheckBoxes[i].checked === true;
        } else if (coxswainCheckBoxes[i].checked === true) {
            coxswainCheckBoxes[i].checked = false;
            crewCheckBoxes[i].disabled = false;
        }
    }
}

function checkCrewCheckBoxes() {
    const coxswainCheckBoxes = document.getElementById("memberTableBody").getElementsByClassName("coxswainCheckBox");
    const crewCheckBoxes = document.getElementById("memberTableBody").getElementsByClassName("crewCheckBox");
    let index = Array.from(crewCheckBoxes).indexOf(this);
    if (this.checked === true) {
        let checkedCheckBoxes = 0;
        for (let i = 0; i < crewCheckBoxes.length; i++) {
            if (crewCheckBoxes[i].checked === true) {
                coxswainCheckBoxes[i].disabled = true;
                checkedCheckBoxes++;
            }
        }

        if (checkedCheckBoxes === maxMembersInCrew + 1) {
            this.checked = false;
            coxswainCheckBoxes[index].disabled = false;
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "No space in crew, you cannot select more members! "
            showModal(modal);
        }
    } else {
        coxswainCheckBoxes[index].disabled = false;
    }
}

function disableCoxswainCheckBoxes() {
    const coxswainCheckBoxes = document.getElementById("memberTableBody")
        .getElementsByClassName("coxswainCheckBox");
    for (let i = 0; i < coxswainCheckBoxes.length; i++) {
        coxswainCheckBoxes[i].disabled = true;
    }
}