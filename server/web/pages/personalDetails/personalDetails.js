window.addEventListener('load', () => {
    initializeFields();
    setupEventHandlers();
});

function setupEventHandlers() {
    const personalDetailsFormEl = document.getElementById('formPersonalDetails');
    personalDetailsFormEl.addEventListener('submit', handleFormSubmit);
}

async function handleFormSubmit (event) {
    event.preventDefault();
    const inputName = document.getElementById('inputName').value;
    const inputEmail = document.getElementById('inputEmail').value;
    const inputPassword = document.getElementById('inputPassword').value;
    const inputPhoneNumber = document.getElementById('inputPhoneNumber').value;
    const data = {
        name: inputName,
        email: inputEmail,
        password: inputPassword,
        phoneNumber: inputPhoneNumber
    }

    if (areTextBoxesFilled(inputName, inputEmail, inputPassword, inputPhoneNumber)) {
        const response = await fetch('editPersonalDetails', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });

        if (response.ok) {
            window.location.href = await response.text();
        } else {
            const alertPopup = document.getElementById('emailAlertText');
            alertPopup.textContent = "Email already taken";
        }
    }
}

function areTextBoxesFilled(inputName, inputEmail, inputPassword, inputPhoneNumber) {
    const missingFieldsAlert = document.getElementById('missingFieldsAlert');
    let textBoxesFilled = true;

    if (inputName === '' || inputPhoneNumber === '' || inputEmail === '' || inputPassword === '') {
        missingFieldsAlert.textContent = "You must fill in all fields!";
        textBoxesFilled = false;
    }

    return textBoxesFilled;
}

async function initializeFields() {
    const response = await fetch('editPersonalDetails', {
        method: 'get',
    });

    const inputName = document.getElementById('inputName');
    const inputEmail = document.getElementById('inputEmail');
    const inputPassword = document.getElementById('inputPassword');
    const inputPhoneNumber = document.getElementById('inputPhoneNumber');
    const memberDetails = await response.json();
    inputEmail.value = memberDetails.email;
    inputPassword.value = memberDetails.password;
    inputName.value = memberDetails.name;
    inputPhoneNumber.value = memberDetails.phoneNumber;
}