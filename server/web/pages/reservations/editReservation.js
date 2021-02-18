let editDateButtonEl;
let confirmDateButtonEl;
let editActivityButtonEl;
let confirmActivityButtonEl;
let editBoatTypesButtonEl;
let confirmBoatTypesButtonEl;
let editBoatCrewButtonEl;
let confirmBoatCrewButtonEl;
let editReservatorButtonEl;
let confirmReservatorButtonEl;
let cancelButtonEl;
let confirmButtonEl;

let reservationToEdit;
let selectedDate;
let selectedActivity;
let selectedBoatTypes = [];
let maxMembersInCrew;
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
let reservatorEdited = false;
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
    editReservatorButtonEl = document.getElementById('buttonEditReservator');
    editReservatorButtonEl.addEventListener('click', handleEditReservator);
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
        if (selectedDate !== reservationToEdit.date) {
            let isBoatCrewAvailable = await checkIfBoatCrewIsAvailable(
                selectedDate, reservationToEdit.activity, reservationToEdit.boatCrew, reservationToEdit.coxswain, reservationToEdit.coxswainSelected);
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
        selectedDate = reservationToEdit.date;
    }

    editDateButtonEl.disabled = true;
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
        selectedActivity = reservationToEdit.activity;
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
        const activityDropDownMenuEl = document.getElementById('activityDropDownMenu');
        selectedActivity = activityList[activityDropDownMenuEl.value];
        if (selectedActivity !== reservationToEdit.activity) {
            let isBoatCrewAvailable = await checkIfBoatCrewIsAvailable(
                selectedDate, selectedActivity, reservationToEdit.boatCrew, reservationToEdit.coxswain, reservationToEdit.coxswainSelected);
            if (!isBoatCrewAvailable) {
                modalTitle.textContent = "Pay Attention!" ;
                modalBody.textContent = "One or more of the crew members are not available on this day, please try again!";
                showModal(modal);
                return;
            }
        }

        activityDropDownMenuEl.disabled = true;
    } else {
        selectedActivity = reservationToEdit.activity;
    }

    editActivityButtonEl.disabled = true;
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
            maxMembersInCrew = calculateMaxBoatTypesCapacity(selectedBoatTypes);
            let boatCrewSize = reservationToEdit.boatCrew.length;
            doSelectedBoatTypesNeedCoxswain = doBoatTypesNeedCoxswain(selectedBoatTypes);
            if (maxMembersInCrew < boatCrewSize || (!doSelectedBoatTypesNeedCoxswain && reservationToEdit.coxswainSelected)) {
                forceBoatCrewReselection = true;
            }
        }
    } else {
        editBoatTypesButtonEl.disabled = true;
        selectedBoatTypes = reservationToEdit.boatTypes;
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
    //confirmBoatCrewButtonEl.disabled = false;

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

    const boatCrewTableBodyEl = document.getElementById('boatCrewTableBody');
    if (!dateEdited && !activityEdited) {
        let i;
        for (i = 0; i < reservationToEdit.boatCrew.length; i++) {
            boatCrewTableBodyEl.appendChild(buildMemberTableEntry(reservationToEdit.boatCrew[i], i));
            manuallyInsertedMembers++;
        }

        if (reservationToEdit.coxswainSelected) {
            boatCrewTableBodyEl.appendChild(buildMemberTableEntry(reservationToEdit.coxswain, i));
            manuallyInsertedMembers++;
        }
    }

    if (response2.status === STATUS_OK) {
        memberList = await response2.json();
        for (let i = manuallyInsertedMembers; i < manuallyInsertedMembers + memberList.length; i++) {
            boatCrewTableBodyEl.appendChild(buildMemberTableEntry(memberList[i - manuallyInsertedMembers], i));
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
                selectedBoatCrew = reservationToEdit.boatCrew;
                selectedCoxswain = reservationToEdit.coxswain;
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
        const boatCrewTableBodyEl = document.getElementById('boatCrewTableBody');
        const crewCheckBoxes = boatCrewTableBodyEl.getElementsByClassName("crewCheckBox");
        let membersSelected = 0;
        for (let i = 0; i < crewCheckBoxes.length; i++) {
            if (crewCheckBoxes[i].checked === true) {
                membersSelected++;
                if (i >= manuallyInsertedMembers) {
                    selectedBoatCrew.push(memberList[i - manuallyInsertedMembers]);
                } else if (i === manuallyInsertedMembers - 1 && reservationToEdit.coxswain !== undefined) {
                    selectedBoatCrew.push(reservationToEdit.coxswain);
                } else {
                    selectedBoatCrew.push(reservationToEdit.boatCrew[i]);
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
            editReservatorButtonEl.disabled = false;
            confirmBoatCrewButtonEl.disabled = true;
            disableCrewCheckBoxes();
        }

        if (doSelectedBoatTypesNeedCoxswain) {
            disableCoxswainCheckBoxes();
            let coxswainSelected = false;
            const coxswainCheckBoxes = boatCrewTableBodyEl.getElementsByClassName("coxswainCheckBox");
            for (let i = 0; i < coxswainCheckBoxes.length; i++) {
                if (coxswainCheckBoxes[i].checked === true) {
                    if (i >= manuallyInsertedMembers) {
                        selectedCoxswain = memberList[i - manuallyInsertedMembers];
                    } else if (i === manuallyInsertedMembers - 1 && reservationToEdit.coxswain !== undefined) {
                        selectedCoxswain = reservationToEdit.coxswain;
                    } else {
                        selectedCoxswain = reservationToEdit.boatCrew[i];
                    }

                    coxswainSelected = true;
                    break;
                }
            }

            if (!coxswainSelected) {
                selectedCoxswain = null;
            }
        } else {
            selectedCoxswain = null;
        }

        if (!isMemberInBoatCrew(reservationToEdit.reservator, selectedBoatCrew, selectedCoxswain)) {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "Please select a new reservator!"
            showModal(modal);
            handleEditReservator();
        } else {
            editReservatorButtonEl.disabled = false;
        }

        confirmReservatorButtonEl.disabled = false;
    } else {
        editBoatCrewButtonEl.disabled = true;
        selectedBoatCrew = reservationToEdit.boatCrew;
        selectedCoxswain = reservationToEdit.coxswain;
        selectedReservator = reservationToEdit.reservator;
        cancelButtonEl.disabled = false;
        confirmButtonEl.disabled = false;
    }

    confirmBoatCrewButtonEl.disabled = true;
}

function handleConfirmReservator(event) {
    if (reservatorEdited) {
        const checkBoxes = document.getElementById('reservatorTableBody').getElementsByTagName("input");
        let reservatorSelectionFound = false;
        for (let i = 0; i < checkBoxes.length; i++) {
            if (checkBoxes[i].checked === true) {
                selectedReservator = (i === checkBoxes.length - 1 && selectedCoxswain !== null) ? selectedCoxswain : selectedBoatCrew[i];
                reservatorSelectionFound = true;
                break;
            }
        }

        if (!reservatorSelectionFound) {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "You must select a reservator!"
            showModal(modal);
            return;
        }

        disableReservatorCheckBoxes();
    } else {
        selectedReservator = reservationToEdit.reservator;
    }

    confirmReservatorButtonEl.disabled = true;
    confirmButtonEl.disabled = false;
    cancelButtonEl.disabled = false;
}

function handleEditReservator(event) {
    reservatorEdited = true;
    confirmReservatorButtonEl.textContent = CONFIRM;
    editReservatorButtonEl.disabled = true;
    const reservatorTableBodyEl = document.getElementById('reservatorTableBody');
    let i;
    for (i = 0; i < selectedBoatCrew.length; i++) {
        reservatorTableBodyEl.appendChild(buildReservatorTableEntry(selectedBoatCrew[i], i));
    }

    if (selectedCoxswain != null) {
        reservatorTableBodyEl.appendChild(buildReservatorTableEntry(selectedCoxswain, i));
    }
}

function handleCancel(event) {
    finalModalTitle.textContent = "";
    finalModalBody.textContent = "Reservation has not been changed";
    showModal(finalModal);
}

async function handleConfirm(event) {
    if (!dateEdited && !activityEdited && !boatTypesEdited && !boatCrewEdited) {
        finalModalTitle.textContent = "";
        finalModalBody.textContent = "Reservation has not been changed";
        showModal(finalModal);
    }

    const data = {
        reservation: reservationToEdit,
        date: selectedDate,
        activity: selectedActivity,
        isDummyActivity: isDummyActivity,
        boatTypes: selectedBoatTypes,
        boatCrew: selectedBoatCrew,
        coxswain: selectedCoxswain,
        reservator: selectedReservator
    }

    const response = await fetch('../../editReservation', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    finalModalTitle.textContent = "";
    finalModalBody.textContent = "Reservation successfully changed";
    showModal(finalModal);
}

async function checkIfBoatCrewIsAvailable(date, activity, boatCrew, coxswain, coxswainSelected) {
    const data = {
        date: date,
        activity: activity,
        boatCrew: boatCrew,
        coxswain: coxswain,
        coxswainSelected: coxswainSelected
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
    reservationToEdit = JSON.parse(sessionStorage.getItem(RESERVATION_TO_EDIT));

    currentDateEl.textContent = reservationToEdit.date;
    currentActivityEl.textContent = reservationToEdit.activity.name + "\n" + reservationToEdit.activity.time + "\n" +
        reservationToEdit.activity.restriction;
    currentBoatTypesEl.textContent = parseBoatTypes(reservationToEdit.boatTypes);
    currentBoatCrewEl.textContent = parseBoatCrew(reservationToEdit.boatCrew, reservationToEdit.coxswain, reservationToEdit.coxswainSelected);
    currentReservatorEl.textContent = reservationToEdit.reservator.name;
    maxMembersInCrew = calculateMaxBoatTypesCapacity(reservationToEdit.boatTypes);
    doSelectedBoatTypesNeedCoxswain = doBoatTypesNeedCoxswain(reservationToEdit.boatTypes);
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

function buildMemberTableEntry(member, index) {
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
    const crewCheckBoxes = document.getElementById("boatCrewTableBody").getElementsByClassName("crewCheckBox");
    const coxswainCheckBoxes = document.getElementById("boatCrewTableBody").getElementsByClassName("coxswainCheckBox");
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
    const coxswainCheckBoxes = document.getElementById("boatCrewTableBody").getElementsByClassName("coxswainCheckBox");
    const crewCheckBoxes = document.getElementById("boatCrewTableBody").getElementsByClassName("crewCheckBox");
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
    } else if (doSelectedBoatTypesNeedCoxswain) {
        coxswainCheckBoxes[index].disabled = false;
    }
}

function disableCoxswainCheckBoxes() {
    const coxswainCheckBoxes = document.getElementById("boatCrewTableBody")
        .getElementsByClassName("coxswainCheckBox");
    for (let i = 0; i < coxswainCheckBoxes.length; i++) {
        coxswainCheckBoxes[i].disabled = true;
    }
}

function disableCrewCheckBoxes() {
    const crewCheckBoxes = document.getElementById('boatCrewTableBody')
        .getElementsByClassName("crewCheckBox");
    for (let i = 0; i < crewCheckBoxes.length; i++) {
        crewCheckBoxes[i].disabled = true;
    }
}

function disableReservatorCheckBoxes() {
    const reservatorCheckBoxes = document.getElementById('reservatorTableBody')
        .getElementsByTagName('input');
    for (let i = 0; i < reservatorCheckBoxes.length; i++) {
        reservatorCheckBoxes[i].disabled = true;
    }
}