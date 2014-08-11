<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set var="targetPrevious" value="_target2" />


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<h3><c:out value="${msgs.title_addTemplate4}"/></h3>

<%@ include file="/WEB-INF/jsp/presentation/wizardHeader.inc"%>

<form method="post" action="addTemplate.osp"><osp:form /> <input
    type="hidden" name="templateId"
    value="<c:out value="${template.id}"/>" /> <input type="hidden"
    name="pickerField" value="" /> <input type="hidden" name="validate"
    value="true" />

    <spring:bind path="template.fileRef.action">
        <input type="hidden" id="<c:out value="${status.expression}"/>"
            name="<c:out value="${status.expression}"/>" value="" />
    </spring:bind>

    <div class="instruction">
        <c:out value="${msgs.instructions_template_new4}"/>
    </div>
	<div class="highlightPanel actionitem">
		<spring:bind path="template.fileRef.usage">
			<c:if test="${status.error}">
				<p class="shorttext validFail" style="border:none">
			</c:if>	
			<c:if test="${!status.error}">
				<p class="shorttext" style="border:none">
			</c:if>
			<span class="reqStar">*</span>
			<label for="<c:out value="${status.expression}"/>-id"><c:out value="${msgs.label_nameUsedInXpath}"/></label>
			<input type="text"
				name="<c:out value="${status.expression}"/>"
				id="<c:out value="${status.expression}"/>-id"
				value="<c:out value="${status.value}"/>" />
			<c:if test="${status.error}">
				<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}" /></span>
			</c:if>

			</p>
		</spring:bind>
    
		<spring:bind path="template.fileRef.fileId">
			<c:if test="${status.error}">
				<p class="shorttext validFail" style="border:none">
			</c:if>	
			<c:if test="${!status.error}">
				<p class="shorttext" style="border:none">
			</c:if>
		</spring:bind>
			<span class="reqStar">*</span>
			<label><c:out value="${msgs.label_chooseFile}"/></label>
			<spring:bind path="template.fileRef.artifactName">
				<input type="text" id="fileName" disabled="disabled"
					value="<c:out value="${status.value}" />" />
			</spring:bind>
			<spring:bind path="template.fileRef.fileId">
				<input type="hidden"
					name="<c:out value="${status.expression}"/>"
					id="<c:out value="${status.expression}"/>"
					value="<c:out value="${status.value}" />" />
				<!--<input type="hidden" name="_target4" id="_target4" value="" /> -->
				<input type="hidden" name="returnPage" id="returnPage"
					value="<c:out value="${currentPage-1}"/>" />
				<a href="javascript:callPicker('<c:out value="${TEMPLATE_SUPPORTFILE}"/>');">
				<c:out value="${msgs.action_pickFile}"/> </a>
	
				<script type="text/javascript">
					function callPicker(pickerField) {
					
						//document.write("<input type='hidden' name='_target4' id='_target4' value='true' />");
						document.getElementById('insertTarget').innerHTML="<input type='hidden' name='_target4' id='_target4' value='true' />"
						document.forms[0].pickerField.value=pickerField;
						document.forms[0].validate.value='false';
						document.forms[0].submit()
					}
				</script>
			</spring:bind>
			<spring:bind path="template.fileRef.fileId">
			<c:if test="${status.error}">
				<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}" /></span>
			</c:if>	
		</spring:bind>
		</p>
      </div>  


    <p class="act" style="margin:0;padding:.5em 0">
        <c:choose>
            <c:when test="${param.editFile}">
                <input type="submit" name="_target3" value="<c:out value="${msgs.button_saveEdit}"/>"
                    onclick="setElementValue(<spring:bind path="template.fileRef.action">'<c:out value="${status.expression}"/>'</spring:bind>,'addFile');return true;"  class="active" />
            </c:when>
            <c:otherwise>
                <input type="submit" name="_target3" value="<c:out value="${msgs.button_addToList}"/>"
                    onclick="setElementValue(<spring:bind path="template.fileRef.action">'<c:out value="${status.expression}"/>'</spring:bind>,'addFile');return true;" />
            </c:otherwise>
        </c:choose>
    </p>


    <c:choose>
    	<c:when test="${template.files['empty']}">
		<p class="instruction"><c:out value="${msgs.addTemplate_thereAreNoSupportingFiles}"/></p>
	</c:when>
	<c:otherwise>
		<table class="listHier lines nolines" cellspacing="0" cellpadding="0" border="0" style="width:auto" summary="<c:out value="${msgs.table_addTemplate3_summary}"/>">
		<thead>
		    <tr>
			<th scope="col"><c:out value="${msgs.table_header_fileName}"/></th>
			<th scope="col"></th>
			<th scope="col"><c:out value="${msgs.table_header_fullXpath}"/></th>
		    </tr>
		</thead>
		<tbody>
		    <c:if test="${not template.files['empty']}">
			<c:forEach var="file" items="${template.files}">
			    <tr>
				<td><c:out value="${file.artifactName}" /></td>
				<td class="itemAction">
					<a href="<osp:url value="editTemplateFile.osp"/>&id=<c:out value="${file.id.value}" />"><c:out value="${msgs.action_edit}"/></a>
					|
					<a href="<osp:url value="deleteTemplateFile.osp"/>&id=<c:out value="${file.id.value}" />"><c:out value="${msgs.action_delete}"/></a>
				</td>
				<td>/ospiPresentation/presentationFiles/<c:out
				    value="${file.usage}" /></td>
			    </tr>
			</c:forEach>
		    </c:if>
		</tbody>
	    </table>
	   </c:otherwise>
	  </c:choose>
<c:set var="suppress_next" value="true" />
<c:set var="suppress_submit" value="true" />
<%@ include file="/WEB-INF/jsp/presentation/wizardFooter.inc"%>
<p id="insertTarget" />
</form>
