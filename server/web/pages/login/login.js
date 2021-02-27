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
    if (areTextBoxesFilled()) {
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
        if (response.status === STATUS_OK) {
            let memberId = await response.text();
            sessionStorage.setItem('id', JSON.stringify(memberId));
            window.location.href = "pages/home/homePage.html";
        } else {
            const alertPopup = document.getElementById('alertText');
            alertPopup.textContent = await response.text();
        }
    }
}

function areTextBoxesFilled() {
    const userEmailInput = document.getElementById('userEmail');
    const emailAlert = document.getElementById('emailHelp');
    const userPasswordInput = document.getElementById('userPassword');
    const passwordAlert = document.getElementById('passwordHelp');
    let textBoxesFilled = true;

    if (userEmailInput.value === '' || userEmailInput.value === 'email@example.com') {
        emailAlert.textContent = "Email missing!";
        textBoxesFilled = false;
    } else if (userEmailInput.value !== '') {
        emailAlert.textContent = '';
    }

    if (userPasswordInput.value === '') {
        passwordAlert.textContent = "Password missing!";
        textBoxesFilled = false;
    } else if (userPasswordInput.value !== '') {
        passwordAlert.textContent = '';
    }

    return textBoxesFilled;
}

async function checkIfLoggedIn() {
    const response = await fetch('login', {
        method: 'get'
    });

    if (response.status === STATUS_OK) {
        window.location.href = await response.text();
    }
}