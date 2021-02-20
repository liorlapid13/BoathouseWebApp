let idInputEl;
let emailInputEl;
let passwordInputEl;
let nameInputEl;
let ageInputEl;
let phoneInputEl;
let memberLevelDropDownMenuEl;
let detailsTextEl;
let isManagerCheckBoxEl;
let hasPrivateBoatCheckBoxEl;
let boatIdInputEl;
let createMemberButtonEl;

let idLocked = false;
let emailLocked = false;
let boatIdLocked = false;

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
    idInputEl = document.getElementById('inputId');
    const checkIdButtonEl = document.getElementById('buttonCheckId');
    checkIdButtonEl.addEventListener('click', handleCheckId);
    emailInputEl = document.getElementById('inputEmail');
    const checkEmailButtonEl = document.getElementById('buttonCheckEmail');
    checkEmailButtonEl.addEventListener('click', handleCheckEmail);
    passwordInputEl = document.getElementById('inputPassword');
    nameInputEl = document.getElementById('inputName');
    ageInputEl = document.getElementById('inputAge');
    phoneInputEl = document.getElementById('inputPhoneNumber');
    memberLevelDropDownMenuEl = document.getElementById('memberLevelDropDownMenu');
    detailsTextEl = document.getElementById('textAreaDetails');
    isManagerCheckBoxEl = document.getElementById('checkBoxIsManager');
    hasPrivateBoatCheckBoxEl = document.getElementById('checkBoxHasPrivateBoat');
    hasPrivateBoatCheckBoxEl.addEventListener('change', handleChangeHasPrivateBoat);
    boatIdInputEl = document.getElementById('inputBoatId');
    const checkBoatIdButton = document.getElementById('buttonCheckBoatId');
    checkBoatIdButton.addEventListener('click', handleCheckBoatId)
    const createMemberFormEl = document.getElementById('formCreateMember');
    createMemberFormEl.addEventListener('submit', handleCreateMember);
    createMemberButtonEl = document.getElementById('buttonCreateMember');
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() => {
        hideModal(modal);
    });
    const finalModalRedirectButtonEl = document.getElementById('redirectButton');
    finalModalRedirectButtonEl.addEventListener('click', () => {
        hideModal(finalModal);
        window.location.href = "members.html";
    });
}

async function handleCheckId(event) {
    const checkIdButtonEl = document.getElementById('buttonCheckId');
    const idAvailabilityTextEl = document.getElementById('idAvailabilityText');

    if (idLocked) {
        createMemberButtonEl.disabled = true;
        checkIdButtonEl.textContent = "Check ID";
        idAvailabilityTextEl.textContent = "";
        idInputEl.disabled = false;
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
            email: null
        }

        const response = await fetch('../../memberAvailability', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });

        if (response.status === STATUS_OK) {
            idAvailabilityTextEl.textContent = "Available";
            idAvailabilityTextEl.style.color = "green";
            checkIdButtonEl.textContent = "Change";
            idInputEl.disabled = true;
            idLocked = true;
            checkAllMandatoryFields();
        } else {
            idAvailabilityTextEl.textContent = "Unavailable";
            idAvailabilityTextEl.style.color = "red";
        }
    }
}

async function handleCheckEmail(event) {
    const checkEmailButtonEl = document.getElementById('buttonCheckEmail');
    const emailAvailabilityTextEl = document.getElementById('emailAvailabilityText');

    if (emailLocked) {
        createMemberButtonEl.disabled = true;
        checkEmailButtonEl.textContent = "Check Email";
        emailAvailabilityTextEl.textContent = "";
        emailInputEl.disabled = false;
        emailLocked = false;
    } else {
        let email = emailInputEl.value;
        if (email.trim() === '') {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "You must enter an email!";
            showModal(modal);
            return;
        }

        const data = {
            id: null,
            email: email
        }

        const response = await fetch('../../memberAvailability', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });

        if (response.status === STATUS_OK) {
            emailAvailabilityTextEl.textContent = "Available";
            emailAvailabilityTextEl.style.color = "green";
            checkEmailButtonEl.textContent = "Change";
            emailInputEl.disabled = true;
            emailLocked = true;
            checkAllMandatoryFields();
        } else {
            emailAvailabilityTextEl.textContent = "Unavailable";
            emailAvailabilityTextEl.style.color = "red";
        }
    }
}

function handleChangeHasPrivateBoat(event) {
    const checkBoatIdButtonEl = document.getElementById('buttonCheckBoatId');
    const boatIdAvailabilityTextEl = document.getElementById('boatIdAvailabilityText');

    if (hasPrivateBoatCheckBoxEl.checked) {
        createMemberButtonEl.disabled = true;
        checkBoatIdButtonEl.disabled = false;
        boatIdInputEl.disabled = false;
        checkAllMandatoryFields();
    } else {
        boatIdAvailabilityTextEl.textContent = "";
        checkBoatIdButtonEl.textContent = "Check Boat ID";
        checkBoatIdButtonEl.disabled = true;
        boatIdInputEl.value = "";
        boatIdInputEl.disabled = true;
        boatIdLocked = false;
    }
}

async function handleCheckBoatId(event) {
    const checkBoatIdButtonEl = document.getElementById('buttonCheckBoatId');
    const boatIdAvailabilityTextEl = document.getElementById('boatIdAvailabilityText');

    if (boatIdLocked) {
        createMemberButtonEl.disabled = true;
        checkBoatIdButtonEl.textContent = "Check Boat ID";
        boatIdAvailabilityTextEl.textContent = "";
        boatIdInputEl.disabled = false;
        boatIdLocked = false;
    } else {
        let boatId = boatIdInputEl.value;
        if (boatId.trim() === '') {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "You must enter a boat ID!";
            showModal(modal);
            return;
        }

        const data = {
            boatId: boatId
        }

        const response = await fetch('../../privateBoatAvailability', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });

        if (response.status === STATUS_OK) {
            boatIdAvailabilityTextEl.textContent = "Boat found";
            boatIdAvailabilityTextEl.style.color = "green";
            checkBoatIdButtonEl.textContent = "Change";
            boatIdInputEl.disabled = true;
            boatIdLocked = true;
            checkAllMandatoryFields();
        } else {
            boatIdAvailabilityTextEl.textContent = "Boat not found";
            boatIdAvailabilityTextEl.style.color = "red";
        }
    }
}

async function handleCreateMember(event) {
    event.preventDefault();
    let boatId = hasPrivateBoatCheckBoxEl.checked ? boatIdInputEl.value : null;
    const data = {
        id: idInputEl.value,
        name: nameInputEl.value,
        age: ageInputEl.value,
        details: detailsTextEl.value,
        level: memberLevelDropDownMenuEl.value,
        hasBoat: hasPrivateBoatCheckBoxEl.checked,
        privateBoatId: boatId,
        phoneNumber: phoneInputEl.value,
        email: emailInputEl.value,
        password: passwordInputEl.value,
        isManager: isManagerCheckBoxEl.checked,
        expirationDate: null
    }

    const response = await fetch('../../createMember', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if (response.status === STATUS_OK) {
        finalModalTitle.textContent = "";
        finalModalBody.textContent = "Member successfully created";
        finalModalBody.style.color = "green";
    } else {
        finalModalTitle.textContent = "";
        finalModalBody.textContent = "Member has not been created";
        finalModalBody.style.color = "red";
    }
    showModal(finalModal);
}

function checkAllMandatoryFields() {
    if (idLocked && emailLocked && (!hasPrivateBoatCheckBoxEl.checked || boatIdLocked)) {
        createMemberButtonEl.disabled = false;
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