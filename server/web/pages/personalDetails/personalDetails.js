window.addEventListener('load', () => {
    initializeFields();
    setupEventHandlers();
});

function setupEventHandlers() {
    const personalDetailsFormEl = document.getElementById('formPersonalDetails');
    personalDetailsFormEl.addEventListener('submit', handleFormSubmit);
    const passwordButton = document.querySelector('.passwordButton');
    passwordButton.addEventListener('click', handlePasswordButton);
}

function handlePasswordButton() {
    const passwordButton = document.querySelector('.passwordButton');
    const inputPassword = document.getElementById('inputPassword');
    if(inputPassword.type === "password") {
        inputPassword.type = 'text';
        passwordButton.textContent = 'Hide Password'
    }
    else {
        inputPassword.type = 'password';
        passwordButton.textContent = 'Show Password'
    }
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
        const response = await fetch('../../editPersonalDetails', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });
        const alertPopup = document.getElementById('emailAlertText');
        if (response.ok) {
            const alertText = document.getElementById('alertText');
            alertText.style.color = "green";
            alertText.textContent = "Personal details successfully changed!";
            alertPopup.textContent = '';
        } else {

            alertPopup.textContent = "Email already taken";
        }
    }
}

function areTextBoxesFilled(inputName, inputEmail, inputPassword, inputPhoneNumber) {
    const alertText = document.getElementById('alertText');
    let textBoxesFilled = true;

    if (inputName === '' || inputPhoneNumber === '' || inputEmail === '' || inputPassword === '') {
        alertText.style.color = "red";
        alertText.textContent = "You must fill in all fields!";
        textBoxesFilled = false;
    } else {
        alertText.textContent = '';
    }

    return textBoxesFilled;
}

async function initializeFields() {
    const response = await fetch('../../editPersonalDetails', {
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