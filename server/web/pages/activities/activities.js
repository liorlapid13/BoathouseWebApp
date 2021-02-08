let removeReservationButtonEl;
let editReservationButtonEl;
let modal;
let modalBody;
let modalTitle;

window.addEventListener('load', () => {
    initializeDaysDropDownMenu();
    initializeModal();
    setupEventHandlers();
});

function setupEventHandlers(){
    removeReservationButtonEl = document.getElementById('buttonRemoveReservation');
    removeReservationButtonEl.addEventListener('click', handleRemoveReservationRequest);
    editReservationButtonEl = document.getElementById('buttonEditReservation');
    editReservationButtonEl.addEventListener('click', handleEditReservationRequest);
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() =>{
        hideModal(modal);
    });
}

function initializeModal() {
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
}