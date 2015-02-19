<?xml version="1.0" encoding="UTF-8" ?>
<!--
/**********************************************************************************
 * $URL: https://source.sakaiproject.org/svn/rwiki/tags/sakai-10.4/rwiki-tool/tool/src/webapp/WEB-INF/command-pages/header.jsp $
 * $Id: header.jsp 20386 2007-01-17 18:17:52Z ian@caret.cam.ac.uk $
 **********************************************************************************/
-->
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0" 
  xmlns:c="http://java.sun.com/jsp/jstl/core"
  >
  <jsp:directive.page language="java"
    contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"/>
  <jsp:scriptlet>
  	String hss = (String)request.getAttribute("headerScriptSource");
  	if ( hss != null &amp;&amp; hss.trim().length() &gt; 0 ) {
  </jsp:scriptlet>
  <script  type="text/javascript" src="${requestScope.headerScriptSource}"> <!-- don't reduce this! --></script>
  <jsp:scriptlet>
  	}
  </jsp:scriptlet>
<script type="text/javascript" >
var placementid = "Main<jsp:expression>request.getAttribute("sakai.tool.placement.id").toString().replace('-','x')</jsp:expression>";
</script>  
</jsp:root>
