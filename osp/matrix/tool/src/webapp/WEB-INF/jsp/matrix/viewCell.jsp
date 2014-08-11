<%@ include file="/WEB-INF/jsp/include.jsp"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<jsp:useBean id="msgs" class="org.sakaiproject.util.ResourceLoader" scope="request"><jsp:setProperty name="msgs" property="baseName" value="org.theospi.portfolio.matrix.bundle.Messages"/></jsp:useBean>


<osp-c:authZMap	prefix="osp.wizard." var="wizardCan" qualifier="${siteId}" />
<c:set var="canOperateWizard" value="false" /> 
<c:if test="${wizardId != null}">
	<osp-c:authZMap	prefix="osp.wizard." var="canOperate" qualifier="${wizardId}" />
	<c:set var="canOperateWizard" value="${canOperate.operate}" /> 	
</c:if>
<c:if test="${nullCellError}">
	<div class="alertMessage">
		<c:out value="${msgs.viewcell_nullwarning}"/>
	</div>
</c:if>
<c:if test="${matrixCanViewCell || (isWizard == 'true' && (isWizardOwner || (canOperateWizard && (wizardCan.evaluate || wizardCan.review))))}">



<%
  	String thisId = request.getParameter("panel");
  	if (thisId == null) 
  	{
    	thisId = "Main" + org.sakaiproject.tool.cover.ToolManager.getCurrentPlacement().getId();
 		 }
%>
<script type="text/javascript">
	function resize(){
		mySetMainFrameHeightViewCell('<%= org.sakaiproject.util.Web.escapeJavascript(thisId)%>');
	}
	
	
function mySetMainFrameHeightViewCell(id)
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
			
			


<c:set var="date_format">
	<osp:message key="dateFormat_time" />
</c:set>


<script type="text/javascript" language="JavaScript" src="/osp-common-tool/js/thickbox.js"></script>
<link href="/osp-common-tool/css/thickbox.css" type="text/css" rel="stylesheet" media="all" />


<script type="text/javascript"
	src="/osp-jsf-resource/xheader/xheader.js"></script>

<c:forEach var="style" items="${styles}">
	<link href="<c:out value='${style}'/>" type="text/css" rel="stylesheet"
		media="all" />
</c:forEach>
<form name="form" method="post"
	<c:if test="${sequential == 'true'}">
      action="<osp:url value="sequentialWizardPage.osp"/>"
   </c:if>
	<c:if test="${helperPage == 'true' && !sequential == 'true'}">
      action="<osp:url value="wizardPage.osp"/>"
   </c:if>
	<c:if test="${helperPage != 'true'}">
      action="<osp:url value="viewCell.osp"/>"
   </c:if>>

<osp:form /> <input type="hidden" name="submitAction" value="" /> <c:if
	test="${sequential == 'true'}">
	<input type="hidden" name="view" value="sequentialWizardPage.osp" />
</c:if> <c:if test="${helperPage == 'true' && !sequential == 'true'}">
	<input type="hidden" name="view" value="wizardPage.osp" />
</c:if> <c:if test="${helperPage != 'true'}">
	<input type="hidden" name="view" value="viewCell.osp" />
</c:if> 
	<c:set var="cell" value="${cellBean.cell}" /> 
	<osp-c:authZMap prefix="osp.matrix.scaffoldingSpecific." var="scaffoldingCan" qualifier="${cell.scaffoldingCell.scaffolding.reference}"/>
	<osp-c:authZMap prefix="osp.matrix.scaffolding." var="can" qualifier="${cell.scaffoldingCell.scaffolding.worksiteId}"/>
	<osp-c:authZMap prefix="osp.matrix.scaffolding.revise." var="canRevise" qualifier="${cell.scaffoldingCell.scaffolding.worksiteId}"/>
	<osp-c:authZMap prefix="osp.matrix.scaffolding.delete." var="canDelete" qualifier="${cell.scaffoldingCell.scaffolding.worksiteId}"/>
	<osp-c:authZMap prefix="osp.matrix.scaffolding.publish." var="canPublish" qualifier="${cell.scaffoldingCell.scaffolding.worksiteId}"/>
	<osp-c:authZMap prefix="osp.matrix.scaffolding.export." var="canExport" qualifier="${cell.scaffoldingCell.scaffolding.worksiteId}"/> 	
	
	
		<%-- TODO - need to see if user gets any of these abilities, if not omit whole toolbar --%>
