<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- === debug mode ======================================= -->
	
	<xsl:template match="general/debug">
		<debug>false</debug>
	</xsl:template>

	<!-- === resources ======================================= -->
	
	<xsl:template match="resource[@enabled='true']/config/user">
		<user>$JDBC_USER</user>
	</xsl:template>

	<xsl:template match="resource[@enabled='true']/config/password">
		<password>$JDBC_PASSWORD</password>
	</xsl:template>
	
	<xsl:template match="resource[@enabled='true']/config/driver">
		<driver>$JDBC_DRIVER</driver>
	</xsl:template>
	
	<xsl:template match="resource[@enabled='true']/config/url">
		<url>$JDBC_URL</url>
	</xsl:template>
	
	<xsl:template match="resource[@enabled='true']/activator"/>

	<!-- === element copying =========================================== -->
	
	<xsl:template match="@*|node()">
		 <xsl:copy>
			  <xsl:apply-templates select="@*|node()"/>
		 </xsl:copy>
	</xsl:template>

</xsl:stylesheet>
