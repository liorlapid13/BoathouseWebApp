const STATUS_OK = 200;

async function fetchAndDisplayActivitiesInDropDownMenu(dropDownMenuEl, modal, modalTitle, modalBody,
                                                       selectActivityButtonEl, manualActivityEl) {
    const response = await fetch('../activities', {
        method: 'get',
    });

    if (response.status === STATUS_OK) {
        //const activityDropDownMenuEl = document.getElementById('activityDropDownMenu');
        let activityList = await response.json();
        for (let i = 0; i < activityList.length; i++) {
            dropDownMenuEl.appendChild(createActivityOption(activityList[i], i));
        }
        return activityList;
    } else {
        //const modalTitle = document.getElementById('modalLabel');
        modalTitle.textContent = "Pay Attention!" ;
        //const modalBody = document.getElementById('modalBody');
        modalBody.textContent = "There are no available activities for this day"
        //const modal = document.getElementById('modal');
        showModal(modal);
        //const selectActivityButtonEl = document.getElementById('buttonSelectActivity');
        selectActivityButtonEl.textContent = "Select Time";
        //const manualActivityEl = document.getElementById('manualActivity');
        manualActivityEl.style.display = "block";
    }
}

function createActivityOption(activity, index) {
    const activityOption = document.createElement('option');
    activityOption.value = index;
    activityOption.textContent = activity.name+ ", " +activity.time;

    return activityOption;
}

/*
function initializeDaysDropDownMenu() {
    const daysDropDownMenu = document.getElementById('daysDropDownMenu');
    const dropDownOptions = daysDropDownMenu.getElementsByTagName('option');
    const options = { weekday: 'long', year: 'numeric', month: 'numeric', day: 'numeric' };
    let day = new Date();
    for (let i = 1; i <= 7; i++) {
        day.setDate(day.getDate() + 1);
        dropDownOptions[i-1].textContent = day.toLocaleDateString(undefined, options);
    }
}

function disableCheckBoxes(checkBoxes) {
    for (let i = 0; i < checkBoxes.length; i++) {
        checkBoxes[i].disable = true;
    }
}

function enableCheckBoxes(checkBoxes) {
    for (let i = 0; i < checkBoxes.length; i++) {
        checkBoxes[i].disable = false;
    }
}*/
