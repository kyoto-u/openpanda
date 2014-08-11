<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<h3>
  <c:out value="${msgs.title_saveMatrix}"/>
</h3>
  
<c:if test="${published}">
	<div class="alertMessage" >
	<fmt:message key="text_areYouSureEdit">
	  <fmt:param><c:out value="${label}"/></fmt:param>
	</fmt:message>
	
	
	<c:if test="${changedCellsSize > 0}">
		<p>
		The following cells have submissions for the form(s) you removed and will no longer use the default forms:
		<ul>
		<c:forEach var="cellName" items="${changedCells}" varStatus="loopCount">
			<li><c:out value="${cellName}"/></li>
		</c:forEach>
		</ul>
		</p>
	</c:if>
	</div>
</c:if>
<c:if test="${warnViewAllGroupsEval}">
	<div class="alertMessage" >
		<fmt:message key="confirm_evalGroup">
		  <fmt:param><c:out value="${label}"/></fmt:param>
		</fmt:message>
	</div>
</c:if>
<form method="post">

<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
<div class="act">
<input name="continue" type="submit" value="<osp:message key="button_continue"/>" class="active" accesskey="s" />
<input name="cancel" type="submit" value="<osp:message key="button_cancel"/>" accesskey="x" />
</div>
</form>