<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.common.bundle.Messages"/></jsp:useBean>

<div class="navPanel">
	<div class="viewNav">
		<h3><c:out value="${msgs.assign_title_manage}"/></h3>
		<p class="instruction"><c:out value="${msgs.edit_addAssign_instructions}"/></p>
	</div>	
	<osp:url var="listUrl" value="listAssignment.osp"/>
	<osp:listScroll listUrl="${listUrl}" className="listNav" />
</div>


<form method="POST" action="<osp:url value="listAssignment.osp"/>">

<table class="listHier lines nolines" cellspacing="0" >
   <thead>
      <tr>
         <th scope="col" class="attach"></th>
         <th scope="col"><c:out value="${msgs.assign_title}"/></th>
         <th scope="col"><c:out value="${msgs.assign_status}"/></th>
         <th scope="col"><c:out value="${msgs.assign_open}"/></th>
         <th scope="col"><c:out value="${msgs.assign_due}"/></th>
      </tr>
   </thead>
   <tbody>
     <c:forEach var="bean" items="${assignments}">
        <tr>
         <td class="attach">
            <input type="checkbox" 
                   id="<c:out value='${bean.assignment.id}'/>" 
                   name="<c:out value='${bean.assignment.id}'/>" 
                   <c:if test="${bean.selected}">checked='checked'</c:if> 
                   />
         </td> <!-- checked -->
          <td><label for="<c:out value='${bean.assignment.id}'/>"><c:out value="${bean.assignment.title}" /></label></td>
          <td><c:out value="${bean.assignment.status}" /></td>
          <td><c:out value="${bean.assignment.openTimeString}" /></td>
          <td><c:out value="${bean.assignment.dueTimeString}" /></td> 
        </tr>
     </c:forEach>
    </tbody>
</table>

<div class="act">
      <input type="submit" name="_save" value="<c:out value="${msgs.button_save}"/>" accesskey="s" class="active"/>
      <input type="submit" name="_cancel" value="<c:out value="${msgs.button_cancel}"/>" accesskey="x" />

</div>

</form>
