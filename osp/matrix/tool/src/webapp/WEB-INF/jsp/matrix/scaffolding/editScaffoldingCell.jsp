<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<link href="/osp-jsf-resource/css/osp_jsf.css" type="text/css" rel="stylesheet" media="all" />
<script type="text/javascript" src="/osp-jsf-resource/xheader/xheader.js"></script>
<%
  	String thisId = request.getParameter("panel");
  	if (thisId == null) 
  	{
    	thisId = "Main" + org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getId();
 		 }
%>
<script type="text/javascript">
	function resize(){
		mySetMainFrameHeight('<%= org.sakaiproject.util.Web.escapeJavascript(thisId)%>');
	}


function mySetMainFrameHeight(id)
{
	// run the script only if this window's name matches the id parameter
	// this tells us that the iframe in parent by the name of 'id' is the one who spawned us
	if (typeof window.name != "undefined" && id != window.name) return;

	var frame = parent.document.getElementById(id);
	if (frame)
	{

		var objToResize = (frame.style) ? frame.style : frame;
  
    // SAK-11014 revert           if ( false ) {

		var height; 		
		var offsetH = document.body.offsetHeight;
		var innerDocScrollH = null;

		if (typeof(frame.contentDocument) != 'undefined' || typeof(frame.contentWindow) != 'undefined')
		{
			// very special way to get the height from IE on Windows!
			// note that the above special way of testing for undefined variables is necessary for older browsers
			// (IE 5.5 Mac) to not choke on the undefined variables.
 			var innerDoc = (frame.contentDocument) ? frame.contentDocument : frame.contentWindow.document;
			innerDocScrollH = (innerDoc != null) ? innerDoc.body.scrollHeight : null;
		}
	
		if (document.all && innerDocScrollH != null)
		{
			// IE on Windows only
			height = innerDocScrollH;
		}
		else
		{
			// every other browser!
			height = offsetH;
		}
   // SAK-11014 revert		} 

   // SAK-11014 revert             var height = getFrameHeight(frame);

		// here we fudge to get a little bigger
		var newHeight = height + 40;

		// but not too big!
		if (newHeight > 32760) newHeight = 32760;

		// capture my current scroll position
		var scroll = findScroll();

		// resize parent frame (this resets the scroll as well)
		objToResize.height=newHeight + "px";

		// reset the scroll, unless it was y=0)
		if (scroll[1] > 0)
		{
			var position = findPosition(frame);
			parent.window.scrollTo(position[0]+scroll[0], position[1]+scroll[1]);
		}
	}
}

</script>

