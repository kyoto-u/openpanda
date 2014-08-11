<f:view>
<sakai:view title="wizardSteps tag - OSP 2.0 JSF example">
<h:form>
<h:commandLink action="main"><h:outputText value="Back to examples index" /></h:commandLink>

<h1>
Placeholder.
</h1>


<ospx:wizardSteps currentStep="0">
   <ospx:wizardStep label="Begin Design" />
   <ospx:wizardStep label="#{testBean.label}" />
   <ospx:wizardStep label="Design" />
   <ospx:wizardStep label="Support" />
   <ospx:wizardStep label="Workflow" />
   <ospx:wizardStep label="Properties" />
</ospx:wizardSteps>
<ospx:wizardSteps currentStep="#{testBean.currentStep}">
   <ospx:wizardStep label="Begin Design" />
   <ospx:wizardStep label="#{testBean.label}" />
   <ospx:wizardStep label="Design" />
   <ospx:wizardStep label="Support" />
   <ospx:wizardStep label="Workflow" />
   <ospx:wizardStep label="Properties" />
</ospx:wizardSteps>
<ospx:wizardSteps currentStep="#{testBean.currentStep}">
   <ospx:wizardStep label="Begin Design" />
   <ospx:wizardStep label="#{testBean.label}" />
   <ospx:wizardStep label="Design" rendered="#{testBean.rendered}" />
   <ospx:wizardStep label="Support" rendered="#{testBean.rendered}" />
   <ospx:wizardStep label="Workflow" rendered="#{testBean.rendered}" />
   <ospx:wizardStep label="Properties" />
</ospx:wizardSteps>
<ospx:wizardSteps currentStep="4">
   <ospx:wizardStep label="Begin Design" />
   <ospx:wizardStep label="#{testBean.label}" />
   <ospx:wizardStep label="Design" />
   <ospx:wizardStep label="Support" />
   <ospx:wizardStep label="Workflow" />
   <ospx:wizardStep label="Properties" />
   <ospx:wizardStep label="Begin Design" disabled="#{testBean.disabled}" />
   <ospx:wizardStep label="#{testBean.label}" disabled="#{testBean.disabled}" />
   <ospx:wizardStep label="Design" disabled="#{testBean.disabled}" />
   <ospx:wizardStep label="Support" disabled="#{testBean.disabled}" />
   <ospx:wizardStep label="Workflow" disabled="#{testBean.disabled}" />
   <ospx:wizardStep label="Properties" disabled="#{testBean.disabled}" />
</ospx:wizardSteps>
</h:form>
</sakai:view>
</f:view>