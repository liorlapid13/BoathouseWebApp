window.addEventListener('load', () => {
    addManagerNavBar()
});

async function addManagerNavBar() {
    const response = await fetch('../../memberType', {
        method: 'get'
    })
    const memberType = await response.text();

    if (memberType === "Manager") {
        addNavBar();
    }
}

function addNavBar() {
    const navUlEl = document.getElementById('mainNav');
    const logoutButton = document.getElementById("logoutButton");
    navUlEl.insertBefore(createManageActivitiesOption(),logoutButton);
    navUlEl.insertBefore(createManageBoatsOption(),logoutButton);
    navUlEl.insertBefore(createManageMembersOption(),logoutButton);
    navUlEl.insertBefore(createManageReservationsOption(),logoutButton);
    navUlEl.insertBefore(createManageAssignmentsOption(),logoutButton);
}

function createManageAssignmentsOption() {
    const manageReservationsLiEl = document.createElement("li");
    manageReservationsLiEl.classList.add("nav-item", "dropdown");
    manageReservationsLiEl.appendChild(createLilink("Manage Assignments","navbarDropdown6"));
    const namesOfItems = ["View All Assignments","Create New Assignment","Remove Assignment"];
    const pageLinks = ["#","#","#"];
    manageReservationsLiEl.appendChild(createDropDownMenu(namesOfItems,pageLinks));

    return manageReservationsLiEl;
}

function createManageReservationsOption() {
    const manageReservationsLiEl = document.createElement("li");
    manageReservationsLiEl.classList.add("nav-item", "dropdown");
    manageReservationsLiEl.appendChild(createLilink("Manage Reservations","navbarDropdown5"));
    const namesOfItems = ["View All Reservations","Edit Reservations","Remove Reservations"]
    const pageLinks = ["#","#","#"];
    manageReservationsLiEl.appendChild(createDropDownMenu(namesOfItems,pageLinks));

    return manageReservationsLiEl;
}

function createManageMembersOption() {
    const manageMembersLiEl = document.createElement("li");
    manageMembersLiEl.classList.add("nav-item", "dropdown");
    manageMembersLiEl.appendChild(createLilink("Manage Members","navbarDropdown4"));
    const namesOfItems = ["Add New Member","Remove Member",
        "View All Members","Edit Member","Import Members From XML","Export Members To XML"]
    const pageLinks = ["#","#","#","#","#","#"];
    manageMembersLiEl.appendChild(createDropDownMenu(namesOfItems,pageLinks));

    return manageMembersLiEl;
}

function createManageBoatsOption() {
    const manageBoatsLiEl = document.createElement("li");
    manageBoatsLiEl.classList.add("nav-item", "dropdown");
    manageBoatsLiEl.appendChild(createLilink("Manage Boats","navbarDropdown3"));
    const namesOfItems = ["Add New Boat","Remove Boat",
        "View All Boats","Edit Boat","Import Boats From XML","Export Boats To XML"]
    const pageLinks = ["#","#","#","#","#","#"];
    manageBoatsLiEl.appendChild(createDropDownMenu(namesOfItems,pageLinks));
    return manageBoatsLiEl;
}

function createManageActivitiesOption() {
    const manageActivitiesLiEl = document.createElement("li");
    manageActivitiesLiEl.classList.add("nav-item", "dropdown");
    manageActivitiesLiEl.appendChild(createLilink("Manage Activities","navbarDropdown2"));
    const namesOfItems = ["Add New Activity","All Activities","Import Activities From XML","Export Activities To XML"]
    const pageLinks = ["#","../activities/activities.html","../activities/importActivities.html","#"];
    manageActivitiesLiEl.appendChild(createDropDownMenu(namesOfItems,pageLinks));

    return manageActivitiesLiEl;
}

function createDropDownMenu(items,links) {
    const dropDownUlEl = document.createElement("ul");
    dropDownUlEl.classList.add("dropdown-menu");
    dropDownUlEl.setAttribute("aria-labelledby","navbarDropdown");
    for(let i = 0; i < items.length;i++){
        dropDownUlEl.appendChild(createDropDownItem("dropdown-item",links[i],items[i]));
    }

    return dropDownUlEl;
}

function createLilink(linkName,id) {
    const linkEl = document.createElement("a");
    linkEl.classList.add("nav-link" ,"dropdown-toggle", "white_link");
    linkEl.setAttribute("href", "#");
    linkEl.setAttribute("id",id);
    linkEl.setAttribute("role", "button");
    linkEl.setAttribute("data-bs-toggle", "dropdown");
    linkEl.setAttribute("aria-expanded", "false");
    linkEl.textContent = linkName;

    return linkEl;
}

function createDropDownItem(className,link,text) {
    const newLiEl = document.createElement("li");
    const linkEl = document.createElement("a");
    linkEl.classList.add(className);
    linkEl.setAttribute("href", link);
    linkEl.textContent = text;
    newLiEl.appendChild(linkEl);

    return newLiEl;
}