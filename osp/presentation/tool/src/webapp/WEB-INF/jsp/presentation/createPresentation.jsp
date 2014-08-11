<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<form method="post" name="wizardform" action="createPresentation.osp" onsubmit="return true;">
<osp:form/>

<%-- The model for this JSP consists of:
  * A CreatePresentationCommandBean bound as "command"
  * A map containing two elements:
    1. availableTemplates, a List<PresentationTemplate> of templates available for use
    2. freeFormTemplateId, the singleton Id referring to the placeholder template for free-form portfolios
--%>

<spring:nestedPath path="command">
<spring:bind path="presentationType">
	<input id="presType" type="hidden" name="${status.expression}"/>
</spring:bind>
<c:set var="showCreate" value="${freeFormEnabled || not empty availableTemplates}" />

<div class="presentationTypeDialog">
	<h3><c:out value="${msgs.new_portfolio_enterName}"/></h3>
	<p class="messageInstruction indnt2">
		<c:out value="${msgs.new_portfolio_enterNameInstructions}"/>
	</p>
	<p class="indnt2">
		<spring:bind path="presentationName">
			<input type="text" size="40" name="${status.expression}" value="${status.value}"/>
		</spring:bind>
	</p>
    <%-- In case we get here without any available types, which should typically not happen due to links being supressed --%>
    <c:choose>
        <c:when test="${showCreate}">
            <h3><c:out value="${msgs.heading_createPresentation}"/></h3>
        </c:when>
        <c:otherwise>
            <h3><c:out value="${msgs.heading_createUnavailable}"/></h3>
        </c:otherwise>
    </c:choose>
	<spring:bind path="*">
		<c:if test="${status.error}">
			<%-- FIXME: This needs an appropriate class for error msg --%>
			<div class="messageValidation">
				<c:out value="${status.errorMessage}"/>
			</div>
		</c:if>
	</spring:bind>
	<spring:bind path="templateId">
		<c:if test="${not empty availableTemplates}">
			<ul class="presentationTypeGroup">
				<c:forEach var="template"
					items="${availableTemplates}"
					varStatus="templateStatus">
					<li class="portfolioTypeOption">
						<input type="radio"
							id="${status.expression}-${templateStatus.count}"
							name="${status.expression}"
							value="<c:out value="${template.id.value}"/>"
							onclick="getElementById('presType').value = 'osp.presentation.type.template';" />
						<label for="${status.expression}-${templateStatus.count}"><c:out value="${template.name}"/></label>
						<p class="messageInstruction">
							<c:out value="${template.description}"/>
						</p>
					</li>
				</c:forEach>
			</ul>
		</c:if>
		
		<%-- Handle option to turn free-form off --%>
		<c:if test="${freeFormEnabled}">
			<ul class="presentationTypeGroup">
				<li class="portfolioTypeOption">
					<input type="radio"
						id="${status.expression}-freeForm"
						name="${status.expression}"
						value="${freeFormTemplateId.value}"
						onclick="getElementById('presType').value = 'osp.presentation.type.freeForm';" />
					<label for="${status.expression}-freeForm"><c:out value="${msgs.label_freeForm}"/></label>
					<p class="messageInstruction">
						<c:out value="${msgs.addPresentation1_manageYourself}"/>
					</p>
				</li>
			</ul>
		</c:if>
	</spring:bind>
    <c:if test="${!showCreate}">
        <div class="presentationTypeGroup">
            <p style="text-align: center;"><c:out value="${msgs.no_portfolio_types}" /></p>
            <p style="text-align: center;"><a href="<osp:url value="listPresentation.osp" />"><c:out value="${msgs.return_to_list}" /></a></p>
        </div>
    </c:if>
</div>

<c:if test="${showCreate}">
<div class="act">
	<input type="submit" name="submit" value="<c:out value="${msgs.button_create}"/>" /> <input type="submit" name="cancel" value="<c:out value="${msgs.button_cancel}"/>" />
</div>
</c:if>
</spring:nestedPath>
</form>
