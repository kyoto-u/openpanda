<f:view>
<sakai:view title="Tag Usage Demos">
<h:form>
<h:commandLink action="main"><h:outputText value="Back to examples index" /></h:commandLink>
<h:commandButton actionListener="#{testBean.processUpgrade}" />

<h2>Tag Usage demonstration.</h2>
<h:panelGrid columns="3" border="0">
<h:commandLink action="wizardSteps"><h:outputText value="*wizardSteps" /></h:commandLink>
<h:commandLink action="scrollArea"><h:outputText value="*scrollArea" /></h:commandLink>
<h:commandLink action="xheader"><h:outputText value="*xheader" /></h:commandLink>
<h:commandLink action="splitArea"><h:outputText value="*splitarea/splitsection" /></h:commandLink>
<h:commandLink action="tabArea"><h:outputText value="*tabArea" /></h:commandLink>
<h:commandLink action="xmlDocument"><h:outputText value="*xmlDocument" /></h:commandLink>
<h:commandLink action="testComponent"><h:outputText value="*testComponent" /></h:commandLink>

</h:panelGrid>
<f:verbatim><br /></f:verbatim>
<f:verbatim><br /></f:verbatim>
<f:verbatim><br /></f:verbatim>
<h2>Demo Pages</h2>

<h:panelGrid columns="3" border="0">
<h:commandLink action="page1"><h:outputText value="page1" /></h:commandLink>

</h:panelGrid>

</h:form>
</sakai:view>
</f:view>