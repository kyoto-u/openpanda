<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="2.0" xmlns="http://www.w3.org/1999/xhtml" xmlns:sakaifn="org.sakaiproject.metaobj.utils.xml.XsltFunctions" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:osp="http://www.osportfolio.org/OspML" xmlns:xs="http://www.w3.org/2001/XMLSchema">
	<!-- todo: final i18n pass -->
	<xsl:template name="complexElement-field">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="$currentSchemaNode/@name" />
		<xsl:variable name="maxOccurs" select="$currentSchemaNode/@maxOccurs" />
		<xsl:variable name="currentCount" select="count($currentParent/node()[$name=name()])" />
		<xsl:comment>
			<xsl:value-of select="$currentCount" />
		</xsl:comment>
		<xsl:call-template name="produce-inline">
			<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
		</xsl:call-template>
		<xsl:choose>
			<xsl:when test="$currentParent/node()[$name=name()]">
				<table class="listHier lines nolines" cellpadding="0" cellspacing="0" style="width:50%">
					<thead>
						<tr>
							<th scope="col">
                        <xsl:if test="($currentSchemaNode/children/element/xs:annotation/xs:documentation[@source='ospi.key']/text())">
                           <xsl:attribute name="colspan">
                              <xsl:value-of 
                                 select="count($currentSchemaNode/children/element/xs:annotation/xs:documentation[@source='ospi.key']/text())"/>
                           </xsl:attribute>
                        </xsl:if>
								<xsl:call-template name="produce-label">
									<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
								</xsl:call-template>
							</th>
							<th scope="col">
								<div style="float:right">
									<input type="submit" name="addButton" id="{$name}" alignment="center" onClick="this.form.childPath.value='{$name}';return true">
										<xsl:attribute name="value">
											<xsl:value-of select="sakaifn:getMessage('messages', 'button_addsubform')" />
										</xsl:attribute>
										<xsl:if test="$maxOccurs != -1 and $currentCount >= $maxOccurs">
											<xsl:attribute name="disabled">true</xsl:attribute>
										</xsl:if>
									</input>
								</div>
							</th>
						</tr>
                  <xsl:if test="($currentSchemaNode/children/element/xs:annotation/xs:documentation[@source='ospi.key']/text())">
                     <tr>
                     <xsl:for-each select="$currentSchemaNode/children/element[xs:annotation/xs:documentation[@source='ospi.key']/text()]">
                        <xsl:sort select="xs:annotation/xs:documentation[@source='ospi.key']/text()" data-type="number"/>
                        <th>
                           <xsl:call-template name="produce-label">
                              <xsl:with-param name="currentSchemaNode" select="." />
                           </xsl:call-template>
                        </th>
                     </xsl:for-each>
                        <th></th>
                     </tr>
                  </xsl:if>
					</thead>
					<tbody>
						<xsl:for-each select="$currentParent/node()[$name=name()]">
							<xsl:call-template name="subListRow">
								<xsl:with-param name="index" select="position() - 1" />
								<xsl:with-param name="fieldName" select="$name" />
								<xsl:with-param name="dataNode" select="." />
                        <xsl:with-param name="currentSchemaNode" select="$currentSchemaNode"/>
                     </xsl:call-template>
						</xsl:for-each>
					</tbody>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<table class="listHier lines nolines" cellpadding="0" cellspacing="0" style="width:50%">
					<thead>
						<tr>
							<th scope="col">
								<xsl:if test="$currentSchemaNode/@minOccurs='1'">
									<span class="reqStar">*</span>
								</xsl:if>
								<xsl:call-template name="produce-label">
									<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
								</xsl:call-template>
							</th>
							<th scope="col">
								<div style="float:right">
									<input type="submit" name="addButton" alignment="center" onClick="this.form.childPath.value='{$name}';return true">
										<xsl:attribute name="value">
											<xsl:value-of select="sakaifn:getMessage('messages', 'button_addsubform')" />
										</xsl:attribute>
										<xsl:if test="$maxOccurs != -1 and $currentCount >= $maxOccurs">
											<xsl:attribute name="disabled">true</xsl:attribute>
										</xsl:if>
									</input>
								</div>
							</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td />
							<td />
						</tr>
					</tbody>
				</table>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:if test="not(@maxOccurs='1')">
			<div id="{$name}-hidden-fields" class="skipthis">
				<input id="{$name}-count" type="text" value="1" />
				<input id="{$name}-max" type="text" value="{@maxOccurs}" />
			</div>
		</xsl:if>
	</xsl:template>
	<!--produce an input type text element -->
	<xsl:template name="shortText-field-empty-list">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="$currentSchemaNode/@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<!-- render some explanatory text associated with this input if documentation/@source=ospi.inlinedescription has a text node-->
		<xsl:call-template name="produce-inline">
			<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
		</xsl:call-template>
		<div id="{$name}-node">
			<xsl:attribute name="class">
            <xsl:call-template
               name="fieldClass"><xsl:with-param
               name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
               name="baseType" select="'shorttext'" /></xsl:call-template>
			</xsl:attribute>
			<xsl:if test="$currentSchemaNode/@minOccurs='1'">
				<span class="reqStar">*</span>
			</xsl:if>
			<xsl:call-template name="produce-label">
				<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
			</xsl:call-template>
			<input type="text" id="{$name}" name="{$name}" value="{$currentNode}">
				<xsl:call-template name="calculateRestrictions">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
				</xsl:call-template>
			</input>
			<!-- if @maxOccurs is not 1, then it is either a discreet number or unbounded, so add a link that will clone the node in the DOM
                -->
			<xsl:if test="not(@maxOccurs='1')">
				<a href="javascript:addItem('{$name}-node','{$name}');" class="addEl" id="{$name}-addlink">
					<xsl:attribute name="title">
						<xsl:value-of select="sakaifn:getMessage('messages', 'add_form_element')" />
					</xsl:attribute>
					<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
				</a>
				<div class="instruction" style="display:inline" id="{$name}-disp">
					<xsl:text> </xsl:text>
				</div>
			</xsl:if>
		</div>
		<!-- render hidden fields to aid the cloning -->
		<xsl:if test="not(@maxOccurs='1')">
			<div id="{$name}-hidden-fields" class="skipthis">
				<input id="{$name}-count" type="text" value="1" />
				<input id="{$name}-max" type="text" value="{@maxOccurs}" />
			</div>
		</xsl:if>
	</xsl:template>
	<xsl:template name="shortText-field">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="$currentSchemaNode/@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<xsl:variable name="count" select="count($currentParent//node()[$name=name()])" />
		<xsl:choose>
			<!-- if there are no values for this named element then the user did not fill them out while creating - so call the "create" template then -->
			<xsl:when test="$count='0'">
				<xsl:call-template name="shortText-field-empty-list">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
					<xsl:with-param name="currentParent" select="$currentParent" />
					<xsl:with-param name="rootNode" select="$rootNode" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<!-- render some inline text if documentation/@source=ospi.inlinedescription -->
				<xsl:call-template name="produce-inline">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<!-- cycle through all the data nodes that have this name, rendering input groups that  can be required, cloned, deleted, have a label, or inlined description -->
		<xsl:for-each select="$currentParent/node()[$name=name()]">
			<div>
				<!-- the id attribute will be used by javascript -->
				<xsl:attribute name="id">
					<xsl:choose>
						<xsl:when test="position()='1'">
							<xsl:value-of select="name()" />-node</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="name()" />-<xsl:value-of select="position()" />-node</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:attribute name="class">
               <xsl:call-template
                  name="fieldClass"><xsl:with-param
                  name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
                  name="baseType" select="'shorttext'" /></xsl:call-template>
				</xsl:attribute>
				<xsl:if test="$currentSchemaNode/@minOccurs='1'">
					<span class="reqStar">*</span>
				</xsl:if>
				<!-- call template that will produce the label in edit mode -->
				<xsl:call-template name="produce-label-edit">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
					<xsl:with-param name="sep">-</xsl:with-param>
					<xsl:with-param name="num" select="position()" />
				</xsl:call-template>
				<input type="text" name="{$name}" value="{.}">
					<xsl:attribute name="id">
						<xsl:value-of select="name()" />-<xsl:value-of select="position()" />
					</xsl:attribute>
					<xsl:call-template name="calculateRestrictions">
						<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
					</xsl:call-template>
				</input>
				<xsl:if test="not($currentSchemaNode/@maxOccurs='1')">
					<!-- calculate if this is an original node that can be cloned, an original node that has been cloned up to the max or a cloned node that can be deleted and render the appropriate links -->
					<xsl:choose>
						<xsl:when test="position() = '1'">
							<xsl:choose>
								<xsl:when test="$currentSchemaNode/@maxOccurs=$count">
									<a id="{$name}-addlink" class="addEl-inact">
										<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
									</a>
									<div class="instruction" style="display:inline" id="{$name}-disp">
										<xsl:text> </xsl:text>
									</div>
								</xsl:when>
								<xsl:otherwise>
									<a href="javascript:addItem('{$name}-node','{$name}');" class="addEl" id="{$name}-addlink">
										<xsl:attribute name="title">
											<xsl:value-of select="sakaifn:getMessage('messages', 'add_form_element')" />
										</xsl:attribute>
										<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
									</a>
									<div class="instruction" style="display:inline" id="{$name}-disp">
										<xsl:text> </xsl:text>
									</div>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<a href="javascript:removeItem('{$name}-{position()}-node','{$name}');" class="deleteEl" id="{$name}-addlink">
								<xsl:attribute name="title">
									<xsl:value-of select="sakaifn:getMessage('messages', 'delete_form_element')" />
								</xsl:attribute>
								<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
							</a>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</div>
		</xsl:for-each>
		<xsl:if test="not($currentSchemaNode/@maxOccurs='1')">
			<div id="{$name}-hidden-fields" class="skipthis">
				<input id="{$name}-count" type="text" value="{$count}" />
				<input id="{$name}-max" type="text" value="{$currentSchemaNode/@maxOccurs}" />
			</div>
		</xsl:if>
	</xsl:template>
	<!-- same in most respects as shorttext element except  1) cannot be cloned, 2) cannot be required (there is always a default) - so create and edit templates are one and the same.
		todo: required work
		-->
	<xsl:template name="select-field">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="$currentSchemaNode/@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<!-- this variable in com with maxOccurs' value controls the xhtml expression of this element (radiogroup, checkboxgroup, single select, multiple select) -->
		<xsl:variable name="htmldeterm">4</xsl:variable>
		<xsl:call-template name="produce-inline">
			<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
		</xsl:call-template>
		<div id="{$name}-node">
			<xsl:choose>
				<xsl:when test="@maxOccurs='1' and count($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration) &lt;= $htmldeterm">
					<!-- this will resolve as a radio group control -->
					<fieldset>
						<xsl:attribute name="class">
                     <xsl:call-template
                        name="fieldClass"><xsl:with-param
                        name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
                        name="baseType" select="'osp-radcheck'" /></xsl:call-template>
						</xsl:attribute>
						<legend>
							<xsl:if test="$currentSchemaNode/@minOccurs='1'">
								<span class="reqStar">*</span>
							</xsl:if>
							<xsl:call-template name="produce-label">
								<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
							</xsl:call-template>
						</legend>
						<xsl:for-each select="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration">
							<div class="checkbox">
								<input id="{$name}-{position()}" name="{$name}" value="{@value}" type="radio">
									<xsl:if test="$currentNode = @value">
										<xsl:attribute name="checked">checked</xsl:attribute>
									</xsl:if>
								</input>
								<label for="{$name}-{position()}">
									<xsl:choose>
                              <xsl:when test="./xs:annotation/xs:documentation[@source='sakai.label']">
                                 <xsl:value-of select="./xs:annotation/xs:documentation[@source='sakai.label']" />
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
                           </xsl:choose>
								</label>
							</div>
						</xsl:for-each>
					</fieldset>
				</xsl:when>
				<xsl:when test="@maxOccurs='1' and count($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration) &gt; $htmldeterm">
					<!-- this will resolve as a single select control-->
					<xsl:attribute name="class">
                  <xsl:call-template
                     name="fieldClass"><xsl:with-param
                     name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
                     name="baseType" select="'shorttext'" /></xsl:call-template>
					</xsl:attribute>
					<xsl:if test="$currentSchemaNode/@minOccurs='1'">
						<span class="reqStar">*</span>
					</xsl:if>
					<xsl:call-template name="produce-label">
						<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
					</xsl:call-template>
					<select id="{$name}" name="{$name}">
						<xsl:for-each select="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration">
							<option value="{@value}">
								<xsl:if test="$currentNode = @value">
									<xsl:attribute name="selected">selected</xsl:attribute>
								</xsl:if>
								<xsl:choose>
                              <xsl:when test="./xs:annotation/xs:documentation[@source='sakai.label']">
                                 <xsl:value-of select="./xs:annotation/xs:documentation[@source='sakai.label']" />
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
                           </xsl:choose>
							</option>
						</xsl:for-each>
					</select>
				</xsl:when>
				<xsl:when test="@maxOccurs !='1' and count($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration) &lt;= $htmldeterm">
					<!-- this will resolve as a checkbox group control -->
					<fieldset>
						<xsl:attribute name="class">
                     <xsl:call-template
                        name="fieldClass"><xsl:with-param
                        name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
                        name="baseType" select="'osp-radcheck'" /></xsl:call-template>
						</xsl:attribute>
						<legend>
							<xsl:if test="$currentSchemaNode/@minOccurs='1'">
								<span class="reqStar">*</span>
							</xsl:if>
							<xsl:call-template name="produce-label">
								<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
							</xsl:call-template>
						</legend>
						<xsl:for-each select="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration">
							<div class="checkbox">
								<input id="{$name}-{position()}" name="{$name}-{position()}" type="checkbox">
									<xsl:attribute name="onChange">
										(this.checked==true) ? document.getElementById('<xsl:value-of select="$name" />-<xsl:value-of select="position()" />-w').value='<xsl:value-of select="@value" />'
										: document.getElementById('<xsl:value-of select="$name" />-<xsl:value-of select="position()" />-w').value=''</xsl:attribute>
									<xsl:if test="$currentNode = @value">
										<xsl:attribute name="checked">checked</xsl:attribute>
									</xsl:if>
								</input>
								<label for="{$name}-{position()}">
									<xsl:value-of select="@value" />
								</label>
								<input type="hidden">
									<xsl:attribute name="name">
										<xsl:value-of select="$name" />
									</xsl:attribute>
									<xsl:attribute name="id">
										<xsl:value-of select="concat($name,'-',position(),'-w')" />
									</xsl:attribute>
									<xsl:attribute name="value">
										<xsl:if test="$currentNode = @value">
											<xsl:value-of select="@value" />
										</xsl:if>
									</xsl:attribute>
								</input>
							</div>
						</xsl:for-each>
					</fieldset>
				</xsl:when>
				<xsl:when test="@maxOccurs !='1' and count($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration) &gt; $htmldeterm">
					<!-- this will resolve as a multiple select control -->
					<xsl:attribute name="class">
                  <xsl:call-template
                     name="fieldClass"><xsl:with-param
                     name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
                     name="baseType" select="'shorttext'" /></xsl:call-template>
					</xsl:attribute>
					<xsl:if test="$currentSchemaNode/@minOccurs='1'">
						<span class="reqStar">*</span>
					</xsl:if>
					<xsl:call-template name="produce-label">
						<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
					</xsl:call-template>
					<select id="{$name}" name="{$name}" multiple="multiple">
						<xsl:attribute name="size">
							<xsl:choose>
								<!-- some crude calculations to determine the select visible row count -->
								<xsl:when test="count($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration) &lt; 10">5</xsl:when>
								<xsl:when test="count($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration) &lt; 20">10</xsl:when>
								<xsl:otherwise>15</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:for-each select="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:enumeration">
							<option value="{@value}">
								<xsl:if test="$currentNode = @value">
									<xsl:attribute name="selected">selected</xsl:attribute>
								</xsl:if>
								<xsl:choose>
                              <xsl:when test="./xs:annotation/xs:documentation[@source='sakai.label']">
                                 <xsl:value-of select="./xs:annotation/xs:documentation[@source='sakai.label']" />
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
                           </xsl:choose>
							</option>
						</xsl:for-each>
					</select>
				</xsl:when>
			</xsl:choose>
		</div>
	</xsl:template>
	<!-- same in most respects as shorttext element except  cannot be cloned -->
	<xsl:template name="richText-field">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="$currentSchemaNode/@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<xsl:call-template name="produce-inline">
			<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
		</xsl:call-template>
		<div id="{$name}-div">
			<xsl:attribute name="class">
            <xsl:call-template
               name="fieldClass"><xsl:with-param
               name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
               name="baseType" select="'longtext'" /></xsl:call-template>
			</xsl:attribute>
			<xsl:if test="$currentSchemaNode/@minOccurs='1'">
				<span class="reqStar">*</span>
			</xsl:if>
			<xsl:call-template name="produce-label">
				<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
				<xsl:with-param name="nodeType">longtext</xsl:with-param>
			</xsl:call-template>
			<table>
				<tr>
					<td>
						<textarea rows="30" cols="80" id="{$name}" name="{$name}">
							<xsl:choose>
								<xsl:when test="string($currentNode) = ''">
									<xsl:text disable-output-escaping="yes">&amp;nbsp;
               </xsl:text>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$currentNode" disable-output-escaping="yes" />
								</xsl:otherwise>
							</xsl:choose>
						</textarea>
						<xsl:if test="string($currentNode) = ''">
							<script language="JavaScript" type="text/javascript"> document.forms[0].<xsl:value-of select="$name" />.value="" </script>
						</xsl:if>
						<xsl:value-of select="sakaifn:getRichTextLaunch($name, $currentSchemaNode)" disable-output-escaping="yes" />
						<xsl:if test="@maxOccurs='-1'">
							<a href="javascript:addItem('{$name}parent');" class="addEl">
								<xsl:attribute name="title">
									<xsl:value-of select="sakaifn:getMessage('messages', 'add_form_element')" />
								</xsl:attribute>
								<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
							</a>
							<input type="hidden" id="{$name}parenthid" value="0" />
						</xsl:if>
					</td>
				</tr>
			</table>
		</div>
	</xsl:template>
	<!-- renders a textarea, similar in most respects to the shorttext element, except where noted below in comments -->
	<xsl:template name="longText-field-empty-list">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="thisname" />
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="$currentSchemaNode/@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<xsl:call-template name="produce-inline">
			<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
		</xsl:call-template>
		<div id="{$name}-node">
			<xsl:attribute name="class">
            <xsl:call-template
               name="fieldClass"><xsl:with-param
               name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
               name="baseType" select="'longtext'" /></xsl:call-template>
			</xsl:attribute>
			<xsl:if test="$currentSchemaNode/@minOccurs='1'">
				<span class="reqStar">*</span>
			</xsl:if>
			<!-- passing a nodeType param to the label producing template creates a label with the css class "block" so that it renders label and textarea in 2 separate lines -->
			<xsl:call-template name="produce-label">
				<xsl:with-param name="nodeType">longtext</xsl:with-param>
				<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
			</xsl:call-template>
			<!-- maxlength expressed as a title attribute as in shorttext, no default maxlength, however, and some rough calculations for rendered sized of the textarea based on the maxLength value -->
			<textarea id="{$name}" name="{$name}">
				<xsl:call-template name="calculateRestrictions">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
					<xsl:with-param name="nodeType" select="longtext" />
				</xsl:call-template>
				<xsl:choose>
					<xsl:when test="string($currentNode) = ''" />
					<xsl:otherwise>
						<xsl:value-of select="$currentNode" disable-output-escaping="yes" />
					</xsl:otherwise>
				</xsl:choose>
			</textarea>
			<xsl:if test="not(@maxOccurs='1')">
				<a href="javascript:addItem('{$name}-node','{$name}');" class="addEl" id="{$name}-addlink">
					<xsl:attribute name="title">
						<xsl:value-of select="sakaifn:getMessage('messages', 'add_form_element')" />
					</xsl:attribute>
					<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
				</a>
				<div class="instruction" style="display:inline" id="{$name}-disp">
					<xsl:text> </xsl:text>
				</div>
			</xsl:if>
		</div>
		<xsl:if test="not(@maxOccurs='1')">
			<div id="{$name}-hidden-fields" class="skipthis">
				<input id="{$name}-count" type="text" value="1" />
				<input id="{$name}-max" type="text" value="{@maxOccurs}" />
			</div>
		</xsl:if>
	</xsl:template>
	<!-- same considerations as in the shorttext edit template -->
	<xsl:template name="longText-field">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="$currentSchemaNode/@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<xsl:variable name="count" select="count($currentParent//node()[$name=name()])" />
		<xsl:choose>
			<xsl:when test="$count='0'">
				<xsl:call-template name="longText-field-empty-list">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
					<xsl:with-param name="currentParent" select="$currentParent" />
					<xsl:with-param name="rootNode" select="$rootNode" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="produce-inline">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<xsl:for-each select="$currentParent/node()[$name=name()]">
			<div>
				<xsl:attribute name="id">
					<xsl:choose>
						<xsl:when test="position()='1'">
							<xsl:value-of select="name()" />-node</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="name()" />-<xsl:value-of select="position()" />-node</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:attribute name="class">
               <xsl:call-template
                  name="fieldClass"><xsl:with-param
                  name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
                  name="baseType" select="'longtext'" /></xsl:call-template>
				</xsl:attribute>
				<xsl:if test="$currentSchemaNode/@minOccurs='1'">
					<span class="reqStar">*</span>
				</xsl:if>
				<xsl:call-template name="produce-label-edit">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
					<xsl:with-param name="sep">-</xsl:with-param>
					<xsl:with-param name="nodeType">longtext</xsl:with-param>
					<xsl:with-param name="num" select="position()" />
				</xsl:call-template>
				<textarea name="{$name}">
					<xsl:attribute name="id">
						<xsl:value-of select="name()" />-<xsl:value-of select="position()" />
					</xsl:attribute>
					<xsl:call-template name="calculateRestrictions">
						<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
						<xsl:with-param name="nodeType" select="longtext" />
					</xsl:call-template>
					<xsl:choose>
						<xsl:when test="string($currentNode) = ''" />
						<xsl:otherwise>
							<xsl:value-of select="." disable-output-escaping="yes" />
						</xsl:otherwise>
					</xsl:choose>
				</textarea>
				<xsl:if test="not($currentSchemaNode/@maxOccurs='1')">
					<xsl:choose>
						<xsl:when test="position() = '1'">
							<xsl:choose>
								<xsl:when test="$currentSchemaNode/@maxOccurs=$count">
									<a id="{$name}-addlink" class="addEl-inact">
										<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
									</a>
									<div class="instruction" style="display:inline" id="{$name}-disp">
										<xsl:text> </xsl:text>
									</div>
								</xsl:when>
								<xsl:otherwise>
									<a href="javascript:addItem('{$name}-node','{$name}');" class="addEl" id="{$name}-addlink">
										<xsl:attribute name="title">
											<xsl:value-of select="sakaifn:getMessage('messages', 'add_form_element')" />
										</xsl:attribute>
										<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
									</a>
									<div class="instruction" style="display:inline" id="{$name}-disp">
										<xsl:text> </xsl:text>
									</div>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<a href="javascript:removeItem('{$name}-{position()}-node','{$name}');" class="deleteEl" id="{$name}-addlink">
								<xsl:attribute name="title">
									<xsl:value-of select="sakaifn:getMessage('messages', 'delete_form_element')" />
								</xsl:attribute>
								<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
							</a>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</div>
		</xsl:for-each>
		<xsl:if test="not($currentSchemaNode/@maxOccurs='1')">
			<div id="{$name}-hidden-fields" class="skipthis">
				<input id="{$name}-count" type="text" value="1" />
				<input id="{$name}-max" type="text" value="{$currentSchemaNode/@maxOccurs}" />
			</div>
		</xsl:if>
	</xsl:template>
	<xsl:template name="checkBox-field">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="$currentSchemaNode/@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<xsl:call-template name="produce-inline">
			<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
		</xsl:call-template>
		<div id="{$name}parent">
			<xsl:attribute name="class">
            <xsl:call-template
               name="fieldClass"><xsl:with-param
               name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
               name="baseType" select="'checkbox indnt1'" /></xsl:call-template>
			</xsl:attribute>
			<xsl:if test="$currentSchemaNode/@minOccurs='1'">
				<span class="reqStar">*</span>
			</xsl:if>
			<xsl:call-template name="checkbox-widget">
				<xsl:with-param name="name" select="$name" />
				<xsl:with-param name="currentNode" select="$currentNode" />
			</xsl:call-template>
			<xsl:call-template name="produce-label">
				<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
				<xsl:with-param name="fieldName" select="concat($name, '_checkbox')" />
			</xsl:call-template>
			<xsl:if test="@maxOccurs='-1'">
				<a href="javascript:addItem('{$name}parent');" class="addEl">
					<xsl:attribute name="title">
						<xsl:value-of select="sakaifn:getMessage('messages', 'add_form_element')" />
					</xsl:attribute>
					<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
				</a>
				<input type="hidden" id="{$name}parenthid" value="0" />
			</xsl:if>
		</div>
	</xsl:template>
	<xsl:template name="checkbox-widget">
		<xsl:param name="name" />
		<xsl:param name="currentNode" />
		<input type="checkbox" id="{$name}_checkbox" name="{$name}_checkbox">
			<xsl:if test="$currentNode = 'true'">
				<xsl:attribute name="checked" />
			</xsl:if>
			<xsl:attribute name="onChange">form['<xsl:value-of select="$name" />'].value=this.checked </xsl:attribute>
		</input>
		<input type="hidden" name="{$name}" value="{$currentNode}" />
	</xsl:template>
	<!-- simple template, maxOccurs happens as a parameter used by the filepicker helper application -->
	<xsl:template name="fileHelper-field">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="$currentSchemaNode/@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<xsl:call-template name="produce-inline">
			<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
		</xsl:call-template>
		<div>
			<div id="{$name}parent">
				<xsl:attribute name="class">
               <xsl:call-template
                  name="fieldClass"><xsl:with-param
                  name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
                  name="baseType" select="'shorttext'" /></xsl:call-template>
				</xsl:attribute>
				<xsl:if test="$currentSchemaNode/@minOccurs='1'">
					<span class="reqStar">*</span>
				</xsl:if>
				<xsl:call-template name="produce-label">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
				</xsl:call-template>
            <xsl:variable name="fieldLabel"><xsl:choose><xsl:when 
               test="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']"><xsl:value-of 
               select="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']" /></xsl:when><xsl:when 
               test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']"><xsl:value-of 
               select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']" 
               /></xsl:when><xsl:otherwise><xsl:value-of select="$name" /></xsl:otherwise></xsl:choose></xsl:variable>
            <input id="{$name}" type="button" onclick="javascript:document.forms[0].childPath.value='{$name}';document.forms[0].childFieldLabel.value='{normalize-space($fieldLabel)}';document.forms[0].fileHelper.value='true';document.forms[0].onsubmit();document.forms[0].submit();">
					<xsl:choose>
						<xsl:when test="$currentParent/node()[$name=name()]">
							<xsl:attribute name="value">
								<xsl:value-of select="sakaifn:getMessage('messages', 'manage_attachments')" />
							</xsl:attribute>
							<xsl:attribute name="title">
								<xsl:value-of select="sakaifn:getMessage('messages', 'manage_attachments_title')" />
							</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="value">
								<xsl:value-of select="sakaifn:getMessage('messages', 'add_attachments')" />
							</xsl:attribute>
							<xsl:attribute name="title">
								<xsl:value-of select="sakaifn:getMessage('messages', 'add_attachments_title')" />
							</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</input>
				<!-- if there are attachments already, render these as a list -->
				<!--todo: mime type lookup to resolve the icons  -->
				<xsl:if test="$currentParent/node()[$name=name()]">
					<ul class="attachList labelindnt" style="clear:both;padding-top:.5em">
						<xsl:for-each select="$currentParent/node()[$name=name()]">
							<li>
								<img>
									<xsl:attribute name="src">
										<xsl:value-of select="sakaifn:getImageUrl(.)" />
									</xsl:attribute>
								</img>
								<input type="hidden" name="{$name}" value="{.}" />
								<a target="_blank">
									<xsl:attribute name="href">
										<xsl:value-of select="sakaifn:getReferenceUrl(.)" />
									</xsl:attribute>
									<xsl:value-of select="sakaifn:getReferenceName(.)" />
								</a>
							</li>
						</xsl:for-each>
					</ul>
				</xsl:if>
			</div>
		</div>
		<!-- todo: remove this test if not needed -->
		<xsl:if test="not(@maxOccurs='1')">
			<div id="{$name}-hidden-fields" class="skipthis">
				<input id="{$name}-count" type="text" value="1" />
				<input id="{$name}-max" type="text" value="{@maxOccurs}" />
			</div>
		</xsl:if>
	</xsl:template>
	<!-- similar to shorttext except as noted -->
	<xsl:template name="date-field-empty-list">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="$currentSchemaNode/@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<xsl:call-template name="produce-inline">
			<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
		</xsl:call-template>
		<div id="{$name}-node">
			<xsl:attribute name="class">
            <xsl:call-template
               name="fieldClass"><xsl:with-param
               name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
               name="baseType" select="'shorttext'" /></xsl:call-template>
			</xsl:attribute>
			<xsl:if test="$currentSchemaNode/@minOccurs='1'">
				<span class="reqStar">*</span>
			</xsl:if>
			<xsl:call-template name="produce-label">
				<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
			</xsl:call-template>
			<!-- calls a template that will produce a link to the calendar popup -->
			<xsl:call-template name="calendar-widget">
				<xsl:with-param name="schemaNode" select="$currentSchemaNode" />
				<xsl:with-param name="dataNode" select="$currentNode" />
			</xsl:call-template>
			<!-- if it can be cloned, render a link to do so -->
			<xsl:if test="not(@maxOccurs='1')">
				<a href="javascript:addItem('{$name}-node','{$name}');" class="addEl" id="{$name}-addlink">
					<xsl:attribute name="title">
						<xsl:value-of select="sakaifn:getMessage('messages', 'add_form_element')" />
					</xsl:attribute>
					<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
				</a>
				<div class="instruction" style="display:inline" id="{$name}-disp">
					<xsl:text> </xsl:text>
				</div>
			</xsl:if>
		</div>
		<xsl:if test="not(@maxOccurs='1')">
			<div id="{$name}-hidden-fields" class="skipthis">
				<input id="{$name}-count" type="text" value="1" />
				<input id="{$name}-max" type="text" value="{@maxOccurs}" />
			</div>
		</xsl:if>
	</xsl:template>
	<!-- as with the shorttext and longtext templates, the edit mode was different enough that it gets its own template -->
	<xsl:template name="date-field">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentParent" />
		<xsl:param name="rootNode" />
		<xsl:variable name="name" select="$currentSchemaNode/@name" />
		<xsl:variable name="currentNode" select="$currentParent/node()[$name=name()]" />
		<xsl:variable name="count" select="count($currentParent//node()[$name=name()])" />
		<xsl:choose>
			<!-- this element was not filled out on create, so does not exist in the data, so call original create template -->
			<xsl:when test="$count='0'">
				<xsl:call-template name="date-field-empty-list">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
					<xsl:with-param name="currentParent" select="$currentParent" />
					<xsl:with-param name="rootNode" select="$rootNode" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="produce-inline">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
		<!--calendar popup needs a unique id to call it - create the unique id here and used it with increments for each element in this cloned collection -->
		<xsl:variable name="fieldId" select="generate-id()" />
		<xsl:for-each select="$currentParent/node()[$name=name()]">
			<div>
				<xsl:attribute name="id">
					<xsl:choose>
						<xsl:when test="position()='1'">
							<xsl:value-of select="name()" />-node</xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="name()" />-<xsl:value-of select="position()" />-node</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
				<xsl:attribute name="class">
               <xsl:call-template
                  name="fieldClass"><xsl:with-param
                  name="schemaNode" select="$currentSchemaNode" /><xsl:with-param
                  name="baseType" select="'shorttext'" /></xsl:call-template>
				</xsl:attribute>
				<xsl:if test="$currentSchemaNode/@minOccurs='1'">
					<span class="reqStar">*</span>
				</xsl:if>
				<xsl:call-template name="produce-label-edit">
					<xsl:with-param name="currentSchemaNode" select="$currentSchemaNode" />
					<xsl:with-param name="sep">-</xsl:with-param>
					<xsl:with-param name="num" select="position()" />
				</xsl:call-template>
				<!-- as with the create version of this template - call a template that will render the click link for the calendar popup. Complications: need to pass the value, the number in the clone sequence, as well as the unique id -->
				<xsl:call-template name="calendar-widget-edit">
					<xsl:with-param name="schemaNode" select="$currentSchemaNode" />
					<xsl:with-param name="dataNode" select="$currentNode" />
					<xsl:with-param name="val" select="." />
					<xsl:with-param name="num" select="position()" />
					<xsl:with-param name="fieldIdclone" select="$fieldId" />
				</xsl:call-template>
				<!-- same as in shorttext, should really templatize this -->
				<xsl:if test="not($currentSchemaNode/@maxOccurs='1')">
					<xsl:choose>
						<xsl:when test="position() = '1'">
							<xsl:choose>
								<xsl:when test="$currentSchemaNode/@maxOccurs=$count">
									<a id="{$name}-addlink" class="addEl-inact">
										<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
									</a>
									<div class="instruction" style="display:inline" id="{$name}-disp">
										<xsl:text> </xsl:text>
									</div>
								</xsl:when>
								<xsl:otherwise>
									<a href="javascript:addItem('{$name}-node','{$name}');" class="addEl" id="{$name}-addlink">
										<xsl:attribute name="title">
											<xsl:value-of select="sakaifn:getMessage('messages', 'add_form_element')" />
										</xsl:attribute>
										<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
									</a>
									<div class="instruction" style="display:inline" id="{$name}-disp">
										<xsl:text> </xsl:text>
									</div>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<a href="javascript:removeItem('{$name}-{position()}-node','{$name}');" class="deleteEl" id="{$name}-addlink">
								<xsl:attribute name="title">
									<xsl:value-of select="sakaifn:getMessage('messages', 'delete_form_element')" />
								</xsl:attribute>
								<img src="/sakai-metaobj-tool/img/blank.gif" alt="" />
							</a>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</div>
		</xsl:for-each>
		<xsl:if test="not($currentSchemaNode/@maxOccurs='1')">
			<div id="{$name}-hidden-fields" class="skipthis">
				<input id="{$name}-count" type="text" value="{$count}" />
				<input id="{$name}-max" type="text" value="{$currentSchemaNode/@maxOccurs}" />
			</div>
		</xsl:if>
	</xsl:template>
	<xsl:template name="calendar-widget">
		<xsl:param name="schemaNode" />
		<xsl:param name="dataNode" />
		<xsl:variable name="year" select="sakaifn:dateField($dataNode, 1, 'date')" />
		<xsl:variable name="currentYear" select="sakaifn:dateField(sakaifn:currentDate(), 1, 'date')" />
		<xsl:variable name="month" select="sakaifn:dateField($dataNode, 2, 'date') + 1" />
		<xsl:variable name="day" select="sakaifn:dateField($dataNode, 5, 'date')" />
		<xsl:variable name="fieldId" select="generate-id()" />
		<input type="text" size="10" name="{$schemaNode/@name}.fullDate" id="{$schemaNode/@name}">
			<xsl:attribute name="value">
				<xsl:value-of select="sakaifn:formatDateWidget($dataNode)" />
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="sakaifn:getMessage('messages', 'date_format_hint')" />
			</xsl:attribute>
		</input>
		<!-- hidden field to hold the value of the unique id the calendar needs, and use it by increment to call the calendar in the context of any cloned nodes -->
		<input type="hidden" value="{$fieldId}" id="{$schemaNode/@name}-dateWId" />
		<!-- since there are two links to the right of the input  put some space between them to avoid confusion -->
		<xsl:text>&#xa0;</xsl:text>
		<img width="16" height="16" style="cursor:pointer;" border="0" src="/jsf-resource/inputDate/images/calendar.gif">
			<xsl:attribute name="alt">
				<xsl:value-of select="sakaifn:getMessage('messages', 'date_pick_alt')" />
			</xsl:attribute>
			<xsl:attribute name="onclick"><xsl:value-of select="sakaifn:getDateWidget($fieldId, $schemaNode/@name)" /></xsl:attribute>
		</img>
		<!-- since there are two links to the right of the input  put some space between them to avoid confusion -->
		<xsl:text>&#xa0;&#xa0;</xsl:text>
	</xsl:template>
	<xsl:template name="calendar-widget-edit">
		<xsl:param name="schemaNode" />
		<xsl:param name="dataNode" />
		<xsl:param name="num" />
		<xsl:param name="val" />
		<xsl:param name="fieldIdclone" />
		<xsl:variable name="year" select="sakaifn:dateField($dataNode, 1, 'date')" />
		<xsl:variable name="currentYear" select="sakaifn:dateField(sakaifn:currentDate(), 1, 'date')" />
		<xsl:variable name="month" select="sakaifn:dateField($dataNode, 2, 'date') + 1" />
		<xsl:variable name="day" select="sakaifn:dateField($dataNode, 5, 'date')" />
		<input type="text" size="10" name="{$schemaNode/@name}.fullDate">
			<xsl:attribute name="id">
				<xsl:value-of select="$schemaNode/@name" />
				<xsl:value-of select="position()" />
			</xsl:attribute>
			<xsl:attribute name="value">
				<xsl:value-of select="sakaifn:formatDateWidget($val)" />
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="sakaifn:getMessage('messages', 'date_format_hint')" />
			</xsl:attribute>
		</input>
		<input type="hidden" value="" id="{$schemaNode/@name}-dateWId" />
		<xsl:text>&#xa0;</xsl:text>
		<img width="16" height="16" style="cursor:pointer;" border="0" src="/jsf-resource/inputDate/images/calendar.gif">
			<xsl:attribute name="alt">
				<xsl:value-of select="sakaifn:getMessage('messages', 'date_pick_alt')" />
			</xsl:attribute>
			<xsl:attribute name="onclick"><xsl:value-of select="sakaifn:getDateWidget(concat($fieldIdclone, $num),
			   concat($schemaNode/@name, $num))" /></xsl:attribute>
		</img>
		<xsl:text>&#xa0;&#xa0;</xsl:text>
	</xsl:template>
	<xsl:template name="month-option">
		<xsl:param name="selectedMonth" />
		<xsl:param name="month" />
		<xsl:param name="monthName" />
		<option value="{$month}">
			<xsl:if test="$month = $selectedMonth">
				<xsl:attribute name="selected">true</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="sakaifn:getMessage('messages', $monthName)" />
		</option>
	</xsl:template>
	<xsl:template name="produce-label">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="nodeType" />
		<xsl:param name="sep" />
		<xsl:param name="num" />
		<xsl:param name="fieldName" />
		<label for="{@name}">
			<xsl:if test="$fieldName">
            <xsl:attribute name="for"><xsl:value-of select="$fieldName"/></xsl:attribute>
			</xsl:if>
			<xsl:if test="$nodeType='longtext'">
				<xsl:attribute name="class">block</xsl:attribute>
			</xsl:if>
			<!--output the ospi.descripion as a title in a link (using nicetitle)  -->
			<xsl:choose>
				<xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.description']/text()">
					<a class="nt">
						<xsl:attribute name="title">
							<xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.description']" />
						</xsl:attribute>
						<xsl:choose>
							<xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']">
								<xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']" />
							</xsl:when>
							<xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']">
								<xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:for-each select="$currentSchemaNode">
									<xsl:value-of select="@name" />
								</xsl:for-each>
							</xsl:otherwise>
						</xsl:choose>
					</a>
				</xsl:when>
				<xsl:otherwise>
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
				</xsl:otherwise>
			</xsl:choose>
		</label>
	</xsl:template>
	<xsl:template name="produce-label-edit">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="nodeType" />
		<xsl:param name="sep" />
		<xsl:param name="num" />
		<label for="{$currentSchemaNode/@name}{$sep}{$num}">
			<xsl:if test="$nodeType='longtext'">
				<xsl:attribute name="class">block</xsl:attribute>
			</xsl:if>
			<!--output the ospi.descripion as a title in a link (using nicetitle)  -->
			<xsl:choose>
				<xsl:when test="($currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.description']/text() and $num='1')">
					<a class="nt">
						<xsl:attribute name="title">
							<xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.description']" />
						</xsl:attribute>
						<xsl:choose>
							<xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']">
								<xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']" />
							</xsl:when>
							<xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']">
								<xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:for-each select="$currentSchemaNode">
									<xsl:value-of select="@name" />
								</xsl:for-each>
							</xsl:otherwise>
						</xsl:choose>
					</a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:choose>
						<xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']">
							<xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='sakai.label']" />
						</xsl:when>
						<xsl:when test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']">
							<xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.label']" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:for-each select="$currentSchemaNode">
								<xsl:value-of select="@name" />
							</xsl:for-each>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:otherwise>
			</xsl:choose>
		</label>
	</xsl:template>
	<!--output the documentation/@source=ospi.inlinedescripion as text block *above* the element -->
	<xsl:template name="produce-inline">
		<xsl:param name="currentSchemaNode" />
		<xsl:if test="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.inlinedescription']/text()">
			<p class="instruction clear">
				<xsl:value-of select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.inlinedescription']" />
			</p>
		</xsl:if>
	</xsl:template>
	<xsl:template name="produce-metadata">
		<xsl:if test="/formView/formData/artifact/metaData/repositoryNode/created">
			<table class="itemSummary">
				<tr>
					<th> Created </th>
					<td>
						<xsl:value-of select="/formView/formData/artifact/metaData/repositoryNode/created" />
					</td>
				</tr>
				<xsl:if test="/formView/formData/artifact/metaData/repositoryNode/modified">
					<tr>
						<th> Modified </th>
						<td>
							<xsl:value-of select="/formView/formData/artifact/metaData/repositoryNode/modified" />
						</td>
					</tr>
				</xsl:if>
			</table>
		</xsl:if>
	</xsl:template>
	<xsl:template name="produce-fields">
		<xsl:param name="currentSchemaNode" />
		<xsl:param name="currentNode" />
		<xsl:param name="rootNode" />
		<xsl:for-each select="$currentSchemaNode/children">
			<xsl:apply-templates select="@*|node()">
				<xsl:with-param name="currentParent" select="$currentNode" />
				<xsl:with-param name="rootNode" select="'false'" />
			</xsl:apply-templates>
		</xsl:for-each>
		<xsl:if test="$rootNode='true' and /formView/formData/artifact/metaData">
			<xsl:call-template name="produce-metadata" />
		</xsl:if>
	</xsl:template>
	<!-- for each subform filled out render a row in the table opened in complexElementField -->
	<xsl:template name="subListRow">
		<xsl:param name="index" />
		<xsl:param name="fieldName" />
		<xsl:param name="dataNode" />
		<xsl:param name="currentSchemaNode" />
		<tr>
         <xsl:choose>
         <xsl:when test="($currentSchemaNode/children/element/xs:annotation/xs:documentation[@source='ospi.key']/text())">
            <xsl:for-each select="$currentSchemaNode/children/element[xs:annotation/xs:documentation[@source='ospi.key']/text()]">
               <xsl:sort select="xs:annotation/xs:documentation[@source='ospi.key']/text()" data-type="number"/>
               <xsl:variable name="keyField" select="@name"/>
               <td>
                  <xsl:value-of select="$dataNode/node()[$keyField=name()]"/>
               </td>
            </xsl:for-each>
         </xsl:when>               
         <xsl:otherwise>
            <td>
               <xsl:choose>
               <xsl:when test="($currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.key']/text())">
                  <xsl:variable name="keyField" 
                                select="$currentSchemaNode/xs:annotation/xs:documentation[@source='ospi.key']/text()"/>
                  <xsl:value-of select="$dataNode/node()[$keyField=name()]"/>
               </xsl:when>               
               <xsl:otherwise>
                  <xsl:value-of select="$dataNode/*[1]" />
               </xsl:otherwise>
               </xsl:choose>
            </td>
         </xsl:otherwise>
         </xsl:choose>
			<td class="itemAction">
				<a>
					<xsl:attribute name="href">javascript:document.forms[0].childPath.value='<xsl:value-of select="$fieldName" />';document.forms[0].editButton.value='Edit';document.forms[0].removeButton.value='';document.forms[0].childIndex.value='<xsl:value-of select="$index" />';document.forms[0].onsubmit();document.forms[0].submit();</xsl:attribute> edit </a> | <a>
					<xsl:attribute name="href">javascript:document.forms[0].childPath.value='<xsl:value-of select="$fieldName" />';document.forms[0].removeButton.value='Remove';document.forms[0].editButton.value='';document.forms[0].childIndex.value='<xsl:value-of select="$index" />';document.forms[0].onsubmit();document.forms[0].submit();</xsl:attribute> remove </a>
			</td>
		</tr>
	</xsl:template>
	<!-- a crutch - since the date in datefields comes in on edit in a specific format, massage it here to render in any format. This template called with a "format" parameter which is not used but could be -->
	<xsl:template name="dateformat">
		<xsl:param name="date" />
		<xsl:param name="format" />
		<xsl:variable name="year" select="substring-before($date,'-')" />
		<xsl:variable name="rest" select="substring-after($date,'-')" />
		<xsl:variable name="month" select="substring-before($rest,'-')" />
		<xsl:variable name="day" select="substring-after($rest,'-')" />
		<xsl:value-of select="$month" />/<xsl:value-of select="$day" />/<xsl:value-of select="$year" />
	</xsl:template>
	<xsl:template name="fieldClass">
		<xsl:param name="schemaNode" />
		<xsl:param name="baseType" />
		<xsl:variable name="name" select="$schemaNode/@name" />
      <xsl:value-of select="$baseType"/> <xsl:if
         test="$schemaNode/@minOccurs = '1'"> required </xsl:if> <xsl:if
         test="//formView/errors/error[@field=$name]"> validFail </xsl:if>
	</xsl:template>
	<!-- all UI restriction driven hints here -->
	<xsl:template name="calculateRestrictions">
		<xsl:param name="currentSchemaNode" />
		<xsl:attribute name="size">20</xsl:attribute>
		<xsl:choose>
			<!-- textarea restrictions and UI hints -->
			<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value and $currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value &gt; 99">
				<xsl:attribute name="title">
					<xsl:value-of select="sakaifn:formatMessage('messages','max_chars',string($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value))" />
				</xsl:attribute>
				<xsl:choose>
					<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value &lt; 600">
						<xsl:attribute name="rows">3</xsl:attribute>
						<xsl:attribute name="cols">45</xsl:attribute>
					</xsl:when>
					<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value &lt; 800">
						<xsl:attribute name="rows">5</xsl:attribute>
						<xsl:attribute name="cols">45</xsl:attribute>
					</xsl:when>
					<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value &lt; 1000">
						<xsl:attribute name="rows">7</xsl:attribute>
						<xsl:attribute name="cols">45</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="rows">9</xsl:attribute>
						<xsl:attribute name="cols">45</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<!-- string restrictions and UI hints -->
			<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value">
				<xsl:attribute name="maxLength">
					<xsl:value-of select="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value" />
				</xsl:attribute>
				<xsl:attribute name="title">
					<xsl:value-of select="sakaifn:formatMessage('messages','max_chars',string($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value))" />
				</xsl:attribute>
				<xsl:attribute name="size">
					<xsl:choose>
						<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value &gt; 25"> 25 </xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:maxLength/@value" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:length/@value">
				<xsl:attribute name="maxLength">
					<xsl:value-of select="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:length/@value" />
				</xsl:attribute>
				<xsl:attribute name="title">
					<xsl:value-of select="sakaifn:formatMessage('messages','exactly',string($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:length/@value))" />
				</xsl:attribute>
				<xsl:attribute name="size">
					<xsl:choose>
						<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:length/@value &gt; 25"> 25 </xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:length/@value" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:minLength/@value">
				<xsl:attribute name="title">
					<xsl:value-of select="sakaifn:formatMessage('messages','at_least',string($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:string']/xs:minLength/@value))" />
				</xsl:attribute>
			</xsl:when>
			<!-- integer restrictions and UI hints -->
			<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:totalDigits/@value">
				<xsl:attribute name="maxLength">
					<xsl:value-of select="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:totalDigits/@value" />
				</xsl:attribute>
				<xsl:attribute name="title">
					<xsl:value-of select="sakaifn:formatMessage('messages','max_digs',string($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:totalDigits/@value))" />
				</xsl:attribute>
				<xsl:attribute name="size">
					<xsl:choose>
						<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:totalDigits/@value &gt; 20"> 20 </xsl:when>
						<xsl:otherwise>
							<xsl:value-of select="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:totalDigits/@value" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:maxInclusive/@value">
				<xsl:attribute name="title">
					<xsl:value-of select="sakaifn:formatMessage('messages','less_or_equal_to',string($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:maxInclusive/@value))" />
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:minInclusive/@value">
				<xsl:attribute name="title">
					<xsl:value-of select="sakaifn:formatMessage('messages','more_than_or_equal_to',string($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:minInclusive/@value))" />
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:minExclusive/@value">
				<xsl:attribute name="title">
					<xsl:value-of select="sakaifn:formatMessage('messages','less_than',string($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:minExclusive/@value))" />
				</xsl:attribute>
			</xsl:when>
			<xsl:when test="$currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:maxExclusive/@value">
				<xsl:attribute name="title">
					<xsl:value-of select="sakaifn:formatMessage('messages','more_than',string($currentSchemaNode/xs:simpleType/xs:restriction[@base='xs:integer']/xs:maxExclusive/@value))" />
				</xsl:attribute>
			</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="size">20</xsl:attribute>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
