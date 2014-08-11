<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.glossary.bundle.Messages"/></jsp:useBean>

    <h3>
        <c:if test="${not empty entry.id}"><c:out value="${msgs.title_editGlossaryTerm}"/></c:if>
        <c:if test="${empty entry.id}"><c:out value="${msgs.title_addGlossaryTerm}"/></c:if>
    </h3>
   

    <p class="instructions">
        <c:out value="${msgs.instructions_addGlossaryTerm}" escapeXml="false" />
    </p>
    <spring:hasBindErrors name="entry">
        <div class="validation"><c:out value="${msgs.error_add}"/></div>
    </spring:hasBindErrors>


<form method="post" name="editTermForm" action="<c:out value="${action}"/>" > 
    <osp:form/>
    
    <spring:bind path="entry.term">

       <c:if test="${status.error}">
	   		<p class="shorttext validFail">
        </c:if>
		<c:if test="${!status.error}">
	           <p class="shorttext">
        </c:if>
            <label for="term-id">
	            <span class="reqStar">*</span>            	
                <c:out value="${msgs.label_Term}"/> <span class="textPanelFooter"> <c:out value="${msgs.label_Term_hint}"/></span>
            </label>
            <input type="text" name="term" id="term-id" 
                   value="<c:out value="${status.value}"/>" 
                   size="47" maxlength="255"
            />
			<c:if test="${status.error}">
				<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
			</c:if>	

        </p>
    </spring:bind>
    
    <spring:bind path="entry.description">
       <c:if test="${status.error}">
	   		<div class="longtext validFail">
        </c:if>
		<c:if test="${!status.error}">
	           <div class="longtext">
        </c:if>

            <label class="block" for="description-id">
            	<span class="reqStar">*</span>
                <c:out value="${msgs.label_shortDesc}"/><span class="textPanelFooter"> <c:out value="${msgs.label_desc_hint}"/></span>
				<c:if test="${status.error}">
					<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
				</c:if>	
            </label>
            <c:set var="item" value="${status.value}"/>
            <textarea rows="3" name="description"  id="description-id" 
                onkeyup="limitChar(this,255)"  cols="80"
            ><c:out value="${item}"/></textarea>
        </div>
    </spring:bind>
    
    <spring:bind path="entry.longDescription">
		   <c:if test="${status.error}">
				<div class="longtext validFail">
			</c:if>
			<c:if test="${!status.error}">
				   <div class="longtext">
			</c:if>
            
            <label class="block">
            	<span class="reqStar">*</span>
                <c:out value="${msgs.label_longDesc}"/>
				<c:if test="${status.error}">
					<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
				</c:if>	

            </label>
            <c:set var="item" value="${status.value}"/>
            <table><tr>
            <td><textarea id="longDescription" name="longDescription" rows="25" cols="105"><c:out value="${item}"/></textarea></td>
            </tr></table>
        </div>
    </spring:bind>

    
    <input type="hidden" name="worksiteId" value="<c:out value="${entry.worksiteId}" />"/>
    
   <osp:richTextWrapper textAreaId="longDescription" />
    
    <div class="act">
        <c:if test="${not empty entry.id}">
            <input type="submit" name="submitButton" class="active" value="<c:out value="${msgs.button_submitEdit}"/>"  accesskey="s" />
        </c:if>
        <c:if test="${empty entry.id}">
            <input type="submit" name="submitButton" class="active" value="<c:out value="${msgs.button_submitAdd}"/>"  accesskey="s"  />
        </c:if>

        <input type="button" value="<c:out value="${msgs.button_cancel}"/>" onclick="window.document.location='<osp:url value="glossaryList.osp"/>'"  accesskey="x" />
    </div>
<script type="text/javascript"><!--
	document.editTermForm.term.focus();
--></script>
</form>
