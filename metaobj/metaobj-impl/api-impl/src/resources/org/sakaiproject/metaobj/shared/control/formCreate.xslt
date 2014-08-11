<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:sakaifn="org.sakaiproject.metaobj.utils.xml.XsltFunctions" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:osp="http://www.osportfolio.org/OspML" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<!--xsl:template match="formView">
      <formView>
            <xsl:copy-of select="*"></xsl:copy-of>
      </formView>
   </xsl:template-->
	<xsl:param name="panelId" />
	<xsl:param name="subForm" />
	<xsl:param name="preview" />
	<xsl:param name="fromResources" />
	<xsl:param name="edit" />
	<xsl:output method="html" version="4.0" cdata-section-elements="" encoding="UTF-8" indent="yes" />
	<xsl:include href="/group/PortfolioAdmin/system/formFieldTemplate.xslt" />
	<xsl:template match="formView">
		<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<meta http-equiv="Content-Style-Type" content="text/css" />
				<title>
					<xsl:value-of select="formData/artifact/schema/element/xs:annotation/xs:documentation[@source='ospi.label']" />
				</title>
				<xsl:for-each select="css/uri">
               <xsl:sort select="@order" data-type="number" />
					<link type="text/css" rel="stylesheet" media="all">
						<xsl:attribute name="href">
							<xsl:value-of select="." />
						</xsl:attribute>
					</link>
				</xsl:for-each>
				<link type="text/css" rel="stylesheet" media="all" href="/sakai-metaobj-tool/css/metaobj.css" />
				<script type="text/javascript" language="JavaScript" src="/library/js/jquery.js"> // empty
					block </script>
				<script type="text/javascript" language="JavaScript" src="/library/js/headscripts.js"> // empty
					block </script>
				<script type="text/javascript" language="JavaScript" src="/sakai-metaobj-tool/js/nicetitle.js"> // empty
					block </script>
				<xsl:value-of select="sakaifn:getRichTextHead()" disable-output-escaping="yes"/>
            <script type="text/javascript" src="/library/calendar/js/calendar1.js"> // empty block </script>
            <script type="text/javascript" src="/library/calendar/js/calendar2.js"> // empty block </script>
			</head>
			<body>
				<xsl:if test="$panelId">
					<xsl:attribute name="onLoad">setMainFrameHeight('<xsl:value-of select="$panelId" />');setFocus(focus_path);</xsl:attribute>
				</xsl:if>
				<div class="portletBody">
					<input type="hidden" id="remove_item_msg">
						<xsl:attribute name="value">
							<xsl:value-of select="sakaifn:getMessage('messages', 'delete_form_element')" />
						</xsl:attribute>
					</input>
					<p class="instruction">
						<xsl:value-of disable-output-escaping="yes" select="formData/artifact/schema/instructions" />
					</p>
					<xsl:for-each select="/formView/errors/error">
						<div class="alertMessage">
							<xsl:value-of select="message" />
						</div>
					</xsl:for-each>
					<xsl:for-each select="/formView/success">
						<div class="success">
							<xsl:value-of select="sakaifn:getMessage('messages', @messageKey)" />
						</div>
					</xsl:for-each>
					<form method="post" onsubmit="a=1;">
						<xsl:if test="formData/artifact/schema/element/xs:annotation/xs:documentation[@source='ospi.description']">
							<h3>
								<xsl:value-of select="formData/artifact/schema/element/xs:annotation/xs:documentation[@source='ospi.description']" />
							</h3>
						</xsl:if>
						<xsl:if test="formData/artifact/schema/element/xs:annotation/xs:documentation[@source='ospi.inlinedescription']">
							<p class="instruction highlightPanel">
								<xsl:value-of select="formData/artifact/schema/element/xs:annotation/xs:documentation[@source='ospi.inlinedescription']" />
							</p>
						</xsl:if>
						<xsl:choose>
							<!-- todo: if this is a subform, display a good title -->
							<xsl:when test="$subForm = 'true'">
								<h4>
									<xsl:value-of select="formData/artifact/metaData/displayName" />
								</h4>
							</xsl:when>
							<xsl:when test="$fromResources = 'true' and $edit = 'true'">
								<h4>
									<xsl:value-of select="formData/artifact/metaData/displayName" />
								</h4>
								<input type="hidden" id="displayName" name="displayName" maxlength="1024">
									<xsl:attribute name="value">
										<xsl:value-of select="formData/artifact/metaData/displayName" />
									</xsl:attribute>
								</input>
							</xsl:when>
							<xsl:when test="$fromResources = 'true'">
								<input type="hidden" id="displayName" name="displayName" maxlength="1024" value="new form" />
							</xsl:when>
							<xsl:otherwise>
								<!-- the name of this entry, always required -->
								<div class="shorttext required">
									<span class="reqStar">*</span>
									<label for="displayName">
										<xsl:value-of select="sakaifn:getMessage('messages', 'display.name.label')" />
									</label>
									<input type="text" id="displayName" name="displayName" maxlength="1024">
										<xsl:attribute name="value">
											<xsl:value-of select="formData/artifact/metaData/displayName" />
										</xsl:attribute>
									</input>
								</div>
							</xsl:otherwise>
						</xsl:choose>
						<xsl:apply-templates select="formData/artifact/schema/element">
							<xsl:with-param name="currentParent" select="formData/artifact/structuredData" />
							<xsl:with-param name="rootNode" select="'true'" />
						</xsl:apply-templates>
                  <input type="hidden" name="childPath" value="" />
                  <input type="hidden" name="childFieldLabel" value="" />
						<input type="hidden" name="childIndex" value="" />
						<input type="hidden" name="fileHelper" value="" />
						<input type="hidden" name="editButton" value="" />
						<input type="hidden" name="removeButton" value="" />
						<div class="act">
							<xsl:choose>
								<xsl:when test="$subForm = 'true'">
									<input type="submit" name="updateNestedButton" class="active" accesskey="s">
										<xsl:attribute name="value">
											<xsl:value-of select="sakaifn:getMessage('messages', 'button_update')" />
										</xsl:attribute>
									</input>
									<input type="submit" name="cancelNestedButton" accesskey="x">
										<xsl:attribute name="value">
											<xsl:value-of select="sakaifn:getMessage('messages', 'button_cancel')" />
										</xsl:attribute>
									</input>
								</xsl:when>
								<xsl:when test="$preview = 'true'">
									<input type="submit" name="submitButton" class="active">
										<xsl:attribute name="value">
											<xsl:value-of select="sakaifn:getMessage('messages', 'button_validate')" />
										</xsl:attribute>
									</input>
									<input type="submit" name="cancel" accesskey="x">
										<xsl:attribute name="value">
											<xsl:value-of select="sakaifn:getMessage('messages', 'button_return')" />
										</xsl:attribute>
									</input>
								</xsl:when>
								<xsl:when test="$fromResources = 'true' and $edit != 'true'">
									<input type="submit" name="submitButton" class="active" accesskey="s">
										<xsl:attribute name="value">
											<xsl:value-of select="sakaifn:getMessage('messages', 'button_saveEditContinue')" />
										</xsl:attribute>
									</input>
									<input type="submit" name="backButton" accesskey="b">
										<xsl:attribute name="value">
											<xsl:value-of select="sakaifn:getMessage('messages', 'button_back')" />
										</xsl:attribute>
									</input>
									<input type="submit" name="cancel" accesskey="x">
										<xsl:attribute name="value">
											<xsl:value-of select="sakaifn:getMessage('messages', 'button_cancel')" />
										</xsl:attribute>
									</input>
								</xsl:when>
								<xsl:otherwise>
									<input type="submit" name="submitButton" class="active" accesskey="s">
										<xsl:attribute name="value">
											<xsl:value-of select="sakaifn:getMessage('messages', 'button_saveEdit')" />
										</xsl:attribute>
									</input>
									<input type="submit" name="cancel" accesskey="x">
										<xsl:attribute name="value">
											<xsl:value-of select="sakaifn:getMessage('messages', 'button_cancel')" />
										</xsl:attribute>
									</input>
								</xsl:otherwise>
							</xsl:choose>
						</div>
					</form>
				</div>
			</body>
		</html>
	</xsl:template>
	<!--
    sub form
   -->
	<xsl:template match="element[children]">
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<xsl:if test="$rootNode = 'true'">
			<xsl:call-template name="produce-fields">
				<xsl:with-param name="currentSchemaNode" select="." />
				<xsl:with-param name="currentNode" select="$currentNode" />
				<xsl:with-param name="rootNode" select="$rootNode" />
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="$rootNode='false'">
			<xsl:call-template name="complexElement-field">
				<xsl:with-param name="currentSchemaNode" select="." />
				<xsl:with-param name="currentParent" select="$currentParent" />
				<xsl:with-param name="rootNode" select="$rootNode" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!--
    date picker
   -->
	<xsl:template match="element[@type = 'xs:date']">
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
      <xsl:call-template name="date-field">
         <xsl:with-param name="currentSchemaNode" select="." />
         <xsl:with-param name="currentParent" select="$currentParent" />
         <xsl:with-param name="rootNode" select="$rootNode" />
      </xsl:call-template>
	</xsl:template>
	<!--
    file picker
   -->
	<xsl:template match="element[@type = 'xs:anyURI']">
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:call-template name="fileHelper-field">
			<xsl:with-param name="currentSchemaNode" select="." />
			<xsl:with-param name="currentParent" select="$currentParent" />
			<xsl:with-param name="rootNode" select="$rootNode" />
		</xsl:call-template>
	</xsl:template>
	<!--
    check box
   -->
	<xsl:template match="element[@type = 'xs:boolean']">
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:call-template name="checkBox-field">
			<xsl:with-param name="currentSchemaNode" select="." />
			<xsl:with-param name="currentParent" select="$currentParent" />
			<xsl:with-param name="rootNode" select="$rootNode" />
		</xsl:call-template>
	</xsl:template>
	<!--
    long text
   -->
	<xsl:template match="element[xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength[@value>99]]">
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
      <xsl:call-template name="longText-field">
         <xsl:with-param name="currentSchemaNode" select="." />
         <xsl:with-param name="currentParent" select="$currentParent" />
         <xsl:with-param name="rootNode" select="$rootNode" />
      </xsl:call-template>
	</xsl:template>
	<!--
    rich text
   -->
	<xsl:template match="element[xs:annotation/xs:documentation[@source='ospi.isRichText' or @source='sakai.isRichText']]">
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:call-template name="richText-field">
			<xsl:with-param name="currentSchemaNode" select="." />
			<xsl:with-param name="currentParent" select="$currentParent" />
			<xsl:with-param name="rootNode" select="$rootNode" />
		</xsl:call-template>
	</xsl:template>
	<!--
    select one or more from many (radio and checkbox groups, single and multiple selects) 
   -->
	<xsl:template match="element[xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration]">
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:call-template name="select-field">
			<xsl:with-param name="currentSchemaNode" select="." />
			<xsl:with-param name="currentParent" select="$currentParent" />
			<xsl:with-param name="rootNode" select="$rootNode" />
		</xsl:call-template>
	</xsl:template>
	<!--
    catch all
   -->
	<xsl:template match="element">
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
      <xsl:call-template name="shortText-field">
         <xsl:with-param name="currentSchemaNode" select="." />
         <xsl:with-param name="currentParent" select="$currentParent" />
         <xsl:with-param name="rootNode" select="$rootNode" />
      </xsl:call-template>
	</xsl:template>
</xsl:stylesheet>
