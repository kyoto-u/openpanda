<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>


<table width="100%">
<tr><td height="100"> &nbsp; </td></tr>
<tr><td align="center">
<c:choose>
<c:when test="${noPagesFound}">
<c:out value="${msgs.presentation_no_pages_founc}"/>
</c:when>
<c:otherwise>
<c:out value="${msgs.presentation_not_found}"/>
</c:otherwise>
</c:choose>
</td></tr></table>
