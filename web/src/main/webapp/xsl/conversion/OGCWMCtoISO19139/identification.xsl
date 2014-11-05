<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:gmd ="http://www.isotc211.org/2005/gmd"
							  xmlns:wmc="http://www.opengis.net/context"
							  xmlns:wmc11="http://www.opengeospatial.net/context"							  
							  xmlns:gco="http://www.isotc211.org/2005/gco"
							  xmlns:gts="http://www.isotc211.org/2005/gts"
							  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
							  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
							  xmlns:xlink="http://www.w3.org/1999/xlink"
							  xmlns:math="http://exslt.org/math"
							  extension-element-prefixes="math">

	<!-- ============================================================================= -->

	<xsl:template match="*" mode="DataIdentification">
		<xsl:param name="topic"/>
		<xsl:param name="lang"/>
		
		<gmd:citation>
			<gmd:CI_Citation>
				<gmd:title>
					<gco:CharacterString><xsl:value-of select="/wmc:ViewContext/wmc:General/wmc:Title
						|/wmc11:ViewContext/wmc11:General/wmc11:Title"/></gco:CharacterString>
				</gmd:title>
				<!-- date is mandatory -->
				<xsl:variable name="df">[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01]</xsl:variable>
				<gmd:date>
					<gmd:CI_Date>
						<gmd:date>
							<gco:DateTime><xsl:value-of select="format-dateTime(current-dateTime(),$df)"/></gco:DateTime>
						</gmd:date>
						<gmd:dateType>
							<gmd:CI_DateTypeCode codeListValue="publication"
								codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/ML_gmxCodelists.xml#CI_DateTypeCode" />
						</gmd:dateType>
					</gmd:CI_Date>
				</gmd:date>	
			</gmd:CI_Citation>
		</gmd:citation>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<gmd:abstract>
			<gco:CharacterString><xsl:value-of select="/wmc:ViewContext/wmc:General/wmc:Abstract
				|/wmc11:ViewContext/wmc11:General/wmc11:Abstract"/></gco:CharacterString>
		</gmd:abstract>

		<!--idPurp-->
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<gmd:status>
			<gmd:MD_ProgressCode codeList="./resources/codeList.xml#MD_ProgressCode" codeListValue="completed" />
		</gmd:status>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<xsl:for-each select="/wmc:ViewContext/wmc:General/wmc:ContactInformation
			|/wmc11:ViewContext/wmc11:General/wmc11:ContactInformation">
			<gmd:pointOfContact>
				<gmd:CI_ResponsibleParty>
					<xsl:apply-templates select="." mode="RespParty"/>
				</gmd:CI_ResponsibleParty>
			</gmd:pointOfContact>
		</xsl:for-each>

		<!-- resMaint -->
		<!-- graphOver -->
		<!-- dsFormat-->
		
		<xsl:for-each select="/wmc:ViewContext/wmc:General/wmc:KeywordList
			|/wmc11:ViewContext/wmc11:General/wmc11:KeywordList">
			<gmd:descriptiveKeywords>
				<gmd:MD_Keywords>
					<xsl:apply-templates select="." mode="Keywords"/>
				</gmd:MD_Keywords>
			</gmd:descriptiveKeywords>
		</xsl:for-each>
		
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
		<gmd:language>
            <gmd:LanguageCode codeList="http://www.loc.gov/standards/iso639-2/" codeListValue="$lang"/>
        </gmd:language>
		
		<gmd:topicCategory>
			<gmd:MD_TopicCategoryCode><xsl:value-of select="$topic"/></gmd:MD_TopicCategoryCode>
		</gmd:topicCategory>

	</xsl:template>



	<!-- ============================================================================= -->
	<!-- === Keywords === -->
	<!-- ============================================================================= -->

	<xsl:template match="*" mode="Keywords">

		<xsl:for-each select="Keyword">
			<gmd:keyword>
				<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
			</gmd:keyword>
		</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<gmd:type>
			<gmd:MD_KeywordTypeCode codeList="./resources/codeList.xml#MD_KeywordTypeCode" codeListValue="theme" />
		</gmd:type>

	</xsl:template>

	<!-- ============================================================================= -->

</xsl:stylesheet>
