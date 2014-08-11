<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://sakaiproject.org/jsf/sakai" prefix="sakai" %>
<%@ taglib uri="http://www.theospi.org/jsf/osp" prefix="ospx" %>

<%
      response.setContentType("text/html; charset=UTF-8");
      response.addDateHeader("Expires", System.currentTimeMillis() - (1000L * 60L * 60L * 24L * 365L));
      response.addDateHeader("Last-Modified", System.currentTimeMillis());
      response.addHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
      response.addHeader("Pragma", "no-cache");
%>

<f:view>
<sakai:view>
<h:form>

   <sakai:view_title value="#{msgs.delete_wizard}"/>
   <h:outputText value="#{wizard.current.deleteMessage}" styleClass="alertMessage"/>
   <%--
   <sakai:instruction_message value="#{wizard.current.deleteMessage}" />
     --%> 

<sakai:button_bar>
   <sakai:button_bar_item id="submit" value="#{msgs.submit_delete_wizard}"
      action="#{wizard.current.processActionDelete}" accesskey="s" styleClass="active" />
   <sakai:button_bar_item id="cancel" value="#{msgs.cancel_delete_wizard}"
      action="listWizards" immediate="true" accesskey="x" />
</sakai:button_bar>


</h:form>
</sakai:view>
</f:view>