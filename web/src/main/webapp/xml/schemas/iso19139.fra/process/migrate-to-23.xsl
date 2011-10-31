<?xml version="1.0" encoding="UTF-8"?>
<!--
    Processing steps are :
    * update host and port
    * update inspire thesaurus name (inspire -> inspire-theme)
    * update inspire theme ids
    
    Parameters:
    * process=migrate-to-23 (fixed value)
    * s : old host URL
    * r : new host URL
    * add gmx:FileName
    
    Calling the process using:
    http://localhost:8080/geonetwork/srv/fr/metadata.select?id=0&selected=add-all
    http://localhost:8080/geonetwork/srv/en/metadata.massive.processing?process=migrate-to-23&s=http://localhost:8084&r=http://localhost:8080
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:geonet="http://www.fao.org/geonetwork" xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:gco="http://www.isotc211.org/2005/gco" 
    xmlns:gmd="http://www.isotc211.org/2005/gmd"
    xmlns:gmx="http://www.isotc211.org/2005/gmx" 
    xmlns:exslt="http://exslt.org/common" version="2.0" exclude-result-prefixes="exslt">
    
    <xsl:param name="s">http://localhost:8084/</xsl:param>
    <xsl:param name="r">http://localhost:8080/</xsl:param>
    
    
    <xsl:variable name="map">
        <map key="http://geonetwork-opensource.org/inspire%232"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/1"/>
        <map key="http://geonetwork-opensource.org/inspire%233"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/2"/>
        <map key="http://geonetwork-opensource.org/inspire%234"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/3"/>
        <map key="http://geonetwork-opensource.org/inspire%235"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/4"/>
        <map key="http://geonetwork-opensource.org/inspire%236"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/5"/>
        <map key="http://geonetwork-opensource.org/inspire%237"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/6"/>
        <map key="http://geonetwork-opensource.org/inspire%238"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/7"/>
        <map key="http://geonetwork-opensource.org/inspire%239"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/8"/>
        <map key="http://geonetwork-opensource.org/inspire%2310"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/9"/>
        <map key="http://geonetwork-opensource.org/inspire%2311"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/10"/>
        <map key="http://geonetwork-opensource.org/inspire%2312"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/11"/>
        <map key="http://geonetwork-opensource.org/inspire%2313"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/12"/>
        <map key="http://geonetwork-opensource.org/inspire%2314"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/13"/>
        <map key="http://geonetwork-opensource.org/inspire%2315"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/14"/>
        <map key="http://geonetwork-opensource.org/inspire%2316"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/15"/>
        <map key="http://geonetwork-opensource.org/inspire%2317"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/16"/>
        <map key="http://geonetwork-opensource.org/inspire%2318"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/17"/>
        <map key="http://geonetwork-opensource.org/inspire%2319"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/18"/>
        <map key="http://geonetwork-opensource.org/inspire%2320"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/19"/>
        <map key="http://geonetwork-opensource.org/inspire%2321"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/20"/>
        <map key="http://geonetwork-opensource.org/inspire%2322"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/21"/>
        <map key="http://geonetwork-opensource.org/inspire%2323"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/22"/>
        <map key="http://geonetwork-opensource.org/inspire%2324"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/23"/>
        <map key="http://geonetwork-opensource.org/inspire%2325"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/24"/>
        <map key="http://geonetwork-opensource.org/inspire%2326"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/25"/>
        <map key="http://geonetwork-opensource.org/inspire%2327"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/26"/>
        <map key="http://geonetwork-opensource.org/inspire%2328"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/27"/>
        <map key="http://geonetwork-opensource.org/inspire%2329"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/28"/>
        <map key="http://geonetwork-opensource.org/inspire%2330"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/29"/>
        <map key="http://geonetwork-opensource.org/inspire%2331"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/30"/>
        <map key="http://geonetwork-opensource.org/inspire%2332"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/31"/>
        <map key="http://geonetwork-opensource.org/inspire%2333"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/32"/>
        <map key="http://geonetwork-opensource.org/inspire%2334"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/33"/>
        <map key="http://geonetwork-opensource.org/inspire%2335"
            value="http://rdfdata.eionet.europa.eu/inspirethemes/themes/34"/>
    </xsl:variable>
    
    <!-- Do a copy of every nodes and attributes -->
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>
    
    <!-- Remove geonet:* elements. -->
    <xsl:template match="geonet:*" priority="2"/>
    
    <!-- Replace url to internal resources by gmx:FileName -->
    <xsl:template match="gmd:otherCitationDetails[gco:CharacterString]" priority="2">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:choose>
                <xsl:when test="contains(gco:CharacterString, '/resources.get')">
                    <gmx:FileName src="{substring-before(substring-after(gco:CharacterString, 'fname='), '&amp;access=')}"><xsl:value-of select="gco:CharacterString"/></gmx:FileName>
                </xsl:when>
                <xsl:otherwise><xsl:apply-templates select="*"/></xsl:otherwise>
            </xsl:choose>
        </xsl:copy>
    </xsl:template>
    
    <!-- Replace url prefix. -->
    <xsl:template match="*[@xlink:href]" priority="2">
        <xsl:message>Processing:<xsl:value-of select="name(.)"/></xsl:message>
        <xsl:message>XLink:<xsl:value-of select="@xlink:href"/></xsl:message>
        <xsl:copy>
            <xsl:apply-templates select="@*[name(.) != 'xlink:href']"/>
            
            <xsl:attribute name="href" namespace="http://www.w3.org/1999/xlink">
                
                <xsl:variable name="url">
                    <xsl:choose>
                        <xsl:when test="name(.)='gmd:descriptiveKeywords'">
                            <xsl:call-template name="remap">
                                <xsl:with-param name="txt" select="@xlink:href"/>
                            </xsl:call-template>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="@xlink:href"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                
                <xsl:choose>
                    <xsl:when test="starts-with($url, $s)">
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
    
    <xsl:template name="remap">
        <xsl:param name="txt"/>
        <xsl:param name="pos" select="1"/>
        <xsl:variable name="mapNodes" select="exslt:node-set($map)"/>
        
        <xsl:variable name="node" select="$mapNodes/map[$pos]"/>
        <xsl:choose>
            <xsl:when test="contains($txt, $node/@key)">
                <xsl:message> - key:<xsl:value-of select="$node/@key"/></xsl:message>
                <xsl:message> - value:<xsl:value-of select="$node/@value"/></xsl:message>
                
                <xsl:variable name="newTxt"
                    select="concat(substring-before($txt, $node/@key), $node/@value, substring-after($txt, $node/@key))"/>
                
                <xsl:message> - updated:<xsl:value-of select="$newTxt"/></xsl:message>
                
                <xsl:if test="$pos &lt; count($mapNodes/*)">
                    <xsl:call-template name="remap">
                        <xsl:with-param name="txt" select="$newTxt"/>
                        <xsl:with-param name="pos" select="$pos + 1"/>
                    </xsl:call-template>
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$pos &lt; count($mapNodes/*)">
                        <xsl:call-template name="remap">
                            <xsl:with-param name="txt" select="$txt"/>
                            <xsl:with-param name="pos" select="$pos + 1"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$txt"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
</xsl:stylesheet>
