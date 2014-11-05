<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" 	xmlns:gmd="http://www.isotc211.org/2005/gmd"
								xmlns:wmc="http://www.opengis.net/context"
								xmlns:wmc11="http://www.opengeospatial.net/context"                              								
								xmlns:gco="http://www.isotc211.org/2005/gco"
								xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
								xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- ============================================================================= -->

	<xsl:template match="*" mode="RespParty">

		<xsl:for-each select="wmc:ContactPersonPrimary/wmc:ContactPerson
			|wmc11:ContactPersonPrimary/wmc11:ContactPerson">
			<gmd:individualName>
				<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
			</gmd:individualName>
		</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<xsl:for-each select="wmc:ContactPersonPrimary/wmc:ContactOrganization
			|wmc11:ContactPersonPrimary/wmc11:ContactOrganization">
			<gmd:organisationName>
				<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
			</gmd:organisationName>
		</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<xsl:for-each select="wmc:ContactPosition|wmc11:ContactPosition">
			<gmd:positionName>
				<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
			</gmd:positionName>
		</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

			<gmd:contactInfo>
				<gmd:CI_Contact>
					<xsl:apply-templates select="." mode="Contact"/>
				</gmd:CI_Contact>
			</gmd:contactInfo>
		
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<gmd:role>
			<gmd:CI_RoleCode codeList="./resources/codeList.xml#CI_RoleCode" codeListValue="pointOfContact" />
		</gmd:role>

	</xsl:template>

	<!-- ============================================================================= -->

	<xsl:template match="*" mode="Contact">

		<gmd:phone>
			<gmd:CI_Telephone>
				<xsl:for-each select="wmc:ContactVoiceTelephone|wmc11:ContactVoiceTelephone">
					<gmd:voice>
						<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
					</gmd:voice>
				</xsl:for-each>
	
				<xsl:for-each select="wmc:ContactFacsimileTelephone|wmc11:ContactFacsimileTelephone">
					<gmd:facsimile>
						<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
					</gmd:facsimile>
				</xsl:for-each>
			</gmd:CI_Telephone>
		</gmd:phone>
	
		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<xsl:for-each select="wmc:ContactAddress|wmc11:ContactAddress">
			<gmd:address>
				<gmd:CI_Address>
					<xsl:apply-templates select="." mode="Address"/>
				</gmd:CI_Address>
			</gmd:address>
		</xsl:for-each>

		<!--cntOnLineRes-->
		<!--cntHours -->
		<!--cntInstr -->

	</xsl:template>


	<!-- ============================================================================= -->

	<xsl:template match="*" mode="Address">

		<xsl:for-each select="wmc:Address|wmc11:Address">
			<gmd:deliveryPoint>
				<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
			</gmd:deliveryPoint>
		</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<xsl:for-each select="wmc:City|wmc11:City">
			<gmd:city>
				<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
			</gmd:city>
		</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<xsl:for-each select="wmc:StateOrProvince|wmc11:StateOrProvince">
			<gmd:administrativeArea>
				<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
			</gmd:administrativeArea>
		</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<xsl:for-each select="wmc:PostCode|wmc11:PostCode">
			<gmd:postalCode>
				<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
			</gmd:postalCode>
		</xsl:for-each>

		<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<xsl:for-each select="wmc:Country|wmc11:Country">
			<gmd:country>
				<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
			</gmd:country>
		</xsl:for-each>

		<!-- TODO - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<xsl:for-each select="wmc:eMailAdd|wmc11:eMailAdd">
			<gmd:electronicMailAddress>
				<gco:CharacterString><xsl:value-of select="."/></gco:CharacterString>
			</gmd:electronicMailAddress>
		</xsl:for-each>

	</xsl:template>

	<!-- ============================================================================= -->

</xsl:stylesheet>
