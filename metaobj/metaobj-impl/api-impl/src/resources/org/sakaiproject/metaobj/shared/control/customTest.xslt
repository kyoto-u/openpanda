<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0"
   xmlns="http://www.w3.org/1999/xhtml"
   xmlns:sakaifn="org.sakaiproject.metaobj.utils.xml.XsltFunctions"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xhtml="http://www.w3.org/1999/xhtml"
   xmlns:osp="http://www.osportfolio.org/OspML"
   xmlns:xs="http://www.w3.org/2001/XMLSchema">

   <xsl:template name="checkBox-field-custom">
      <xsl:param name="currentSchemaNode"/>
      <xsl:param name="currentParent"/>
      <xsl:param name="rootNode"/>
      <xsl:variable name="name" select="$currentSchemaNode/@name"/>
      <xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]"/>
      <div class="shorttext indnt1">
         <xsl:call-template name="produce-label">
            <xsl:with-param name="currentSchemaNode" select="$currentSchemaNode"/>
         </xsl:call-template>
         <xsl:call-template name="checkbox-widget-custom">
            <xsl:with-param name="name" select="$name"/>
            <xsl:with-param name="currentNode" select="$currentNode"/>
         </xsl:call-template>
      </div>
   </xsl:template>

   <xsl:template name="checkbox-widget-custom">
      <xsl:param name="name"/>
      <xsl:param name="currentNode"/>
      custom
      <input type="checkbox" value="true">
         <xsl:attribute name="name"><xsl:value-of select="$name"/>_checkbox</xsl:attribute>
         <xsl:if test="$currentNode = 'true'">
            <xsl:attribute name="checked"/>
         </xsl:if>
         <xsl:attribute name="onChange">form['<xsl:value-of
            select="$name"/>'].value=this.checked</xsl:attribute>
      </input>
      <input type="hidden">
         <xsl:attribute name="name"><xsl:value-of select="$name"/></xsl:attribute>
         <xsl:attribute name="value"><xsl:value-of select="$currentNode"/></xsl:attribute>
      </input>
   </xsl:template>

</xsl:stylesheet>