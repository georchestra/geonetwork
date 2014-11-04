<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" 
				xmlns="http://www.isotc211.org/2005/gmd" 
                xmlns:geonet="http://www.fao.org/geonetwork" 
				xmlns:wmc="http://www.opengis.net/context"
				xmlns:wmc11="http://www.opengeospatial.net/context"
				xmlns:gts="http://www.isotc211.org/2005/gts"
				xmlns:gco="http://www.isotc211.org/2005/gco"
				xmlns:gml="http://www.opengis.net/gml"
				xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns:xlink="http://www.w3.org/1999/xlink"
				xmlns:java="java:org.fao.geonet.util.XslUtil"
				xmlns:saxon="http://saxon.sf.net/">

				
	<!-- ============================================================================= -->				

	<xsl:param name="lang">eng</xsl:param>
	<xsl:param name="topic"></xsl:param>
    <xsl:param name="viewer_url"></xsl:param>
    <xsl:param name="wmc_url"></xsl:param>
    
    <!-- These are provided by the ImportWmc.java jeeves service -->
    <xsl:param name="currentuser_name"></xsl:param>
    <xsl:param name="currentuser_phone"></xsl:param>
    <xsl:param name="currentuser_mail"></xsl:param>
    <xsl:param name="currentuser_org"></xsl:param>
    
    
	
	<xsl:include href="./resp-party.xsl"/>
	<xsl:include href="./identification.xsl"/>
	
	<!-- ============================================================================= -->
	
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
	
	<!-- ============================================================================= -->

	<xsl:template match="/">
		<xsl:apply-templates/>
	</xsl:template>
	
	<!-- ============================================================================= -->	
	
	<xsl:template match="wmc:ViewContext|wmc11:ViewContext">
		<MD_Metadata>
			
			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
			
			<!--  <fileIdentifier>
				<gco:CharacterString><xsl:value-of select="/wmc:ViewContext/@id"/></gco:CharacterString>
			</fileIdentifier>
			 -->
			
			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
			
			<language>
				<gco:CharacterString><xsl:value-of select="$lang"/></gco:CharacterString>
				<!-- English is default. Not available in Web Map Context. Selected by user from GUI -->
			</language>
			
			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

			<characterSet>
				<MD_CharacterSetCode codeList="./resources/codeList.xml#MD_CharacterSetCode" codeListValue="utf8" />
			</characterSet>

			<!-- parentIdentifier : Web Map Context has no parent -->
			<!-- mdHrLv -->
			<!-- mdHrLvName -->

			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

			<xsl:for-each select="/wmc:ViewContext/wmc:General/wmc:ContactInformation|/wmc11:ViewContext/wmc11:General/wmc11:ContactInformation">
				<contact>
					<CI_ResponsibleParty>
						<xsl:apply-templates select="." mode="RespParty"/>
					</CI_ResponsibleParty>
				</contact>
			</xsl:for-each>

			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
			<xsl:variable name="df">[Y0001]-[M01]-[D01]T[H01]:[m01]:[s01]</xsl:variable>
			<dateStamp>
				<gco:DateTime><xsl:value-of select="format-dateTime(current-dateTime(),$df)"/></gco:DateTime>
			</dateStamp>

			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

			<metadataStandardName>
				<gco:CharacterString>ISO 19115:2003/19139</gco:CharacterString>
			</metadataStandardName>

			<metadataStandardVersion>
				<gco:CharacterString>1.0</gco:CharacterString>
			</metadataStandardVersion>
			
			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
			
			<referenceSystemInfo>
				<MD_ReferenceSystem>
					<referenceSystemIdentifier>
						<RS_Identifier>
							<code>
								<gco:CharacterString><xsl:value-of select="/wmc:ViewContext/wmc:General/wmc:BoundingBox/@SRS
									|/wmc11:ViewContext/wmc11:General/wmc11:BoundingBox/@SRS"/></gco:CharacterString>
							</code>
						</RS_Identifier>
					</referenceSystemIdentifier>
				</MD_ReferenceSystem>
			</referenceSystemInfo>
			
			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

			<identificationInfo>
				<MD_DataIdentification>
					<xsl:apply-templates select="." mode="DataIdentification">
						<xsl:with-param name="topic"><xsl:value-of select="$topic"/></xsl:with-param>
					</xsl:apply-templates>
					<!--  sets a default contact author -->
					<pointOfContact>
						<CI_ResponsibleParty>
							<individualName>
								<gco:CharacterString><xsl:value-of select="$currentuser_name" /></gco:CharacterString>
							</individualName>
							<organisationName>
								<gco:CharacterString><xsl:value-of select="$currentuser_org" /></gco:CharacterString>
							</organisationName>
							<contactInfo>
								<CI_Contact>
									<phone>
										<CI_Telephone>
											<voice>
											    <gco:CharacterString><xsl:value-of select="$currentuser_phone" /></gco:CharacterString>
											</voice>
										</CI_Telephone>
									</phone>
									<address>
										<CI_Address>
											<electronicMailAddress>
												<gco:CharacterString><xsl:value-of select="$currentuser_mail" /></gco:CharacterString>
											</electronicMailAddress>
										</CI_Address>
									</address>
								</CI_Contact>
							</contactInfo>
							<role>
								<CI_RoleCode codeList="http://standards.iso.org/ittf/PubliclyAvailableStandards/ISO_19139_Schemas/resources/Codelist/ML_gmxCodelists.xml#CI_RoleCode"
								codeListValue="author" />
							</role>
						</CI_ResponsibleParty>
					</pointOfContact>
					<!--  extracts the extent -->
					<extent>
						<EX_Extent>
							<geographicElement>
								<EX_GeographicBoundingBox>
									<westBoundLongitude>
										<gco:Decimal><xsl:value-of select="/wmc:ViewContext/wmc:General/wmc:BoundingBox/@minx" /></gco:Decimal>
									</westBoundLongitude>
									<eastBoundLongitude>
										<gco:Decimal><xsl:value-of select="/wmc:ViewContext/wmc:General/wmc:BoundingBox/@maxx" /></gco:Decimal>
									</eastBoundLongitude>
									<southBoundLatitude>
										<gco:Decimal><xsl:value-of select="/wmc:ViewContext/wmc:General/wmc:BoundingBox/@miny" /></gco:Decimal>
									</southBoundLatitude>
									<northBoundLatitude>
										<gco:Decimal><xsl:value-of select="/wmc:ViewContext/wmc:General/wmc:BoundingBox/@maxy" /></gco:Decimal>
									</northBoundLatitude>
								</EX_GeographicBoundingBox>
							</geographicElement>
						</EX_Extent>
					</extent>
				</MD_DataIdentification>
			</identificationInfo>
			
			<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
			
			<distributionInfo>
				<MD_Distribution>
					<transferOptions>
						<MD_DigitalTransferOptions>
							<onLine>
							     <!-- iterates over the layers 
							     need to:
							     * extract version
							     * extract url
							     * name
							     * title
							     -->

			                 <xsl:for-each select="/wmc:ViewContext/wmc:LayerList">
			                    <xsl:variable name="wmsUrl" select="./wmc:Layer/wmc:Server/wmc:OnlineResource/@xlink:href" />
			                    <xsl:variable name="wmsName" select="./wmc:Layer/wmc:Name/text()" />
			                    <xsl:variable name="wmsTitle" select="./wmc:Layer/wmc:Title/text()" />
			                    <xsl:variable name="wmsVersion" select="./wmc:Layer/wmc:Server/@version" />
								<CI_OnlineResource>
									<linkage>
										<URL><xsl:value-of select="$wmsUrl" /></URL>
									</linkage>
									<protocol>
										<gco:CharacterString><xsl:value-of select="concat('OGC:WMS-', $wmsVersion, '-http-get-map')" /></gco:CharacterString>
									</protocol>
									<name>
										<gco:CharacterString><xsl:value-of select="$wmsName" /></gco:CharacterString>
									</name>
									<description>
										<gco:CharacterString><xsl:value-of select="$wmsTitle" /></gco:CharacterString>
									</description>
								</CI_OnlineResource>
							</xsl:for-each>
								<CI_OnlineResource>
									<linkage>
									  <URL><xsl:value-of select="$wmc_url" /></URL>
									</linkage>
									<protocol>
										<!-- FIXME : use standardized label for WMS protocol -->
										<gco:CharacterString>OGC:WMC</gco:CharacterString>
									</protocol>
									<name>
										<gco:CharacterString><xsl:value-of select="/wmc:ViewContext/wmc:General/wmc:Title
											|/wmc11:ViewContext/wmc11:General/wmc11:Title"/></gco:CharacterString>
									</name>
									<description>
										<gco:CharacterString><xsl:value-of select="/wmc:ViewContext/wmc:General/wmc:Title
											|/wmc11:ViewContext/wmc11:General/wmc11:Title"/></gco:CharacterString>
									</description>
								</CI_OnlineResource>
							</onLine>
							<onLine>
                                <CI_OnlineResource>
                                    <linkage>
                                      <URL><xsl:value-of select="$viewer_url" /></URL>
                                    </linkage>
                                    <protocol>
                                        <gco:CharacterString>WWW:LINK-1.0-http--link</gco:CharacterString>
                                    </protocol>
                                    <name>
                                        <gco:CharacterString><xsl:value-of select="/wmc:ViewContext/wmc:General/wmc:Title
                                            |/wmc11:ViewContext/wmc11:General/wmc11:Title"/></gco:CharacterString>
                                    </name>
                                    <description>
                                        <gco:CharacterString><xsl:value-of select="/wmc:ViewContext/wmc:General/wmc:Title
                                            |/wmc11:ViewContext/wmc11:General/wmc11:Title"/></gco:CharacterString>
                                    </description>
                                </CI_OnlineResource>
                            </onLine>
						</MD_DigitalTransferOptions>
					</transferOptions>
				</MD_Distribution>
			</distributionInfo>
				 <dataQualityInfo>
			        <DQ_DataQuality>
			            <lineage>
			                <!-- TODO: iterate over the OnlineRes of each layers -->
			                <LI_Lineage>
                                <xsl:for-each select="/wmc:ViewContext/wmc:LayerList">
                                    <xsl:variable name="sourceNode" select="java:generateLineageSource(string(./wmc:Layer/wmc:MetadataURL/wmc:OnlineResource/@xlink:href))" />
			                        <xsl:copy-of select="saxon:parse($sourceNode)" />
			                   </xsl:for-each>
			                </LI_Lineage>
			            </lineage>
			        </DQ_DataQuality>
			    </dataQualityInfo>
		</MD_Metadata>
	</xsl:template>	
	
</xsl:stylesheet>
