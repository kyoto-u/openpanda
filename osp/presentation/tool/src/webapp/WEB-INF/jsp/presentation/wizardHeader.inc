<!-- <div class="intraNavTool">
      <c:if test="${!suppress_previous}">
         <span name="previous" onclick="javascript:document.forms[0].elements['<c:out value="${targetPrevious}"/>'].click()" style="cursor:pointer" title="Previous">&lt;&lt;</span>
      </c:if>
      <i><font face="Verdana, Arial, Helvetica, sans-serif" size="2"> Step <c:out value="${currentPage}"/> of <c:out value="${totalPages}"/> </font></i>
      <c:if test="${!suppress_next}">
         <span name="next" onclick="javascript:document.forms[0].elements['<c:out value="${targetNext}"/>'].click()" style="cursor:pointer" title="Next">&gt;&gt;</span>
      </c:if>
</div>-->
<c:if test="${not empty step}">
<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css" rel="stylesheet" media="all" />
<h3>
<c:if test="${step eq 1}" >
<fmt:message key="paging_steps">
          <fmt:param><fmt:message key="step_1"/></fmt:param>
          <fmt:param><fmt:message key="step_3"/></fmt:param>:
</fmt:message>:
        <fmt:message key="step_begin"/>
</c:if>
<c:if test="${step eq 2}" >
<fmt:message key="paging_steps">
          <fmt:param><fmt:message key="step_2"/></fmt:param>
          <fmt:param><fmt:message key="step_3"/></fmt:param>
</fmt:message>:
         <fmt:message key="step_design"/>
</c:if>
<c:if test="${step eq 3}" >
<fmt:message key="paging_steps">
          <fmt:param><fmt:message key="step_3"/></fmt:param>
          <fmt:param><fmt:message key="step_3"/></fmt:param>

 </fmt:message>:
         <fmt:message key="step_publish"/>
</c:if>
</h3>
</c:if>
<c:if test="${empty step}">
<div class="xheader">
<h3>
        <fmt:message key="paging_steps">
          <fmt:param value="${currentPage}"/>
          <fmt:param value="${totalPages}"/>
        </fmt:message>

</h3>
</div>
</c:if>
