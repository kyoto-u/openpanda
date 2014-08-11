<%@ include file="/WEB-INF/jsp/include.jsp" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.glossary.bundle.Messages"/></jsp:useBean>
<html>
<bead>
</head>
<body>
<h3><c:out value="${msgs.title_glossary}"/></h3>

<c:forEach var="entry" items="${glossary}">
<a name="<c:out value="${entry.term}"/>"/>
<p><b><c:out value="${entry.term}"/></b> - <c:out value="${entry.description}"/></p>
</c:forEach>

</body>
</html>