<div class="navIntraTool TB_hideControl"><c:if
	test="${(isWizard != 'true' && scaffoldingCan.manageStatus) || (isWizard == 'true' && wizardCan.create)}">
	<a name="linkManageCellStatus" id="linkManageCellStatus"
		href="<osp:url value="manageCellStatus.osp">
            <osp:param name="page_id" value="${cell.wizardPage.id}"/>
            <osp:param name="readOnlyMatrix" value="${readOnlyMatrix}" />
            <osp:param name="isWizard" value="${isWizard}" />
            <osp:param name="sequential" value="${sequential}" />
            </osp:url>"><osp:message
		key="manage_cell_status" /></a>
</c:if> <c:if test="${taggable && !(empty helperInfoList)}">
	<c:forEach var="helperInfo" items="${helperInfoList}">
		<a title='<c:out value="${helperInfo.description}"/>'
			href="javascript:document.form.submitAction.value='tagItem';document.form.providerId.value='<c:out value="${helperInfo.provider.id}"/>';document.form.submit();">
		<c:out value="${helperInfo.name}" /> </a>
	</c:forEach>
</c:if></div>
<c:if test="${cell.scaffoldingCell.scaffolding.preview}">
	<div class="validation"><c:out value="${msgs.title_cellPreview}" /></div>
</c:if>

<c:if test="${(isWizard != 'true' && (scaffoldingCan.accessUserList || cell.wizardPage.owner.id == currentUser)) || (isWizard == 'true')}">
<h2 class="owner">
   <c:out value="${wizardOwner}" />
</h2>
</c:if>

<c:if test="${isWizard == 'true'}">
	<osp-h:glossary link="true" hover="true">
		<h3><c:out value="${wizardTitle}" /></h3>
		<div class="instruction"><c:out value="${wizardDescription}"
			escapeXml="false" /></div>
	</osp-h:glossary>

	<c:if test="${sequential == 'true'}">
		<p class="step"><c:out value="${msgs.seq_pages_step}"/>
				<c:out value="${currentStep}" /> / 
				<c:out value="${totalSteps}" /> :
				<c:out value="${cell.scaffoldingCell.wizardPageDefinition.title}" />
		</p>
	</c:if>

</c:if> <osp-h:glossary link="true" hover="true">
	<c:if test="${isWizard == 'true' and sequential != 'true'}">
		<h3>
			<c:out value="${categoryTitle}" />
			<c:if test="${categoryTitle != ''}">:
	          
	        </c:if>
	    </h3>
	</c:if> 
	<c:choose>
		<c:when test="${isMatrix == 'true'}">
			<br>
			<h3 style="display: inline"><c:if
				test="${scaffoldingCan.accessUserList || cell.wizardPage.owner.id == currentUser}">
				<c:out value="${cell.wizardPage.owner.displayName}: "/>
			</c:if> <c:out
				value="${cell.scaffoldingCell.scaffolding.title}: ${cell.scaffoldingCell.wizardPageDefinition.title}" />
			</h3>
		</c:when>
		<c:otherwise>
			<h3 style="display: inline"><osp:message key="label_title" />:&nbsp;</h3>
			<c:out value="${cell.scaffoldingCell.wizardPageDefinition.title}" />
		</c:otherwise>
	</c:choose>
	<br>	
	<br>
	
		
		
</osp-h:glossary> <c:if test="${(cell.status != 'READY' && cell.status != 'RETURNED')}">
	<div class="information"><c:out value="${msgs.status_warning}"/> <c:out value="${cell.status}" />
	</div>
</c:if> 
<c:if test="${feedbackSent}">
	<div class="information"><c:out value="${msgs.feedbackSentMessage}"/></div>
