<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<h3><c:out value="${msgs.title_manageCellStatus}"/></h3>
   
<div class="validation">
   <c:out value="${msgs.validation_statusWarning}" />
</div>

<form method="POST">

   <fieldset>
      <legend class="radio">
      	<c:out value="${msgs.legend_changeStatusTo}" />

			<select id="newStatusValue" name="newStatusValue">
				<c:forEach items="${statuses}" var="status">
					<option value="${status}">
						<c:out value="${status}"/>
					</option>
				</c:forEach>
			</select>
		</legend>
      <div class="checkbox indnt1">
         <input type="radio" id="changeUserOnly" name="changeOption" value="changeUserOnly" checked="checked" />
         <label for="changeUserOnly"><c:out value="${msgs.label_forThisUserOnly}"/></label>
      </div>
      <div class="checkbox indnt1">
         <input type="radio" id="changeAll" name="changeOption" value="changeAll" />
         <label for="changeAll"><c:out value="${msgs.label_forAllMatrixUsers}"/></label>
      </div>
      <div class="checkbox indnt1">
         <input type="radio" id="changeGroup" name="changeOption" value="changeGroup" />
         <label for="changeGroup"><c:out value="${msgs.label_forGroupMatrixUsers}"/></label>
         	<select id="groupId" name="groupId">
				<c:forEach items="${groups}" var="group">
					<option value="${group.id}">
						<c:out value="${group.title}" />
					</option>
				</c:forEach>
				<option value="ungrouped"><c:out value="${msgs.text_ungrouped}"/></option>
			</select>
      </div>
   </fieldset>
    
   <div class="act">
      <input name="continue" type="submit" value="<osp:message key="button_continue"/>" accesskey="s" class="active" />
      <input name="cancel" type="submit" value="<osp:message key="button_cancel"/>" accesskey="x" />
   </div>
</form>