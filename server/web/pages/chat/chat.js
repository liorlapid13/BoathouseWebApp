let chatVersion = 0;
const chatRefreshRate = 2 * 1000;

window.addEventListener('load', () => {
    joinChat();
    fetchChat();
    fetchUsers();
    setInterval(fetchUsers, chatRefreshRate);
    setupEventHandlers();
});


window.addEventListener('beforeunload', () => {
    leaveChat();
})

function setupEventHandlers() {
    const chatFormEl = document.getElementById('chatform');
    chatFormEl.addEventListener('submit', handleFormSubmit);
}

async function joinChat() {
    await fetch('../../joinChat');
}

async function fetchChat() {
    try {
        const response = await fetch('../../chat', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(chatVersion)
        });

        if (response.status === STATUS_OK) {
            const chat = await response.json();

            chatVersion = chat.version;
            appendNewChatEntries(chat.entries);
        }
    } finally {
        triggerTimeoutRefreshChat();
    }
}

function appendNewChatEntries(entries) {
    const chatAreaEl = document.querySelector('#chatarea');

    entries.forEach(appendMessageEntry);

    const height = chatAreaEl.scrollHeight - chatAreaEl.getBoundingClientRect().height;
    chatAreaEl.scrollTop = height;
}

function appendMessageEntry(messageEntry) {
    const chatAreaEl = document.querySelector('#chatarea');

    chatAreaEl.append(createMessageEntryElement(messageEntry));
}

function createMessageEntryElement(messageEntry) {
    const element = document.createElement('div');
    element.classList.add('success');
    element.textContent = `${messageEntry.username}: ${messageEntry.message}`;

    return element;
}

function triggerTimeoutRefreshChat() {
    setTimeout(fetchChat, chatRefreshRate);
}

async function fetchUsers() {
    const response = await fetch('../../users');
    const users = await response.json();
    refreshUsersList(users);
}

function refreshUsersList(users = []) {
    const usersListEl = document.querySelector('#userslist');

    usersListEl.innerHTML = '';

    users.forEach((username, index) => {
        usersListEl.append(createUsernameElement(username));
    });
}

function createUsernameElement (username) {
    const listItemEl = document.createElement('li');

    listItemEl.innerText = username;

    return listItemEl;
}

async function leaveChat() {
    await fetch('../../leaveChat');
}

function handleFormSubmit(event) {
    event.preventDefault();
    sendMessage().then(() => {
        document.getElementById('inputChat').value = '';
    });
}

async function sendMessage() {
    const chatInputEl = document.getElementById('inputChat');

    if (chatInputEl.value.trim() !== '') {
        let message = chatInputEl.value;

        await fetch('../../sendMessage', {
            method: 'post',
            headers: new Headers({
                'Content-Type': 'application/json;charset=utf-8'
            }),
            body: JSON.stringify(message)
        });
    }
}