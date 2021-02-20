let memberToEdit;

let nameInputEl;
let ageInputEl;
let phoneInputEl;
let emailInputEl;
let passwordInputEl;
let memberLevelDropDownMenuEl;
let expirationDateInputEl;
let detailsTextAreaEl;
let isManagerCheckBoxEl;
let hasPrivateBoatCheckBoxEl;
let boatIdInputEl;
let editMemberButtonEl;

let modal;
let modalBody;
let modalTitle;
let finalModal;
let finalModalBody;
let finalModalTitle;

window.addEventListener('load', () => {
    initializeMemberData();
    initializeModals();
    setupEventHandlers();
});

function setupEventHandlers() {
    const editMemberFormEl = document.getElementById('formEditMember');
    editMemberFormEl.addEventListener('submit', handleEditMember);
    const checkEmailButtonEl = document.getElementById('buttonCheckEmail');
    checkEmailButtonEl.addEventListener('click', handleCheckEmail);
    hasPrivateBoatCheckBoxEl.addEventListener('change', handleChangeHasPrivateBoat);
    const checkBoatIdButtonEl = document.getElementById('buttonCheckBoatId');
    checkBoatIdButtonEl.addEventListener('click', handleCheckBoatId);

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

async function handleCheckEmail(event) {
    const checkEmailButtonEl = document.getElementById('buttonCheckEmail');
    const emailAvailabilityTextEl = document.getElementById('emailAvailabilityText');

    if (emailInputEl.disabled) {
        checkEmailButtonEl.textContent = "Check Email";
        emailAvailabilityTextEl.textContent = "";
        emailInputEl.disabled = false;
    } else {
        let email = emailInputEl.value;
        if (email.trim() === '') {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "You must enter an email!";
            showModal(modal);
            return;
        }

        if (email.trim() === memberToEdit.email) {
            checkEmailButtonEl.textContent = "Change";
            emailInputEl.disabled = true;
            emailAvailabilityTextEl.textContent = "This is the original email";
            emailAvailabilityTextEl.style.color = "black";
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
        checkBoatIdButtonEl.disabled = false;
        boatIdInputEl.disabled = false;
    } else {
        boatIdAvailabilityTextEl.textContent = "";
        checkBoatIdButtonEl.textContent = "Check Boat ID";
        checkBoatIdButtonEl.disabled = true;
        boatIdInputEl.value = "";
        boatIdInputEl.disabled = true;
    }
}

async function handleCheckBoatId(event) {
    const checkBoatIdButtonEl = document.getElementById('buttonCheckBoatId');
    const boatIdAvailabilityTextEl = document.getElementById('boatIdAvailabilityText');

    if (boatIdInputEl.disabled) {
        checkBoatIdButtonEl.textContent = "Check Boat ID";
        boatIdAvailabilityTextEl.textContent = "";
        boatIdInputEl.disabled = false;
    } else {
        let boatId = boatIdInputEl.value;
        if (boatId.trim() === '') {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "You must enter a boat ID!";
            showModal(modal);
            return;
        }

        if (boatId.trim() === memberToEdit.privateBoatId) {
            checkBoatIdButtonEl.textContent = "Change";
            boatIdInputEl.disabled = true;
            boatIdAvailabilityTextEl.textContent = "This is the original boat ID";
            boatIdAvailabilityTextEl.style.color = "black";
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
        } else {
            boatIdAvailabilityTextEl.textContent = "Boat not found";
            boatIdAvailabilityTextEl.style.color = "red";
        }
    }
}

async function handleEditMember(event) {
    event.preventDefault();
    if (!emailInputEl.disabled || !boatIdInputEl.disabled) {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "Email and/or Boat ID fields are missing!";
        showModal(modal);
        return
    }

    let boatId = hasPrivateBoatCheckBoxEl.checked ? boatIdInputEl.value : null;
    const data = {
        id: memberToEdit.id,
        name: nameInputEl.value,
        age: ageInputEl.value,
        details: detailsTextAreaEl.value,
        level: memberLevelDropDownMenuEl.value,
        hasBoat: hasPrivateBoatCheckBoxEl.checked,
        privateBoatId: boatId,
        phoneNumber: phoneInputEl.value,
        email: emailInputEl.value,
        password: passwordInputEl.value,
        isManager: isManagerCheckBoxEl.checked,
        expirationDate: expirationDateInputEl.value
    }

    const response = await fetch('../../editMember', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if (response.status === STATUS_OK) {
        finalModalTitle.textContent = "";
        finalModalBody.textContent = "Member successfully edited";
        finalModalBody.style.color = "green";
    } else {
        finalModalTitle.textContent = "";
        finalModalBody.textContent = "Member has not been changed";
        finalModalBody.style.color = "red";
    }
    showModal(finalModal);
}

function initializeMemberData() {
    nameInputEl = document.getElementById('inputName');
    ageInputEl = document.getElementById('inputAge');
    phoneInputEl = document.getElementById('inputPhoneNumber');
    emailInputEl = document.getElementById('inputEmail');
    passwordInputEl = document.getElementById('inputPassword');
    memberLevelDropDownMenuEl = document.getElementById('memberLevelDropDownMenu');
    expirationDateInputEl = document.getElementById('inputExpirationDate');
    detailsTextAreaEl = document.getElementById('textAreaDetails');
    isManagerCheckBoxEl = document.getElementById('checkBoxIsManager');
    hasPrivateBoatCheckBoxEl = document.getElementById('checkBoxHasPrivateBoat');
    boatIdInputEl = document.getElementById('inputBoatId');

    memberToEdit = JSON.parse(sessionStorage.getItem(MEMBER_TO_EDIT));
    nameInputEl.value = memberToEdit.name;
    ageInputEl.value = memberToEdit.age;
    phoneInputEl.value = memberToEdit.phoneNumber;
    emailInputEl.value = memberToEdit.email;
    passwordInputEl.value = memberToEdit.password;
    initializeMemberLevel(memberLevelDropDownMenuEl);
    expirationDateInputEl.value = memberToEdit.expirationDate.split("T")[0];
    expirationDateInputEl.setAttribute('min', new Date().toISOString().split("T")[0]);
    detailsTextAreaEl.value = memberToEdit.details;
    isManagerCheckBoxEl.checked = memberToEdit.isManager;
    hasPrivateBoatCheckBoxEl.checked = memberToEdit.hasBoat;
    if (memberToEdit.privateBoatId !== undefined) {
        boatIdInputEl.value = memberToEdit.privateBoatId;
    }
}

function initializeMemberLevel(memberLevelDropDownMenuEl) {
    switch (memberToEdit.level) {
        case "Beginner":
            memberLevelDropDownMenuEl.selectedIndex = 0;
            break;
        case "Intermediate":
            memberLevelDropDownMenuEl.selectedIndex = 1;
            break;
        case "Advanced":
            memberLevelDropDownMenuEl.selectedIndex = 2;
            break;
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