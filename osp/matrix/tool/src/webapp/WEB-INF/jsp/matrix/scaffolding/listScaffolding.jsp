<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%
  	String thisId = request.getParameter("panel");
  	if (thisId == null) 
  	{
    	thisId = "Main" + org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getId();
 		 }
%>

<script type="text/javascript" language="JavaScript" src="/library/js/jquery-ui-latest/js/jquery-ui.min.js"></script>
<script type="text/javascript" language="JavaScript" src="/osp-common-tool/js/dialog.js"></script>

<link rel="stylesheet" type="text/css" media="all" href="/osp-common-tool/css/dialog.css" />

<script type="text/javascript">
	function resize(){
		mySetMainFrameHeight('<%= org.sakaiproject.util.Web.escapeJavascript(thisId)%>');
	}


function mySetMainFrameHeight(id)
{
	// run the script only if this window's name matches the id parameter
	// this tells us that the iframe in parent by the name of 'id' is the one who spawned us
	if (typeof window.name != "undefined" && id != window.name) return;

	var frame = parent.document.getElementById(id);
	if (frame)
	{

		var objToResize = (frame.style) ? frame.style : frame;
  
    // SAK-11014 revert           if ( false ) {

		var height; 		
		var offsetH = document.body.offsetHeight;
		var innerDocScrollH = null;

		if (typeof(frame.contentDocument) != 'undefined' || typeof(frame.contentWindow) != 'undefined')
		{
			// very special way to get the height from IE on Windows!
			// note that the above special way of testing for undefined variables is necessary for older browsers
			// (IE 5.5 Mac) to not choke on the undefined variables.
 			var innerDoc = (frame.contentDocument) ? frame.contentDocument : frame.contentWindow.document;
			innerDocScrollH = (innerDoc != null) ? innerDoc.body.scrollHeight : null;
		}
	
		if (document.all && innerDocScrollH != null)
		{
			// IE on Windows only
			height = innerDocScrollH;
		}
		else
		{
			// every other browser!
			height = offsetH;
		}
   // SAK-11014 revert		} 

   // SAK-11014 revert             var height = getFrameHeight(frame);

		// here we fudge to get a little bigger
		var newHeight = height + 40;

		// but not too big!
		if (newHeight > 32760) newHeight = 32760;

		// capture my current scroll position
		var scroll = findScroll();

		// resize parent frame (this resets the scroll as well)
		objToResize.height=newHeight + "px";

		// reset the scroll, unless it was y=0)
		if (scroll[1] > 0)
		{
			var position = findPosition(frame);
			parent.window.scrollTo(position[0]+scroll[0], position[1]+scroll[1]);
		}
	}
}

</script>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request">
   <jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/>
</jsp:useBean>

<c:set var="date_format">
	<osp:message key="dateFormat_list" />
</c:set>

<c:if test="${!myworkspace}">
  <osp-c:authZMap prefix="osp.matrix.scaffolding.revise." var="canRevise" useSite="true"/>
  <osp-c:authZMap prefix="osp.matrix.scaffolding.delete." var="canDelete" useSite="true"/>
  <osp-c:authZMap prefix="osp.matrix.scaffolding.publish." var="canPublish" useSite="true"/>
  <osp-c:authZMap prefix="osp.matrix.scaffolding.export." var="canExport" useSite="true"/>
  <osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" useSite="true"/>
</c:if>


<script type="text/javascript">
	<!--
		function toggle_visibility(id) {
		   var e = document.getElementById(id);
		   var elabel = document.getElementById('toggle' + id);
		   if(e.style.display == 'block')
		   {
			  e.style.display = 'none';
			  elabel.src='/library/image/sakai/expand.gif'
			  elabel.title='<c:out value="${msgs.hideshowdesc_toggle_show}"/>'
			  resizeFrame('shrink');
			}
		   else
		   {
			  e.style.display = 'block';
			  elabel.src='/library/image/sakai/collapse.gif'
			  elabel.title='<c:out value="${msgs.hideshowdesc_toggle_hide}"/>'
			  resizeFrame();
			}  
		}
		function resizeFrame(updown) {
		  var frame = parent.document.getElementById( window.name );
		  if( frame ) {
			if(updown=='shrink')
			{
			var clientH = document.body.clientHeight + 30;
		  }
		  else
		  {
		  var clientH = document.body.clientHeight + 30;
		  }
			$( frame ).height( clientH );
		  } else {
			throw( "resizeFrame did not get the frame (using name=" + window.name + ")" );
		  }
		}
			//-->

