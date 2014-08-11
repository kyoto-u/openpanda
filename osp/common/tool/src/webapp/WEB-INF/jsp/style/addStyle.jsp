<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.common.bundle.Messages"/></jsp:useBean>

<form method="post" action="addStyle.osp">
    <osp:form />

    <input name="filePickerAction" id="filePickerAction" type="hidden" value="" />
    <input type="hidden" name="validate" value="true" />
    <spring:bind path="style.id">
        <input type="hidden" name="style_id" value="<c:out value="${status.value}"/>" />
    </spring:bind>

    <c:if test="${empty style.id}">
        <h3><c:out value="${msgs.title_addStyle}"/></h3>
        <p class="instruction">
    </c:if>
    <c:if test="${not empty style.id}">
        <h3><c:out value="${msgs.title_editStyle}"/></h3>
        <p class="instruction">
    </c:if>
        <c:out value="${msgs.instructions_requiredFields}" escapeXml="false"/>
    </p>
    
    
  <spring:bind path="style.name">
  		<c:if test="${status.error}">
			<p class="shorttext validFail">
		</c:if>			
		<c:if test="${!status.error}">
			<p class="shorttext">
		</c:if>	
		<span class="reqStar">*</span><label for="<c:out value="${status.expression}"/>-id"><c:out value="${msgs.label_displayName}"/></label>
		<input type="text" name="<c:out value="${status.expression}"/>"  id="<c:out value="${status.expression}"/>-id" 
				 value="<c:out value="${status.value}"/>" 
			  size="25" maxlength="25" <c:out value="${disabledText}"/> />
		  <c:if test="${status.error}">
               <span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></div>
            </c:if>
	  
	 </p>
        </spring:bind>
      
      <spring:bind path="style.description">
      	<c:if test="${status.error}">
            <div class="validation"><c:out value="${status.errorMessage}" /></div>
        	</c:if>
      	<div class="longtext">
         	<label class="block" for="descriptionTextArea"><c:out value="${msgs.label_description}"/></label>
			<textarea name="<c:out value="${status.expression}"/>" id="descriptionTextArea" rows="5" cols="80" 
			<c:out value="${disabledText}"/>><c:out value="${status.value}"/></textarea>
      	</div>
    </spring:bind>

    <spring:bind path="style.styleFile">
		<c:if test="${status.error}">
			<p class="shorttext validFail">
		</c:if>			
		<c:if test="${!status.error}">
			<p class="shorttext">
		</c:if>	
		<span class="reqStar">*</span>
		<label  for="styleFileName"><c:out value="${msgs.label_styleFile}"/></label>
		<input type="text" id="styleFileName" disabled="disabled"
			value="<c:out value="${styleFileName}"/>" />
		<input type="hidden" name="styleFile" id="styleFile"
			value="<c:out value="${status.value}"/>" />
		<a href="javascript:document.forms[0].filePickerAction.value='<c:out value="${STYLE_FILE}"/>';document.forms[0].validate.value='false';document.forms[0].submit();" title='<c:out value="${msgs.title_pickFile}"/>'>
		<c:out value="${msgs.label_pickFile}"/> </a>
		<c:if test="${status.error}">
			<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}" /></span>
		</c:if>
		</p>	
    </spring:bind>

    
   <div class="act">

      <input type="submit" name="save" class="active" accesskey="s"
      <c:if test="${empty style.id}">
             value="<c:out value="${msgs.button_submit}"/>"
      </c:if>
      <c:if test="${not empty style.id}">
             value="<c:out value="${msgs.button_submitEdit}"/>"
      </c:if>
            onclick="javascript:document.forms[0].validate.value='true';" />
      <input type="button" name="cancel" value="<c:out value="${msgs.button_cancel}" />"
            onclick="window.document.location='<osp:url value="listStyle.osp"/>'" accesskey="x" />
   </div>

</form>
