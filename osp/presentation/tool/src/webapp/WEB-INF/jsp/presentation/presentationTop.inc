<%--
	NOTE: This include file expects that there a Presentation is bound as ${presentation} and
		  that the active page should be in ${pres_active_page}, (summary, content, share)
--%>
<script type="text/javascript" src="/library/js/jquery.js">//empty</script>
<script type="text/javascript" src="/osp-presentation-tool/js/osp.editor.js">//empty</script>
<script type="text/javascript">	
osp.bag.presentationId = '<c:out value="${presentation.id.value}"/>';
$(document).ready(function() {
	osp.bag.phEdit = osp.editor('#presentationName', 'name', 'updatePresentation.osp', { id: osp.bag.presentationId });
	osp.bag.pdEdit = osp.editor('#presentationDescription', 'description', 'updatePresentation.osp', { id: osp.bag.presentationId });
});
</script>

<table width="100%" style="margin-top:1em">
	<tr>
		<td  style="vertical-align:middle;text-align:left">
         <%-- Portfolio Name:  --%>
			<spring:bind path="presentation.name">
				<div id="presentationName">
					<span class="editableText portfolio_name"><c:out value="${status.value}"/></span>
					<input class="inlineEdit" type="text" style="display:none;margin:0;" size="40" />
					<span class="itemAction">
						<a href="#" class="editLink"><fmt:message key="action_edit_title"/></a>
						<a href="#" class="saveLink" style="display: none;"><fmt:message key="button_saveEdit"/></a>
						<a href="#" class="undoLink" style="display: none;"><fmt:message key="button_cancel"/></a>
					</span>
				</div>
			</spring:bind>
		</td>
		<td style="vertical-align:middle; text-align:right">
			<a href="<osp:url value="listPresentation.osp" />"><fmt:message key="return_to_list" /></a>
			<c:if test="${!optionsAreNull}">
			  |
			  <a target="_blank" title="<fmt:message key="table_presentationManager_new_window"/>"
					href="<c:out value="${baseUrl}"/><c:out value="${presentation.id.value}" />">
					<fmt:message key="pres_preview" />
			  </a>
			</c:if>
		</td>
	</tr>
   <tr>
		<td style="vertical-align:middle;text-align:left" colspan="2">
       <%-- Description:  --%>
         <spring:bind path="presentation.description">
      		<div id="presentationDescription">
      		   <fmt:message key="table_row_description"/>
      			<span class="editableText"><c:out value="${status.value}" /></span>
      			<textarea class="inlineEdit" cols="40" rows="4" style="display:none;"></textarea>
      			<span class="itemAction">
                  <a href="#" class="editLink"><fmt:message key="edit"/></a>
      				<a href="#" class="saveLink" style="display: none;"><fmt:message key="button_saveEdit"/></a>
      				<a href="#" class="undoLink" style="display: none;"><fmt:message key="button_cancel"/></a>
      			</span>
      		</div>
      </spring:bind>
     </td>
   </tr>
</table>

<form method="post" name="topForm" id="topForm" style="margin:0">
<input type="hidden" name="freeFormContent" value=""/>

<ul class="tabNav specialLink">
	<c:choose>
		<c:when test="${pres_active_page == 'summary'}">
			<li class="selected"><span><fmt:message key="pres_summary"/></span></li>
		</c:when>
		<c:otherwise>
			<li><a href="<osp:url value="editPresentation.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_summary"/></a></li>
		</c:otherwise>
	</c:choose>
	<c:choose>
		<c:when test="${pres_active_page == 'content'}">
			<li class="selected"><span><fmt:message key="pres_content"/></span></li>
		</c:when>
		<c:otherwise>
		<c:if test="${! presentation.isFreeFormType}">
			<li><a href="<osp:url value="editContent.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_content"/></a></li>
		</c:if>
		<c:if test="${presentation.isFreeFormType}">
			<li><a href="javascript:document.topForm.freeFormContent.value='true';document.topForm.submit();"><fmt:message key="pres_content"/></a></li>
		</c:if>
		</c:otherwise>
	</c:choose>
	<c:if test="${!disableShare && !optionsAreNull}">
		<c:choose>
			<c:when test="${pres_active_page == 'share'}">
				<li class="selected"><span><fmt:message key="pres_share"/></span></li>	
			</c:when>
			<c:otherwise>
				<li><a href="<osp:url value="sharePresentation.osp"/>&id=<c:out value="${presentation.id.value}" />"><fmt:message key="pres_share"/></a></li>	
			</c:otherwise>
		</c:choose> 
	</c:if>	
</ul>
</form>
