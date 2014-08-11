<%@ include file="/WEB-INF/jsp/include.jsp" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.presentation.bundle.Messages"/></jsp:useBean>

<div class="validation"><c:out value="${msgs.alert_presentationExpired}"/></div>


