window.addEventListener('load', () => {
    initializeDaysDropDownMenu();
    setupEventHandlers();
});

function setupEventHandlers() {
    const nextWeekReservationsButtonEl = document.getElementById('buttonNextWeekReservations');
    nextWeekReservationsButtonEl.addEventListener('click', handleNextWeekReservations);
    const pastWeekReservationsButtonEl = document.getElementById('buttonPastWeekReservations');
    pastWeekReservationsButtonEl.addEventListener('click', handlePastReservations);
    const specificDayReservationsButtonEl = document.getElementById('buttonSpecificDayReservations');
    specificDayReservationsButtonEl.addEventListener('click', handleSpecificDayReservations);
}

async function handlePastReservations(event) {

}

async function handleNextWeekReservations(event) {

}

async function handleSpecificDayReservations(event) {

}

async function initializeDaysDropDownMenu() {
    const daysDropDownMenu = document.getElementById('daysDropDownMenu');
    const dropDownOptions = daysDropDownMenu.childNodes;
    const options = { weekday: 'long', year: 'numeric', month: 'numeric', day: 'numeric' };

    for (let i=1; i<=7; i++) {
        let event = new Date(Date.now() + i);
        dropDownOptions[i].textContent = event.toLocaleDateString(undefined, options);
    }
}