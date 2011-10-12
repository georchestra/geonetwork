<?xml version="1.0" encoding="UTF-8"?>
<!--
    Parameters:
    * process=update-xlink (fixed value)
    * s : old host URL
    * r : new host URL
    
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:geonet="http://www.fao.org/geonetwork" xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:gco="http://www.isotc211.org/2005/gco" 
    xmlns:gmd="http://www.isotc211.org/2005/gmd"
    xmlns:gmx="http://www.isotc211.org/2005/gmx" 
    xmlns:exslt="http://exslt.org/common" version="2.0" exclude-result-prefixes="exslt">
    
    <xsl:param name="s">http://localhost:8084/</xsl:param>
    <xsl:param name="r">http://localhost:8080/</xsl:param>
    
    <!-- Do a copy of every nodes and attributes -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Remove geonet:* elements. -->
    <xsl:template match="geonet:*" priority="2"/>
    
 
    <!-- Replace url prefix. -->
    <xsl:template match="*[@xlink:href]" priority="2">
        <xsl:message>Processing:<xsl:value-of select="name(.)"/></xsl:message>
        <xsl:message>  XLink:<xsl:value-of select="@xlink:href"/></xsl:message>
        <xsl:copy>
            <xsl:apply-templates select="@*[name(.) != 'xlink:href']"/>
            
            <xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
                
                <xsl:variable name="url">
					<xsl:value-of select="@xlink:href"/>
                </xsl:variable>
                
                <xsl:choose>
                    <xsl:when test="starts-with($url, $s)">
                    	<xsl:message> Replaced by:<xsl:value-of select="$r"/><xsl:value-of select="substring-after($url, $s)"/></xsl:message>
                        <xsl:value-of select="$r"/><xsl:value-of select="substring-after($url, $s)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$url"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates select="*"/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
