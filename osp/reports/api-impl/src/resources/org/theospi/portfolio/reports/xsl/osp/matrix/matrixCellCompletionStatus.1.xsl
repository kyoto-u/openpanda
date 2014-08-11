<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
<xsl:template match="/">
    <div>
    <h5>Matrix Cell Completion Status</h5>
    
    Run on: <xsl:value-of select="//runDate" />
    <br />
    
    Matrix: <xsl:value-of select="//datarow[1]/element['title']/." />
    
    <div class="instruction">This data is only up to the date of the last data warehouse synchronization.</div>
    
    <h5>Users</h5>
    <table width="100%" class="lines">

       <tr class="exclude">
          <td></td>
          <td>Ready</td>
          <td>Pending</td>
          <td>Complete</td>
          <td>Locked</td>
       </tr>
       
       <xsl:for-each select="//group[@by = 'userId']/datarow">
          <xsl:sort select="element[@colName='userId']" />
          
          <xsl:variable name = "varUserName" select = "element[@colName='userId']" />
          <tr>
                <td>
                    <xsl:value-of select="$varUserName"/>
                </td>
                <xsl:if test="count(//data/datarow[element[@colName='userId'] = $varUserName]) &gt; 1">
                <td>
                   <xsl:value-of select="count(//data/datarow[element[@colName='userId'] = $varUserName and element[@colName='status'] = 'READY'])"/>
                    / <xsl:value-of select="count(//data/datarow[element[@colName='userId'] = $varUserName])"/>
                </td>
                <td>
                   <xsl:value-of select="count(//data/datarow[element[@colName='userId'] = $varUserName and element[@colName='status'] = 'PENDING'])"/>
                    / <xsl:value-of select="count(//data/datarow[element[@colName='userId'] = $varUserName])"/>
                </td>
                <td>
                   <xsl:value-of select="count(//data/datarow[element[@colName='userId'] = $varUserName and element[@colName='status'] = 'COMPLETE'])"/>
                    / <xsl:value-of select="count(//data/datarow[element[@colName='userId'] = $varUserName])"/>
                </td>
                <td>
                   <xsl:value-of select="count(//data/datarow[element[@colName='userId'] = $varUserName and element[@colName='status'] = 'LOCKED'])"/>
                    / <xsl:value-of select="count(//data/datarow[element[@colName='userId'] = $varUserName])"/>
                </td>
                </xsl:if>
                <xsl:if test="count(//data/datarow[element[@colName='userId'] = $varUserName]) &lt;= 1">
                   <td colspan="4" align="center">
                      Not Started
                   </td>
                </xsl:if>

           </tr>
       </xsl:for-each>
    </table>


    </div>
</xsl:template>
</xsl:stylesheet>

