<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"  xmlns:j2ee="http://java.sun.com/xml/ns/j2ee" exclude-result-prefixes="j2ee">

	<xsl:template match="j2ee:display-name" xml:space="preserve">
		 <xsl:copy>$SERVLET/intermap</xsl:copy>
	</xsl:template>

	<xsl:template match="j2ee:servlet-name" xml:space="preserve">
		 <xsl:copy>$SERVLET/intermap</xsl:copy>
	</xsl:template>
	
	<xsl:template match="@*|node()">
		 <xsl:copy>
			  <xsl:apply-templates select="@*|node()"/>
		 </xsl:copy>
	</xsl:template>

</xsl:stylesheet>
