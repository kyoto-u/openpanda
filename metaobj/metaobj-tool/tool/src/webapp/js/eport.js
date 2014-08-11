var currentPickerId = 0;

function createFilePicker(url, selectedId, pickerService, params, nodeIdField, nodeNameField, expanded) {
   var pickerId = createFilePickerLink(url, selectedId, pickerService, params, nodeIdField, nodeNameField, expanded);
   createFilePickerFrame(pickerId, url, selectedId, pickerService, params, nodeIdField, nodeNameField, expanded);
}

function createFilePickerLink(url, selectedId, pickerService, params, nodeIdField, nodeNameField, expanded) {
   pickerId = "picker" + currentPickerId;
   currentPickerId++;   

   var pickerUrl = url+"&action=show&selected="+selectedId+"&" +
      "filterName="+pickerService+"&nodeIdField="+nodeIdField +
      "&nodeNameField="+nodeNameField+"&pickerId="+pickerId+"&panelId="+pickerId+"Frame&"+params;      

   document.write("<script language='javascript'>");
   document.write("function showFilePicker"+pickerId+"() {");
   document.write("return showFilePicker('"+pickerId+"')}");   
   document.write("</script>");
   
   document.write("<script language='javascript'>");
   document.write("function refreshFrame"+pickerId+"Frame() {");   
   document.write("   ospGetElementById('"+pickerId+"Frame').src='"+pickerUrl+"';");   
   document.write("}");   
   document.write("</script>");
   
   document.write("<a href='"+pickerUrl+"'");
   document.write(" target='"+pickerId+"Frame' onClick='showFilePicker"+pickerId+"()' ");
   document.write(" id='"+pickerId+"Link'>");
   document.write("pick</a>");
   
   return pickerId;
}

function createFilePickerFrame(pickerId, url, selectedId, pickerService, params, nodeIdField, nodeNameField, expanded) {
   // write the link, div, iframe and javascript stuff

   var pickerUrl = url+"&action=show&selected="+selectedId+"&" +
      "filterName="+pickerService+"&nodeIdField="+nodeIdField +
      "&nodeNameField="+nodeNameField+"&pickerId="+pickerId+"&panelId="+pickerId+"Frame&"+params;      

   document.write("<div class='filePicker' id='"+ pickerId +"'>");
   document.write(" ");
   document.write("<iframe");
	document.write(" name='"+pickerId+"Frame'");
	document.write(" id='"+pickerId+"Frame'");
	document.write(" height='0'");
	document.write(" width='650'");
	document.write(" title='File Picker'");
	document.write(" frameborder='0'");
	document.write(" marginwidth='0'");
	document.write(" marginheight='0'");
	document.write(" scrolling='auto'");

	if (expanded) {
	   document.write(" src='"+pickerUrl+"'>");
	}
   else {
	   document.write(" src='"+url+"&action=none'>");
	}

	document.write("</iframe>");   
   document.write("</div>");
   
}

function showFilePicker(pickerId) {
   // set div visible
   return true;
}

function setFileFieldsAndClose(pickerId, selectedFileId, selectedFileName, nodeIdField, nodeNameField) {
   // set field values   
   var idField = parent.ospGetElementById(nodeIdField);
   var nameField = parent.ospGetElementById(nodeNameField);
   
   if (idField) {
      idField.value = selectedFileId;
   }
   
   if (nameField) {
      nameField.value = selectedFileName;
   }
}

function closeFileDiv(pickerId) {
   // close div
      
   var obj = parent.ospGetElementById(pickerId + "Frame");
   if (obj) {      
      obj.style.height = "0px";     
   }
   parent.resetHeight();
}

function XP_IE_Browser(){
   var agt=navigator.userAgent.toLowerCase();
   if (agt.indexOf("xp") != -1 && agt.indexOf("msie") != -1) return true;
   if (agt.indexOf("nt 5.1") != -1 && agt.indexOf("msie") != -1) return true;
   return false;
}

