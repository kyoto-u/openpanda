<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="courselink.kyoto_u.ac.jp.bundle.messages"/>
</jsp:useBean>

<!-- menu -->
<div class="navIntraTool" style="font-size:15px !important;">
	<img src="image/table_edit.png" align="middle"> <a href=index.htm?page=requestlist><c:out value="${msgs.item_menu_request_list_title}" /></a>
	<img src="image/table_error.png" align="middle"> <a href=index.htm?page=rejectlist><c:out value="${msgs.item_menu_reject_list_title }" /></a>
	<img src="image/table_delete.png" align="middle"> <a href=index.htm?page=removeLogs><c:out value="${msgs.item_menu_remove_title }" /></a>
</div>

<table class="listHier lines nolines" cellspacing="0" cellpadding="0" border="0" summary="site info" >
<tr>
	<td><c:out value="${msgs.item_course_id }"/></td>
	<td><c:out value="${siteId }"/></td>
</tr>
<tr>
	<td><c:out value="${msgs.item_course_title }"/></td>
	<td><c:out value="${siteTitle }"/></td>
</tr>
<tr>
	<td><c:out value="${msgs.item_site_status }"/></td>
	<td>
								<c:choose>
								<c:when test="${status}">
									<c:out value="${msgs.item_site_exist }" />
								</c:when>
								<c:otherwise>
									<c:out value="${msgs.item_site_nonexist }" />
								</c:otherwise>
							</c:choose></td>
</tr>
</table>

<!--  list of site requests -->
	<c:if test="${not empty requestDetailList }">
		<br/>
		<table class="nolines" cellspacing="0" cellpadding="0" border="0" summary="request list" width="400px">
		<tr><td>
		<table class="tablesorter" id="requestlist" cellspacing="0" cellpadding="0" border="0" summary="site list" >
			<thead>
				<tr>
					<th scope="col"><c:out value="${msgs.item_insert_date }"/></th>
					<th scope="col"><c:out value="${msgs.item_create_user_name}"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="request" items="${requestDetailList }">
					<tr>
						<td style="white-space:nowrap">
							<c:out value="${request.insertDate}"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${request.insertUserId }"/>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		</td></tr></table>
	</c:if>
<jsp:directive.include file="/templates/footer.jsp"/>


<script>
<!--
jQuery( function() {
	jQuery( '#requestlist' ).tablesorter();
	});
// -->
</script>