<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="courselink.kyoto_u.ac.jp.bundle.messages"/>
</jsp:useBean>

<!-- menu -->
<div class="navIntraTool" style="font-size:15px !important;">
	<img src="image/table_edit.png" align="middle"> <a href="#"><c:out value="${msgs.item_menu_request_list_title}" /></a>
	<img src="image/table_error.png" align="middle"> <a href=index.htm?page=rejectlist><c:out value="${msgs.item_menu_reject_list_title }" /></a>
	<img src="image/table_delete.png" align="middle"> <a href=index.htm?page=removeLogs><c:out value="${msgs.item_menu_remove_title }" /></a>
</div>

<!-- request none -->
<c:if test="${empty ownersList }">
		<table class="listHier noline">
			<tbody>
				<tr>
				<td>
					<c:out value="${msgs.item_request_none}" />
				<td>
				</td>
				<td align="right">
<!-- 					<a href=index.htm?action=toggleHideCreated >
					<c:choose>
						<c:when test="${showCreatedFlg }">
							<c:out value="${msgs.hideCreated }"/>
						</c:when>
						<c:otherwise>
							<c:out value="${msgs.showCreated }"/>
						</c:otherwise>
					</c:choose>
					</a>
				</td> -->
				</tr>
			</tbody>
		</table>
</c:if>

<!-- result of creating site -->
<c:if test="${not empty createSiteId }">
	<c:choose>
		<c:when test="${siteCreateResult }">
			<div class="msgStrongBlue">
				<c:out value="${msgs.item_site_create_success }" />
			</div>
		</c:when>
		<c:otherwise>
			<div class="msgStrongRed">
				<c:out value="${msgs.item_site_create_failure }" />
			</div>
		</c:otherwise>
	</c:choose>
</c:if>
<br/>

	<c:if test="${not empty ownersList }">
		<table class="listHier noline">
			<tbody>
				<tr>
				<td>
					<c:out value="${msgs.item_requested_list }" />
				<td>
				</td>
				<td align="right">
<!--  					<a href=index.htm?action=toggleHideCreated >
					<c:choose>
						<c:when test="${showCreatedFlg }">
							<c:out value="${msgs.hideCreated }"/>
						</c:when>
						<c:otherwise>
							<c:out value="${msgs.showCreated }"/>
						</c:otherwise>
					</c:choose>
					</a>-->
				</td>
				</tr>
			</tbody>
		</table>

		<table id="requestlist" class="tablesorter" cellspacing="0" cellpadding="0" border="0" summary="site list" >
			<thead>
				<tr>
					<th scope="col"><c:out value="${msgs.item_course_id }"/></th>
					<th scope="col"><c:out value="${msgs.item_course_title }"/></th>
					<th scope="col"><c:out value="${msgs.item_request_num }"/></th>
					<th scope="col"><c:out value="${msgs.item_create_date }"/></th>
					<th scope="col"><c:out value="${msgs.item_create_user_name}"/></th>
					<th scope="col"><c:out value="${msgs.item_site_status }"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="site" items="${ownersList }">
					<tr>
						<td style="white-space:nowrap">
							<c:out value="${site.siteId }"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${site.title }"/>
						</td>
						<td style="white-space:nowrap">
							<a href=index.htm?action=showRequest&siteId=<c:out value="${site.siteId }"/> >
								<c:out value="${site.requestNum }"/>
							</a>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${site.createDate }"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${site.createUserName }"/>
						</td>
						<td style="white-space:nowrap" class="centerColumn">
							<c:choose>
								<c:when test="${site.existSite}">
									<c:out value="${msgs.item_site_exist }" />
										<c:if test="${site.status != 0 }">
											<c:if test="${site.status == beginnerStatus}">
												<c:out value="( ${msgs.item_beginner } )" />
											</c:if>
											<c:if test="${site.status != beginnerStatus}">
												<c:out value="( ${msgs.item_label_department }" /><c:out value="${site.status } )" />
											</c:if>
										</c:if>
								</c:when>
								<c:otherwise>
									<a href=index.htm?action=createSite&siteId=<c:out value="${site.siteId }"/>  target = "_parent">
										<c:out value="${msgs.item_site_create }" />
									</a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
<script>
<!--
jQuery( function() {
	jQuery( '#requestlist' ).tablesorter();
	});
// -->
</script>
<jsp:directive.include file="/templates/footer.jsp"/>

