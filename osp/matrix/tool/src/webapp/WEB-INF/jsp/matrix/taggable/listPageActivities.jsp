<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}" />
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>

<script type="text/javascript" language="JavaScript"
	src="/osp-common-tool/js/thickbox.js"></script>
<link href="/osp-common-tool/css/thickbox.css" type="text/css"
	rel="stylesheet" media="all" />

<h3>
 <c:out value="${msgs.matrix_page_associations}"/>
 <c:out value="${pageTitle}"/>
</h3>

<table class="listHier lines nolines" cellspacing="0" border="0"
	summary="<c:out value="${msgs.list_activity_summary}"/>">
	<thead>
		<tr>
			<th scope="col"><c:out value="${msgs.item_name_title}" /></th>
			<th scope="col"><c:out value="${msgs.type_title}" /></th>
			<th scope="col"><c:out value="${msgs.site_heading}" /></th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="activity" items="${pageActivities}">
			<tr>
				<td style="white-space: nowrap"><a class="thickbox"
					href="<c:out value="${activity.activity.activityDetailUrl}" /><c:if test="${activity.activity.useDecoration}">/null/<c:out value="${decoWrapper}" /></c:if><c:out value="${activity.activity.activityDetailUrlParams}" />&tagReference=<c:out value="${criteriaRef}"/>">
				<c:out value="${activity.activity.title}" /> </a></td>
				<td style="white-space: nowrap"><c:out
					value="${activity.activity.typeName}" /></td>
				<td style="white-space: nowrap"><c:out
					value="${activity.contextName}" escapeXml="false" /></td>

			</tr>
		</c:forEach>
	</tbody>

</table>
<br/>
<form name="form" method="POST">
	<input type="submit" name="submit" value="<c:out value="${msgs.button_back}"/>" accesskey="b" class="active"/>
</form>

