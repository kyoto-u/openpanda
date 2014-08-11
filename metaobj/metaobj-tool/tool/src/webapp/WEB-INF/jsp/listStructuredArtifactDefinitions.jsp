<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "messages"/>

<osp:authZMap prefix="metaobj." var="can" qualifier="${authZqualifier}"/>
<!-- GUID=<c:out value="${newFormId}" /> -->

<osp:url value="listStructuredArtifactDefinitions.osp" var="homeUrl"/>

<div class="navIntraTool">
<c:if test="${can.create}">
      <a href="<osp:url value="/addStructuredArtifactDefinition.osp?new=true"/>"
          title="<fmt:message key="action_new_title"/>" ><fmt:message key="action_new"/></a>
      <a href="<osp:url value="/importStructuredArtifactDefinition.osp"/>"
          title="<fmt:message key="action_import_title"/>" ><fmt:message key="action_import"/></a>
    </c:if>
   <c:if test="${isMaintainer}">

      <a href="<osp:url value="sakai.permissions.helper.helper/tool?panel=Main">
       <osp:param name="session.sakaiproject.permissions.description">
         <fmt:message key="message_permissionsEdit">
           <fmt:param><c:out value="${tool.title}"/></fmt:param>
	   <fmt:param><c:out value="${worksite.title}"/></fmt:param>
         </fmt:message>
       </osp:param>

       <osp:param name="session.sakaiproject.permissions.targetRef"
            value="${worksite.reference}"/>
      <osp:param name="session.sakai.permissions.helpersakai.tool.helper.done.url" value="${homeUrl}"/>
      <osp:param name="session.sakaiproject.permissions.prefix" value="metaobj."/>
       </osp:url>" title="<fmt:message key="action_permissions"/>" ><fmt:message key="action_permissions_title"/>
     </a>

   </c:if>
</div>

<osp:url var="listUrl" value="listStructuredArtifactDefinitions.osp"/>
<osp:listScroll listUrl="${listUrl}" className="listNav" />


