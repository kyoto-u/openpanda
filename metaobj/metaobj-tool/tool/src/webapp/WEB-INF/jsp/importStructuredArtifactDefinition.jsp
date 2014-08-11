<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename="messages" var="msgs" />

<form  method="POST">
    <osp:form/>
    <input type="hidden" name="submitAction" id="submitAction" value="" />
    <input type="hidden" name="validate" value="true" />
    
    <h3><osp:message key="metaobj.import.title" bundle="${msgs}" /></h3>
    
    <spring:bind path="bean.uploadedForm">
        <c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        </c:if>
        <p class="shorttext">
            <label><osp:message key="metaobj.import.importTheseFiles" bundle="${msgs}" /></label>
            <input type="text" id="name" disabled="true"
                value="<c:out value="${name}"/>" />
            <input type="hidden" name="uploadedForm" id="uploadedForm"
                value="<c:out value="${status.value}"/>" />
           <a href="javascript:document.forms[0].submitAction.value='pickImport';document.forms[0].validate.value='false';document.forms[0].submit();">
           <osp:message key="metaobj.import.pickFilesToImport" bundle="${msgs}" /></a>
        </p>
    </spring:bind>
    
    <br/>
    
    <div class="act">
      <input type="submit" value="<osp:message key="metaobj.import.importButton" bundle="${msgs}" />" alignment="center" class="active" accesskey="s"> 
      <input type="button" value="<osp:message key="metaobj.import.cancelButton" bundle="${msgs}" />" onclick="window.document.location='<osp:url value="listStructuredArtifactDefinitions.osp"/>'" accesskey="x">
    </div>

</form>







