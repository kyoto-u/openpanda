<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<form method="get" action="<osp:url value="scaffoldingCellInfo.osp"/>">
<osp:form/>

<p class="shorttext">
<label><c:out value="${msgs.label_title}" /></label> <c:out value="  ${scaffoldingCell.title}" />
</p>
<c:if test="${not empty scaffoldingCell.wizardPageDefinition.description}">
<p class="longtext">
<label class="block"><c:out value="${msgs.label_description}" /></label>
<c:out value="  ${scaffoldingCell.wizardPageDefinition.description}" escapeXml="false"/>
</p>
</c:if>

<c:if test="${not empty scaffoldingCell.guidance && not empty scaffoldingCell.guidance.instruction}">
<p class="longtext">
	<label class="block"><c:out value="${msgs.instructions}" /></label>
	<c:out value="  ${scaffoldingCell.guidance.instruction.text}" escapeXml="false"/>
</p>
</c:if>
</form>
