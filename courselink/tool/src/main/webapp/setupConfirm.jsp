<jsp:directive.include file="/templates/includes.jsp"/>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="courselink.kyoto_u.ac.jp.bundle.messages"/>
</jsp:useBean>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Pragma" content="no-cache">
    <meta http-equiv="Cache-Control" content="no-cache">
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
<c:set var="nextUrl" value="/direct/courselink/_kcd=${param._kcd}:_request=${param._request}:sitesetup="/>
<script>
<!--
$(document).ready(function(){
	var date = new Date();
	jConfirm("<c:out value='${msgs.course_sitesetup_confirm_message}'/>","<c:out value='${msgs.course_sitesetup_confirm_title}'/>", function(r){ if (r) {location.href="${nextUrl}"+"true?time="+date.getTime();}  else {location.href="${nextUrl}"+"false?time="+date.getTime();}});
});
// -->
</script>
</head>
<body>
<jsp:directive.include file="/templates/footer.jsp"/>

