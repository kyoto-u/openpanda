<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ include file="/WEB-INF/jsp/include.jsp" %>

<%
		response.setContentType("text/html; charset=UTF-8");
%>

<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <link rel="stylesheet" type="text/css" media="all" href="/osp-common-tool/css/eport.css" />
    <link href="<c:out value="${sakai_skin_base}"/>"
          type="text/css"
          rel="stylesheet"
          media="all" />
    <link href="<c:out value="${sakai_skin}"/>"
          type="text/css"
          rel="stylesheet"
          media="all" />
    <meta http-equiv="Content-Style-Type" content="text/css" />
    <title><%= (String)request.getAttribute("_title")%></title>
    <script type="text/javascript" language="JavaScript" src="/library/js/headscripts.js">
    </script>
    <script language="JavaScript" src="/osp-common-tool/js/eport.js"/>"></script>

  </head>

  <body onload="loaded();">
      <div class="portletBody">
         <c:if test="${not empty requestScope.panelId}"><div class="ospEmbedded"></c:if>
            <jsp:include page="<%= (String)request.getAttribute(\"_body\")%>" />
         <c:if test="${not empty requestScope.panelId}"></div></c:if>
      </div>
   </body>
</html>

