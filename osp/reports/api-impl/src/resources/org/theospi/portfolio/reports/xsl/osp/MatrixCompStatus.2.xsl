<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="reportResult">
<style>
.searchCriteriaTable {
	width: 100%;
	border-top: 1px solid #CCCCCC;
}
.searchCriteriaLabel {
	font: bold x-small Arial, Helvetica, sans-serif;
	color: #666666;
	padding: 3px;
	border-bottom: 1px solid #CCCCCC;
	white-space: nowrap;
}
.searchCriteriaData {
	width: 90%;
	font: normal x-small Arial, Helvetica, sans-serif;
	color: #666666;
	padding: 3px;
	border-left: 1px solid #CCCCCC;
	border-bottom: 1px solid #CCCCCC;
}
.legend1 {
	font: bold x-small Arial, Helvetica, sans-serif;
	color: #666666;
}
.legend2 {
	font-weight: normal;
}
.reportTable {
	border-top: 1px solid #CCCCCC;
	border-left: 1px solid #CCCCCC;
	margin-bottom: 25px;
}
.reportTableHeader1 {
	background-color: #F4F4F4;
	font: bold x-small Arial, Helvetica, sans-serif;
	color: #666666;
	border-right: 1px solid #CCCCCC;
	border-bottom: 1px solid #CCCCCC;
	vertical-align: top;
}
.reportTableHeader2 {
	background-color: #F4F4F4;
	font: bold x-small Arial, Helvetica, sans-serif;
	color: #666666;
	text-align: center;
	border-left: 1px solid #CCCCCC;
}
.reportTableRow1 {
	font: normal x-small Arial, Helvetica, sans-serif;
	color: #666666;
	background: White;
	border-right: 1px solid #CCCCCC;
	border-bottom: 1px solid #CCCCCC;
}
.reportTableRow2 {
	font: normal x-small Arial, Helvetica, sans-serif;
	color: #666666;
	background: White;
	text-align: center;
	border-top: 1px solid #CCCCCC;
	border-left: 1px solid #CCCCCC;
}
.reportError {
	font: bold x-small Arial, Helvetica, sans-serif;
	color: Red;
}
.matrixCellLabel {
	font: bold x-small Arial, Helvetica, sans-serif;
	color: #666666;
	width: 30%;
}
.matrixCellData {
	font: normal x-small Arial, Helvetica, sans-serif;
	color: #666666;
	text-align: right;
}
.matrixCellOddRow {
	background-color: #F4F4F4;
}
.matrixCellEvenRow {
	background-color: White;
}
</style>

		<xsl:choose>
			<!-- DISPLAY THE RESULTS IF THE SEARCH CRITERIA MATCHED ANYTHING -->
			<xsl:when test="data and data != ''">
			
			<table class="searchCriteriaTable">
					<tr>

						<td class="searchCriteriaLabel">Report Date</td>
						<td class="searchCriteriaData"><xsl:value-of select="runDate"/></td>
					</tr>
					<tr>
						<td class="searchCriteriaLabel">Roles Selected</td>
						<td class="searchCriteriaData">
							<xsl:call-template name="printRoles"/>
						</td>

					</tr>
					<tr>
						<td class="searchCriteriaLabel">Matrix</td>
						<td class="searchCriteriaData"><xsl:value-of select="extraReportResult[1]/data/datarow/element[@colName='TITLE']"/></td>
					</tr>
					<tr>
						<td class="searchCriteriaLabel">Matrix Users</td>
						<td class="searchCriteriaData"><xsl:value-of select="//extraReportResult[1]/data/datarow/element[@colName='USERS']"/></td>

					</tr>
					<tr>
						<td class="searchCriteriaLabel">Matrix Cells by Level of Completion</td>
						<td class="searchCriteriaData">
							<span class="legend1">R:</span><xsl:call-template name="summaryCells"><xsl:with-param name="status" select="'READY'"/></xsl:call-template> 
							<span class="legend1">P:</span><xsl:call-template name="summaryCells"><xsl:with-param name="status" select="'PENDING'"/></xsl:call-template> 
							<span class="legend1">C:</span><xsl:call-template name="summaryCells"><xsl:with-param name="status" select="'COMPLETE'"/></xsl:call-template> 
							<span class="legend1">L:</span><xsl:call-template name="summaryCells"><xsl:with-param name="status" select="'LOCKED'"/></xsl:call-template>

						</td>
					</tr>
			</table>
			<br />
			<div class="legend1">
				R: <span class="legend2">Ready </span>
				P: <span class="legend2">Pending </span>

				C: <span class="legend2">Completed </span>
				L: <span class="legend2">Locked</span>
			</div>
			<br />
				<table width="100%" cellpadding="8" cellspacing="0" class="reportTable">
					<tr><!-- TABLE HEADERS -->
						<td class="reportTableHeader1">Matrix Categories</td>

						<xsl:for-each select="//extraReportResult[4]/data/datarow">
							<td class="reportTableHeader1"><xsl:value-of select="element[@colName='DESCRIPTION']"/></td>
						</xsl:for-each>
					</tr>

				<xsl:for-each select="//extraReportResult[3]/data/datarow">
					<xsl:variable name="criterion">
						<xsl:value-of select="element[@colName='DESCRIPTION']"/>
					</xsl:variable>

					<tr><!-- TABLE ROWS -->
						<td class="reportTableHeader1">
							<xsl:value-of select="$criterion"/>
						</td>
						<xsl:for-each select="//extraReportResult[4]/data/datarow">
							<td class="reportTableRow1">
								<xsl:call-template name="matrixCell">
									<xsl:with-param name="criterion" select="$criterion"/>
									<xsl:with-param name="level" select="element[@colName='DESCRIPTION']"/>

								</xsl:call-template>
							</td>
						</xsl:for-each>
					</tr>
				</xsl:for-each>
				</table>

			</xsl:when>
			<!-- DISPLAY ERROR IF SEARCH CRITERIA MATCHED NOTHING -->

			<xsl:otherwise>
				<div class="reportError">Nothing found matching search criteria.</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- MATRIX CELL TEMPLATE -->
	<xsl:template name="matrixCell">
		<xsl:param name="criterion"/>

		<xsl:param name="level"/>

		<xsl:variable name="cellTotal">
			<xsl:value-of select="sum(//reportResult/data/datarow[element=$criterion and element=$level]/element[@colName='TOTAL'])"/>
		</xsl:variable>
		<xsl:variable name="cellReady">
			<xsl:choose>
				<xsl:when test="//reportResult/data/datarow[element=$criterion and element=$level and element='READY']">
					<xsl:value-of select="//reportResult/data/datarow[element=$criterion and element=$level and element='READY']/element[@colName='TOTAL']"/>

				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="cellPending">
			<xsl:choose>
				<xsl:when test="//reportResult/data/datarow[element=$criterion and element=$level and element='PENDING']">
					<xsl:value-of select="//reportResult/data/datarow[element=$criterion and element=$level and element='PENDING']/element[@colName='TOTAL']"/>

				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="cellCompleted">
			<xsl:choose>
				<xsl:when test="//reportResult/data/datarow[element=$criterion and element=$level and element='COMPLETE']">
					<xsl:value-of select="//reportResult/data/datarow[element=$criterion and element=$level and element='COMPLETE']/element[@colName='TOTAL']"/>

				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="cellLocked">
			<xsl:choose>
				<xsl:when test="//reportResult/data/datarow[element=$criterion and element=$level and element='LOCKED']">
					<xsl:value-of select="//reportResult/data/datarow[element=$criterion and element=$level and element='LOCKED']/element[@colName='TOTAL']"/>

				</xsl:when>
				<xsl:otherwise>0</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<table width="100%" cellspacing="0">
				<tr class="matrixCellOddRow">
					<td class="matrixCellLabel">R</td>
					<td class="matrixCellData">

						<xsl:variable name="result1">
							<xsl:value-of select="format-number($cellReady div $cellTotal * 100,'#0')"/>
						</xsl:variable>
						<xsl:choose>
							 <xsl:when test="contains(number($result1),'NaN')">
								<xsl:text>0</xsl:text>
							</xsl:when>
							<xsl:otherwise>

								<xsl:value-of select="$result1"/>
							</xsl:otherwise>
						</xsl:choose>% (<xsl:value-of select="$cellReady"/>/<xsl:value-of select="$cellTotal"/>)
					</td>
				</tr>
				<tr class="matrixCellEvenRow">
					<td class="matrixCellLabel">P</td>
					<td class="matrixCellData">

						<xsl:variable name="result2">
							<xsl:value-of select="format-number($cellPending div $cellTotal * 100,'#0')"/>
						</xsl:variable>
						<xsl:choose>
							 <xsl:when test="contains(number($result2),'NaN')">
								<xsl:text>0</xsl:text>
							</xsl:when>
							<xsl:otherwise>

								<xsl:value-of select="$result2"/>
							</xsl:otherwise>
						</xsl:choose>% (<xsl:value-of select="$cellPending"/>/<xsl:value-of select="$cellTotal"/>)
					</td>
				</tr>
				<tr class="matrixCellOddRow">
					<td class="matrixCellLabel">C</td>
					<td class="matrixCellData">

						<xsl:variable name="result3">
							<xsl:value-of select="format-number($cellCompleted div $cellTotal * 100,'#0')"/>
						</xsl:variable>
						<xsl:choose>
							 <xsl:when test="contains(number($result3),'NaN')">
								<xsl:text>0</xsl:text>
							</xsl:when>
							<xsl:otherwise>

								<xsl:value-of select="$result3"/>
							</xsl:otherwise>
						</xsl:choose>% (<xsl:value-of select="$cellCompleted"/>/<xsl:value-of select="$cellTotal"/>)
					</td>
				</tr>
				<tr class="matrixCellEvenRow">
					<td class="matrixCellLabel">L</td>
					<td class="matrixCellData">

						<xsl:variable name="result4">
							<xsl:value-of select="format-number($cellLocked div $cellTotal * 100,'#0')"/>
						</xsl:variable>
						<xsl:choose>
							 <xsl:when test="contains(number($result4),'NaN')">
								<xsl:text>0</xsl:text>
							</xsl:when>
							<xsl:otherwise>

								<xsl:value-of select="$result4"/>
							</xsl:otherwise>
						</xsl:choose>% (<xsl:value-of select="$cellLocked"/>/<xsl:value-of select="$cellTotal"/>)
					</td>
				</tr>
		</table>
	
	</xsl:template>
	
	<!-- SUMMARY CELL STATUS TEMPLATE -->

	<xsl:template name="summaryCells">
		<xsl:param name="status"/>
		
		<xsl:variable name="cellTotal">
			<xsl:value-of select="sum(//reportResult/data/datarow/element[@colName='TOTAL'])"/>
		</xsl:variable>
		
		<xsl:variable name="statusTotal">
			<xsl:value-of select="sum(//reportResult/data/datarow[element=$status]/element[@colName='TOTAL'])"/>
		</xsl:variable>
	
		<xsl:value-of select="$statusTotal"/>/<xsl:value-of select="$cellTotal"/> (<xsl:value-of select="format-number($statusTotal div $cellTotal * 100,'#0')"/>%)
	</xsl:template>

	
	<!-- PRINT ROLES TEMPLATE -->
	<xsl:template name="printRoles">
		<xsl:for-each select="//extraReportResult[2]/data/datarow/element[@colName='ROLE_ID']">
			<xsl:if test="contains(//extraReportResult[2]/parameters/parameter[@name='roleId'],.)">
				<xsl:value-of select="../element[@colName='ROLE_NAME']"/>
					<xsl:if test="not(position()=last())">
						<xsl:text>, </xsl:text>
					</xsl:if>

			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>
