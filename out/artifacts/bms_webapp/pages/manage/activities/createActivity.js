let finalModal;
let finalModalBody;
let finalModalTitle;


window.addEventListener('load', () => {
    initializeFinalModal();
    setupEventHandlers();
});

function setupEventHandlers(){
    const formCreateActivityEl = document.getElementById('formCreateActivity');
    formCreateActivityEl.addEventListener('submit',handleActivityCreation);
    const inputStartTime = document.getElementById("inputStartTime")
    inputStartTime.addEventListener("change", setMinEndTime);
    const finalModalRedirectButtonEl = document.getElementById('redirectButton');
    finalModalRedirectButtonEl.addEventListener('click', () => {
        hideModal(finalModal);
        window.location.href = "activities.html";
    });
}
function setMinEndTime(){
    const minEndTime = addOneMinute(this.value);
    document.getElementById("inputEndTime") .setAttribute("min",minEndTime);
}
async function handleActivityCreation(event){
    event.preventDefault();
    const inputName = document.getElementById('inputName').value;
    const inputStartTime = document.getElementById('inputStartTime').value;
    const inputEndTime = document.getElementById('inputEndTime').value;
    const inputBoatTypeRestriction = document.getElementById('boatTypesDropDownMenu').value;
    const time = inputStartTime.concat("-",inputEndTime);
    const data = {
        name: inputName,
        time: time,
        restriction: inputBoatTypeRestriction
    }

    const response = await fetch('../../createActivity', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });
    if(response.status === STATUS_OK){
        finalModalTitle.textContent = "";
        finalModalBody.style.color = "green";
        finalModalBody.textContent = "Activity added successfully"
        showModal(finalModal);
    }
    else{
        finalModalTitle.textContent = "Pay Attention!";
        finalModalBody.style.color = "red";
        finalModalBody.textContent = await response.text()
        showModal(finalModal);
    }
}
function initializeFinalModal() {
    finalModal = document.getElementById("finalModal");
    finalModalBody = document.getElementById("finalModalBody");
    finalModalTitle = document.getElementById("finalModalLabel");
}
