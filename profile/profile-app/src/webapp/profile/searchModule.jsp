	<!--searchModule.jsp -->
		<div class="searchNav clear">
			 <h:inputText id="inputSearchBox" value="#{SearchTool.searchKeyword}" onclick="this.value=''"/>
			 <h:commandButton id="searchButton" title ="#{msgs.search}" action="#{SearchTool.processActionSearch}" onkeypress="document.forms[0].submit;" value="#{msgs.bar_search}" />
		</div>	
	<div class="clear"> </div>
		<h:outputText  id="showResult" rendered="#{SearchTool.showSearchResults}" styleClass="crud">
			<%@ include file="searchResults.jsp"%>	
		 </h:outputText>
		  <h:panelGroup id="nomatchfound"  rendered="#{SearchTool.showNoMatchFound}" styleClass="alertMessage"> 
		   <%@ include file="noMatchFound.jsp"%>	
		 </h:panelGroup>


