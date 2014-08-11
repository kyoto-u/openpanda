<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
<xsl:template match="/">
    <div>
    <h5>Matrix Cell Completion Status</h5>
    
    Matrix: <xsl:value-of select="//datarow[1]/element[@name='title']/." />
    
    <div class="instruction">This data is only up to the date of the last data warehouse synchronization.</div>
    
    <h5>Users</h5>
    <table width="100%">

       
       <xsl:for-each select="//datarow">
          <xsl:sort select="element[@name='userId']/." />
          <xsl:variable name = "varUserName" select = "element[@colName='userId']" />
          <xsl:value-of select="element[@colName='userId']"/>
          <xsl:if test="not(preceding-sibling::datarow[element[@colName='level_sequence'] = 
                        current()/element[@colName='level_sequence']])">
             <xsl:variable name = "varLevel" select = "element[@colName='level_sequence']" />
             <tr><td width="100%">



                <table width="100%" class="lines">
                   <tr class="exclude">
                      <td>
                         <xsl:value-of select="$varLevel"/>
                      </td>

                      <xsl:for-each select="//datarow[element[@colName='criterion_sequence'] = 0]">
                         <xsl:sort data-type="number" select="element[@name='level_sequence']" />
                         <xsl:if test="not(preceding-sibling::datarow[element[@colName='level_sequence'] = 
                        current()/element[@colName='level_sequence']])">
                            <td>
                               <xsl:value-of select="element[@colName='level_description']"/>
                            </td>
                         </xsl:if>
                      </xsl:for-each>
                   </tr>



                   <xsl:for-each select="//datarow[element[@colName='userId'] = $varUserName]">
                      <xsl:sort data-type="number" select="element[@name='criterion_sequence']" />

                      <xsl:variable name = "varUserCriterion" select = "element[@colName='criterion_sequence']" />
                     
                      <xsl:if test="not(preceding-sibling::datarow[position() = 1]/element[@colName='criterion_sequence'] = $varUserCriterion)">
                         <tr>
                            <td>
                               <xsl:value-of select="element[@colName='criterion_description']"/>
                            </td>






                   <xsl:for-each select="//datarow[element[@colName='userId'] = $varUserName and
                        element[@colName='criterion_sequence'] = current()/element[@colName='criterion_sequence']]">
                      <xsl:sort data-type="number" select="element[@name='level_sequence']/." />
            <td>
                   <!--      <xsl:if test="element[@colName='status']/. = 'READY'"><xsl:value-of select="'&lt;td '"/><xsl:value-of select="element[@name='readyColor']/."/><xsl:value-of select="'&gt;'"/></xsl:if>
<xsl:if test="element[@colName='status']/. = 'PENDING'"><xsl:value-of select="'&lt;td '"/><xsl:value-of select="element[@name='pendingColor']/."/><xsl:value-of select="'&gt;'"/></xsl:if>
<xsl:if test="element[@colName='status']/. = 'LOCKED'"><xsl:value-of select="'&lt;td '"/><xsl:value-of select="element[@name='lockedColor']/."/><xsl:value-of select="'&gt;'"/></xsl:if>
<xsl:if test="element[@colName='status']/. = 'COMPLETE'"><xsl:value-of select="'&lt;td '"/><xsl:value-of select="element[@name='completedColor']/."/><xsl:value-of select="'&gt;'"/></xsl:if>
-->
                <xsl:value-of select="element[@colName='status']"/>


                         </td>
                   </xsl:for-each>




                         </tr>
                      </xsl:if>
                   </xsl:for-each>

                </table>
                <p /><p />

             </td></tr>
           </xsl:if>
       </xsl:for-each>
    </table>

<!--
    
    <table>
    <tr>
    <xsl:for-each select="//column">
        <th>  
            <xsl:value-of select="@title"/>
              
        </th>
    </xsl:for-each>
    </tr>
    
    <xsl:for-each select="//datarow">
        <tr>
            <xsl:for-each select="element">
                <td>
                    <xsl:value-of select="."/>
                </td>
            </xsl:for-each>
        </tr>
    </xsl:for-each>
    </table>
-->
    </div>
</xsl:template>
</xsl:stylesheet>