</c:if>

	<c:if test="${not empty cell.scaffoldingCell.guidance || not empty cell.scaffoldingCell.wizardPageDefinition.description}">
	<div>
		<a name="viewCellInformation" id="viewCellInformation_show" title="<osp:message key="supporting_info" />" class="show" style="display:none"
				href="#" onclick="$(this).next('.hide').toggle();$('div.toggle:first', $(this).parents('div:first')).slideToggle(resize);$(this).toggle();">
				<osp:message key="matrix_viewing_title_view" />&nbsp;<osp:message key="supporting_info" /></a>
	

		<a name="hideGuidanceInformation" id="hideGuidanceInformation_hide" title="<osp:message key="supporting_info" />" class="hide" 
				href="#" onclick="$(this).prev('.show').toggle(); $('div.toggle:first', $(this).parents('div:first')).slideToggle(resize);$(this).toggle();">
			   <osp:message key="matrix_viewing_title_hide" />&nbsp;<osp:message key="supporting_info" /></a>
		
		<div class="toggle">
		
		<!-- ** cell description (not part of guidance, but showing it here anyway) ** -->
		<c:if test="${not empty cell.scaffoldingCell.wizardPageDefinition.description}">
			<h3><osp:message key="label_description" /></h3>
			<div class="textPanel indnt1">
					<c:out value="${cell.scaffoldingCell.wizardPageDefinition.description}" escapeXml="false" />
			</div>
		</c:if>
		
		<c:set value="false" var="oneDisplayed" />
		<c:set value="0" var="i" />
	
		<%@ include file="viewCell_guidance.jspf"%>
		
		</div>
		</div>
	
	</c:if> 


<table class="matrixCellList" cellpadding="0" cellspacing="0" border="0">
	<tr>
		<th colspan="2" width="40%"><osp:message key="evidence_head" /></th>
		<th><div class="itemActionHeader"><osp:message key="table_header_actions" /></div></th>
		<th><osp:message key="table_header_originating_site" /></th>
		<th><osp:message key="table_header_createdBy" /></th>
		<th><osp:message key="table_header_modified" /></th>
	</tr>
	
	<%@ include file="viewCell_forms.jspf"%>
	
	<%@ include file="viewCell_resources.jspf"%>
	
	<%@ include file="viewCell_linkedArtifacts.jspf"%>
	
	<%@ include file="viewCell_attachedAssignments.jspf"%>
 
	<%@ include file="viewCell_reflection.jspf"%>

	<%@ include file="viewCell_feedback.jspf"%>

	<%@ include file="viewCell_evaluation.jspf"%>

</table>


<c:if
	test="${taggable}">
	<%@ include file="tagLists.jspf"%>
</c:if>

<div class="act"><c:if test="${sequential == 'true'}">
	<c:if test="${currentStep < (totalSteps)}">
		<!-- this is included because evaluating a seq wizard the user can browse all the pages -->
		<input type="submit" name="_next" class="TB_hideControl"
			value="<c:out value="${msgs.button_continue}"/>" accesskey="s" />
	</c:if>

	<c:if test="${isEvaluation != 'true'}">
		<c:if test="${currentStep != 1}">
			<input type="submit" name="_back" class="TB_hideControl"
				value="<c:out value="${msgs.button_back}"/>" accesskey="b" />
		</c:if>
		<input type="submit" name="matrix" class="TB_hideControl"
			value="<c:out value="${msgs.button_finish}"/>" />
			
		<!-- 
	   <input type="submit" name="cancel" value="<c:out value="${msgs.button_cancel}"/>"/>
	   -->
	</c:if>
</c:if> 



<c:if test="${(cell.status == 'READY' or cell.status == 'RETURNED') and readOnlyMatrix != 'true'}">

	<c:if test="${canReflect == 'true'}">
		<span class="act" style="margin:0">
			<c:choose>
				<c:when test="${isWizard !='true'}">
					<c:if test="${((cell.scaffoldingCell.evaluationDevice != null && !cell.scaffoldingCell.wizardPageDefinition.defaultEvaluationForm) 
									|| (cell.scaffoldingCell.scaffolding.evaluationDevice != null && cell.scaffoldingCell.wizardPageDefinition.defaultEvaluationForm))}">
					<input type="submit" name="submit" class="active TB_hideControl"
						value="<osp:message key='submit_cell_for_evaluation'/>"
						<c:if test="${sequential == 'true' && currentStep < (totalSteps)}">
							  onclick="document.form._next=true"
						   </c:if>
					/>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:if test="${cell.scaffoldingCell.evaluationDevice != null}">
				
						<input type="submit" name="submit" class="active TB_hideControl"
							value="<osp:message key='submit_wpage_for_evaluation'/>"
							<c:if test="${sequential == 'true' && currentStep < (totalSteps)}">
								  onclick="document.form._next=true"
							   </c:if>
						/>
					</c:if>
				</c:otherwise>
			</c:choose>	
				   
		<c:if test="${sequential == 'true'}">
			<c:if test="${currentStep < (totalSteps)}">
				<input type="hidden" name="_next" id="_next" />
			</c:if>
			<c:if test="${currentStep == (totalSteps)}">
				<input type="hidden" name="_last" id="_last" value="true" />
			</c:if>
		</c:if>
	</c:if>
	<%-- TODO: this seems very confusing - being in the last step is no indication that I am done - I may want to go through again and edit unsubmitted pages.--%>
	<c:if
		test="${canReflect == 'true' && currentStep == (totalSteps) && sequential == 'true' && evaluationItem != '' && evaluationItem != null}">
		<input type="submit" name="submitWizard" class="TB_hideControl"
			value="<osp:message key="submit_wizard_for_evaluation"/>" />
	</c:if>
	</span>