function getSelectedValue(field){
 var selected = ospGetElementById(field);
 return selected.options[selected.selectedIndex].value;
}

function ospGetElementById(id){
	if (document.getElementById) {
		return (document.getElementById(id));
	} else if (document.all) {
		return (document.all[id]);
	} else {
		if ((navigator.appname.indexOf("Netscape") != -1) && parseInt(navigator.appversion == 4)) {
			return (document.layers[id]);
		}
	}
}

/**
 * This function sets the given select form element (drop-down list) to a new selection
 * selectObj - the select form element object
 * newSelection - the value to select in the select form element (drop-down list)
 */
function ospUpdateSelect(selectObj, newSelection)
{
	for (i=0; i<selectObj.length; i++)
	{
  		if (selectObj.options[i].value == newSelection)
  		{
  			selectObj.selectedIndex = i;
  			return;
  		}
  	}
}

function osp_dateselectionwidgetpopup(yearselect_id, monthselect_id, dayselect_id)
{
	var calendarcounter = document.calendarcounter++;

	var inputfield_id = "chef_calendarhiddenfield"+calendarcounter;

	// The image button that the user clicks on to pop up the calendar
	document.write('<img src="/sakai-chef-tool/calendar/images/calendar/cal.gif" onClick="osp_dateselection_init(\'' + yearselect_id + '\', \'' + monthselect_id + '\',\'' + dayselect_id + '\');popupCalendar(\''+inputfield_id+'\');" alt="" style="cursor: pointer;" title="Popup date selector" />');

	// A hidden input field where the selected date value will be stored.
	document.write('<input type="hidden" name="'+inputfield_id+'" id="'+inputfield_id+'" />');

	// stuff away variables specific to this particular calendar instance so that updateXXX() can get them
	document.calendars[calendarcounter] = new Array(yearselect_id, monthselect_id, dayselect_id, inputfield_id);
}


/**
 * sets drop down in date picker to first value, this allows date to not be required on pages
 * once a user selects
 */
function osp_dateselection_init(year, month, day){
   if (getSelectedValue(year) == "" || getSelectedValue(month) == "" || getSelectedValue(day) == ""){
      ospUpdateSelect(ospGetElementById(year), ospGetElementById(year).options[1].value);
      ospUpdateSelect(ospGetElementById(day), ospGetElementById(day).options[1].value);
      ospUpdateSelect(ospGetElementById(month), ospGetElementById(month).options[1].value);
   }
}

function limitChar(field, maxlimit){
    var msg1 = "You have reached the maximum character limit of "
    var msg2 = " characters.\n\nExtra characters will be removed."
    var strValue = field.value
    var strLen = strValue.length
    if (strLen > maxlimit){
        field.blur();
        field.focus();
        field.select();
        alert(msg1 + maxlimit + msg2);
    var strNew = strValue.substring(0, maxlimit)
        field.value = strNew;
        return false;
    } else {
       return true;
    }

}

//Script created by Jim Young (www.requestcode.com)
//Submitted to JavaScript Kit (http://javascriptkit.com)
//Visit http://javascriptkit.com for this script

//Set the tool tip message you want for each link here.
function showtip(current,e,tip) {
   if (document.layers) // Netscape 4.0+
      {
       theString="<DIV CLASS='ttip'>"+tip+"</DIV>"
       document.tooltip.document.write(theString)
       document.tooltip.document.close()
       document.tooltip.left=e.pageX+14
       document.tooltip.top=e.pageY+2
       document.tooltip.visibility="show"
      }
   else
   {
      if(document.all || document.getElementById) // Netscape 6.0+ and Internet Explorer 5.0+
      {
          var mouseX, mouseY, db=document.body;
          elm=document.getElementById("tooltip")
          elm.innerHTML='<table width="250" cellpadding="0" cellspacing="0" border="0"><tr><td><div align="left">' + tip + '</div></td></tr></table>';

          if (document.getElementById && !document.all) {
              mouseX = e.pageX; mouseY = e.pageY;
              elm.style.left = ((mouseX + current.offsetWidth) > (window.innerWidth-20 + window.pageXOffset)) ? mouseX - current.offsetWidth -10 + 'px' : window.pageXOffset+mouseX + 'px';
          } else {
              mouseX = window.event.clientX + db.scrollLeft;
              mouseY = window.event.clientY + db.scrollTop;
              elm.style.left = ((e.x + current.clientWidth) > (db.clientWidth + db.scrollLeft)) ? (db.clientWidth + db.scrollLeft) - current.clientWidth-10 + 'px' : mouseX + 'px';
          }

          elm.style.top = mouseY + 20 + 'px';
          elm.style.visibility = "visible"
      }
   }
}

