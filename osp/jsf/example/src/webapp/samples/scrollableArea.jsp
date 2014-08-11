<f:view>
<sakai:view title="scrollarea tag - OSP 2.0 JSF example">
<h:form>
<h:commandLink action="main"><h:outputText value="Back to examples index" /></h:commandLink>

<h1>
scrollarea

</h1>



       <span class="tag">&lt;ospx:scrollablearea</span> 
         <span="param">id</span> = <span class="value">"divID"</span>
         <span="param">width</span> = <span class="value">"200px"</span>
         <span="param">height</span> = <span class="value">"95%"</span>
         <span="param">cssclass</span> = <span class="value">"cssClass"</span>
         <span="param">rendered</span> = <span class="value">"true/false"</span>&gt;<br>
         &nbsp; &nbsp; &nbsp; &nbsp; This inner text is what goes into the scrollable area.<br>
       <span class="tag">&lt;/ospx:scrollablearea&gt</span>
   <br><br>
<ospx:scrollablearea id="divID" height="100px" width="250px">
    step 1<br>
    step 2<br>
    step 2<br>
    step 2<br>
    step 2<br>
    step 2<br>
    step 2<br>
    step 2<br>
    step 2<br>
    step 2<br>
    step 2<br>
    step 2<br>
</ospx:scrollablearea>


</h:form>
</sakai:view>
</f:view>