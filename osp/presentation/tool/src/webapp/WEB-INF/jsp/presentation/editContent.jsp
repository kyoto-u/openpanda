<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<c:set var="pres_active_page" value="content"/>
<%@ include file="/WEB-INF/jsp/presentation/presentationTop.inc"%>

<script type="text/javascript" language="JavaScript">
$(document).ready(function() {
	osp.bag.selections = {};
	$('select.artifactPicker').change(function() {
		osp.bag.selections[$(this).attr('id')] = $(this).val();
	});
	$('select.artifactPicker').each(function() {
		osp.bag.selections[$(this).attr('id')] = $(this).val();
	});
	osp.bag.formTypes = {};
	<c:forEach var="itemDefinition" items="${types}" varStatus="loopCounter">
		osp.bag.formTypes['<c:out value="${itemDefinition.id.value}"/>'] = '<c:out value="${itemDefinition.type}"/>';  
	</c:forEach>
	$('a.inlineFormEdit').click(function(ev) {
		ev.preventDefault();
		var item = this.href.substring(this.href.indexOf('#') + 1);
		if (osp.bag.selections[item]) {
			var pieces = osp.bag.selections[item].split('.');
			var itemDefId = pieces[0];
			var formTypeId = osp.bag.formTypes[itemDefId];
			var formId = pieces[1];
			window.location = '<osp:url value="editPresentationForm.osp" />'
					+ '&id=<c:out value="${presentation.id.value}" />'
					+ '&formTypeId=' + formTypeId
					+ '&formId=' + formId
					+ '&itemDefId=' + itemDefId;
		}		
	});
});

	 function showEditSelect(listId) {
		var selectedIndex = document.getElementById(listId).selectedIndex;
		var selectedOption = document.getElementById(listId).options[selectedIndex];
		if ( selectedOption.className != 'readOnly' ) {
			$("#noedit_"+listId).hide();
			$("#edit_"+listId).show();
		}
		else {
			$("#edit_"+listId).hide();
			$("#noedit_"+listId).show();
		}
	 }
    
	 function updateItems() {
		 var arrBox = new Array();
		 var i = 0;
		 var j = 0;
	 
		 <c:forEach var="itemDefinition" items="${types}" varStatus="loopCounter">
			 <c:if test="${itemDefinition.allowMultiple == true}">
				 arrBox[i] = ospGetElementById('items_<c:out value="${loopCounter.index}"/>');
				 i++;
             var length = document.wizardform.elements['items_<c:out value="${loopCounter.index}"/>'].options.length;
             if ( length == 0 )
                document.wizardform.elements['items_<c:out value="${loopCounter.index}"/>'].options[0] = new Option("", "", false, false);
			 </c:if>
		 </c:forEach>
		 for (i = 0; i < arrBox.length; i++) {
			 var nextBox = arrBox[i];
			 for (j = 0; j < nextBox.options.length; j++) {
				 nextBox.options[j].selected = true;
			 }
		 }
		 document.wizardform.submit();		 
		 return true;
	 }
</script>

<div class="tabNavPanel">

<h3>
   <p class="instructionMessage"><c:out value="${msgs.instructions_addPresentation2}"/></p>
</h3>

<form method="post" name="wizardform" action="editContent.osp"> 

<input type="hidden" name="id" value="<c:out value="${presentation.id.value}" />" />    
    
