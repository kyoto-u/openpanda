<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="courselink.kyoto_u.ac.jp.bundle.messages"/>
</jsp:useBean>

<!-- menu -->
<div class="navIntraTool" style="font-size:15px !important;">
	<img src="image/table_edit.png" align="middle"> <a href=index.htm?page=requestlist><c:out value="${msgs.item_menu_request_list_title}" /></a>
	<img src="image/table_error.png" align="middle"> <a href="#"><c:out value="${msgs.item_menu_reject_list_title }" /></a>
	<img src="image/table_delete.png" align="middle"> <a href=index.htm?page=removeLogs><c:out value="${msgs.item_menu_remove_title }" /></a>
</div>
<br/>
<!-- request none -->
<c:if test="${empty rejectList }">
	<c:if test="${empty ownersList }">
		<c:out value="${msgs.item_request_none}" />
	</c:if>
</c:if>

<!--  list of site requests -->
	<c:if test="${not empty rejectList }">
		<c:out value="${msgs.item_reject_list }" />
		<table  id="rejectlist" class="display"cellspacing="0" cellpadding="0" border="0" summary="site list">
			<thead>
				<tr>
					<th scope="col"><c:out value="${msgs.item_insert_date }"/></th>
					<th scope="col" class="userid"><c:out value="${msgs.item_owner_eid }"/></th>
					<th scope="col"><c:out value="${msgs.item_reject_log}"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="request" items="${rejectList }">
					<tr>
						<td style="white-space:nowrap">
							<c:out value="${request.insertDate}"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${request.insertUserId}"/>
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
	jQuery( '#rejectlist' ).dataTable({
		"bLengthChange":false,
		"bStateSace": true,
		"bInfo":true,
		"bFilter": false,
		"iDisplayLength": 15
	});
	});
// -->
</script>
