<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:geonet="http://www.fao.org/geonetwork" xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd" version="1.0">
	
	<!-- Do a copy of every nodes and attributes -->
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>
	
	<!-- Remove geonet:* elements. -->
	<xsl:template match="geonet:*" priority="2"/>
	
	<!-- Add missing namespace to extended elements (FIX for GeoSource v1 export) -->
	<xsl:template match="*[@gco:isoType!='']">
		<xsl:copy>
			<xsl:attribute name="gco:isoType">gmd:<xsl:value-of select="@gco:isoType"/></xsl:attribute>
			<xsl:apply-templates select="node()"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
