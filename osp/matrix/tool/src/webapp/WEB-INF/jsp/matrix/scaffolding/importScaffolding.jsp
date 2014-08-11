<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<form  method="post">
	<osp:form/>
   <input name="formAction" type="hidden" value="import" />

	<h3><c:out value="${msgs.title_importScaffolding}"/></h3>
		
	<spring:bind path="uploadForm.uploadedScaffolding">
		<c:if test="${status.error}">
			<p class="shorttext validFail">
		</c:if>	
		<c:if test="${!status.error}">
			<p class="shorttext">
		</c:if>			
            <label for="name"><c:out value="${msgs.label_importFile}"/></label>
			<input type="text" id="name" disabled="disabled" value="<c:out value="${uploadForm.scaffoldingFileName}"/>" />
			<input type="hidden" name="uploadedScaffolding" id="uploadedScaffolding" value="<c:out value="${status.value}"/>" />
         <a href="javascript:document.forms[0].formAction.value='filePicker';document.forms[0].submit();"
            title='<c:out value="${msgs.action_chooseFile_title}"/>' >
				<c:out value="${msgs.action_chooseFile}"/>
			</a>
			<c:if test="${status.error}">
			   <span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
			</c:if>
		</p>
	</spring:bind>
	
	<div class="act">
		<input class="active" type="submit" value="<osp:message key="button_importScaffolding"  />" accesskey="s" /> 
      <input type="button" value="<osp:message key="button_cancel"/>" onclick="window.document.location='<osp:url value="listScaffolding.osp"/>'" accesskey="x"  />
	</div>

</form>