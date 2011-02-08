<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<axsl:stylesheet xmlns:axsl="http://www.w3.org/1999/XSL/Transform" xmlns:sch="http://www.ascc.net/xml/schematron" version="1.0" xmlns:gml="http://www.opengis.net/gml" gml:dummy-for-xmlns="" xmlns:gmd="http://www.isotc211.org/2005/gmd" gmd:dummy-for-xmlns="" xmlns:srv="http://www.isotc211.org/2005/srv" srv:dummy-for-xmlns="" xmlns:gco="http://www.isotc211.org/2005/gco" gco:dummy-for-xmlns="" xmlns:geonet="http://www.fao.org/geonetwork" geonet:dummy-for-xmlns="" xmlns:xlink="http://www.w3.org/1999/xlink" xlink:dummy-for-xmlns="">
<axsl:output method="html"/>
<axsl:param name="lang"/>
<axsl:variable select="document(concat('loc/', $lang, '/schematron.xml'))" name="loc"/>
<axsl:include href="../../../xsl/main.xsl"/>
<axsl:template mode="schematron-get-full-path" match="*|@*">
<axsl:apply-templates mode="schematron-get-full-path" select="parent::*"/>
<axsl:text>/</axsl:text>
<axsl:if test="count(. | ../@*) = count(../@*)">@</axsl:if>
<axsl:value-of select="name()"/>
<axsl:text>[</axsl:text>
<axsl:value-of select="1+count(preceding-sibling::*[name()=name(current())])"/>
<axsl:text>]</axsl:text>
</axsl:template>
<axsl:template match="/">
<html>
<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
<title/>
<link type="text/css" rel="stylesheet">
<axsl:attribute name="href">
<axsl:text>../../css/</axsl:text>
<axsl:value-of select="/root/gui/env/site/theme"/>
<axsl:text>default.css</axsl:text>
</axsl:attribute>
</link>
</head>
<body>
<table height="100%" width="100%">
<tr class="banner">
<td class="banner">
<img align="top" alt="GeoNetwork opensource">
<axsl:attribute name="src">
<axsl:text>../../images/</axsl:text>
<axsl:value-of select="/root/gui/env/site/theme"/>
<axsl:text>default/header-left.jpg</axsl:text>
</axsl:attribute>
</img>
</td>
<td class="banner" align="right">
<img align="top" alt="World picture">
<axsl:attribute name="src">
<axsl:text>../../images/</axsl:text>
<axsl:value-of select="/root/gui/env/site/theme"/>
<axsl:text>default/header-right.gif</axsl:text>
</axsl:attribute>
</img>
</td>
</tr>
<tr height="100%">
<td colspan="3" class="content">
<h1>
<axsl:value-of select="$loc/strings/title"/>
</h1>
<h2>
<axsl:attribute name="title">
<axsl:value-of select="$loc/strings/report.alt"/>
</axsl:attribute>
<axsl:value-of select="$loc/strings/report"/>
</h2>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M6, '\', '')"/>
</h3>
<axsl:apply-templates mode="M7" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M7, '\', '')"/>
</h3>
<axsl:apply-templates mode="M8" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M8, '\', '')"/>
</h3>
<axsl:apply-templates mode="M9" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M9, '\', '')"/>
</h3>
<axsl:apply-templates mode="M10" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M10, '\', '')"/>
</h3>
<axsl:apply-templates mode="M11" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M11, '\', '')"/>
</h3>
<axsl:apply-templates mode="M12" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M13, '\', '')"/>
</h3>
<axsl:apply-templates mode="M13" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M14, '\', '')"/>
</h3>
<axsl:apply-templates mode="M14" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M15, '\', '')"/>
</h3>
<axsl:apply-templates mode="M15" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M16, '\', '')"/>
</h3>
<axsl:apply-templates mode="M16" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M17, '\', '')"/>
</h3>
<axsl:apply-templates mode="M17" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M18, '\', '')"/>
</h3>
<axsl:apply-templates mode="M18" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M19, '\', '')"/>
</h3>
<axsl:apply-templates mode="M19" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M20, '\', '')"/>
</h3>
<axsl:apply-templates mode="M20" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M21, '\', '')"/>
</h3>
<axsl:apply-templates mode="M21" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M22, '\', '')"/>
</h3>
<axsl:apply-templates mode="M22" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M23, '\', '')"/>
</h3>
<axsl:apply-templates mode="M23" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M24, '\', '')"/>
</h3>
<axsl:apply-templates mode="M24" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M25, '\', '')"/>
</h3>
<axsl:apply-templates mode="M25" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M26, '\', '')"/>
</h3>
<axsl:apply-templates mode="M26" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M27, '\', '')"/>
</h3>
<axsl:apply-templates mode="M27" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M28, '\', '')"/>
</h3>
<axsl:apply-templates mode="M28" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M29, '\', '')"/>
</h3>
<axsl:apply-templates mode="M29" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M30, '\', '')"/>
</h3>
<axsl:apply-templates mode="M30" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M31, '\', '')"/>
</h3>
<axsl:apply-templates mode="M31" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M32, '\', '')"/>
</h3>
<axsl:apply-templates mode="M32" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M33, '\', '')"/>
</h3>
<axsl:apply-templates mode="M33" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M34, '\', '')"/>
</h3>
<axsl:apply-templates mode="M34" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M35, '\', '')"/>
</h3>
<axsl:apply-templates mode="M35" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M36, '\', '')"/>
</h3>
<axsl:apply-templates mode="M36" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M37, '\', '')"/>
</h3>
<axsl:apply-templates mode="M37" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M38, '\', '')"/>
</h3>
<axsl:apply-templates mode="M38" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M39, '\', '')"/>
</h3>
<axsl:apply-templates mode="M39" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M40, '\', '')"/>
</h3>
<axsl:apply-templates mode="M40" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M41, '\', '')"/>
</h3>
<axsl:apply-templates mode="M41" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M42, '\', '')"/>
</h3>
<axsl:apply-templates mode="M42" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M43, '\', '')"/>
</h3>
<axsl:apply-templates mode="M43" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M44, '\', '')"/>
</h3>
<axsl:apply-templates mode="M44" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M45, '\', '')"/>
</h3>
<axsl:apply-templates mode="M45" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M46, '\', '')"/>
</h3>
<axsl:apply-templates mode="M46" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M47, '\', '')"/>
</h3>
<axsl:apply-templates mode="M47" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M48, '\', '')"/>
</h3>
<axsl:apply-templates mode="M48" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M49, '\', '')"/>
</h3>
<axsl:apply-templates mode="M49" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M50, '\', '')"/>
</h3>
<axsl:apply-templates mode="M50" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M51, '\', '')"/>
</h3>
<axsl:apply-templates mode="M51" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M52, '\', '')"/>
</h3>
<axsl:apply-templates mode="M52" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M53, '\', '')"/>
</h3>
<axsl:apply-templates mode="M53" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M54, '\', '')"/>
</h3>
<axsl:apply-templates mode="M54" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M55, '\', '')"/>
</h3>
<axsl:apply-templates mode="M55" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M56, '\', '')"/>
</h3>
<axsl:apply-templates mode="M56" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M57, '\', '')"/>
</h3>
<axsl:apply-templates mode="M57" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M58, '\', '')"/>
</h3>
<axsl:apply-templates mode="M58" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M59, '\', '')"/>
</h3>
<axsl:apply-templates mode="M59" select="/"/>
<h3>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/M60, '\', '')"/>
</h3>
<axsl:apply-templates mode="M60" select="/"/>
</td>
</tr>
</table>
</body>
</html>
</axsl:template>
<axsl:template mode="M7" priority="4000" match="*[gco:CharacterString]">
<axsl:if test="(normalize-space(gco:CharacterString) = '') and (not(@gco:nilReason) or not(contains('inapplicable missing template unknown withheld',@gco:nilReason)))">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M6.characterString, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M7"/>
</axsl:template>
<axsl:template mode="M7" priority="-1" match="text()"/>
<axsl:template mode="M8" priority="4000" match="//gml:DirectPositionType">
<axsl:if test="not(@srsDimension) or @srsName">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M6.directPosition, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:if test="not(@axisLabels) or @srsName">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M7.axisAndSrs, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:if test="not(@uomLabels) or @srsName">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M7.uomAndSrs, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:if test="(not(@uomLabels) and not(@axisLabels)) or (@uomLabels and @axisLabels)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M7.uomAndAxis, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M8"/>
</axsl:template>
<axsl:template mode="M8" priority="-1" match="text()"/>
<axsl:template mode="M9" priority="4000" match="//gmd:CI_ResponsibleParty">
<axsl:choose>
<axsl:when test="(count(gmd:individualName) + count(gmd:organisationName) + count(gmd:positionName)) &gt; 0"/>
<axsl:otherwise>
<li>
<a title="Link to where this pattern was expected" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M8, '\', '')"/>
<b/>
</a>
</li>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M9"/>
</axsl:template>
<axsl:template mode="M9" priority="-1" match="text()"/>
<axsl:template mode="M10" priority="4000" match="//gmd:MD_LegalConstraints|//*[gco:isoType='gmd:MD_LegalConstraints']">
<axsl:if test="gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions' and not(gmd:otherConstraints)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M9.access, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:if test="gmd:useConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions' and not(gmd:otherConstraints)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M9.use, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M10"/>
</axsl:template>
<axsl:template mode="M10" priority="-1" match="text()"/>
<axsl:template mode="M11" priority="4000" match="//gmd:MD_Band">
<axsl:if test="(gmd:maxValue or gmd:minValue) and not(gmd:units)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M9, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M11"/>
</axsl:template>
<axsl:template mode="M11" priority="-1" match="text()"/>
<axsl:template mode="M12" priority="4000" match="//gmd:LI_Source">
<axsl:choose>
<axsl:when test="gmd:description or gmd:sourceExtent"/>
<axsl:otherwise>
<li>
<a title="Link to where this pattern was expected" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M11, '\', '')"/>
<b/>
</a>
</li>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M12"/>
</axsl:template>
<axsl:template mode="M12" priority="-1" match="text()"/>
<axsl:template mode="M13" priority="4000" match="//gmd:DQ_DataQuality">
<axsl:if test="(((count(*/gmd:LI_Lineage/gmd:source) + count(*/gmd:LI_Lineage/gmd:processStep)) = 0)                      and (gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode/@codeListValue='dataset'                              or gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode/@codeListValue='series'))                      and not(gmd:lineage/gmd:LI_Lineage/gmd:statement)                      and (gmd:lineage)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M13, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M13"/>
</axsl:template>
<axsl:template mode="M13" priority="-1" match="text()"/>
<axsl:template mode="M14" priority="4000" match="//gmd:LI_Lineage">
<axsl:if test="not(gmd:source) and not(gmd:statement) and not(gmd:processStep)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M14, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M14"/>
</axsl:template>
<axsl:template mode="M14" priority="-1" match="text()"/>
<axsl:template mode="M15" priority="4000" match="//gmd:LI_Lineage">
<axsl:if test="not(gmd:processStep) and not(gmd:statement) and not(gmd:source)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M15, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M15"/>
</axsl:template>
<axsl:template mode="M15" priority="-1" match="text()"/>
<axsl:template mode="M16" priority="4000" match="//gmd:DQ_DataQuality">
<axsl:if test="gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode/@codeListValue='dataset'                                  and not(gmd:report)                                  and not(gmd:lineage)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M16, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M16"/>
</axsl:template>
<axsl:template mode="M16" priority="-1" match="text()"/>
<axsl:template mode="M17" priority="4000" match="//gmd:DQ_Scope">
<axsl:choose>
<axsl:when test="gmd:level/gmd:MD_ScopeCode/@codeListValue='dataset'                                  or gmd:level/gmd:MD_ScopeCode/@codeListValue='series'                                  or (gmd:levelDescription and ((normalize-space(gmd:levelDescription) != '')                                  or (gmd:levelDescription/gmd:MD_ScopeDescription)                                  or (gmd:levelDescription/@gco:nilReason                                  and contains('inapplicable missing template unknown withheld',gmd:levelDescription/@gco:nilReason))))"/>
<axsl:otherwise>
<li>
<a title="Link to where this pattern was expected" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M17, '\', '')"/>
<b/>
</a>
</li>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M17"/>
</axsl:template>
<axsl:template mode="M17" priority="-1" match="text()"/>
<axsl:template mode="M18" priority="4000" match="//gmd:MD_Medium">
<axsl:if test="gmd:density and not(gmd:densityUnits)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M18, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M18"/>
</axsl:template>
<axsl:template mode="M18" priority="-1" match="text()"/>
<axsl:template mode="M19" priority="4000" match="//gmd:MD_Distribution">
<axsl:choose>
<axsl:when test="count(gmd:distributionFormat)&gt;0                              or count(gmd:distributor/gmd:MD_Distributor/gmd:distributorFormat)&gt;0"/>
<axsl:otherwise>
<li>
<a title="Link to where this pattern was expected" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M19, '\', '')"/>
<b/>
</a>
</li>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M19"/>
</axsl:template>
<axsl:template mode="M19" priority="-1" match="text()"/>
<axsl:template mode="M20" priority="4000" match="//gmd:EX_Extent">
<axsl:choose>
<axsl:when test="count(gmd:description)&gt;0                              or count(gmd:geographicElement)&gt;0                              or count(gmd:temporalElement)&gt;0                              or count(gmd:verticalElement)&gt;0"/>
<axsl:otherwise>
<li>
<a title="Link to where this pattern was expected" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M20, '\', '')"/>
<b/>
</a>
</li>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M20"/>
</axsl:template>
<axsl:template mode="M20" priority="-1" match="text()"/>
<axsl:template mode="M21" priority="4000" match="//gmd:MD_DataIdentification|//*[gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="(not(../../gmd:hierarchyLevel) or ../../gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='dataset')                      and (count(gmd:extent/*/gmd:geographicElement/gmd:EX_GeographicBoundingBox)                          + count (gmd:extent/*/gmd:geographicElement/gmd:EX_GeographicDescription))=0 ">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M21, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M21"/>
</axsl:template>
<axsl:template mode="M21" priority="-1" match="text()"/>
<axsl:template mode="M22" priority="4000" match="//gmd:MD_DataIdentification|//*[gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="(not(../../gmd:hierarchyLevel) or ../../gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='dataset'                      or ../../gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='series')                     and not(gmd:topicCategory)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M6, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M22"/>
</axsl:template>
<axsl:template mode="M22" priority="-1" match="text()"/>
<axsl:template mode="M23" priority="4000" match="//gmd:MD_AggregateInformation">
<axsl:choose>
<axsl:when test="gmd:aggregateDataSetName or gmd:aggregateDataSetIdentifier"/>
<axsl:otherwise>
<li>
<a title="Link to where this pattern was expected" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M22, '\', '')"/>
<b/>
</a>
</li>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M23"/>
</axsl:template>
<axsl:template mode="M23" priority="-1" match="text()"/>
<axsl:template mode="M24" priority="4000" match="//gmd:MD_Metadata/gmd:language|//*[@gco:isoType='gmd:MD_Metadata']/gmd:language">
<axsl:choose>
<axsl:when test=". and ((normalize-space(.) != '')                      or (normalize-space(./gco:CharacterString) != '')                      or (./gmd:LanguageCode)                      or (./@gco:nilReason                          and contains('inapplicable missing template unknown withheld',./@gco:nilReason)))"/>
<axsl:otherwise>
<li>
<a title="Link to where this pattern was expected" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M23, '\', '')"/>
<b/>
</a>
</li>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M24"/>
</axsl:template>
<axsl:template mode="M24" priority="-1" match="text()"/>
<axsl:template mode="M25" priority="4000" match="//gmd:MD_Metadata|//*[@gco:isoType='gmd:MD_Metadata']">
<axsl:apply-templates mode="M25"/>
</axsl:template>
<axsl:template mode="M25" priority="-1" match="text()"/>
<axsl:template mode="M26" priority="4000" match="//gmd:MD_ExtendedElementInformation">
<axsl:choose>
<axsl:when test="(gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelist' or gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='enumeration' or gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelistElement') or (gmd:obligation and ((normalize-space(gmd:obligation) != '')  or (gmd:obligation/gmd:MD_ObligationCode) or (gmd:obligation/@gco:nilReason and contains('inapplicable missing template unknown withheld',gmd:obligation/@gco:nilReason))))"/>
<axsl:otherwise>
<li>
<a title="Link to where this pattern was expected" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M26.obligation, '\', '')"/>
<b/>
</a>
</li>
</axsl:otherwise>
</axsl:choose>
<axsl:choose>
<axsl:when test="(gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelist' or gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='enumeration' or gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelistElement') or (gmd:maximumOccurrence and ((normalize-space(gmd:maximumOccurrence) != '')  or (normalize-space(gmd:maximumOccurrence/gco:CharacterString) != '') or (gmd:maximumOccurrence/@gco:nilReason and contains('inapplicable missing template unknown withheld',gmd:maximumOccurrence/@gco:nilReason))))"/>
<axsl:otherwise>
<li>
<a title="Link to where this pattern was expected" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M26.minimumOccurence, '\', '')"/>
<b/>
</a>
</li>
</axsl:otherwise>
</axsl:choose>
<axsl:choose>
<axsl:when test="(gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelist' or gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='enumeration' or gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelistElement') or (gmd:domainValue and ((normalize-space(gmd:domainValue) != '')  or (normalize-space(gmd:domainValue/gco:CharacterString) != '') or (gmd:domainValue/@gco:nilReason and contains('inapplicable missing template unknown withheld',gmd:domainValue/@gco:nilReason))))"/>
<axsl:otherwise>
<li>
<a title="Link to where this pattern was expected" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M26.domainValue, '\', '')"/>
<b/>
</a>
</li>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M26"/>
</axsl:template>
<axsl:template mode="M26" priority="-1" match="text()"/>
<axsl:template mode="M27" priority="4000" match="//gmd:MD_ExtendedElementInformation">
<axsl:if test="gmd:obligation/gmd:MD_ObligationCode='conditional' and not(gmd:condition)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M27, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M27"/>
</axsl:template>
<axsl:template mode="M27" priority="-1" match="text()"/>
<axsl:template mode="M28" priority="4000" match="//gmd:MD_ExtendedElementInformation">
<axsl:if test="gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelistElement' and not(gmd:domainCode)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M28, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M28"/>
</axsl:template>
<axsl:template mode="M28" priority="-1" match="text()"/>
<axsl:template mode="M29" priority="4000" match="//gmd:MD_ExtendedElementInformation">
<axsl:if test="gmd:dataType/gmd:MD_DatatypeCode/@codeListValue!='codelistElement' and not(gmd:shortName)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M29, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M29"/>
</axsl:template>
<axsl:template mode="M29" priority="-1" match="text()"/>
<axsl:template mode="M30" priority="4000" match="//gmd:MD_Georectified">
<axsl:if test="(gmd:checkPointAvailability/gco:Boolean='1' or gmd:checkPointAvailability/gco:Boolean='true') and not(gmd:checkPointDescription)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M30, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M30"/>
</axsl:template>
<axsl:template mode="M30" priority="-1" match="text()"/>
<axsl:template mode="M31" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="not(gmd:language)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M31, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M31"/>
</axsl:template>
<axsl:template mode="M31" priority="-1" match="text()"/>
<axsl:template mode="M32" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="not(gmd:characterSet)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M32, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M32"/>
</axsl:template>
<axsl:template mode="M32" priority="-1" match="text()"/>
<axsl:template mode="M33" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="not(gmd:hierarchyLevel)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M33, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M33"/>
</axsl:template>
<axsl:template mode="M33" priority="-1" match="text()"/>
<axsl:template mode="M34" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="not(gmd:hierarchyLevelName)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M34, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M34"/>
</axsl:template>
<axsl:template mode="M34" priority="-1" match="text()"/>
<axsl:template mode="M35" priority="4000" match="//gmd:citation">
<axsl:if test="not(*/gmd:title) or (*/gmd:title/@gco:nilReason)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M35, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M35"/>
</axsl:template>
<axsl:template mode="M35" priority="-1" match="text()"/>
<axsl:template mode="M36" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:abstract) or (gmd:abstract/@gco:nilReason)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M36, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M36"/>
</axsl:template>
<axsl:template mode="M36" priority="-1" match="text()"/>
<axsl:template mode="M37" priority="4000" match="//gmd:MD_Metadata/gmd:hierarchyLevel">
<axsl:if test="not(.) or (gmd:MD_ScopeCode/@codeListValue='')">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M37, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M37"/>
</axsl:template>
<axsl:template mode="M37" priority="-1" match="text()"/>
<axsl:template mode="M38" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:citation/*/gmd:identifier/*/gmd:code) or (gmd:citation/*/gmd:identifier/*/gmd:code/@gco:nilReason)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M38, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M38"/>
</axsl:template>
<axsl:template mode="M38" priority="-1" match="text()"/>
<axsl:template mode="M39" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:topicCategory) or (normalize-space(gmd:topicCategory/gmd:MD_TopicCategoryCode) = '')">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M39, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M39"/>
</axsl:template>
<axsl:template mode="M39" priority="-1" match="text()"/>
<axsl:template mode="M40" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:descriptiveKeywords) or (gmd:descriptiveKeywords/*/gmd:keyword/@gco:nilReason) or (normalize-space(gmd:descriptiveKeywords/*/gmd:keyword/gco:CharacterString)='')">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M40, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M40"/>
</axsl:template>
<axsl:template mode="M40" priority="-1" match="text()"/>
<axsl:template mode="M41" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not((gmd:extent/*/gmd:geographicElement/*/gmd:westBoundLongitude) and (gmd:extent/*/gmd:geographicElement/*/gmd:eastBoundLongitude)  and (gmd:extent/*/gmd:geographicElement/*/gmd:southBoundLatitude) and (gmd:extent/*/gmd:geographicElement/*/gmd:northBoundLatitude))  or not(normalize-space(gmd:extent/*/gmd:geographicElement/*/gmd:westBoundLongitude/gco:Decimal)) or not(normalize-space(gmd:extent/*/gmd:geographicElement/*/gmd:northBoundLatitude/gco:Decimal))  or not(normalize-space(gmd:extent/*/gmd:geographicElement/*/gmd:eastBoundLongitude/gco:Decimal)) or not(normalize-space(gmd:extent/*/gmd:geographicElement/*/gmd:southBoundLatitude/gco:Decimal))">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M41, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M41"/>
</axsl:template>
<axsl:template mode="M41" priority="-1" match="text()"/>
<axsl:template mode="M42" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(normalize-space(gmd:citation//gmd:date//gmd:date/gco:DateTime)) and not(gmd:extent//gmd:temporalElement//gmd:extent/gml:TimePeriod) and not(gmd:extent//gmd:temporalElement//gmd:extent/gml:TimeInstant)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M42, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M42"/>
</axsl:template>
<axsl:template mode="M42" priority="-1" match="text()"/>
<axsl:template mode="M43" priority="4000" match="//gmd:DQ_DataQuality">
<axsl:if test="not(gmd:lineage//gmd:statement) or (gmd:lineage//gmd:statement/@gco:nilReason)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M43, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M43"/>
</axsl:template>
<axsl:template mode="M43" priority="-1" match="text()"/>
<axsl:template mode="M44" priority="4000" match="//gmd:dataQualityInfo/gmd:DQ_DataQuality">
<axsl:if test="not(gmd:report//gmd:result) or (gmd:report//gmd:result//gmd:specification//gmd:title/@gco:nilReason)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M44, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M44"/>
</axsl:template>
<axsl:template mode="M44" priority="-1" match="text()"/>
<axsl:template mode="M45" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:resourceConstraints/*/gmd:useLimitation) or (gmd:resourceConstraints/*/gmd:useLimitation/@gco:nilReason)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M45, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M45"/>
</axsl:template>
<axsl:template mode="M45" priority="-1" match="text()"/>
<axsl:template mode="M46" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="(not(gmd:resourceConstraints/*/gmd:accessConstraints) or (gmd:resourceConstraints/*/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='')) and (not(gmd:resourceConstraints/*/gmd:classification) or (gmd:resourceConstraints/*/gmd:classification/gmd:MD_ClassificationCode/@codeListValue='')) and (not(gmd:resourceConstraints/*/gmd:otherConstraints) or (gmd:resourceConstraints/*/gmd:otherConstraints/@gco:nilReason))">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M46, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M46"/>
</axsl:template>
<axsl:template mode="M46" priority="-1" match="text()"/>
<axsl:template mode="M47" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:pointOfContact/*/gmd:organisationName) or (gmd:pointOfContact/*/gmd:organisationName/@gco:nilReason) or not(gmd:pointOfContact/*/gmd:contactInfo/*/gmd:address/*/gmd:electronicMailAddress) or (gmd:pointOfContact/*/gmd:contactInfo/*/gmd:address/*/gmd:electronicMailAddress/@gco:nilReason)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M47, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M47"/>
</axsl:template>
<axsl:template mode="M47" priority="-1" match="text()"/>
<axsl:template mode="M48" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="not(gmd:contact/gmd:CI_ResponsibleParty/gmd:organisationName) or (gmd:contact/gmd:CI_ResponsibleParty/gmd:organisationName/@gco:nilReason) or not(gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress) or (gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/@gco:nilReason))">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M48, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M48"/>
</axsl:template>
<axsl:template mode="M48" priority="-1" match="text()"/>
<axsl:template mode="M49" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="(not(gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='service') and (not(gmd:language) or (gmd:language/@gco:nilReason)))">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M49, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M49"/>
</axsl:template>
<axsl:template mode="M49" priority="-1" match="text()"/>
<axsl:template mode="M50" priority="4000" match="//gmd:MD_Metadata|//*[gco:isoType='gmd:MD_Metadata']">
<axsl:if test="not(gmd:dateStamp)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M50, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M50"/>
</axsl:template>
<axsl:template mode="M50" priority="-1" match="text()"/>
<axsl:template mode="M51" priority="4000" match="//gmd:identificationInfo">
<axsl:if test="(../../gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='service') and not(*/srv:operatesOn)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M51, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M51"/>
</axsl:template>
<axsl:template mode="M51" priority="-1" match="text()"/>
<axsl:template mode="M52" priority="4000" match="//gmd:distributionInfo">
<axsl:if test="(not(*/gmd:transferOptions/*/gmd:onLine/*/gmd:linkage) or normalize-space(*/gmd:transferOptions/*/gmd:onLine/*/gmd:linkage/gmd:URL)='')">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M52, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M52"/>
</axsl:template>
<axsl:template mode="M52" priority="-1" match="text()"/>
<axsl:template mode="M53" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="count(gmd:resourceConstraints/gmd:MD_Constraints/gmd:useLimitation) &gt;1 or                  count(gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useLimitation) &gt;1 or                  count(gmd:resourceConstraints/gmd:MD_SecurityConstraints/gmd:useLimitation) &gt;1 or                  count(gmd:resourceConstraints/*[gco:isoType='gmd:MD_Constraints']/gmd:useLimitation) &gt;1 or                  count(gmd:resourceConstraints/*[gco:isoType='gmd:MD_LegalConstraints']/gmd:useLimitation) &gt;1 or                  count(gmd:resourceConstraints/*[gco:isoType='gmd:MD_SecurityConstraints']/gmd:useLimitation) &gt;1">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M53, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M53"/>
</axsl:template>
<axsl:template mode="M53" priority="-1" match="text()"/>
<axsl:template mode="M54" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="(gmd:resourceConstraints/*/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions') and                  not(gmd:resourceConstraints/*/gmd:otherConstraints)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M54, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M54"/>
</axsl:template>
<axsl:template mode="M54" priority="-1" match="text()"/>
<axsl:template mode="M55" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(../../gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='service') and (not(gmd:language) or (gmd:language/@gco:nilReason))">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M55, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M55"/>
</axsl:template>
<axsl:template mode="M55" priority="-1" match="text()"/>
<axsl:template mode="M56" priority="4000" match="//gmd:identificationInfo">
<axsl:if test="not(*/gmd:spatialResolution/*/gmd:equivalentScale) and not(*/gmd:spatialResolution/*/gmd:distance)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M56, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M56"/>
</axsl:template>
<axsl:template mode="M56" priority="-1" match="text()"/>
<axsl:template mode="M57" priority="4000" match="//srv:SV_ServiceIdentification">
<axsl:if test="not(srv:containsOperations/srv:SV_OperationMetadata/srv:operationName)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M57, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M57"/>
</axsl:template>
<axsl:template mode="M57" priority="-1" match="text()"/>
<axsl:template mode="M58" priority="4000" match="//srv:SV_ServiceIdentification">
<axsl:if test="not(srv:containsOperations/srv:SV_OperationMetadata/srv:DCP)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M58, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M58"/>
</axsl:template>
<axsl:template mode="M58" priority="-1" match="text()"/>
<axsl:template mode="M59" priority="4000" match="//srv:SV_ServiceIdentification">
<axsl:if test="not(srv:containsOperations/srv:SV_OperationMetadata/srv:connectPoint)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M59, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M59"/>
</axsl:template>
<axsl:template mode="M59" priority="-1" match="text()"/>
<axsl:template mode="M60" priority="4000" match="//srv:SV_ServiceIdentification">
<axsl:if test="not(srv:serviceType)">
<li>
<a title="Link to where this pattern was found" target="out" href="schematron-out.html#{generate-id(.)}">
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="translate($loc/strings/alert.M60, '\', '')"/>
<b/>
</a>
</li>
</axsl:if>
<axsl:apply-templates mode="M60"/>
</axsl:template>
<axsl:template mode="M60" priority="-1" match="text()"/>
<axsl:template priority="-1" match="text()"/>
</axsl:stylesheet>
