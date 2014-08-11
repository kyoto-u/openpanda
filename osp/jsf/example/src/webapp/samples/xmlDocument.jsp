<f:view>
<sakai:view title="Demo Page">
<h:form>
<h:commandLink action="main"><h:outputText value="Back to examples index" /></h:commandLink>

<table>
<tr>
<td align="right">


</td>
</tr>
</table>
<h:outputText value="#{testBean.label}"/>

<ospx:xmlDocument factory="#{testBean.factory}" xmlFile="#{testBean.sampleXmlFile}" />

</h:form>
</sakai:view>
</f:view>