</script>

<div class="navIntraTool">
<c:if test="${!myworkspace && (can.create || isMaintainer)}">
   
        <c:if test="${can.create}">
            <a href="<osp:url value="addScaffolding.osp?scaffolding_id=${matrixContents.scaffolding.id}"/>"><c:out value="${msgs.action_create}"/></a> 
            <c:if test="${empty matrixContents.scaffolding}">
             <a href="<osp:url value="importScaffolding.osp"/>" title='<c:out value="${msgs.action_import_title}"/>' ><c:out value="${msgs.action_import}"/></a> 
            </c:if>     
        </c:if> 
        <c:if test="${isMaintainer}">
        	<a href="<osp:url value="sakai.siteassociation.siteAssoc.helper/showSiteAssocs"></osp:url>"
               title='<c:out value="${msgs.association_title}"/>'><c:out value="${msgs.action_association}"/></a>
        
        
             <a href="<osp:url value="osp.permissions.helper/editPermissions">
               <osp:param name="message"><c:out value='${msgs.action_message_setPermission}'/></osp:param>
               <osp:param name="name" value="scaffolding"/>
               <osp:param name="qualifier" value="${worksite.id}"/>
               <osp:param name="returnView" value="listScaffoldingRedirect"/>
               </osp:url>"
               title='<c:out value="${msgs.action_permissions_title}"/>' >
            <c:out value="${msgs.action_permissions}"/>
             </a>
         </c:if>
</c:if>
    <div id="dialogDiv" style="display:none">
       <iframe id="dialogFrame" width="100%" height="100%" frameborder="0"></iframe>
    </div>
         <a href="#"
         	onclick="dialogutil.openDialog('#dialogDiv', '#dialogFrame', '<osp:url value="osp.prefs.helper/prefs">
         		<osp:param name="dialogDivId" value="#dialogDiv" />
         		<osp:param name="typeKey" value="${typeKey}" />
         		<osp:param name="qualifier_text"><fmt:message key="prefs_qualifier"/></osp:param>
         		<osp:param name="prefsSiteSavedDiv" value="#prefsSiteSavedDiv" />
         		<osp:param name="prefsAllSavedDiv" value="#prefsAllSavedDiv" />
         		<osp:param name="toolId" value="osp.matrix" />
         		<osp:param name="frameId" value="#dialogFrame" />
         		</osp:url>')"
               title="<fmt:message key="action_prefs"/>"><fmt:message key="action_prefs"/></a>
         
    </div>

<c:if test="${toolPermissionSaved}">
	<div class="success"><c:out value="${msgs.changesSaved}"/></div>	
</c:if>

	<div class="success" id="prefsSiteSavedDiv" style="display:none"><fmt:message key="prefs_saved_site"/></div>	
	<div class="success" id="prefsAllSavedDiv" style="display:none"><fmt:message key="prefs_saved_all"/></div>	


<div class="navPanel">
	<div class="viewNav">
		<c:if test="${can.create}">
			<h3><c:out value="${msgs.title_matrixManager}"/></h3>
		</c:if>	
		<c:if test="${!(can.create)}">
			<h3><c:out value="${msgs.title_matrixUser}"/></h3>
		</c:if>
	</div>
	<%--//gsilver: if list is less or equal to 10 omit the pager --%>
	<c:if test="${scaffoldingListSize > 10}">
		<osp:url var="listUrl" value="listScaffolding.osp"/>
		<osp:listScroll listUrl="${listUrl}" className="listNav" />
	</c:if>
