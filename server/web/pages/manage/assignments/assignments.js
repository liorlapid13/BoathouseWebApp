let assignmentList;

let removeAssignmentButtonEl;

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
    const nextWeekAssignmentsButtonEl = document.getElementById('buttonNextWeekAssignments');
    nextWeekAssignmentsButtonEl.addEventListener('click', handleNextWeekAssignments);
    const specificDayAssignmentsButtonEl = document.getElementById('buttonSpecificDayAssignments');
    specificDayAssignmentsButtonEl.addEventListener('click', handleSpecificDayAssignments);
    removeAssignmentButtonEl = document.getElementById('buttonRemove');
    removeAssignmentButtonEl.addEventListener('click', handleRemoveAssignment);
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() => {
        hideModal(modal);
    });
}

function handleNextWeekAssignments(event) {
    const data = {
        requestType: "next",
        daysFromToday : null
    }

    getSelectedAssignments(data);
}

function handleSpecificDayAssignments(event) {
     const currentSelectedDay = document.getElementById('daysDropDownMenu').value;
    const data = {
        requestType: "day",
        daysFromToday : currentSelectedDay
    }

    getSelectedAssignments(data);
}

async function getSelectedAssignments(data) {
    const alertPopup = document.getElementById("alertText");
    alertPopup.style.background = "";
    alertPopup.textContent = "";
    const response = await fetch('../../../allAssignments', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    const tableBody = document.getElementById('tableBody');
    while (tableBody.children.length>1) {
        tableBody.removeChild(tableBody.lastChild);
    }

    if (response.status === STATUS_OK) {
        assignmentList = await response.json();
        sessionStorage.setItem(ALL_ASSIGNMENTS_LIST, JSON.stringify(assignmentList));
        for(let i = 0; i < assignmentList.length; i++) {
            tableBody.appendChild(buildTableEntry(assignmentList[i]));
        }
        removeAssignmentButtonEl.disabled = false;
    } else {
        noAssignmentsAlert();
    }
}

function noAssignmentsAlert() {
    const alertPopup = document.getElementById("alertText");
    alertPopup.style.background = "white";
    alertPopup.textContent = "No assignments to display";
    removeAssignmentButtonEl.disabled = true;
}

async function handleRemoveAssignment(event) {
    const tableBodyEl = document.getElementById('tableBody');
    let checkedCheckBox = findCheckedCheckBox(getAllCheckBoxes());
    if (checkedCheckBox !== -1) {
        const selectedAssignment = assignmentList[checkedCheckBox];
        const tableRows = tableBodyEl.getElementsByTagName("tr");
        const response = await fetch('../../../removeAssignment', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(selectedAssignment)
        });

        if (response.status === STATUS_OK) {
            assignmentList.splice(checkedCheckBox, 1);
            tableRows[checkedCheckBox + 1].remove();
            modalTitle.textContent = "";
            modalBody.style.color = "green";
            modalBody.textContent = "Assignment removed successfully"
        } else {
            modalTitle.textContent = "Pay Attention!";
            modalBody.style.color = "red";
            modalBody.textContent = "Error: Failed to remove Assignment"
        }
        showModal(modal);
    } else {
        modalTitle.textContent = "Pay Attention!";
        modalBody.textContent = "You must select an assignment to remove"
        showModal(modal);
    }
}

function buildTableEntry(assignment) {
    const tableEntryEl = document.createElement("tr");

    const tableHeaderEl = document.createElement("th");
    const checkBoxEl = document.createElement("input");
    const reservatorEl = document.createElement("td");
    const dateEl = document.createElement("td");
    const activityEl = document.createElement("td");
    const boatTypesEl = document.createElement("td");
    const boatCrewEl = document.createElement("td");

    const boatIdEl = document.createElement("td");
    const boatNameEl = document.createElement("td");
    const boatTypeEl = document.createElement("td");

    checkBoxEl.setAttribute('type', 'radio');
    checkBoxEl.setAttribute('name', 'assignmentRadio');
    tableHeaderEl.setAttribute("scope", "row");

    reservatorEl.textContent = assignment.reservation.reservator.name;
    dateEl.textContent = assignment.reservation.date;
    activityEl.textContent = assignment.reservation.activity.name + "\n" + assignment.reservation.activity.time;
    boatTypesEl.textContent = parseBoatTypes(assignment.reservation.boatTypes);
    boatCrewEl.textContent = parseBoatCrew(
        assignment.reservation.boatCrew, assignment.reservation.coxswain, assignment.reservation.coxswainSelected);

    boatIdEl.textContent = assignment.boat.id;
    boatNameEl.textContent = assignment.boat.name;
    boatTypeEl.textContent = assignment.boat.boatType;

    tableHeaderEl.appendChild(checkBoxEl);
    tableEntryEl.appendChild(tableHeaderEl);
    tableEntryEl.appendChild(reservatorEl);
    tableEntryEl.appendChild(dateEl);
    tableEntryEl.appendChild(activityEl);
    tableEntryEl.appendChild(boatTypesEl);
    tableEntryEl.appendChild(boatCrewEl);
    tableEntryEl.appendChild(boatIdEl);
    tableEntryEl.appendChild(boatNameEl);
    tableEntryEl.appendChild(boatTypeEl);

    return tableEntryEl;
}

function getAllCheckBoxes() {
    const reservationTableBodyEl = document.getElementById('tableBody');
    return reservationTableBodyEl.getElementsByTagName('input');
}

function initializeModal() {
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
}