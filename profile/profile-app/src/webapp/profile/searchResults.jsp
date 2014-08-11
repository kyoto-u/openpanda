	<!--searchResults.jsp -->
  <h:panelGrid id="t1" summary="#{msgs.search_table}" columns="9" styleClass="profileSearchResultsNav">
	<h:commandLink id="first" title="#{msgs.alt_search_result1}" action="#{SearchTool.processActionDisplayFirst}"  style="font-weight:bold" value="&lt;&lt;" rendered="#{SearchTool.showPrevious}"/> 		 
	<h:outputText id="blankspace" rendered="#{SearchTool.showPrevious}" value="          "/>
	<h:commandLink id="previous" title ="#{msgs.alt_search_result2}" action="#{SearchTool.processActionDisplayPrevious}"  style="font-weight:bold" value="&lt;" rendered="#{SearchTool.showPrevious}"/> 		 
	<h:outputText id= "spaces" rendered="#{SearchTool.showPrevious}" value="          "/>
	<h:selectOneMenu  id="selectNoOfRec" title ="#{msgs.alt_search_result3}"  rendered="#{SearchTool.showSearchResults}" onchange="this.form.submit();"  valueChangeListener="#{SearchTool.processValueChangeForDisplayNSearchResult}" value="#{SearchTool.displayNoOfRec}">  
		<f:selectItem id="ten" itemValue="10" itemLabel="#{msgs.alt_select10}" />
		<f:selectItem id="twenty"  itemValue="20" itemLabel="#{msgs.alt_select20}" />
		<f:selectItem id="thirty"  itemValue="30" itemLabel="#{msgs.alt_select30}"/>
		<f:selectItem id="forty" itemValue="40" itemLabel="#{msgs.alt_select40}" />
		<f:selectItem id="fifty"  itemValue="50" itemLabel="#{msgs.alt_select50}" />
	</h:selectOneMenu>  
	 <h:outputText id="blankspace2" rendered="#{SearchTool.showPrevious}" value="          "/>
	 <h:commandLink id="next" title ="#{msgs.alt_search_result4}" action="#{SearchTool.processActionDisplayNext}" style="font-weight:bold"  value="&gt;" rendered="#{SearchTool.showNext}"/>  
	 <h:outputText id="blankspace3" rendered="#{SearchTool.showPrevious}" value="          "/>
	 <h:commandLink id="last" title ="#{msgs.alt_search_result5}"   action="#{SearchTool.processActionDisplayLast}" style="font-weight:bold"  value="&gt;&gt;" rendered="#{SearchTool.showNext}"/>  
</h:panelGrid>  

<h:dataTable id="searchTable" summary="#{msgs.alt_table2summary}" value="#{SearchTool.currentSearchResults}" var="searchResult" styleClass="profileSearchResultsList listHier lines nolines" columnClasses="profileSearchResultsListSingle specialLink" >
	<h:column>	
		<h:commandLink id="profile" title ="#{msgs.display_profile}" action="#{searchResult.processActionDisplayProfile}">
		<h:outputText id="blankspace4" value="     " />
		<h:outputText id="profileLastName" value="#{searchResult.profile.lastName}" /> 
		<h:outputText id="comma" value=", " /> 
		<h:outputText id="profileFirstName" value="#{searchResult.profile.firstName}" />
		<h:outputText id="blankspace5" value="                          " />
		</h:commandLink >
	</h:column>
</h:dataTable> 
