let modal;
let modalBody;
let modalTitle;
let fileText;

window.addEventListener('load', () => {
    initializeModal();
    setupEventHandlers();

});

function setupEventHandlers(){
    const importFormEl= document.getElementById("formImportBoats");
    importFormEl.addEventListener('submit',handleImportSubmit)
    const exportActivitiesButton = document.getElementById("exportBoatsButton");
    exportActivitiesButton.addEventListener('click',handleExportBoats)
    const modalCloseButtonEl = document.getElementById("closeButton");
    document.getElementById('fileInput').addEventListener('change', handleFileSelect, false);
    modalCloseButtonEl.addEventListener('click',() =>{
        hideModal(modal);
    });
}
function initializeModal(){
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
}
async function handleExportBoats(){

    const data = {
        typeOfData : "boats"
    }
    const response = await fetch('../../../exportData', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if(response.status == STATUS_OK){
        var text = await response.text();
        var fileName = "exportBoats.xml"
        download(fileName,text);
    }
}

function handleFileSelect(event){
    const reader = new FileReader()
    reader.onload = handleFileLoad;
    reader.readAsText(event.target.files[0])
}

function handleFileLoad(event){
    fileText = event.target.result;
}
async function handleImportSubmit(event){
    event.preventDefault();
    const errorText = document.querySelector(".errorText");
    errorText.style.display = "none";

    if( document.getElementById('fileInput').files.length == 0){
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select any xml file!"
        showModal(modal);
        return;
    }
    const radioButtons = document.querySelector(".importOptions").getElementsByTagName("input")
    if(radioButtons[0].checked === false && radioButtons[1].checked === false) {
        modalTitle.textContent = "Pay Attention!" ;
        modalBody.textContent = "You must select between override or append before import"
        showModal(modal);
        return;
    }
    const isOverride = radioButtons[0].checked;

    const data = {
        typeOfData : "boats",
        xmlString : fileText,
        override : isOverride
    }
    const response = await fetch('../../../importData', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if(response.status == STATUS_OK){
        modalTitle.textContent = "" ;
        modalBody.style.color = "green";
        modalBody.textContent = "Boats imported successfully"
        showModal(modal);
    }
    else {
        errorText.style.display = "block";
        document.getElementById("errorsTextArea").textContent = await response.text();
    }
}


