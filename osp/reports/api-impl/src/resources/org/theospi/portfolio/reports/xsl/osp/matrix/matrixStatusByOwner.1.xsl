<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    version="1.0">
    

   <xsl:template match="element">
      <xsl:param name="currentParent"/>
      <xsl:param name="rootNode"/>
      <xsl:variable name="name" select="@name"/>
      <xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]"/>
      <xsl:choose>
         <xsl:when test="children">
            <xsl:if test="$rootNode = 'true'">
               <xsl:call-template name="produce-fields">
                  <xsl:with-param name="currentSchemaNode" select="."/>
                  <xsl:with-param name="currentNode" select="$currentNode"/>
                  <xsl:with-param name="rootNode" select="$rootNode"/>
               </xsl:call-template>
            </xsl:if>
            <xsl:if test="$rootNode='false'">
               <tr>
                  <td>
                     <xsl:call-template name="produce-label">
                        <xsl:with-param name="currentSchemaNode" select="."/>
                     </xsl:call-template>
                  </td>
                  <td>
                     <xsl:call-template name="produce-fields">
                        <xsl:with-param name="currentSchemaNode" select="."/>
                        <xsl:with-param name="currentNode" select="$currentNode"/>
                        <xsl:with-param name="rootNode" select="$rootNode"/>
                     </xsl:call-template>
                  </td>
               </tr>
            </xsl:if>
         </xsl:when>
         <xsl:otherwise>
            <tr>
               <td>
                  <xsl:call-template name="produce-label">
                     <xsl:with-param name="currentSchemaNode" select="."/>
                  </xsl:call-template>
               </td>
               <td>
                  <xsl:value-of select="$currentNode"/>
               </td>
            </tr>
         </xsl:otherwise>
      </xsl:choose>
   </xsl:template>

   <xsl:template name="produce-label">
      <xsl:param name="currentSchemaNode"/>
      <label>
         <xsl:choose>
            <xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']">
               <xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']"/>
            </xsl:when>
            <xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']">
               <xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']"/>
            </xsl:when>
            <xsl:otherwise>
               <xsl:for-each select="$currentSchemaNode">
                  <xsl:value-of select="@name"/>
               </xsl:for-each>
            </xsl:otherwise>
         </xsl:choose>
      </label>
   </xsl:template>

   <xsl:template name="produce-fields">
      <xsl:param name="currentSchemaNode"/>
      <xsl:param name="currentNode"/>
      <xsl:param name="rootNode"/>

      <table class="itemSummary">
         <xsl:for-each select="$currentSchemaNode/children">
            <xsl:apply-templates select="@*|node()">
               <xsl:with-param name="currentParent" select="$currentNode"/>
               <xsl:with-param name="rootNode" select="'false'"/>
            </xsl:apply-templates>
         </xsl:for-each>
         <xsl:if test="$rootNode='true'">
            <xsl:call-template name="produce-metadata"/>
         </xsl:if>
      </table>
   </xsl:template>

   <xsl:template name="produce-metadata">
      <tr>
         <td>
            <label>Created</label>
         </td>
         <td>
            <xsl:value-of select="metaData/repositoryNode/created"/>
         </td>
      </tr>
      <tr>
         <td>
            <label>Modified</label>
         </td>
         <td>
            <xsl:value-of select="metaData/repositoryNode/modified"/>
         </td>
      </tr>
   </xsl:template>

<xsl:template match="artifact">
   
   <xsl:value-of select="metaData/displayName"/><br />

   <xsl:apply-templates select="schema/element">
      <xsl:with-param name="currentParent" select="structuredData"/>
      <xsl:with-param name="rootNode" select="'true'"/>
   </xsl:apply-templates>

</xsl:template>



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
                            <B><xsl:value-of select="$varUserName"/></B>
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






                            <td valign="top">
                            <xsl:for-each select="//data/datarow[
                                element[@colName='userId'] = $varUserName and
                                element[@colName='criterion_sequence'] = $varUserCriterion
                              ]">
                              <xsl:sort data-type="number" select="element[@colName='level_sequence']/." />
                                  <xsl:if test="not(preceding-sibling::datarow[1]/element[@colName='level_sequence'] =
                                            element[@colName='level_sequence']) and position() != 1" >
                     <xsl:value-of disable-output-escaping = "yes" select = "string('&lt;/td&gt;&lt;td valign=top&gt;')" />
                                     <xsl:value-of select="element[@colName='status']"/><BR />
                                  </xsl:if>
                                  <xsl:if test="position() = 1" >
                                     <xsl:value-of select="element[@colName='status']"/><BR />
                                  </xsl:if>
                     
                 <xsl:apply-templates select="element[@colName='evaluation_artifact']/artifact"  />

                            </xsl:for-each>
                            </td>



                         </tr>
                         
                   </xsl:for-each>

                         <xsl:if test="position() != last()" >
                            <tr class="exclude">
                               <td colspan="*"> <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text> </td>
                            </tr>
                         </xsl:if>

       </xsl:for-each>
    </table>

    </div>
</xsl:template>
</xsl:stylesheet>