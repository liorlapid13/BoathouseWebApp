let modal;
let modalBody;
let modalTitle;

window.addEventListener('load', () => {
    initializeModal();
    setupEventHandlers();

});

function setupEventHandlers(){
    const importFormEl= document.getElementById("formImportActivities");
    importFormEl.addEventListener('submit',handleImportSubmit)
    const exportActivitiesButton = document.getElementById("exportActivitiesButton");
    exportActivitiesButton.addEventListener('click',handleExportActivites)
    const modalCloseButtonEl = document.getElementById("closeButton");
    modalCloseButtonEl.addEventListener('click',() =>{
        hideModal(modal);
    });
}
function initializeModal(){
    modal = document.getElementById("modal");
    modalBody = document.getElementById("modalBody");
    modalTitle = document.getElementById("modalLabel");
}
async function handleExportActivites(){

    const data = {
        typeOfData : "activities"
    }
    const response = await fetch('../../exportData', {
        method: 'post',
        headers: new Headers({
            'Content-Type': 'application/json;charset=utf-8'
        }),
        body: JSON.stringify(data)
    });

    if(response.status == STATUS_OK){
        var text = await response.text();
        var fileName = "exportActivities.xml"
        download(fileName,text);
    }
}

function handleImportSubmit(event){
    event.preventDefault();
    const xmlFile =document.getElementById("formFileLg").value;

    if(checkLegalXmlFileName(xmlFile)){
        readFileContent(xmlFile);
        fetch(xmlFile).then((response) =>{response.text().then((text) =>{
            xmlString = text;
       });
        });
    }
    else{
        modalTitle.textContent = "Pay Attention!";
        modalBody.textContent = "You must to insert XML file"
        showModal(modal);
        return;
    }
}

function checkLegalXmlFileName(xmlFile){
    return xmlFile.length >= 5 &&
        xmlFile.charAt(xmlFile.length - 1) === 'l' &&
        xmlFile.charAt(xmlFile.length - 2) === 'm' &&
        xmlFile.charAt(xmlFile.length - 3) === 'x' &&
        xmlFile.charAt(xmlFile.length - 4) === '.';
}

function createAndOpenFile(){
    var stupidExample = '<?xml version="1.0" encoding="UTF-8"?> <engine>\n' +
        '    <assignmentList/>' +
        '</engine>';
    document.open('data:Application/octet-stream,' + encodeURIComponent(stupidExample));
}


