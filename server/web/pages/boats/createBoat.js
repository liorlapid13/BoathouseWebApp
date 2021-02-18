let idLocked = false;
let nameLocked = false;

let modal;
let modalBody;
let modalTitle;
let finalModal;
let finalModalBody;
let finalModalTitle;

window.addEventListener('load', () => {
    initializeModals();
    setupEventHandlers();
});

function setupEventHandlers() {
    const formCreateBoatEl = document.getElementById('formCreateBoat');
    formCreateBoatEl.addEventListener('submit', handleCreateBoat);
    const checkIdButtonEl = document.getElementById('buttonCheckId');
    checkIdButtonEl.addEventListener('click', handleCheckId);
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

async function handleCheckId(event) {
    const idInputEl = document.getElementById('inputId');
    const checkIdButtonEl = document.getElementById('buttonCheckId');
    const idAvailabilityEl = document.getElementById('idAvailabilityText');

    if (idLocked) {
        if (nameLocked) {
            const createBoatButtonEl = document.getElementById('buttonCreateBoat');
            createBoatButtonEl.disabled = true;
        }
        idInputEl.disabled = false;
        checkIdButtonEl.textContent = "Check ID";
        idAvailabilityEl.textContent = "";
        idLocked = false;
    } else {
        let id = idInputEl.value;
        if (id.trim() === '') {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "You must enter an ID!";
            showModal(modal);
            return;
        }
        const data = {
            id: id,
            name: null
        }

        const response = await fetch('../../boatAvailability', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });

        if (response.status === STATUS_OK) {
            idAvailabilityEl.textContent = "Available";
            idAvailabilityEl.style.color = "green";
            idInputEl.disabled = true;
            checkIdButtonEl.textContent = "Change";
            idLocked = true;
            checkAllFormFields();
        } else {
            idAvailabilityEl.textContent = "Unavailable";
            idAvailabilityEl.style.color = "red";
        }
    }
}

async function handleCheckName(event) {
    const nameInputEl = document.getElementById('inputName');
    const checkNameButtonEl = document.getElementById('buttonCheckName');
    const nameAvailabilityEl = document.getElementById('nameAvailabilityText');

    if (nameLocked) {
        if (idLocked) {
            const createBoatButtonEl = document.getElementById('buttonCreateBoat');
            createBoatButtonEl.disabled = true;
        }
        nameInputEl.disabled = false;
        checkNameButtonEl.textContent = "Check Name";
        nameAvailabilityEl.textContent = "";
        nameLocked = false;
    } else {
        let name = nameInputEl.value;
        if (name.trim() === '') {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "You must enter a name!";
            showModal(modal);
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
            nameLocked = true;
            checkAllFormFields();
        } else {
            nameAvailabilityEl.textContent = "Unavailable";
            nameAvailabilityEl.style.color = "red";
        }
    }
}

function checkAllFormFields() {
    if (nameLocked && idLocked) {
        const createBoatButtonEl = document.getElementById('buttonCreateBoat');
        createBoatButtonEl.disabled = false;
    }
}

async function handleCreateBoat(event) {
    event.preventDefault();
    const idInput = document.getElementById('inputId').value;
    const nameInput = document.getElementById('inputName').value;
    const boatTypeInput = document.getElementById('boatTypesDropDownMenu').value;
    const isWide = document.getElementById('checkBoxWide').checked;
    const isPrivate = document.getElementById('checkBoxPrivate').checked;
    const isCoastal = document.getElementById('checkBoxCoastal').checked;
    const data = {
        id: idInput,
        name: nameInput,
        boatType: boatTypeInput,
        isWide: isWide,
        isPrivate: isPrivate,
        isCoastal: isCoastal,
        isDisabled: false
    }

    const response = await fetch('../../createBoat', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if (response.status === STATUS_OK) {
        finalModalTitle.textContent = "";
        finalModalBody.textContent = "Boat successfully created";
        finalModalBody.style.color = "green";
    } else {
        finalModalTitle.textContent = "";
        finalModalBody.textContent = "Boat has not been created";
        finalModalBody.style.color = "red";
    }
    showModal(finalModal);
}

function initializeModals() {
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
    finalModal = document.getElementById("finalModal");
    finalModalBody = document.getElementById("finalModalBody");
    finalModalTitle = document.getElementById("finalModalLabel");
}