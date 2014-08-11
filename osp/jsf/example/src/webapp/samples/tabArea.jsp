<f:view>
<sakai:view title="TabArea tag - OSP 2.0 JSF example">
<h:form>
<h:commandLink action="main"><h:outputText value="Back to examples index" /></h:commandLink>

<h1>
Placeholder.
</h1>


<ospx:tabArea direction="horizontal">
   <ospx:tab title="Style">
      <h:outputText value="Here's some content for the first tab."/>
   </ospx:tab>
   <ospx:tab title="Layout" selected="true">
      <h:outputText value="Here's some content for the second tab."/>
   </ospx:tab>
   <ospx:tab title="Arrange">
      <h:outputText value="Here's some content for the third tab."/>
   </ospx:tab>
</ospx:tabArea>

<ospx:tabArea direction="vertical">
   <ospx:tab title="Style">
      <h:outputText value="Here's some content for the first tab."/>
   </ospx:tab>
   <ospx:tab title="Layout" selected="true">
      <h:outputText value="Here's some content for the second tab."/>
   </ospx:tab>
   <ospx:tab title="Arrange">
      <h:outputText value="Here's some content for the third tab."/>
   </ospx:tab>   
</ospx:tabArea>

</h:form>
</sakai:view>
</f:view>