<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xhtml="http://www.w3.org/1999/xhtml"
   xmlns:osp="http://www.osportfolio.org/OspML">

<xsl:output method="html"/>
   
<xsl:variable name="layoutFile" select="/ospiPresentation/layout/artifact/fileArtifact/uri" />
<xsl:variable name="page" select="document($layoutFile)"/>
<xsl:variable name="presContent" select="/"/>

<xsl:template match="/">
    <xsl:apply-templates select="$page/xhtml:html"/>
</xsl:template>

<xsl:template match="osp:sequence">
   <xsl:variable name="currentId" select="@firstChild" />
   <xsl:variable name="sequence" select="." />
   
   <xsl:for-each select="$presContent/ospiPresentation/regions/region[@id=$currentId]">
      <xsl:variable name="currentSeqNo" select="@sequence" />
      <xsl:for-each select="$sequence">
         <xsl:copy>
            <xsl:apply-templates select="@*|node()">
               <xsl:with-param name="currentSeqNo" select="$currentSeqNo" />
            </xsl:apply-templates>
         </xsl:copy>
      </xsl:for-each>
   </xsl:for-each>
</xsl:template>

<xsl:template match="osp:region">
   <xsl:param name="currentSeqNo" />
   <xsl:variable name="currentId" select="@id" />
   
   <xsl:choose>
      <xsl:when test="$presContent/ospiPresentation/regions/region[@id=$currentId]/@sequence" >
         <xsl:apply-templates select="$presContent/ospiPresentation/regions/region[@id=$currentId and @sequence=$currentSeqNo]"/>
      </xsl:when>
      <xsl:otherwise>
         <xsl:apply-templates select="$presContent/ospiPresentation/regions/region[@id=$currentId]"/>
      </xsl:otherwise>
   </xsl:choose>
   
</xsl:template>


<!-- render the various region types -->
<!-- text -->
<xsl:template match="region[@type='text']">
   <xsl:value-of select="value" disable-output-escaping="no" />      
</xsl:template>
<!-- text -->

<!-- rich text -->
<xsl:template match="region[@type='richtext']">
   <xsl:value-of select="value" disable-output-escaping="yes" />     
</xsl:template>
<!-- rich text -->

<!-- link -->
<xsl:template match="region[@type='link']">
   <a>
      <xsl:attribute name="href">
         <xsl:value-of select="artifact/fileArtifact/uri" />
      </xsl:attribute>
      <xsl:attribute name="target">
         <xsl:value-of select="itemProperties/linkTarget" />
      </xsl:attribute>
      <xsl:value-of select="artifact/metaData/displayName" />     
   </a>  
</xsl:template>
<!-- link -->

<!-- form -->
<xsl:template match="region[@type='form']">
   <xsl:text>Form data here</xsl:text>
</xsl:template>
<!-- form -->

<!-- inline file -->
<xsl:template match="region[@type='inline']">
   <xsl:choose>
      <xsl:when test="artifact/metaData/repositoryNode/mimeType/primary = 'image'">
         <xsl:call-template name="inline-image" />
      </xsl:when>    
      <xsl:otherwise>
         <xsl:call-template name="inline-error" />
      </xsl:otherwise>
   </xsl:choose>
</xsl:template>

   <!-- various inline file type templates -->
   <!-- image -->
   <xsl:template name="inline-image">
      <img>
         <xsl:attribute name="src">
            <xsl:value-of select="artifact/fileArtifact/uri" />      
         </xsl:attribute>
         <xsl:attribute name="alt">
            <xsl:value-of select="artifact/metaData/displayName" />     
         </xsl:attribute>
         <xsl:attribute name="height">
            <xsl:value-of select="itemProperties/itemHeight" />
         </xsl:attribute>
         <xsl:attribute name="width">
            <xsl:value-of select="itemProperties/itemWidth" />
         </xsl:attribute>
      </img>      
   </xsl:template>
   
   <!-- error -->
   <xsl:template name="inline-error">
      <xsl:text>Unable to display file: </xsl:text>
      <a>
         <xsl:attribute name="href">
            <xsl:value-of select="artifact/fileArtifact/uri" />
         </xsl:attribute>
         <xsl:value-of select="artifact/metaData/displayName" />     
      </a>
      <xsl:text> inline</xsl:text>
   </xsl:template>
   <!-- inline file -->

   <xsl:template match="region">
      <xsl:text>Unknown region type</xsl:text>
   </xsl:template>

   <xsl:template name="apply-navigation">
      <xsl:if test="$presContent/ospiPresentation/navigation/nextPage |
                    $presContent/ospiPresentation/navigation/previousPage">
<div class="navIntraTool">
   <xsl:if test="$presContent/ospiPresentation/navigation/previousPage">
<a title="Previous">
<xsl:attribute name="href">
   <xsl:value-of
      select="$presContent/ospiPresentation/navigation/previousPage/artifact/fileArtifact/uri"/>
</xsl:attribute>
<xsl:value-of
   select="$presContent/ospiPresentation/navigation/previousPage/artifact/metaData/displayName"/>
</a>
   </xsl:if>
   <xsl:text>   </xsl:text>
   <xsl:if test="$presContent/ospiPresentation/navigation/nextPage">
<a title="Next">
<xsl:attribute name="href">
   <xsl:value-of
      select="$presContent/ospiPresentation/navigation/nextPage/artifact/fileArtifact/uri"/>
</xsl:attribute>
<xsl:value-of
   select="$presContent/ospiPresentation/navigation/nextPage/artifact/metaData/displayName"/>
</a>
   </xsl:if>
</div>
      </xsl:if>
   </xsl:template>
   

   <xsl:template name="apply-pageStyle">
      <xsl:if test="$presContent//ospiPresentation/pageStyle">
         <link rel="stylesheet" type="text/css" media="all">
            <xsl:attribute name="href">
               <xsl:value-of select="$presContent/ospiPresentation/pageStyle/artifact/fileArtifact/uri"/>
            </xsl:attribute>
         </link>
      </xsl:if>
   </xsl:template>   
   

   <!-- head tag -->
   <xsl:template match="xhtml:head">
      <xsl:copy>
         <xsl:call-template name="apply-pageStyle" />
      </xsl:copy>
   </xsl:template>   
   
   <!-- body tag -->
   <xsl:template match="xhtml:body">
      <xsl:param name="currentSeqNo" />
      <xsl:copy>
         <xsl:call-template name="apply-navigation"/>
         <xsl:apply-templates select="@*|node()" >
            <xsl:with-param name="currentSeqNo" select="$currentSeqNo" />
         </xsl:apply-templates>
      </xsl:copy>
   </xsl:template>

   <!-- Identity transformation -->
   <xsl:template match="@*|*">
      <xsl:param name="currentSeqNo" />
      <xsl:copy>
         <xsl:apply-templates select="@*|node()" >
            <xsl:with-param name="currentSeqNo" select="$currentSeqNo" />
         </xsl:apply-templates>
      </xsl:copy>
   </xsl:template>

</xsl:stylesheet>

