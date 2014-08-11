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
    <title><%= org.sakaiproject.tool.cover.ToolManager.getCurrentTool().getTitle()%></title>
    <script type="text/javascript"  src="/library/js/headscripts.js">
    </script>
    <script type="text/javascript" src="/osp-common-tool/js/eport.js"></script>
    	<script type="text/javascript" language="JavaScript" src="/library/js/jquery-ui-latest/js/jquery.min.js">
		</script>
  <%
      String panelId = request.getParameter("panel");
      if (panelId == null) {
         panelId = "Main" + org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getId();
      }

  %>

  <script type="text/javascript">
   function resetHeight() {
      setMainFrameHeight('<%= org.sakaiproject.util.Validator.escapeJavascript(panelId)%>');
   }

   function loaded() {
      resetHeight();
      parent.updCourier(doubleDeep, ignoreCourier);
      if (parent.resetHeight) {
         parent.resetHeight();
      }
   }
   
   iframeId = '<%= org.sakaiproject.util.Validator.escapeJavascript(panelId)%>';
   
   urlPrefix = '/tool/<%=org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getId()%>';
   
  </script>
  <%= request.getAttribute("editorHeadScript") %>
  
  </head>
  <body onload="loaded();">
      <div class="portletBody">
         <c:if test="${not empty requestScope.panelId}"><div class="ospEmbedded"></c:if>
             <jsp:include page="<%= (String)request.getAttribute(\"_body\")%>" /> 
         <c:if test="${not empty requestScope.panelId}"></div></c:if>
      </div>
   </body>
</html>
