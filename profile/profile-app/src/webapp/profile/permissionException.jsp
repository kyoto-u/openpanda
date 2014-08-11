<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %> 
<f:view>
 <sakai:view title="#{msgs.user_not_logged_in}"> 
 	<!--permissionException.jsp -->
  		<h:outputText id="error" styleClass="alertMessage" value="#{msgs.permission_exception}" />	 	
 </sakai:view>  
</f:view>
