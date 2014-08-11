<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.common.bundle.Messages"/></jsp:useBean>



<div class ="portletBody">

<h3><c:out value="${msgs.perm_page_title}"/></h3>
<div class="instruction">
   <c:out value="${message}" escapeXml="false" />
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

   <table class="listHier lines" cellspacing="0" summary ="<c:out value="${msgs.perm_list_summary}"/>" border="0">
      <tr>
         <th id="role">
            <c:out value="${msgs.perm_hdr_role}"/>
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
                  <label><input type="checkbox" name="<c:out value="${status.expression}"/>"
                     value="<c:out value="${checkValue}" />"
                     <c:out value="${checked}" />
                     /><span class="skip"><c:out value="${msgs.perm_list_check_label}"/></span></label>
            </c:if>
            </td>
            </c:forEach>
         </tr>
      </c:forEach>
   </table>
</spring:bind>


   <div class="act">
   	  <input type="hidden" id="toolPermissionsSaved" name="toolPermissionsSaved" value="false"/>
      <input type="submit" value="<c:out value="${msgs.button_save}"/>" accesskey="s" class="active" onclick="javascript:document.getElementById('toolPermissionsSaved').value=true"/>
      <input name="_cancel" type="submit" value="<c:out value="${msgs.button_cancel}"/>" accesskey="x"/>
   </div>

</form>
</div>
