                                                                            <%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<form method="post" action="addLayout.osp">
    <osp:form />

    <input name="filePickerAction" id="filePickerAction" type="hidden" value="" />
    <input type="hidden" name="validate" value="true" />
    <spring:bind path="layout.id">
        <input type="hidden" name="layout_id" value="<c:out value="${status.value}"/>" />
    </spring:bind>
<c:choose>
    <c:when test="${empty layout.id}">
        <h3><c:out value="${msgs.title_addLayout}"/></h3>
    </c:when>
    <c:otherwise>
        <h3><c:out value="${msgs.title_editLayout}"/></h3>
    </c:otherwise>
</c:choose>
    <p class="instruction">
        <c:out value="${msgs.instructions_addLayout}"/>
        <c:out value="${msgs.instructions_requiredFields}" escapeXml="false"/>
    </p>
    
    
<spring:bind path="layout.name">
  		<c:if test="${status.error}">
			<p class="shorttext validFail">
		</c:if>
  		<c:if test="${!status.error}">
			 <p class="shorttext">
		</c:if>
			<label for="<c:out value="${status.expression}"/>-id" ><span class="reqStar">*</span><c:out value="${msgs.label_displayName}"/></label>
            <input type="text" name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>-id" 
                     value="<c:out value="${status.value}"/>" 
                  size="25" maxlength="25" <c:out value="${disabledText}"/> />
			  <c:if test="${status.error}">
					<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
				</c:if>
				  </p>
        </spring:bind>
      
      <spring:bind path="layout.description">
 		 <p class="longtext">
        	<label class="block" for="descriptionTextArea"><c:out value="${msgs.label_description}"/>
			</label>
			<textarea name="<c:out value="${status.expression}"/>" id="descriptionTextArea" rows="5" cols="80" 
                   <c:out value="${disabledText}"/>><c:out value="${status.value}"/></textarea>
         </p>
      </spring:bind>    

    <spring:bind path="layout.xhtmlFileId">
		<c:if test="${status.error}">
			<p class="shorttext validFail">
		</c:if>
  		<c:if test="${!status.error}">
			 <p class="shorttext">
		</c:if>
            <label for="xhtmlFileName"><span class="reqStar" >*</span><c:out value="${msgs.label_XHTMLLayoutFile}"/></label>
            <input type="text" id="xhtmlFileName" disabled="disabled"
                value="<c:out value="${xhtmlFileName}"/>" />
            <input type="hidden" name="xhtmlFileId" id="xhtmlFileId"
                value="<c:out value="${status.value}"/>" />
            <a href="javascript:document.forms[0].filePickerAction.value='<c:out value="${XHTML_FILE}"/>';document.forms[0].validate.value='false';document.forms[0].submit();">
            <c:out value="${msgs.label_pickFile}"/> </a>
				<c:if test="${status.error}">
					<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
				</c:if>
			</p>	
    </spring:bind>

    <spring:bind path="layout.previewImageId">
        <p class="shorttext">
            <label for="previewImageName"><c:out value="${msgs.label_previewImage}"/></label>
            <input type="text" id="previewImageName" disabled="disabled"
                value="<c:out value="${previewImageName}"/>" />
            <input type="hidden" name="previewImageId" id="previewImageId"
                value="<c:out value="${status.value}"/>" />
            <a href="javascript:document.forms[0].filePickerAction.value='<c:out value="${PREVIEW_IMAGE}"/>';document.forms[0].validate.value='false';document.forms[0].submit();">
                <c:out value="${msgs.label_pickFile}"/> </a>
				</p>
    </spring:bind>

    
   <div class="act">

       <c:choose>
    <c:when test="${empty layout.id}">
        <input type="submit" name="save" class="active" value="<c:out value="${msgs.button_submit}"/>"
            onclick="javascript:document.forms[0].validate.value='true';" accesskey="s" />
    </c:when>
    <c:otherwise>
        <input type="submit" name="save" class="active" value="<c:out value="${msgs.button_saveEdit}"/>"
            onclick="javascript:document.forms[0].validate.value='true';" accesskey="s" />
    </c:otherwise>
</c:choose>

      <input type="button" name="cancel" value="<c:out value="${msgs.button_cancel}"/>"
            onclick="window.document.location='<osp:url value="listLayout.osp"/>'"  accesskey="x" />
   </div>

</form>


