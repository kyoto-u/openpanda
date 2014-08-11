<%@ taglib uri="http://java.sun.com/jsf/html" prefix="h" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
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

<sakai:view_title rendered="#{!audience.inviteFeedbackAudience}" value="#{common_msgs.title_notifyViewers}"/>
<sakai:view_title rendered="#{audience.inviteFeedbackAudience}" value="#{common_msgs.title_notifyUsers}"/>

<f:subview rendered="#{!audience.inviteFeedbackAudience}" id="sV1">
	<p class="instruction"><h:outputText value="#{common_msgs.instructions_pickUsersFromList}"/></p>
</f:subview>
<f:subview rendered="#{audience.inviteFeedbackAudience}" id="sV2">
	<sakai:instruction_message value="#{common_msgs.instructions_notifyViewersChangesToX}"/>
</f:subview>


<h3><div class="highlight"><h:outputText value="#{audience.pageContext}"/></div></h3>
<div class="highlight"><h:outputText value="#{audience.pageContext2}"/></div>
<sakai:view>   


<f:verbatim>
<script type="text/javascript" language="JavaScript" src="/library/js/jquery-ui-latest/js/jquery.min.js"></script>
</f:verbatim>

<sakai:messages rendered="#{!empty facesContext.maximumSeverity}"/>

<h:form id="mainForm">
    
    
    
    <f:subview id="sV3">
		<p class="indnt2">
			<sakai:view_title value="#{common_msgs.to_label}" />
			<h:selectManyListbox id="selectedUsers" size="10" value="#{audience.selectedArray}"
														   style="width:350px;">
			   <f:selectItems value="#{audience.selectedList}"/>
			</h:selectManyListbox>
		</p>
		<f:verbatim>
			<script type="text/javascript" language="JavaScript">
				jQuery(document).ready(function() {
					jQuery("#mainForm\\:sV3\\:selectedUsers > option[selected!=true]").attr("selected", "selected");
				});
			</script>
		</f:verbatim>
    </f:subview>
    
    
    
    <p class="longtext">
    	<sakai:view_title value="#{common_msgs.label_yourMessage}" />
        <label class="block"><c:out value="${msgs.label_yourMessage}"/></label>
        <h:inputTextarea id="message" rows="5" cols="80" value="#{audience.message}"/>
    </p>
    
    <sakai:button_bar>
        <sakai:button_bar_item id="save_button" action="#{audience.processActionNotify}"
                               value="#{common_msgs.notify_audience}" styleClass="active" accesskey="s" />
        <sakai:button_bar_item id="_target1" action="#{audience.processActionCancel}"
                               value="#{common_msgs.cancel_audience}" accesskey="x" />
    </sakai:button_bar>
</h:form>

</sakai:view>
</f:view>