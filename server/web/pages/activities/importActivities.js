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


