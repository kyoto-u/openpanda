<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>
<osp-p:renderPresentation 
 template="<%= request.getAttribute(\"renderer\") %>" 
 doc="<%= request.getAttribute(\"document\") %>"
 uriResolver="<%= request.getAttribute(\"uriResolver\") %>" />
<osp-c:authZMap prefix="osp.presentation." qualifier="${presentation.id}" var="isAuthorizedTo" />


<c:if test="${presentation.allowComments}">
  <c:choose>
    <c:when test="${presentation.preview}">
      <br />
      <h3><c:out value="${msgs.comments_placeholder}" /></h3>
    </c:when>
    <c:otherwise>
      <%@ include file="/WEB-INF/jsp/presentation/comments.inc" %>
    </c:otherwise>
  </c:choose>
</c:if>
