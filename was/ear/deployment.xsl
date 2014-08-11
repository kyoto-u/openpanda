<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/" xmlns:pom="http://maven.apache.org/POM/4.0.0">
<appdeployment:Deployment xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:appdeployment="http://www.ibm.com/websphere/appserver/schemas/5.0/appdeployment.xmi" xmi:id="Deployment_{generate-id(.)}">
  <deployedObject xmi:type="appdeployment:ApplicationDeployment" xmi:id="ApplicationDeployment_{generate-id(.)}" startingWeight="10">
    <xsl:for-each select="pom:project/pom:dependencies/pom:dependency">
      <modules xmi:type="appdeployment:WebModuleDeployment" xmi:id="WebModuleDeployment_{generate-id(.)}" startingWeight="10000" uri="" classloaderMode="PARENT_LAST">
        <xsl:attribute name ="uri" ><xsl:value-of select ="pom:artifactId" />-<xsl:value-of select ="pom:version"/>.war</xsl:attribute> 
      </modules>
    </xsl:for-each>
    <classloader xmi:id="Classloader_{generate-id(.)}" mode="PARENT_LAST"/>
  </deployedObject>
</appdeployment:Deployment>
</xsl:template>

<!-- a global replace string function -->
 <xsl:template name="replace-string">
    <xsl:param name="text"/>
    <xsl:param name="from"/>
    <xsl:param name="to"/>
    <xsl:choose>
      <xsl:when test="contains($text, $from)">
	<xsl:variable name="before" select="substring-before($text, $from)"/>
	<xsl:variable name="after" select="substring-after($text, $from)"/>
	<xsl:variable name="prefix" select="concat($before, $to)"/>
	<xsl:value-of select="$before"/>
	<xsl:value-of select="$to"/>
        <xsl:call-template name="replace-string">
	  <xsl:with-param name="text" select="$after"/>
	  <xsl:with-param name="from" select="$from"/>
	  <xsl:with-param name="to" select="$to"/>
	</xsl:call-template>
      </xsl:when> 
      <xsl:otherwise>
        <xsl:value-of select="$text"/>  
      </xsl:otherwise>
    </xsl:choose>            
 </xsl:template>

</xsl:stylesheet>
