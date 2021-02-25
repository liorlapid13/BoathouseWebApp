let isManager;

let modal;
let modalBody;
let modalTitle;

window.addEventListener('load', () => {
    initializeModal();
    setupEventHandlers();
    initializeNotificationBoard().then(r => initializeAddButton());
});

function setupEventHandlers() {
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() =>{
        hideModal(modal);
    });
}

async function handleAddNotification(event) {
    const textInputEl = document.getElementById('inputText');
    let input = textInputEl.value.trim();
    if (input === "") {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "Notification message is empty!";
        showModal(modal);
    } else {
        const response = await fetch('../../addNotification', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(input)
        });

        if (response.status === STATUS_OK) {
            clearNotificationTable();
            initializeNotificationBoard();
            textInputEl.value = "";
        } else {
            modalTitle.textContent = "Pay Attention!" ;
            modalBody.textContent = "Error: Failed to add notification!";
            showModal(modal);
        }
    }
}

async function handleRemoveBoardNotification(event) {
    let selectedIndex = this.id;
    const response = await fetch('../../removeNotification', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(selectedIndex)
    }).then(r => {
        clearNotificationTable();
        initializeNotificationBoard();
    });
}

async function initializeNotificationBoard() {
    const response1 = await fetch('../../notificationBoard', {
        method: 'get'
    });
    const notifications = await response1.json();

    const response2 = await fetch('../../memberType', {
        method: 'get'
    })
    const memberType = await response2.text();
    isManager = memberType === "Manager";

    sessionStorage.setItem('notificationBoard', JSON.stringify(notifications));
    if (notifications.length === 0) {
        noNotificationsMessage();
    } else {
        buildNotificationTable(notifications, memberType);
    }
}

function initializeAddButton() {
    if (!isManager) {
        const notificationBoardEl = document.getElementById('notificationBoard');
        notificationBoardEl.removeChild(notificationBoardEl.children[notificationBoardEl.children.length - 1]);
    } else {
        const addNotificationButtonEl = document.getElementById('buttonAddNotification');
        addNotificationButtonEl.addEventListener('click', handleAddNotification);
    }
}

function buildNotificationTable(notifications) {
    const notificationsBody = document.getElementById('notificationsBody');
    for (let i = 0; i < notifications.length; i++) {
        notificationsBody.appendChild(buildNotificationEntry(notifications[i], i));
    }
}

function clearNotificationTable() {
    const notificationsBody = document.getElementById('notificationsBody');
    while (notificationsBody.firstChild) {
        notificationsBody.removeChild(notificationsBody.firstChild);
    }
}

function buildNotificationEntry(notification, index) {
    const liEl = document.createElement('li');
    const rowEl = document.createElement('div');
    const notificationColEl = document.createElement('div');
    const creatorEl = document.createElement('strong');
    const messageEl = document.createElement('div');
    const dateTimeEl = document.createElement('small');

    liEl.classList.add('notification-box', 'bottom-border');
    rowEl.classList.add('row');
    notificationColEl.classList.add('col-lg-9', 'col-sm-9', 'col-9');
    creatorEl.classList.add('text-info');
    creatorEl.textContent = notification.creator.name;
    messageEl.textContent = notification.message;
    dateTimeEl.classList.add('text-info');
    dateTimeEl.textContent = notification.dateTime;

    notificationColEl.appendChild(creatorEl);
    notificationColEl.appendChild(messageEl);
    notificationColEl.appendChild(dateTimeEl);
    rowEl.appendChild(notificationColEl);
    liEl.appendChild(rowEl);

    if (isManager) {
        const trashIconColEl = document.createElement('div');
        const trashButtonEl = document.createElement('button');
        const trashIconEl = document.createElement('i');

        trashIconColEl.classList.add('col', 'align-self-center', 'offset-1');
        trashIconColEl.setAttribute('align', 'center');
        trashButtonEl.classList.add('btn', 'btn-trash');
        trashButtonEl.setAttribute('id', index);
        trashButtonEl.addEventListener('click', handleRemoveBoardNotification);
        trashIconEl.classList.add('fa', 'fa-trash');

        trashButtonEl.appendChild(trashIconEl);
        trashIconColEl.appendChild(trashButtonEl);
        rowEl.appendChild(trashIconColEl);
    }

    return liEl;
}

function noNotificationsMessage() {
    const notificationsBody = document.getElementById('notificationsBody');
    const liEl = document.createElement('li');
    const rowEl = document.createElement('div');
    const colEl = document.createElement('div');
    const messageEl = document.createElement('strong');

    liEl.classList.add('notification-box');
    rowEl.classList.add('row');
    colEl.classList.add('col-lg-9', 'col-sm-9', 'col-9')
    messageEl.classList.add('text-info');
    messageEl.textContent = "No Notifications to Display";

    colEl.appendChild(messageEl);
    rowEl.appendChild(colEl);
    liEl.appendChild(rowEl);
    notificationsBody.appendChild(liEl);
}

function initializeModal() {
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
}