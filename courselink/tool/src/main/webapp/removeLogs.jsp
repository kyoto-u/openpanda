<jsp:directive.include file="/templates/includes.jsp"/>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css" />
    <link media="all" href="/library/skin/neo-default/tool.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" type="text/css" media="all" href="${pageContext.request.contextPath}/css/courselink.css" />

<link type="text/css" href="lib/jquery-ui/css/smoothness/jquery-ui-1.10.1.custom.css" rel="stylesheet" />
<script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
<script type="text/javascript" src="lib/jquery-ui/js/jquery-1.9.1.js"></script>
<script type="text/javascript" src="lib/jquery-ui/js/jquery-ui-1.10.1.custom.min.js"></script>
<script type="text/javascript">
$(function(){
	$('#datepicker').datepicker({
		showOn: "button",
		buttonImage: "/library/calendar/images/calendar/cal.gif",
		buttonImageOnly: true,
		dateFormat: 'yy/mm/dd',
		changeMonth: true,
		changeYear: true,
		showMonthAfterYear: true
	});
});
</script>
</head>
<body onload="<%=request.getAttribute("sakai.html.body.onload")%>">
<div class="portletBody">
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="courselink.kyoto_u.ac.jp.bundle.messages"/>
</jsp:useBean>

<!-- menu -->
<div class="navIntraTool" style="font-size:15px !important;">
	<img src="image/table_edit.png" align="middle"> <a href=index.htm?page=requestlist><c:out value="${msgs.item_menu_request_list_title}" /></a>
	<img src="image/table_error.png" align="middle"> <a href=index.htm?page=rejectlist><c:out value="${msgs.item_menu_reject_list_title }" /></a>
	<img src="image/table_delete.png" align="middle"> <a href="#"><c:out value="${msgs.item_menu_remove_title }" /></a>
</div>
<br/>

<c:if test="${not empty removedRequestsNum }">
	<c:choose>
		<c:when test="${removedRequestsNum < 0}">
			<div class="msg-cancel"><spring:message code="error_dareforamt_invalid" /></div>
		</c:when>
		<c:otherwise>
			<div class="msg-check"><spring:message code="item_removed_results" arguments="${removedRequestsNum }"/></div>
		</c:otherwise>
	</c:choose>
	<br/>
</c:if>
<c:out value="${msgs.item_remove_msg }" /><br/>
<form:form name="dateForm" action="index.htm?action=confirmRemoveLogs">
<c:out value="${msgs.item_remove_date}" />
<input id="datepicker" name="insertDate" type="text" readonly="readonly" />
<input type="submit" value='<c:out value="${msgs.item_remove_confirm_button_title}" />'
  onclick="return checkConfirm('<c:out value='${msgs.error_no_date}' />');" />
</form:form>

<table border="0" height="200">
<tr><td></td></tr>
</table>
<script>
<!--
function checkConfirm(msg1){
	if(document.dateForm.insertDate.value == ""){
		window.alert(msg1);
		return false;
	}
	return true;
}
//-->
</script>
<jsp:directive.include file="/templates/footer.jsp"/>