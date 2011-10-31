<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:geonet="http://www.fao.org/geonetwork" xmlns:gco="http://www.isotc211.org/2005/gco"
  xmlns:gmd="http://www.isotc211.org/2005/gmd" version="2.0">

  <xsl:template name="list-keywords-remove-duplicates">
    <suggestion process="keywords-remove-duplicates"/>
  </xsl:template>

  <!-- Analyze the metadata record and return available suggestion
      for that process -->
  <xsl:template name="analyze-keywords-remove-duplicates">
    <xsl:param name="root"/>
    <xsl:variable name="distinctKeywords"
      select="count(distinct-values($root//gmd:keyword/gco:CharacterString))"/>
    <xsl:variable name="keywords" select="count($root//gmd:keyword/gco:CharacterString)"/>
    <!-- TODO : PT_FreeText -->
    <xsl:variable name="duplicates" select="$keywords - $distinctKeywords"/>
    <xsl:if test="$duplicates > 0">
      <suggestion process="keywords-remove-duplicates" category="keyword" target="keyword">
        <name xml:lang="en"><xsl:value-of select="$duplicates"/> duplicate keyword(s) found. Run
          this task to remove it(them).</name>
        <operational>true</operational>
        <form/>
      </suggestion>
    </xsl:if>
  </xsl:template>

  <!-- Do a copy of every nodes and attributes -->
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

  <!-- Remove geonet:* elements. -->
  <xsl:template match="geonet:*" priority="2"/>

  <!-- Remove duplicates
  <descriptiveKeywords>
    <MD_Keywords>
      <keyword>
        <gco:CharacterString/>
  -->
  <xsl:template match="gmd:keyword" priority="2">
    <xsl:variable name="current" select="gco:CharacterString"/>
    <xsl:variable name="count"
      select="count(../../../gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword[gco:CharacterString = $current])"/>
    <xsl:choose>
      <xsl:when test="$count = 1">
          <xsl:copy-of select="."/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:variable name="position" select="position()"/>
        <xsl:variable name="firstDuplicateposition" select="../../../gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword[1][gco:CharacterString = $current]/position()"/>
        <xsl:message>##<xsl:value-of select="$count"/> for <xsl:value-of select="gco:CharacterString"
        />:<xsl:value-of select="$position"
        />=<xsl:value-of select="$firstDuplicateposition"/></xsl:message>
          <xsl:copy-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
    
    

  </xsl:template>

</xsl:stylesheet>