<form name="form" method="post">

	<c:if test="${taggable}">
		<div class="navIntraTool">
			<c:if test="${!(empty helperInfoList)}">
			<c:forEach var="helperInfo" items="${helperInfoList}">
				<a href="javascript:document.forms[0].submitAction.value='tagActivity';document.forms[0].providerId.value='<c:out value="${helperInfo.provider.id}"/>';document.forms[0].submit();"
					title="<c:out value="${helperInfo.description}"/>">
					<c:out value="${helperInfo.name}"/>
				</a>
			</c:forEach>
			</c:if>
			<c:if test="${!isWizard}">
				<a href="javascript:document.forms[0].submitAction.value='listPageActivities';document.forms[0].providerId.value='<c:out value="${helperInfo.provider.id}"/>';document.forms[0].submit();"
					title='<c:out value="${msgs.link_page_activities}"/>'>
					<c:out value="${msgs.link_page_activities}"/>
				</a>
			</c:if>
		</div>
		
	</c:if>
	
	
	<c:if test="${isWizard}">
		<!-- Since wizard doesn't have anything to check these, assume they are false until it is set to true based on isPageUsed --->
		<c:set var="evaluationFormUsed" value="false"/>
		<c:set var="feedbackFormUsed" value="false"/>
		<c:set var="reflectionFormUsed" value="false"/>
		<c:set var="customFormUsed" value="false"/>
	</c:if>
	

	<h3>
		<c:choose>
		  <c:when test="${pageTitleKey == 'view_cell'}">
			 <c:out value="${msgs.view_cell}" /> 
		  </c:when>
		  <c:when test="${pageTitleKey == 'title_editCell'}">
			 <c:out value="${msgs.title_editCell}" /> 
		  </c:when>
		  <c:when test="${pageTitleKey == 'view_wizardPage'}">
			 <c:out value="${msgs.view_wizardPage}" /> 
		  </c:when>
		  <c:when test="${pageTitleKey == 'title_editWizardPage'}">
			 <c:out value="${msgs.title_editWizardPage}" /> 
		  </c:when>
		</c:choose>
		- 
		<c:if test="${!isWizard}">
			<c:out value="${msgs.matrix_name}"/>
		</c:if>
		<c:if test="${isWizard}">
			<c:out value="${msgs.wizard_name}"/>
		</c:if>
		<span class="highlight"><c:out value="${scaffoldingCell.scaffolding.title}"/></span>
	</h3>

	<c:if test="${empty helperPage}">
		(<c:out value="${scaffoldingCell.title}"/>) 
		</c:if>
	<fieldset class="fieldsetVis">
		<legend>
		<c:choose>
		  <c:when test="${pageInstructionsKey == 'instructions_cellSettings'}">
			 <c:out value="${msgs.instructions_cellSettings}" /> 
		  </c:when>
		  <c:when test="${pageInstructionsKey == 'instructions_wizardPageSettings'}">
			 <c:out value="${msgs.instructions_wizardPageSettings}" /> 
		  </c:when>
		</c:choose>
		</legend>
	
		<div class="instruction"> 
			<c:out value="${msgs.instructions_requiredFields}" escapeXml="false"/> 
			<c:if test="${scaffoldingCell.scaffolding.published}">
				<c:if test="${isCellUsed}">
					<c:out value="${msgs.instructions_hasBeenUsed}"/>
					<c:set var="localDisabledText" value="disabled=\"disabled\""/>
				</c:if>
				<c:if test="${!isCellUsed}">
					<c:out value="${msgs.instructions_hasBeenPublished}"/>
				</c:if>
			</c:if>
			<c:if test="${wizardPublished}">
				<c:if test="${isPageUsed}">
					<c:out value="${msgs.instructions_hasBeenUsed}"/>
					
					<!-- Since wizard doesn't have anything to check these, assume the isUsed booleans are all true --->
					<c:set var="evaluationFormUsed" value="true"/>
					<c:set var="feedbackFormUsed" value="true"/>
					<c:set var="reflectionFormUsed" value="true"/>
					<c:set var="customFormUsed" value="true"/>
					
					<c:set var="localDisabledText" value="disabled=\"disabled\""/>
				</c:if>
				<c:if test="${!isPageUsed}">
					<c:out value="${msgs.instructions_wizardHasBeenPublished}"/>
				</c:if>
			</c:if>
		</div>
		
		
		<osp:form/>
		<input type="hidden" name="params" value="" />
		<input type="hidden" name="submitAction" value="forward" />
		<input type="hidden" name="dest" value="loadReviewers" />
		<input type="hidden" name="label" value="" /> 
		<input type="hidden" name="displayText" value="" /> 
		<input type="hidden" name="finalDest" value="" /> 
		<input type="hidden" name="validate" value="false" />
		
		
		<spring:bind path="scaffoldingCell.title">
			<c:if test="${status.error}">
				<p class="shorttext validFail">
			</c:if>			
			<c:if test="${!status.error}">
				<p class="shorttext">
			</c:if>	
				<span class="reqStar">*</span><label for="<c:out value="${status.expression}"/>-id"><c:out value="${msgs.label_cellTitle}"/></label>
				<input type="text" name="<c:out value="${status.expression}"/>"
				value="<c:out value="${status.displayValue}"/>" size="40" id="<c:out value="${status.expression}"/>-id" />
				<c:if test="${status.error}">
					<span class="alertMessageInline" style="border:none"><c:out value="${status.errorMessage}"/></span>
				</c:if>
			</p>
		</spring:bind>

		
		<div class="longtext">
			<label class="block"><c:out value="${msgs.label_cellDescription}"/></label>
			<spring:bind path="scaffoldingCell.wizardPageDefinition.description">
				<table><tr>
				<td><textarea name="<c:out value="${status.expression}"/>" id="descriptionTextArea" rows="5" cols="80">
				<c:out value="${status.value}"/></textarea></td>
				</tr></table>
				<c:if test="${status.error}">
					<div class="validation"><c:out value="${status.errorMessage}"/></div>
				</c:if>
			</spring:bind>
		</div>
	
		<c:if test="${isWizard != 'true'}">
			<spring:bind path="scaffoldingCell.initialStatus">  
				<c:if test="${status.error}">
					<div class="validation"><c:out value="${status.errorMessage}"/></div>
				</c:if>
				<p class="shorttext">
					<label for="<c:out value="${status.expression}"/>-id"><c:out value="${msgs.label_initialStatus}"/></label>     
					<select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}"/>-id">
						<option value="READY" <c:if test="${status.value=='READY'}"> selected="selected"</c:if>><c:out value="${msgs.matrix_legend_ready}"/></option>
						<option value="LOCKED" <c:if test="${status.value=='LOCKED'}">selected="selected"</c:if>><c:out value="${msgs.matrix_legend_locked}"/></option>
					</select>
				</p>
			</spring:bind>
		</c:if>
		
		<c:if test="${isWizard == 'true'}">
			<spring:bind path="scaffoldingCell.initialStatus">  
				<input type="hidden" name="<c:out value="${status.expression}"/>" value="READY" />
			</spring:bind>
		</c:if>
	
		<!-- ************* Style Area Start ************* -->
	
		<p class="shorttext">
			<label for="styleName"><c:out value="${msgs.style_section_header}"/></label>    
			<c:if test="${empty scaffoldingCell.wizardPageDefinition.style}">
				<input name="styleName" value="<c:out value="" />" id="styleName" type="text" />
				<a href="javascript:document.forms[0].dest.value='stylePickerAction';
				document.forms[0].submitAction.value='forward';
				document.forms[0].params.value='stylePickerAction=true:pageDef_id=<c:out value="${scaffoldingCell.wizardPageDefinition.id}" />:styleReturnView=<c:out value="${returnView}" />';
				document.forms[0].submit();">
				<osp:message key="select_style" /></a>
			</c:if>
			<c:if test="${not empty scaffoldingCell.wizardPageDefinition.style}">
				<c:set value="${scaffoldingCell.wizardPageDefinition.style}" var="style" />
				<input name="styleName" value="<c:out value="${style.name}" />" id="styleName" type="text" />
				<a href="javascript:document.forms[0].dest.value='stylePickerAction';
				document.forms[0].submitAction.value='forward';
				document.forms[0].params.value='stylePickerAction=true:currentStyleId=<c:out value="${style.id}"/>:pageDef_id=<c:out value="${scaffoldingCell.wizardPageDefinition.id}" />:styleReturnView=<c:out value="${returnView}" />';
				document.forms[0].submit();">
				<osp:message key="change_style" /></a>
			</c:if>
		</p>
		
		<p class="shorttext">
			<spring:bind path="scaffoldingCell.wizardPageDefinition.suppressItems">  
				<label for="suppressItems" ><c:out value="${msgs.suppressSelectItems_header}"/></label>    
				<input type="checkbox" name="suppressItems" value="true"  id="suppressItems" 
				<c:if test="${status.value}">checked</c:if> />
			</spring:bind>
		</p>
	</fieldset>
	<!-- ************* Style Area End ************* -->
	
	<!-- ************* Guidance Area Start ************* -->
	<fieldset class="fieldsetVis">
		<legend><osp:message key="guidance_header"/></legend>
		<c:if test ="${empty scaffoldingCell.guidance.instruction.limitedText && empty scaffoldingCell.guidance.instruction.attachments}">
			<h5 style="display:inline"><osp:message key="instructions"/></h5>
			<span class="indnt1">
				<a href="#"	onclick="javascript:document.forms[0].dest.value='editInstructions';document.forms[0].submitAction.value='forward';document.forms[0].submit();">
						<osp:message key="add_first_instructions"/></a>
			</span>
			<p class="indnt1 instruction">			
				<br>
				<osp:message key="add_first_instructions_message"/>
				<br>
				<br>
			</p>	
		</c:if>
		<c:if test ="${not empty scaffoldingCell.guidance.instruction.limitedText || not empty scaffoldingCell.guidance.instruction.attachments}">
			<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" style="width:70%" summary="<osp:message key="guidance_table_summary"/>">
				<tr>
					<th style="border-right-style:none"><h5><osp:message key="instructions"/></h5></th>                     
					<th style="text-align:right;border-left-style:none;"  class="specialLink itemAction">
						<a href="#" 
							onclick="javascript:document.forms[0].dest.value='editInstructions';document.forms[0].submitAction.value='forward';document.forms[0].submit();">
							<osp:message key="reviseInstructions"/>
						</a>	
					</th>
				</tr>
				<tr class="exclude">
					<td colspan="2">
						<c:if test="${not empty scaffoldingCell.guidance.instruction.limitedText}">
							<div class="textPanel"><c:out value="${scaffoldingCell.guidance.instruction.limitedText}" escapeXml="false" /></div>
						</c:if>
						<c:if test="${not empty scaffoldingCell.guidance.instruction.attachments}">
							<ul class="attachList indnt1">
								<c:forEach var="attachment" items="${scaffoldingCell.guidance.instruction.attachments}" varStatus="loopStatus">
									<li><img border="0" title="<c:out value="${attachment.displayName}" />"
										alt="<c:out value="${attachment.displayName}"/>" 
										src="/library/image/<osp-c:contentTypeMap 
										fileType="${attachment.mimeType}" mapType="image" 
										/>"/>
										<a title="<c:out value="${attachment.displayName}" />"
											href="<c:out value="${attachment.fullReference.base.url}" />" target="_blank">
											<c:out value="${attachment.displayName}"/>
										</a>
										<c:out value=" (${attachment.contentLength})"/>
									</li>
								</c:forEach>
							</ul>
						</c:if>
					</td>
				</tr>
			</table>
		</c:if>	
		<c:if test ="${empty scaffoldingCell.guidance.rationale.limitedText && empty scaffoldingCell.guidance.rationale.attachments}">
			<h5 style="display:inline"><osp:message key="rationale"/></h5>
			<span clas="indnt1">
				&nbsp;&nbsp;
				<a href="#" onclick="javascript:document.forms[0].dest.value='editRationale';document.forms[0].submitAction.value='forward';document.forms[0].submit();">
					<osp:message key="add_first_rationale"/></a>
			</span>
			<p class="indnt1 instruction">		
				<br>	
				<osp:message key="add_first_rationale_message"/>
				<br>	
				<br>			
			</p>
		</c:if>	
		<c:if test ="${not empty scaffoldingCell.guidance.rationale.limitedText || not empty scaffoldingCell.guidance.rationale.attachments}">		
			<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" style="width:70%"  summary="<osp:message key="rationale_table_summary"/>">
				<tr>
					<th style="border-right-style:none"><h5><osp:message key="rationale"/></h5></th>
					<th style="text-align:right;border-left-style:none;" class="specialLink itemAction">
						<a href="#" 
							onclick="javascript:document.forms[0].dest.value='editRationale';document.forms[0].submitAction.value='forward';document.forms[0].submit();" >
							<osp:message key="reviseRationale"/>
						</a>
					</th>
				</tr>	
				<tr class="exclude">
					<td colspan="2">
						<c:if test="${not empty scaffoldingCell.guidance.rationale.limitedText}">
							<div class="textPanel"><c:out value="${scaffoldingCell.guidance.rationale.limitedText}" escapeXml="false" /></div>
						</c:if>
						<c:if test="${not empty scaffoldingCell.guidance.rationale.attachments}">
							<ul class="attachList indnt1">
								<c:forEach var="attachment" items="${scaffoldingCell.guidance.rationale.attachments}" varStatus="loopStatus">
									<li><img border="0" title="<c:out value="${attachment.displayName}" />"
										alt="<c:out value="${attachment.displayName}"/>" 
										src="/library/image/<osp-c:contentTypeMap 
										fileType="${attachment.mimeType}" mapType="image" 
										/>"/>
										<a title="<c:out value="${attachment.displayName}" />"
											href="<c:out value="${attachment.fullReference.base.url}" />" target="_blank">
											<c:out value="${attachment.displayName}"/>
										</a>
										<c:out value=" (${attachment.contentLength})"/>
									</li>
								</c:forEach>
							</ul>
						</c:if>	
					</td>
				</tr>
			</table>
		</c:if>	
	
		<c:if test ="${empty scaffoldingCell.guidance.example.limitedText && empty scaffoldingCell.guidance.example.attachments}">
			<h5 style="display:inline"><osp:message key="examples"/></h5>
			<span clas="indnt1">
				&nbsp;&nbsp;
				<a href="#" onclick="javascript:document.forms[0].dest.value='editExamples';document.forms[0].submitAction.value='forward';document.forms[0].submit();">
					<osp:message key="add_first_examples"/></a>
			</span>
			<p class="indnt1 instruction">
				<br>
				<osp:message key="add_first_examples_message"/>
				<br>
				<br>				
			</p>	
		</c:if>
		<c:if test ="${not empty scaffoldingCell.guidance.example.limitedText || not empty scaffoldingCell.guidance.example.attachments}">	
			<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" style="width:70%"  summary="<osp:message key="examples_table_summary"/>">
				<tr>
					<th style="border-right-style:none"><h5><osp:message key="examples"/></h5></th>
					<th style="text-align:right;border-left-style:none;" class="itemAction specialLink">
						<a href="#"  onclick="javascript:document.forms[0].dest.value='editExamples';document.forms[0].submitAction.value='forward';document.forms[0].submit();" >
							<osp:message key="reviseExamples"/>
						</a>	
					</th>
				</tr>
				<tr class="exclude">
					<td colspan="2">
						<c:if test="${not empty scaffoldingCell.guidance.example.limitedText}">
							<div class="textPanel"><c:out value="${scaffoldingCell.guidance.example.limitedText}" escapeXml="false" /></div>
						</c:if>
						<c:if test="${not empty scaffoldingCell.guidance.example.attachments}">
							<ul class="attachList indnt1">
								<c:forEach var="attachment" items="${scaffoldingCell.guidance.example.attachments}" varStatus="loopStatus">
									<li><img border="0" title="<c:out value="${attachment.displayName}" />"
										alt="<c:out value="${attachment.displayName}"/>" 
										src="/library/image/<osp-c:contentTypeMap 
										fileType="${attachment.mimeType}" mapType="image" 
										/>"/>
										<a title="<c:out value="${attachment.displayName}" />"
											href="<c:out value="${attachment.fullReference.base.url}" />" target="_new">
											<c:out value="${attachment.displayName}"/>
										</a>
										<c:out value=" (${attachment.contentLength})"/>
									</li>	
								</c:forEach>
							</ul>
						</c:if>	
					</td>
				</tr>
			</table>
		</c:if>
		
		<!-- Rubric -->
		<c:if test ="${empty scaffoldingCell.guidance.rubric.limitedText && empty scaffoldingCell.guidance.rubric.attachments}">
			<h5 style="display:inline"><osp:message key="rubrics"/></h5>
			<span class="indnt1">
				<a href="#" onclick="javascript:document.forms[0].dest.value='editRubrics';document.forms[0].submitAction.value='forward';document.forms[0].submit();">
					<osp:message key="add_first_rubrics"/></a>
			</span>
			<p class="indnt1 instruction">
				<br>
				<osp:message key="add_first_rubrics_message"/>
				<br>	
				<br>			
			</p>	
		</c:if>
		<c:if test ="${not empty scaffoldingCell.guidance.rubric.limitedText || not empty scaffoldingCell.guidance.rubric.attachments}">	
			<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" style="width:70%"  summary="<osp:message key="rubrics_table_summary"/>">
				<tr>
					<th style="border-right-style:none"><h5><osp:message key="rubrics"/></h5></th>
					<th style="text-align:right;border-left-style:none;" class="itemAction specialLink">
						<a href="#"  onclick="javascript:document.forms[0].dest.value='editRubrics';document.forms[0].submitAction.value='forward';document.forms[0].submit();" >
							<osp:message key="reviseRubrics"/>
						</a>	
					</th>
				</tr>
				<tr class="exclude">
					<td colspan="2">
						<c:if test="${not empty scaffoldingCell.guidance.rubric.limitedText}">
							<div class="textPanel"><c:out value="${scaffoldingCell.guidance.rubric.limitedText}" escapeXml="false" /></div>
						</c:if>
						<c:if test="${not empty scaffoldingCell.guidance.rubric.attachments}">
							<ul class="attachList indnt1">
								<c:forEach var="attachment" items="${scaffoldingCell.guidance.rubric.attachments}" varStatus="loopStatus">
									<li><img border="0" title="<c:out value="${attachment.displayName}" />"
										alt="<c:out value="${attachment.displayName}"/>" 
										src="/library/image/<osp-c:contentTypeMap 
										fileType="${attachment.mimeType}" mapType="image" 
										/>"/>
										<a title="<c:out value="${attachment.displayName}" />"
											href="<c:out value="${attachment.fullReference.base.url}" />" target="_new">
											<c:out value="${attachment.displayName}"/>
										</a>
										<c:out value=" (${attachment.contentLength})"/>
									</li>	
								</c:forEach>
							</ul>
						</c:if>	
					</td>
				</tr>
			</table>
		</c:if>
		
		<!-- Expectations -->
		<c:if test ="${empty scaffoldingCell.guidance.expectations.limitedText && empty scaffoldingCell.guidance.expectations.attachments}">
			<h5 style="display:inline"><osp:message key="expectations"/></h5>
			<span class="indnt1">
				<a href="#" onclick="javascript:document.forms[0].dest.value='editExpectations';document.forms[0].submitAction.value='forward';document.forms[0].submit();">
					<osp:message key="add_first_expectations"/></a>
			</span>
			<p class="indnt1 instruction">
				<br>
				<osp:message key="add_first_expectations_message"/>
				<br>	
				<br>			
			</p>	
		</c:if>
		<c:if test ="${not empty scaffoldingCell.guidance.expectations.limitedText || not empty scaffoldingCell.guidance.expectations.attachments}">	
			<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" style="width:70%"  summary="<osp:message key="expectations_table_summary"/>">
				<tr>
					<th style="border-right-style:none"><h5><osp:message key="expectations"/></h5></th>
					<th style="text-align:right;border-left-style:none;" class="itemAction specialLink">
						<a href="#"  onclick="javascript:document.forms[0].dest.value='editExpectations';document.forms[0].submitAction.value='forward';document.forms[0].submit();" >
							<osp:message key="reviseExpectations"/>
						</a>	
					</th>
				</tr>
				<tr class="exclude">
					<td colspan="2">
						<c:if test="${not empty scaffoldingCell.guidance.expectations.limitedText}">
							<div class="textPanel"><c:out value="${scaffoldingCell.guidance.expectations.limitedText}" escapeXml="false" /></div>
						</c:if>
						<c:if test="${not empty scaffoldingCell.guidance.expectations.attachments}">
							<ul class="attachList indnt1">
								<c:forEach var="attachment" items="${scaffoldingCell.guidance.expectations.attachments}" varStatus="loopStatus">
									<li><img border="0" title="<c:out value="${attachment.displayName}" />"
										alt="<c:out value="${attachment.displayName}"/>" 
										src="/library/image/<osp-c:contentTypeMap 
										fileType="${attachment.mimeType}" mapType="image" 
										/>"/>
										<a title="<c:out value="${attachment.displayName}" />"
											href="<c:out value="${attachment.fullReference.base.url}" />" target="_new">
											<c:out value="${attachment.displayName}"/>
										</a>
										<c:out value=" (${attachment.contentLength})"/>
									</li>	
								</c:forEach>
							</ul>
						</c:if>	
					</td>
				</tr>
			</table>
		</c:if>
	</fieldset>	
	<!-- ************* Guidance Area End ************* -->    
	
	
	<!-- ************* Guidance and reflection Area Start ************* -->   
	
	
	<!-- *************  User Forms Area  Start ************* -->
	<fieldset class="fieldsetVis">
		<legend><c:out value="${msgs.legend_additional_user_Forms}"/></legend>
		
	<div>

		
		<h5><c:out value="${msgs.title_additionalForms}"/></h5>
		
		<!-- default case is currently only needed for matrices -->
		<c:if test="${scaffoldingCell.scaffolding != null && enableDafaultMatrixOptions == 'true'}" >
		
			<!-- ************* Default Matrix Custom Form Checkbox Start *********** -->
			<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultCustomForm">  
				<input type="hidden" name="hiddenDefaultCustomForm" value="${status.value}"/>
				<input type="checkbox" name="defaultCustomForm" value="true"  id="defaultCustomForm" 
					<c:if test="${status.value}">
						checked
					</c:if> 
					<c:if test="${customFormUsed}">
					    <c:out value="${localDisabledText}"/> 
					</c:if>
					onclick="$('div.toggle:first', $(this).parents('div:first')).slideToggle(resize);$('div.toggle2:first', $(this).parents('div:first')).slideToggle(resize);document.forms[0].hiddenDefaultCustomForm.value=this.checked;"/>
				<label for="defaultCustomForm" ><c:out value="${msgs.defaultCustomFormText}"/></label>    
			</spring:bind>		
			<!-- ************* Default Matrix Checkbox End *********** -->
			
			
		
			<!-- Start of Defualt Custom Forms -->
			
			
			<div name="defaultCustomFormSpan" id="defaultCustomFormSpan" class="toggle" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultCustomForm}">style='display:none' </c:if>>
			
				<c:if test="${ empty defaultSelectedAdditionalFormDevices}">
					<p class="indnt1"> 
						<span class="highlight"><c:out value="${msgs.addForms_instructions_noforms}" /></span>
					</p>
				</c:if>
				<c:if test="${not empty defaultSelectedAdditionalFormDevices}">
					<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" summary="<c:out value="${msgs.table_forms_summary}"/>" style="width:50%">
						<c:forEach var="chosenForm" items="${defaultSelectedAdditionalFormDevices}">
							<tr>
								<td>
									<span class="indnt1">
										<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
										<c:out value="${chosenForm.name}" />
									</span>
								</td>
							</tr>
						</c:forEach>
					</table>
				</c:if>
			</div>
			
			<!--- End of Defualt Custom Forms --->
			
		<!-- default case is currently only needed for matrices -->
		</c:if>
		
		
			<!--- Cell Custom Forms Start --->
			<div name="cellCustomFormSpan" id="cellCustomFormSpan"  class="toggle2" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultCustomForm}">style='display:none' </c:if>>
				
				<p class="indnt1"> 
					<c:out value="${msgs.addForms_instructions}" />
					
				</p>
				
				
				<p class="shorttext">
					<label for="selectAdditionalFormId" ><c:out value="${msgs.label_selectForm}"/></label>    
					<select name="selectAdditionalFormId"  id="selectAdditionalFormId"  onchange="document.getElementById('addForm-id').className='active';">
						<option value="" selected="selected"><c:out value="${msgs.select_form_text}" /></option>
						<c:forEach var="addtlForm" items="${additionalFormDevices}" varStatus="loopCount">
							<option value="<c:out value="${addtlForm.id}"/>">
						<c:out value="${addtlForm.name}"/></option>
						</c:forEach>
					</select>
					<span class="act">
						<input type="submit" id="addForm-id" name="addForm" value="<c:out value="${msgs.button_add}"/>" onclick="javascript:document.forms[0].validate.value='false';" />
					</span>
				</p>
				<c:if test="${ empty selectedAdditionalFormDevices}">
					<span class="indnt2 instruction"><c:out value="${msgs.addForms_instructions_noforms}" /></span>
				</c:if>
				<c:if test="${not empty selectedAdditionalFormDevices}">
					<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" summary="<c:out value="${msgs.table_forms_summary}"/>" style="width:50%">
						<c:forEach var="chosenForm" items="${selectedAdditionalFormDevices}">
							<tr>
								<td>
									<span class="indnt1">
										<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
										<c:out value="${chosenForm.name}" />
									</span>

			   						<c:set var="formUsed" value="false"/>
			   						<c:forEach var="usedForm" items="${usedAdditionalForms}">
			   							<c:if test="${usedForm == chosenForm.id}">
			   								<c:set var="formUsed" value="true"/>
			   							</c:if>
									</c:forEach>
									<c:if test="${formUsed == false}">
										<span class="itemAction indnt1">
											<a href="javascript:document.forms[0].submitAction.value='removeFormDef';
												document.forms[0].params.value='id=<c:out value="${chosenForm.id}"/>';
												document.forms[0].submit();">
												<osp:message key="remove"/>
											</a>
										</span>
									</c:if>
								</td>
							</tr>
						</c:forEach>
					</table>
				</c:if>
			</div>
			
			</div>
			<!--- Cell Custom Forms End --->
			
			
			<!-- ************* Assignments Area Start ************* -->   
				
			<c:if test="${enableAssignments}">
			<br><br>
				<h5><osp:message key="edit.assignments"/></h5>
				<c:if test="${empty selectedAssignments}">
					<p class="indnt1">
						<a href="#"	onclick="javascript:document.forms[0].dest.value='assignPickerAction';
							document.forms[0].submitAction.value='forward';
							document.forms[0].params.value='assignPickerAction=true:pageDef_id=<c:out value="${scaffoldingCell.wizardPageDefinition.id}" />:assignReturnView=<c:out value="${returnView}" />';
							document.forms[0].submit();">
							<osp:message key="add_first_assignment"/>
						</a>
						&nbsp;<osp:message key="add_first_assignment_message"/>					
					</p>	
				</c:if>
				<c:if test="${not empty selectedAssignments}">
					<table cellpadding="0" cellspacing="0" border="0" style="width:70%" class="listHier lines nolines collectionListBordered">
						<tr>
							<th style="text-align:left"></th> 
							<th style="text-align:right" class="itemAction">
								<a href="#"	onclick="javascript:document.forms[0].dest.value='assignPickerAction';
									document.forms[0].submitAction.value='forward';
									document.forms[0].params.value='assignPickerAction=true:pageDef_id=<c:out value="${scaffoldingCell.wizardPageDefinition.id}" />:assignReturnView=<c:out value="${returnView}" />';
									document.forms[0].submit();">
									<osp:message key="edit.addAssign"/>
								</a>
							</th>
						</tr>
						<c:forEach var="assign" items="${selectedAssignments}">
							<tr>
								<td colspan="2">
									<span class="indnt1">
										<img src = '/library/image/silk/page_white_edit.png' border= '0' alt ='' />
										<c:out value="${assign.title}" />
									</span>
								</td>
							</tr>
						</c:forEach>
					</table>
				</c:if>	
			</c:if>	
			
			<!-- ************* Assignments Area End ************* -->   
			
			
		<br><br>
			
		<div>
		<h5><osp:message key="label_selectReflectionDevice"/></h5>
		
		<!-- default case is currently only needed for matrices -->
		<c:if test="${scaffoldingCell.scaffolding != null && enableDafaultMatrixOptions == 'true'}" >
		
			<!-- ************* Default Matrix Reflection Form Checkbox Start *********** -->
			<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultReflectionForm">  
				<input type="hidden" name="hiddenDefaultReflectionForm" value="${status.value}"/>
				<input type="checkbox" name="defaultReflectionForm" value="true"  id="defaultReflectionForm" 
					<c:if test="${status.value}">
						checked
					</c:if> 
				    <c:if test="${reflectionFormUsed}"><c:out value="${localDisabledText}"/></c:if>
				onclick="$('div.toggle:first', $(this).parents('div:first')).slideToggle(resize);$('div.toggle2:first', $(this).parents('div:first')).slideToggle(resize);document.forms[0].hiddenDefaultReflectionForm.value=this.checked;"/>

				<label for="defaultReflectionForm" ><c:out value="${msgs.defaultReflectionFormText}"/></label>    
			</spring:bind>		
			<!-- ************* Default Matrix Checkbox End *********** -->
			
			
			
			<!-- Default Reflection Area start -->
			
			<div name="defaultReflectionFormSpan" id="defaultReflectionFormSpan" class="toggle" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultReflectionForm}">style='display:none' </c:if>>
				
				<spring:bind path="scaffoldingCell.scaffolding.reflectionDevice">
					<c:if test="${status.value == null}">
						<p class="indnt1"> 
							<span class="highlight"><c:out value="${msgs.addForms_instructions_noforms}" /></span>
						</p>
					</c:if>
						
					<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" summary="<c:out value="${msgs.table_forms_summary}"/>" style="width:50%">
						<c:forEach var="refDev" items="${reflectionDevices}" varStatus="loopCount">
							<c:if test="${status.value==refDev.id}">
								<tr>
									<td>
										<span class="indnt1">
											<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
											<c:out value="${refDev.name}"/>
										</span>
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</table>						
				
				</spring:bind>
			</div>
			
			<!-- Default Reflection Area end -->
		
		<!-- default case is currently only needed for matrices -->
		</c:if>
		
		

		<!-- cell Reflection area start -->   
		
		<div name="cellReflectionFormSpan" id="cellReflectionFormSpan" class="toggle2" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultReflectionForm}">style='display:none' </c:if>>

			<spring:bind path="scaffoldingCell.reflectionDeviceType">  
				<input type="hidden" name="<c:out value="${status.expression}"/>"
				value="<c:out value="${status.value}"/>" />
			</spring:bind>
		
			<spring:bind path="scaffoldingCell.reflectionDevice">  
				<c:if test="${status.error}">
					<div class="validation"><c:out value="${status.errorMessage}"/></div>
				</c:if>
				<p class="indnt1">
					<c:out value="${msgs.reflection_select_instructions}"/>
				</p>	
				<p class="shorttext"> 
					<label for="<c:out value="${status.expression}-id"/>"><c:out value="${msgs.label_selectReflectionDevice}"/></label>    
					<select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>" 
						<c:if test="${not empty status.value}"> <c:if test="${reflectionFormUsed}"><c:out value="${localDisabledText}"/></c:if> </c:if>>
						<option onclick="document.forms[0].reflectionDeviceType.value='';" value=""><c:out value="${msgs.select_item_text}" /></option>
						<c:forEach var="refDev" items="${reflectionDevices}" varStatus="loopCount">
							<option onclick="document.forms[0].reflectionDeviceType.value='<c:out value="${refDev.type}"/>';" 
							value="<c:out value="${refDev.id}"/>" <c:if test="${status.value==refDev.id}"> selected="selected"</c:if>><c:out value="${refDev.name}"/></option>
						</c:forEach>
					</select>
				</p>
			</spring:bind>
		
		</div>
		
		</div>
		<!-- *********  End span for hidding user forms when default user forms is checked *** -->
	</fieldset>
	
	
	

	<!--- Feedback Fieldset: --->

	<c:if test="${not feedbackOpts.itemFeedbackNone or not feedbackOpts.generalFeedbackNone}">
	<fieldset class="fieldsetVis">
		<legend><c:out value="${msgs.legend_feedback}"/></legend>
		
		<div>
		<h5><osp:message key="label_selectReviewDevice"/></h5>		
		
		
		<!-- this case is currently only needed for matrices -->
		<c:if test="${scaffoldingCell.scaffolding != null && enableDafaultMatrixOptions == 'true'}">


			<!-- ************* Default Matrix Checkbox Start *********** -->
			<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultFeedbackForm">  		
				<input type="hidden" name="hiddenDefaultFeedbackForm" value="${status.value}"/>
				<input type="checkbox" name="defaultFeedbackForm" value="true"  id="defaultFeedbackForm" 
				<c:if test="${status.value}">checked</c:if> 
				onclick="$('div.toggle:first', $(this).parents('div:first')).slideToggle(resize);$('div.toggle2:first', $(this).parents('div:first')).slideToggle(resize);document.forms[0].hiddenDefaultFeedbackForm.value=this.checked;"
				<c:if test="${feedbackFormUsed}"><c:out value="${localDisabledText}"/></c:if> />
				<label for="defaultFeedbackForm" ><c:out value="${msgs.defaultFeedbackFormText}"/></label> 
			</spring:bind>
			
			<!-- ************* Default Matrix Checkbox Start *********** -->
	
	
			<!-- Default Feedback Form start -->
			<div name="defaultFeedbackEvalSpan" id="defaultFeedbackEvalSpan" class="toggle" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultFeedbackForm}">style='display:none' </c:if>>
				<!-- Feedback -->
				
				<spring:bind path="scaffoldingCell.scaffolding.reviewDevice">
					<c:if test="${status.value == null}">
						<p class="indnt1"> 
							<span class="highlight"><c:out value="${msgs.addForms_instructions_noforms}" /></span>
						</p>
					</c:if>
						
					<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" summary="<c:out value="${msgs.table_forms_summary}"/>" style="width:50%">
						<c:forEach var="revDev" items="${reviewDevices}" varStatus="loopCount">
							<c:if test="${status.value==revDev.id}">
								<tr>
									<td>
										<span class="indnt1">
											<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
											<c:out value="${revDev.name}"/>
										</span>
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</table>						
				</spring:bind>
			</div>
			<!-- Default Feedback Form end -->
		
			
		<!-- this case is currently only needed for matrices -->
		</c:if>

		<!--- Cell Feedback form start --->
		<div name="cellFeedbackFormSpan" id="cellFeedbackFormSpan" class="toggle2" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultFeedbackForm}">style='display:none' </c:if>>
			
			<spring:bind path="scaffoldingCell.reviewDeviceType">  
				<input type="hidden" name="<c:out value="${status.expression}"/>"
				value="<c:out value="${status.value}"/>" />
			</spring:bind>   
			
			<spring:bind path="scaffoldingCell.reviewDevice">  
				<c:if test="${status.error}">
					<div class="validation"><c:out value="${status.errorMessage}"/></div>
				</c:if>

				<p class="indnt1">
					<c:out value="${msgs.feedback_select_instructions}"/>
				</p>	
				<p class="shorttext">
					<label for="<c:out value="${status.expression}-id"/>"><c:out value="${msgs.label_selectReviewDevice}"/></label>    
					<select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>"
						<c:if test="${not empty status.value}"> <c:if test="${feedbackFormUsed}"><c:out value="${localDisabledText}"/></c:if> </c:if>>
						<option onclick="document.forms[0].reviewDeviceType.value='';" value=""><c:out value="${msgs.select_item_text}" /></option>
						<c:forEach var="reviewDev" items="${reviewDevices}" varStatus="loopCount">
							<option onclick="document.forms[0].reviewDeviceType.value='<c:out value="${reviewDev.type}"/>';" 
							value="<c:out value="${reviewDev.id}"/>" <c:if test="${status.value==reviewDev.id}"> selected="selected"</c:if>><c:out value="${reviewDev.name}"/></option>
						</c:forEach>
					</select>
				</p>
			</spring:bind>
		</div>
		
		</div>
		<!--- Cell Feedback form end --->			
			
			
		<c:if test="${!isWizard}">
		
			<!--- Reviewers Area Start --->
			
			<h5><osp:message key="label_reviwers"/></h5>		
			
			<div>
			<!-- this case is currently only needed for matrices -->
			<c:if test="${scaffoldingCell.scaffolding != null && enableDafaultMatrixOptions == 'true'}">
	
	
				<!-- ************* Default Matrix Checkbox Start *********** -->
				<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultReviewers">  			   
					<input type="checkbox" name="defaultReviewers" value="true"  id="defaultReviewers" 
					<c:if test="${status.value}">checked</c:if> 
					onclick="$('div.toggle:first', $(this).parents('div:first')).slideToggle(resize);$('div.toggle2:first', $(this).parents('div:first')).slideToggle(resize);"
					
					<label for="defaultReviewers" ><c:out value="${msgs.defaultReviewersText}"/></label> 
				</spring:bind>
				
				<!-- ************* Default Matrix Checkbox Start *********** -->
		
		
				<!-- Default Reviewers start -->
				<div name="defaultReviewersSpan" id="defaultReviewersSpan" class="toggle" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultReviewers}">style='display:none' </c:if>>
					
					<p class="indnt1">
						<input type="checkbox" name="diabledCheckbox" value="true"  id="disabledCheckbox" 
							<c:if test="${scaffoldingCell.scaffolding.allowRequestFeedback}">checked</c:if> onclick="defaultFormClicked(this.checked, 'defaultReviewersSpan', 'cellReviewersSpan');" disabled/>
						<label for="diabledCheckbox" ><c:out value="${msgs.allowRequestFeedback}"/></label> 
					</p>
	
	
	
					<!-- Reviewers list -->
	
					<c:if test="${not empty defaultReviewers}">
						<ol>
							<c:forEach var="eval" items="${defaultReviewers}">
								<li><c:out value="${eval}" /></li>
							</c:forEach>
						</ol>
					</c:if>	
					<c:if test="${empty defaultReviewers}">
						<p class="indnt1">
							<span class="instruction"><c:out value="${msgs.info_reviewersNone}"/></span>
						</p>			
					</c:if>
					
					
				</div>
				<!--  Default Reviewers start  -->
				
				
	
			<!-- this case is currently only needed for matrices -->
			</c:if>
				
					
			<!-- Cell Reviewers Start -->            
			<div name="cellReviewersSpan" id="cellReviewersSpan" class="toggle2" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultReviewers}">style='display:none' </c:if>>
		
				<c:if test="${not empty reviewers}">
					<ol>
						<c:forEach var="eval" items="${reviewers}" varStatus="rowCounter">
							<li><c:out value="${rouCounter.count} ${eval}" /></li>
						</c:forEach>
					</ol>
				</c:if>	
				
				<c:if test="${empty reviewers}">
					<div class="indnt1 instruction"><c:out value="${msgs.info_reviewersNone}"/></div>
				</c:if>
				<div class="indnt1">
					<a href="#"	onclick="javascript:document.forms[0].dest.value='selectReviewers';document.forms[0].submitAction.value='forward';document.forms[0].submit();" >
						<osp:message key="select_reviewers"/>
					</a>	 					
				</div>
				<p class="indnt1"> 
				<spring:bind path="scaffoldingCell.wizardPageDefinition.allowRequestFeedback">  			
					<input type="checkbox" name="allowRequestFeedback" value="true"  id="allowRequestFeedback" 
						<c:if test="${status.value}">
							checked
						</c:if> 
					 />
					<label for="allowRequestFeedback" ><c:out value="${msgs.allowRequestFeedback}"/></label>    
				</spring:bind>	
				</p>
			</div>
			</div>
			<!-- Cell Reviewers End -->
		</c:if>
			
	</fieldset>
	</c:if>
		
	<!--  ********** Evaluation start ************* -->
	<fieldset class="fieldsetVis">
		<legend><c:out value="${msgs.legend_evaluation}"/></legend>

		<h5><c:out value="${msgs.header_Evaluators}"/></h5>
		<div>
		<!-- this case is currently only needed for matrices -->
		<c:if test="${scaffoldingCell.scaffolding != null && enableDafaultMatrixOptions == 'true'}">


			<!-- ************* Default Matrix Checkbox Start *********** -->
			<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultEvaluationForm">  		
				<input type="hidden" name="hiddenDefaultEvaluationForm" value="${status.value}"/>
				<input type="checkbox" name="defaultEvaluationForm" value="true"  id="defaultEvaluationForm" 
					<c:if test="${status.value}">checked</c:if> 
					onclick="$('div.toggle:first', $(this).parents('div:first')).slideToggle(resize);$('div.toggle2:first', $(this).parents('div:first')).slideToggle(resize);document.forms[0].hiddenDefaultEvaluationForm.value=this.checked;"
					<c:if test="${evaluationFormUsed}"><c:out value="${localDisabledText}"/></c:if>  
				/>
				<label for="defaultEvaluationForm" ><c:out value="${msgs.defaultEvaluationFormText}"/></label> 
			</spring:bind>
		
		
		
		
			<!-- Evaluation Form Default Area Start-->
			<div name="defaultEvaluationFormSpan" id="defaultEvaluationFormSpan" class="toggle" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultEvaluationForm}">style='display:none' </c:if>>
				<spring:bind path="scaffoldingCell.scaffolding.evaluationDevice">
					<p class="indnt1">
						<input type="checkbox" name="diabledCheckbox2" value="true"  id="disabledCheckbox" 
							<c:if test="${scaffoldingCell.scaffolding.hideEvaluations}">checked</c:if> disabled/>
						<label for="diabledCheckbox2" ><c:out value="${msgs.hideEvaluations}"/></label> 
					</p>
					
					<c:if test="${status.value == null}">
						<p class="indnt1"> 
							<span class="highlight"><c:out value="${msgs.addForms_instructions_noforms}" /></span>
						</p>
					</c:if>
						
					<table class="listHier lines nolines" cellpadding="0" cellspacing="0" border="0" summary="<c:out value="${msgs.table_forms_summary}"/>" style="width:50%">
						<c:forEach var="evalDev" items="${evaluationDevices}" varStatus="loopCount">
							<c:if test="${status.value==evalDev.id}">
								<tr>
									<td>
										<span class="indnt1">
											<img src = '/library/image/sakai/generic.gif' border= '0' alt ='' />
											<c:out value="${evalDev.name}"/>
										</span>
									</td>
								</tr>
							</c:if>
						</c:forEach>
					</table>						
				</spring:bind>
			</div>
			
			<!-- Evaluation Form Default Area End -->	
		
		<!-- this case is currently only needed for matrices -->
		</c:if>	
		
		
		<!-- Evaluation Form Cell Area Start -->
		<div name="cellEvaluationFormSpan" id="cellEvaluationFormSpan" class="toggle2" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultEvaluationForm}">style='display:none' </c:if>>
			<div id="evaluatorsDiv">  
				<p class="indnt1">
					<c:out value="${msgs.evaluation_select_instructions}"/>
				</p>
				<spring:bind path="scaffoldingCell.evaluationDevice">  
					<c:if test="${status.error}">
				<div class="validation"><c:out value="${status.errorMessage}"/></div>
				</c:if>
					<p class="shorttext">
						<label for="<c:out value="${status.expression}-id"/>"><c:out value="${msgs.label_selectEvaluationDevice}"/></label>    
						<select name="<c:out value="${status.expression}"/>" id="<c:out value="${status.expression}-id"/>"
							<c:if test="${not empty status.value}"> <c:if test="${evaluationFormUsed}"><c:out value="${localDisabledText}"/></c:if> </c:if>>
							<option onclick="document.forms[0].evaluationDeviceType.value='';" value=""><c:out value="${msgs.select_item_text}" /></option>
							<c:forEach var="evalDev" items="${evaluationDevices}" varStatus="loopCount">
								<option onclick="document.forms[0].evaluationDeviceType.value='<c:out value="${evalDev.type}"/>';" 
								value="<c:out value="${evalDev.id}"/>" <c:if test="${status.value==evalDev.id}"> selected="selected"</c:if>><c:out value="${evalDev.name}"/></option>
							</c:forEach>
						</select>
					</p>
				</spring:bind>
				
				<c:if test="${!isWizard}">
					<p class="indnt1"> 
						<spring:bind path="scaffoldingCell.wizardPageDefinition.hideEvaluations">  			
							<input type="checkbox" name="hideEvaluations" value="true"  id="hideEvaluations" 
								<c:if test="${status.value}">
									checked
								</c:if> 
							 />
							<label for="hideEvaluations" ><c:out value="${msgs.hideEvaluations}"/></label>    
						</spring:bind>	
					</p>
				</c:if>
			</div>
		</div>		
		<!-- Evaluation Form Cell Area End -->		
				
		</div>		
				
				
				
		<!--  Evaluator List Area Start --->
		<h5><c:out value="${msgs.label_evaluators}"/></h5>
		<div>
		
		<!-- this case is currently only needed for matrices -->
		<c:if test="${scaffoldingCell.scaffolding != null && enableDafaultMatrixOptions == 'true'}">


			<!-- ************* Default Matrix Checkbox Start *********** -->
			<spring:bind path="scaffoldingCell.wizardPageDefinition.defaultEvaluators">  			   
				<input type="checkbox" name="defaultEvaluators" value="true"  id="defaultEvaluators" 
				<c:if test="${status.value}">checked</c:if>
				onclick="$('div.toggle:first', $(this).parents('div:first')).slideToggle(resize);$('div.toggle2:first', $(this).parents('div:first')).slideToggle(resize);" />
				<label for="defaultEvaluators" ><c:out value="${msgs.defaultEvaluatorsText}"/></label> 
			</spring:bind>
			
			<!-- Evaluator List Default Area Start-->
			<div name="defaultEvaluatorsSpan" id="defaultEvaluatorsSpan" class="toggle" <c:if test="${!scaffoldingCell.wizardPageDefinition.defaultEvaluators}">style='display:none' </c:if>>
				<c:if test="${not empty defaultEvaluators}">
					<ol>
						<c:forEach var="eval" items="${defaultEvaluators}" varStatus="rowCounter">
							<li><c:out value="${rouCounter.count} ${eval}" /></li>
						</c:forEach>
					</ol>
				</c:if>	
				<c:if test="${empty defaultEvaluators}">
					<p class="indnt1">
						<span class="instruction"><c:out value="${msgs.no_evaluators}"/></span>
					</p>			
				</c:if>
			
			</div>
			<!-- Evaluator List Default Area End -->
			
			
		<!-- this case is currently only needed for matrices -->
		</c:if>	
			
		<!-- Cell Evaluator List Start -->
		<div name="cellEvaluatorsSpan" id="cellEvaluatorsSpan" class="toggle2" <c:if test="${!isWizard and scaffoldingCell.wizardPageDefinition.defaultEvaluators}">style='display:none' </c:if>>
			<c:if test="${not empty evaluators}">
				<ol>
					<c:forEach var="eval" items="${evaluators}">
						<li><c:out value="${eval}" /></li>
					</c:forEach>
				</ol>
			</c:if>	
			
			<c:if test="${empty evaluators}">
				<div class="instruction indnt1"><c:out value="${msgs.no_evaluators}"/></div>
			</c:if>
			<div class="indnt1">
				<a href="#"	onclick="javascript:document.forms[0].dest.value='selectEvaluators';document.forms[0].submitAction.value='forward';document.forms[0].submit();" >
					<osp:message key="select_evaluators"/>
				</a>	 
			</div>
		
		</div>
		</div>
		<!-- Cell Evaluator List End -->

	</fieldset>



	<spring:bind path="scaffoldingCell.id">
		<input type="hidden" name="<c:out value="${status.expression}"/>" value="<c:out value="${status.displayValue}"/>"/>
		<span class="error" style="border:none"><c:out value="${status.errorMessage}"/></span>
	</spring:bind>
	
	<c:if test="${taggable}">
		<%@ include file="../tagLists.jspf" %>
	</c:if>
	
	<div class="act">
		<input type="submit" name="saveAction" value="<osp:message key="save"/>" class="active" onclick="javascript:document.forms[0].validate.value='true';" accesskey="s" />
		<c:if test="${empty helperPage}">
			<input type="button" name="action" value="<osp:message key="cancel"/>"
			onclick="javascript:document.form.submitAction.value='cancel';document.form.submit();" accesskey="x"/>
		</c:if>
		<c:if test="${not empty helperPage}">
			<input type="button" name="action" value="<osp:message key="cancel"/>"
			onclick="javascript:doCancel()"  accesskey="x"/>
			<input type="hidden" name="canceling" value="" />
		</c:if>
	</div>
	
	<osp:richTextWrapper textAreaId="descriptionTextArea" />
	
</form>
	
<form name="cancelForm" method="post">
	<osp:form/>

	<input type="hidden" name="validate" value="false" />
	<input type="hidden" name="canceling" value="true" />
</form>
