<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="matrixStyle.jspf" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<c:forEach var="style" items="${styles}">
   <link href="<c:out value='${style}'/>" type="text/css" rel="stylesheet" media="all" />
</c:forEach>

<script type="text/javaScript">

	function hrefViewCell(pageId) {
	  window.location="<osp:url value="viewCell.osp?page_id="/>"+pageId;
	}
	
	function noAccess(cellName){
		
		document.getElementById('accessTable').style.display = "";
		deleteColumn('accessTable');
		addColumn('accessTable', cellName);
	}
	
	function addColumn(tblId, cellName)
	{
		var tblBodyObj = document.getElementById(tblId).tBodies[0];
		for (var i=0; i<tblBodyObj.rows.length; i++) {
			var newCell = tblBodyObj.rows[i].insertCell(-1);
			newCell.innerHTML = "<span class='alertMessage'><c:out value='${no_view_access}'/><br>" + cellName + "</span>";
		}
	}
	function deleteColumn(tblId)
	{
		var allRows = document.getElementById(tblId).rows;
		for (var i=0; i<allRows.length; i++) {
			if (allRows[i].cells.length > 1) {
				allRows[i].deleteCell(-1);
			}
		}
	}


</script>
<osp-c:authZMap prefix="osp.matrix." var="matrixCan" qualifier="${matrixContents.scaffolding.worksiteId}"/>
<osp-c:authZMap prefix="osp.matrix.scaffoldingSpecific." var="scaffoldingCan" qualifier="${matrixContents.scaffolding.reference}"/>

<c:if test="${isExposedPage != true}">
	<div class="navIntraTool">
		<a href="<osp:url value="listScaffolding.osp"/>"><c:out value="${msgs.action_list}"/></a>
	</div>
</c:if>


<h3>
	<c:set var="readOnly_label"><c:out value="${msgs.matrix_readOnly}"/></c:set>
	<c:choose>
		<%-- if size is greater than 0 than that means you can review or evaluate at least one cell --%>
		<c:when test="${scaffoldingCan.accessUserList}">
					<c:if test="${matrixContents.scaffolding.preview}">
						<span class="highlight">
							<c:out value="${msgs.matrix_viewing_title_preview}"/>
						</span>
					</c:if>		  
					<c:if test="${!matrixContents.scaffolding.preview}">
						<c:out value="${msgs.matrix_viewing_title_view}"/>
					</c:if>
               
				"<c:out value="${matrixContents.scaffolding.title}" />"
				<c:if test="${readOnlyMatrix}">(<c:out value="${readOnly_label}"/>)</c:if>:
				<c:out value="${matrixOwner.displayName}" />
		</c:when>
		<c:otherwise>
			<c:if test="${matrixContents.scaffolding.preview}">
				<span class="highlight">
					<c:out value="${msgs.scaffolding_published_preview}"/>
				</span>
			</c:if>		  
			<c:out value="${matrixContents.scaffolding.title}" />
		</c:otherwise>
	</c:choose>	
</h3>
   
<c:if test="${matrixContents.scaffolding.preview}">
	<div class="information">
    	<c:out value="${msgs.title_matrixPreview}"/>
	</div>
</c:if>
<c:if test="${not empty matrixContents.scaffolding.description}">
	<div class="textPanelFooter">
		<osp-h:glossary link="true" hover="true">
			<c:out value="${matrixContents.scaffolding.description}" escapeXml="false" />
		</osp-h:glossary>
	</div>
</c:if>

