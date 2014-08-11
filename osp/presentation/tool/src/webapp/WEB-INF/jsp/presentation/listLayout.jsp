<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<!-- GUID=<c:out value="${newPresentationLayoutId}"/> -->

<osp-c:authZMap prefix="osp.presentation.layout." useSite="true" var="can" />

<c:if test="${can.create || isMaintainer}">
    <div class="navIntraTool">
       <c:if test="${can.create}">
          <a href="<osp:url value="addLayout.osp"/>" title='<c:out value="${msgs.action_add}"/>' >
          <c:out value="${msgs.action_add}"/>
          </a>
       </c:if>
       <c:if test="${isMaintainer && selectableLayout != 'true'}">
          <a href="<osp:url value="osp.permissions.helper/editPermissions">
                <osp:param name="message"><c:out value="${msgs.message_permissionsEdit}"/>
                </osp:param>
                <osp:param name="name" value="presentationLayout"/>
                <osp:param name="qualifier" value="${worksite.id}"/>
                <osp:param name="returnView" value="listLayoutRedirect"/>
                </osp:url>"
                title='<c:out value="${msgs.action_permissions_title}"/>' >
          <c:out value="${msgs.action_permissions}"/>
          </a>
       </c:if>
    </div>
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
			  elabel.title='Show description'
			  resizeFrame('shrink');
			}
		   else
		   {
			  e.style.display = 'block';
			  elabel.src='/library/image/sakai/collapse.gif'
			  elabel.title='Hide description'
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
<script type="text/javascript" language="JavaScript" src="/library/js/jquery.js">
		
</script>
<div class="navPanel">
	<div class="viewNav">
		<h3><c:out value="${msgs.title_presentationLayoutManager}"/></h3>
	</div>	
	<osp:url var="listUrl" value="listLayout.osp"/>
	<osp:listScroll listUrl="${listUrl}" className="listNav" />
</div>	

<c:choose>
	<c:when test="${empty layouts}">
		<p class="instruction"><c:out value="${msgs.layout_table_empty}"/></p>
	</c:when>
	<c:otherwise>
		<table class="listHier lines nolines" cellspacing="0"  cellpadding="0" border="0" summary="<c:out value="${msgs.layout_table_summary}"/>">
		   <thead>
			  <tr>
				 <th scope="col" class="attach"></th>
				 <th scope="col"><c:out value="${msgs.table_header_name}"/></th>
			 <th scope="col"></th>
				 <th scope="col"><c:out value="${msgs.table_header_owner}"/></th>
				 <th scope="col"><c:out value="${msgs.table_header_published}"/></th>
			  </tr>
		   </thead>
		   <tbody>
		  <c:forEach var="layout" items="${layouts}">
			<osp-c:authZMap prefix="osp.presentation.layout." qualifier="${layout.id}" var="isAuthorizedTo" />
			<tr>
			  <td class="attach">
				 <c:if test="${selectedLayout == layout.id}">      	
					<img src="<osp:url value="/img/arrowhere.gif"/>" alt='<c:out value="${msgs.table_image_title}" />' />
			  </c:if>
			  </td>
			  <td>
				<h4>
				<c:if test="${!(empty layout.description)}">	
					<a href="#"  onclick="toggle_visibility('<c:out value="${layout.id.value}" />')"><img  id="toggle<c:out value="${layout.id.value}" />"  src="/library/image/sakai/expand.gif" style="padding-top:4px;width:13px" title='<c:out value="${msgs.hideshowdesc_toggle_show}"/>' /></a>
				</c:if>
				<c:if test="${(empty layout.description)}">
					<img  src="/library/image/sakai/s.gif" style="width:13px" />
				</c:if>	
				<c:out value="${layout.name}" />
			</h4>
			</td>
			<td  class="itemAction" style="white-space:nowrap">
				 <c:set var="hasFirstAction" value="false" />
					 <c:if test="${can.publish && (layout.globalState == 0 || layout.globalState == 1) && isGlobal}">
						 <c:if test="${hasFirstAction}" > | </c:if>
						 <c:set var="hasFirstAction" value="true" />
						 <a href="<osp:url value="publishLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />"><c:out value="${msgs.table_action_publish}"/></a>
					 </c:if>
					 
					 <c:if test="${isAuthorizedTo.suggestPublish && layout.globalState == 0 && selectableLayout != 'true' && !isGlobal}">
						 <c:if test="${hasFirstAction}" > | </c:if>
						 <c:set var="hasFirstAction" value="true" />
						 <a href="<osp:url value="publishLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />&suggest=true"><c:out value="${msgs.table_action_suggeset_publish}"/></a>
					 </c:if>
					 
					 <c:if test="${isAuthorizedTo.edit}">
					   <c:if test="${hasFirstAction}" > | </c:if>
					   <a href="<osp:url value="editLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />"><c:out value="${msgs.table_action_edit}"/></a>
					   <c:set var="hasFirstAction" value="true" />
					 </c:if>
			
					 <c:if test="${isAuthorizedTo.delete}">
						 <c:if test="${hasFirstAction}" > | </c:if>
						 <c:set var="hasFirstAction" value="true" />
						 <a onclick="return confirmDeletion();"
						   href="<osp:url value="deleteLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />"><c:out value="${msgs.table_action_delete}"/></a>
					 </c:if>
					 
					 <c:if test="${selectableLayout == 'true' and selectedLayout != layout.id.value and (layout.globalState == 2 or layout.owner == osp_agent)}">
						 <c:if test="${hasFirstAction}" > | </c:if>
						 <c:set var="hasFirstAction" value="true" />
						 <a href="<osp:url value="selectLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />&selectAction=on"><c:out value="${msgs.table_action_select}"/></a>
					 </c:if>
					 
					 <c:if test="${selectableLayout == 'true' and selectedLayout == layout.id.value}">
						 <c:if test="${hasFirstAction}" > | </c:if>
						 <c:set var="hasFirstAction" value="true" />
						 <a href="<osp:url value="selectLayout.osp"/>&layout_id=<c:out value="${layout.id.value}" />&selectAction=off"><c:out value="${msgs.table_action_unselect}"/></a>
					 </c:if>
			  </td>
			  <td><c:out value="${layout.owner.displayName}" /></td>
			  <td>
				  <c:choose>
				  <c:when test="${layout.globalState eq '0'}">
					  <c:out value="${msgs.layout_published_status0}"/>
				  </c:when>
				  <c:when test="${layout.globalState eq '1'}">
					  <c:out value="${msgs.layout_published_status1}"/>
				  </c:when>
				  <c:when test="${layout.globalState eq '2'}">
					  <c:out value="${msgs.layout_published_status2}"/>
				  </c:when>
				  </c:choose>
			  </td>
			</tr>
			<c:if test="${!(empty layout.description)}">
				<tr class="exclude">
				<td class="attach"></td>
				<td colspan="4">
					<div class="instruction indnt2 textPanelFooter" id="<c:out value="${layout.id.value}" />" style="padding:0;margin:0;display:none">
						<c:out value="${layout.description}" />
					</div>
				  </td>    
				  </tr>
		 </c:if> 
		  </c:forEach>
			</tbody>
		  </table>
		 </c:otherwise>
		 </c:choose>
  
     <div class="act">
      <c:if test="${selectableLayout == 'true'}">
         <input type="button" name="goBack" class="active" value="<c:out value="${msgs.button_goback}"/>"
            onclick="window.document.location='<osp:url value="selectLayout.osp"/>'"/>
      </c:if>
   </div>
