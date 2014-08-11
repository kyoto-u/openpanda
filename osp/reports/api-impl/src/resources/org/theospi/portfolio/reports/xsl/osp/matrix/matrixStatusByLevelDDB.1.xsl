<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
<xsl:template match="/">
    <div>
    <h5>Matrix Cell Completion Status</h5>
    
    Matrix: <xsl:value-of select="//datarow[1]/element[@colName='TITLE']/." />
   
    <div class="instruction">This data is only up to the date of the last data warehouse synchronization.</div>
    
    <h5>Users</h5>
    <table>

       <xsl:for-each select="//group[@by='USERID']/datarow">
          <xsl:variable name = "varUserName" select = "element[@colName='USERID']/." />
          
             <xsl:variable name = "varLevel" select = "element[@colName='LEVEL_SEQUENCE']" />
             
                   <tr class="exclude">
                      <td>
                         <xsl:if test="//parameters/parameter[@name='anonymize'] = '1'" >
                            Anonymous User
                         </xsl:if>
                         <xsl:if test="//parameters/parameter[@name='anonymize'] = '0'" >
                            <B><xsl:value-of select="$varUserName"/></B>
                         </xsl:if>
                      </td>

                      <xsl:for-each select="//group[@by='LEVEL_SEQUENCE']/datarow">
                            <td>
                               <xsl:value-of select="element[@colName='LEVEL_DESCRIPTION']"/>
                            </td>
                      </xsl:for-each>
                   </tr>



                   <xsl:for-each select="//group[@by='CRITERION_SEQUENCE']/datarow">

                      <xsl:variable name = "varUserCriterion" select = "element[@colName='CRITERION_SEQUENCE']" />
                     
                         <tr>
                            <td>
                               <xsl:value-of select="element[@colName='CRITERION_DESCRIPTION']"/>
                            </td>






						   <xsl:for-each select="//group[@by='LEVEL_SEQUENCE']/datarow">
							  <xsl:variable name = "varUserLevel" select = "element[@colName='LEVEL_SEQUENCE']" />
								<td>
									<xsl:value-of select="//data/datarow[element[@colName='LEVEL_SEQUENCE'] = $varUserLevel and 
																		 element[@colName='CRITERION_SEQUENCE'] = $varUserCriterion and 
																		 element[@colName='USERID'] = $varUserName]/element[@colName='STATUS']"/>
								</td>
						   </xsl:for-each>




                         </tr>
                   </xsl:for-each>
           
           
       </xsl:for-each>
    </table>

    </div>
</xsl:template>
</xsl:stylesheet>