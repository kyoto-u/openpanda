<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


    <h3><osp:message key="submit_cell_confirmation"/></h3>
   
      <div class="alertMessage"><c:out value="${msgs.instructions_paragraph1}"/></div>
	  <h4><c:out value="${page.pageDefinition.title}"/></h4>
       <div class="textPanel"><c:out value="${page.pageDefinition.description}" escapeXml="false"/></div>
<form>
   <p class="act">
      <input type="hidden" name="page_id" value="<c:out value="${page_id}" />">
      <input type="submit" name="submit" value="<osp:message key="button_submitCell"/>" class="active" accesskey="s">
      <input type="submit" name="cancel" value="<osp:message key="button_cancel"/>" accesskey="x">
   </p>
</form>