<c:if test="${(not empty matrixContents.scaffolding)}">
	<div class="navPanel">
		<c:if test="${scaffoldingCan.accessUserList}">
			<c:choose>
				<c:when test="${hasGroups && empty userGroups}">
					<p class="instruction"><c:out value="${msgs.matrix_groups_unavailable}"/></p>
				</c:when>
				<c:otherwise>
					<form method="get" action="<osp:url value="viewMatrix.osp"/>">
						<osp:form/>
						<div class="viewNav">
							<c:if test="${not empty userGroups && userGroupsCount > 1}">
								<label for="group_filter-id"><c:out value="${msgs.matrix_viewing_select_group}" /></label>
								<select name="group_filter" id="group_filter-id" onchange="this.form.submit()">
									<option value="">
										<c:out value="${msgs.matrix_viewing_select_group}" />
									</option>
									<option value="" <c:if test="${empty filteredGroup}">selected="selected"</c:if>>
									<c:out value="${msgs.matrix_groups_showall}"/>
									</option>
									<c:forEach var="group" items="${userGroups}">
										<option value="<c:out value="${group.id}"/>" <c:if test="${filteredGroup == group.id}">selected="selected"</c:if>>
											<c:out value="${group.title}"></c:out>
										</option>
									</c:forEach>
								</select>
							</c:if>
							 &nbsp;&nbsp;&nbsp;
							<label for="view_user-id"><c:out value="${msgs.matrix_viewing_select_user}" /></label>
							<select name="view_user"  id="view_user-id" onchange="if(this.value != ''){this.form.submit()}">
								<option value="">
									<c:out value="${msgs.matrix_viewing_select_user}" />
								</option>
								<c:forEach var="user" items="${members}">
									<option value="<c:out value="${user.id}"/>" <c:if test="${matrixOwner.id.value == user.id}"> selected="selected" </c:if>>
										<c:out value="${user.sortName}"/>
									</option>
								</c:forEach>
							</select>
							<input type="hidden" name="scaffolding_id" value="<c:out value="${matrixContents.scaffolding.id.value}" />" />
						</div>
					</form>
				</c:otherwise>
			</c:choose>
		</c:if>
	</div>	
</c:if>

