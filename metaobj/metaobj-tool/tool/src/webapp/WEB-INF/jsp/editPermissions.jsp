<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class ="chefPortletContent">

<div class="chefPageviewTitle">
   <c:out value="${message}"/>
</div>

<form method="post">
<osp:form/>

<%--
<spring:bind path="permissionsEdit.siteId">
<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.displayValue}"/>"/>
</spring:bind>
    --%>
<spring:bind path="permissionsEdit.permissions">

<input type="hidden" name="<c:out value="${status.expression}"/>" value="" />

   <table class="chefFlatListViewTable" cellspacing="0" summary ="List of roles and permissions that can be applied to this folder. Layout: each row lists the permissions of a role. Layout: column 1 lists the roles, the other columns list the permissions, checkboxes permit enabling a permission for a role." border="0">
      <tr>
         <th id="role">
            Role
         </th>
         <c:forEach var="function" items="${toolFunctions}">
         <th id="<spring:message code="${function}" text="${function}" />"><spring:message code="${function}" text="${function}" /></th>
         </c:forEach>
      </tr>

      <c:forEach var="role" items="${roles}">
         <tr>
            <td headers="role"><c:out value="${role.id}" /></td>
            
            <c:forEach var="function" items="${toolFunctions}">
            <c:set var="checkValue"><c:out value="${role.id}" />~<c:out value="${function}" /></c:set>
            <td headers="revise" style="text-align: left;">
            <c:set var="checked" value=""/>

            <c:forEach var="current" items="${permissionsEdit.permissions}">
               <c:set var="currentString" value="${current}" />
               <c:if test="${currentString eq checkValue}">
                  <c:if test="${current.readOnly}">
                     <c:set var="checked" value="checkedPerm"/>
                  </c:if>
                  <c:if test="${!current.readOnly}">
                     <c:set var="checked" value="checked"/>
                  </c:if>
               </c:if>
            </c:forEach>
            
            <c:if test="${checked == 'checkedPerm'}">
               <img alt="This permission is read only"  src="<osp:url value="/img/checkon.gif"/>" border="0"/>
            </c:if>
            <c:if test="${checked != 'checkedPerm'}">                      
                  <input type="checkbox" name="<c:out value="${status.expression}"/>"
                     value="<c:out value="${checkValue}" />"
                     <c:out value="${checked}" />
                     />
            </c:if>
            </td>
            </c:forEach>   
         </tr>
      </c:forEach>
   </table>
</spring:bind>

  <br/>

   <p class="act">
      <input type="submit" value="save" />
      <input name="_cancel" type="submit" value="cancel"/>
   </p>

</form>
</div>
