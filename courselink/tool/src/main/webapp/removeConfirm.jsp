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
<br/>
<!-- request none -->
<c:if test="${empty removeList }">
	<c:out value="${msgs.item_removed_none}" />
</c:if>

<!--  list of site requests -->
	<c:if test="${not empty removeList }">
	<table border="0">
	<tr>
	<td>
		<c:out value="${msgs.item_removed_log }" />
	</td>
	<td>
	<form:form name="dateForm" action="index.htm?action=removeLogs">
		<input name="insertDate" type="hidden"  value='<c:out value="${insertDate}" />'/>
		<input type="submit" value='<c:out value="${msgs.item_remove}" />'
  			onclick="return confirm('<c:out value='${msgs.item_remove_confirm}' />');" />
  	</form:form>
	</td>
	</tr>
	</table>
		<table  id="removelist" class="display"cellspacing="0" cellpadding="0" border="0" summary="site list">
			<thead>
				<tr>
					<th scope="col"><c:out value="${msgs.item_insert_date }"/></th>
					<th scope="col" class="userid"><c:out value="${msgs.item_owner_eid }"/></th>
					<th scope="col"><c:out value="${msgs.item_course_id }"/></th>
					<th scope="col"><c:out value="${msgs.item_course_title }"/></th>
					<th scope="col"><c:out value="${msgs.item_reject_log}"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="request" items="${removeList }">
					<tr>
						<td style="white-space:nowrap">
							<c:out value="${request.insertDate}"/>
						</td>
						<td style="white-space:nowrap;">
							<c:out value="${request.insertUserId}"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${request.siteId }"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${request.siteTitle }"/>
						</td>
						<td style="white-space:normal">
							<c:out value="${request.memo}"/>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

	</c:if>
<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!--
jQuery( function() {
	jQuery( '#removelist' ).dataTable({
		"bLengthChange":false,
		"bStateSace": true,
		"bInfo":true,
		"bFilter": false,
		"iDisplayLength": 15
	});
	});
// -->
</script>