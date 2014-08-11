<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<form  method="POST">
    <osp:form/>
    <input type="hidden" name="submitAction" id="submitAction" value="" />
    <input type="hidden" name="validate" value="true" />
    
    <h3><c:out value="${msgs.title_importTemplate}"/></h3>
    
    <spring:bind path="uploadForm.uploadedTemplate">
	<c:if test="${status.error}">
		<p class="shorttext validFail" style="border:none">
	</c:if>	
	<c:if test="${!status.error}">
		<p class="shorttext" style="border:none">
	</c:if>
            <label for="id"><c:out value="${msgs.label_templateFile}"/></label>
            <input type="text" id="name" disabled="disabled"
                value="<c:out value="${name}"/>" />
            <input type="hidden" name="uploadedTemplate" id="uploadedTemplate"
                value="<c:out value="${status.value}"/>" />
           <a href="javascript:document.forms[0].submitAction.value='pickImport';document.forms[0].validate.value='false';document.forms[0].submit();">
           <c:out value="${msgs.instructions_pickFile}"/> </a>
	   <c:if test="${status.error}">
		<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}" /></span>
		</c:if>	
        </p>
    </spring:bind>
    
    <div class="act">
      <input type="submit" value="<c:out value="${msgs.button_importTemplate}"/>" alignment="center" class="active" accesskey="s" /> 
      <input type="button" value="<c:out value="${msgs.button_cancel}"/>" onclick="window.document.location='<osp:url value="listTemplate.osp"/>'" accesskey="x" />
    </div>

</form>