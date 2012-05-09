<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	exclude-result-prefixes="fo"
	xmlns:gmd="http://www.isotc211.org/2005/gmd"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:gml="http://www.opengis.net/gml"
	xmlns:gts="http://www.isotc211.org/2005/gts"
	xmlns:gco="http://www.isotc211.org/2005/gco"
	xmlns:geonet="http://www.fao.org/geonetwork"
	xmlns:exslt="http://exslt.org/common">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="no"
		indent="yes" />

	<xsl:include href="../../utils.xsl" />
	<xsl:include href="../../metadata-fop.xsl" />
	<xsl:include href="../../metadata-fop-utils.xsl" />

	<xsl:variable name="server" select="concat('http://', /root/gui/env/server/host, ':', /root/gui/env/server/port)"/>
	<xsl:variable name="siteURL" select="substring-before(/root/gui/siteURL, '/srv')"/>

	<xsl:variable name="geonetNodeSet">
		<geonet:dummy />
	</xsl:variable>

	<xsl:variable name="geonetUri">
		<xsl:value-of
			select="namespace-uri(exslt:node-set($geonetNodeSet)/*)" />
	</xsl:variable>

	<xsl:variable name="currTab">
		<xsl:choose>
			<xsl:when test="/root/gui/currTab">
				<xsl:value-of select="/root/gui/currTab" />
			</xsl:when>
			<xsl:otherwise>simple</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template mode="schema" match="*">
		<xsl:choose>
			<xsl:when test="string(geonet:info/schema)!=''">
				<xsl:value-of select="geonet:info/schema" />
			</xsl:when>
			<xsl:when test="name(.)='Metadata'">iso19115</xsl:when>
			<xsl:when test="local-name(.)='MD_Metadata'">iso19139</xsl:when>
			<xsl:when test="name(.)='metadata'">fgdc-std</xsl:when>
			<xsl:otherwise>UNKNOWN</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
	<!-- main schema switch -->
	<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

	<xsl:template mode="elementEP" match="*|@*">
		<xsl:param name="schema"/>
		
		<xsl:if test="$schema=''">
			<xsl:apply-templates mode="schema" select="." />
		</xsl:if>
		
		<xsl:choose>
            <!-- ISO 19139 and profils -->
			<xsl:when test="contains($schema,'iso19139')">
                <xsl:apply-templates mode="iso19139" select=".">
                    <xsl:with-param name="schema" select="$schema" />
                </xsl:apply-templates>
            </xsl:when>
			<!-- default, no schema-specific formatting -->
			<xsl:otherwise>
				<xsl:apply-templates mode="element" select=".">
					<xsl:with-param name="schema" select="$schema" />
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>


	<!-- Metadata 19139 -->
	<xsl:template mode="iso19139" match="MD_Metadata|*[@gco:isoType='gmd:MD_Metadata']">
        <xsl:param name="schema" />
            <fo:block>ISO19139 pdf output</fo:block>
        <xsl:call-template name="iso19139SimplePdf">
            <xsl:with-param name="schema" select="$schema" />
        </xsl:call-template> 
    </xsl:template>


	
	
	<xsl:template mode="iso19139" match="*|@*">
		<xsl:param name="schema" />
		<xsl:variable name="empty">
			<xsl:apply-templates mode="iso19139IsEmpty" select="."/>
		</xsl:variable>
		
		<xsl:if test="$empty!=''">
			<xsl:apply-templates mode="element" select=".">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="geonetUri" select="$geonetUri"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>
	
	<!-- ===================================================================== -->
	<!-- these elements should be boxed -->
	<!-- ===================================================================== -->
	
	<xsl:template mode="iso19139" match="gmd:graphicOverview"/>
	<xsl:template mode="iso19139" match="gmd:identificationInfo|gmd:distributionInfo|gmd:descriptiveKeywords|gmd:spatialRepresentationInfo|gmd:pointOfContact|gmd:dataQualityInfo|gmd:referenceSystemInfo|gmd:equivalentScale|gmd:projection|gmd:ellipsoid|gmd:extent[name(..)!='gmd:EX_TemporalExtent']|gmd:geographicBox|gmd:EX_TemporalExtent|gmd:MD_Distributor">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:apply-templates mode="complexElement" select=".">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
		</xsl:apply-templates>
	</xsl:template>
	
	
	<!-- ============================================================================= -->
	<!-- utilities -->
	<!-- ============================================================================= -->
	
	<xsl:template mode="iso19139IsEmpty" match="*|@*|text()">
		<xsl:choose>
			<!-- normal element -->
			<xsl:when test="*">
				<xsl:apply-templates mode="iso19139IsEmpty"/>
			</xsl:when>
			<!-- text element -->
			<xsl:when test="text()!=''">txt</xsl:when>
			<!-- empty element -->
			<xsl:otherwise>
				<!-- attributes? -->
				<xsl:for-each select="@*">
					<xsl:if test="string-length(.)!=0">att</xsl:if>
				</xsl:for-each>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="iso19139SimplePdf">
        <xsl:param name="schema" />

        <xsl:apply-templates mode="elementEP"
        	select="gmd:MD_DataIdentification|*[@gco:isoType='gmd:MD_DataIdentification']">
            <xsl:with-param name="schema" select="$schema" />
        </xsl:apply-templates>
		
	</xsl:template>

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			<fo:layout-master-set>
				<fo:simple-page-master master-name="simpleA4"
					page-height="29.7cm" page-width="21cm" margin-top="1cm"
					margin-bottom="2cm" margin-left="1cm" margin-right="1cm">
					<fo:region-body />
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="simpleA4">
				<fo:flow flow-name="xsl-region-body">

					<!-- Banner level -->
					<xsl:call-template name="banner" />


					<fo:block font-size="10pt">
						<xsl:call-template name="content" />
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>

	</xsl:template>

	<!--
		page content
	-->
	<xsl:template name="content">
		<xsl:for-each
			select="/root/*[name(.)!='gui' and name(.)!='request']">
            
            <xsl:variable name="id" select="geonet:info/id"/>
			<xsl:variable name="schema" select="geonet:info/schema"/>
			
			<fo:table width="100%" table-layout="fixed">
				<fo:table-column column-width="3cm" />
				<fo:table-column column-width="8cm" />
				<fo:table-column column-width="8cm" />
				<fo:table-body>
		
					<fo:table-row background-color="#eeeeee" border-bottom="1pt solid black">					
						<fo:table-cell>
							<fo:block font-size="10pt" font-weight="bold" vertical-align="middle">
								<xsl:variable name="source" select="string(./geonet:info/source)"/>
												
                                <fo:external-graphic content-width="40pt">
									<xsl:attribute name="src"><xsl:text>url('</xsl:text><xsl:value-of
											select="concat($server, /root/gui/url, '/images/', /root/gui/env/site/theme, '/logos/', $source, '.gif')" /><xsl:text>')"</xsl:text></xsl:attribute>
								</fo:external-graphic>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block font-size="10pt" font-weight="bold" vertical-align="middle">
								<xsl:value-of
									select="./gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString|
									./gmd:identificationInfo/*[@gco:isoType='gmd:MD_DataIdentification']/gmd:citation/gmd:CI_Citation/gmd:title/gco:CharacterString" />
							</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block text-align="right">
								<xsl:call-template name="thumbnail">
									<xsl:with-param name="server" select="$server"/>
								</xsl:call-template>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row height="10pt">
						<fo:table-cell>
							<fo:block/>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block/>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block/>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>

			<fo:table width="100%" table-layout="fixed">
				<fo:table-column column-width="3cm" />
				<fo:table-column column-width="8cm" />
				<fo:table-column column-width="8cm" />
				<fo:table-body>
				
					<xsl:apply-templates mode="elementEP">
						<xsl:with-param name="schema" select="$schema"/>
					</xsl:apply-templates>
                    
				</fo:table-body>
			</fo:table>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
