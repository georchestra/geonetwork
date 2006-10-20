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

	<!-- === application handler ======================================= -->
	
	<xsl:template match="appHandler" xml:space="preserve">		
		<appHandler class="org.fao.geonet.Geonetwork">
			<param name="network"        value="$NETWORK" />
			<param name="netmask"        value="$NETMASK" />
			<param name="port"           value="$PORT" />
			<param name="luceneDir"      value="WEB-INF/lucene" />
			<param name="z3950Port"      value="$Z3950_PORT" />
			<param name="schemaMappings" value="schema-mappings.xml" />
			<param name="dataDir"        value="WEB-INF/data" />
			<param name="siteId"         value="$SITE_ID" />
			<param name="publicHost"     value="$PUBLIC_HOST" />
			<param name="publicPort"     value="$PUBLIC_PORT" />
		</appHandler>
	</xsl:template>
	
	<!-- === element copying =========================================== -->
	
	<xsl:template match="@*|node()">
		 <xsl:copy>
			  <xsl:apply-templates select="@*|node()"/>
		 </xsl:copy>
	</xsl:template>

</xsl:stylesheet>
