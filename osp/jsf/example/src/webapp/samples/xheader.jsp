<f:view>
<sakai:view title="scrollarea tag - OSP 2.0 JSF example">
<h:form>
<h:commandLink action="main"><h:outputText value="Back to examples index" /></h:commandLink>

<h1>
xheader
</h1>

       <span class="tag">&lt;ospx:xheader</span> 
         <span="param">rendered</span> = <span class="value">"true/false"</span>&gt;<br>
            &nbsp; &nbsp; &nbsp; &nbsp; 
                
       <span class="tag">&lt;ospx:xheadertitle</span> 
         <span="param">id</span> = <span class="value">"headerDivId"</span>
         <span="param">rendered</span> = <span class="value">"true/false"</span>&gt;<br>
         &nbsp; &nbsp; &nbsp; &nbsp; 
         The title of the bar
         <br>
         &nbsp; &nbsp; &nbsp; &nbsp; 
       <span class="tag">&lt;/ospx:xheader&gt</span>
            <br>
       <span class="tag">&lt;/ospx:xheader&gt</span>
       <br><br><br>
       
       
       <span class="tag">&lt;ospx:xheader</span> 
         <span="param">rendered</span> = <span class="value">"true/false"</span>&gt;<br>
            &nbsp; &nbsp; &nbsp; &nbsp; 
                
       <span class="tag">&lt;ospx:xheadertitle</span> 
         <span="param">id</span> = <span class="value">"headerDivId"</span>
         <span="param">value</span> = <span class="value">"header title"</span>&gt;<br>
         &nbsp; &nbsp; &nbsp; &nbsp;
         The title of the bar.  This text will be clickable to collapse the header.
         <br>
         &nbsp; &nbsp; &nbsp; &nbsp;
         <span="param">rendered</span> = <span class="value">"true/false"</span>&gt;<br>
         &nbsp; &nbsp; &nbsp; &nbsp;
         The title of the bar, no underarea
         <br>
         &nbsp; &nbsp; &nbsp; &nbsp;
       <span class="tag">&lt;/ospx:xheadertitle&gt</span>
            <br>
            
            &nbsp; &nbsp; &nbsp; &nbsp; 
                
       <span class="tag">&lt;ospx:xheaderdrawer</span> 
         <span="param">id</span> = <span class="value">"headerDivId"</span>
         <span="param">rendered</span> = <span class="value">"true/false"</span>&gt;<br>
         &nbsp; &nbsp; &nbsp; &nbsp; 
         under area
         <br>
         &nbsp; &nbsp; &nbsp; &nbsp; 
       <span class="tag">&lt;/ospx:xheaderdrawer&gt</span>
            <br>
       <span class="tag">&lt;/ospx:xheader&gt</span>
       
   <br><br>
   <br><br>
   
   <ospx:xheader>
       <ospx:xheadertitle id="title1">
            <h:outputText value="Title only -" />
        </ospx:xheadertitle>
    </ospx:xheader>
    <br><br>
    <br><br>
   <ospx:xheader>
       <ospx:xheadertitle id="testtitle" cssclass="xheader" value="Another Title -" />
       <ospx:xheaderdrawer>
            <h:outputText value="This inner text is what goes into the under area." />
            <ospx:xheader>
                <ospx:xheadertitle cssclass="xheader" value="Embeded title -" />
                <ospx:xheaderdrawer>
                     <h:outputText value="This inner text is what goes into the embedded under area." />
                 </ospx:xheaderdrawer>
             </ospx:xheader>
        </ospx:xheaderdrawer>
    </ospx:xheader>
    <br><br>
    <br><br>
   <ospx:xheader>
       <ospx:xheadertitle id="title55544" cssclass="xheader" value="Title -" >
            <h:commandButton value="Preview" type="button" style="act" />
        </ospx:xheadertitle>
       <ospx:xheaderdrawer id="underarea123" initiallyexpanded="true" cssclass="theUnderArea">
         <h:outputText value="This inner text is what goes into the under area." />
         <h:panelGrid columns="3" styleClass="indnt2">
            <h:outputText escape="false" value="<strong>Description</strong>
               This Portfolio contains various assignments from
               English 311 - Transcendetal American Literature.
               <br/><strong>Keywords</strong>
               Writing, Assignments, English 311, Transcendentalism,
               American Literature"/>
            <h:outputText escape="false" value="<strong>Layout:</strong> Portfolio Default
               <br/><strong>Style:</strong> Portfolio Default
               <br/><strong>Pages:</strong> 4
               <br/><strong>Size:</strong> 4.5 mb
               <br/><strong>Modified Date:</strong> August 10, 2005"/>
            <h:commandButton value="Edit Info" type="button" style="act" />
         </h:panelGrid>
      </ospx:xheaderdrawer>
   </ospx:xheader>


</h:form>
</sakai:view>
</f:view>