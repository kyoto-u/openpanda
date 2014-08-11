<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<!--[if gt IE 5.0]><![if lt IE 7]>
<style type="text/css">
/* for  IE 6 */ 
</style>
<![endif]><![endif]-->
<!--[if gt IE 6.0]>
<style type="text/css">
/* for  IE 7 an 8*/ 
.makeMenuChild{
	margin-left:1em !important;
	}
</style>
<![endif]-->

<c:set var="pres_active_page" value="share" />
<%@ include file="/WEB-INF/jsp/presentation/presentationTop.inc"%>

<script  type ="text/javascript">
var selectCount=0;
$(document).ready(function() {
   $("#back_add").hide();
	setupMessageListener("messageHolder", "messageInformation");
	$(".multSelectHolder").each(function(){
		if ($(this).height() > 180) {
		$(this).addClass("oversize")
}
})

   $(".multSelectHolder input:checkbox").click( function() {
      if ($(this).attr('checked')) {
         $(this).parents("li").addClass("selected")
         selectCount++;
         if ( selectCount == 1 ) {
            $("#back_add").show();
            $("#back").hide();
         }
      }
      else
      {
         $(this).parents("li").removeClass("selected")
         selectCount--;
         if ( selectCount == 0 ) {
            $("#back_add").hide();
            $("#back").show();
         }
      }
})

	jQuery('body').click(function(e) { 
			
		if ( e.target.className !='menuOpen' && e.target.className !='dropdn'  ){
			$('.makeMenuChild').fadeOut();
		}
			else
			{
				if( e.target.className =='dropdn' ){
			targetId=$(e.target).parent('li').attr('id');
			$('.makeMenuChild').hide();
			$('#menu-' + targetId).fadeIn(500);

}
				else{
			targetId=e.target.id;
			$('.makeMenuChild').hide();
			$('#menu-' + targetId).fadeIn(500);
			}}
	});
	});

</script>
<div class="tabNavPanel">

<h3>
   <c:out value="${msgs.title_share_add}"/>
</h3>

<c:if test="${not empty errMsg}">
	<div class="messageValidation">
     <c:out value="${errMsg}" />
	</div>
</c:if>
   
<form method="post" name="mainForm">
<input type="hidden" name="shareBy" value="${shareBy}"/>

<table>
<tr>
  <td>
   <ul class=" inlineMenu" style="margin:0;display:block;border:none;">
   	<li id="0" class="menuOpen"><c:out value="${msgs.share_by}"/>
		<ul id="menu-0" class="makeMenuChild" style="display:none">
	   <c:if test="${!myWorkspace}">
	   <li><a href="javascript:document.mainForm.shareBy.value='share_browse';document.mainForm.submit();"><c:out value="${msgs.share_browse}"/></a> </li>
      </c:if>
	   <c:if test="${hasGroups}">
		  <li><a href="javascript:document.mainForm.shareBy.value='share_group';document.mainForm.submit();"><c:out value="${msgs.share_group}"/></a> </li>
	   </c:if>
	   <li><a href="javascript:document.mainForm.shareBy.value='share_search';document.mainForm.submit();"><c:out value="${msgs.share_search}"/></a> </li>
	   <c:if test="${guestEnabled}">
		  <li><a href="javascript:document.mainForm.shareBy.value='share_email';document.mainForm.submit();"><c:out value="${msgs.share_email}"/></a> </li>
	   </c:if>
	   <c:if test="${!myWorkspace}">
	   <li><a href="javascript:document.mainForm.shareBy.value='share_role';document.mainForm.submit();"><c:out value="${msgs.share_role}"/></a> </li>
      </c:if>
	   <li><a href="javascript:document.mainForm.shareBy.value='share_allrole';document.mainForm.submit();"><c:out value="${msgs.share_allrole}"/></a> </li>
   </ul>
   </li>
      </ul>
</td>
</tr>
</table>

<!-- select groups to to filter -->
<c:if test="${shareBy=='share_group' && hasGroups}">
<blockquote>
   <table width="auto" rules="groups">
   <thead>
   
   <tr>
   <td>
      <span class="messageInstruction"><c:out value="${msgs.share_group_filter}"/></span>
   </td>
   </thead>
         
   <%-- this is a scrollable  box of 180 px height if the content goes over 180px, if less, it will he as high as the contents--%>
   <tbody>
   <c:forEach var="group" items="${groupList}"> 
     <tr><td colspan="2">
     <div class="checkbox">
     <input type="radio" name="groups" id="groups" value="${group.id}"  
         <c:if test="${group.checked}"> checked="checked"</c:if>
         onchange="javascript:document.mainForm.submit();" />
     <label for="groups">
     <c:out value="${group.title}" />
     </label>
     </div>
     </td></tr>
   </c:forEach>
   </tbody>
   </table>
