<?xml version='1.0'?>
<!-- File from uPortal2.0 by JASIG. Modified and adapted to OSPI by <a href="felipeen@udel.edu">Luis F.C. Mendes</a> - University of Delaware-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:output method="xml"/>

<xsl:template match="/">
  <xsl:apply-templates select="tables/table" mode="drop"/>
  <xsl:apply-templates select="tables/table" mode="create"/>
  <xsl:apply-templates select="tables/alter"/>
  <xsl:apply-templates select="tables/index"/>
</xsl:template>

<xsl:template match="table" mode="drop">
<statement type="drop">
DROP TABLE <xsl:value-of select="name"/><xsl:text>
</xsl:text>
</statement><xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="table" mode="create">
<statement type="create">
CREATE TABLE <xsl:value-of select="translate(name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/>
(
<xsl:apply-templates select="columns/column"/>
<xsl:apply-templates select="primary-key"/>
)
</statement><xsl:text>
</xsl:text>
</xsl:template>

<xsl:template match="column">
  <xsl:text>  </xsl:text>
  <xsl:value-of select="name"/>
  <xsl:text> </xsl:text>
  <column-type><xsl:value-of select="type"/>
    <xsl:if test="param">
      <type-param><xsl:value-of select="param"/></type-param>
    </xsl:if>
  </column-type>  
   <xsl:if test="../../not-null = node() or ../../primary-key = node()"> NOT NULL</xsl:if>
  <xsl:if test="position() != last() or ../../primary-key"><xsl:text>,
</xsl:text></xsl:if>
</xsl:template>

<xsl:template match="primary-key">
<xsl:if test="position() = 1">  PRIMARY KEY (</xsl:if><xsl:value-of select="."/><xsl:if test="position() != last()">, </xsl:if><xsl:if test="position() = last()">)</xsl:if>
</xsl:template>

<xsl:template match="alter">
  <statement type="alter">
ALTER TABLE <xsl:value-of select="translate(table-name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/> ADD FOREIGN KEY (<xsl:value-of select="fo-key"/>) REFERENCES <xsl:value-of select="reference"/> (<xsl:value-of select="ref-key"/>)
  </statement>
  <xsl:text></xsl:text>
</xsl:template>

<xsl:template match="index">
<statement type="index">
CREATE INDEX <xsl:value-of select="index-name"/> ON <xsl:value-of select="translate(table-name, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz')"/> (<xsl:value-of select="column"/>)
 </statement>
 <xsl:text></xsl:text>
</xsl:template>

</xsl:stylesheet>
