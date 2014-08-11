<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
<xsl:template match="/">

    <div>
    <h3>Cells Pending Evaluation</h3>
    
    Matrix: <xsl:value-of select="//datarow[1]/element[@colName='title']/." />
   
    <div class="instruction">This report shows you the users who has open cells that can submit for evaluation when completed.</div>
    
	
	<table border="1" width="90%" align="center" cellpadding="3">
		<tr>
			<td>
					<xsl:attribute name = "width" > 
						<xsl:value-of select = "100 div (count(//group[@by='LEVEL_SEQUENCE']/datarow[not(element[@colName='LEVEL_SEQUENCE']/@isNull)]) + 1)" />%
					</xsl:attribute> 
			</td>
			
			<xsl:for-each select="//group[@by='LEVEL_SEQUENCE']/datarow[not(element[@colName='LEVEL_SEQUENCE']/@isNull)]">
		   	  <xsl:sort select="element[@colName='LEVEL_SEQUENCE']" data-type="number" />
				<td width="50%" align="center">
					<xsl:attribute name = "width" > 
						<xsl:value-of select = "100 div (count(//group[@by='LEVEL_SEQUENCE']/datarow[not(element[@colName='LEVEL_SEQUENCE']/@isNull)]) + 1)" />%
					</xsl:attribute> 
					<xsl:value-of select="element[@colName='LEVEL_DESCRIPTION']"/>
				</td>
			</xsl:for-each>
		</tr>
	
	<xsl:variable name = "numOfUsers" select = "count(//group[@by='USERID']/datarow)" />
		
	<xsl:for-each select="//group[@by='CRITERION_SEQUENCE']/datarow[not(element[@colName='CRITERION_SEQUENCE']/@isNull)]">
		<xsl:sort select="element[@colName='CRITERION_SEQUENCE']" data-type="number" />

		<xsl:variable name = "varCriterion" select = "element[@colName='CRITERION_SEQUENCE']" />
	 
		 <tr>
			<td>
				<xsl:value-of select="element[@colName='CRITERION_DESCRIPTION']"/>
			</td>






		   <xsl:for-each select="//group[@by='LEVEL_SEQUENCE']/datarow[not(element[@colName='LEVEL_SEQUENCE']/@isNull)]">
		   	  <xsl:sort select="element[@colName='LEVEL_SEQUENCE']" data-type="number" />
			  <xsl:variable name = "varLevel" select = "element[@colName='LEVEL_SEQUENCE']" />
			  <td align="left" valign="top"> 
			  		
					<xsl:variable name = "uniqueProficiencies" select = "//data/datarow[
														element[@colName='LEVEL_SEQUENCE'] = $varLevel and 
														element[@colName='CRITERION_SEQUENCE'] = $varCriterion and
														(element[@colName='STATUS'] = 'PENDING' or element[@colName='STATUS'] = 'COMPLETE') and
														(preceding-sibling::*[1]/element[@colName='USERID'] != element[@colName='USERID']
															or preceding-sibling::*[
																preceding-sibling::*[1]/element[@colName='USERID'] != element[@colName='USERID'] 
															]/element[@colName='USERART'] = element[@colName='USERART']
														) ]" />
					<!--
					<xsl:for-each select="$uniqueProficiencies">
						<xsl:value-of select="element[@colName='USERID']"/> - 
						<xsl:value-of select="element[@colName='LEVEL_SEQUENCE']"/> - 
						<xsl:value-of select="element[@colName='CRITERION_SEQUENCE']"/> - 
						<xsl:value-of select="element[@colName='STATUS']"/> - 
						<xsl:value-of select="element[@colName='ARTIFACTID']"/> - 
						<xsl:value-of select="element[@colName='EVALUATION_ARTIFACT']/artifact/structuredData/evaluationform/grade"/>
						<br/>
						<br/>
					</xsl:for-each> 
					-->
					<xsl:variable name = "exceedsExpectations" 
						select = "count($uniqueProficiencies[element[@colName='EVALUATION_ARTIFACT'
									]/artifact/structuredData/evaluationform/grade = 'Exceeds Expectations'])" />
					<xsl:variable name = "meetsExpectations" 
						select = "count($uniqueProficiencies[element[@colName='EVALUATION_ARTIFACT'
									]/artifact/structuredData/evaluationform/grade = 'Meets Expectations'])" />
					<xsl:variable name = "goodStart" 
						select = "count($uniqueProficiencies[element[@colName='EVALUATION_ARTIFACT'
									]/artifact/structuredData/evaluationform/grade = 'Good Start'])" />
					<xsl:variable name = "incomplete" 
						select = "count($uniqueProficiencies[element[@colName='EVALUATION_ARTIFACT'
									]/artifact/structuredData/evaluationform/grade = 'Incomplete'])" />
					
					<xsl:variable name = "pendingEvaluations" select = "count($uniqueProficiencies[element[@colName='STATUS'] = 'PENDING'])" />
					<xsl:variable name = "pendingSumbission" select = "count(//data/datarow[
													(element[@colName='LEVEL_SEQUENCE'] = $varLevel or element[@colName='LEVEL_SEQUENCE']/@isNull) and 
													(element[@colName='CRITERION_SEQUENCE'] = $varCriterion or element[@colName='CRITERION_SEQUENCE']/@isNull) and 
													(element[@colName='STATUS'] = 'READY' or element[@colName='STATUS']/@isNull)  ])" />
					
					Exceeds Expectation: <xsl:value-of select="$exceedsExpectations"/>/<xsl:value-of select="$numOfUsers"/> -- 
													<xsl:value-of select="floor(100 * $exceedsExpectations div $numOfUsers)"/>%<br />
					Meets Expectations: <xsl:value-of select="$meetsExpectations"/>/<xsl:value-of select="$numOfUsers"/> -- 
													<xsl:value-of select="floor(100 * $meetsExpectations div $numOfUsers)"/>%<br />
					Good Start: <xsl:value-of select="$goodStart"/>/<xsl:value-of select="$numOfUsers"/> -- 
													<xsl:value-of select="floor(100 * $goodStart div $numOfUsers)"/>%<br />
					Incomplete: <xsl:value-of select="$incomplete"/>/<xsl:value-of select="$numOfUsers"/> -- 
													<xsl:value-of select="floor(100 * $incomplete div $numOfUsers)"/>%<br />
					Pending Evaluation: <xsl:value-of select="$pendingEvaluations"/>/<xsl:value-of select="$numOfUsers"/> -- 
													<xsl:value-of select="floor(100 * $pendingEvaluations div $numOfUsers)"/>%<br />
					Pending Submission: <xsl:value-of select="$pendingSumbission"/>/<xsl:value-of select="$numOfUsers"/> -- 
													<xsl:value-of select="floor(100 * $pendingSumbission div $numOfUsers)"/>%<br />
					
					<br /><br />
					
					<xsl:for-each select="//data/datarow[element[@colName='LEVEL_SEQUENCE'] = $varLevel and 
													element[@colName='CRITERION_SEQUENCE'] = $varCriterion and
													(element[@colName='STATUS'] = 'PENDING' or element[@colName='STATUS'] = 'COMPLETE')]">
						
						<xsl:variable name = "varUser" select = "element[@colName='USERID']" />
						<xsl:variable name = "varResourceId" select = "element[@colName='RESOURCE_ID']" />
						
						<xsl:if test="position()=1 or preceding-sibling::*[1]/element[@colName='USERID'] != $varUser" >
						
						
						
							<xsl:if test="position() != 1">
								<br/>
							</xsl:if>
							<u>
							 <xsl:if test="not(element[@colName='LAST_NAME']/@isNull = 'true')" >
								<xsl:value-of select="element[@colName='LAST_NAME']"/><xsl:if test="not(element[@colName='FIRST_NAME']/@isNull = 'true')" >, </xsl:if>
							 </xsl:if>
							 <xsl:if test="not(element[@colName='FIRST_NAME']/@isNull = 'true')" >
								<xsl:value-of select="element[@colName='FIRST_NAME']"/>
							 </xsl:if>
							 
							 <xsl:if test="not(element[@colName='LAST_NAME']/@isNull = 'true') or not(element[@colName='FIRST_NAME']/@isNull = 'true') " 
							 > (</xsl:if><xsl:value-of select="element[@colName='USERID']"
							 /><xsl:if test="not(element[@colName='LAST_NAME']/@isNull = 'true') or not(element[@colName='FIRST_NAME']/@isNull = 'true')  " 
							 >)</xsl:if>
							
							</u>
								<br/>
								
								<div class="indnt2">
							 <xsl:for-each select="//data/datarow[element[@colName='LEVEL_SEQUENCE'] = $varLevel and 
													element[@colName='CRITERION_SEQUENCE'] = $varCriterion and
													element[@colName='USERID'] = $varUser and
													element[@colName='RESOURCE_ID'] = $varResourceId]">
								<xsl:value-of select=".//grade"/><br />
							 </xsl:for-each> 
							 </div>
						</xsl:if>
						<xsl:if test="position()=1 or preceding-sibling::*[1]/element[@colName='RESOURCE_ID'] != element[@colName='RESOURCE_ID'] " >
							
								<div class="indnt2">
								<a target="_blank">
								<xsl:attribute name = "href" >
									<xsl:value-of select="//accessUrl"/>/ospMatrix/<xsl:value-of 
									select="element[@colName='WORKSITEID']"/>/<xsl:value-of 
									select="element[@colName='WIZARD_PAGE_ID']"/>/content<xsl:value-of select="element[@colName='RESOURCE_ID']"/>
								</xsl:attribute>
								<xsl:value-of select="element[@colName='RESOURCE_DISPLAYNAME']"/>
							</a>
							</div>
							
						</xsl:if>
							
							
				   </xsl:for-each> 
				   
		   
				</td>
		   </xsl:for-each>




		 </tr>
   </xsl:for-each>
   </table>
    

    </div>

</xsl:template>
</xsl:stylesheet>