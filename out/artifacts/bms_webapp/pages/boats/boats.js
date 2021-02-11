let removeBoatButtonEl;
let editBoatButtonEl;
let modal;
let modalBody;
let modalTitle;
let boatList;

window.addEventListener('load', () => {
    initializeModal();
    setupEventHandlers();
    initializeBoatsTable();

});

function setupEventHandlers(){
    removeBoatButtonEl = document.getElementById('buttonRemoveBoat');
    removeBoatButtonEl.addEventListener('click', handleRemoveBoatRequest);
    editBoatButtonEl = document.getElementById('buttonEditBoat');
    editBoatButtonEl.addEventListener('click', handleEditBoatRequest);
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

async function initializeBoatsTable(){
    const alertPopup = document.getElementById("alertText");
    alertPopup.style.background = "";
    alertPopup.textContent = "";
    const response = await fetch('../../boats', {
        method: 'get',
    });
    if (response.status === STATUS_OK) {
        const boatTableBody = document.getElementById('boatsTableBody');
        boatList = await response.json();
        for (let i = 0; i < boatList.length; i++) {
            boatTableBody.appendChild(createBoatsTableEntry(boatList[i]));
        }
    } else {
        alertPopup.style.background ="white";
        alertPopup.textContent = "No Boats to display";
        removeMemberButtonEl.disabled = true;
        editMemberButtonEl.disabled = true;
    }
}

function createBoatsTableEntry(boat) {
    const tableEntryEl = document.createElement("tr");
    const tableHeaderEl = document.createElement("th");
    const checkBoxEl = document.createElement("input");
    checkBoxEl.setAttribute('type', 'radio');
    checkBoxEl.setAttribute('name', 'boatRadio')
    tableHeaderEl.setAttribute("scope", "row");
    tableHeaderEl.appendChild(checkBoxEl);
    tableEntryEl.appendChild(tableHeaderEl);
    appendBoatTableData(tableEntryEl, boat);

    return tableEntryEl;
}

function appendBoatTableData(tableEntryEl, boat) {
    const boatIDEl = document.createElement("td");
    const boatNameEl = document.createElement("td");
    const boatTypeEl = document.createElement("td");
    const boatWideEl = document.createElement("td");
    const boatPrivateEl = document.createElement("td");
    const boatCoastalEl = document.createElement("td");
    const boatDisabledEl = document.createElement("td");


    boatIDEl.textContent = boat.id;
    boatNameEl.textContent = boat.name;
    boatTypeEl.textContent = boat.boatType;
    addIconDependentOnCondition(boatWideEl,boat.isWide);
    addIconDependentOnCondition(boatPrivateEl,boat.isPrivate);
    addIconDependentOnCondition(boatCoastalEl,boat.isCoastal);
    addIconDependentOnCondition(boatDisabledEl,boat.isDisabled);

    tableEntryEl.appendChild(boatIDEl);
    tableEntryEl.appendChild(boatNameEl);
    tableEntryEl.appendChild(boatTypeEl);
    tableEntryEl.appendChild(boatWideEl);
    tableEntryEl.appendChild(boatPrivateEl);
    tableEntryEl.appendChild(boatCoastalEl);
    tableEntryEl.appendChild(boatDisabledEl);
}
async function handleRemoveBoatRequest(){
    const membertableBodyEl = document.getElementById("boatsTableBody");
    let checkedCheckBox = findCheckedCheckBox(getAllCheckBoxes());
    if (checkedCheckBox !== -1) {
        const boatToRemove = boatList[checkedCheckBox];

        const checkFutureAssignmentsResponse = await fetch('../../boats', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(boatToRemove)
        });

        let doesBoatHasFutureAssignments = false;
        if(checkFutureAssignmentsResponse.status !== STATUS_OK){
            doesBoatHasFutureAssignments = true;
            if (!window.confirm("This boat has an future assignment,if you perform this change," +
                " this boat's assignments will be removed are you sure you want to remove him?")) {
                return;
            }
        }

        const data = {
            boatToRemove:boatToRemove,
            boatHasFutureAssignment:doesBoatHasFutureAssignments
        }

        const removalResponse = await fetch('../../removeBoat', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });
        if (removalResponse.status === STATUS_OK) {
            modalTitle.textContent = "";
            modalBody.textContent = "Boat remove successfuly"
            showModal(modal);
            while (membertableBodyEl.firstChild) {
                membertableBodyEl.removeChild(membertableBodyEl.firstChild);
            }
            initializeBoatsTable();
        }
    }
    else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select boat to remove"
        showModal(modal);
    }
}

function getAllCheckBoxes() {
    const tableBodyEl = document.getElementById("boatsTableBody");
    return tableBodyEl.getElementsByTagName("input");
}
function handleEditBoatRequest(){

}