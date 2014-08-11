<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<c:forEach var="log" items="${presentationLogs}" varStatus="presentationStatus">
    <c:set var="count" value="${presentationStatus.count}" scope="request" />
    <c:set var="presentationName" value="${log.presentation.name}" />
</c:forEach>

<h3><c:out value="${msgs.title_presentationStats}"/></h3>

    <c:out value="${msgs.viewPresentationStats_countReport}"/>
    <c:out value="${presentationName}" />


<table class="listHier">
    <tr>
        <th><c:out value="${msgs.table_header_viewer}"/></th>
        <th><c:out value="${msgs.table_header_date}"/></th>
    </tr>


    <c:forEach var="log" items="${presentationLogs}">
       <TR>
          <TD width="200"><c:out value="${log.viewer.displayName}" />&nbsp;</TD>
          <TD width="200"><c:set var="dateFormat"><c:out value="${msgs.dateFormat_Middle}"/></c:set><fmt:formatDate value="${log.viewDate}" pattern="${dateFormat}"/></TD>	  
       </TR>
    </c:forEach>
</table>
<form method="post" action="listPresentation.osp">
<input type="submit" name="submit" value="<c:out value="${msgs.button_back}"/>"/>
</form>
