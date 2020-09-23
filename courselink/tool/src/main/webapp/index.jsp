<jsp:directive.include file="/templates/includes.jsp"/>
<jsp:directive.include file="/templates/header.jsp"/>
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
	<jsp:setProperty name="msgs" property="baseName" value="courselink.kyoto_u.ac.jp.bundle.messages"/>
</jsp:useBean>
<c:if test="${not empty err }">
	<div class="validation">
		<c:out value="${err}" />
	</div>
</c:if>
<c:if test="${empty requestList }">
	<c:if test="${empty ownersList }">
		<table class="listHier noline">
			<tbody>
				<tr>
				<td>
					<c:out value="${msgs.item_request_none}" />
				<td>
				</td>
				<td align="right">
					<a href=${pageContext.request.contextPath}/index.htm?action=toggleHideCreated >
					<c:choose>
						<c:when test="${showCreatedFlg }">
							<c:out value="${msgs.hideCreated }"/>
						</c:when>
						<c:otherwise>
							<c:out value="${msgs.showCreated }"/>
						</c:otherwise>
					</c:choose>
					</a>
				</td>
				</tr>
			</tbody>
		</table>
	</c:if>
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


<!--  list of site requests -->
	<c:if test="${not empty requestList }">
		<table class="listHier noline">
			<tbody>
				<tr>
				<td>
					<c:out value="${msgs.item_request_list }" />
				<td>
				</td>
				<td align="right">
					<a href=${pageContext.request.contextPath}/index.htm?action=toggleHideCreated >
					<c:choose>
						<c:when test="${showCreatedFlg }">
							<c:out value="${msgs.hideCreated }"/>
						</c:when>
						<c:otherwise>
							<c:out value="${msgs.showCreated }"/>
						</c:otherwise>
					</c:choose>
					</a>
				</td>
				</tr>
			</tbody>
		</table>

		<table id="requestlist" class="tablesorter" cellspacing="0" cellpadding="0" border="0" summary="site list" >
			<thead>
				<tr>
					<th scope="col"><c:out value="${msgs.item_course_id }"/></th>
					<th scope="col"><c:out value="${msgs.item_course_title }"/></th>
					<th scope="col"><c:out value="${msgs.item_insert_date }"/></th>
					<th scope="col"><c:out value="${msgs.item_site_status }"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="request" items="${requestList }">
					<tr>
						<td style="white-space:nowrap">
							<c:out value="${request.siteId }"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${request.siteTitle }"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${request.insertDate}"/>
						</td>
						<td style="white-space:nowrap" class="centerColumn">
							<c:choose>
								<c:when test="${request.existSite}">
									<c:out value="${msgs.item_site_exist }" />
								</c:when>
								<c:otherwise>
									<c:out value="${msgs.item_site_nonexist }" />
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	<c:if test="${not empty ownersList }">
		<table class="listHier noline">
			<tbody>
				<tr>
				<td>
					<c:out value="${msgs.item_requested_list }" />
				<td>
				</td>
				<td align="right">
					<a href=${pageContext.request.contextPath}/index.htm?action=toggleHideCreated >
					<c:choose>
						<c:when test="${showCreatedFlg }">
							<c:out value="${msgs.hideCreated }"/>
						</c:when>
						<c:otherwise>
							<c:out value="${msgs.showCreated }"/>
						</c:otherwise>
					</c:choose>
					</a>
				</td>
				</tr>
			</tbody>
		</table>

		<table id="requestedlist" class="tablesorter" cellspacing="0" cellpadding="0" border="0" summary="site list" >
			<thead>
				<tr>
					<th scope="col"><c:out value="${msgs.item_course_id }"/></th>
					<th scope="col"><c:out value="${msgs.item_course_title }"/></th>
					<th scope="col"><c:out value="${msgs.item_request_num }"/></th>
					<th scope="col"><c:out value="${msgs.item_create_date }"/></th>
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
							<c:out value="${site.requestNum }"/>
						</td>
						<td style="white-space:nowrap">
							<c:out value="${site.createDate }"/>
						</td>
						<td style="white-space:nowrap" class="centerColumn">
							<c:choose>
								<c:when test="${site.existSite}">
									<c:out value="${msgs.item_site_exist }" />
									<c:if test="${site.status != 0 }">
										<c:if test="${site.status == 9999 }">
											<c:out value="(" /><c:out value="${msgs.item_beginner }" /><c:out value=")" />
										</c:if>
										<c:if test="${site.status != 9999 }">
											<c:out value="(" /><c:out value="${msgs.item_label_department }" /><c:out value="${site.status }" /><c:out value=")" />
										</c:if>
									</c:if>
								</c:when>
								<c:otherwise>
									<a href=${pageContext.request.contextPath}/index.htm?action=createSite&siteId=<c:out value="${site.siteId }"/> target = "_parent">
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
<jsp:directive.include file="/templates/footer.jsp"/>
<script>
<!--
jQuery( function() {
	jQuery( '#requestlist' ).tablesorter();
	jQuery( '#requestedlist' ).tablesorter();
	});
// -->
</script>
