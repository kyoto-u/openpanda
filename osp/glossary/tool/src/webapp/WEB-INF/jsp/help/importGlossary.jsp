<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.glossary.bundle.Messages"/></jsp:useBean>

<form  method="post">
    <osp:form/>
    <input type="hidden" name="submitAction" id="submitAction" value="" />
    <input type="hidden" name="validate" value="true" />
    
    <h3><c:out value="${msgs.osp_help_glossary_importTitle}" /></h3>
    
    <p class="instruction"><c:out value="${msgs.osp_help_glossary_importInstructions}" /></p>
    <spring:bind path="uploadGlossary.uploadedGlossary">
       <c:if test="${status.error}">
	   		<p class="shorttext validFail">
        </c:if>
		<c:if test="${!status.error}">
	           <p class="shorttext">
        </c:if>
         <span class="reqStar">*</span>
            <label for="name"><c:out value="${msgs.osp_help_glossary_importTheseFiles}" /></label>
            <input type="text" id="name" disabled="disabled"
                value="<c:out value="${name}"/>" />
            <input type="hidden" name="uploadedGlossary" id="uploadedGlossary"
                value="<c:out value="${status.value}"/>" />
           <a href="javascript:document.forms[0].submitAction.value='pickImport';document.forms[0].validate.value='false';document.forms[0].submit();">
           <c:out value="${msgs.osp_help_glossary_PickFilesToImport}" /></a>
   			<c:if test="${status.error}">
				<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
			</c:if>	
        </p>
    </spring:bind>
        
    <spring:bind path="uploadGlossary.replaceExistingTerms">
        <p class="instruction"><c:out value="${msgs.osp_help_glossary_whenTermExists}" /></p>
        <p class="checkbox indnt1">
            <input type="radio" name="replaceExistingTerms" id="replaceTerm" value="true" 
                <c:if test="${status.value}">checked="checked"</c:if> />
            <label for="replaceTerm">
                    <c:out value="${msgs.osp_help_glossary_replaceExistingTerm}" />
            </label>
        </p>
        <p class="checkbox indnt1">
            <input type="radio" name="replaceExistingTerms" id="ignoreTerm" value="false" 
                <c:if test="${status.value == false}">checked</c:if> />
            <label for="ignoreTerm">
                    <c:out value="${msgs.osp_help_glossary_ignoreExistingTerm}" />
            </label>
        </p>
        
    </spring:bind>
    
    <div class="act">
      <input type="submit" value="<c:out value="${msgs.osp_help_glossary_importButton}" />" class="active" accesskey="s" /> 
      <input type="button" value="<c:out value="${msgs.osp_help_glossary_cancelButton}" />" onclick="window.document.location='<osp:url value="glossaryList.osp"/>'" accesskey="x" />
    </div>

</form>