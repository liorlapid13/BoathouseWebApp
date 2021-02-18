let finalModal;
let finalModalBody;
let finalModalTitle;

let activityToEdit;

window.addEventListener('load', () => {
    initializeActivityData();
    initializeFinalModal();
    setupEventHandlers();
});

function setupEventHandlers() {
    const formCreateActivityEl = document.getElementById('formEditActivity');
    formCreateActivityEl.addEventListener('submit',handleEditActivity);
    const inputStartTime = document.getElementById("inputStartTime")
    inputStartTime.addEventListener("change", setMinEndTime);
    const modalCloseButtonEl = document.getElementById("closeButton");
    const finalModalRedirectButtonEl = document.getElementById('redirectButton');
    finalModalRedirectButtonEl.addEventListener('click', () => {
        hideModal(finalModal);
        window.location.href = "activities.html";
    });
}

function initializeActivityData() {
    const currentNameEl = document.getElementById('inputName');
    const currentStartTimeEl = document.getElementById('inputStartTime');
    const currentEndTimeEl = document.getElementById('inputEndTime');
    const currentBoatTypeRestrictionEl = document.getElementById('boatTypesDropDownMenu');

    activityToEdit = JSON.parse(sessionStorage.getItem(ACTIVITY_TO_EDIT));

    const activityTimeSplit = activityToEdit.time.split("-");
    currentNameEl.value = activityToEdit.name;
    currentStartTimeEl.value = activityTimeSplit[0];
    currentEndTimeEl.value = activityTimeSplit[1];
    for(var i, j = 0; i = currentBoatTypeRestrictionEl.options[j]; j++) {
        if(i.value == activityToEdit.restriction) {
            currentBoatTypeRestrictionEl.selectedIndex = j;
            break;
        }
    }
}
async function handleEditActivity(event){
    event.preventDefault();
    const inputName = document.getElementById('inputName').value;
    const inputStartTime = document.getElementById('inputStartTime').value;
    const inputEndTime = document.getElementById('inputEndTime').value;
    const inputBoatTypeRestriction = document.getElementById('boatTypesDropDownMenu').value;
    const time = inputStartTime.concat("-",inputEndTime);
    const data = {
        selectedActivity : activityToEdit,
        newName: inputName,
        newTime: time,
        newRestriction: inputBoatTypeRestriction
    }
    const response = await fetch('../../editActivity', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });
    if(response.status === STATUS_OK){
        finalModalTitle.textContent = "";
        finalModalBody.style.color = "green";
        finalModalBody.textContent = "Activity changed successfully"
        showModal(finalModal);
    }
    else{
        finalModalTitle.textContent = "Pay Attention!";
        finalModalBody.style.color = "red";
        finalModalBody.textContent = await response.text()
        showModal(finalModal);
    }

}
function setMinEndTime(){
    const minEndTime = addOneMinute(this.value);
    document.getElementById("inputEndTime") .setAttribute("min",minEndTime);
}
function initializeFinalModal() {
    finalModal = document.getElementById("finalModal");
    finalModalBody = document.getElementById("finalModalBody");
    finalModalTitle = document.getElementById("finalModalLabel");
}