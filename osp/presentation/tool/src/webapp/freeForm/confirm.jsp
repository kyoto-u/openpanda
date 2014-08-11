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

    <h3><h:outputText value="#{msgs.remove_page_title}"/></h3>
   
      <span class="alertMessage"><h:outputText value="#{msgs.remove_page_instructions}"/></span>
      <br />

	<table class="listHier">
	   <tr>
	      <th><h:outputText value="#{msgs.remove_page_meaning_title}"/></th>
          <th><h:outputText value="#{msgs.remove_page_description}"/></th>
       </tr>
       <tr>
          <td><h:outputText value="#{freeForm.currentPage.base.title}"/></td>
          <td><h:outputText value="#{freeForm.currentPage.base.description}"/></td>
       </tr>
    </table>

<sakai:button_bar>
   <sakai:button_bar_item id="remove" value="#{msgs.remove_page_button}"
      action="#{freeForm.currentPage.processActionDelete}" />
   <sakai:button_bar_item id="cancel" value="#{msgs.cancel_remove_page}"
      action="cancel" immediate="true" />
</sakai:button_bar>


</h:form>
</sakai:view>
</f:view> 
