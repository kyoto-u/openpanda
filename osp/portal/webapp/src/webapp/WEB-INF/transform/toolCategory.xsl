<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xhtml="http://www.w3.org/1999/xhtml"
   xmlns:osp="http://www.osportfolio.org/OspML">

   <xsl:variable name="layoutFile" select="/toolCategory/category/layoutFile" />
   <xsl:variable name="layout" select="document($layoutFile)"/>
   <xsl:variable name="content" select="/toolCategory"/>

   <xsl:template match="/">
       <xsl:apply-templates select="$layout/xhtml:html"/>
   </xsl:template>

   <xsl:template match="osp:tool">
      <xsl:variable name="currentToolId" select="@id" />
      <xsl:if test="$content/category/pages/page[@toolId=$currentToolId]">
         <xsl:apply-templates select="@*|node()" >
            <xsl:with-param name="currentTool" select="$content/category/pages/page[@toolId=$currentToolId]" />
         </xsl:apply-templates>
      </xsl:if>
   </xsl:template>

   <xsl:template match="osp:toolLink">
      <xsl:param name="currentTool" />
      <a target="_parent">
         <xsl:attribute name="href">
            <xsl:value-of select="$currentTool/url"/>
         </xsl:attribute>
         <xsl:apply-templates select="@*|node()" >
            <xsl:with-param name="currentTool" select="$currentTool" />
         </xsl:apply-templates>
      </a>
   </xsl:template>

   <xsl:template match="osp:site_role">
      <xsl:param name="currentTool" />
      <xsl:variable name="roleId" select="@role"/>
      <xsl:if test="$content/roles/role[@id=$roleId]">
         <xsl:apply-templates select="@*|node()" >
            <xsl:with-param name="currentTool" select="$currentTool" />
         </xsl:apply-templates>
      </xsl:if>
   </xsl:template>

   <!-- Identity transformation -->
   <xsl:template match="@*|*">
      <xsl:param name="currentTool" />
      <xsl:copy>
         <xsl:apply-templates select="@*|node()" >
            <xsl:with-param name="currentTool" select="$currentTool" />
         </xsl:apply-templates>
      </xsl:copy>
   </xsl:template>

</xsl:stylesheet>

