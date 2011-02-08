<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<axsl:stylesheet xmlns:axsl="http://www.w3.org/1999/XSL/Transform" xmlns:sch="http://www.ascc.net/xml/schematron" version="1.0" xmlns:gml="http://www.opengis.net/gml" gml:dummy-for-xmlns="" xmlns:gmd="http://www.isotc211.org/2005/gmd" gmd:dummy-for-xmlns="" xmlns:srv="http://www.isotc211.org/2005/srv" srv:dummy-for-xmlns="" xmlns:gco="http://www.isotc211.org/2005/gco" gco:dummy-for-xmlns="" xmlns:geonet="http://www.fao.org/geonetwork" geonet:dummy-for-xmlns="" xmlns:xlink="http://www.w3.org/1999/xlink" xlink:dummy-for-xmlns="">
<axsl:output method="xml"/>
<axsl:param name="lang"/>
<axsl:variable select="document(concat('loc/', $lang, '/schematron.xml'))" name="loc"/>
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
<geonet:schematronerrors>
<axsl:apply-templates mode="M7" select="/"/>
<axsl:apply-templates mode="M8" select="/"/>
<axsl:apply-templates mode="M9" select="/"/>
<axsl:apply-templates mode="M10" select="/"/>
<axsl:apply-templates mode="M11" select="/"/>
<axsl:apply-templates mode="M12" select="/"/>
<axsl:apply-templates mode="M13" select="/"/>
<axsl:apply-templates mode="M14" select="/"/>
<axsl:apply-templates mode="M15" select="/"/>
<axsl:apply-templates mode="M16" select="/"/>
<axsl:apply-templates mode="M17" select="/"/>
<axsl:apply-templates mode="M18" select="/"/>
<axsl:apply-templates mode="M19" select="/"/>
<axsl:apply-templates mode="M20" select="/"/>
<axsl:apply-templates mode="M21" select="/"/>
<axsl:apply-templates mode="M22" select="/"/>
<axsl:apply-templates mode="M23" select="/"/>
<axsl:apply-templates mode="M24" select="/"/>
<axsl:apply-templates mode="M25" select="/"/>
<axsl:apply-templates mode="M26" select="/"/>
<axsl:apply-templates mode="M27" select="/"/>
<axsl:apply-templates mode="M28" select="/"/>
<axsl:apply-templates mode="M29" select="/"/>
<axsl:apply-templates mode="M30" select="/"/>
<axsl:apply-templates mode="M31" select="/"/>
<axsl:apply-templates mode="M32" select="/"/>
<axsl:apply-templates mode="M33" select="/"/>
<axsl:apply-templates mode="M34" select="/"/>
<axsl:apply-templates mode="M35" select="/"/>
<axsl:apply-templates mode="M36" select="/"/>
<axsl:apply-templates mode="M37" select="/"/>
<axsl:apply-templates mode="M38" select="/"/>
<axsl:apply-templates mode="M39" select="/"/>
<axsl:apply-templates mode="M40" select="/"/>
<axsl:apply-templates mode="M41" select="/"/>
<axsl:apply-templates mode="M42" select="/"/>
<axsl:apply-templates mode="M43" select="/"/>
<axsl:apply-templates mode="M44" select="/"/>
<axsl:apply-templates mode="M45" select="/"/>
<axsl:apply-templates mode="M46" select="/"/>
<axsl:apply-templates mode="M47" select="/"/>
<axsl:apply-templates mode="M48" select="/"/>
<axsl:apply-templates mode="M49" select="/"/>
<axsl:apply-templates mode="M50" select="/"/>
<axsl:apply-templates mode="M51" select="/"/>
<axsl:apply-templates mode="M52" select="/"/>
<axsl:apply-templates mode="M53" select="/"/>
<axsl:apply-templates mode="M54" select="/"/>
<axsl:apply-templates mode="M55" select="/"/>
<axsl:apply-templates mode="M56" select="/"/>
<axsl:apply-templates mode="M57" select="/"/>
<axsl:apply-templates mode="M58" select="/"/>
<axsl:apply-templates mode="M59" select="/"/>
<axsl:apply-templates mode="M60" select="/"/>
</geonet:schematronerrors>
</axsl:template>
<axsl:template mode="M7" priority="4000" match="*[gco:CharacterString]">
<axsl:if test="(normalize-space(gco:CharacterString) = '') and (not(@gco:nilReason) or not(contains('inapplicable missing template unknown withheld',@gco:nilReason)))">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M6.characterString"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M7"/>
</axsl:template>
<axsl:template mode="M7" priority="-1" match="text()"/>
<axsl:template mode="M8" priority="4000" match="//gml:DirectPositionType">
<axsl:if test="not(@srsDimension) or @srsName">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M6.directPosition"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:if test="not(@axisLabels) or @srsName">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M7.axisAndSrs"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:if test="not(@uomLabels) or @srsName">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M7.uomAndSrs"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:if test="(not(@uomLabels) and not(@axisLabels)) or (@uomLabels and @axisLabels)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M7.uomAndAxis"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M8"/>
</axsl:template>
<axsl:template mode="M8" priority="-1" match="text()"/>
<axsl:template mode="M9" priority="4000" match="//gmd:CI_ResponsibleParty">
<axsl:choose>
<axsl:when test="(count(gmd:individualName) + count(gmd:organisationName) + count(gmd:positionName)) &gt; 0"/>
<axsl:otherwise>
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M8"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M9"/>
</axsl:template>
<axsl:template mode="M9" priority="-1" match="text()"/>
<axsl:template mode="M10" priority="4000" match="//gmd:MD_LegalConstraints|//*[gco:isoType='gmd:MD_LegalConstraints']">
<axsl:if test="gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions' and not(gmd:otherConstraints)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M9.access"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:if test="gmd:useConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions' and not(gmd:otherConstraints)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M9.use"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M10"/>
</axsl:template>
<axsl:template mode="M10" priority="-1" match="text()"/>
<axsl:template mode="M11" priority="4000" match="//gmd:MD_Band">
<axsl:if test="(gmd:maxValue or gmd:minValue) and not(gmd:units)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M9"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M11"/>
</axsl:template>
<axsl:template mode="M11" priority="-1" match="text()"/>
<axsl:template mode="M12" priority="4000" match="//gmd:LI_Source">
<axsl:choose>
<axsl:when test="gmd:description or gmd:sourceExtent"/>
<axsl:otherwise>
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M11"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M12"/>
</axsl:template>
<axsl:template mode="M12" priority="-1" match="text()"/>
<axsl:template mode="M13" priority="4000" match="//gmd:DQ_DataQuality">
<axsl:if test="(((count(*/gmd:LI_Lineage/gmd:source) + count(*/gmd:LI_Lineage/gmd:processStep)) = 0)                      and (gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode/@codeListValue='dataset'                              or gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode/@codeListValue='series'))                      and not(gmd:lineage/gmd:LI_Lineage/gmd:statement)                      and (gmd:lineage)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M13"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M13"/>
</axsl:template>
<axsl:template mode="M13" priority="-1" match="text()"/>
<axsl:template mode="M14" priority="4000" match="//gmd:LI_Lineage">
<axsl:if test="not(gmd:source) and not(gmd:statement) and not(gmd:processStep)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M14"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M14"/>
</axsl:template>
<axsl:template mode="M14" priority="-1" match="text()"/>
<axsl:template mode="M15" priority="4000" match="//gmd:LI_Lineage">
<axsl:if test="not(gmd:processStep) and not(gmd:statement) and not(gmd:source)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M15"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M15"/>
</axsl:template>
<axsl:template mode="M15" priority="-1" match="text()"/>
<axsl:template mode="M16" priority="4000" match="//gmd:DQ_DataQuality">
<axsl:if test="gmd:scope/gmd:DQ_Scope/gmd:level/gmd:MD_ScopeCode/@codeListValue='dataset'                                  and not(gmd:report)                                  and not(gmd:lineage)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M16"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M16"/>
</axsl:template>
<axsl:template mode="M16" priority="-1" match="text()"/>
<axsl:template mode="M17" priority="4000" match="//gmd:DQ_Scope">
<axsl:choose>
<axsl:when test="gmd:level/gmd:MD_ScopeCode/@codeListValue='dataset'                                  or gmd:level/gmd:MD_ScopeCode/@codeListValue='series'                                  or (gmd:levelDescription and ((normalize-space(gmd:levelDescription) != '')                                  or (gmd:levelDescription/gmd:MD_ScopeDescription)                                  or (gmd:levelDescription/@gco:nilReason                                  and contains('inapplicable missing template unknown withheld',gmd:levelDescription/@gco:nilReason))))"/>
<axsl:otherwise>
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M17"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M17"/>
</axsl:template>
<axsl:template mode="M17" priority="-1" match="text()"/>
<axsl:template mode="M18" priority="4000" match="//gmd:MD_Medium">
<axsl:if test="gmd:density and not(gmd:densityUnits)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M18"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M18"/>
</axsl:template>
<axsl:template mode="M18" priority="-1" match="text()"/>
<axsl:template mode="M19" priority="4000" match="//gmd:MD_Distribution">
<axsl:choose>
<axsl:when test="count(gmd:distributionFormat)&gt;0                              or count(gmd:distributor/gmd:MD_Distributor/gmd:distributorFormat)&gt;0"/>
<axsl:otherwise>
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M19"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M19"/>
</axsl:template>
<axsl:template mode="M19" priority="-1" match="text()"/>
<axsl:template mode="M20" priority="4000" match="//gmd:EX_Extent">
<axsl:choose>
<axsl:when test="count(gmd:description)&gt;0                              or count(gmd:geographicElement)&gt;0                              or count(gmd:temporalElement)&gt;0                              or count(gmd:verticalElement)&gt;0"/>
<axsl:otherwise>
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M20"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M20"/>
</axsl:template>
<axsl:template mode="M20" priority="-1" match="text()"/>
<axsl:template mode="M21" priority="4000" match="//gmd:MD_DataIdentification|//*[gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="(not(../../gmd:hierarchyLevel) or ../../gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='dataset')                      and (count(gmd:extent/*/gmd:geographicElement/gmd:EX_GeographicBoundingBox)                          + count (gmd:extent/*/gmd:geographicElement/gmd:EX_GeographicDescription))=0 ">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M21"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M21"/>
</axsl:template>
<axsl:template mode="M21" priority="-1" match="text()"/>
<axsl:template mode="M22" priority="4000" match="//gmd:MD_DataIdentification|//*[gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="(not(../../gmd:hierarchyLevel) or ../../gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='dataset'                      or ../../gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='series')                     and not(gmd:topicCategory)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M6"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M22"/>
</axsl:template>
<axsl:template mode="M22" priority="-1" match="text()"/>
<axsl:template mode="M23" priority="4000" match="//gmd:MD_AggregateInformation">
<axsl:choose>
<axsl:when test="gmd:aggregateDataSetName or gmd:aggregateDataSetIdentifier"/>
<axsl:otherwise>
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M22"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M23"/>
</axsl:template>
<axsl:template mode="M23" priority="-1" match="text()"/>
<axsl:template mode="M24" priority="4000" match="//gmd:MD_Metadata/gmd:language|//*[@gco:isoType='gmd:MD_Metadata']/gmd:language">
<axsl:choose>
<axsl:when test=". and ((normalize-space(.) != '')                      or (normalize-space(./gco:CharacterString) != '')                      or (./gmd:LanguageCode)                      or (./@gco:nilReason                          and contains('inapplicable missing template unknown withheld',./@gco:nilReason)))"/>
<axsl:otherwise>
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M23"/>
</geonet:diagnostics>
</geonet:errorFound>
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
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M26.obligation"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:otherwise>
</axsl:choose>
<axsl:choose>
<axsl:when test="(gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelist' or gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='enumeration' or gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelistElement') or (gmd:maximumOccurrence and ((normalize-space(gmd:maximumOccurrence) != '')  or (normalize-space(gmd:maximumOccurrence/gco:CharacterString) != '') or (gmd:maximumOccurrence/@gco:nilReason and contains('inapplicable missing template unknown withheld',gmd:maximumOccurrence/@gco:nilReason))))"/>
<axsl:otherwise>
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M26.minimumOccurence"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:otherwise>
</axsl:choose>
<axsl:choose>
<axsl:when test="(gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelist' or gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='enumeration' or gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelistElement') or (gmd:domainValue and ((normalize-space(gmd:domainValue) != '')  or (normalize-space(gmd:domainValue/gco:CharacterString) != '') or (gmd:domainValue/@gco:nilReason and contains('inapplicable missing template unknown withheld',gmd:domainValue/@gco:nilReason))))"/>
<axsl:otherwise>
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M26.domainValue"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:otherwise>
</axsl:choose>
<axsl:apply-templates mode="M26"/>
</axsl:template>
<axsl:template mode="M26" priority="-1" match="text()"/>
<axsl:template mode="M27" priority="4000" match="//gmd:MD_ExtendedElementInformation">
<axsl:if test="gmd:obligation/gmd:MD_ObligationCode='conditional' and not(gmd:condition)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M27"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M27"/>
</axsl:template>
<axsl:template mode="M27" priority="-1" match="text()"/>
<axsl:template mode="M28" priority="4000" match="//gmd:MD_ExtendedElementInformation">
<axsl:if test="gmd:dataType/gmd:MD_DatatypeCode/@codeListValue='codelistElement' and not(gmd:domainCode)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M28"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M28"/>
</axsl:template>
<axsl:template mode="M28" priority="-1" match="text()"/>
<axsl:template mode="M29" priority="4000" match="//gmd:MD_ExtendedElementInformation">
<axsl:if test="gmd:dataType/gmd:MD_DatatypeCode/@codeListValue!='codelistElement' and not(gmd:shortName)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M29"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M29"/>
</axsl:template>
<axsl:template mode="M29" priority="-1" match="text()"/>
<axsl:template mode="M30" priority="4000" match="//gmd:MD_Georectified">
<axsl:if test="(gmd:checkPointAvailability/gco:Boolean='1' or gmd:checkPointAvailability/gco:Boolean='true') and not(gmd:checkPointDescription)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M30"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M30"/>
</axsl:template>
<axsl:template mode="M30" priority="-1" match="text()"/>
<axsl:template mode="M31" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="not(gmd:language)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M31"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M31"/>
</axsl:template>
<axsl:template mode="M31" priority="-1" match="text()"/>
<axsl:template mode="M32" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="not(gmd:characterSet)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M32"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M32"/>
</axsl:template>
<axsl:template mode="M32" priority="-1" match="text()"/>
<axsl:template mode="M33" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="not(gmd:hierarchyLevel)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M33"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M33"/>
</axsl:template>
<axsl:template mode="M33" priority="-1" match="text()"/>
<axsl:template mode="M34" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="not(gmd:hierarchyLevelName)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M34"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M34"/>
</axsl:template>
<axsl:template mode="M34" priority="-1" match="text()"/>
<axsl:template mode="M35" priority="4000" match="//gmd:citation">
<axsl:if test="not(*/gmd:title) or (*/gmd:title/@gco:nilReason)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M35"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M35"/>
</axsl:template>
<axsl:template mode="M35" priority="-1" match="text()"/>
<axsl:template mode="M36" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:abstract) or (gmd:abstract/@gco:nilReason)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M36"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M36"/>
</axsl:template>
<axsl:template mode="M36" priority="-1" match="text()"/>
<axsl:template mode="M37" priority="4000" match="//gmd:MD_Metadata/gmd:hierarchyLevel">
<axsl:if test="not(.) or (gmd:MD_ScopeCode/@codeListValue='')">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M37"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M37"/>
</axsl:template>
<axsl:template mode="M37" priority="-1" match="text()"/>
<axsl:template mode="M38" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:citation/*/gmd:identifier/*/gmd:code) or (gmd:citation/*/gmd:identifier/*/gmd:code/@gco:nilReason)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M38"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M38"/>
</axsl:template>
<axsl:template mode="M38" priority="-1" match="text()"/>
<axsl:template mode="M39" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:topicCategory) or (normalize-space(gmd:topicCategory/gmd:MD_TopicCategoryCode) = '')">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M39"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M39"/>
</axsl:template>
<axsl:template mode="M39" priority="-1" match="text()"/>
<axsl:template mode="M40" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:descriptiveKeywords) or (gmd:descriptiveKeywords/*/gmd:keyword/@gco:nilReason) or (normalize-space(gmd:descriptiveKeywords/*/gmd:keyword/gco:CharacterString)='')">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M40"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M40"/>
</axsl:template>
<axsl:template mode="M40" priority="-1" match="text()"/>
<axsl:template mode="M41" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not((gmd:extent/*/gmd:geographicElement/*/gmd:westBoundLongitude) and (gmd:extent/*/gmd:geographicElement/*/gmd:eastBoundLongitude)  and (gmd:extent/*/gmd:geographicElement/*/gmd:southBoundLatitude) and (gmd:extent/*/gmd:geographicElement/*/gmd:northBoundLatitude))  or not(normalize-space(gmd:extent/*/gmd:geographicElement/*/gmd:westBoundLongitude/gco:Decimal)) or not(normalize-space(gmd:extent/*/gmd:geographicElement/*/gmd:northBoundLatitude/gco:Decimal))  or not(normalize-space(gmd:extent/*/gmd:geographicElement/*/gmd:eastBoundLongitude/gco:Decimal)) or not(normalize-space(gmd:extent/*/gmd:geographicElement/*/gmd:southBoundLatitude/gco:Decimal))">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M41"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M41"/>
</axsl:template>
<axsl:template mode="M41" priority="-1" match="text()"/>
<axsl:template mode="M42" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(normalize-space(gmd:citation//gmd:date//gmd:date/gco:DateTime)) and not(gmd:extent//gmd:temporalElement//gmd:extent/gml:TimePeriod) and not(gmd:extent//gmd:temporalElement//gmd:extent/gml:TimeInstant)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M42"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M42"/>
</axsl:template>
<axsl:template mode="M42" priority="-1" match="text()"/>
<axsl:template mode="M43" priority="4000" match="//gmd:DQ_DataQuality">
<axsl:if test="not(gmd:lineage//gmd:statement) or (gmd:lineage//gmd:statement/@gco:nilReason)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M43"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M43"/>
</axsl:template>
<axsl:template mode="M43" priority="-1" match="text()"/>
<axsl:template mode="M44" priority="4000" match="//gmd:dataQualityInfo/gmd:DQ_DataQuality">
<axsl:if test="not(gmd:report//gmd:result) or (gmd:report//gmd:result//gmd:specification//gmd:title/@gco:nilReason)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M44"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M44"/>
</axsl:template>
<axsl:template mode="M44" priority="-1" match="text()"/>
<axsl:template mode="M45" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:resourceConstraints/*/gmd:useLimitation) or (gmd:resourceConstraints/*/gmd:useLimitation/@gco:nilReason)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M45"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M45"/>
</axsl:template>
<axsl:template mode="M45" priority="-1" match="text()"/>
<axsl:template mode="M46" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="(not(gmd:resourceConstraints/*/gmd:accessConstraints) or (gmd:resourceConstraints/*/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='')) and (not(gmd:resourceConstraints/*/gmd:classification) or (gmd:resourceConstraints/*/gmd:classification/gmd:MD_ClassificationCode/@codeListValue='')) and (not(gmd:resourceConstraints/*/gmd:otherConstraints) or (gmd:resourceConstraints/*/gmd:otherConstraints/@gco:nilReason))">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M46"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M46"/>
</axsl:template>
<axsl:template mode="M46" priority="-1" match="text()"/>
<axsl:template mode="M47" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(gmd:pointOfContact/*/gmd:organisationName) or (gmd:pointOfContact/*/gmd:organisationName/@gco:nilReason) or not(gmd:pointOfContact/*/gmd:contactInfo/*/gmd:address/*/gmd:electronicMailAddress) or (gmd:pointOfContact/*/gmd:contactInfo/*/gmd:address/*/gmd:electronicMailAddress/@gco:nilReason)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M47"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M47"/>
</axsl:template>
<axsl:template mode="M47" priority="-1" match="text()"/>
<axsl:template mode="M48" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="not(gmd:contact/gmd:CI_ResponsibleParty/gmd:organisationName) or (gmd:contact/gmd:CI_ResponsibleParty/gmd:organisationName/@gco:nilReason) or not(gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress) or (gmd:contact/gmd:CI_ResponsibleParty/gmd:contactInfo/gmd:CI_Contact/gmd:address/gmd:CI_Address/gmd:electronicMailAddress/@gco:nilReason))">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M48"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M48"/>
</axsl:template>
<axsl:template mode="M48" priority="-1" match="text()"/>
<axsl:template mode="M49" priority="4000" match="//gmd:MD_Metadata">
<axsl:if test="(not(gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='service') and (not(gmd:language) or (gmd:language/@gco:nilReason)))">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M49"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M49"/>
</axsl:template>
<axsl:template mode="M49" priority="-1" match="text()"/>
<axsl:template mode="M50" priority="4000" match="//gmd:MD_Metadata|//*[gco:isoType='gmd:MD_Metadata']">
<axsl:if test="not(gmd:dateStamp)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M50"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M50"/>
</axsl:template>
<axsl:template mode="M50" priority="-1" match="text()"/>
<axsl:template mode="M51" priority="4000" match="//gmd:identificationInfo">
<axsl:if test="(../../gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='service') and not(*/srv:operatesOn)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M51"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M51"/>
</axsl:template>
<axsl:template mode="M51" priority="-1" match="text()"/>
<axsl:template mode="M52" priority="4000" match="//gmd:distributionInfo">
<axsl:if test="(not(*/gmd:transferOptions/*/gmd:onLine/*/gmd:linkage) or normalize-space(*/gmd:transferOptions/*/gmd:onLine/*/gmd:linkage/gmd:URL)='')">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M52"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M52"/>
</axsl:template>
<axsl:template mode="M52" priority="-1" match="text()"/>
<axsl:template mode="M53" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="count(gmd:resourceConstraints/gmd:MD_Constraints/gmd:useLimitation) &gt;1 or                  count(gmd:resourceConstraints/gmd:MD_LegalConstraints/gmd:useLimitation) &gt;1 or                  count(gmd:resourceConstraints/gmd:MD_SecurityConstraints/gmd:useLimitation) &gt;1 or                  count(gmd:resourceConstraints/*[gco:isoType='gmd:MD_Constraints']/gmd:useLimitation) &gt;1 or                  count(gmd:resourceConstraints/*[gco:isoType='gmd:MD_LegalConstraints']/gmd:useLimitation) &gt;1 or                  count(gmd:resourceConstraints/*[gco:isoType='gmd:MD_SecurityConstraints']/gmd:useLimitation) &gt;1">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M53"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M53"/>
</axsl:template>
<axsl:template mode="M53" priority="-1" match="text()"/>
<axsl:template mode="M54" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="(gmd:resourceConstraints/*/gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue='otherRestrictions') and                  not(gmd:resourceConstraints/*/gmd:otherConstraints)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M54"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M54"/>
</axsl:template>
<axsl:template mode="M54" priority="-1" match="text()"/>
<axsl:template mode="M55" priority="4000" match="//gmd:MD_DataIdentification|//*[@gco:isoType='gmd:MD_DataIdentification']">
<axsl:if test="not(../../gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='service') and (not(gmd:language) or (gmd:language/@gco:nilReason))">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M55"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M55"/>
</axsl:template>
<axsl:template mode="M55" priority="-1" match="text()"/>
<axsl:template mode="M56" priority="4000" match="//gmd:identificationInfo">
<axsl:if test="not(*/gmd:spatialResolution/*/gmd:equivalentScale) and not(*/gmd:spatialResolution/*/gmd:distance)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M56"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M56"/>
</axsl:template>
<axsl:template mode="M56" priority="-1" match="text()"/>
<axsl:template mode="M57" priority="4000" match="//srv:SV_ServiceIdentification">
<axsl:if test="not(srv:containsOperations/srv:SV_OperationMetadata/srv:operationName)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M57"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M57"/>
</axsl:template>
<axsl:template mode="M57" priority="-1" match="text()"/>
<axsl:template mode="M58" priority="4000" match="//srv:SV_ServiceIdentification">
<axsl:if test="not(srv:containsOperations/srv:SV_OperationMetadata/srv:DCP)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M58"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M58"/>
</axsl:template>
<axsl:template mode="M58" priority="-1" match="text()"/>
<axsl:template mode="M59" priority="4000" match="//srv:SV_ServiceIdentification">
<axsl:if test="not(srv:containsOperations/srv:SV_OperationMetadata/srv:connectPoint)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M59"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M59"/>
</axsl:template>
<axsl:template mode="M59" priority="-1" match="text()"/>
<axsl:template mode="M60" priority="4000" match="//srv:SV_ServiceIdentification">
<axsl:if test="not(srv:serviceType)">
<geonet:errorFound ref="#_{geonet:element/@ref}">
<geonet:pattern name="{name(.)}"/>
<geonet:diagnostics>
<xsl:value-of xmlns:xsl="http://www.w3.org/1999/XSL/Transform" select="$loc/strings/alert.M60"/>
</geonet:diagnostics>
</geonet:errorFound>
</axsl:if>
<axsl:apply-templates mode="M60"/>
</axsl:template>
<axsl:template mode="M60" priority="-1" match="text()"/>
<axsl:template priority="-1" match="text()"/>
</axsl:stylesheet>
