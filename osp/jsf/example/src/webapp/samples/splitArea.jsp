<f:view>
<sakai:view title="scrollarea tag - OSP 2.0 JSF example">
<h:form>
<h:commandLink action="main"><h:outputText value="Back to examples index" /></h:commandLink>

<h1>
splitarea

</h1>



       <span class="tag">&lt;ospx:splitarea</span> 
         <span="param">direction</span> = <span class="value">"vertical/horizontal/x/y"</span>
         <span="param">width</span> = <span class="value">"500/95%"</span>
         <span="param">height</span> = <span class="value">"90%"</span>
         <span="param">rendered</span> = <span class="value">"true/false"</span>&gt;<br><br>
         
         &nbsp;  &nbsp; &nbsp; &nbsp;
           <span class="tag">&lt;ospx:splitsection</span> 
             <span="param">cssclass</span> = <span class="value">"cssClass"</span>
             <span="param">size</span> = <span class="value">"200/20%"</span>
             <span="param">id</span> = <span class="value">"wrapperId"</span>
             <span="param">rendered</span> = <span class="value">"true/false"</span>
           <span class="tag">&gt</span>
           This is the content of the cell
           <span class="tag">&lt;/ospx:splitsection</span><span class="tag">&gt</span><br>
           
             &nbsp;  &nbsp; &nbsp; &nbsp;
           <span class="tag">&lt;ospx:splitsection</span> 
             <span="param">cssclass</span> = <span class="value">"cssClass"</span>
             <span="param">size</span> = <span class="value">"200/20%"</span>
             <span="param">id</span> = <span class="value">"wrapperId"</span>
             <span="param">rendered</span> = <span class="value">"true/false"</span>
           <span class="tag">&gt</span>
           This is the content of the cell
           <span class="tag">&lt;/ospx:splitsection</span><span class="tag">&gt</span>
           <br><br>
           <span class="tag">&lt;/ospx:splitarea</span><span class="tag">&gt</span>
           
   <br><br>
   <style>
        .headclass {
            background-color:#e8e8e8;
            padding: 5px;
        }
        .tailclass {
            background-color:#e0e0e0;
        }
        .innerheadclass {
            background-color:#e0e0e0;
            border: 1px solid #8888DD;
        }
        .innertailclass {
            background-color:#d8d8d8;
        }
   </style>
   
<ospx:splitarea direction="horizontal" height="200" width="500">
    <ospx:splitsection cssclass="headclass" size="200" id="firstID">
    
    
        <ospx:splitarea direction="vertical" height="100%" width="100%">
            <ospx:splitsection cssclass="innerheadclass" size="100" id="innerFirstID">
            
                <ospx:scrollablearea id="scroll" height="100" width="">
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
            
            </ospx:splitsection>
            <ospx:splitsection cssclass="innertailclass" size="50%" id="innersecond">
            step 6<br>
            step 7<br>
            step 8<br>
            </ospx:splitsection>
        </ospx:splitarea>
    
    </ospx:splitsection>
    <ospx:splitsection cssclass="tailclass" id="second">
        step 11<br>
        step 12<br>
        step 13<br>
        step 14<br>
        step 15<br>
    </ospx:splitsection>
</ospx:splitarea>


</h:form>
</sakai:view>
</f:view>