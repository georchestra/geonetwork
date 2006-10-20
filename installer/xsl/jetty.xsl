<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output doctype-public="-//Mort Bay Consulting//DTD Configure//EN" doctype-system="jetty-configure_1_3.dtd"/> 
    
	<!-- ============================================================ -->

	<xsl:template match="Call[string(@name)='addWebApplication']" xml:space="preserve">
		<Call name="addWebApplication">
			<Arg>/$SERVLET</Arg>
			<Arg>../web</Arg>
		</Call>
	</xsl:template>

	<!-- ============================================================ -->

	<xsl:template match="Set[string(@name)='port']">
		<Set name="port">$PORT</Set>
	</xsl:template>

	<!-- ============================================================ -->

	<xsl:template match="@*|node()">
		 <xsl:copy>
			  <xsl:apply-templates select="@*|node()"/>
		 </xsl:copy>
	</xsl:template>

</xsl:stylesheet>
