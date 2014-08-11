<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>
<form method="get" action="<c:choose><c:when test="${empty returnView}">listPresentation.osp</c:when><c:otherwise><c:out value="${returnView}"/></c:otherwise></c:choose>" name="backtolist">
<input type="hidden" name="id" value="<c:out value="${id}"/>" />

<c:forEach begin="0" end="0" items="${comments}" var="comment">
	<ul  class="smallNavIntraTool " style="margin-top:2em">
		<li>
			<h3 style="display:inline">
				<c:out value="${comment.presentation.name}" />
				(<c:out value="${comment.presentation.template.name}" />) 
			</h3>
		</li>
		<li class="firstItem">
			<span>
				<a target="_blank" href="<osp:url value="viewPresentation.osp"/>&id=<c:out value="${comment.presentation.id.value}" />#comment<c:out value="${comment.id.value}" />"
					title='<c:out value="${msgs.table_comments_link_hint}"/>'>
					<c:out value="${msgs.table_comments_link}"/>
				</a>
			</span>
		</li>
		<li>
			<span>
				<a href="#" onclick="document.backtolist.submit()"><c:choose><c:when test="${empty returnText}"><c:out value="${msgs.table_comments_back}"/></c:when><c:otherwise><c:out value="${msgs[returnText]}"/></c:otherwise></c:choose></a>
			</span>	
		</li>
	</ul>	
</c:forEach>
<c:if test="${not empty comments}">
	<ul class="commentsList">
		<c:forEach begin="0" items="${comments}" var="comment">
			<li>
				<div class="commentMetadata">
					<h4 style="display:inline">
						<c:out value="${comment.title}" />
					</h4>
					- <c:out value="${comment.creator.displayName}" />
					<span class="textPanelFooter">
						<c:set var="dateFormat">
							<c:out value="${msgs.dateFormat_Middle}"/>
						</c:set>
						(<fmt:formatDate value="${comment.created}" pattern="${dateFormat}"/>)
						- <strong class="highlight">
						<c:if test="${comment.visibility == 1}">
							<c:out value="${msgs.comments_private}"/>
						</c:if>
						<c:if test="${comment.visibility == 2}">
							<c:out value="${msgs.comments_shared}"/>
						</c:if>
						<c:if test="${comment.visibility == 3}">
							<c:out value="${msgs.comments_public}"/>
						</c:if>
						</strong>
					</span>
				</div>
				<div class="commentBody"><c:out value="${comment.comment}" /></div>	
			</li>
		</c:forEach>
	</ul>
</c:if>
	<p class="act">
	   <input type="submit" name="_cancel" value="<c:out value="${msgs.button_back}"/>" accesskey="x" class="active" />
	 </p>  
</form>

