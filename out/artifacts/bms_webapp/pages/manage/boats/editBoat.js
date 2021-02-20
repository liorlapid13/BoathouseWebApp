let boatToEdit;

let nameInputEl;
let boatTypesDropDownMenuEl;
let checkNameButtonEl;
let coastalCheckBoxEl;
let privateCheckBoxEl;
let disabledCheckBoxEl;
let confirmButtonEl;

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
    nameInputEl = document.getElementById('inputName');
    nameInputEl.addEventListener('change', handleNameChanged);
    boatTypesDropDownMenuEl = document.getElementById('boatTypesDropDownMenu');
    boatTypesDropDownMenuEl.addEventListener('change', handleBoatTypeChanged);
    checkNameButtonEl = document.getElementById('buttonCheckName');
    checkNameButtonEl.addEventListener('click', handleCheckName);
    coastalCheckBoxEl = document.getElementById('checkBoxCoastal');
    coastalCheckBoxEl.addEventListener('change', handleChangeCoastal);
    privateCheckBoxEl = document.getElementById('checkBoxPrivate');
    privateCheckBoxEl.addEventListener('change', handleChangePrivate);
    disabledCheckBoxEl = document.getElementById('checkBoxDisabled');
    disabledCheckBoxEl.addEventListener('change', handleChangeDisabled);
    const formEditBoatEl = document.getElementById('formEditBoat');
    formEditBoatEl.addEventListener('submit', handleConfirm);
    confirmButtonEl = document.getElementById('buttonEditBoat');
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
    let name = nameInputEl.value;

    if (nameChanged) {
        confirmButtonEl.disabled = true;
        nameInputEl.disabled = false;
        checkNameButtonEl.textContent = "Check Name";
        nameAvailabilityEl.textContent = "";
        nameChanged = false;
    } else {
        if (name.trim() === '') {
            nameChanged = false;
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

function handleBoatTypeChanged(event) {
    let selectedBoatType = boatTypesDropDownMenuEl.value;
    if (selectedBoatType !== boatToEdit.boatType) {
        doesNewBoatTypeNeedCoxswain = doesBoatTypeNeedCoxswain(selectedBoatType);
        boatTypeChanged = true;
        confirmButtonEl.disabled = false;
    } else {
        boatTypeChanged = false;
        confirmButtonEl.disabled = true;
    }
}

function handleChangeCoastal(event) {
    if (boatToEdit.isCoastal !== coastalCheckBoxEl.checked) {
        isCoastalChanged = true;
        confirmButtonEl.disabled = false;
    } else {
        isCoastalChanged = false;
        confirmButtonEl.disabled = true;
    }
}

function handleChangePrivate(event) {
    if (boatToEdit.isPrivate !== privateCheckBoxEl.checked) {
        isPrivateChanged = true;
        confirmButtonEl.disabled = false;
    } else {
        isPrivateChanged = false;
        confirmButtonEl.disabled = true;
    }
}

function handleChangeDisabled(event) {
    if (boatToEdit.isDisabled !== disabledCheckBoxEl.checked) {
        isDisabledChanged = true;
        confirmButtonEl.disabled = false;
    } else {
        isDisabledChanged = false;
        confirmButtonEl.disabled = true;
    }
}

async function handleConfirm(event) {
    event.preventDefault();
    if (boatTypeChanged && (doesBoatTypeNeedCoxswain(boatToEdit.boatType) !== doesNewBoatTypeNeedCoxswain)) {
        if (!window.confirm("Coxswain status has changed, any future assignments will be removed, do you want to continue?")) {
            return;
        }
    }

    if (!boatToEdit.isDisabled && isDisabledChanged) {
        if (!window.confirm("Boat will become disabled, any future assignments will be removed, do you want to continue?")) {
            return;
        }
    }

    let id = boatToEdit.id;
    let name = nameChanged ? nameInputEl.value : boatToEdit.name;
    let boatType = boatTypeChanged ? boatTypesDropDownMenuEl.value : boatToEdit.boatType;
    let isWide = boatToEdit.isWide;
    let isPrivate = privateCheckBoxEl.checked;
    let isCoastal = coastalCheckBoxEl.checked;
    let isDisabled = disabledCheckBoxEl.checked;

    const data = {
        id: id,
        name: name,
        boatType: boatType,
        isWide: isWide,
        isPrivate: isPrivate,
        isCoastal: isCoastal,
        isDisabled: isDisabled
    }

    const response = await fetch('../../editBoat', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if (response.status === STATUS_OK) {
        finalModalTitle.textContent = "";
        finalModalBody.textContent = "Boat edited successfully";
    } else {
        finalModalTitle.textContent = "";
        finalModalBody.textContent = "Boat has not been changed";
    }
    showModal(finalModal);
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
    isCoastalCheckBoxEl.checked = boatToEdit.isCoastal;
    isPrivateCheckBoxEl.checked = boatToEdit.isPrivate;
    isDisabledCheckBoxEl.checked = boatToEdit.isDisabled;
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