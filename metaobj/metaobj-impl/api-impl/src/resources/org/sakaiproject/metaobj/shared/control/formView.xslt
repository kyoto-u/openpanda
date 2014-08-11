<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:sakaifn="org.sakaiproject.metaobj.utils.xml.XsltFunctions" exclude-result-prefixes="xs sakaifn">
	<xsl:output method="xml" omit-xml-declaration="yes" encoding="UTF-8" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" />
	<xsl:param name="urlDecoration" />
	<xsl:template match="formView">
		<!--  note: equivalent to / -->
		<html lang="en" xml:lang="en" xmlns="http://www.w3.org/1999/xhtml">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<meta http-equiv="Content-Style-Type" content="text/css" />
				<title>
					<xsl:value-of select="formData/artifact/metaData/displayName" />
				</title>
				<script type="text/javascript" language="JavaScript" src="/library/js/headscripts.js">  // empty
					block </script>
				<script language="JavaScript">
					<![CDATA[
						// in case the parent iframe id has whitespace, trim so that IE can use it.
						function trim(s){
						if((s==null)||(typeof(s)!='string')||!s.length)return'';return s.replace(/^\s+/,'').replace(/\s+$/,'')}
					]]>
				</script>
				<link type="text/css" rel="stylesheet" media="all" href="/sakai-metaobj-tool/css/metaobj.css" />
				<xsl:apply-templates select="css" />
			</head>
			<body onload="(window.frameElement) ? setMainFrameHeight(trim(window.frameElement.id)):''" class="formDisplay">
				<div class="portletBodyForm" style="padding:1em 0">
					<xsl:apply-templates select="formData/artifact/schema/element">
						<xsl:with-param name="currentParent" select="formData/artifact/structuredData" />
						<xsl:with-param name="rootNode" select="'true'" />
					</xsl:apply-templates>
				</div>
			</body>
		</html>
	</xsl:template>
	<xsl:template match="element">
		<xsl:param name="currentParent" />
		<xsl:param name="nodetype" />
		<xsl:param name="rootNode" />
		<xsl:param name="thisname" />
		<xsl:param name="subform" />
		<xsl:variable name="name" select="@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<xsl:variable name="elementtype">
			<!-- find out element type -->
			<xsl:choose>
				<xsl:when test="xs:simpleType/xs:restriction/@base='xs:boolean'">boolean</xsl:when>
				<xsl:when test="xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value &lt; 99">shorttext</xsl:when>
				<xsl:when test="xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value &gt;= 99">
					<xsl:choose>
						<xsl:when test="xs:annotation/xs:documentation[@source='ospi.isRichText' or @source='sakai.isRichText']='true'">richtext</xsl:when>
						<xsl:otherwise>longtext</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="xs:simpleType/xs:restriction/@base='xs:date'">date</xsl:when>
				<xsl:when test="xs:simpleType/xs:restriction/@base='xs:anyURI' or @type='xs:anyURI'">file</xsl:when>
				<xsl:when test="xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration">select</xsl:when>
				<!-- fallback element type -->
				<xsl:otherwise>shorttext</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="children">
				<xsl:if test="$rootNode = 'true'">
					<xsl:call-template name="produce-label">
						<xsl:with-param name="currentSchemaNode" select="." />
						<xsl:with-param name="elementtype" select="$elementtype" />
						<xsl:with-param name="rootNode" select="$rootNode" />
						<xsl:with-param name="itemCount" select="count($currentParent/*[name()=$name])" />
					</xsl:call-template>
					<!-- has children and this is a top level node, not a subform element  -->
					<table class="top">
						<xsl:attribute name="summary"><xsl:value-of select="sakaifn:getMessage('messages', 'table_top_summary')" /></xsl:attribute>
						<xsl:call-template name="produce-fields">
							<xsl:with-param name="currentSchemaNode" select="." />
							<xsl:with-param name="currentNode" select="$currentNode" />
							<xsl:with-param name="rootNode" select="$rootNode" />
							<xsl:with-param name="thisname" select="name()" />
						</xsl:call-template>
					</table>
				</xsl:if>
				<xsl:if test="$rootNode='false'">
					<!-- a darn subform -->
					<tr>
						<td colspan="2" class="subformlabel">
							<xsl:call-template name="produce-label">
								<xsl:with-param name="currentSchemaNode" select="." />
								<xsl:with-param name="itemCount" select="count($currentParent/*[name()=$name])" />
							</xsl:call-template>
						</td>
					</tr>
					<tr>
						<td colspan="2">
							<table class="subformtable">
								<xsl:attribute name="summary"><xsl:value-of select="sakaifn:getMessage('messages', 'table_subform_summary')" /></xsl:attribute>
								<xsl:call-template name="elem">
									<xsl:with-param name="currentSchemaNode" select="." />
									<xsl:with-param name="elem" select="element" />
									<xsl:with-param name="data" select="$currentNode" />
									<xsl:with-param name="thisname" select="$thisname" />
									<xsl:with-param name="name" select="$name" />
									<xsl:with-param name="subform" select="'true'" />
								</xsl:call-template>
							</table>
						</td>
					</tr>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<!-- a plain old element - of which there are 2 types, big things to lay horizontally, short ones to lay vertically  -->
				<xsl:if test="($thisname='element' or $thisname=$name) and $subform !='true'">
					<tr>
						<td colspan="2">
							<table>
								<xsl:attribute name="summary"><xsl:value-of select="sakaifn:getMessage('messages', 'table_item_summary')" /></xsl:attribute>
								<xsl:choose>
									<xsl:when test="$elementtype='longtext' or $elementtype='richtext'">
										<xsl:attribute name="class">h</xsl:attribute>
										<tr>
											<td>
												<xsl:call-template name="produce-label">
													<xsl:with-param name="currentSchemaNode" select="." />
													<xsl:with-param name="itemCount" select="count($currentParent/*[name()=$name])" />
													<xsl:with-param name="elementtype" select="$elementtype" />
												</xsl:call-template>
											</td>
										</tr>
										<tr>
											<td>
												<xsl:call-template name="h">
													<xsl:with-param name="currentParent" select="$currentParent" />
													<xsl:with-param name="currentSchemaNode" select="." />
													<xsl:with-param name="currentNode" select="$currentNode" />
													<xsl:with-param name="rootNode" select="$rootNode" />
													<xsl:with-param name="thisname" select="name()" />
													<xsl:with-param name="name" select="$name" />
													<xsl:with-param name="elementtype" select="$elementtype" />
												</xsl:call-template>
											</td>
										</tr>
									</xsl:when>
									<xsl:otherwise>
										<xsl:attribute name="class">v</xsl:attribute>
										<tr>
											<th>
												<xsl:call-template name="produce-label">
													<xsl:with-param name="currentSchemaNode" select="." />
													<xsl:with-param name="elementtype" select="$elementtype" />
													<xsl:with-param name="itemCount" select="count($currentParent/*[name()=$name])" />
												</xsl:call-template>
											</th>
											<td>
												<xsl:call-template name="v">
													<xsl:with-param name="currentParent" select="$currentParent" />
													<xsl:with-param name="currentSchemaNode" select="." />
													<xsl:with-param name="currentNode" select="$currentNode" />
													<xsl:with-param name="rootNode" select="$rootNode" />
													<xsl:with-param name="thisname" select="name()" />
													<xsl:with-param name="name" select="$name" />
													<xsl:with-param name="elementtype" select="$elementtype" />
												</xsl:call-template>
											</td>
										</tr>
									</xsl:otherwise>
								</xsl:choose>
							</table>
						</td>
					</tr>
					<!--need to see if this is a subform-->
				</xsl:if>
				<xsl:if test="$subform='true'">
					<xsl:if test="@name = $thisname">
						<xsl:choose>
							<xsl:when test="$elementtype='longtext' or $elementtype='richtext'">
								<tr>
									<th>
										<xsl:call-template name="produce-label">
											<xsl:with-param name="currentSchemaNode" select="." />
											<xsl:with-param name="elementtype" select="$elementtype" />
											<xsl:with-param name="itemCount" select="count($currentParent/*[name()=$name])" />
										</xsl:call-template>
									</th>
								</tr>
								<tr class="longtextsubform">
									<td colspan="2">
										<xsl:call-template name="h">
											<xsl:with-param name="currentParent" select="$currentParent" />
										</xsl:call-template>
									</td>
								</tr>
							</xsl:when>
							<xsl:otherwise>
								<tr>
									<th>
										<xsl:call-template name="produce-label">
											<xsl:with-param name="elementtype" select="$elementtype" />
											<xsl:with-param name="currentSchemaNode" select="." />
											<xsl:with-param name="itemCount" select="count($currentParent/*[name()=$name])" />
										</xsl:call-template>
									</th>
									<td>
										<xsl:call-template name="v">
											<xsl:with-param name="currentParent" select="$currentParent" />
										</xsl:call-template>
									</td>
								</tr>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:if>
				</xsl:if>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- named templates  -->
	<xsl:template name="produce-label">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="elementtype" />
		<xsl:param name="rootNode" />
		<xsl:param name="itemCount" />
		<h4>
			<xsl:choose>
				<xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']">
					<xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']" />
				</xsl:when>
				<xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']">
					<xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:for-each select="$currentSchemaNode">
						<!-- todo: this is sort of a radical fallback -->
						<xsl:value-of select="@name" />
					</xsl:for-each>
				</xsl:otherwise>
			</xsl:choose>
		</h4>
		<xsl:if test="$rootNode='true'">
			<xsl:call-template name="produce-metadata" />
		</xsl:if>
		<xsl:if test="$itemCount &gt; 1">
			<div class="textPanelFooter">
				<xsl:value-of select="$itemCount" /><xsl:text> </xsl:text>
				<xsl:choose>
					<xsl:when test="$elementtype='select'">
						<xsl:value-of select="sakaifn:getMessage('messages', 'itemsselected')" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="sakaifn:getMessage('messages', 'items')" />
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>
		<!-- these - where to put these, do they even make sense in a form display? As a nicetitle hover? in the response but visible only on printing? 
		<xsl:if test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.inlinedescription']/text()">
			<p class="instruction clear"> (i) <xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.inlinedescription']" />
			</p>
		</xsl:if>
		<xsl:if test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.description']/text()">
			<p class="instruction clear"> (d) <xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.description']" />
			</p>
		</xsl:if>
		-->
	</xsl:template>
	<xsl:template name="produce-fields">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentNode" />
		<xsl:param name="rootNode" />
		<xsl:param name="thisname" />
		<xsl:param name="subform" />
		<xsl:for-each select="$currentSchemaNode/children">
			<xsl:apply-templates select="@*|node()">
				<xsl:with-param name="currentParent" select="$currentNode" />
				<xsl:with-param name="rootNode" select="'false'" />
				<xsl:with-param name="currentNode" select="$currentNode" />
				<xsl:with-param name="thisname" select="$thisname" />
				<xsl:with-param name="subform" select="$subform" />
			</xsl:apply-templates>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="produce-metadata">
		<div class="textPanelFooter">
			<strong>
				<xsl:value-of select="sakaifn:getMessage('messages', 'created')" />
			</strong>
			<xsl:text> </xsl:text>
			<xsl:value-of select="/formView/formData/artifact/metaData/repositoryNode/created" />
			<xsl:text> /  </xsl:text>
			<strong>
				<xsl:value-of select="sakaifn:getMessage('messages', 'modified')" />
			</strong>
			<xsl:text> </xsl:text>
			<xsl:value-of select="/formView/formData/artifact/metaData/repositoryNode/modified" />
		</div>
	</xsl:template>
	<xsl:template name="elem">
		<xsl:param name="elem" />
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="data" />
		<xsl:param name="thisname" />
		<xsl:param name="name" />
		<xsl:param name="subform" />
		<xsl:for-each select="$data/*">
			<xsl:call-template name="produce-fields">
				<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
				<xsl:with-param name="currentNode" select="." />
				<xsl:with-param name="rootNode" select="false" />
				<xsl:with-param name="thisname" select="name()" />
				<xsl:with-param name="subform" select="$subform" />
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>
	<!-- a template for each element type group-->
	<xsl:template name="v">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="currentNode" />
		<xsl:param name="rootNode" />
		<xsl:param name="elementtype" />
		<xsl:param name="thisname" />
		<xsl:param name="name" />
		<xsl:choose>
			<!-- a root node (not a node in a subform) -->
			<xsl:when test="$thisname='element'">
				<xsl:for-each select="$currentParent/*[name()=$name]">
					<xsl:choose>
						<xsl:when test="$elementtype='shorttext'">
								<xsl:choose>
									<xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.isRichText']='true'">
										<xsl:value-of disable-output-escaping="yes" select="." />
									</xsl:when>
									<xsl:otherwise>
										<div class="textPanel">
										<xsl:value-of select="." />
										</div>
									</xsl:otherwise>
								</xsl:choose>
						</xsl:when>
						<xsl:when test="$elementtype='date'">
							<div class="textPanel">
								<xsl:call-template name="dateformat">
									<xsl:with-param name="date" select="." />
									<xsl:with-param name="format">mm/dd/yy</xsl:with-param>
								</xsl:call-template>
							</div>
						</xsl:when>
						<xsl:when test="$elementtype='file'">
							<div class="textPanel">
								<img src="/library/image/sakai/attachments.gif" alt="attachment" />
								<xsl:text> </xsl:text>
								<a target="_blank">
									<xsl:choose>
										<xsl:when test="$urlDecoration != ''">
											<xsl:attribute name="href">
												<xsl:value-of select="sakaifn:getReferenceUrl(., $urlDecoration)" />
											</xsl:attribute>
											<xsl:value-of select="sakaifn:getReferenceName(., $urlDecoration)" />
										</xsl:when>
										<xsl:otherwise>
											<xsl:attribute name="href">
												<xsl:value-of select="sakaifn:getReferenceUrl(.)" />
											</xsl:attribute>
											<xsl:value-of select="sakaifn:getReferenceName(.)" />
										</xsl:otherwise>
									</xsl:choose>
								</a>
							</div>
						</xsl:when>
						<xsl:when test="$elementtype='boolean'">
							<xsl:if test=".='true'">
								<div class="textPanel">
									<img src="/library/image/sakai/checkon.gif" alt="checked " />
								</div>
							</xsl:if>
						</xsl:when>
						<xsl:otherwise> </xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
				<!-- selects need their own iteration -->
				<xsl:if test="$elementtype='select'">
					<ul class="selectList">
						<xsl:for-each select="$currentSchemaNode/xs:simpleType/xs:restriction/xs:enumeration">
                     <li>
                        <xsl:choose>
                           <xsl:when test="@value=$currentParent/node()[$name=name()]">
                              <img src="/library/image/sakai/checkon.gif" alt="checked" />
                           </xsl:when>
                           <xsl:otherwise>
                              <img src="/library/image/sakai/checkoff.gif" alt="unchecked" />
                           </xsl:otherwise>
                        </xsl:choose>
                        <xsl:choose>
                           <xsl:when test="./xs:annotation/xs:documentation[@source='sakai.label']">
                              <xsl:value-of
                                 select="./xs:annotation/xs:documentation[@source='sakai.label']" />
                           </xsl:when>
                           <xsl:when test="./xs:annotation/xs:documentation[@source='ospi.label']">
                              <xsl:value-of select="./xs:annotation/xs:documentation[@source='ospi.label']" />
                           </xsl:when>
                           <xsl:when test="./xs:annotation/xs:documentation">
                              <xsl:value-of select="./xs:annotation/xs:documentation" />
                           </xsl:when>
                           <xsl:otherwise>
                              <xsl:value-of select="@value" />
                           </xsl:otherwise>
                        </xsl:choose>. </li>
                  </xsl:for-each>
					</ul>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<!--- a subform node -->
				<div class="textPanel">
					<xsl:value-of select="$currentParent" />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="h">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="currentNode" />
		<xsl:param name="rootNode" />
		<xsl:param name="thisname" />
		<xsl:param name="name" />
		<xsl:choose>
			<!-- a root node (not a node in a subform) -->
			<xsl:when test="$thisname='element'">
				<xsl:for-each select="$currentParent/*[name()=$name]">
					<div class="textPanel">
						<xsl:value-of disable-output-escaping="yes" select="." />
					</div>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<!--- a subform node -->
				<div class="textPanel">
					<xsl:value-of disable-output-escaping="yes" select="$currentParent" />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="dateformat">
		<xsl:param name="date" />
		<xsl:param name="format" />
		<xsl:variable name="year" select="substring-before($date,'-')" />
		<xsl:variable name="rest" select="substring-after($date,'-')" />
		<xsl:variable name="month" select="substring-before($rest,'-')" />
		<xsl:variable name="day" select="substring-after($rest,'-')" />
		<xsl:value-of select="$month" />/<xsl:value-of select="$day" />/<xsl:value-of select="$year" />
	</xsl:template>
	<xsl:template match="css">
		<xsl:apply-templates />
	</xsl:template>
	<xsl:template match="uri">
		<link href="{.}" type="text/css" rel="stylesheet" media="all" />
	</xsl:template>
</xsl:stylesheet>
