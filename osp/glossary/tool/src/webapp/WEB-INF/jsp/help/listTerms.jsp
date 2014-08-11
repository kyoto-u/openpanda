<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!-- GUID=<c:out value="${newTermId}" /> -->

<osp-c:authZMap prefix="osp.help.glossary." var="can" />
<osp-c:authZMap prefix="" var="canWorksite" useSite="true" />


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.glossary.bundle.Messages"/></jsp:useBean>

<div class="navIntraTool">
    <c:if test="${can.add}">
        <a href="<osp:url value="editGlossaryTerm.osp"/>" title='<c:out value="${msgs.label_title_new}"/>'>
        <c:out value="${msgs.action_new}"/></a>
    </c:if>
    
    <c:if test="${can.add}">
        <a href="<osp:url value="importGlossaryTerm.osp"/>" title='<c:out value="${msgs.label_import}"/>'>
        <c:out value="${msgs.action_import}"/> </a>
    </c:if>
    
    <c:if test="${can.export && not empty glossary}">
	    <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=helpManagerTarget&templateId=<c:out value="${template.id.value}"/>/<c:out value="${worksite.title}" /> Glossary.zip"><c:out value="${msgs.action_export}"/></a>
    </c:if>
    
    <c:if test="${canWorksite.maintain}">
        <a href="<osp:url value="osp.permissions.helper/editPermissions">
			<osp:param name="message"> 
			<c:out value="${msgs.message_permissionsEdit}"/>
			</osp:param>
			<osp:param name="name" value="glossary"/>
			<osp:param name="qualifier" value="${worksite.id}"/>
			<osp:param name="returnView" value="glossaryListRedirect"/>
		</osp:url>"
            title='<c:out value="${msgs.action_permissions_title}"/>'>
            <c:out value="${msgs.action_permissions}"/></a>
    </c:if>
</div>

<div class="navPanel">
	<div class="viewNav">
		<c:if test="${!global}">
			<h3><c:out value="${msgs.title_glossaryManager}"/></h3>
		</c:if>
		<c:if test="${global}">
			<h3><c:out value="${msgs.title_glossaryManagerGlobal}"/></h3>
		</c:if>
	</div>	
	
	<osp:url var="listUrl" value="glossaryList.osp" />
	<osp:listScroll listUrl="${listUrl}" className="listNav" />
</div>	


<c:if test="${import_success}">
   <div class="success"><c:out value="${msgs.import_msg_success}"/></div>
</c:if>
<c:if test="${import_unrecognized_file}">
   <div class="alertMessage"><c:out value="${msgs.import_msg_bad_file}"/></div>
</c:if>
<c:if test="${import_failed}">
   <div class="alertMessage"><c:out value="${msgs.import_msg_failed}"/></div>
</c:if>
<c:if test="${import_bad_parse}">
   <div class="alertMessage"><c:out value="${msgs.import_msg_bad_file_parse}"/></div>
</c:if>
<c:choose>
	<c:when test="${not empty glossary}">
		<table class="listHier lines nolines" cellspacing="0" cellpadding="0"  border="0" summary="<c:out value="${msgs.glossary_list_summary}"/>">
			<thead>
				<tr>
					<th scope="col"><c:out value="${msgs.label_Term}"/></th>
					<th scope="col"></th>
					<th scope="col"><c:out value="${msgs.label_desc}"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="term" items="${glossary}">
		
					<tr>
						<td style="white-space:nowrap">
							<osp-h:glossary link="true" hover="false"><c:out value="${term.term}" /></osp-h:glossary>
						</td>
						<td style="white-space:nowrap" class="itemAction">
							<c:if test="${can.edit || can.delete}">
								<c:if test="${can.edit}">
									<a href="<osp:url value="editGlossaryTerm.osp"/>&id=<c:out value="${term.id}" />"><c:out value="${msgs.table_action_edit}"/></a>
								</c:if>
								<c:if test="${can.edit && can.delete}">
									|
								</c:if>
								<c:if test="${can.delete}">
									<a href="<osp:url value="removeGlossaryTerm.osp"/>&id=<c:out value="${term.id}" />"><c:out value="${msgs.table_action_delete}"/></a>
								</c:if>
							</c:if>
						</td>
		
						<td class="textPanel textPanelFooter"><c:out value="${term.description}" /></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<p class="instruction">
			<c:out value="${msgs.glossary_list_emptymessage}"/>
		</p>
	</c:otherwise>
	</c:choose>	