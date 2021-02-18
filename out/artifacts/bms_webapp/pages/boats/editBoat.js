let boatToEdit;

let nameChanged = false;
let boatTypeChanged = false;
let doesNewBoatTypeNeedCoxswain;
let isCoastalChanged = false;
let isPrivateChanged = false;
let isDisabledChanged = false;

let modal;
let modalBody;
let modalTitle;
let finalModal;
let finalModalBody;
let finalModalTitle;

window.addEventListener('load', () => {
    initializeBoatData();
    initializeModals();
    setupEventHandlers();
});

function setupEventHandlers() {
    const nameInputEl = document.getElementById('inputName');
    nameInputEl.addEventListener('change', handleNameChanged);
    const checkNameButtonEl = document.getElementById('buttonCheckName');
    checkNameButtonEl.addEventListener('click', handleCheckName);


    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() => {
        hideModal(modal);
    });
    const finalModalRedirectButtonEl = document.getElementById('redirectButton');
    finalModalRedirectButtonEl.addEventListener('click', () => {
        hideModal(finalModal);
        window.location.href = "boats.html";
    });
}

function handleNameChanged(event) {
    const checkNameButtonEl = document.getElementById('buttonCheckName');
    checkNameButtonEl.disabled = false;
    const nameAvailabilityEl = document.getElementById('nameAvailabilityText');
    nameAvailabilityEl.textContent = "";
}

async function handleCheckName(event) {

    const nameAvailabilityEl = document.getElementById('nameAvailabilityText');
    const checkNameButtonEl = document.getElementById('buttonCheckName');
    const confirmButtonEl = document.getElementById('buttonEditBoat');
    const nameInputEl = document.getElementById('inputName');
    let name = nameInputEl.value;

    if (nameChanged) {
        confirmButtonEl.disabled = true;
        nameInputEl.disabled = false;
        checkNameButtonEl.textContent = "Check Name";
        nameAvailabilityEl.textContent = "";
        nameChanged = false;
    } else {
        if (name.trim() === '') {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "You must enter a name!";
            showModal(modal);
            return;
        }

        if (name.trim() === boatToEdit.name) {
            checkNameButtonEl.disabled = true;
            nameAvailabilityEl.textContent = "This is the original boat name";
            nameAvailabilityEl.style.color = "black";
            return;
        }

        const data = {
            id: null,
            name: name
        }

        const response = await fetch('../../boatAvailability', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });

        if (response.status === STATUS_OK) {
            nameAvailabilityEl.textContent = "Available";
            nameAvailabilityEl.style.color = "green";
            nameInputEl.disabled = true;
            checkNameButtonEl.textContent = "Change";
            nameChanged = true;
            confirmButtonEl.disabled = false;
        } else {
            nameAvailabilityEl.textContent = "Unavailable";
            nameAvailabilityEl.style.color = "red";
        }
    }
}

function initializeBoatData() {
    const nameInputEl = document.getElementById('inputName');
    const boatTypesDropDownMenuEl = document.getElementById('boatTypesDropDownMenu');
    const isCoastalCheckBoxEl = document.getElementById('checkBoxCoastal');
    const isPrivateCheckBoxEl = document.getElementById('checkBoxPrivate');
    const isDisabledCheckBoxEl = document.getElementById('checkBoxDisabled');

    boatToEdit = JSON.parse(sessionStorage.getItem(BOAT_TO_EDIT));
    initializeBoatTypes(boatTypesDropDownMenuEl);
    nameInputEl.value = boatToEdit.name;
    isCoastalCheckBoxEl.disabled = boatToEdit.isCoastal;
    isPrivateCheckBoxEl.disabled = boatToEdit.isPrivate;
    isDisabledCheckBoxEl.disabled = boatToEdit.isDisabled;
}

function initializeBoatTypes(boatTypesDropDownMenu) {
    switch (boatToEdit.boatType) {
        case "1X":
            boatTypesDropDownMenu.appendChild(buildBoatTypeOption("1X"));
            boatTypesDropDownMenu.disabled = true;
            break;
        case "2-":
        case "2+":
        case "2X":
        case "2X+":
            boatTypesDropDownMenu.appendChild(buildBoatTypeOption("2X+"));
            boatTypesDropDownMenu.appendChild(buildBoatTypeOption("2X"));
            boatTypesDropDownMenu.appendChild(buildBoatTypeOption("2+"));
            boatTypesDropDownMenu.appendChild(buildBoatTypeOption("2-"));
            break;
        case "4-":
        case "4+":
        case "4X":
        case "4X+":
            boatTypesDropDownMenu.appendChild(buildBoatTypeOption("4X+"));
            boatTypesDropDownMenu.appendChild(buildBoatTypeOption("4X"));
            boatTypesDropDownMenu.appendChild(buildBoatTypeOption("4+"));
            boatTypesDropDownMenu.appendChild(buildBoatTypeOption("4-"));
            break;
        case "8+":
        case "8X+":
            boatTypesDropDownMenu.appendChild(buildBoatTypeOption("8X+"));
            boatTypesDropDownMenu.appendChild(buildBoatTypeOption("8+"));
            break;
    }

    for (let i = 0; i < boatTypesDropDownMenu.length; i++) {
        if (boatTypesDropDownMenu.options[i].value === boatToEdit.boatType) {
            boatTypesDropDownMenu.selectedIndex = i;
            break;
        }
    }
}

function buildBoatTypeOption(boatType) {
    const boatTypeOptionEl = document.createElement('option');
    boatTypeOptionEl.value = boatTypeOptionEl.textContent = boatType;

    return boatTypeOptionEl;
}

function initializeModals() {
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
    finalModal = document.getElementById("finalModal");
    finalModalBody = document.getElementById("finalModalBody");
    finalModalTitle = document.getElementById("finalModalLabel");
}