<h3><fmt:message key="title_formManager"/></h3>
<c:if test="${!empty types}">
 <table class="listHier lines nolines" cellspacing="0" cellpadding="0" summary="<fmt:message key="table_header_summary"/>">
   <thead>
      <tr>
         <th scope="col"><fmt:message key="table_header_name"/></th>
         <th scope="col"><fmt:message key="table_header_owner"/></th>
         <th scope="col"><fmt:message key="table_header_siteId"/></th>
         <th scope="col"><fmt:message key="table_header_modified"/></th>
         <c:if test="${isGlobal == false}">
            <th scope="col"><fmt:message key="table_header_siteState"/></th>
         </c:if>
         <th scope="col"><fmt:message key="table_header_globalState"/></th>
      </tr>
   </thead>

  <c:forEach var="home" items="${types}">
    <TR>
      <TD nowrap>
         <c:out value="${home.type.description}" />
         <c:if test="${home.modifiable}">
         	<c:set var="hasFirstAction" value="false" />
            <div class="itemAction indnt1">
                <c:if test="${can.edit || home.owner == currentAgent}"><c:set var="hasFirstAction" value="true" /><a href="<osp:url value="/editStructuredArtifactDefinition.osp"/>&id=<c:out value="${home.id}" />"><fmt:message key="table_action_edit"/></a></c:if>
                
                <c:if test="${can.export}"><c:if test="${hasFirstAction}" > | </c:if><c:set var="hasFirstAction" value="true" /> <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=structuredArtifactDefinitionManager&formId=<c:out value="${home.id.value}" />/<c:out value="${home.type.description}" /> Form.zip"><fmt:message key="table_action_export"/></a>
                </c:if>
                <c:if test="${!isGlobal && can.publish && home.canPublish}"> <c:if test="${hasFirstAction}" > | </c:if><c:set var="hasFirstAction" value="true" /> <a href="<osp:url value="confirmSADPublish.osp"/>&action=site_publish&id=<c:out value="${home.id}" />"><fmt:message key="table_action_publish"/></a></c:if>
                <c:if test="${isGlobal && can.publish &&  home.canGlobalPublish}"> <c:if test="${hasFirstAction}" > | </c:if><c:set var="hasFirstAction" value="true" /> <a href="<osp:url value="confirmSADPublish.osp"/>&action=global_publish&id=<c:out value="${home.id}" />"><fmt:message key="table_action_globalPublish"/></a></c:if>
                <c:if test="${!isGlobal && home.canSuggestGlobalPublish && can['suggest.global.publish']}"> <c:if test="${hasFirstAction}" > | </c:if><c:set var="hasFirstAction" value="true" /> <a href="<osp:url value="confirmSADPublish.osp"/>&action=suggest_global_publish&id=<c:out value="${home.id}" />"><fmt:message key="table_action_suggestPublishGlobal"/></a></c:if>
                <c:if test="${isGlobal && home.canApproveGlobalPublish && can.publish}"> <c:if test="${hasFirstAction}" > | </c:if><c:set var="hasFirstAction" value="true" /> <a href="<osp:url value="confirmSADPublish.osp"/>&action=global_publish&id=<c:out value="${home.id}" />"><fmt:message key="table_action_approveGlobalPublish"/></a></c:if>
                <c:if test="${can.delete || home.owner == currentAgent}"> <c:if test="${hasFirstAction}" > | </c:if> <c:set var="hasFirstAction" value="true" /><a href="<osp:url value="confirmSADDelete.osp"/>&id=<c:out value="${home.id}" />"><fmt:message key="table_action_delete"/></a></c:if>
                <c:if test="${isMaintainer && toolShowUsage}"> <c:if test="${hasFirstAction}" > | </c:if> <c:set var="hasFirstAction" value="true" /><a href="<osp:url value="formUsage.osp" />&id=<c:out value="${home.id}" />"><fmt:message key="table_action_usage" /></a></c:if>
            </div>
         </c:if>
      </TD>
      <TD>
         <c:if test="${home.modifiable}">
            <c:out value="${home.owner.displayName}" />
         </c:if>
      </TD>
      <TD>
         <c:choose>
            <c:when test="${!home.modifiable}">
               <fmt:message key="text_global"/>
            </c:when>
            <c:otherwise>
               <c:set var="site" value="${sites[home.siteId]}" />
               <c:if test="${!empty site}">
                  <c:out value="${site.title}" />
               </c:if>
               <c:if test="${empty site}">
                  <fmt:message key="text_global"/>
               </c:if>
            </c:otherwise>
         </c:choose>
      </TD>
      <TD nowrap><c:set var="dateFormat"><fmt:message key="dateFormat_Middle"/></c:set><fmt:formatDate value="${home.modified}" pattern="${dateFormat}"/></TD>
      <c:if test="${isGlobal == false}">
      <TD>
         <c:choose>
            <c:when test="${home.global}">
               <fmt:message key="text_na"/>
            </c:when>
            <c:otherwise>
               <c:if test="${home.modifiable}">
                  <c:choose>
                     <c:when test="${home.siteState == 0}">
                        <fmt:message key="text_unpublished"/>
                     </c:when>
                     <c:when test="${home.siteState == 2}">
                        <fmt:message key="text_published"/>
                     </c:when>
                  </c:choose>
               </c:if>
            </c:otherwise>
         </c:choose>
      </TD>
      </c:if>
      <TD>
         <c:if test="${home.modifiable}">
            <c:choose>
               <c:when test="${home.globalState == 0}">
                  <fmt:message key="text_unpublished"/>
               </c:when>
               <c:when test="${home.globalState == 1}">
                  <fmt:message key="text_waitingForApproval"/>
               </c:when>
               <c:when test="${home.globalState == 2}">
                  <fmt:message key="text_published"/>
               </c:when>
            </c:choose>
         </c:if>
         <c:if test="${!home.modifiable}"><fmt:message key="text_published"/></c:if>
      </TD>
    </TR>
  </c:forEach>
  </table>
<br/>
<fmt:message key="text_explainState"/>
<fmt:message key="text_adminsApproveGlobalAccess"/>

</c:if>

<c:if test="${empty types}">
	<fmt:message key="text_noFormsAvailable"/>
	<c:if test="${can.create}">
		<fmt:message key="text_clickAdd"/>
	</c:if>
</c:if>
