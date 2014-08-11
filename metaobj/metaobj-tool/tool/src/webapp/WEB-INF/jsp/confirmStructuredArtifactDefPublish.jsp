<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "messages"/>

<form method="POST" action="confirmSADPublish.osp">
<osp:form/>

<spring:bind path="bean.id">
<input type="hidden" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>" />
</spring:bind>

   <spring:hasBindErrors name="bean">
<div class="alertMessage">
   <c:forEach items="${errors.allErrors}" var="error">
      <spring:message message="${error}" htmlEscape="true"/>
   </c:forEach>
</div>
   </spring:hasBindErrors>

<h3><fmt:message key="legend_confirm_publish"/></h3>

<div class="instruction">
<spring:bind path="bean.action">
	<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>"/>
	<c:choose>
	<c:when test="${status.value == 'site_publish'}">
	<fmt:message key="confirm_publish"/>
	</c:when>
	<c:when test="${status.value == 'global_publish'}">
	<fmt:message key="confirm_globalPublish"/>
	</c:when>
	<c:when test="${status.value == 'suggest_global_publish'}">
	<fmt:message key="confirm_requestGlobalPublish"/>
	</c:when>
	</c:choose>
</spring:bind>

<spring:bind path="bean.description">
   <c:if test="${status.error}">
      <p class="shorttext">
         <span class="reqStar">*</span><label><fmt:message key="label_newName"/></label>
         <input type="text" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.value}"/>"/>
      </p>
   </c:if>
</spring:bind>


<p class="act">
<input name="publish" type="submit" value="<fmt:message key="button_yes"/>" accesskey="s"  class="active"/>
<input name="_cancel" type="submit" value="<fmt:message key="button_no"/>" accesskey="x" />
</p>

</form>

</div>