</div>
<c:if test="${!(empty scaffolding)}">
	<c:set var="studentView" 
		value="${(!canPublish.any && !canPublish.own && !canRevise.any && !canRevise.own && !canDelete.any 
					&& !canDelete.own && !canExport.any && !canExport.own) || myworkspace}" />
	<div>
	<table class="listHier lines nolines" cellspacing="0"  border="0" summary="<c:out value="${msgs.list_matrix_summary}"/>">
	   <thead>
		  <tr>
			 <!-- matrix title -->
			 <th scope="col">
				<c:if test="${sortBy == 'title' && sortAscending == true }">
		 			<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="title"/>
	               			<osp:param name="ascending" value="false"/>
	               			</osp:url>">
	               		<c:out value="${msgs.table_header_name}"/>
	               	</a>
	               	<img src="img/sortascending.gif"/>
				</c:if>
		 		<c:if test="${sortBy == 'title' && sortAscending == false }">
		 			<a href="<osp:url value="listScaffolding.osp">
	               		<osp:param name="sort" value="title"/>
	               		<osp:param name="ascending" value="true"/>
	               		</osp:url>">
	               		<c:out value="${msgs.table_header_name}"/>
	               	</a>
	               	<img src="img/sortdescending.gif"/>
		 		</c:if>
		 		<c:if test="${sortBy != 'title'}">
		 			<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="title"/>
	               			<osp:param name="ascending" value="true"/>
	               			</osp:url>">
	               		<c:out value="${msgs.table_header_name}"/>
	               	</a>
		 		</c:if>
			 </th>
			 <c:if test="${!studentView || myworkspace}">
			 	<th scope="col"></th>
			 </c:if>
			 <!-- matrix owner -->
			 <c:if test="${!studentView}">
			 	<th scope="col">
			 		<c:if test="${sortBy == 'owner' && sortAscending == true }">
				 		<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="owner"/>             		
	               			<osp:param name="ascending" value="false"/>
	               			</osp:url>">
	               			<c:out value="${msgs.table_header_owner}"/> 
	               		</a>
	               		<img src="img/sortascending.gif"/>
					</c:if>
			 		<c:if test="${sortBy == 'owner' && sortAscending == false }">
			 			<a href="<osp:url value="listScaffolding.osp">
		               		<osp:param name="sort" value="owner"/>
		               		<osp:param name="ascending" value="true"/>
		               		</osp:url>">
		               		<c:out value="${msgs.table_header_owner}"/>
		               	</a>
		               	<img src="img/sortdescending.gif"/>
			 		</c:if> 
			 		<c:if test="${sortBy != 'owner'}">
			 			<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="owner"/>             		
	               			<osp:param name="ascending" value="true"/>
	               			</osp:url>">
	               			<c:out value="${msgs.table_header_owner}"/>
	               		</a>
			 		</c:if>	
			 	</th>
			 </c:if>
			 <!-- matrix status (publish/preview) -->
				<th scope="col">
					<c:if test="${sortBy == 'published' && sortAscending == true }">
				 		<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="published"/>             		
	               			<osp:param name="ascending" value="false"/>
	               			</osp:url>">
	               			<c:out value="${msgs.table_header_published}"/> 
	               		</a>
	               		<img src="img/sortascending.gif"/>
					</c:if>
			 		<c:if test="${sortBy == 'published' && sortAscending == false }">
			 			<a href="<osp:url value="listScaffolding.osp">
		               		<osp:param name="sort" value="published"/>
		               		<osp:param name="ascending" value="true"/>
		               		</osp:url>">
		               		<c:out value="${msgs.table_header_published}"/> 
		               	</a>
		               	<img src="img/sortdescending.gif"/>
			 		</c:if> 
			 		<c:if test="${sortBy != 'published'}">
			 			<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="published"/>             		
	               			<osp:param name="ascending" value="true"/>
	               			</osp:url>">
	               			<c:out value="${msgs.table_header_published}"/>
	               		</a>
			 		</c:if>	
				</th>
			 <!-- matrix last modified date -->
			 <c:if test="${!studentView}">
				 <th scope="col">
			 		<c:if test="${sortBy == 'modified' && sortAscending == true }">
				 		<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="modified"/>             		
	               			<osp:param name="ascending" value="false"/>
	               			</osp:url>">
	               			<c:out value="${msgs.table_header_modified}"/>
	               		</a>
	               		<img src="img/sortascending.gif"/>
					</c:if>
			 		<c:if test="${sortBy == 'modified' && sortAscending == false }">
			 			<a href="<osp:url value="listScaffolding.osp">
		               		<osp:param name="sort" value="modified"/>
		               		<osp:param name="ascending" value="true"/>
		               		</osp:url>">
		               		<c:out value="${msgs.table_header_modified}"/> 
		               	</a>
		               	<img src="img/sortdescending.gif"/>
			 		</c:if> 
			 		<c:if test="${sortBy != 'modified'}">
			 			<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="modified"/>             		
	               			<osp:param name="ascending" value="true"/>
	               			</osp:url>">
	               			<c:out value="${msgs.table_header_modified}"/>
	               		</a>
			 		</c:if>			 
				 </th>
			 </c:if>
			 <!-- matrix worksite -->
			 <c:if test="${myworkspace}">
			 	<th scope="col">
				 	<c:if test="${sortBy == 'worksite' && sortAscending == true }">
				 		<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="worksite"/>             		
	               			<osp:param name="ascending" value="false"/>
	               			</osp:url>">
	               			<c:out value="${msgs.table_header_worksite}"/> 
	               		</a>
	               		<img src="img/sortascending.gif"/>
					</c:if>
			 		<c:if test="${sortBy == 'worksite' && sortAscending == false }">
			 			<a href="<osp:url value="listScaffolding.osp">
		               		<osp:param name="sort" value="worksite"/>
		               		<osp:param name="ascending" value="true"/>
		               		</osp:url>">
		               		<c:out value="${msgs.table_header_worksite}"/> 
		               	</a>
		               	<img src="img/sortdescending.gif"/>
			 		</c:if> 
			 		<c:if test="${sortBy != 'worksite'}">
			 			<a href="<osp:url value="listScaffolding.osp">
	               			<osp:param name="sort" value="worksite"/>             		
	               			<osp:param name="ascending" value="true"/>
	               			</osp:url>">
	               			<c:out value="${msgs.table_header_worksite}"/>
	               		</a>
			 		</c:if>	
			   </th>
			 </c:if>
		  </tr>
	   </thead>
	   <tbody>
		  <c:forEach var="dScaffold" items="${scaffolding}">
			<tr>
				<td style="white-space: nowrap">
				<h4 style="display:inline">
					
					
					<%-- if there is a description and user can create, show a toggle to open description, otherwise not--%>
					<c:if test="${!(empty dScaffold.scaffolding.description)}">		
						<a name="viewDesc" id="viewDesc" class="show" href="#" onclick="$(this).next('.hide').toggle();$('div.toggle${dScaffold.scaffolding.id.value}:first', $(this).parents('div:first')).slideToggle(resize);$(this).toggle();">
							<img  id='toggle<c:out value="${dScaffold.scaffolding.id.value}" />'  src="/library/image/sakai/expand.gif" style="padding-top:4px;width:13px" title='<c:out value="${msgs.hideshowdesc_toggle_show}"/>'>
						</a>
				
			
						<a name="hideDesc" id="hideDesc" class="hide" style="display:none" href="#" onclick="$(this).prev('.show').toggle(); $('div.toggle${dScaffold.scaffolding.id.value}:first', $(this).parents('div:first')).slideToggle(resize);$(this).toggle();">
							<img  id='toggle<c:out value="${dScaffold.scaffolding.id.value}" />'  src="/library/image/sakai/collapse.gif" style="padding-top:4px;width:13px" title='<c:out value="${msgs.hideshowdesc_toggle_hide}"/>'>
						</a>				
					</c:if>
					<c:if test="${(empty dScaffold.scaffolding.description)}">						
							<img  src="/library/image/sakai/s.gif" style="width:13px" />					
					</c:if>	
					<c:if test="${(dScaffold.scaffolding.published || dScaffold.scaffolding.preview)}">
						<a href="<osp:url value="viewMatrix.osp"/>&scaffolding_id=<c:out value="${dScaffold.scaffolding.id.value}" />" title='<c:out value="${msgs.scaffolding_link_title}"/>' >
					</c:if>
					<c:out value="${dScaffold.scaffolding.title}" />
					<c:if test="${(dScaffold.scaffolding.published || dScaffold.scaffolding.preview)}">
						</a>
					</c:if>
				</h4>
				</td>
				<c:if test="${myworkspace}">
					<td>
						<div class="itemAction">
							<a href="<c:out value="${dScaffold.scaffoldingToolUrl}" />" title='<c:out value="${msgs.scaffolding_goToTool}"/>' target="_top" >
							       	<c:out value="${msgs.scaffolding_goToTool}"/>
						    </a>
						</div>
					</td>
				</c:if>
			
				
				<c:if test="${!studentView}">
				<td>
					<c:set var="hasFirstAction" value="false" />
					<div class="itemAction">
						<c:if test="${(canPublish.any || (canPublish.own && dScaffold.scaffolding.owner == osp_agent)) && !dScaffold.scaffolding.preview && !dScaffold.scaffolding.published}">
							<c:set var="hasFirstAction" value="true" />
							<a href="<osp:url value="previewScaffolding.osp"/>&scaffolding_id=<c:out value="${dScaffold.scaffolding.id.value}" />" title='<c:out value="${msgs.action_preview}"/> <c:out value="${dScaffold.scaffolding.title}" />' ><c:out value="${msgs.action_preview}"/></a>
						</c:if>
						 <c:if test="${(canPublish.any || (canPublish.own && dScaffold.scaffolding.owner == osp_agent)) && !dScaffold.scaffolding.published && dScaffold.scaffolding.preview}">
							<c:set var="hasFirstAction" value="true" />
							<a href="<osp:url value="publishScaffoldingConfirmation.osp"/>&scaffolding_id=<c:out value="${dScaffold.scaffolding.id.value}" />"  title='<c:out value="${msgs.action_publish}"/> <c:out value="${dScaffold.scaffolding.title}" />'><c:out value="${msgs.action_publish}"/></a>
						 </c:if>
						 <c:if test="${(canRevise.any || (canRevise.own && dScaffold.scaffolding.owner == osp_agent)) && !useExperimentalMatrix}">
							 <c:if test="${hasFirstAction}" > | </c:if>
							 <c:set var="hasFirstAction" value="true" />
							 <a href="<osp:url value="viewScaffolding.osp"/>&scaffolding_id=<c:out value="${dScaffold.scaffolding.id.value}" />" title='<c:out value="${msgs.table_action_edit}"/> <c:out value="${dScaffold.scaffolding.title}" />'><c:out value="${msgs.table_action_edit}"/></a>
						 </c:if>
						 <c:if test="${(canRevise.any || (canRevise.own && dScaffold.scaffolding.owner == osp_agent)) && useExperimentalMatrix}">
							 <c:if test="${hasFirstAction}" > | </c:if>
							 <c:set var="hasFirstAction" value="true" />
							<a href="<osp:url value="prettyScaffolding.osp"/>&scaffolding_id=<c:out value="${dScaffold.scaffolding.id.value}" />" title='<c:out value="${msgs.table_action_edit}"/> <c:out value="${dScaffold.scaffolding.title}" />'><c:out value="${msgs.table_action_edit}"/></a>
						 </c:if>
						 <c:if test="${(canDelete.any || (canDelete.own && dScaffold.scaffolding.owner == osp_agent))}">
							<c:if test="${hasFirstAction}" > | </c:if>
							<c:set var="hasFirstAction" value="true" />
							<a href="<osp:url value="deleteScaffoldingConfirmation.osp"/>&scaffolding_id=<c:out value="${dScaffold.scaffolding.id.value}" />"  title='<c:out value="${msgs.table_action_delete}"/> <c:out value="${dScaffold.scaffolding.title}" />'><c:out value="${msgs.table_action_delete}"/></a>
						 </c:if>
				
						 <c:if test="${(canExport.any || (canExport.own && dScaffold.scaffolding.owner == osp_agent))}">
							<c:if test="${hasFirstAction}" > | </c:if>
							<c:set var="hasFirstAction" value="true" />
							 <a href="<osp:url includeQuestion="false" value="/repository/1=1"/>&manager=matrixManager&scaffoldingId=<c:out value="${dScaffold.scaffolding.id.value}"/>/<c:out value="${dScaffold.scaffolding.title}" />.zip" title='<c:out value="${msgs.table_action_export}"/> <c:out value="${dScaffold.scaffolding.title}" />'><c:out value="${msgs.table_action_export}"/></a>
						</c:if>
						<c:if test="${(isMaintainer || canRevise.any || (canRevise.own && dScaffold.scaffolding.owner == osp_agent))}">
							<c:if test="${hasFirstAction}" > | </c:if>
							<c:set var="hasFirstAction" value="true" />
							<a
								href="<osp:url value="osp.permissions.helper/editPermissions_new">
				               <osp:param name="message"><c:out value='${msgs.action_message_setMatrixPermission}'/></osp:param>
				               <osp:param name="name" value="scaffoldingSpecific"/>
				               <osp:param name="qualifier" value="${dScaffold.scaffolding.reference}"/>
				               <osp:param name="returnView" value="listScaffoldingRedirect"/>
				               </osp:url>"
								title='<c:out value="${msgs.action_permissions_title}"/> <c:out value="${dScaffold.scaffolding.title}" />'> <c:out
								value="${msgs.action_permissions}" /> </a>
						</c:if>
						<%--  Hiding this functionality as it hasn't gotten much testing
								<c:if test="${isMaintainer && empty dScaffold.scaffolding.exposedPageId}">
									<c:if test="${hasFirstAction}" > | </c:if>
									<c:set var="hasFirstAction" value="true" />
									<a href="<osp:url value="exposedScaffolding.osp"/>&expose=true&scaffolding_id=<c:out value="${dScaffold.scaffolding.id.value}"/>">
									   <c:out value="${msgs.table_action_expose}"/>
									</a>
								</c:if>
								
								<c:if test="${isMaintainer && not empty dScaffold.scaffolding.exposedPageId}">
									<c:if test="${hasFirstAction}" > | </c:if>
									<c:set var="hasFirstAction" value="true" />
									<a href="<osp:url value="exposedScaffolding.osp"/>&expose=false&scaffolding_id=<c:out value="${dScaffold.scaffolding.id.value}"/>">
									   <c:out value="${msgs.table_action_unexpose}"/>
									</a>
								</c:if>
						   --%>     
					 </div>
				 </td>
				 </c:if>
				 <c:if test="${!studentView}">
				 <td>
						<c:out value="${dScaffold.scaffolding.owner.displayName}" />
				</td>
				</c:if>
				  <td>
					 <c:if test="${dScaffold.scaffolding.published}">
						<c:out value="${msgs.scaffolding_published_true}"/>
					 </c:if>
					 <c:if test="${dScaffold.scaffolding.preview}">
						<c:out value="${msgs.scaffolding_published_preview}"/>
					 </c:if>
					 <c:if test="${!dScaffold.scaffolding.published && !dScaffold.scaffolding.preview}">
						<c:out value="${msgs.scaffolding_published_false}"/>
					 </c:if>
				 </td>
				 <c:if test="${!studentView}">
					 <td>
					 	<fmt:formatDate
							value="${dScaffold.scaffolding.modifiedDate}"
							pattern="${date_format}" />
					 </td>
				 </c:if>
				<c:if test="${myworkspace}">
				 <td>
					<c:out value="${dScaffold.scaffolding.worksiteName}" />
				 </td>
				</c:if>
			</tr>
			<%-- if there is a description and user can create,  description visibility can be toggled, if user cannot create, just show--%>
			<c:if test="${!(empty dScaffold.scaffolding.description)}">
				<tr class="exclude">
					<td colspan="5">					
							<div class="toggle${dScaffold.scaffolding.id.value} instruction indnt2 textPanelFooter" style="padding:0;margin:0;display:none">
								<c:out value="${dScaffold.scaffolding.description}" escapeXml="false"/>
							</div>													
					</td>
				</tr>
			</c:if>	
	
		  </c:forEach>
		</tbody>
	</table>
	</div>
</c:if> 
<c:if test="${(empty scaffolding)}">
	<c:if test="${can.create}">
		<c:out value="${msgs.table_empty_list_message_create}" />
	</c:if>	
	<c:if test="${!(can.create)}">
		<c:out value="${msgs.table_empty_list_message}" />
	</c:if>
</c:if>
