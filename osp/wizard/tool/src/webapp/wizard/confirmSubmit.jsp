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

    <h3><h:outputText value="#{msgs.submit_wizard_for_evaluation}"/></h3>
   
      <div class="alertMessage"><h:outputText value="#{msgs.complete_wizard_instructions}"/></div>

	<table class="listHier lines nolines" cellpadding="0" cellspacing="0">
	   <tr>
	      <th><h:outputText value="#{msgs.wizard_meaning_title}"/></th>
          <th><h:outputText value="#{msgs.wizard_description}"/></th>
       </tr>
       <tr>
          <td><h:outputText value="#{wizard.current.base.name}"/></td>
          <td><h:outputText value="#{wizard.current.base.description}" escape="false"/></td>
       </tr>
    </table>
<sakai:button_bar>
   <sakai:button_bar_item id="submit" value="#{msgs.submit_wizard}"
      action="#{wizard.current.runningWizard.processSubmitWizard}"  styleClass="active" accesskey="s" />
   <sakai:button_bar_item id="cancel" value="#{msgs.cancel_submit_wizard}"
      action="cancelled" immediate="true" accesskey="x" />
</sakai:button_bar>



</h:form>
</sakai:view>
</f:view> 