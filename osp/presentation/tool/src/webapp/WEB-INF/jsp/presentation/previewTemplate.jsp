<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<script type="text/javascript">
	setMainFrameHeight('previewFrame');
</script>
<div style="padding:0 1em">
	<h3><c:out value="${msgs.title_preview}"/> <c:out value="${template.name}"/></h3>
	<div class="textPanel indnt2">
		<h4>Template Description</h4>
		<div class="textPanel">
			<c:out value="${template.description}"/>
		</div>
	</div>	
	<div class="textPanel indnt2">
		<h4><c:out value="${msgs.table_header_owner}"/></h4>
		<div class="textPanel">
			<c:out value="${template.owner.displayName}"/>
		</div>
	</div>	
	<h3><c:out value="${msgs.table_header_content}"/></h3>

	<c:forEach var="itemDefinition" items="${template.itemDefinitions}" varStatus="loopCounter">
		<div class="textPanel indnt2">
			<h4><c:out value="${itemDefinition.title}"/></h4>
			<div class="textPanel indnt1">
				<h5><c:out value="${msgs.table_row_description}"/></h5>
				<c:choose>
					<c:when test="${not empty itemDefinition.description}">
						<div class="textPanel"><c:out value="${itemDefinition.description}"/></div>
					</c:when>
					<c:otherwise>
						<div class="textPanel"><c:out value="${msgs.preview_item_nodescription}" /></div>
					</c:otherwise>
				</c:choose>
				<h5><c:out value="${msgs.table_row_type}"/></h5>
				<div class="textPanel">
					<c:out value="${itemDefinition.type}"/>
					<c:if test="${itemDefinition.hasMimeTypes}">
						<c:forEach var="mimeType" items="${itemDefinition.mimeTypes}" varStatus="loopCounter2">
							<c:if test="${loopCounter2.index == 0}">( </c:if>
							<c:if test="${loopCounter2.index > 0}">, </c:if>
							<c:out value="${mimeType}"/>
						</c:forEach>
						)
					</c:if>
				</div>
			</div>
		</div>	
	</c:forEach>
</div>
