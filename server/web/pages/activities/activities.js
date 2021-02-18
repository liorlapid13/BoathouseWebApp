let removeActivityButtonEl;
let editActivityButtonEl;
let modal;
let modalBody;
let modalTitle;
let activityList;

window.addEventListener('load', () => {
    initializeModal();
    setupEventHandlers();
    initializeActivityTable();

});

function setupEventHandlers(){
    removeActivityButtonEl = document.getElementById('buttonRemoveActivity');
    removeActivityButtonEl.addEventListener('click', handleRemoveActivityRequest);
    editActivityButtonEl = document.getElementById('buttonEditActivity');
    editActivityButtonEl.addEventListener('click', handleEditActivityRequest);
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() =>{
        hideModal(modal);
    });
}

function initializeModal() {
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
}

async function initializeActivityTable() {
    const alertPopup = document.getElementById("alertText");
    alertPopup.style.background = "";
    alertPopup.textContent = "";

    const response = await fetch('../../activities', {
        method: 'get',
    });

    if (response.status === STATUS_OK) {
        const activityTableBody = document.getElementById('activityTableBody');
        activityList = await response.json();
        for (let i = 0; i < activityList.length; i++) {
            activityTableBody.appendChild(createActivityTableEntry(activityList[i]));
        }
    } else {
        alertPopup.style.background ="white";
        alertPopup.textContent = "No Activities to display";
        removeActivityButtonEl.disabled = true;
        editActivityButtonEl.disabled = true;
    }
}

function createActivityTableEntry(activity) {
    const tableEntryEl = document.createElement("tr");
    const tableHeaderEl = document.createElement("th");
    const checkBoxEl = document.createElement("input");
    checkBoxEl.setAttribute('type', 'radio');
    checkBoxEl.setAttribute('name', 'activityRadio');
    tableHeaderEl.setAttribute("scope", "row");
    tableHeaderEl.appendChild(checkBoxEl);
    tableEntryEl.appendChild(tableHeaderEl);
    appendActivityTableData(tableEntryEl, activity);

    return tableEntryEl;
}

function appendActivityTableData(tableEntryEl, activity) {
    const activityNameEl = document.createElement("td");
    const activityTimeEl = document.createElement("td");
    const activityRestrictionEl = document.createElement("td");

    activityNameEl.textContent = activity.name;
    activityTimeEl.textContent = activity.time;
    activityRestrictionEl.textContent = activity.restriction;

    tableEntryEl.appendChild(activityNameEl);
    tableEntryEl.appendChild(activityTimeEl);
    tableEntryEl.appendChild(activityRestrictionEl);
}
function handleEditActivityRequest(){
    const tableBodyEl = document.getElementById("activityTableBody");
    let checkedCheckBox = findCheckedCheckBox(getAllCheckBoxes());
    if (checkedCheckBox !== -1) {
        const activityToEdit = activityList[checkedCheckBox];
        sessionStorage.setItem(ACTIVITY_TO_EDIT, JSON.stringify(activityToEdit));
        window.location.href = "editActivity.html";
    } else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select a activity to edit"
        showModal(modal);
    }
}

function getAllCheckBoxes() {
    const tableBodyEl = document.querySelector("#activityTableBody");
    return tableBodyEl.getElementsByTagName("input");
}
async function handleRemoveActivityRequest(){
    const activitytableBodyEl = document.getElementById("activityTableBody");
    let checkedCheckBox = findCheckedCheckBox(getAllCheckBoxes());
    if (checkedCheckBox !== -1) {
        const activityToRemove = activityList[checkedCheckBox];

        const response = await fetch('../../removeActivity', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(activityToRemove)
        });
        if (response.status === STATUS_OK) {
            modalTitle.textContent = "";
            modalBody.style.color = "green";
            modalBody.textContent = "Activity remove successfuly"
            showModal(modal);
            while (activitytableBodyEl.firstChild) {
                activitytableBodyEl.removeChild(activitytableBodyEl.firstChild);
            }
            initializeActivityTable();
        }
    }
    else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select activity to remove"
        showModal(modal);
    }

}
function getAllCheckBoxes() {
    const tableBodyEl = document.getElementById("activityTableBody");
    return tableBodyEl.getElementsByTagName("input");
}