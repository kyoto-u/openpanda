<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<c:set var="pres_active_page" value="summary" />
<%@ include file="/WEB-INF/jsp/presentation/presentationTop.inc"%>

<script type="text/javascript">
$(document).ready(function() {
	$('.autoPost').change(function() {
		if ($(this).attr('checked')) {
			var params = { id : osp.bag.presentationId };
			params[$(this).attr('name')] = $(this).val();
			$.post('updatePresentation.osp', params );
		}
	});
});
</script>

<style type="text/css">
.quickLink { padding-top: 0.2em; text-align: center; font-size: 1.2em; }
.quickLinkInfo { margin-top: 0.1em; text-align: center; font-size: 0.9em; }
.quickLinkDisabled { color: #666666; }
.quickLinkDisabled label { color: #666666; }
.presentation_menu{
}
.presentation_menu th{
	text-align:left;
	padding:0;
	}

.presentation_menu_block {
	margin: auto;
	width:230px;
	height: 100%;
	}

.presentation_menu_header {
 	text-align: center;
	font-size: 1.4em;
	color: #555555;
	margin-bottom: 0.1em;
}

.presentation_menu_body {
	border: 1px solid #CCCCCC;
	background-color: #FCFCEE;
	padding: 0.2em;
 	text-align: center;
	}
.presentation_menu_body p{
	padding:0 .3em
}

</style>

<spring:nestedPath path="presentation">

<div class="tabNavPanel">

<form name="mainForm" id="mainForm" method="post" onsubmit="return true;">
<input type="hidden" name="freeFormContent" value=""/>

<div class="presentationPanel">

<table style="width: 100%;" cellspacing="10" class="presentation_menu">
<tbody>
<tr>
<th  class="presentation_menu_header">
	<c:out value="${msgs.pres_status}" />
</th>
<th class="presentation_menu_header">
			<c:out value="${msgs.quick_start}" />
</th>
<th  class="presentation_menu_header" >
	<c:out value="${msgs.pres_comments_heading}" />
</th>
<tr>
<td class="presentation_menu_body">
	<div class="presentation_menu_block">
		<div>
			<div> 
				<p class="quickLink">
					<input class="autoPost" type="radio"
					       id="btnActive"
					       name="active" value="true"
					       <c:if test="${active}">checked="checked"</c:if> />
					<label for="btnActive"><c:out value="${msgs.button_active}" /></label>
				</p>
				<p class="quickLinkInfo">
					<c:out value="${msgs.active_caption}" />
				</p>
				
				<p class="quickLink">
					<input class="autoPost" type="radio"
					       id="btnInactive"
					       name="active" value="false"
					       <c:if test="${not active}">checked="checked"</c:if> />
					<label for="btnInactive"><c:out value="${msgs.button_inactive}" /></label>
				</p>
				<p class="quickLinkInfo">
					<c:out value="${msgs.inactive_caption}" />
				</p>
			</div>
		</div>
	</div>
</td>
<td  class="presentation_menu_body">
	<div class="presentation_menu_block">
		<div>
			<c:if test="${not empty presentation.template.propertyFormType && !disableOptions}">
				<p class="quickLink"><a href="<osp:url value="editOptions.osp"/>&id=<c:out value="${presentation.id.value}" />"><c:out value="${msgs.pres_options}"/></a></p>
				<p class="quickLinkInfo"><c:out value="${msgs.pres_options_caption}"/></p>
			</c:if>

			<c:choose>			 
				<c:when test="${presentation.isFreeFormType}">
					<p class="quickLink"><a href="javascript:document.mainForm.freeFormContent.value='true';document.mainForm.submit();"><c:out value="${msgs.pres_content}"/></a></p>
				</c:when>
				<c:otherwise> <%-- templated portfolio --%>
					<p class="quickLink"><a href="<osp:url value="editContent.osp"/>&id=<c:out value="${presentation.id.value}" />"><c:out value="${msgs.pres_content}"/></a></p>
				</c:otherwise>
			</c:choose>
         
			<p class="quickLinkInfo"><c:out value="${msgs.pres_content_caption}"/></p>
			<c:if test="${!disableShare}">
			<div <c:if test="${optionsAreNull}">class="quickLinkDisabled"</c:if>>			
			  <c:choose>
				 <c:when test="${optionsAreNull}">
					<p class="quickLink"><c:out value="${msgs.pres_share}"/></p>
					<p class="quickLinkInfo"><c:out value="${msgs.inactive_hint}"/></p>
				 </c:when>
				 <c:otherwise>
					<p class="quickLink"><a href="<osp:url value="sharePresentation.osp"/>&id=<c:out value="${presentation.id.value}" />"><c:out value="${msgs.pres_share}"/></a></p>
					<p class="quickLinkInfo"><c:out value="${msgs.pres_share_caption}"/></p>
				 </c:otherwise>
			  </c:choose>
			</div>
			</c:if>
		</div>
	</div>
</td>
<td  class="presentation_menu_body">
	<div class="presentation_menu_block">
		<div>
			<div>
				<p class="quickLink">
					<input class="autoPost" type="radio"
					       id="btnAllow"
					       name="allowComments" value="true"
					       <c:if test="${presentation.allowComments}">checked="checked"</c:if> />
					<label for="btnAllow"><c:out value="${msgs.button_allow}" /></label>
				</p>
				<p class="quickLinkInfo">
					<c:out value="${msgs.allowed_caption}" />
				</p>
				<p class="quickLink">
					<input class="autoPost" type="radio"
					       id="btnDisallow"
					       name="allowComments" value="false"
					       <c:if test="${not presentation.allowComments}">checked="checked"</c:if> />
					<label for="btnDisallow"><c:out value="${msgs.button_disallow}" /></label>
				</p>
				<p class="quickLinkInfo">
					<c:out value="${msgs.disallowed_caption}" />
				</p>
			</div>
			<c:if test="${numComments > 0}">
				<hr style="border: 1px solid #CCCCCC; width: 90%; "/>
				<p class="quickLinkInfo">
					<c:choose>
						<c:when test="${numComments == 1}"><c:out value="${msgs.comments_hint}"/></c:when>
						<c:otherwise><c:out value="${msgs.comments_hint_plural}"/></c:otherwise>
					</c:choose>
					<a href="<osp:url value="listComments.osp"/>&id=<c:out value="${presentation.id.value}"/>&returnView=editPresentation.osp&returnText=back_to_presentation"><c:out value="${numComments}" />
						<c:choose>
							<c:when test="${numComments == 1}"><c:out value="${msgs.comments_hint2}"/></c:when>
							<c:otherwise><c:out value="${msgs.comments_hint2_plural}"/></c:otherwise>
						</c:choose>
					</a>
				</p>
			</c:if>
		</div>
	</div>
</td>
</tr>
</table>

<h3>
   <c:out value="${msgs.pres_details}"/>
</h3>

<table class="itemSummary">
<tbody>

<spring:bind path="template.name">
	<tr>
		<th><c:out value="${msgs.table_row_type}"/></th>
		<td><c:out value="${status.value}" /></td>
	</tr>
</spring:bind>
<%-- Created On: --%>
<spring:bind path="created">
	<tr>
		<th><c:out value="${msgs.table_row_created}"/></th>
		<td><c:out value="${status.value}" /></td>
	</tr>
</spring:bind>
<%-- Modified On: --%>
<spring:bind path="modified">
	<tr>
		<th><c:out value="${msgs.table_row_modified}"/></th>
		<td><c:out value="${status.value}" /></td>
	</tr>
</spring:bind>

</tbody>
</table>

</spring:nestedPath>
</div>
</form>
</div>
