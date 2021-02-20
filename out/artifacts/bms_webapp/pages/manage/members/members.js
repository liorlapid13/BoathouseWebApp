let removeMemberButtonEl;
let editMemberButtonEl;
let modal;
let modalBody;
let modalTitle;
let memberList;

window.addEventListener('load', () => {
    initializeModal();
    setupEventHandlers();
    initializeMembersTable();

});

function setupEventHandlers(){
    removeMemberButtonEl = document.getElementById('buttonRemoveMember');
    removeMemberButtonEl.addEventListener('click', handleRemoveMemberRequest);
    editMemberButtonEl = document.getElementById('buttonEditMember');
    editMemberButtonEl.addEventListener('click', handleEditMemberRequest);
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

async function initializeMembersTable(){
    const alertPopup = document.getElementById("alertText");
    alertPopup.style.background = "";
    alertPopup.textContent = "";
    const response = await fetch('../../members', {
        method: 'get',
    });
    if (response.status === STATUS_OK) {
        const memberTableBody = document.getElementById('membersTableBody');
        memberList = await response.json();
        for (let i = 0; i < memberList.length; i++) {
            memberTableBody.appendChild(createMembersTableEntry(memberList[i]));
        }
    } else {
        alertPopup.style.background ="white";
        alertPopup.textContent = "No Members to display";
        removeMemberButtonEl.disabled = true;
        editMemberButtonEl.disabled = true;
    }
}

function createMembersTableEntry(member) {
    const tableEntryEl = document.createElement("tr");
    const tableHeaderEl = document.createElement("th");
    const checkBoxEl = document.createElement("input");
    checkBoxEl.setAttribute('type', 'radio');
    checkBoxEl.setAttribute('name', 'memberRadio')
    tableHeaderEl.setAttribute("scope", "row");
    tableHeaderEl.appendChild(checkBoxEl);
    tableEntryEl.appendChild(tableHeaderEl);
    appendMemberTableData(tableEntryEl, member);

    return tableEntryEl;
}

function appendMemberTableData(tableEntryEl, member) {
    const memberIDEl = document.createElement("td");
    const memberNameEl = document.createElement("td");
    const memberEmailEl = document.createElement("td");
    const memberPhoneEl = document.createElement("td");
    const memberAgeEl = document.createElement("td");
    const memberLevelEl = document.createElement("td");
    const memberHasPrivateBoatEl = document.createElement("td");
    const memberTypeEl = document.createElement("td");

    memberIDEl.textContent = member.id;
    memberNameEl.textContent = member.name;
    memberEmailEl.textContent = member.email;
    memberPhoneEl.textContent = member.phoneNumber;
    memberAgeEl.textContent = member.age;
    memberLevelEl.textContent = member.level
    if(member.hasBoat){
        memberHasPrivateBoatEl.appendChild(createGreenCheckIcon())
    }
    else{
        memberHasPrivateBoatEl.appendChild(createRedXIcon());
    }
    memberTypeEl.textContent = member.isManager ? "Manager" : "Member";

    tableEntryEl.appendChild(memberIDEl);
    tableEntryEl.appendChild(memberNameEl);
    tableEntryEl.appendChild(memberEmailEl);
    tableEntryEl.appendChild(memberPhoneEl);
    tableEntryEl.appendChild(memberAgeEl);
    tableEntryEl.appendChild(memberLevelEl);
    tableEntryEl.appendChild(memberHasPrivateBoatEl);
    tableEntryEl.appendChild(memberTypeEl);

}
async function handleRemoveMemberRequest(){
    const membertableBodyEl = document.getElementById("membersTableBody");
    let checkedCheckBox = findCheckedCheckBox(getAllCheckBoxes());
    if (checkedCheckBox !== -1) {
        const memberToRemove = memberList[checkedCheckBox];

        const checkFutureReservationResponse = await fetch('../../members', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(memberToRemove)
        });

        let doesMemberHasFutureReservation = false;
        if(checkFutureReservationResponse.status !== STATUS_OK){
            doesMemberHasFutureReservation = true;
            if (!window.confirm("This member is part of future reservations, are you sure you want to remove him?")) {
                return;
            }
        }

        const data = {
            memberToRemove:memberToRemove,
            memberHasFutureReservation:doesMemberHasFutureReservation
        }

        const removalResponse = await fetch('../../removeMember', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(data)
        });
        if (removalResponse.status === STATUS_OK) {
            modalTitle.textContent = "";
            modalBody.style.color = "green";
            modalBody.textContent = "Member removed successfully"
            showModal(modal);
            while (membertableBodyEl.firstChild) {
                membertableBodyEl.removeChild(membertableBodyEl.firstChild);
            }
            initializeMembersTable();
        }
        else{
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = await removalResponse.text();
            showModal(modal);
        }
    }
    else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select member to remove"
        showModal(modal);
    }

}

function getAllCheckBoxes() {
    const tableBodyEl = document.getElementById("membersTableBody");
    return tableBodyEl.getElementsByTagName("input");
}
function handleEditMemberRequest() {
    let checkedCheckBox = findCheckedCheckBox(getAllCheckBoxes());
    if (checkedCheckBox !== -1) {
        const memberToEdit = memberList[checkedCheckBox];
        sessionStorage.setItem(MEMBER_TO_EDIT, JSON.stringify(memberToEdit));
        window.location.href = "editMember.html";
    } else {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select a member to edit"
        showModal(modal);
    }
}