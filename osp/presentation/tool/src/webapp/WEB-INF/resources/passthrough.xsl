<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:template match="ospiPresentation">
	   <ospiPresentation>
	      	<xsl:copy-of select="*"></xsl:copy-of>
	   </ospiPresentation>
	</xsl:template>

</xsl:stylesheet>