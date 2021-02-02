window.addEventListener('load', () => {
    checkIfLoggedIn();
    setupEventHandlers();
});

function setupEventHandlers() {
    const loginFormEl = document.getElementById('formLogin');
    loginFormEl.addEventListener('submit', handleFormSubmit);
}

async function handleFormSubmit (event) {
    event.preventDefault();
    if (TextBoxesAreFull()) {
        const userEmail = document.getElementById('userEmail').value;
        const userPassword = document.getElementById('userPassword').value;
        const data = {
            email: userEmail,
            password: userPassword
        }

        const response = await fetch('login', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });

        if (response.ok) {
            window.location.href = await response.text();
        } else {
            const alertPopup = document.getElementById('alertText');
            alertPopup.textContent = await response.text();
        }
    }
}

function TextBoxesAreFull() {
    const userEmailInput = document.getElementById('userEmail');
    const emailAlert = document.getElementById('emailHelp');
    const userPasswordInput = document.getElementById('userPassword');
    const passwordAlert = document.getElementById('passwordHelp');
    var textBoxesFull = true;

    if (userEmailInput.value === '' || userEmailInput.value === 'email@example.com') {
        emailAlert.textContent = "Email missing!";
        textBoxesFull = false;
    } else if (userEmailInput.value !== '') {
        emailAlert.textContent = '';
    }

    if (userPasswordInput.value === '') {
        passwordAlert.textContent = "Password missing!";
        textBoxesFull = false;
    } else if(userPasswordInput.value !== '') {
        passwordAlert.textContent = '';
    }

    return textBoxesFull;
}

async function checkIfLoggedIn() {
    const response = await fetch('login', {
        method: 'get'
    });

    if (response.ok) {
        window.location.href = await response.text();
    }
}