</blockquote>
</c:if>

<c:choose>

<%-- select new users or roles to share with --%>
<c:when test="${shareBy=='share_browse' || shareBy=='share_group' || shareBy=='share_role' || shareBy=='share_allrole'}">

   <c:if test="${empty availList}">
      <span class="messageInstruction">
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
      <c:out value="${msgs.share_no_users}"/> 
      </c:if>
      <c:if test="${shareBy=='share_role'}">
      <c:out value="${msgs.share_no_roles}"/> 
      </c:if>
      <c:if test="${shareBy=='share_allrole'}">
      <c:out value="${msgs.share_no_allroles}"/> 
      </c:if>
      </span>
   </c:if>
   
   <c:if test="${not empty availList}">
   <table style="width:auto">
   <thead>
   <tr>
   <td>
      <span class="messageInstruction">
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
      <c:out value="${msgs.share_user_list}"/> 
      </c:if>
      <c:if test="${shareBy=='share_role' || shareBy=='share_allrole'}">
      <c:out value="${msgs.share_role_list}"/> 
      </c:if>
      </span>
   </td>
   <td style="text-align:right;padding-left:2em;white-space:nowrap" class="specialLink">
      <c:if test="${shareBy=='share_browse' || shareBy=='share_group'}">
      	<a href="javascript:document.mainForm.submit();" class="addSmall"><span><c:out value="${msgs.share_add_users}"/></span></a>
      </c:if>
      <c:if test="${shareBy=='share_role' || shareBy=='share_allrole'}">
         <a href="javascript:document.mainForm.submit();" class="addSmall"><span><c:out value="${msgs.share_add_roles}"/></span></a>
      </c:if>
   </td>
   </tr>
   </thead>
         
   <%-- this will be a scrollable box of 180 px height if the content goes over 180px, 
        if less, it will he as high as the contents and will not have a scrollbar --%>
   <tbody>
   	<tr> 
		<td  colspan="2">
			<ul class="multSelectHolder">
   <c:forEach var="member" items="${availList}" varStatus="loopCounter">
   	<c:choose>
		<c:when test="${(loopCounter.index mod 2) == 0}">
			<li class="checkbox odd">
		</c:when>
		<c:when test="${(loopCounter.index mod 2) ==1}">
			<li class="checkbox even">
		</c:when>
	</c:choose>	
     <label for="${member.id.value}">
     	<input type="checkbox" name="${member.id.value}" id="${member.id.value}" />
     <c:out value="${member.displayName}" />
     </label>
     </li>

   </c:forEach>
     </ul>
         </td>
		 </tr>
		 </tbody>
   <tfoot>
   <tr>
   <td colspan="2">
      <span class="messageInstruction"><c:out value="${msgs.share_hint}"/></span>
   </td>
   </tr>
   </tfoot>
   </table>
   </c:if>
</c:when>

<%-- enter new user or email to share with --%>
<c:when test="${shareBy=='share_search' || shareBy=='share_email'}">
<p class="longttext">
  <table style="width:auto">
  <tr>
  <td>
    <label for="share_enter_userid" style="display:block;padding:.3em">
      <c:if test="${shareBy=='share_search'}">
      <c:out value="${msgs.share_enter_userid}"/>
      </c:if>
      <c:if test="${shareBy=='share_email'}"> 
      <c:out value="${msgs.share_enter_email}"/>
      </c:if>
    </label> 
  </td>
  <td style="text-align:right;padding-left:2em;white-space:nowrap" class="specialLink">
     <a href="javascript:document.mainForm.submit();"  class="addSmall"><span><c:out value="${msgs.share_submit}"/></span></a>
  </td>
  </tr>
  <tr><td colspan="2">
  <input type="text" name="share_user" id="share_user" size="60" />
  </td></tr>
</table>
</p> 
</c:when>

</c:choose>

   <div class="act">
   <c:choose>
      <c:when test="${shareBy=='share_browse' || shareBy=='share_group' || shareBy=='share_role' || shareBy=='share_allrole'}">

         <input id="back_add" name="back_add" type="submit" value="<c:out value="${msgs.button_add_return}" />" class="active" accesskey="b" />
         <input id="back"     name="back" type="submit" value="<c:out value="${msgs.button_return}" />" class="active" accesskey="b" />
      </c:when>
      <c:otherwise>
         <input id="back" name="back" type="submit" value="<c:out value="${msgs.button_add_return}" />" class="active" accesskey="b" />
      </c:otherwise>
    </c:choose>
    </div>
   
</form>
</div>
