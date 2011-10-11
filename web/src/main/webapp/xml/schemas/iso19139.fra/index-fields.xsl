<?xml version="1.0" encoding="UTF-8" ?>

<xsl:stylesheet version="1.0" xmlns:gmd="http://www.isotc211.org/2005/gmd"
										xmlns:gco="http://www.isotc211.org/2005/gco"
										xmlns:gml="http://www.opengis.net/gml"
										xmlns:srv="http://www.isotc211.org/2005/srv"
                                        xmlns:fra="http://www.cnig.gouv.fr/2005/fra"
										xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                                        xmlns:skos="http://www.w3.org/2004/02/skos/core#">

	<!-- This file defines what parts of the metadata are indexed by Lucene
	     Searches can be conducted on indexes defined here. 
	     The Field@name attribute defines the name of the search variable.
		 If a variable has to be maintained in the user session, it needs to be 
		 added to the GeoNetwork constants in the Java source code.
		 Please keep indexes consistent among metadata standards if they should
		 work accross different metadata resources -->
	<!-- ========================================================================================= -->
	
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

	<!-- ========================================================================================= -->

    <xsl:template match="/">
		<Document>
			<xsl:apply-templates select="gmd:MD_Metadata" mode="metadata"/>
		</Document>
	</xsl:template>
	
	<!-- ========================================================================================= -->

	<xsl:template match="*" mode="metadata">

		<!-- === Data or Service Identification === -->		
        

		<!-- the double // here seems needed to index FRA_DataIdentification when
           it is nested in a SV_ServiceIdentification class -->

		<xsl:for-each select="gmd:identificationInfo/fra:FRA_DataIdentification|gmd:identificationInfo/srv:SV_ServiceIdentification">

			<xsl:for-each select="gmd:citation/gmd:CI_Citation">
				<xsl:for-each select="gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString">
					<Field name="identifier" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>
	
				<xsl:for-each select="gmd:title/gco:CharacterString">
					<Field name="title" string="{string(.)}" store="true" index="true" token="true"/>
                    <!-- not tokenized title for sorting -->
                    <Field name="_title" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>
	
				<xsl:for-each select="gmd:alternateTitle/gco:CharacterString">
					<Field name="altTitle" string="{string(.)}" store="true" index="true" token="true"/>
				</xsl:for-each>

				<xsl:for-each select="gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='revision']/gmd:date/gco:Date">
					<Field name="revisionDate" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>

				<xsl:for-each select="gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='creation']/gmd:date/gco:Date">
					<Field name="createDate" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>

				<xsl:for-each select="gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='publication']/gmd:date/gco:Date">
					<Field name="publicationDate" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>

				<!-- fields used to search for metadata in paper or digital format -->

				<xsl:for-each select="gmd:presentationForm">
					<xsl:if test="contains(gmd:CI_PresentationFormCode/@codeListValue, 'Digital')">
						<Field name="digital" string="true" store="true" index="true" token="false"/>
					</xsl:if>
				
					<xsl:if test="contains(gmd:CI_PresentationFormCode/@codeListValue, 'Hardcopy')">
						<Field name="paper" string="true" store="true" index="true" token="false"/>
					</xsl:if>
				</xsl:for-each>
			</xsl:for-each>

			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->		
	
			<xsl:for-each select="gmd:abstract/gco:CharacterString">
				<Field name="abstract" string="{string(.)}" store="true" index="true" token="true"/>
			</xsl:for-each>

			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->		

			<xsl:for-each select="*/gmd:EX_Extent">
				<xsl:apply-templates select="gmd:geographicElement/gmd:EX_GeographicBoundingBox" mode="latLon"/>

				<xsl:for-each select="gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code/gco:CharacterString">
					<Field name="geoDescCode" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>

				<xsl:for-each select="gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent|
							gmd:temporalElement/gmd:EX_SpatialTemporalExtent/gmd:extent">
					<xsl:for-each select="gml:TimePeriod/gml:beginPosition">
						<Field name="tempExtentBegin" string="{string(.)}" store="true" index="true" token="false"/>
					</xsl:for-each>

					<xsl:for-each select="gml:TimePeriod/gml:endPosition">
						<Field name="tempExtentEnd" string="{string(.)}" store="true" index="true" token="false"/>
					</xsl:for-each>

					<xsl:for-each select="gml:TimePeriod/gml:begin/gml:TimeInstant/gml:timePosition">
						<Field name="tempExtentBegin" string="{string(.)}" store="true" index="true" token="false"/>
					</xsl:for-each>

					<xsl:for-each select="gml:TimePeriod/gml:end/gml:TimeInstant/gml:timePosition">
						<Field name="tempExtentEnd" string="{string(.)}" store="true" index="true" token="false"/>
					</xsl:for-each>

				</xsl:for-each>
			</xsl:for-each>


			</xsl:for-each>
			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
				
			<!--  PMT C2C GeoBretagne
				There seems to be a bug with saxon XML parser, instead of doing a logical 'or' using pipes
				it concatenates the different fields (same problem as the one we faced with xsl:fop
				export).
				Doing the same as what is currently done on iso19139/index-fields.xsl stylesheet
			 -->

            <xsl:variable name="lower">eoaoeecaaabcdefghijklmnopqrstuvwxyz</xsl:variable>
            <xsl:variable name="upper">ÉÔÂôéèçàâABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>

            <xsl:for-each select="gmd:identificationInfo/fra:FRA_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords">

				<xsl:for-each select="gmd:keyword/gmd:PT_FreeText/gmd:textGroup/gmd:LocalisedCharacterString">
                    <xsl:variable name="keywordLower" select="normalize-space(translate(string(.),$upper,$lower))"/>
                    <Field name="keyword" string="{$keywordLower}" store="true" index="true" token="false"/>
					<Field name="subject" string="{$keywordLower}" store="true" index="true" token="false"/>
					<Field name="subject" string="{string(.)}" store="true" index="true" token="false"/>

					<xsl:if test="string-length(.) &gt; 0">
						<xsl:variable name="inspire-thesaurus"
							select="document('../../codelist/external/thesauri/theme/inspire-theme.rdf')" />
						<xsl:variable name="inspire-theme" select="$inspire-thesaurus//skos:Concept" />
						<xsl:variable name="inspireannex">
							<xsl:call-template name="determineInspireAnnex">
								<xsl:with-param name="keyword" select="string(.)" />
								<xsl:with-param name="inspireThemes" select="$inspire-theme" />
							</xsl:call-template>
						</xsl:variable>
						<Field name="inspiretheme" string="{string(.)}" store="true" index="true" token="false" />
						<Field name="inspireannex" string="{$inspireannex}" store="true" index="true" token="false" />
						<Field name="inspirecat" string="true" store="true" index="true" token="false" />
					</xsl:if>
                </xsl:for-each>

				<xsl:for-each select="gmd:keyword/gco:CharacterString">
                    <xsl:variable name="keywordLower" select="normalize-space(translate(string(.),$upper,$lower))"/>
                    <Field name="keyword" string="{$keywordLower}" store="true" index="true" token="false"/>
					<Field name="subject" string="{$keywordLower}" store="true" index="true" token="false"/>
					<Field name="subject" string="{string(.)}" store="true" index="true" token="false"/>
					<xsl:if test="string-length(.) &gt; 0">
						<xsl:variable name="inspire-thesaurus"
							select="document('../../codelist/external/thesauri/theme/inspire-theme.rdf')" />
						<xsl:variable name="inspire-theme" select="$inspire-thesaurus//skos:Concept" />
						<xsl:variable name="inspireannex">
							<xsl:call-template name="determineInspireAnnex">
								<xsl:with-param name="keyword" select="string(.)" />
								<xsl:with-param name="inspireThemes" select="$inspire-theme" />
							</xsl:call-template>
						</xsl:variable>
						<Field name="inspiretheme" string="{string(.)}" store="true" index="true" token="false" />
						<Field name="inspireannex" string="{$inspireannex}" store="true" index="true" token="false" />
						<Field name="inspirecat" string="true" store="true" index="true" token="false" />
					</xsl:if>
										
                </xsl:for-each>

				<xsl:for-each select="gmd:type/gmd:MD_KeywordTypeCode/@codeListValue">
					<Field name="keywordType" string="{string(.)}" store="true" index="true" token="true"/>
				</xsl:for-each>
			
			</xsl:for-each>
			
			
			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->		
	
			<xsl:for-each select="gmd:pointOfContact/gmd:CI_ResponsibleParty">
                <xsl:call-template name="contact">
                    <xsl:with-param name="orgName" select="gmd:organisationName/gco:CharacterString"/>
                    <xsl:with-param name="indName" select="gmd:individualName/gco:CharacterString"/>
                    <xsl:with-param name="role" select="gmd:role/gmd:CI_RoleCode/@codeListValue"/>
                </xsl:call-template>
            </xsl:for-each>

			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->		
	
			<xsl:choose>
				<xsl:when test="gmd:resourceConstraints/gmd:MD_SecurityConstraints">
					<Field name="secConstr" string="true" store="true" index="true" token="false"/>
				</xsl:when>
				<xsl:otherwise>
					<Field name="secConstr" string="false" store="true" index="true" token="false"/>
				</xsl:otherwise>
			</xsl:choose>

			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->		
			
			<!--  PMT C2C GeoBretagne : Same problem with Saxon 
				  which does not seem to handle "|" correctly. Doing 2 loops instead
			-->
			<xsl:for-each select="gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString">
				<Field name="subject" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>

			<xsl:for-each select="gmd:topicCategory/gmd:MD_TopicCategoryCode">
				<Field name="subject" string="{string(.)}" store="true" index="true" token="false" />
			</xsl:for-each>
			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
	
			<xsl:for-each select="gmd:topicCategory/gmd:MD_TopicCategoryCode">
                <Field name="topicCat" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>

			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->		
	
			<xsl:for-each select="gmd:language/gco:CharacterString">
				<Field name="datasetLang" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>

			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->		

			<xsl:for-each select="gmd:spatialResolution/gmd:MD_Resolution">
				<xsl:for-each select="gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator/gco:Integer">
					<Field name="denominator" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>

				<xsl:for-each select="gmd:distance/gco:Distance">
					<Field name="distanceVal" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>

				<xsl:for-each select="gmd:distance/gco:Distance/@uom">
					<Field name="distanceUom" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>
			</xsl:for-each>
			
			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
			<!--  Fields use to search on Service -->
			
			<xsl:for-each select="gmd:resourceConstraints">
				<xsl:for-each select="//gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue">
					<Field name="accessConstr" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>
				<xsl:for-each select="//gmd:otherConstraints/gco:CharacterString">
					<Field name="otherConstr" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>
				<xsl:for-each select="//gmd:classification/gmd:MD_ClassificationCode/@codeListValue">
					<Field name="classif" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>
				<xsl:for-each select="//gmd:useLimitation/gco:CharacterString">
					<Field name="conditionApplyingToAccessAndUse" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>
			</xsl:for-each>
			
			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
			<!--  Fields use to search on Service -->
			
			<xsl:for-each select="srv:serviceType/gco:LocalName">
				<Field  name="serviceType" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>
			
			<xsl:for-each select="srv:serviceTypeVersion/gco:CharacterString">
				<Field  name="serviceTypeVersion" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>
			
			<xsl:for-each select="//srv:SV_OperationMetadata/srv:operationName/gco:CharacterString">
				<Field  name="operation" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>
			
			<xsl:for-each select="srv:operatesOn/@uuidref">
				<Field  name="operatesOn" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>
			
			<xsl:for-each select="srv:coupledResource">
				<xsl:for-each select="srv:SV_CoupledResource/srv:identifier/gco:CharacterString">
					<Field  name="operatesOnIdentifier" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>
				
				<xsl:for-each select="srv:SV_CoupledResource/srv:operationName/gco:CharacterString">
					<Field  name="operatesOnName" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>
			</xsl:for-each>
			
			<xsl:for-each select="//srv:SV_CouplingType/srv:code/@codeListValue">
				<Field  name="couplingType" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->		
		<!-- === Distribution === -->		

		<xsl:for-each select="gmd:distributionInfo/gmd:MD_Distribution">
			<xsl:for-each select="gmd:distributionFormat/gmd:MD_Format/gmd:name/gco:CharacterString">
				<Field name="format" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>

			<!-- index online protocol -->
			
			<xsl:for-each select="gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:protocol/gco:CharacterString">
				<Field name="protocol" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>
		</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->		
        <!-- === Service specific stuff === -->

        <!-- Metadata type  -->
        <xsl:choose>
            <xsl:when test="gmd:identificationInfo/srv:SV_ServiceIdentification">
                <Field name="type" string="service" store="true" index="true" token="false"/>
            </xsl:when>
            <!-- <xsl:otherwise>
                ... gmd:FRA_DataIdentification / hierachicalLevel is used and return dataset, serie, ... 
            </xsl:otherwise>-->
        </xsl:choose>

        <!-- Service type -->
        <xsl:choose>
            <xsl:when test="gmd:identificationInfo/srv:SV_ServiceIdentification/srv:serviceType/gco:LocalName">
                <Field name="stype" string="{string(gmd:identificationInfo/srv:SV_ServiceIdentification/srv:serviceType/gco:LocalName)}" store="true" index="true" token="false"/>
            </xsl:when>
            <!-- <xsl:otherwise>
                 Not used if not metadata for services. TODO : We could search for protocols in Distribution section ? 
            </xsl:otherwise>-->
        </xsl:choose>
		
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->      
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->      
		<!-- === Data Quality  === -->
		<xsl:for-each select="gmd:dataQualityInfo/*/gmd:report/*/gmd:result">
			
			<xsl:for-each select="//gmd:pass/gco:Boolean">
				<Field name="degree" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>
			
			<xsl:for-each select="//gmd:specification/*/gmd:title/gco:CharacterString">
				<Field name="specificationTitle" string="{string(.)}" store="true" index="true" token="true"/>
			</xsl:for-each>
			
			<xsl:for-each select="//gmd:specification/*/gmd:date/*/gmd:date/gco:DateTime">
				<Field name="specificationDate" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>
			
			<xsl:for-each select="//gmd:specification/*/gmd:date/*/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue">
				<Field name="specificationDateType" string="{string(.)}" store="true" index="true" token="false"/>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:for-each select="gmd:dataQualityInfo/*/gmd:lineage/*/gmd:statement/gco:CharacterString">
			<Field name="lineage" string="{string(.)}" store="true" index="true" token="false"/>
		</xsl:for-each>

        <!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->      
		<!-- === General stuff === -->
		
		<xsl:choose>
			<xsl:when test="gmd:hierarchyLevel">
				<xsl:for-each select="gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue">
					<Field name="type" string="{string(.)}" store="true" index="true" token="false"/>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<Field name="type" string="dataset" store="true" index="true" token="false"/>
			</xsl:otherwise>
		</xsl:choose>
		
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->      
		
		<xsl:for-each select="gmd:hierarchyLevelName/gco:CharacterString">
			<Field name="levelName" string="{string(.)}" store="true" index="true" token="true"/>
		</xsl:for-each>
		
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->      
		
		<xsl:for-each select="gmd:language/gco:CharacterString">
			<Field name="language" string="{string(.)}" store="true" index="true" token="false"/>
		</xsl:for-each>
		
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->      
		
		<xsl:for-each select="gmd:fileIdentifier/gco:CharacterString">
			<Field name="fileId" string="{string(.)}" store="true" index="true" token="false"/>
		</xsl:for-each>
		
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->      
		
		<xsl:for-each select="gmd:parentIdentifier/gco:CharacterString">
			<Field name="parentUuid" string="{string(.)}" store="true" index="true" token="false"/>
		</xsl:for-each>
		
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
		
		<xsl:for-each select="gmd:dateStamp/gco:DateTime">
			<Field name="changeDate" string="{string(.)}" store="true" index="true" token="false"/>
		</xsl:for-each>
		
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
		
		<xsl:for-each select="gmd:contact/*/gmd:organisationName/gco:CharacterString">
			<Field name="metadataPOC" string="{string(.)}" store="true" index="true" token="false"/>
		</xsl:for-each>
		
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->      
		
		<xsl:for-each select="gmd:contact/gmd:CI_ResponsibleParty">
			<xsl:call-template name="contact">
				<xsl:with-param name="orgName" select="gmd:organisationName/gco:CharacterString"/>
				<xsl:with-param name="indName" select="gmd:individualName/gco:CharacterString"/>
				<xsl:with-param name="role" select="gmd:role/gmd:CI_RoleCode/@codeListValue"/>
			</xsl:call-template>
		</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->		
		<!-- === Reference system info === -->		

		<xsl:for-each select="gmd:referenceSystemInfo/fra:FRA_DirectReferenceSystem">
			<xsl:for-each select="gmd:referenceSystemIdentifier/gmd:RS_Identifier">
				<xsl:variable name="crs" select="concat(string(gmd:codeSpace/gco:CharacterString),'::',string(gmd:code/gco:CharacterString))"/>

				<xsl:if test="$crs != '::'">
					<Field name="crs" string="{$crs}" store="true" index="true" token="false"/>
				</xsl:if>
			</xsl:for-each>
		</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->		
		<!-- === Free text search === -->		

		<Field name="any" store="false" index="true" token="true">
			<xsl:attribute name="string">
				<xsl:apply-templates select="." mode="allText"/>
			</xsl:attribute>
		</Field>

		<xsl:apply-templates select="." mode="codeList"/>
		
	</xsl:template>

	<!-- ========================================================================================= -->
	<!-- codelist element, indexed, not stored nor tokenized -->
	
	<xsl:template match="*[./*/@codeListValue]" mode="codeList">
		<xsl:param name="name" select="name(.)"/>
		
		<Field name="{$name}" string="{*/@codeListValue}" store="false" index="true" token="false"/>		
	</xsl:template>

	<!-- ========================================================================================= -->
	
	<xsl:template match="*" mode="codeList">
		<xsl:apply-templates select="*" mode="codeList"/>
	</xsl:template>
	
	<!-- ========================================================================================= -->
	<!-- latlon coordinates + 360, zero-padded, indexed, not stored, not tokenized -->
	
	<xsl:template match="*" mode="latLon">
	
		<xsl:for-each select="gmd:westBoundLongitude">
			<Field name="westBL" string="{string(gco:Decimal) + 360}" store="true" index="true" token="false"/>
		</xsl:for-each>
	
		<xsl:for-each select="gmd:southBoundLatitude">
			<Field name="southBL" string="{string(gco:Decimal) + 360}" store="true" index="true" token="false"/>
		</xsl:for-each>
	
		<xsl:for-each select="gmd:eastBoundLongitude">
			<Field name="eastBL" string="{string(gco:Decimal) + 360}" store="true" index="true" token="false"/>
		</xsl:for-each>
	
		<xsl:for-each select="gmd:northBoundLatitude">
			<Field name="northBL" string="{string(gco:Decimal) + 360}" store="true" index="true" token="false"/>
		</xsl:for-each>
	
	</xsl:template>

	<!-- ========================================================================================= -->
	<!--allText -->
	
	<xsl:template match="*" mode="allText">
		<xsl:for-each select="@*">
			<xsl:if test="name(.) != 'codeList' ">
				<xsl:value-of select="concat(string(.),' ')"/>
			</xsl:if>	
		</xsl:for-each>

		<xsl:choose>
			<xsl:when test="*"><xsl:apply-templates select="*" mode="allText"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="concat(string(.),' ')"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ========================================================================================= -->

    <xsl:template name="contact">
        <xsl:param name="orgName"/>
        <xsl:param name="indName"/>
        <xsl:param name="role"/>
        
        <xsl:if test="$orgName != ''">
            <Field name="orgName" string="{string($orgName)}" store="true" index="true" token="true"/><!-- FIXME : should not be tokenized -->
            <Field name="_orgName" string="{string($orgName)}" store="true" index="true" token="false"/>
        </xsl:if>
        <xsl:if test="$indName != ''">
            <Field name="indName" string="{string($indName)}" store="true" index="true" token="true"/><!-- FIXME : should not be tokenized -->
        </xsl:if>
        <xsl:if test="$role != '' and $orgName != ''">
            <Field name="orgRole" string="{string($orgName)}{string($role)}" store="false" index="true" token="true"/>
        </xsl:if>
    </xsl:template>

	<!-- ========================================================================================= -->

	<!-- PMT GeOrchestra : Stolen from index-fields.xsl of the regular iso19139 schema
		 related to PIGMA issue #2111 														-->


	<!-- inspireThemes is a nodeset consisting of skos:Concept elements -->
	<!-- each containing a skos:definition and skos:prefLabel for each language -->
	<!-- This template finds the provided keyword in the skos:prefLabel elements and returns the English one from the same skos:Concept -->
	<xsl:template name="translateInspireThemeToEnglish">
		<xsl:param name="keyword"/>
		<xsl:param name="inspireThemes"/>
		<xsl:for-each select="$inspireThemes/skos:prefLabel">
			<!-- if this skos:Concept contains a kos:prefLabel with text value equal to keyword -->
			<xsl:if test="text() = $keyword">
				<xsl:value-of select="../skos:prefLabel[@xml:lang='en']/text()"/>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>	

	<xsl:template name="determineInspireAnnex">
		<xsl:param name="keyword"/>
		<xsl:param name="inspireThemes"/>
		
		<xsl:variable name="englishKeywordMixedCase">
			<xsl:call-template name="translateInspireThemeToEnglish">
				<xsl:with-param name="keyword" select="$keyword"/>
				<xsl:with-param name="inspireThemes" select="$inspireThemes"/>
			</xsl:call-template>
		</xsl:variable>
		
            <xsl:variable name="lower">abcdefghijklmnopqrstuvwxyz</xsl:variable>
            <xsl:variable name="upper">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
            <xsl:variable name="englishKeyword" select="translate(string($englishKeywordMixedCase),$upper,$lower)"/>			
		
		<xsl:choose>
			<!-- annex i -->
			<xsl:when test="$englishKeyword='coordinate reference systems' or $englishKeyword='geographical grid systems' or $englishKeyword='geographical names' or $englishKeyword='administrative units' or $englishKeyword='addresses' or $englishKeyword='cadastral parcels' or $englishKeyword='transport networks' or $englishKeyword='hydrography' or $englishKeyword='protected sites'">
			    <xsl:text>i</xsl:text>
			</xsl:when>
			<!-- annex ii -->
			<xsl:when test="$englishKeyword='elevation' or $englishKeyword='land cover' or $englishKeyword='orthoimagery' or $englishKeyword='geology'">
				 <xsl:text>ii</xsl:text>
			</xsl:when>
			<!-- annex iii -->
			<xsl:when test="$englishKeyword='statistical units' or $englishKeyword='buildings' or $englishKeyword='soil' or $englishKeyword='land use' or $englishKeyword='human health and safety' or $englishKeyword='utility and government services' or $englishKeyword='environmental monitoring facilities' or $englishKeyword='production and industrial facilities' or $englishKeyword='agricultural and aquaculture facilities' or $englishKeyword='population distribution - demography' or $englishKeyword='area management/restriction/regulation zones and reporting units' or $englishKeyword='natural risk zones' or $englishKeyword='atmospheric conditions' or $englishKeyword='meteorological geographical features' or $englishKeyword='oceanographic geographical features' or $englishKeyword='sea regions' or $englishKeyword='bio-geographical regions' or $englishKeyword='habitats and biotopes' or $englishKeyword='species distribution' or $englishKeyword='energy resources' or $englishKeyword='mineral resources'">
				 <xsl:text>iii</xsl:text>
			</xsl:when>
			<!-- inspire annex cannot be established: leave empty -->
			<xsl:otherwise><xsl:value-of select="$englishKeyword"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>
								
	
</xsl:stylesheet>
