<script type="text/javascript">
<!--
  function confirmDelete() {
    return confirm("<fmt:message key="comments_confirmDelete"/>");
  }
-->
</script>
<div class="commentArea">
	<div class="commentAreaExisting">
		<a name="comments"></a>
		<h4><fmt:message key="title_comments"/></h4>
		
		<c:if test="${!empty comments}">
			<c:forEach begin="0" items="${comments}" var="comment" >
				<a name="comment<c:out value="${comment.id.value}"/>"></a>
				<h5 style="padding:.2em 0;margin:0">
					<c:if test="${comment.visibility == 1}">
						<fmt:message key="comments_private"/> 
					</c:if>
					<c:if test="${comment.visibility == 2}">
						<fmt:message key="comments_shared"/> 
					</c:if>
					<c:if test="${comment.visibility == 3}">
						<fmt:message key="comments_public"/> 
					</c:if>      
					<fmt:message key="comments_from"/>
					<c:choose>
						<c:when test="${comment.creator == currentAgent}">
							<fmt:message key="comments_yourself"/>
						</c:when>
						<c:otherwise>
							<c:out value="${comment.creator.displayName}"/>
						</c:otherwise>
					</c:choose>
					(<c:set var="dateFormat"><fmt:message key="dateFormat_Middle"/></c:set><fmt:formatDate value="${comment.created}" pattern="${dateFormat}"/>)
					<c:set var="firstAction" value="false" />
					<c:if test="${comment.creator == currentAgent}">
						<c:set var="firstAction" value="true" />         
						&nbsp;<a href='<osp:url value="deleteComment.osp"/>&id=<c:out value="${comment.id.value}" />' ><fmt:message key="action_delete"/></a>
					</c:if>
					<c:choose>
						<c:when test="${comment.creator == currentAgent && comment.presentation.owner == currentAgent}">
							<!-- you created the comment and are the element owner -->
							<c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
							<c:if test="${comment.visibility == 1}">
								<c:set var="firstAction" value="true" />
								<a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePublic"/></a>
							</c:if>
							<c:if test="${comment.visibility != 1}">
								<c:set var="firstAction" value="true" />
								<a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=1&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePrivate"/></a>
							</c:if>
						</c:when>
						<c:when test="${comment.creator == currentAgent}">
							<!-- you created the comment and are not the element owner -->
							<c:if test="${comment.visibility == 1}">
								<c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
								<a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=2&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makeShared"/></a>
								&nbsp;<span class="pipe">|</a>&nbsp;
								<a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePublic"/></a>
							</c:if>
							<c:if test="${comment.visibility == 2}">
								<c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
									<a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=1&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePrivate"/></a>
									&nbsp;<span class="pipe">|</a>&nbsp;
									<a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePublic"/></a>
							</c:if>
							<c:if test="${comment.visibility == 3}">
								<c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
								<a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=1&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePrivate"/></a>
								&nbsp;<span class="pipe">|</a>&nbsp;
								<a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=2&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makeShared"/></a>
							</c:if>
						</c:when>
						<c:when test="${comment.presentation.owner == currentAgent}">
							<!-- you are the element owner and didn't create the comment -->
							<c:if test="${firstAction}">&nbsp;<span class="pipe">|</a>&nbsp;</c:if>
							<c:if test="${comment.visibility == 2}"><a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=3&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makePublic"/></a></c:if>
							<c:if test="${comment.visibility == 3}"><a href="<osp:url value="updateCommentVisibility.osp"/>&visibility=2&id=<c:out value="${comment.id.value}"/>"><fmt:message key="action_makeShared"/></a></c:if>
						</c:when>
					</c:choose>
				</h5>
				<p style="padding:.2em 1em;font-size:.8em">
					<c:out value="${comment.title}"/>&nbsp;-&nbsp;<c:out value="${comment.comment}"/>
				</p>
			</c:forEach>
		</c:if>
		<c:if test="${empty comments}">
			<h4><fmt:message key="comments_noComments"/></h4>
		</c:if>
	</div>
	<div class="commentAreaNew">	
		<c:if test="${!empty currentAgent && currentAgent.displayName != 'anonymous' && allowComments}">
			<h4><fmt:message key="comments_addComment"/></h4>
			<form method="post" action="addComment.osp" >
			<osp:form/>
			<input type="hidden" name="presentationId" value="<c:out value="${presentation.id.value}"/>"/>
			<input type="hidden" name="id" value="<c:out value="${presentation.id.value}"/>"/>
			<input type="hidden" name="sakai.tool.placement.id" value="<c:out value="${placementId}"/>"/>
			<p style="color:#333;font-size:.8em">
				<fmt:message key="instructions_addComment"/> 
				<fmt:message key="instructions_requiredFields"/>
			</p>
			<spring:bind path="newComment.title">
				<p class="commentFormElement" style="font-size:.8em">
					<c:if test="${status.error}">
						<label style="color:red;display:block;padding:.2em" for="<c:out value="${status.expression}"/>-id"><span style="color:red">*</span>
					</c:if>
					<c:if test="${!status.error}">
						<label style="color:#000;display:block;padding:.2em"  for="<c:out value="${status.expression}"/>-id"><span style="color:red">*</span>
					</c:if>	
					<fmt:message key="table_row_title"/>
					</label>
					<input name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>-id"  size="25" maxlength="50" value="<c:out value="${status.value}"/>"/>
					<c:if test="${status.error}"><span style="color:red"><c:out value="${status.errorMessage}" /></span></c:if>
				</p>
			</spring:bind>
	
	
			<spring:bind path="newComment.comment">
				<p class="commentFormElement">
					<c:if test="${status.error}">
						<label style="color:red;display:block;font-size:.8em;padding:.2em" for="<c:out value="${status.expression}"/>-id"><span style="color:red">*</span>
					</c:if>
					<c:if test="${!status.error}">
						<label style="color:#000;display:block;font-size:.8em;padding:.2em"  for="<c:out value="${status.expression}"/>-id"><span style="color:red">*</span>
					</c:if>	
					<fmt:message key="table_row_comment"/>
					<c:if test="${status.error}"><span style="color:red"><c:out value="${status.errorMessage}" /></span></c:if>
					</label>
					<textarea name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>-id"  value="" cols="80" rows="5" maxlength="4000"></textarea>
				</p>
		  </spring:bind>
	
	
		<spring:bind path="newComment.visibility">
			<h5 style="padding:.3em 0;margin:0">
				<fmt:message key="legend_makeCommentVisibleTo"/>
			</h5>
			<p style="font-size:.8em;margin:0;padding-left:2em">
				<input type="radio" id="onlyme" name='<c:out value="${status.expression}"/>' <c:if test="${status.value == 1}">checked="checked"</c:if> value="1"/>
				<label for="onlyme"><fmt:message key="label_onlyMe"/></label>
			</p>
			<c:if test="${currentAgent != presentation.owner}">
				<p style="font-size:.8em;margin:0;padding-left:2em">
					<input type="radio" id="ownerAndI" name='<c:out value="${status.expression}"/>' <c:if test="${status.value == 2}">checked="checked"</c:if> value="2"/>
					<label for="ownerAndI"><fmt:message key="label_ownerAndI"/></label>
				</p>
			</c:if>
			<p style="font-size:.8em;margin:0;padding-left:2em">
				<input type="radio" id="everyone" name='<c:out value="${status.expression}"/>' <c:if test="${status.value == 3 || status.value == 0}">checked="checked"</c:if> value="3"/>
				<label for="everyone"><fmt:message key="label_everyone"/></label>
			</p>
		</spring:bind>
	</div>	

      <p style="margin:1em 0 ">
         <%-- <input type="submit" name="<fmt:message key="button_saveComment"/>" value="submit" alt="<fmt:message key="button_saveCommentAlt"/>" /> --%>
         <input type="submit" name="submit" value="<fmt:message key="button_saveComment"/>" title="<fmt:message key="button_saveCommentAlt"/>" accesshey="s" />
      </div>
   </form>
   </c:if>
   </div>