<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:set var="scaffolding" value="${matrixContents.scaffolding}" />
<%@ include file="../matrixStyle.jspf" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<SCRIPT LANGUAGE="JavaScript">

function hrefViewCell(cellId) {
  window.location="<osp:url value="editScaffoldingCell.osp?scaffoldingCell_id="/>"+cellId;
}

</SCRIPT>

   <script type="text/javascript" src="/library/js/scriptaculous/prototype.js"></script>
   <script type="text/javascript" src="/library/js/scriptaculous/scriptaculous.js"></script>
   <style type="text/css">

      ul#headers {
         clear: both;
         display:block;
         padding: 0;
         margin: 0;
      }

      ul#headers li{
         border: 1px solid red;
         cursor: pointer;
         display: block;
         list-style-type: none;
         margin: 1px;
         float: left;
         background-color: #yellow;
      }

      ul#rows {
         clear: left;
         float: left;
         padding: 0;
         margin: 0;
      }
      ul#rows li {
         border: 1px solid blue;
         list-style-type: none;
         cursor: pointer;
         margin: 1px;
      }
   </style>
   <script type="text/javascript">
   
   	function updateRowColSize(rows, cols) {
   		//alert(rows + ":" + cols);
   		
   		$('rowHeadings').rowspan = rows+2;
   		$('colHeadings').colspan = cols+1;
   	}
   
      function gethandles(){
         //match widths of <li> and <td>
         $('table').down('tr').next().getElementsBySelector('td').each(function(el,i){
            if($('header_' + i).getWidth() < el.getWidth()){
               $('header_' + i).style.width = el.getWidth() + 'px';
            }else{
               el.style.width = $('header_' + i).getWidth() + 'px';
            }
         });
         //match heights of <li> and <tr>
         $('rows').style.marginTop = ($('header_1').getHeight() + 10) + 'px';
         $('table').getElementsBySelector('tr.datarow').each(function(el,i){
            el.id='trow_' + i;
            if($('row_' + i).getHeight() > el.getHeight()){
               el.style.height = $('row_' + i).getHeight()+ 'px';
            }else{
               $('row_' + i).style.height = (el.getHeight()-2) + 'px';
            }
         });
         //add cords
         $('table').getElementsBySelector('tr.datarow').each(function(el,i){
            el.getElementsBySelector('td').each(function(ej, j){
               ej.id='cell_' + i + '_' + j;
            });
         });
         Sortable.create('headers', {constraint: 'horizontal', overlap: 'horizontal', onUpdate: redrawTableX});
         Sortable.create('rows', {constraint: 'vertical', overlap: 'vertical', onUpdate: redrawTableY});
      }
      redrawTableX = function(){
         //get serialized list  
         $('table').getElementsBySelector('tr.datarow').each(function(el,i){
            $A(Sortable.sequence('headers').toArray(',')).each(function(a,j){             
               el.appendChild($('cell_' + el.id.substring(5) + '_' + a));
            });
         });      }
      redrawTableY = function(){
         //get serialized list
         $A(Sortable.sequence('rows').toArray(',')).each(function(i){
            $('table').down('tbody').appendChild($('trow_' + i)); //$('table').getElementsBySelector('tr')[i]);
         });
      }
    </script>

<osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" qualifier="${matrixConetnts.scaffolding.worksiteId}"/>


		<div class="navIntraTool">
			<c:if test="${can.create}">
				<a href="<osp:url value="addScaffolding.osp?scaffolding_id=${matrixContents.scaffolding.id}"/>"><c:out value="${msgs.action_edit}"/></a>
			</c:if>
         <a href="<osp:url value="listScaffolding.osp"/>"><c:out value="${msgs.action_list}"/></a>
		</div>

	<h3><c:out value="${msgs.title_matrixScaffolding}"/></h3>
   
   <c:if test="${not empty matrixContents.scaffolding.description}">
      <p class="instruction">
         <osp-h:glossary link="true" hover="true">
            <c:out value="${matrixContents.scaffolding.description}" escapeXml="false" />
         </osp-h:glossary>
      </p>
   </c:if>
  
	<c:if test="${empty matrixContents.columnLabels}">
		<p class="instruction"><c:out value="${msgs.instructions_clickEdittosetup}"/></p>
	</c:if>
	<c:if test="${not empty matrixContents.columnLabels}">
		<p class="instruction"><c:out value="${msgs.instructions_clickOnaCelltoEdit}"/></p>
 
 <c:set var="rowNums" value="0" />
 <c:set var="colNums" value="0" />

		<c:set var="columnHeading" value="${matrixContents.columnLabels}" />
		<table cellspacing="0" width="100%" id="table">
		<caption><osp-h:glossary link="true" hover="true">
   					<c:out value="${matrixContents.scaffolding.title}"/>
               </osp-h:glossary></caption>
			<tr>

				<th id="rowHeadings" rowspan="0" valign="bottom" width="0">

         <ul id="rows">
         	<c:forEach var="rowLabel" items="${matrixContents.rowLabels}" varStatus="rowLoopStatus" >
            <li id="row_<c:out value="${rowLoopStatus.index}" />" class="matrix-row-heading" style="background-color:<c:out value="${rowLabel.color}"/>" bgcolor="<c:out value="${rowLabel.color}"/>" >
                  <osp-h:glossary link="true" hover="true">
                     <font color="<c:out value="${rowLabel.textColor}"/>">
                        <c:out value="${rowLabel.description}"/>
                     </font>
                  </osp-h:glossary>
               </li>
               <c:set var="rowNums" value="${rowLoopStatus.index}" />
            </c:forEach>
         </ul>
      </th>
				<th id="colHeadings" colspan="0">
				<ul id="headers">
				<c:forEach var="head" items="${columnHeading}" varStatus="cols">
					<li id="header_<c:out value="${cols.index}" />" class="matrix-column-heading" width="10%" 
                  bgcolor="<c:out value="${head.color}"/>">
                  <osp-h:glossary link="true" hover="true">
                     <font color="<c:out value="${head.textColor}"/>">
   						   <c:out value="${head.description}"/>
                     </font>
                  </osp-h:glossary>
					</li>
					<c:set var="colNums" value="${cols.index}" />
				</c:forEach>
				</ul>
				</th>
			</tr>   
			<c:forEach var="rowLabel" items="${matrixContents.rowLabels}" varStatus="loopStatus" >
				<tr class="datarow">
	    
					<c:forEach var="cell" items="${matrixContents.matrixContents[loopStatus.index]}">
						<td class="matrix-cell-border matrix-<c:out value="${cell.initialStatus}"/>" onClick="hrefViewCell('<c:out value="${cell.id}"/>') " style="cursor:pointer">
							&nbsp;
						</td>
					</c:forEach>
				</tr>
			</c:forEach>
		</table>
		

     <%@ include file="../matrixLegend.jspf" %>
  
	</c:if>

<SCRIPT LANGUAGE="JavaScript">
	updateRowColSize(<c:out value="${rowNums}"/>, <c:out value="${colNums}"/>);
	gethandles();
</SCRIPT>
