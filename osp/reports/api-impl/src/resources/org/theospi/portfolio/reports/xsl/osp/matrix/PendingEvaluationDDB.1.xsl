<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
<xsl:template match="/">

    <div>
    <h3>Cells Pending Submission</h3>
    
    Matrix: <xsl:value-of select="//datarow[1]/element[@colName='title']/." />
   
    <div class="instruction">
    	This report shows you the users who have open cells that can submit for evaluation when completed.
    </div>
    
	
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
			  <td align="center">
				
					<xsl:attribute name = "bgcolor" > 
						<xsl:value-of select = "element[@colName='PENDINGCOLOR']" />
					</xsl:attribute> 
					
				   <xsl:for-each select="//data/datarow[(element[@colName='LEVEL_SEQUENCE'] = $varLevel or element[@colName='LEVEL_SEQUENCE']/@isNull) and 
														(element[@colName='CRITERION_SEQUENCE'] = $varCriterion or element[@colName='CRITERION_SEQUENCE']/@isNull) and
														(element[@colName='STATUS'] = 'PENDING')]">
														 
						
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
						
							<br/>
							
							
				   </xsl:for-each>
		   
				</td>
		   </xsl:for-each>




		 </tr>
   </xsl:for-each>
   </table>
    

    </div>

</xsl:template>
</xsl:stylesheet>