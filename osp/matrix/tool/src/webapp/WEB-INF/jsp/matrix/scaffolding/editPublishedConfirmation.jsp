<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<h3>
  <c:out value="${msgs.title_saveMatrix}"/>
</h3>
  
<div class="alertMessage" >
<c:out value="${msgs.text_areYouSureEdit}"/>



<c:if test="${changedCellsSize > 0}">
	<p>
   <c:out value="${msgs.text_cell_submissions}"/>
	<ul>
	<c:forEach var="cellName" items="${changedCells}" varStatus="loopCount">
		<li><c:out value="${cellName}"/></li>
	</c:forEach>
	</ul>
</c:if>
</div>

<form method="post">

<input type="hidden" name="<c:out value="${isInSession}"/>" value="true"/>
<div class="act">
<input name="continue" type="submit" value="<osp:message key="button_continue"/>" class="active" accesskey="s" />
<input name="cancel" type="submit" value="<osp:message key="button_cancel"/>" accesskey="x" />
</div>
</form>