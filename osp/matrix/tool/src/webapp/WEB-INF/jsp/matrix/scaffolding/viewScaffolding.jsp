<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ include file="../matrixStyle.jspf" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<c:forEach var="style" items="${styles}">
   <link href="<c:out value='${style}'/>" type="text/css" rel="stylesheet"
      media="all" />
</c:forEach>

<script type="text/javascript">

function hrefViewCell(cellId) {
  window.location="<osp:url value="editScaffoldingCell.osp?scaffoldingCell_id="/>"+cellId;
}

</script>

<osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" qualifier="${matrixContents.scaffolding.worksiteId}"/>
<osp-c:authZMap prefix="osp.matrix.scaffolding.revise." var="canRevise" qualifier="${matrixContents.scaffolding.worksiteId}"/>
		<div class="navIntraTool">
			<c:if test="${canRevise.any || (canRevise.own && matrixContents.scaffolding.owner == osp_agent)}">
				<a href="<osp:url value="addScaffolding.osp?scaffolding_id=${matrixContents.scaffolding.id}"/>"><c:out value="${msgs.action_edit}"/></a>
			</c:if>
			<c:if test="${canRevise.any || (canRevise.own && matrixContents.scaffolding.owner == osp_agent)}">
				<a
					href="<osp:url value="osp.permissions.helper/editPermissions_new">
	               <osp:param name="message"><c:out value="${msgs.action_message_setMatrixPermission}"/>
                  </osp:param>
	               <osp:param name="name" value="scaffoldingSpecific"/>
	               <osp:param name="qualifier" value="${matrixContents.scaffolding.reference}"/>
	               <osp:param name="returnView" value="viewScaffoldingRedirect"/>
	               <osp:param name="returnKey" value="scaffolding_id"/>
	               <osp:param name="returnKeyValue" value="${matrixContents.scaffolding.id}"/>
	               </osp:url>"
					title='<c:out value="${msgs.action_permissions_title}"/>'> 
					<c:out value="${msgs.action_permissions}"/>
					</a>
			</c:if>
			
         	<a href="<osp:url value="listScaffolding.osp"/>"><c:out value="${msgs.action_list}"/></a>
         	
		</div>
		

	<c:if test="${toolPermissionSaved}">
		<div class="success"><c:out value="${msgs.changesSaved}"/></div>	
	</c:if>
	
	<h3><c:out value="${msgs.title_matrixScaffolding}"/></h3>
   
   <c:if test="${not empty matrixContents.scaffolding.description}">
      <p class="instruction">
         <osp-h:glossary link="true" hover="true">
            <c:out value="${matrixContents.scaffolding.description}" escapeXml="false" />
         </osp-h:glossary>
      </p>
   </c:if>
  
	<c:if test="${empty matrixContents.columnLabels}">
		<p class="instruction"><c:out value="${msgs.instructions_clickEdittosetup}"/></p>
	</c:if>
	<c:if test="${not empty matrixContents.columnLabels}">
		<p class="instruction"><c:out value="${msgs.instructions_clickOnaCelltoEdit}"/></p>  

		<c:set var="columnHeading" value="${matrixContents.columnLabels}" />
		<table cellspacing="0" width="100%" summary="<c:out value="${msgs.table_summary_matrixScaffolding}"/>">
			<tr>
				<th class="matrix-row-heading" scope="col">
               <osp-h:glossary link="true" hover="true">
   					<c:out value="${matrixContents.scaffolding.title}"/>
               </osp-h:glossary>
				</th>
				<c:forEach var="head" items="${columnHeading}">
					<th class="matrix-column-heading matriColumnDefault" 
                  bgcolor="<c:out value="${head.color}"/>" 
                  style="color: <c:if test="${not empty head.textColor}" ><c:out value="${head.textColor}"/></c:if>" scope="col">
                  <osp-h:glossary link="true" hover="true">
                     <c:out value="${head.description}"/>
                  </osp-h:glossary>
					</th>
				</c:forEach>
			</tr>   
			<c:forEach var="rowLabel" items="${matrixContents.rowLabels}" varStatus="loopStatus" >
				<tr>
					<th class="matrix-row-heading matriRowDefault" bgcolor="<c:out value="${rowLabel.color}"/>" 
						style="color: <c:if test="${not empty rowLabel.textColor}" ><c:out value="${rowLabel.textColor}"/></c:if>">
                  <osp-h:glossary link="true" hover="true">
                        <c:out value="${rowLabel.description}"/>
                  </osp-h:glossary>
					</th>
	    			<c:forEach var="cell" items="${matrixContents.matrixContents[loopStatus.index]}">
						<td class="matrix-cell-border matrix-<c:out value="${cell.initialStatus}"/>" 
							<c:if test="${canRevise.any || (canRevise.own && matrixContents.scaffolding.owner == osp_agent)}">
								onclick="hrefViewCell('<c:out value="${cell.id}"/>') "
							</c:if>
							 style="cursor:pointer">
							 <c:if test="${canRevise.any || (canRevise.own && matrixContents.scaffolding.owner == osp_agent)}">
							 	<a href="#" onclick="hrefViewCell('<c:out value="${cell.id}"/>') " class="skip"><c:out value="${msgs.table_cell_link_title}"/></a>
									&nbsp;
							</c:if>
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
	  
     <%@ include file="../matrixLegend.jspf" %>
  
	</c:if>
