const STATUS_OK = 200;

function initializeDaysDropDownMenu() {
    const daysDropDownMenu = document.getElementById('daysDropDownMenu');
    const dropDownOptions = daysDropDownMenu.getElementsByTagName('option');
    const options = { weekday: 'long', year: 'numeric', month: 'numeric', day: 'numeric' };
    let day = new Date();
    for (let i = 1; i <= 7; i++) {
        day.setDate(day.getDate() + 1);
        dropDownOptions[i-1].textContent = day.toLocaleDateString(undefined, options);
    }
}

function disableCheckBoxes(checkBoxes) {
    for (let i = 0; i < checkBoxes.length; i++) {
        checkBoxes[i].disable = true;
    }
}

function enableCheckBoxes(checkBoxes) {
    for (let i = 0; i < checkBoxes.length; i++) {
        checkBoxes[i].disable = false;
    }
}