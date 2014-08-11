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
<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="session">
   <jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.portal.messages"/>
</jsp:useBean>

<sakai:view>
<h:form styleClass="#{siteType.siteTypeClass}">

   <sakai:view_title value="#{msgs.siteType_page_title_portfolioAdmin}"
                     rendered="#{siteType.siteType == 'org.theospi.portfolio.portal.portfolioAdmin'}" />
   <sakai:view_title value="#{msgs.siteType_page_title_portfolio}"
                     rendered="#{siteType.siteType == 'org.theospi.portfolio.portal.portfolio'}" />
   <sakai:view_title value="#{msgs.siteType_page_title_project}"
                     rendered="#{siteType.siteType == 'org.theospi.portfolio.portal.project'}" />
   <sakai:view_title value="#{msgs.siteType_page_title_course}"
                     rendered="#{siteType.siteType == 'org.theospi.portfolio.portal.course'}" />
   <sakai:view_title value="#{msgs.siteType_page_title}" />

<sakai:pager id="pager"
    totalItems="#{siteType.sites.totalItems}"
    firstItem="#{siteType.sites.firstItem}"
    pageSize="#{siteType.sites.pageSize}"
    textItem="#{msgs.site_types_item}" />

   <h:dataTable value="#{siteType.sites.subList}" var="site" styleClass="listHier">
      <h:column>
         <h:outputLink
            value="/osp-portal/site/#{site.id}" target="_parent"
            title="#{site.title}">
            <h:graphicImage value="/library/skin/default/images/portfolioAdminLink.gif"
               rendered="#{siteType.siteType == 'org.theospi.portfolio.portal.portfolioAdmin'}" />
            <h:graphicImage value="/library/skin/default/images/portfolioLink.gif"
               rendered="#{siteType.siteType == 'org.theospi.portfolio.portal.portfolio'}" />
            <h:graphicImage value="/library/skin/default/images/projectLink.gif"
               rendered="#{siteType.siteType == 'org.theospi.portfolio.portal.project'}" />
            <h:graphicImage value="/library/skin/default/images/courseLink.gif"
               rendered="#{siteType.siteType == 'org.theospi.portfolio.portal.course'}" />
            <h:outputText value="#{site.title}"/>
         </h:outputLink>
         <h:outputText value=" -- " rendered="#{site.shortDescription != null}" />
         <h:outputText value="#{site.shortDescription}"/>
      </h:column>
   </h:dataTable>

   </h:form>
   </sakai:view>
</f:view>
