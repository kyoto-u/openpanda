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
<h:commandButton actionListener="#{testBean.processTestButton}" />

<ospx:test/>

<h:dataTable var="aSubBean" value="#{testBean.subBeans}">
   <h:column>
      <h:commandButton actionListener="#{aSubBean.processTestButton}"/>
   </h:column>
</h:dataTable>


</h:form>
</sakai:view>
</f:view>