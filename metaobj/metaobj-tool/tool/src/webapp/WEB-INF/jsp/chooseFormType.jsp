<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<fmt:setLocale value="${locale}"/>
<fmt:setBundle basename = "messages"/>

<h3><fmt:message key='form_type_title'/></h3>
<p class="instruction"><fmt:message key='form_type_instructions'/></p>
<form method="POST">

<spring:bind path="bean.formId">
   <c:if test="${status.error}">
      <div class="validation"><c:out value="${status.errorMessage}"/></div>
   </c:if>
</spring:bind>
   
   <div class="sidebyside">
      <select name="formId" size="15">
         <optgroup label="<fmt:message key='form_type_global'/>" class="main">
            <c:forEach var="form" items="${globalForms}" varStatus="loopCount">
               <option value="<c:out value='${form.id}'/>">
                  <c:out value="${form.description}"/></option>
            </c:forEach>
         </optgroup>
         
         <c:forEach var="site" items="${categorizedFormList}" varStatus="loopCount">
            <optgroup label="<c:out value='${site.site.title}'/>">
               <c:forEach var="form" items="${site.homes}" varStatus="loopCount">
                  <option value="<c:out value='${form.id}'/>">
                     <c:out value="${form.description}"/>
                  </option>
               </c:forEach>
            </optgroup>
         </c:forEach>
      </select>
   </div>
   <p class="act">
      <input type="submit" value="<fmt:message key='form_type_submit'/>" class="active" accesskey="s" />
      <input type="submit" value="<fmt:message key='form_type_cancel'/>" onclick="this.form.canceling.value='true'" accesskey="x" />
      <input type="hidden" value="" name="canceling" />
   </p>
</form>
