const UNSEEN_NOTIFICATIONS = "unseenNotifications";
const NOTIFICATIONS_LIST = "notificationsList";
const refreshRate = 30 * 1000;

window.addEventListener('load', () => {
    initializeNotificationsList();
    setupNotificationsEventHandlers();
    setInterval(updateNotifications, refreshRate)
});

async function initializeNotificationsList() {
    const notificationsList = JSON.parse(sessionStorage.getItem(NOTIFICATIONS_LIST));
    if (notificationsList != null) {
        if (notificationsList.length !== 0) {
            clearNotificationsList();
            buildNotificationsList(notificationsList);
        }
        const unseenNotifications = JSON.parse(sessionStorage.getItem(UNSEEN_NOTIFICATIONS));
        setNumberOfUnseenNotifications(unseenNotifications);
    } else {
        const emptyList = [];
        sessionStorage.setItem(NOTIFICATIONS_LIST, JSON.stringify(emptyList));
        sessionStorage.setItem(UNSEEN_NOTIFICATIONS, JSON.stringify(0));
    }
}

function setupNotificationsEventHandlers() {
    const notificationsButtonEl = document.getElementById('buttonNotifications');
    notificationsButtonEl.addEventListener('mousedown', markAllNotificationsAsRead);
}

function handleRemoveNotification(event) {
    const notificationsList = JSON.parse(sessionStorage.getItem(NOTIFICATIONS_LIST));
    let selectedNotification = this.id;
    notificationsList.splice(selectedNotification, 1);
    clearNotificationsList();
    buildNotificationsList(notificationsList);
    sessionStorage.setItem(NOTIFICATIONS_LIST, JSON.stringify(notificationsList));
}

async function updateNotifications() {
    const notificationsList = JSON.parse(sessionStorage.getItem(NOTIFICATIONS_LIST));
    const response = await fetch('../../notifications', {
        method: 'get'
    });
    const newNotifications = await response.json();
    if (newNotifications.length !== 0) {
        let unseenNotifications = JSON.parse(sessionStorage.getItem(UNSEEN_NOTIFICATIONS));
        unseenNotifications += newNotifications.length;
        setNumberOfUnseenNotifications(unseenNotifications);
        sessionStorage.setItem(UNSEEN_NOTIFICATIONS, JSON.stringify(unseenNotifications));
        for (let i = 0; i < newNotifications.length; i++) {
            notificationsList.unshift(newNotifications[newNotifications.length - i - 1]);
        }
        clearNotificationsList();
        buildNotificationsList(notificationsList);
        sessionStorage.setItem(NOTIFICATIONS_LIST, JSON.stringify(notificationsList));
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

function markAllNotificationsAsRead(event) {
    const numberOfUnseenNotificationsEl = document.getElementById('notificationsNumber');
    numberOfUnseenNotificationsEl.style.display = 'none';
    sessionStorage.setItem(UNSEEN_NOTIFICATIONS, JSON.stringify(0));
}