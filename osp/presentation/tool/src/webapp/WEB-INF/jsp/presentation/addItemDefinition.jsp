<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<jsp:useBean id="msg" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msg" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<script>
    function displayMimeTypeSelection(selectBox, divName)
    {
       var divElement = ospGetElementById(divName);
       var selectedIndex = selectBox.selectedIndex;
    
       //do nothing if nothing is selected
       if (selectedIndex == -1 || undefined == selectedIndex){
          return false;
       }
    
       var value = selectBox.options[selectedIndex].value;
    
       if (value == 'fileArtifact'){
//          divElement.style.height="<c:out value="${mimeTypeListSize * 20}"/>px";
          divElement.style.display="block";
       } else {
   //       divElement.style.height="0px";
          divElement.style.display="none";
       }
       resetHeight();
    }
</script>

<spring:bind path="template.item.id">
    <input type="hidden" name="<c:out value="${status.expression}"/>"
        value="<c:out value="${status.value}"/>" />
</spring:bind>

<input type="hidden" name="templateId" value="<c:out value="${template.id}"/>" />

<spring:bind path="template.item.action">
    <input type="hidden" id="<c:out value="${status.expression}"/>"
        name="<c:out value="${status.expression}"/>" value="" />
</spring:bind>
<div class="highlightPanel actionitem">
	<spring:bind path="template.item.type">
			<c:if test="${status.error}">
				<p class="shorttext validFail" style="border:none">
			</c:if>	
			<c:if test="${!status.error}">
				<p class="shorttext"  style="border:none">
			</c:if>
	
			<span class="reqStar">*</span>
			<label for="<c:out value="${status.expression}"/>"><c:out value="${msg.label_type}"/></label>
			<select id="<c:out value="${status.expression}"/>"
					name="<c:out value="${status.expression}"/>"
					onchange='displayMimeTypeSelection(this,"mimeTypeSelection")'>
				<option value=""><c:out value="${msg.addItemDef_pleaseSelectaType}"/></option>
				<option value="">- - - - - - - - - - - - - - - - - - - - -</option>
				<c:forEach var="home" items="${homes}">
					<c:if test="${!home.type.systemOnly}">
						<option
							<c:if test="${status.value == home.type.id.value}">selected="selected"</c:if>
							value="<c:out value="${home.type.id.value}"/>"><c:out
							value="${home.type.description}" /></option>
					</c:if>
				</c:forEach>
			</select>
			<c:if test="${status.error}">
				<span  class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}" /></span>
			</c:if>
		</p>
	
	</spring:bind>
	
	<spring:bind path="template.item.name">
			<c:if test="${status.error}">
				<p class="shorttext validFail" style="border:none">
			</c:if>	
			<c:if test="${!status.error}">
				<p class="shorttext" style="border:none">
			</c:if>
			<span class="reqStar">*</span>
			<label for="<c:out value="${status.expression}"/>-id"><c:out value="${msg.label_name}"/></label>
			<input type="text"
				id="<c:out value="${status.expression}"/>-id"
				name="<c:out value="${status.expression}"/>"
				value="<c:out value="${status.value}"/>" />
			<c:if test="${status.error}">
				<span  class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}" /></span>
			</c:if>
		</p>
	</spring:bind>
	
	<spring:bind path="template.item.title">
			<c:if test="${status.error}">
				<p class="shorttext validFail" style="border:none">
			</c:if>	
			<c:if test="${!status.error}">
				<p class="shorttext" style="border:none">
			</c:if>
			<span class="reqStar">*</span>
			<label for="<c:out value="${status.expression}"/>-id"><c:out value="${msg.label_title}"/></label>
			<input type="text"
				name="<c:out value="${status.expression}"/>"
				id="<c:out value="${status.expression}"/>-id"
				value="<c:out value="${status.value}"/>" />
				<c:if test="${status.error}">
					<span  class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}" /></span>
				</c:if>
			</p>
	
	</spring:bind>
	
	<spring:bind path="template.item.description">
		<c:if test="${status.error}">
			<div class="validation"><c:out value="${status.errorMessage}" /></div>
		</c:if>
		<p class="longtext" style="border:none">
			<label class="block" for="<c:out value="${status.expression}"/>-id">
				<c:out value="${msg.label_description}"/>
				<c:if test="${status.error}">
					<span  class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}" /></span>
				</c:if>
			</label>
			<textarea cols="80" rows="5" name="<c:out value="${status.expression}"/>"  id="<c:out value="${status.expression}"/>-id"><c:out
				value="${status.value}" /></textarea>
		</p>
	</spring:bind>
	
	<spring:bind path="template.item.allowMultiple">
		<h4><c:out value="${msg.legend_AllowMultipleSelection}"/></h4>
		<div class="checkbox indnt1" style="border:none">
			<input type="radio" id="multiYes"
				name="<c:out value="${status.expression}"/>" value="true"
				<c:if test="${status.value == true}">checked="checked"</c:if> />
			<label for="multiYes"><c:out value="${msg.label_yes}"/></label>
		</div>
		<div class="checkbox indnt1" style="border:none">
			<input type="radio" id="multiNo"
				name="<c:out value="${status.expression}"/>" value="false"
				<c:if test="${status.value == false}">checked="checked"</c:if> />
			<label for="multiNo"><c:out value="${msg.label_no}"/></label>
		</div>
	</spring:bind>
	
	
	<spring:bind path="template.item.mimeTypes">
	
		<div style="display:none" id="mimeTypeSelection">
					<h4><c:out value="${msg.label_limitToTheseMimeTypes}"/></h4>
	
					<c:forEach var="mimeType" items="${mimeTypeList}">
						<p class="checkbox indnt1" style="border:none">
							<input type="checkbox"
								name="<c:out value="${status.expression}"/>"
								id="<c:out value="${mimeType}"/>-id"
								<c:forEach var="next" items="${template.item.mimeTypes}"><c:if test="${mimeType eq next.value}">checked="checked"</c:if></c:forEach>
								value="<c:out value="${mimeType}"/>" />
							<label for="<c:out value="${mimeType}"/>-id"><c:out value="${mimeType}" /></label>
						</p>
					</c:forEach>
	
		</div>
	</spring:bind>
</div>

    <p class="act" style="margin:0;padding:.5em">
        <c:choose>
            <c:when test="${param.editItem}">
                <input type="submit" name="_target2" value="<c:out value="${msg.button_saveEdit}"/>"
                    onclick="setElementValue(<spring:bind path="template.item.action">'<c:out value="${status.expression}"/>'</spring:bind>,'addItem');return true;" class="active"/>
            </c:when>
            <c:otherwise>
                <input type="submit" name="_target2" value="<c:out value="${msg.button_addToList}"/>"
                    onclick="setElementValue(<spring:bind path="template.item.action">'<c:out value="${status.expression}"/>'</spring:bind>,'addItem');return true;" />
            </c:otherwise>
        </c:choose>
    </p>

<spring:bind path="template.item.type">
    <script type="text/javascript">
displayMimeTypeSelection(ospGetElementById("<c:out value="${status.expression}"/>"),"mimeTypeSelection");
</script>
</spring:bind>