function hidetip(){
   if (document.layers) { // Netscape 4.0+
      document.tooltip.visibility="hidden"
   } else {
      if(document.getElementById) // Netscape 6.0+ and Internet Explorer 5.0+
         elm.style.visibility="hidden"
   }
}

function move(fboxName, tboxName, allItems) {
   var fbox = ospGetElementById(fboxName);
   var tbox = ospGetElementById(tboxName);   
   var arrFbox = new Array();
   var arrTbox = new Array();
   var arrLookup = new Array();
   var i;

   for (i = 0; i < tbox.options.length; i++) {
      arrLookup[tbox.options[i].text] = tbox.options[i].value;
      arrTbox[i] = tbox.options[i].text;
   }
   var fLength = 0;
   var tLength = arrTbox.length;

   for(i = 0; i < fbox.options.length; i++) {
      arrLookup[fbox.options[i].text] = fbox.options[i].value;
      if (allItems && fbox.options[i].value != ""){
         arrTbox[tLength] = fbox.options[i].text;
         tLength++;
      }
      else if (fbox.options[i].selected && fbox.options[i].value != "") {
         arrTbox[tLength] = fbox.options[i].text;
         tLength++;
      }
      else {
         arrFbox[fLength] = fbox.options[i].text;
         fLength++;
      }
   }
   arrFbox.sort();
   arrTbox.sort();
   fbox.length = 0;
   tbox.length = 0;
   var c;
   for(c = 0; c < arrFbox.length; c++) {
       addOption(fbox,arrLookup[arrFbox[c]],arrFbox[c],c);
   }
   for(c = 0; c < arrTbox.length; c++) {
       addOption(tbox,arrLookup[arrTbox[c]],arrTbox[c],c);
   }
}

function addOption(select, value, text, index){
      var no = new Option();
      no.value = value;
      no.text = text;
      select[index] = no;
}

/*
function addOptionIE(select, value, text, index){
    var doc = select.ownerDocument;
    if (!doc)
        doc = select.document;
    var opt = doc.createElement('OPTION');
    opt.value = value;
    opt.text = text;
    select.options.add(opt, index);
}
*/

function confirmDeletion(element) {
   if (element == null || element.length == 0) element = 'this item';
   return confirm("Are you sure you want to permanently delete " + element + "?");
}

function showFrame(selectBoxName,previewFrameName, previewButtonName,closeButtonName,url,message){
   var selectBox = ospGetElementById(selectBoxName);
   var selectedIndex = selectBox.selectedIndex;

   //do nothing if nothing is selected
   if (selectedIndex == -1 || undefined == selectedIndex){
      alert(message);
      return false;
   }
   var value = selectBox.options[selectedIndex].value;
   if (undefined == value || value == ""){
      alert(message);
      return false;
   }
   var previewFrame = document.getElementById(previewFrameName);
   var previewButton = ospGetElementById(previewButtonName);
   var closeButton = ospGetElementById(closeButtonName);

   previewButton.style.visibility="hidden";
   previewFrame.style.height="180px";
   closeButton.style.visibility="visible";
   previewFrame.src=url;
   return false;
}

