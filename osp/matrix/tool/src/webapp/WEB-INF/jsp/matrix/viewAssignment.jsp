<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css" rel="stylesheet" media="all" />
<script type="text/javascript" src="/osp-jsf-resource/xheader/xheader.js"></script>
<c:set var="imageLib" value="/library/image/"/>

<form name="form" method="POST"
	<h3>
		<osp:message key="assignment.title"/>
		<span class="highlight">- <c:out value="${submission.status}"/></span>
	</h3>
    <table class="itemSummary">
		<tr>
			<th>
				<osp:message key="hdr.assignment.label"/>
			</th>
			<td>
				<c:out value="${submission.assignment.title}"/>
			</td>
		</tr>
		<tr>
			<th>
				<osp:message key="hdr.status"/>
			</th>
			<td>
				<c:out value="${submission.status}"/>
			</td>
		</tr>
		<c:if test="${submission.gradeReleased}">
		<tr>
			<th>
				<osp:message key="hdr.grade"/>
			</th>
			<td>
				<span class="highlight"><c:out value="${submission.grade}"/></span>
			</td>
		</tr>	
      </c:if>
	 </table> 
        
      <h4><osp:message key="assign.instruct"/></h4>
      <div class="textPanel indnt2"><c:out value="${submission.assignment.content.instructions}" escapeXml="false"/></div>
      
      <c:if test="${not empty assignAttachments}">
         <h4><osp:message key="assign.attach"/></h4>
         <ul class="attachList indnt2">
         <c:forEach var="attach" items="${assignAttachments}">
		 	<li>
				<img src="<c:out value='${imageLib}${attach.iconUrl}'/>" border="0"/>
				<a href="${attach.url}" target="_blank"><c:out value="${attach.displayName}"/></a>
				<span class="textPanelFooter">(<c:out value="${attach.size}"/>)</span>
			</li>
         </c:forEach>
         </ul>
      </c:if>
      
      <hr class="itemSeparator"/>
      
      <c:if test="${submission.submitted}">
         <c:if test="${not empty submission.feedbackFormattedText && submission.gradeReleased}">
            <h4><osp:message key="assign.submission"/></h4>
            <div class="textPanel indnt2"><c:out value="${submission.feedbackFormattedText}" escapeXml="false"/></div>
         </c:if>
         <c:if test="${not empty submission.submittedText && not submission.gradeReleased}">
            <h4><osp:message key="assign.submission.original"/></h4>
            <div class="textPanel indnt2"><c:out value="${submission.submittedText}" escapeXml="false"/></div>
         </c:if>
          
         <c:if test="${not empty submitAttachments}">
            <h4><osp:message key="assign.submit.attach"/></h4>
           <ul class="attachList indnt2">
            <c:forEach var="attach" items="${submitAttachments}">
               <li>
			   <img src="<c:out value='${imageLib}${attach.iconUrl}'/>" border="0"/>
               <a href="${attach.url}" target="_blank"><c:out value="${attach.displayName}"/></a>
               <span class="textPanelFooter">(<c:out value="${attach.size}"/>)</span>
			   </li>
            </c:forEach>
            </ul>
         </c:if>
         
         <c:if test="${not empty submission.feedbackComment && submission.gradeReleased}">
            <h4><osp:message key="assign.comments"/></h4>
            <div class="textPanel indnt2"><c:out value="${submission.feedbackComment}" escapeXml="false"/></div>
         </c:if>
         
         <c:if test="${not empty feedbackAttachments && submission.gradeReleased}">
            <h4><osp:message key="assign.feedback.attach"/></h4>
            <ul class="attachList indnt2">
            <c:forEach var="attach" items="${feedbackAttachments}">
               <li>
			   <img src="<c:out value='${imageLib}${attach.iconUrl}'/>" border="0"/>
               <a href="${attach.url}" target="_blank"><c:out value="${attach.displayName}"/></a>
               <span class="textPanelFooter">(<c:out value="${attach.size}"/>)</span>
			   </li>
            </c:forEach>
            </ul>
         </c:if>
      
      </c:if>
   </dl>

   <input type="submit" name="submit" value="<c:out value="${msgs.button_back}"/>" accesskey="b" class="active"/>
</form>
