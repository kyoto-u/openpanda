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

   <sakai:tool_bar>
      <h:selectOneMenu value="#{freeForm.currentPageId}">
         <f:selectItems value="#{freeForm.pageDropList}" />
      </h:selectOneMenu>
      <h:commandButton value="#{msgs.change_arrange_page}"
         action="#{freeForm.processChangeCurrentPage}" />
   </sakai:tool_bar>

   <sakai:view_title value=""/>
   <sakai:instruction_message value="" />
   <sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>

   <ospx:splitarea direction="horizontal" width="100%">
      <ospx:splitsection size="75%" valign="top">

         <f:subview id="arrange">
            <ospx:xmlDocument  factory="#{freeForm.factory}"
               xmlFile="#{freeForm.currentPage.xmlFile}"
               var="freeForm.currentPage.regionMap"/>
         </f:subview>

      </ospx:splitsection>
      <ospx:splitsection size="25%" valign="top" cssclass="selectedListBox">
         <f:subview id="selectedAudience">
            <%@ include file="items.jspf" %>
         </f:subview>
      </ospx:splitsection>
   </ospx:splitarea>


   <ospx:splitarea direction="vertical" width="100%">
      <ospx:splitsection>
         <h:commandButton action="main"
            value="#{msgs.saveAndReturnToPageList}"/>
      </ospx:splitsection>
      <ospx:splitsection>
         <f:subview id="navigation">
            <%@ include file="navigation.jspf" %>
         </f:subview>
      </ospx:splitsection>
   </ospx:splitarea>
   
</h:form>

</sakai:view>
</f:view>
