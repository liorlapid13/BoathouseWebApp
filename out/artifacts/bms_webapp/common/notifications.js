const UNSEEN_NOTIFICATIONS = "unseenNotifications";
const NOTIFICATIONS_LIST = "notificationsList";
const refreshRate = 15 * 1000;

let memberId;

window.addEventListener('load', () => {
    initializeNotificationsList();
    setupNotificationsEventHandlers();
    setInterval(updateNotifications, refreshRate)
});

async function initializeNotificationsList() {
    memberId = JSON.parse(sessionStorage.getItem('id'));
    const notificationsList = JSON.parse(sessionStorage.getItem(NOTIFICATIONS_LIST + memberId));
    if (notificationsList != null) {
        if (notificationsList.length !== 0) {
            clearNotificationsList();
            buildNotificationsList(notificationsList);
        }
        const unseenNotifications = JSON.parse(sessionStorage.getItem(UNSEEN_NOTIFICATIONS + memberId));
        setNumberOfUnseenNotifications(unseenNotifications);
    } else {
        const emptyList = [];

        sessionStorage.setItem(NOTIFICATIONS_LIST + memberId, JSON.stringify(emptyList));
        sessionStorage.setItem(UNSEEN_NOTIFICATIONS + memberId, JSON.stringify(0));
    }
}

function setupNotificationsEventHandlers() {
    const notificationsButtonEl = document.getElementById('buttonNotifications');
    notificationsButtonEl.addEventListener('mousedown', markAllNotificationsAsRead);
}

function handleRemoveNotification(event) {
    const notificationsList = JSON.parse(sessionStorage.getItem(NOTIFICATIONS_LIST + memberId));
    let selectedNotification = this.id;
    notificationsList.splice(selectedNotification, 1);
    clearNotificationsList();
    if(notificationsList.length === 0){
        noNotificationsMessageEntry()
    }
    else{
        buildNotificationsList(notificationsList);
    }
    sessionStorage.setItem(NOTIFICATIONS_LIST + memberId, JSON.stringify(notificationsList));
}

async function updateNotifications() {
    const notificationsList = JSON.parse(sessionStorage.getItem(NOTIFICATIONS_LIST + memberId));
    const notificationsUrl = window.location.pathname.includes('manage') ?
        '../../../notifications' : '../../notifications';
    const response = await fetch(notificationsUrl, {
        method: 'get'
    });
    const newNotifications = await response.json();
    if (newNotifications.length !== 0) {
        let unseenNotifications = JSON.parse(sessionStorage.getItem(UNSEEN_NOTIFICATIONS + memberId));
        unseenNotifications += newNotifications.length;
        setNumberOfUnseenNotifications(unseenNotifications);
        sessionStorage.setItem(UNSEEN_NOTIFICATIONS + memberId, JSON.stringify(unseenNotifications));
        for (let i = 0; i < newNotifications.length; i++) {
            notificationsList.unshift(newNotifications[newNotifications.length - i - 1]);
        }
        clearNotificationsList();
        buildNotificationsList(notificationsList);
        sessionStorage.setItem(NOTIFICATIONS_LIST + memberId, JSON.stringify(notificationsList));
    }
}

function clearNotificationsList() {
    const notificationsListBodyEl = document.getElementById('notifications');
    while (notificationsListBodyEl.firstChild) {
        notificationsListBodyEl.removeChild(notificationsListBodyEl.firstChild);
    }
}

function buildNotificationsList(notifications) {
    const notificationsListBodyEl = document.getElementById('notifications');
    for (let i = 0; i < notifications.length; i++) {
        notificationsListBodyEl.appendChild(buildNotificationListEntry(notifications[i], i));
    }
}

function buildNotificationListEntry(notification, index) {
    const liEl = document.createElement('li');
    const rowEl = document.createElement('div');
    const notificationColEl = document.createElement('div');
    const creatorEl = document.createElement('strong');
    const messageEl = document.createElement('div');
    const dateTimeEl = document.createElement('small');
    const trashIconColEl = document.createElement('div');
    const trashButtonEl = document.createElement('button');
    const trashIconEl = document.createElement('i');

    liEl.classList.add('notification-box', 'bottom-border');
    if (index % 2 !== 0) {
        liEl.classList.add('bg-gray');
    }
    rowEl.classList.add('row');
    notificationColEl.classList.add('col-lg-9', 'col-sm-9', 'col-9');
    creatorEl.classList.add('text-info');
    creatorEl.textContent = notification.creator.name;
    messageEl.textContent = notification.message;
    dateTimeEl.classList.add('text-info');
    dateTimeEl.textContent = notification.dateTime;
    trashIconColEl.classList.add('col', 'align-self-center', 'offset-1');
    trashIconColEl.setAttribute('align', 'center');
    trashButtonEl.classList.add('btn', 'btn-trash');
    trashButtonEl.setAttribute('id', index);
    trashButtonEl.addEventListener('click', handleRemoveNotification);
    trashIconEl.classList.add('fa', 'fa-trash');

    notificationColEl.appendChild(creatorEl);
    notificationColEl.appendChild(messageEl);
    notificationColEl.appendChild(dateTimeEl);
    rowEl.appendChild(notificationColEl);
    trashButtonEl.appendChild(trashIconEl);
    trashIconColEl.appendChild(trashButtonEl);
    rowEl.appendChild(trashIconColEl);
    liEl.appendChild(rowEl);

    return liEl;
}

function setNumberOfUnseenNotifications(numberOfUnseenNotifications) {
    const numberOfUnseenNotificationsEl = document.getElementById('notificationsNumber');
    if (numberOfUnseenNotifications === 0) {
        numberOfUnseenNotificationsEl.style.display = 'none';
    } else {
        numberOfUnseenNotificationsEl.style.display = 'block';
        numberOfUnseenNotificationsEl.textContent = numberOfUnseenNotifications;
    }
}

function markAllNotificationsAsRead() {
    const numberOfUnseenNotificationsEl = document.getElementById('notificationsNumber');
    numberOfUnseenNotificationsEl.style.display = 'none';
    sessionStorage.setItem(UNSEEN_NOTIFICATIONS + memberId, JSON.stringify(0));
}
function noNotificationsMessageEntry() {
    const notificationsBody = document.getElementById('notifications');
    const liEl = document.createElement('li');
    const rowEl = document.createElement('div');
    const colEl = document.createElement('div');
    const messageEl = document.createElement('strong');

    liEl.classList.add('notification-box');
    rowEl.classList.add('row');
    colEl.classList.add('col-lg-9', 'col-sm-9', 'col-9')
    messageEl.classList.add('text-info');
    messageEl.textContent = "No Notifications";

    colEl.appendChild(messageEl);
    rowEl.appendChild(colEl);
    liEl.appendChild(rowEl);
    notificationsBody.appendChild(liEl);
}