function closeFrame(previewFrameName, previewButtonName, closeButtonName){
   var previewButton = ospGetElementById(previewButtonName);
   var previewFrame = document.getElementById(previewFrameName);
   var closeButton = ospGetElementById(closeButtonName);

   closeButton.style.visibility="hidden";
   previewFrame.style.height="0px";
   previewButton.style.visibility="visible";
   return false;
}

function setElementValue(elementName, value){
   var element = ospGetElementById(elementName);
   element.value = value;
}

function addViewer(elementId, url){
   var viewername = ospGetElementById(elementId).value;
   if (viewername.length > 0){
      window.document.location=url + viewername;
   }
}

function updateItems(selectBoxName) {
   var selectBox = ospGetElementById(selectBoxName);
   for (j = 0; j < selectBox.options.length; j++) {
      selectBox.options[j].selected = true;
   }
   return true;
}

function userHasRole(memberId, roleId){
   var i;
   var roles = getRolesForMember(memberId);
   for (i=0;i<roles.length;i++){
   //alert("memberId=" + memberId);
   //alert("roles[i]=" + roles[i]);
   //alert("roleId=" + roleId);

      if (roles[i] == roleId){
         return true;
      }
   }
   return false;
}




/*
 * returns true if the given selectBox option list element contains the given value
 */
function contains(selectBox, value){
   var undefined;
   if (selectBox.options == null || selectBox.options == undefined){
        return false;
   }
   for (i = 0; i < selectBox.options.length; i++) {
      if (selectBox.options[i].value == value){
         return true;
      }
   }
   return false;
}

/*
 * filters the first select box with filter preference in filter select box
 * doesn't include values already in the second select box
 */
function updateParticipantList(filterName,select1Name,select2Name){
   var select1Box = ospGetElementById(select1Name);
   var select2Box = ospGetElementById(select2Name);

   // clear out first select box
   select1Box.length = 0;

   if (getSelectedValue(filterName) == "roles"){
      var i;
      var j=0;
      var roles = getRoles();

      for (i = 0; i < roles.length; i++) {
         if (contains(select2Box, roles[i].value) == false){
            select1Box.options[j++] = roles[i];
         }
      }
   } else if (getSelectedValue(filterName) == "all"){
      var i;
      var j=0;
      var roles = getRoles();
      var members = getMembers();

      for (i = 0; i < roles.length; i++) {
         if (contains(select2Box, roles[i].value) == false){
            select1Box.options[j++] = roles[i];
         }
      }

      for (i = 0; i < members.length; i++) {
         if (contains(select2Box, members[i].value) == false){
            select1Box.options[j++] = members[i];
         }
      }
   } else {
      var i;
      var j=0;
      var members = getMembers();
      var roleId = getSelectedValue(filterName);

      for (i = 0; i < members.length; i++) {
         if (contains(select2Box, members[i].value) == false &&
               userHasRole(members[i].value, roleId)){
            select1Box.options[j++] = members[i];
         }
      }
   }
}



/*
 * submits a form called cancelForm
 */
function doCancel() {
	document.cancelForm.submit();
}

function selectChange(changedControl, controlToPopulate, itemArray) {
   var selectedValue = changedControl.value;
   var myEle ;

   var newArray = itemArray[selectedValue];
   
   for (var q=controlToPopulate.options.length;q>=0;q--) 
      controlToPopulate.options[q]=null;

   // Now loop through the array of individual items
   // Any containing the same child id are added to
   // the second dropdown box
   for ( x = 0 ; x < newArray.length  ; x++ ) {
      controlToPopulate.options[x] = new Option(newArray[x], newArray[x]) ;
   }
      
}

function openNewWindow(URL)
{
	var width = screen.width/4;
	var height = screen.height/2;
	var left = screen.width-width;
	
   sFeatures = "left=" + left + ",top=0,width=" + width + ",height=" + height + ",titlebar=yes,status=yes,scrollbars=yes,resizable=yes";
   window.open(URL, "osp_window", sFeatures);
}

