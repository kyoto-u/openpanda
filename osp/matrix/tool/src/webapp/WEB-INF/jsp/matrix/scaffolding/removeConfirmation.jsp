<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<h3>
   <c:out value="${msgs.title_remove}"/>
</h3>
   
<div class="alertMessage">
   <c:out value="${msgs.text_AreYouSureRemove}"/>
</div>

<form method="post">
   <div class="act">
      <input name="continue" type="submit" value="<c:out value="${msgs.button_continue}" />" class="active" accesskey="s" />
      <input name="cancel" type="submit" value="<c:out value="${msgs.button_cancel}" />"  accesskey="x" />
   </div>
</form>