<c:if test="${not empty matrixContents.columnLabels}">
	<p class="instruction">
		<c:out value="${msgs.instructions_clickOnaCellToEdit}"/>
	</p>
	<c:set var="columnHeading" value="${matrixContents.columnLabels}" />
	
		<!-- This is used to alert the user if they do not have access to the cell they clicked on -->

		<table id="accessTable" border="0" style="display:none">
			<tr>
				<td></td>
			</tr>
		</table>

        <table class="matrixTable" cellspacing="0" width="100%" summary="<c:out value="${msgs.table_summary_matrixScaffolding}"/>">
            <thead>
            <tr>
                <th class="matrix-row-heading" id="chead" scope="col">
                    <osp-h:glossary link="true" hover="true">
                       <c:out value="${matrixContents.scaffolding.title}"/>
                    </osp-h:glossary>
                </th>
                <c:forEach var="head" items="${columnHeading}" varStatus="loopStatus">
                    <th class="matrix-column-heading matrixColumnDefault" id="chead-<c:out value="${loopStatus.index}"/>" 
                        <c:if test="${not empty head.color}">bgcolor="<c:out value="${head.color}"/>"</c:if>
                        <c:if test="${not empty head.textColor}" >style="color: <c:out value="${head.textColor}"/>"</c:if>  
                        scope="col">
                        <osp-h:glossary link="true" hover="true">
                              <c:out value="${head.description}"/>
                        </osp-h:glossary>
                    </th>
                </c:forEach>
            </tr>   
            </thead>
            
            <tbody>
            <c:forEach var="rowLabel" items="${matrixContents.rowLabels}" varStatus="rowLoop" >
              <tr>
                    <th class="matrix-row-heading matrixRowDefault" id="rhead-<c:out value="${rowLoop.index}"/>" 
                        <c:if test="${not empty rowLabel.color}">bgcolor="<c:out value="${rowLabel.color}"/>" </c:if>
                        <c:if test="${not empty rowLabel.textColor}" >style="color: <c:out value="${rowLabel.textColor}"/>"</c:if> scope="row"> 
                      <osp-h:glossary link="true" hover="true"><c:out value="${rowLabel.description}"/></osp-h:glossary>
                    </th>

                 <c:forEach var="cellBean" items="${matrixContents.matrixContents[rowLoop.index]}" varStatus="cellLoop">
                     <c:set var="cell" value="${cellBean.cell}"/>
                     <c:set var="hasAccess" value="${accessibleCells[cell.id.value] != null}"/>
                     <td class="matrix-cell-border matrix-<c:out value="${cell.status}"/>" 
                     	<c:if test="${hasAccess}">
                     		onclick="hrefViewCell('<c:out value="${cell.wizardPage.id}"/>') "
                     	</c:if> 

                     	<c:if test="${!hasAccess}">
                     		onclick="noAccess('<c:out value="${cell.scaffoldingCell.wizardPageDefinition.title}"/>')"
                     	</c:if>
                     	style="cursor:pointer">
                        &nbsp;
                        <c:if test="${hasAccess}">
							<a href="#" onclick="hrefViewCell('<c:out value="${cell.wizardPage.id}"/>') " class="skip"><c:out value="${msgs.table_cell_link_title}"/></a>
						</c:if>
                        <c:forEach var="node" items="${cellBean.nodes}">
                            <fmt:formatDate value="${node.technicalMetadata.lastModified}" pattern="MM/dd/yyyy" var="date"/>
                               <c:set var="hover" value="Name:${node.name}; Size:${node.technicalMetadata.size} bytes; Last Modified: ${date}"/>
                               <img border="0" title="<c:out value="${hover}" />"
                               	alt="<c:out value="${node.name}"/>" 
                               	src="/library/image/<osp-c:contentTypeMap fileType="${node.mimeType}" mapType="image" />"/>
                        </c:forEach>
                        <c:forEach var="taggableItem" items="${cellBean.taggableItems}">
							<c:set var="hover" value="Name:${taggableItem.activity.title}; Type:${taggableItem.typeName};"/>
                               <img border="0" title="<c:out value="${hover}" />"
                               	alt="<c:out value="${taggableItem.activity.title}"/>" 
                               	src="<c:out value="${taggableItem.iconUrl}"/>" mapType="image" />
						</c:forEach>
                        <c:if test="${ !(empty cellBean.assignments)}">
                          <br/>&nbsp;
						  <c:forEach var="node" items="${cellBean.assignments}">
							<img src = '/library/image/silk/page_white_edit.png' border= '0' alt ='' mapType="image" />
						   </c:forEach>
                        </c:if>
                        <c:if test="${ !(empty cellBean.reflections)}">
                            <c:forEach var="review" items="${cellBean.reflections}">
                               <fmt:formatDate value="${review.reviewContentNode.technicalMetadata.lastModified}" pattern="MM/dd/yyyy" var="date"/>
                               <c:set var="hover" value="Reflection: Name:${review.reviewContentNode.name}; Last Modified:${date}"/>
                               <img border="0" title="<c:out value="${hover}" />"
                                alt="<c:out value="${review.reviewContentNode.name}"/>" 
                                src="/library/image/silk/lightbulb.png"/>
                            </c:forEach>
                        </c:if>
                        <c:if test="${ !(empty cellBean.reviews)}">
                            <c:forEach var="review" items="${cellBean.reviews}">
                               <fmt:formatDate value="${review.reviewContentNode.technicalMetadata.lastModified}" pattern="MM/dd/yyyy" var="date"/>
                               <c:set var="hover" value="Review: Name:${review.reviewContentNode.name}; Last Modified:${date}"/>
                               <img border="0" title="<c:out value="${hover}" />"
                                alt="<c:out value="${review.reviewContentNode.name}"/>" 
                                src="/library/image/silk/comment.gif"/>
                            </c:forEach>
                        </c:if>
                        <c:if test="${ !(empty cellBean.evaluations)}">
                            <c:forEach var="review" items="${cellBean.evaluations}">
                               <fmt:formatDate value="${review.reviewContentNode.technicalMetadata.lastModified}" pattern="MM/dd/yyyy" var="date"/>
                               <c:set var="hover" value="Evaluation: Name:${review.reviewContentNode.name}; Last Modified:${date}"/>
                               <img border="0" title="<c:out value="${hover}" />"
                                alt="<c:out value="${review.reviewContentNode.name}"/>" 
                                src="/library/image/silk/tick.png"/>
                            </c:forEach>
                        </c:if>
                     </td>
                 </c:forEach>
              </tr>
            </c:forEach>
            </tbody>
            
            <c:if test="${showFooter}">
            <tfoot>
            <tr>
                <th>&nbsp;</th>
                <c:forEach var="foot" items="${columnHeading}" varStatus="loopStatus">
                    <th class="matrix-column-footer matrixColumnDefault" id="cfoot-<c:out value="${loopStatus.index}"/>" 
                        <c:if test="${not empty foot.color}">bgcolor="<c:out value="${foot.color}"/>"</c:if>
                        <c:if test="${not empty foot.textColor}" >style="color: <c:out value="${foot.textColor}"/>"</c:if>  
                        scope="col">
                        <osp-h:glossary link="true" hover="true">
                              <c:out value="${foot.description}"/>
                        </osp-h:glossary>
                    </th>
                </c:forEach>
            </tr>   
            </tfoot>
            </c:if>
            
        </table>
        
        <%@ include file="matrixLegend.jspf" %>
    
    </c:if>
