<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    version="1.0">
    
<xsl:template match="/">
    <div>
    <h5>Matrix Cell Status</h5>
    
    Run on: <xsl:value-of select="//runDate" />
    <br />
    
    Matrix: <xsl:value-of select="//datarow[1]/element[@name='title']/." />
    
    <div class="instruction">This data is only up to the date of the last data warehouse synchronization.</div>
    
    <h5>Users</h5>

       
       <table width="100%" class="lines">
       <xsl:for-each select="//group[@by = 'userId']/datarow">
          <xsl:sort select="element[@colName='userId']/." />

             <xsl:variable name = "varUserName" select = "element[@colName='userId']" />



                   <tr class="exclude">
                      <td>
                         <xsl:if test="element[@colName='random'] = 'true'" >
                            Anonymous User
                         </xsl:if>
                         <xsl:if test="element[@colName='random'] != 'true'" >
                            <xsl:value-of select="$varUserName"/>
                         </xsl:if>
                      </td>

                      <xsl:for-each select="//group[@by = 'level_sequence']/datarow[element[@colName='level_description'] != '']">
                         <xsl:sort data-type="number" select="element[@colName='level_sequence']" />
                         <td>
                            <xsl:value-of select="element[@colName='level_description']"/>
                         </td>
                      </xsl:for-each>
                   </tr>



                   <xsl:for-each select="//group[@by = 'criterion_sequence']/datarow[element[@colName='criterion_sequence'] != '']">
                      <xsl:sort data-type="number" select="element[@colName='criterion_sequence']" />

                      <xsl:variable name = "varUserCriterion" select = "element[@colName='criterion_sequence']" />
                     
                         <tr>
                            <td>
                               <xsl:value-of select="element[@colName='criterion_description']"/>
                            </td>






                            <xsl:for-each select="//data/datarow[
                                element[@colName='userId'] = $varUserName and
                                element[@colName='criterion_sequence'] = $varUserCriterion and
                                not(preceding-sibling::datarow[1]/element[@colName='level_sequence'] =
                                element[@colName='level_sequence'])
                              ]">
                              <xsl:sort data-type="number" select="element[@colName='level_sequence']/." />
                                   <td>
                                  <xsl:value-of select="element[@colName='status']"/> <BR />
                                  <xsl:if test="starts-with(element[@colName='evaluators'],'/site')" >
                                     <b><xsl:value-of select="substring-after(substring-after(element[@colName='evaluators'],'/site/'), '/')" /></b> (Role)<br />
                                  </xsl:if>
                                  <xsl:if test="not (starts-with(element[@colName='evaluators'],'/site'))" >
                                     <b><xsl:value-of select="element[@colName='evaluators']" /></b><br />
                                  </xsl:if>
                                  </td>
                               
                            </xsl:for-each>




                         </tr>
                         
                   </xsl:for-each>

                         <xsl:if test="position() != last()" >
                            <tr class="exclude">
                               <td colspan="*"> <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
                            </tr>
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
    
    <xsl:for-each select="//data/datarow">
        <tr>
            <xsl:for-each select="element">
                <td>
                    <xsl:value-of select="."/>
                </td>
            </xsl:for-each>
        </tr>
    </xsl:for-each>
    </table> -->

    </div>
</xsl:template>
</xsl:stylesheet>