<div class="editContentPanel">
	<div class="instruction">
		
	</div>
	<spring:bind path="presentation.items">
		<ul class="presentationElementGroup">
			<c:forEach var="itemDefinition" items="${types}"
				varStatus="loopCounter">
	
				<c:if test="${loopCounter.index % 2 == 0}">
					<c:set var="alternating">class="bg"</c:set>
				</c:if>
				<c:if test="${loopCounter.index % 2 != 0}">
					<c:set var="alternating"></c:set>
				</c:if>
	
				<li class="presentationElement">
					<c:choose>
						<c:when
								test="${itemDefinition.allowMultiple == true}">
								<c:set var="list1">
									<c:out value="${status.expression}" />_unselected_<c:out
										value="${loopCounter.index}" />
								</c:set>
								<c:set var="list2">
									<c:out value="${status.expression}" />_<c:out
										value="${loopCounter.index}" />
								</c:set>
		
								<c:set var="selectBox">
									<c:out value="${list1}" />
								</c:set>
								<h3><c:out value="${itemDefinition.title}" /></h3>
								<div class="textPanel"><c:out value="${itemDefinition.description}" /></div>
								<table width="100%" class="sidebyside" border="0" summary="<c:out value="${msgs.item_selection_table_summary_step2}"/>">
									<tr>
										<th style="padding:0">
											<table width="100%" style="margin:0">
												<tr>
													<td>
														<c:out value="${msgs.label_availableItems_step2}"/>
													</td>
													<c:if test="${itemDefinition.isFormType}">
														<td style="text-align:right">
															<a href="<osp:url value="editPresentationForm.osp"/>&amp;id=<c:out value="${presentation.id.value}" />&amp;formTypeId=<c:out value="${itemDefinition.type}"/>&amp;itemDefId=<c:out value="${itemDefinition.id}"/>"
												   class="inlineCreate"><c:out value="${msgs.create_new}"/></a>
													|
														<a href="#<c:out value="${list1}"/>" 
															id="edit_<c:out value="${list1}"/>"
															class="inlineFormEdit" style="display:none;">
													  <c:out value="${msgs.edit_selected}"/></a>
														<span id="noedit_<c:out value="${list1}"/>"
															class="itemAction">
													  <c:out value="${msgs.edit_selected}"/></span>
														</td>
													</c:if>
												</tr>
											</table>	
										</th>
										<th></th>
										<th style="padding:0">
											<table width="100%" style="margin:0">
												<tr>
													<td>
														<c:out value="${msgs.label_selectedItems_step2}"/>
													</td>
													<c:if test="${itemDefinition.isFormType}">
													<td style="text-align:right">
														<a href="#<c:out value="${list2}"/>" 
															id="edit_<c:out value="${list2}"/>"
															class="inlineFormEdit" style="display:none;">
                                         <c:out value="${msgs.edit_selected}"/></a>
														<span id="noedit_<c:out value="${list2}"/>"
															class="itemAction">
                                         <c:out value="${msgs.edit_selected}"/></span>
													</td>
													</c:if>
												</tr>
											</table>	
									  </th>
									</tr>
									<tr>
										<td style="width:40%">
											<select multiple="multiple"
												class="artifactPicker"
												style="width:100%"
												size="10"
												onclick="showEditSelect('<c:out value="${list1}"/>');"
												id="<c:out value="${list1}"/>"
												name="<c:out value="${list1}"/>">
                                    
												<%-- construct list of unselected artifacts --%>
												<c:forEach var="artifact" items="${artifacts[itemDefinition.id.value]}">
													<c:set var="itemId">
														<c:out value="${itemDefinition.id.value}" />.<c:out value="${artifact.id.value}" />
													</c:set>
													<c:if test="${empty itemHash[itemId]}">
														<option
															value="<c:out value="${itemId}" />">
															<c:out value="${artifact.displayName}" />
														</option>
													</c:if>
												</c:forEach>
											</select>
										</td>
										<td style="text-align:center">
											<input name="add"  type="button"
												onclick="move('<c:out value="${list1}"/>','<c:out value="${list2}"/>',false); updateItems();"
												value="<c:out value="${msgs.button_add}"/> &gt;" 
											/> 
											<br />
											<input name="add all" type="button" 
												onclick="move('<c:out value="${list1}"/>','<c:out value="${list2}"/>',true); updateItems();" 
												value="<c:out value="${msgs.button_addAll}"/> &gt;&gt;" 
											/>
											<hr class="itemSeparator" />
											<input name="remove" type="button"
												onclick="move('<c:out value="${list2}"/>','<c:out value="${list1}"/>',false); updateItems();"
												value="<c:out value="${msgs.button_remove}"/> &lt;"
											/>
											<br />
											<input name="remove all" type="button" 
												onclick="move('<c:out value="${list2}"/>','<c:out value="${list1}"/>',true); updateItems();" 
												value="<c:out value="${msgs.button_removeAll}"/> &lt;&lt;"
											/>
										</td>
										<td style="width:40%">
											<select multiple="multiple"
												class="artifactPicker"
												style="width:100%"
												size="10"
												onclick="showEditSelect('<c:out value="${list2}"/>');"
												id="<c:out value="${list2}"/>"
												name="<c:out value="${status.expression}"/>">
												
                                    <%-- construct list of selected artifacts --%>
												<c:forEach var="artifact" items="${artifacts[itemDefinition.id.value]}">
													<c:set var="itemId">
													  <c:out value="${itemDefinition.id.value}"/>.<c:out value="${artifact.id.value}"/>
													</c:set>
													<c:if test="${not empty itemHash[itemId]}">
														<c:choose>
														  <c:when test="${itemDefinition.isFormType && itemHash[itemId].owner.id.value eq currentUser}">
															 <c:set var="mine" value="true"/>
														  </c:when>
														  <c:otherwise>
															 <c:set var="mine" value=""/>
														  </c:otherwise>
														</c:choose>
														<option value="<c:out value="${itemId}" />"
															<c:if test="${empty mine}">class="readOnly"</c:if> >
															<c:out value="${artifact.displayName}"/>
														</option>
													</c:if>
												</c:forEach>
											</select>
										</td>
									</tr>
								</table>
						</c:when>
						<c:otherwise>
							<c:set var="selectBox"><c:out value="${status.expression}"/><c:out value="${loopCounter.index}"/></c:set>
								<div class="navPanel" style="background:transparent;">
									<div class="viewNav" style="background:transparent;width:60%">
										<h3 style="margin:0;padding:0;"><c:out
											value="${itemDefinition.title}" /></h3>
										<c:if test="${not empty itemDefinition.description}">
											<div class="instruction" style="margin:0">
												<c:out
													value="${itemDefinition.description}" />
											</div>		
										</c:if>

									</div>
									<div class="listNav">
										<label  for="<c:out value="${selectBox}"/>" class="itemAction" style="margin-left:0;padding-left:0;display:block"><span><c:out value="${msgs.label_availableItems}"/></span></label>
										<select
											onchange="updateItems()"
											class="artifactPicker"
											id="<c:out value="${selectBox}"/>"
											name="<c:out value="${status.expression}"/>">
											<option value=""><c:out value="${msgs.addPresentation2_selectItem}"/>
											   </option>
											<option value="">- - - - - - - - - -
											- - - - - - - - - - -</option>
											<c:forEach var="artifact"
												items="${artifacts[itemDefinition.id.value]}">
												<c:set var="itemId">
													<c:out value="${itemDefinition.id.value}" />.<c:out value="${artifact.id.value}" />
												</c:set>
												<option value="<c:out value="${itemId}" />"
												  <c:if test="${not empty itemHash[itemId]}">
													 selected="selected"
													 <c:choose>
														<c:when test="${itemDefinition.isFormType && itemHash[itemId].owner.id.value eq currentUser}">
														  <c:set var="mine" value="true"/>
														</c:when>
														<c:otherwise>
														  <c:set var="mine" value=""/>
														</c:otherwise>
													 </c:choose>
												  </c:if>
												>
												<c:out
													value="${artifact.displayName}" />
												</option>
											</c:forEach>
										</select>
									<c:if test="${itemDefinition.isFormType}">
										<span class="itemAction"  style="margin-left:0;padding-left:0;white-space:nowrap;padding-top:4px;display:inline-block;">
											<a href="<osp:url value="editPresentationForm.osp"/>&amp;id=<c:out value="${presentation.id.value}" />&amp;formTypeId=<c:out value="${itemDefinition.type}"/>&amp;itemDefId=<c:out value="${itemDefinition.id}"/>"
											   class="inlineCreate""><c:out value="${msgs.create_new}" /></a>| 
											<c:choose>
											<c:when test="${mine}">
											  <a href="#<c:out value="${selectBox}" />"
													class="inlineFormEdit"><c:out value="${msgs.edit_selected}"/></a>|
											</c:when>
											<c:otherwise>
													<c:out value="${msgs.edit_selected}"/> |
											</c:otherwise>
											</c:choose>
											<a href="#" onclick="document.wizardform.<c:out value='${selectBox}'/>.selectedIndex=0;updateItems()">
											   <c:out value="${msgs.remove_selected}"/></a>
										</span>
									</c:if>
								</div>
							</div>
						</c:otherwise>
					</c:choose>
				</li>
			</c:forEach>
		</li>
    </spring:bind>
</div>
</form>
</div>
