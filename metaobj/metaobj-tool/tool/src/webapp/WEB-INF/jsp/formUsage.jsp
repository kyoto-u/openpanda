<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "messages"/>

<div class="navIntraTool">
	<a href="<osp:url value="/listStructuredArtifactDefinitions.osp"/>"
         title="<fmt:message key="back_to_list"/>" ><fmt:message key="back_to_list"/></a>
</div>

<osp:url var="listUrl" value="formUsage.osp">
	<osp:param name="id" value="${formId}"/>
</osp:url>
<osp:listScroll listUrl="${listUrl}" className="listNav" />

<h3><fmt:message key="title_formUsage">
	<fmt:param value="${formName}" />
</fmt:message>
</h3>

<c:if test="${!empty usage}">
 <table class="listHier lines nolines" cellspacing="0" cellpadding="0" summary="<fmt:message key="table_header_summary"/>">
   <thead>
      <tr>
         <th scope="col"><fmt:message key="table_header_type"/></th>
         <th scope="col"><fmt:message key="table_header_detail1"/></th>
         <th scope="col"><fmt:message key="table_header_detail2"/></th>
         <th scope="col"><fmt:message key="table_header_sitename"/></th>
      </tr>
   </thead>

  <c:forEach var="use" items="${usage}">
    <TR>
      <TD>
         <c:out value="${use.type}" />
      </TD>
      <TD>
         <c:out value="${use.detail1}" />
      </TD>
      <TD>
         <c:out value="${use.detail2}" />
      </TD>
      <TD>
         <c:out value="${use.siteName}" />
      </TD>
    </TR>
  </c:forEach>
  </table>
<br/>


</c:if>

<c:if test="${empty usage}">
<fmt:message key="text_noUsageAvailable"/>
</c:if>
