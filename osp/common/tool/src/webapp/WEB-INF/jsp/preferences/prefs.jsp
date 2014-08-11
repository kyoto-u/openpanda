<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<fmt:setLocale value="${locale}" />
<fmt:setBundle basename="org.theospi.portfolio.common.bundle.Messages" />

<script type="text/javascript" language="javascript">

<c:if test="${pref.readyToClose}">

	<c:if test="${pref.prefsSavedDivToReturn != ''}">
		parent.dialogutil.showDiv('<c:out value="${pref.prefsSavedDivToReturn}" />');
	</c:if>
	parent.dialogutil.closeDialog('<c:out value="${pref.dialogDivId}" />','<c:out value="${pref.frameId}" />');
	
</c:if>	

	parent.dialogutil.replaceBodyOnLoad("myLoaded();", this);

function myLoaded() {
    resetHeight();
    //don't want to update the parent's height cause that'll jack up the sizing we've already done.
 }
	
</script>

<div class ="portletBody">

<h3><fmt:message key="prefs_title"/></h3>
<div class="instruction">
   <fmt:message key="prefs_instructions">
   	<fmt:param><c:out value="${pref.qualifier_text}"/></fmt:param>
   </fmt:message>
</div>

<form method="post" id="prefsForm">
<osp:form/>
		
		<spring:bind path="pref.notificationOption">
         <c:forTokens var="token" items="3,2,1"
                    delims="," varStatus="loopCount">
            <div class="checkbox indnt1">
            <input type="radio" id="<c:out value="${token}" />" name="<c:out value="${status.expression}"/>" value="${token}"
               <c:if test="${status.value == token}"> checked="checked" </c:if>/>
            <label for="<c:out value="${token}" />">
			   <osp:message key="prefs_opt${token}" />
			   <c:if test="${token == pref.defaultOption}">
			   <span style="color: green"><fmt:message key="prefs_default" /></span>
			   </c:if>
            </label>
         </div>
         </c:forTokens>
        </spring:bind>

<div class="act">
      <input type="submit" name="update" value="<fmt:message key="prefs_update"><fmt:param><c:out value="${pref.qualifier_text}"/></fmt:param></fmt:message>" accesskey="u" class="active" />
      <input type="submit" name="updateAll" value="<fmt:message key="prefs_update_all"><fmt:param><c:out value="${pref.qualifier_text}"/></fmt:param></fmt:message>" accesskey="a" />
      <button name="cancel" title="<fmt:message key="prefs_cancel"/>" value="<fmt:message key="prefs_cancel"/>" accesskey="x" onclick="parent.dialogutil.closeDialog('<c:out value="${pref.dialogDivId}" />','<c:out value="${pref.frameId}" />');return false;">
      	<fmt:message key="prefs_cancel"/>
      </button>
   </div>

</form>

</div>