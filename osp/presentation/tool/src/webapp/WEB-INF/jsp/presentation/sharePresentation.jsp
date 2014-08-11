<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>
<c:set var="pres_active_page" value="share" />
<%@ include file="/WEB-INF/jsp/presentation/presentationTop.inc"%>
 
<script type="text/javascript">
    $(document).ready(function(){
		$("#hideUrl").hide();
		$("#urlText").hide();

	$(".multSelectHolder").each(function(){
		if ($(this).height() > 180) {
		$(this).addClass("oversize")
}
})

	$(".multSelectHolder input:checkbox").click( function() {
		if ($(this).attr('checked')) {
		$(this).parents("li").addClass("selected")
		}
		else
		{
		$(this).parents("li").removeClass("selected")
		}
})
					
					
});
</script>

<script  type ="text/javascript">
$(document).ready(function() {
	setupMessageListener("messageHolder", "messageInformation");
	$(".multSelectHolder").each(function(){
		if ($(this).height() > 180) {
		$(this).addClass("oversize")
}
})

	$(".multSelectHolder input:checkbox").click( function() {
		if ($(this).attr('checked')) {
		$(this).parents("li").addClass("selected")
		}
		else
		{
		$(this).parents("li").removeClass("selected")
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

<c:if test="${actionNotify}">
       <div class="messageInformation" id="messageHolder" style="width:20em">
       <c:out value="${msgs.confirm_notify}"/>
       </div>
</c:if>

<form method="post" name="mainForm">

<p><c:out value="${msgs.share_when_active}"/></p>
		
   <c:choose>
     <c:when test="${empty shareList}">
       <h3 style="padding:0;margin:0"><c:out value="${msgs.pres_share_none}"/></h3>
         <p><a href="<osp:url value="sharePresentationMore.osp"/>&id=<c:out value="${presentation.id.value}" />"  class="addUsersSmall"><span><c:out value="${msgs.pres_share_add}"/></span></a></p>
     </c:when>
     
     <c:otherwise>
         <table width="80%"   style="margin-top:1em">
         <thead>
         <tr>
         <td><h3 style="padding:0;margin:0"><c:out value="${msgs.pres_share_list}"/></h3></td>
         <td align="right" class="specialLink">
         <a href="<osp:url value="sharePresentationMore.osp"/>&id=<c:out value="${presentation.id.value}" />"  class="addUsersSmall"><span><c:out value="${msgs.pres_share_more}"/></span></a>
         </td>
         </tr>
         </thead>
         
<tbody>
	<tr>
		<td  colspan="2">
			<ul class="multSelectHolder">
				<c:forEach var="shareMember" items="${shareList}"  varStatus="loopCounter"> 
					<c:choose>
						<c:when test="${(loopCounter.index mod 2) == 0}">
							<li class="checkbox odd">
						</c:when>
						<c:when test="${(loopCounter.index mod 2) ==1}">
							<li class="checkbox even">
						</c:when>
					</c:choose>
					<label for="${shareMember.id.value}">
						<input type="checkbox" name="${shareMember.id.value}" id="${shareMember.id.value}" />
						<c:out value="${shareMember.displayName}" />
					</label>
					</li> 
				</c:forEach>
			</ul>
		</td>
	</tr>
</tbody>

         <tfoot>
         <tr>
         <td colspan="2" align="right" class="specialLink">
            <a href="javascript:document.mainForm.submit();"  class="removeSmall"><span><c:out value="${msgs.pres_share_rem}"/></span></a> 
         </td>
         </tr>
         </tfoot>
         </table>
     </c:otherwise>
   </c:choose>
	
   <input type="hidden" name="notify" />
   <p>
      <a class="shareEmail" href="javascript:document.mainForm.notify.value='true'; document.mainForm.submit();"><c:out value="${msgs.send_email}"/></a>
   </p><br/>
   
   <h3>
      <c:out value="${msgs.pres_share_collab}"/>
   </h3>

   <div class="checkbox">
      <input type="hidden" name="pres_share_collab" value="${pres_share_collab}" />
      
      <input type="checkbox" name="collab_checkbox" id="collab_checkbox"
         <c:if test="${pres_share_collab=='true'}"> checked="checked"</c:if>
         onclick="document.mainForm.pres_share_collab.value=(document.mainForm.collab_checkbox.checked) ? 'true' :'false'; document.mainForm.submit();"
      />
      <label for="pres_share_collab">
         <c:out value="${msgs.pres_share_collab_edit}"/>
      </label>
   </div>	
	
   <h3>
      <c:out value="${msgs.pres_share_this}"/>
   </h3>

   <div class="checkbox">
      <input type="hidden" name="pres_share_public" value="${pres_share_public}" />
      
      <input type="checkbox" name="public_checkbox" id="public_checkbox"
         <c:if test="${pres_share_public=='true'}"> checked="checked"</c:if>
         onclick="document.mainForm.pres_share_public.value=(document.mainForm.public_checkbox.checked) ? 'true' :'false'; document.mainForm.submit();"
      />
      <label for="pres_share_public">
         <c:out value="${msgs.pres_share_public}"/>
      </label>
       <a id="showUrl" href="#" onclick="$('#hideUrl').show(); $('#showUrl').hide(); $('#urlText').show();"><c:out value="${msgs.pres_share_showurl}"/></a>
       <a id="hideUrl" href="#" onclick="$('#showUrl').show(); $('#hideUrl').hide(); $('#urlText').hide();"><c:out value="${msgs.pres_share_hideurl}"/></a>
       <input id="urlText" type="text" readonly="true" name="publicUrl" value="${publicUrl}" size="120"/>
   </div>	
	
   </div> <%--end of #tabNavPanel --%>
</form>
