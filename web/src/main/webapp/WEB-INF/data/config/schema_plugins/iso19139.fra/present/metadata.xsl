<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gts="http://www.isotc211.org/2005/gts"
	xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:fra="http://www.cnig.gouv.fr/2005/fra"
	xmlns:gmx="http://www.isotc211.org/2005/gmx" xmlns:srv="http://www.isotc211.org/2005/srv"
	xmlns:gml="http://www.opengis.net/gml" xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:geonet="http://www.fao.org/geonetwork" xmlns:exslt="http://exslt.org/common"
	exclude-result-prefixes="gmd gco gml gts srv xlink exslt geonet">

  <xsl:import href="metadata-fop.xsl"/>

  <xsl:template name="iso19139.fraBrief">
    <metadata>
			<xsl:choose>
		    <xsl:when test="geonet:info/isTemplate='s'">
		      <xsl:apply-templates mode="iso19139-subtemplate" select="."/>
		      <xsl:copy-of select="geonet:info" copy-namespaces="no"/>
		    </xsl:when>
		    <xsl:otherwise>
	
			<!-- call iso19139 brief -->
			<xsl:call-template name="iso19139-brief"/>
		    </xsl:otherwise>
		  </xsl:choose>    
    </metadata>
  </xsl:template>


  <xsl:template name="iso19139.fraCompleteTab">
    <xsl:param name="tabLink"/>
    <xsl:param name="schema"/>
    
    <xsl:call-template name="iso19139CompleteTab">
      <xsl:with-param name="tabLink" select="$tabLink"/>
      <xsl:with-param name="schema" select="$schema"/>
    </xsl:call-template>
  	
  	
  	<!-- FRA tabs -->
  	<xsl:if test="/root/gui/config/metadata-tab/fra">
  		<xsl:call-template name="displayTab">
  			<xsl:with-param name="tab"     select="'packages'"/>
  			<xsl:with-param name="text"    select="/root/gui/strings/fraTab"/>
  			<xsl:with-param name="tabLink" select="''"/>
  		</xsl:call-template>
  		
  		<xsl:call-template name="displayTab">
  			<xsl:with-param name="tab"     select="'fraTabDesc'"/>
  			<xsl:with-param name="text"    select="/root/gui/strings/fraTabDesc"/>
  			<xsl:with-param name="indent"  select="'&#xA0;&#xA0;&#xA0;'"/>
  			<xsl:with-param name="tabLink" select="$tabLink"/>
  		</xsl:call-template>
  		
  		<xsl:call-template name="displayTab">
  			<xsl:with-param name="tab"     select="'fraTabTech'"/>
  			<xsl:with-param name="text"    select="/root/gui/strings/fraTabTech"/>
  			<xsl:with-param name="indent"  select="'&#xA0;&#xA0;&#xA0;'"/>
  			<xsl:with-param name="tabLink" select="$tabLink"/>
  		</xsl:call-template>
  		
  		<xsl:call-template name="displayTab">
  			<xsl:with-param name="tab"     select="'fraTabQua'"/>
  			<xsl:with-param name="text"    select="/root/gui/strings/fraTabQua"/>
  			<xsl:with-param name="indent"  select="'&#xA0;&#xA0;&#xA0;'"/>
  			<xsl:with-param name="tabLink" select="$tabLink"/>
  		</xsl:call-template>
  		
  		<xsl:call-template name="displayTab">
  			<xsl:with-param name="tab"     select="'fraTabAcc'"/>
  			<xsl:with-param name="text"    select="/root/gui/strings/fraTabAcc"/>
  			<xsl:with-param name="indent"  select="'&#xA0;&#xA0;&#xA0;'"/>
  			<xsl:with-param name="tabLink" select="$tabLink"/>
  		</xsl:call-template>
  		
  		<xsl:call-template name="displayTab">
  			<xsl:with-param name="tab"     select="'fraTabMd'"/>
  			<xsl:with-param name="text"    select="/root/gui/strings/fraTabMd"/>
  			<xsl:with-param name="indent"  select="'&#xA0;&#xA0;&#xA0;'"/>
  			<xsl:with-param name="tabLink" select="$tabLink"/>
  		</xsl:call-template>
  	</xsl:if>
  </xsl:template>

	<!-- main template - the way into processing iso19139.fra -->
	<xsl:template name="metadata-iso19139.fra">
		<xsl:param name="schema"/>
		<xsl:param name="edit" select="false()"/>
		<xsl:param name="embedded"/>

		<xsl:apply-templates mode="iso19139" select="." >
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="embedded" select="$embedded" />
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- ===================================================================== -->
	<!-- these elements should be boxed -->
	<!-- ===================================================================== -->
	<xsl:template mode="iso19139fra"
		match="fra:FRA_LegalConstraints|fra:FRA_SecurityConstraints|fra:FRA_SecurityConstraints">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>

		<xsl:apply-templates mode="complexElement" select=".">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit" select="$edit"/>
		</xsl:apply-templates>
	</xsl:template>



	<!--
		Redirection template for profil fra in order to process 
		extraTabs.
	-->
	<xsl:template mode="iso19139" match="gmd:MD_Metadata|*[@gco:isoType='gmd:MD_Metadata']" priority="2">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:param name="embedded"/>
		
		<xsl:variable name="dataset" select="gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue='dataset' or normalize-space(gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue)=''"/>
		
		<!-- thumbnail -->
		<tr>
			<td valign="middle" colspan="2">
				<xsl:if test="$currTab='metadata' or $currTab='identification' or /root/gui/config/metadata-tab/*[name(.)=$currTab]/@flat">
					<div style="float:left;width:70%;text-align:center;">
						<xsl:variable name="md">
							<xsl:apply-templates mode="brief" select="."/>
						</xsl:variable>
						<xsl:variable name="metadata" select="exslt:node-set($md)/*[1]"/>
						<xsl:call-template name="thumbnail">
							<xsl:with-param name="metadata" select="$metadata"/>
						</xsl:call-template>
					</div>
				</xsl:if>
				<xsl:if test="/root/gui/config/editor-metadata-relation">
					<div style="float:right;">				
						<xsl:call-template name="relatedResources">
							<xsl:with-param name="edit" select="$edit"/>
						</xsl:call-template>
					</div>
				</xsl:if>
			</td>
		</tr>
		
		<xsl:choose>
			
			<!-- metadata tab -->
			<xsl:when test="$currTab='metadata'">
				<xsl:call-template name="iso19139Metadata">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:call-template>
			</xsl:when>
			
			<!-- identification tab -->
			<xsl:when test="$currTab='identification'">
				<xsl:apply-templates mode="elementEP" select="gmd:identificationInfo|geonet:child[string(@name)='identificationInfo']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- maintenance tab -->
			<xsl:when test="$currTab='maintenance'">
				<xsl:apply-templates mode="elementEP" select="gmd:metadataMaintenance|geonet:child[string(@name)='metadataMaintenance']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- constraints tab -->
			<xsl:when test="$currTab='constraints'">
				<xsl:apply-templates mode="elementEP" select="gmd:metadataConstraints|geonet:child[string(@name)='metadataConstraints']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- spatial tab -->
			<xsl:when test="$currTab='spatial'">
				<xsl:apply-templates mode="elementEP" select="gmd:spatialRepresentationInfo|geonet:child[string(@name)='spatialRepresentationInfo']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- refSys tab -->
			<xsl:when test="$currTab='refSys'">
				<xsl:apply-templates mode="elementEP" select="gmd:referenceSystemInfo|geonet:child[string(@name)='referenceSystemInfo']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- distribution tab -->
			<xsl:when test="$currTab='distribution'">
				<xsl:apply-templates mode="elementEP" select="gmd:distributionInfo|geonet:child[string(@name)='distributionInfo']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- embedded distribution tab -->
			<xsl:when test="$currTab='distribution2'">
				<xsl:apply-templates mode="elementEP" select="gmd:distributionInfo/gmd:MD_Distribution/gmd:transferOptions/gmd:MD_DigitalTransferOptions">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- dataQuality tab -->
			<xsl:when test="$currTab='dataQuality'">
				<xsl:apply-templates mode="elementEP" select="gmd:dataQualityInfo|geonet:child[string(@name)='dataQualityInfo']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- appSchInfo tab -->
			<xsl:when test="$currTab='appSchInfo'">
				<xsl:apply-templates mode="elementEP" select="gmd:applicationSchemaInfo|geonet:child[string(@name)='applicationSchemaInfo']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- porCatInfo tab -->
			<xsl:when test="$currTab='porCatInfo'">
				<xsl:apply-templates mode="elementEP" select="gmd:portrayalCatalogueInfo|geonet:child[string(@name)='portrayalCatalogueInfo']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- contentInfo tab -->
			<xsl:when test="$currTab='contentInfo'">
				<xsl:apply-templates mode="elementEP" select="gmd:contentInfo|geonet:child[string(@name)='contentInfo']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- extensionInfo tab -->
			<xsl:when test="$currTab='extensionInfo'">
				<xsl:apply-templates mode="elementEP" select="gmd:metadataExtensionInfo|geonet:child[string(@name)='metadataExtensionInfo']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<!-- ISOMinimum tab -->
			<xsl:when test="$currTab='ISOMinimum'">
				<xsl:call-template name="isotabs">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="dataset" select="$dataset"/>
					<xsl:with-param name="core" select="false()"/>
				</xsl:call-template>
			</xsl:when>
			
			<!-- ISOCore tab -->
			<xsl:when test="$currTab='ISOCore'">
				<xsl:call-template name="isotabs">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="dataset" select="$dataset"/>
					<xsl:with-param name="core" select="true()"/>
				</xsl:call-template>
			</xsl:when>
			
			<!-- ISOAll tab -->
			<xsl:when test="$currTab='ISOAll'">
				<xsl:call-template name="iso19139Complete">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:call-template>
			</xsl:when>
			
			<!-- INSPIRE tab -->
			<xsl:when test="$currTab='inspire'">
				<xsl:call-template name="inspiretabs">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="dataset" select="$dataset"/>
				</xsl:call-template>
			</xsl:when>
			
			<!-- fra tab -->
			<xsl:when test="$currTab='fraTabDesc'">
				<xsl:call-template name="fraTabDesc">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="dataset" select="$dataset"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$currTab='fraTabTech'">
				<xsl:call-template name="fraTabTech">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="dataset" select="$dataset"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$currTab='fraTabQua'">
				<xsl:call-template name="fraTabQua">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="dataset" select="$dataset"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$currTab='fraTabAcc'">
				<xsl:call-template name="fraTabAcc">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="dataset" select="$dataset"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="$currTab='fraTabMd'">
				<xsl:call-template name="fraTabMd">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="dataset" select="$dataset"/>
				</xsl:call-template>
			</xsl:when>
			
			<!-- default -->
			<xsl:otherwise>
				<xsl:call-template name="iso19139Simple">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="flat"   select="/root/gui/config/metadata-tab/*[name(.)=$currTab]/@flat"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<xsl:template name="fraTabDesc">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:param name="dataset"/>
		<xsl:param name="core"/>
		
		<xsl:call-template name="complexElementGuiWrapper">
			<xsl:with-param name="title" select="/root/gui/strings/fraTabDesc"/>
			<xsl:with-param name="id" select="generate-id(/root/gui/strings/fraTabDesc)"/>
			<xsl:with-param name="content">
		
				<xsl:apply-templates mode="elementEP" select="
					gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:title|
					gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/geonet:child[string(@name)='title']|
					gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:alternateTitle|
					gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/geonet:child[string(@name)='alternateTitle']|
					gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:edition|
					gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/geonet:child[string(@name)='edition']|
					gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:date|
					gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/geonet:child[string(@name)='date']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="complexElement" select="
					gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/gmd:identifier
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="force"   select="true()"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:identificationInfo/*/gmd:citation/gmd:CI_Citation/geonet:child[string(@name)='identifier']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="force"   select="true()"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:identificationInfo/*/gmd:abstract|
					gmd:identificationInfo/*/geonet:child[string(@name)='abstract']|
					gmd:identificationInfo/*/gmd:purpose|
					gmd:identificationInfo/*/geonet:child[string(@name)='purpose']|
					gmd:identificationInfo/*/gmd:supplementalInformation|
					gmd:identificationInfo/*/geonet:child[string(@name)='supplementalInformation']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:identificationInfo/*/gmd:topicCategory|
					gmd:identificationInfo/*/geonet:child[string(@name)='topicCategory']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
				
				
				<xsl:apply-templates mode="elementEP" select="gmd:identificationInfo/*/srv:serviceType|
					gmd:identificationInfo/*/geonet:child[string(@name)='serviceType']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="force"   select="true()"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="gmd:identificationInfo/*/srv:serviceTypeVersion|
					gmd:identificationInfo/*/geonet:child[string(@name)='serviceTypeVersion']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="force"   select="true()"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="gmd:identificationInfo/*/srv:couplingType|
					gmd:identificationInfo/*/geonet:child[string(@name)='couplingType']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="force"   select="true()"/>
				</xsl:apply-templates>
				
				<!-- FIXME : take care of operatesOn adding action -->
				<xsl:apply-templates mode="complexElement" select="gmd:identificationInfo/*/srv:operatesOn[1]">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="true()"/>
					<xsl:with-param name="content">
						<xsl:apply-templates mode="elementEP" select="gmd:identificationInfo/*/srv:operatesOn|
							gmd:identificationInfo/*/geonet:child[string(@name)='operatesOn']">
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit"   select="$edit"/>
							<xsl:with-param name="force"   select="true()"/>
						</xsl:apply-templates>
					</xsl:with-param>
				</xsl:apply-templates>
				
				
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:identificationInfo/*/gmd:descriptiveKeywords
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="force"   select="true()"/>
				</xsl:apply-templates>
				<xsl:if test="not(gmd:identificationInfo/*/gmd:descriptiveKeywords)">
					<xsl:apply-templates mode="elementEP" select="
						gmd:identificationInfo/*/geonet:child[string(@name)='descriptiveKeywords']
						">
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="edit"   select="$edit"/>
						<xsl:with-param name="force"   select="true()"/>
					</xsl:apply-templates>
				</xsl:if>
				
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:identificationInfo/*/gmd:pointOfContact
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="force"   select="true()"/>
				</xsl:apply-templates>
				<xsl:if test="not(gmd:identificationInfo/*/gmd:pointOfContact)">
					<xsl:apply-templates mode="elementEP" select="
						gmd:identificationInfo/*/geonet:child[string(@name)='pointOfContact']
						">
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="edit"   select="$edit"/>
						<xsl:with-param name="force"   select="true()"/>
					</xsl:apply-templates>
				</xsl:if>
				
				<xsl:for-each select="gmd:identificationInfo/*/gmd:extent">
					<xsl:apply-templates mode="complexElement" select=".">
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="edit"   select="true()"/>
						<xsl:with-param name="content">
							<xsl:apply-templates mode="elementEP" select="gmd:EX_Extent/gmd:description|
								./gmd:EX_Extent/gmd:geographicElement">
								<xsl:with-param name="schema" select="$schema"/>
								<xsl:with-param name="edit"   select="$edit"/>
							</xsl:apply-templates>
							<xsl:apply-templates mode="elementEP" select="./gmd:EX_Extent/geonet:child[string(@name)='geographicElement']">
								<xsl:with-param name="schema" select="$schema"/>
								<xsl:with-param name="edit"   select="$edit"/>
								<xsl:with-param name="force"   select="true()"/>
							</xsl:apply-templates>
						</xsl:with-param>
					</xsl:apply-templates> 
					
					<!-- FIXME : how to force display of add/delete option -->
					<xsl:apply-templates mode="elementEP" select="gmd:EX_Extent/gmd:verticalElement|gmd:EX_Extent/gmd:temporalElement">
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="edit"   select="$edit"/>
					</xsl:apply-templates>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
		
		
	</xsl:template>
	<xsl:template name="fraTabTech">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:param name="dataset"/>
		<xsl:param name="core"/>
		
		<xsl:call-template name="complexElementGuiWrapper">
			<xsl:with-param name="title" select="/root/gui/strings/fraTabTech"/>
			<xsl:with-param name="id" select="generate-id(/root/gui/strings/fraTabTech)"/>
			<xsl:with-param name="content">
						
				<xsl:apply-templates mode="elementEP" select="
					gmd:identificationInfo/*/gmd:status|
					gmd:identificationInfo/*/geonet:child[string(@name)='status']|
					gmd:identificationInfo/*/gmd:resourceMaintenance|
					gmd:identificationInfo/*/geonet:child[string(@name)='resourceMaintenance']|
					gmd:identificationInfo/*/gmd:spatialRepresentationType|
					gmd:identificationInfo/*/geonet:child[string(@name)='spatialRepresentationType']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:spatialRepresentationInfo|
					geonet:child[string(@name)='spatialRepresentationInfo']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:identificationInfo/*/gmd:spatialResolution|
					gmd:identificationInfo/*/geonet:child[string(@name)='spatialResolution']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:referenceSystemInfo|
					geonet:child[string(@name)='referenceSystemInfo']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:identificationInfo/*/fra:relatedCitation/gmd:CI_Citation|
					gmd:identificationInfo/*/fra:relatedCitation/geonet:child[string(@name)='CI_Citation']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:language|
					geonet:child[string(@name)='language']|                            
					gmd:characterSet|
					geonet:child[string(@name)='characterSet']                           
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:with-param>
		</xsl:call-template>		
	</xsl:template>
	<xsl:template name="fraTabQua">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:param name="dataset"/>
		<xsl:param name="core"/>
		
		<xsl:call-template name="complexElementGuiWrapper">
			<xsl:with-param name="title" select="/root/gui/strings/fraTabQua"/>
			<xsl:with-param name="id" select="generate-id(/root/gui/strings/fraTabQua)"/>
			<xsl:with-param name="content">
				
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:dataQualityInfo|
					geonet:child[string(@name)='dataQualityInfo']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:with-param>
		</xsl:call-template>
		
	</xsl:template>
	<xsl:template name="fraTabAcc">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:param name="dataset"/>
		<xsl:param name="core"/>
		
		<xsl:call-template name="complexElementGuiWrapper">
			<xsl:with-param name="title" select="/root/gui/strings/fraTabAcc"/>
			<xsl:with-param name="id" select="generate-id(/root/gui/strings/fraTabAcc)"/>
			<xsl:with-param name="content">
						
				<xsl:apply-templates mode="elementEP" select="
					gmd:distributionInfo|
					geonet:child[string(@name)='distributionInfo']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
				
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:identificationInfo/*/gmd:resourceConstraints">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="gmd:identificationInfo/*/geonet:child[string(@name)='resourceConstraints']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="force"   select="true()"/>
				</xsl:apply-templates>
			</xsl:with-param>
		</xsl:call-template>
		
	</xsl:template>
	<xsl:template name="fraTabMd">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:param name="dataset"/>
		<xsl:param name="core"/>
		
		<xsl:call-template name="complexElementGuiWrapper">
			<xsl:with-param name="title" select="/root/gui/strings/fraTabMd"/>
			<xsl:with-param name="id" select="generate-id(/root/gui/strings/fraTabMd)"/>
			<xsl:with-param name="content">
						
				<xsl:apply-templates mode="elementEP" select="
					gmd:fileIdentifier|
					gmd:language|
					geonet:child[string(@name)='language']|
					gmd:characterSet|
					geonet:child[string(@name)='characterSet']|
					gmd:hierarchyLevel|
					geonet:child[string(@name)='hierarchyLevel']|
					gmd:hierarchyLevelName|
					geonet:child[string(@name)='hierarchyLevelName']|
					gmd:parentIdentifier|
					geonet:child[string(@name)='parentIdentifier']|
					gmd:contact|
					geonet:child[string(@name)='contact']|
					gmd:dateStamp|
					geonet:child[string(@name)='dateStamp']|
					gmd:metadataStandardName|
					geonet:child[string(@name)='metadataStandardName']|
					gmd:metadataStandardVersion|
					geonet:child[string(@name)='metadataStandardVersion']
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="
					gmd:metadataConstraints
					">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- =================================================================== -->
	<!-- === Javascript used by functions in this presentation XSLT          -->
	<!-- =================================================================== -->
	<xsl:template name="iso19139.fra-javascript"/>

</xsl:stylesheet>
