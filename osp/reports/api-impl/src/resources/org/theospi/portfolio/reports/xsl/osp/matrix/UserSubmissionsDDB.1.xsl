<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
<xsl:template match="/">

    <div>
    <h3>Cells Pending Evaluation</h3>
    
    Matrix: <xsl:value-of select="//datarow[1]/element[@colName='title']/." />
   
    <div class="instruction">This report shows you the users who has open cells that can submit for evaluation when completed.</div>
    
	
	<table border="1" width="80%" align="center">
		<tr>
			<td>
					<xsl:attribute name = "width" > 
						<xsl:value-of select = "100 div (count(//group[@by='LEVEL_SEQUENCE']/datarow[not(element[@colName='LEVEL_SEQUENCE']/@isNull)]) + 1)" />%
					</xsl:attribute> 
			</td>
			
			<xsl:for-each select="//group[@by='LEVEL_SEQUENCE']/datarow[not(element[@colName='LEVEL_SEQUENCE']/@isNull)]">
				<xsl:sort select="element[@colName='LEVEL_SEQUENCE']" data-type="number" />
				<td width="50%">
					<xsl:attribute name = "width" > 
						<xsl:value-of select = "100 div (count(//group[@by='LEVEL_SEQUENCE']/datarow[not(element[@colName='LEVEL_SEQUENCE']/@isNull)]) + 1)" />%
					</xsl:attribute> 
					<xsl:value-of select="element[@colName='LEVEL_DESCRIPTION']"/>
				</td>
			</xsl:for-each>
		</tr>
		
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
				
					
					
					<xsl:for-each select="//data/datarow[element[@colName='LEVEL_SEQUENCE'] = $varLevel and 
													element[@colName='CRITERION_SEQUENCE'] = $varCriterion and
													(element[@colName='STATUS'] = 'PENDING' or element[@colName='STATUS'] = 'COMPLETE')]">
						
						<xsl:variable name = "varUser" select = "element[@colName='USERID']" />
						<xsl:if test="position()=1 or preceding-sibling::*[1]/element[@colName='USERID'] != $varUser" >
							<div class="indnt1">
							<xsl:if test="position() != 1">
								<br/>
							</xsl:if>
						
							 <xsl:if test="not(element[@colName='LAST_NAME']/@isNull = 'true')" >
								<xsl:value-of select="element[@colName='LAST_NAME']"/><xsl:if test="not(element[@colName='FIRST_NAME']/@isNull = 'true')" >, </xsl:if>
							 </xsl:if>
							 <xsl:if test="not(element[@colName='FIRST_NAME']/@isNull = 'true')" >
								<xsl:value-of select="element[@colName='FIRST_NAME']"/>
							 </xsl:if>
							 
							 <xsl:if test="not(element[@colName='LAST_NAME']/@isNull = 'true') " 
							 > (</xsl:if><xsl:value-of select="element[@colName='USERID']"
							 /><xsl:if test="not(element[@colName='LAST_NAME']/@isNull = 'true') " 
							 >)</xsl:if>
							</div>
						</xsl:if>
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
							
							
				   </xsl:for-each>
		   
				</td>
		   </xsl:for-each>




		 </tr>
   </xsl:for-each>
   </table>
    

    </div>

</xsl:template>
</xsl:stylesheet>