let selectedDay;
let selectedActivity;
let activityList;
let selectedBoatTypes = [];
let selectedBoatCrew = [];
let memberList;
let selectedCoxswain;
let selectDayButtonEl;
let selectActivityButtonEl;
let selectBoatTypesButtonEl;
let selectBoatCrewButtonEl;
let maxMembersInCrew;
let modal;
let modalBody;
let modalTitle;
const STATUS_OK = 200;

window.addEventListener('load', () => {
    initializeDaysDropDownMenu();
    setupEventHandlers();
});

function setupEventHandlers() {
    selectDayButtonEl = document.getElementById('buttonSelectDay');
    selectDayButtonEl.addEventListener('click', handleDaySelection);
    selectActivityButtonEl = document.getElementById('buttonSelectActivity');
    selectActivityButtonEl.addEventListener('click', handleActivitySelection);
    selectBoatTypesButtonEl = document.getElementById('buttonSelectBoatTypes');
    selectBoatTypesButtonEl.addEventListener('click', handleBoatTypesSelection);
    selectBoatCrewButtonEl = document.getElementById('buttonSelectBoatCrew');
    selectBoatCrewButtonEl.addEventListener('click', handleBoatCrewSelection);
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() =>{
        hideModal(modal);
    });
}

async function handleDaySelection(event) {
    selectDayButtonEl.disabled = true;
    selectActivityButtonEl.disabled = false;
    const daysMenuEl =document.getElementById("daysDropDownMenu");
    daysMenuEl.disabled = true;
    selectedDay = document.getElementById('daysDropDownMenu').value;
    const data = {
        day: selectedDay
    }

    const response = await fetch('../../activities', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if (response.status === STATUS_OK) {
        const activityDropDownMenuEl = document.getElementById('activityDropDownMenu');
        activityList = await response.json();
        // store item in session storage?
        for (let i = 0; i < activityList.length; i++) {
            activityDropDownMenuEl.appendChild(createActivityOption(activityList[i], i));
        }
    } else {
        // TODO: no available activities
    }
}

function handleActivitySelection(event) {
    selectActivityButtonEl.disabled = true;
    selectBoatTypesButtonEl.disabled = false;
    const activitiesMenuEl = document.getElementById("activityDropDownMenu");
    activitiesMenuEl.disabled = true;
    selectedActivity = activityList[document.getElementById('activityDropDownMenu').value];
}

async function handleBoatTypesSelection(event) {
    selectBoatTypesButtonEl.disabled = true;
    selectBoatCrewButtonEl.disabled = false;
    const boatTypesTableBodyEl = document.querySelector(".boatTypesTableBody");
    const allCheckBoxes = boatTypesTableBodyEl.getElementsByTagName("input");
    for (let i = 0; i < allCheckBoxes.length; i++) {
        if (allCheckBoxes[i].checked === true) {
            selectedBoatTypes.push(allCheckBoxes[i].textContent);
        }
    }
    maxMembersInCrew = calculateMaxMemberInCrew(selectedBoatTypes);

    const data = {
        activity: selectedActivity,
        day: selectedDay
    }

    const response = await fetch('../../membersForReservation', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if (response.status === STATUS_OK) {
        const memberTableBodyEl = document.getElementById('memberTableBody');
        memberList = await response.json();
        for (let i = 0; i < memberList.length; i++) {
            memberTableBodyEl.appendChild(buildMemberTableEntry(memberList[i],i));
        }
    } else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "There is no available members for this activity "
        showModal(modal);
    }
}
function calculateMaxMemberInCrew(selectedBoatTypes){
    let maxMemberInCrew = 0;
    for(let i = 0;i < selectedBoatTypes.length;i++){
        let currentBoatType = selectedBoatTypes[i].charAt(0);
        if(currentBoatType > maxMemberInCrew){
            maxMemberInCrew = parseInt(currentBoatType);
        }
    }
    return maxMemberInCrew;
}

async function handleBoatCrewSelection(event) {

}

function createActivityOption(activity, index) {
    const activityOption = document.createElement('option');
    activityOption.value = index;
    activityOption.textContent = activity.name+ ", " +activity.time;

    return activityOption;
}

function buildMemberTableEntry(member,index) {
    const tableRowEl = document.createElement('tr');
    const tableHeaderEl = document.createElement('th');
    const tableDataEl1 = document.createElement('td');
    const tableDataEl2 = document.createElement('td');
    const checkBoxEl1 = document.createElement('input');
    const checkBoxEl2 = document.createElement('input');

    tableHeaderEl.setAttribute('scope', 'row');
    checkBoxEl1.setAttribute('type', 'checkbox');
    checkBoxEl1.classList.add('form-check-input', 'coxswainCheckBox');
    checkBoxEl1.setAttribute("id", "check" + index);
    checkBoxEl1.addEventListener('change', checkCoxswainCheckBoxes);
    tableHeaderEl.appendChild(checkBoxEl1);
    checkBoxEl2.setAttribute('type', 'checkbox');
    checkBoxEl2.classList.add('form-check-input', 'crewCheckBox');
    checkBoxEl2.addEventListener('change', checkCrewCheckBoxes);
    tableDataEl1.appendChild(checkBoxEl2);
    tableDataEl2.textContent = member.name + " " + member.email;
    tableRowEl.appendChild(tableHeaderEl);
    tableRowEl.appendChild(tableDataEl1);
    tableRowEl.appendChild(tableDataEl2);

    return tableRowEl;
}

function checkCoxswainCheckBoxes() {
    const coxswainCheckBoxes = document.getElementById("memberTableBody").
    getElementsByClassName("coxswainCheckBox");
    for (let i = 0; i < coxswainCheckBoxes.length; i++) {
        if(coxswainCheckBoxes[i].id != this.id && coxswainCheckBoxes[i].checked === true) {
            coxswainCheckBoxes[i].checked = false;
        }
    }
}

function checkCrewCheckBoxes() {
    const crewCheckBoxes = document.getElementById("memberTableBody").
    getElementsByClassName("crewCheckBox");
    let checkedCheckBoxes = 0;
    for(let i = 0; i < crewCheckBoxes.length; i++){
        if(crewCheckBoxes[i].checked = true){
            checkedCheckBoxes++;
        }
    }
    if(checkedCheckBoxes === maxMembersInCrew){
        this.checked = false;
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "No space in crew, you cannot select more members! "
        showModal(modal);

    }
}
function initializeDaysDropDownMenu() {
    const daysDropDownMenu = document.getElementById('daysDropDownMenu');
    const dropDownOptions = daysDropDownMenu.getElementsByTagName('option');
    const options = { weekday: 'long', year: 'numeric', month: 'numeric', day: 'numeric' };

    let day = new Date();
    let i;
    for (i=1; i<=7; i++) {
        day.setDate(day.getDate() + 1);
        dropDownOptions[i-1].textContent = day.toLocaleDateString(undefined, options);
    }
}

