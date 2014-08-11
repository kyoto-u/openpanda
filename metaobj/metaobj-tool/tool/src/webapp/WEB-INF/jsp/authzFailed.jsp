<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>
<%@ taglib prefix='osp' uri='http://www.theospi.org' %>


<H2>
  <fmt:message key="text_authenticationFailed">
    <fmt:param><c:out value="${exception.function}"/></fmt:param>
    <fmt:param><c:out value="${exception.qualifier}"/></fmt:param>
  </fmt:message>
</H2>
<P>


<%
Exception ex = (Exception) request.getAttribute("exception");
ex.printStackTrace(new java.io.PrintWriter(out));
%>

<P>
<BR>