</c:if> 



	<c:if test="${isMatrix == 'true' and (cell.scaffoldingCell.scaffolding.allowRequestFeedback && cell.scaffoldingCell.wizardPageDefinition.defaultReviewers ||
											cell.scaffoldingCell.wizardPageDefinition.allowRequestFeedback && !cell.scaffoldingCell.wizardPageDefinition.defaultReviewers) &&
												cell.wizardPage.owner.id == currentUser && 
												((cell.scaffoldingCell.reviewDevice != null && !cell.scaffoldingCell.wizardPageDefinition.defaultFeedbackForm) 
												|| (cell.scaffoldingCell.scaffolding.reviewDevice != null && cell.scaffoldingCell.wizardPageDefinition.defaultFeedbackForm))}">
		<input type="submit" name="inviteFeedback" class="active TB_hideControl"
				value="<c:out value="${msgs.share_collection}"/>"/>
	</c:if> 
<c:if test="${hasAnyReviewers}">
	<c:if test="${isMatrix == 'true' and (!cell.scaffoldingCell.scaffolding.allowRequestFeedback && cell.scaffoldingCell.wizardPageDefinition.defaultReviewers ||
										!cell.scaffoldingCell.wizardPageDefinition.allowRequestFeedback && !cell.scaffoldingCell.wizardPageDefinition.defaultReviewers) &&
											cell.wizardPage.owner.id == currentUser && 
											((cell.scaffoldingCell.reviewDevice != null && !cell.scaffoldingCell.wizardPageDefinition.defaultFeedbackForm) 
											|| (cell.scaffoldingCell.scaffolding.reviewDevice != null && cell.scaffoldingCell.wizardPageDefinition.defaultFeedbackForm))}">
		<input type="submit" name="submitForReview" class="active TB_hideControl"
			value="<c:out value="${msgs.share_collection}"/>" />
	</c:if>
</c:if>

<c:if test="${isEvaluation == 'true'}">
	<input type="submit" name="matrix" class="active TB_hideControl"
		value="<c:out value="${msgs.button_back_to_evaluation}"/>" accesskey="x" />
</c:if> 
<c:if test="${sequential != 'true' && isEvaluation != 'true'}">
	<c:if test="${isWizard == 'true'}">
		<input type="submit" name="matrix" class="active TB_hideControl"
			value="<c:out value="${msgs.button_back_to_wizard}"/>" accesskey="x" />
	</c:if>
	<!-- Display back button only if this is the users matrix, or the user has accessUserList permission
		This extra check is based on review access through a link and not through the matrix.  This avoids
		giving reviewers more access than allowed
	 -->
	<c:if test="${isMatrix == 'true' && (scaffoldingCan.accessUserList || cell.wizardPage.owner.id == currentUser)}">
		<input type="submit" name="matrix" class="active TB_hideControl"
			value="<c:out value="${msgs.button_back_to_matrix}"/>" accesskey="x" />
	</c:if>
</c:if>

	
</div>



</form>
</c:if>

<c:if test="${!matrixCanViewCell}">
	<c:if test="${isMatrix == 'true'}">
		<br>
		<c:out value="${msgs.no_permission}"/>	
	</c:if>
	<c:if test="${(isWizard == 'true' && !(isWizardOwner || (canOperateWizard && (wizardCan.evaluate || wizardCan.review))))}">
		<br>
		<c:out value="${msgs.no_permission}"/>
	</c:if>
</c:if>
