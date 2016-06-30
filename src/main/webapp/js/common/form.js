function jumpPage(pageNo) {
	$("#pageNo").val(pageNo);
	$("#mainForm").submit();
}

function sort(orderBy, defaultOrder) {
	if ($("#orderBy").val() == orderBy) {
		if ($("#order").val() == "") {
			$("#order").val(defaultOrder);
		}
		else if ($("#order").val() == "desc") {
			$("#order").val("asc");
		}
		else if ($("#order").val() == "asc") {
			$("#order").val("desc");
		}
	}
	else {
		$("#orderBy").val(orderBy);
		$("#order").val(defaultOrder);
	}

	$("#mainForm").submit();
}

function search() {
	$("#order").val("");
	$("#orderBy").val("");
	$("#pageNo").val("1");
	$("#mainForm").submit();
}

function clearForm(formName) { 
    var formObj = document.forms[formName]; 
    var formEl = formObj.elements; 
    for (var i=0; i<formEl.length; i++) { 
        var element = formEl[i]; 
        if (element.type == 'submit') { continue; } 
        if (element.type == 'reset') { continue; } 
        if (element.type == 'button') { continue; } 
        if (element.type == 'hidden') { continue; } 
 
        if (element.type == 'text') { element.value = ''; } 
		if (element.type == 'password') { element.value = ''; } 
        if (element.type == 'textarea') { element.value = ''; } 
        if (element.type == 'checkbox') { element.checked = false; } 
        if (element.type == 'radio') { element.checked = false; } 
        if (element.type == 'select-multiple') { element.selectedIndex = -1; } 
        if (element.type == 'select-one') { element.selectedIndex = 0; }  
    } 
} 