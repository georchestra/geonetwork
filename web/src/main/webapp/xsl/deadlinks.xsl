<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:include href="main.xsl"/>
	
	<!--
	page content
	-->
	<xsl:template name="content">
	<h2><xsl:value-of select="/root/gui/strings/deadlinks" /></h2>
	<ul>
		<xsl:apply-templates select="//badUrl" mode="badUrl" />
	</ul>

	<h2><xsl:value-of select="/root/gui/strings/deadchildparentlinks" /></h2>

	<ul>
	 	<xsl:apply-templates select="//badMd" mode="badMd" />
	</ul>


	</xsl:template>

	<xsl:template match="//badUrl" mode="badUrl">
		<xsl:variable name="id" select="@mdId" />
		<xsl:variable name="deadlink" select="." />
		<li>
			<a href="{/root/gui/locService}/metadata.edit?id={$id}"><xsl:value-of select="$deadlink"/> (Metadata #<xsl:value-of select="$id"/>)</a>
		</li>
	</xsl:template>


	<xsl:template match="//badMd" mode="badMd">
		<xsl:variable name="id" select="@mdId" />
		<xsl:variable name="deadlink" select="." />
		<li>
			<a href="{/root/gui/locService}/metadata.edit?uuid={$id}">parent <xsl:value-of select="$deadlink"/></a>
		</li>
	</xsl:template>
		  
</xsl:stylesheet>