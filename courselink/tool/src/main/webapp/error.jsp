<jsp:directive.include file="/templates/includes.jsp"/>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="courselink.kyoto_u.ac.jp.bundle.messages"/>
</jsp:useBean>
<c:choose>
	<c:when test="${param.errKind == 'invalid'}">
		<c:set var="mess" value="${msgs.error_invalid }"/>
	</c:when>
	<c:when test="${param.errKind == 'siteInvalid'}">
		<c:set var="mess" value="${msgs.error_invalid_site }"/>
	</c:when>
	<c:when test="${param.errKind == 'studentInvalid'}">
		<c:set var="mess" value="${msgs.error_invalid_student }"/>
	</c:when>
</c:choose>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link media="all" href="/library/skin/tool_base.css" rel="stylesheet" type="text/css" />
    <link media="all" href="/library/skin/default/tool.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" type="text/css" media="all" href="css/courselink.css" />
    <link rel="stylesheet" type="text/css" media="all" href="lib/blue/style.css" />
    <link rel="stylesheet" type="text/css" media="all" href="lib/jquery.alerts.css" />
    <script src="/library/js/headscripts.js" language="JavaScript" type="text/javascript"></script>
    <script type="text/javascript" src="lib/jquery-1.2.6.js"></script>
    <script type="text/javascript" src="lib/jquery.alerts.js"></script>

    <title>CourselinkTool</title>
<script>
<!-- 
$(document).ready(function(){
	jAlert("<c:out value='${mess}'/>","<c:out value='${msgs.error_title}'/>", function(r){location.href="/portal/"});
});
// -->
</script>
</head>
<body>
<jsp:directive.include file="/templates/footer.jsp"/>

