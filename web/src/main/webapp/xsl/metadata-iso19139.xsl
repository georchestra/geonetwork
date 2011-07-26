<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl ="http://www.w3.org/1999/XSL/Transform"
	xmlns:gmd="http://www.isotc211.org/2005/gmd"
	xmlns:gts="http://www.isotc211.org/2005/gts"
	xmlns:gco="http://www.isotc211.org/2005/gco"
	xmlns:gmx="http://www.isotc211.org/2005/gmx"
	xmlns:srv="http://www.isotc211.org/2005/srv"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:gml="http://www.opengis.net/gml"
    xmlns:xlink="http://www.w3.org/1999/xlink"
	xmlns:geonet="http://www.fao.org/geonetwork"
	xmlns:exslt="http://exslt.org/common"
	exclude-result-prefixes="gmx xsi gmd gco gml gts srv xlink exslt geonet">

	<xsl:include href="metadata-iso19139-utils.xsl"/>
	<xsl:include href="metadata-iso19139-geo.xsl"/>
	<xsl:include href="metadata-iso19139-inspire.xsl"/>
	
	<!-- =================================================================== -->
	<!-- default: in simple mode just a flat list -->
	<!-- =================================================================== -->

	<xsl:template mode="iso19139" match="*|@*">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<!-- do not show empty elements in view mode -->
		<xsl:choose>
			<xsl:when test="$edit=true()">
				<xsl:apply-templates mode="element" select=".">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="true()"/>
					<xsl:with-param name="flat"   select="/root/gui/config/metadata-tab/*[name(.)=$currTab]/@flat"/>
				</xsl:apply-templates>
			</xsl:when>
			
			<xsl:otherwise>
				<xsl:variable name="empty">
					<xsl:apply-templates mode="iso19139IsEmpty" select="."/>
				</xsl:variable>
				
				<xsl:if test="$empty!=''">
					<xsl:apply-templates mode="element" select=".">
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="edit"   select="false()"/>
						<xsl:with-param name="flat"   select="/root/gui/config/metadata-tab/*[name(.)=$currTab]/@flat"/>
					</xsl:apply-templates>
				</xsl:if>
				
			</xsl:otherwise>
		</xsl:choose>
			
	</xsl:template>
	
	
	<!--=====================================================================-->
	<!-- these elements should not be displayed 
		* do not display graphicOverview managed by GeoNetwork (ie. having a 
		fileDescription set to thumbnail or large_thumbnail). Those thumbnails
		are managed in then thumbnail popup. Others could be valid URL pointing to
		an image available on the Internet.
	-->
	<!--=====================================================================-->
	
	<xsl:template mode="iso19139"
		match="gmd:graphicOverview[gmd:MD_BrowseGraphic/gmd:fileDescription/gco:CharacterString='thumbnail' or gmd:MD_BrowseGraphic/gmd:fileDescription/gco:CharacterString='large_thumbnail']"
		priority="20" />
	
	
	<!-- ===================================================================== -->
	<!-- these elements should be boxed -->
	<!-- ===================================================================== -->

	<xsl:template mode="iso19139" match="gmd:identificationInfo|gmd:distributionInfo|gmd:descriptiveKeywords|gmd:thesaurusName|gmd:spatialRepresentationInfo|gmd:pointOfContact|gmd:dataQualityInfo|gmd:referenceSystemInfo|gmd:equivalentScale|gmd:projection|gmd:ellipsoid|gmd:extent[name(..)!='gmd:EX_TemporalExtent']|gmd:geographicBox|gmd:EX_TemporalExtent|gmd:MD_Distributor|srv:containsOperations|srv:SV_CoupledResource|gmd:metadataConstraints">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:apply-templates mode="complexElement" select=".">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- ===================================================================== -->
	<!-- some gco: elements and gmx:MimeFileType are swallowed -->
	<!-- ===================================================================== -->

	<xsl:template mode="iso19139" match="gmd:*[gco:Date|gco:DateTime|gco:Integer|gco:Decimal|gco:Boolean|gco:Real|gco:Measure|gco:Length|gco:Distance|gco:Angle|gco:Scale|gco:RecordType|gmx:MimeFileType]|
									srv:*[gco:Date|gco:DateTime|gco:Integer|gco:Decimal|gco:Boolean|gco:Real|gco:Measure|gco:Length|gco:Distance|gco:Angle|gco:Scale|gco:RecordType|gmx:MimeFileType]">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:call-template name="iso19139String">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- ==================================================================== -->

	<!--
		OperatesOn element display or edit attribute uuidref. In edit mode
		the metadata selection panel is provided to set the uuid.
		In view mode, the title of the metadata is displayed.
		
		Note: it could happen that linked metadata record is not accessible
		to current user. In such a situation, clicking the link will return
		a privileges exception.
		-->
	<xsl:template mode="iso19139" match="srv:operatesOn">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:variable name="text">
			
			<xsl:choose>
				<xsl:when test="$edit=true()">
					<xsl:variable name="ref" select="geonet:element/@ref"/>
					<input type="text" name="_{$ref}_uuidref" id="_{$ref}_uuidref" value="{./@uuidref}" size="20"
						onfocus="javascript:showLinkedMetadataSelectionPanel('{$ref}', 'uuidref');"/>
					<img src="../../images/find.png" alt="{/root/gui/strings/search}" title="{/root/gui/strings/search}"
						onclick="javascript:showLinkedMetadataSelectionPanel('{$ref}', 'uuidref');" onmouseover="this.style.cursor='pointer';"/>
				</xsl:when>
				<xsl:otherwise>
					<a href="metadata.show?uuid={@uuidref}">
                 		<xsl:call-template name="getMetadataTitle">
							<xsl:with-param name="uuid" select="@uuidref"/>
						</xsl:call-template>
					</a>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:apply-templates mode="simpleElement" select=".">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="text"   select="$text"/>
		</xsl:apply-templates>
	</xsl:template>



    <!-- ============================================================================= -->
    <!--   
	 Display a list of related resources to which the current service metadata operatesOn.
	 
     Ie. User should define related metadata record using operatesOn elements and then if
     needed, set a coupledResource to create a link to the data itself
      (using layer name/feature type/coverage name as described in capabilities documents). 
     
     To create a relation best is using the related resources panel (see relatedResources 
     template in metadata-iso19139-utils.xsl).
      -->
    <xsl:template mode="iso19139" match="srv:coupledResource/srv:SV_CoupledResource/srv:identifier" priority="200">
        <xsl:param name="schema"/>
        <xsl:param name="edit"/>

        <xsl:choose>
            <xsl:when test="$edit=true()">
                <xsl:variable name="text">
                    <xsl:variable name="ref" select="gco:CharacterString/geonet:element/@ref"/>
                   	<xsl:variable name="currentUuid" select="gco:CharacterString/text()"/>
                    <input type="text" class="md" name="_{$ref}" id="_{$ref}" onchange="validateNonEmpty(this)" value="{$currentUuid}" size="30"/>
                    <xsl:choose>
                        <xsl:when test="count(//srv:operatesOn[@uuidref!=''])=0">
                            <xsl:value-of select="/root/gui/strings/noOperatesOn"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <select onchange="javascript:$('_{$ref}').value=this.options[this.selectedIndex].value;" class="md">
                                <option></option>
                                <xsl:for-each select="//srv:operatesOn[@uuidref!='']">
                                    <option value="{@uuidref}">
                                    	<xsl:if test="@uuidref = $currentUuid">
                                    		<xsl:attribute name="selected">selected</xsl:attribute>
                                    	</xsl:if>
                                        <xsl:call-template name="getMetadataTitle">
                                            <xsl:with-param name="uuid" select="@uuidref"/>
                                        </xsl:call-template>
                                    </option>
                                </xsl:for-each>
                            </select>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:apply-templates mode="simpleElement" select=".">
                    <xsl:with-param name="schema" select="$schema"/>
                    <xsl:with-param name="edit"   select="true()"/>
                    <xsl:with-param name="text"   select="$text"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates mode="simpleElement" select=".">
                    <xsl:with-param name="schema"  select="$schema"/>
                    <xsl:with-param name="text">
                    	<a href="metadata.show?uuid={gco:CharacterString}">
                 			<xsl:call-template name="getMetadataTitle">
								<xsl:with-param name="uuid" select="gco:CharacterString"/>
							</xsl:call-template>
                    	</a>
                    </xsl:with-param>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>




	<!--
		Create widget to handle editing of xsd:duration elements.
		
		Format: PnYnMnDTnHnMnS
		
		*  P indicates the period (required)
		* nY indicates the number of years
		* nM indicates the number of months
		* nD indicates the number of days
		* T indicates the start of a time section (required if you are going to specify hours, minutes, or seconds)
		* nH indicates the number of hours
		* nM indicates the number of minutes
		* nS indicates the number of seconds
		
		TODO : onload, we should run validateNumber handler in order to change 
		input class when needed.
		
	-->
	<xsl:template mode="iso19139" match="gts:TM_PeriodDuration" priority="100">
		<xsl:param name="schema" />
		<xsl:param name="edit" />
		
		<!--Set default value -->
		<xsl:variable name="p">
			<xsl:choose>
				<xsl:when test=".=''">P0Y0M0DT0H0M0S</xsl:when>
				<xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!-- Extract fragment -->
		<xsl:variable name="NEG">
			<xsl:choose>
				<xsl:when test="starts-with($p, '-')">true</xsl:when>
				<xsl:otherwise></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="Y" select="substring-before(substring-after($p, 'P'), 'Y')"/>
		<xsl:variable name="M" select="substring-before(substring-after($p, 'Y'), 'M')"/>
		<xsl:variable name="D" select="substring-before(substring-after($p, 'M'), 'DT')"/>
		<xsl:variable name="H" select="substring-before(substring-after($p, 'DT'), 'H')"/>
		<xsl:variable name="MI" select="substring-before(substring-after($p, 'H'), 'M')"/>
		<xsl:variable name="S" select="substring-before(substring-after(substring-after($p,'M' ),'M' ), 'S')"/>
		
		<xsl:variable name="text">
			<xsl:choose>
				<xsl:when test="$edit=true()">
					<xsl:variable name="ref" select="geonet:element/@ref"/>
					
					<input type="checkbox" id="N{$ref}" onchange="buildDuration('{$ref}');">
						<xsl:if test="$NEG!=''"><xsl:attribute name="checked">checked</xsl:attribute></xsl:if>
					</input>
					<label for="N{$ref}"><xsl:value-of select="/root/gui/strings/durationSign"/></label><br/>
					<xsl:value-of select="/root/gui/strings/durationNbYears"/><input type="text" id="Y{$ref}" class="content" value="{substring-before(substring-after($p, 'P'), 'Y')}" size="4" onchange="buildDuration('{$ref}');" onkeyup="validateNumber(this,true,false);"/>-
					<xsl:value-of select="/root/gui/strings/durationNbMonths"/><input type="text" id="M{$ref}" class="content" value="{substring-before(substring-after($p, 'Y'), 'M')}" size="4" onchange="buildDuration('{$ref}');" onkeyup="validateNumber(this,true,false);"/>-
					<xsl:value-of select="/root/gui/strings/durationNbDays"/><input type="text" id="D{$ref}" class="content" value="{substring-before(substring-after($p, 'M'), 'DT')}" size="4" onchange="buildDuration('{$ref}');" onkeyup="validateNumber(this,true,false);"/><br/>
					<xsl:value-of select="/root/gui/strings/durationNbHours"/><input type="text" id="H{$ref}" class="content" value="{substring-before(substring-after($p, 'DT'), 'H')}" size="4" onchange="buildDuration('{$ref}');" onkeyup="validateNumber(this,true,false);"/>-
					<xsl:value-of select="/root/gui/strings/durationNbMinutes"/><input type="text" id="MI{$ref}" class="content" value="{substring-before(substring-after($p, 'H'), 'M')}" size="4" onchange="buildDuration('{$ref}');" onkeyup="validateNumber(this,true,false);"/>-
					<xsl:value-of select="/root/gui/strings/durationNbSeconds"/><input type="text" id="S{$ref}" class="content" value="{substring-before(substring-after(substring-after($p,'M' ),'M' ), 'S')}" size="4" onchange="buildDuration('{$ref}');" onkeyup="validateNumber(this,true,true);"/><br/>
					<input type="hidden" name="_{$ref}" id="_{$ref}" value="{$p}" size="20"/><br/>
					
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="$NEG!=''">-</xsl:if><xsl:text> </xsl:text>
					<xsl:value-of select="$Y"/><xsl:text> </xsl:text><xsl:value-of select="/root/gui/strings/durationYears"/><xsl:text>  </xsl:text>
					<xsl:value-of select="$M"/><xsl:text> </xsl:text><xsl:value-of select="/root/gui/strings/durationMonths"/><xsl:text>  </xsl:text>
					<xsl:value-of select="$D"/><xsl:text> </xsl:text><xsl:value-of select="/root/gui/strings/durationDays"/><xsl:text> / </xsl:text>
					<xsl:value-of select="$H"/><xsl:text> </xsl:text><xsl:value-of select="/root/gui/strings/durationHours"/><xsl:text>  </xsl:text>
					<xsl:value-of select="$MI"/><xsl:text> </xsl:text><xsl:value-of select="/root/gui/strings/durationMinutes"/><xsl:text>  </xsl:text>
					<xsl:value-of select="$S"/><xsl:text> </xsl:text><xsl:value-of select="/root/gui/strings/durationSeconds"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:apply-templates mode="simpleElement" select=".">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="text"   select="$text"/>
		</xsl:apply-templates>
	</xsl:template>

    <!-- ==================================================================== -->

	<xsl:template name="iso19139String">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:param name="rows" select="1"/>
		<xsl:param name="cols" select="40"/>
		<xsl:param name="langId" />
		<xsl:param name="widget" />
		<xsl:param name="validator" />
		
		<xsl:variable name="title">
			<xsl:call-template name="getTitle">
				<xsl:with-param name="name"   select="name(.)"/>
				<xsl:with-param name="schema" select="$schema"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="helpLink">
			<xsl:call-template name="getHelpLink">
				<xsl:with-param name="name"   select="name(.)"/>
				<xsl:with-param name="schema" select="$schema"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="text">
			<xsl:choose>
				<xsl:when test="not($edit=true() and $widget)">
					<!-- Having only gmd:PT_FreeText is allowed by schema.
						So using a PT_FreeText to set a translation even
						in main metadata language could be valid.-->
					<xsl:choose>
						<xsl:when test="not(gco:*)">
							<xsl:for-each select="gmd:PT_FreeText">
								<xsl:call-template name="getElementText">
									<xsl:with-param name="edit" select="$edit" />
									<xsl:with-param name="schema" select="$schema" />
									<xsl:with-param name="rows" select="$rows" />
									<xsl:with-param name="cols" select="$cols" />
									<xsl:with-param name="langId" select="$langId" />
									<xsl:with-param name="validator" select="$validator" />
								</xsl:call-template>
							</xsl:for-each>
						</xsl:when>
						<xsl:otherwise>
							<xsl:for-each select="gco:*">
								<xsl:call-template name="getElementText">
									<xsl:with-param name="edit" select="$edit" />
									<xsl:with-param name="schema" select="$schema" />
									<xsl:with-param name="rows" select="$rows" />
									<xsl:with-param name="cols" select="$cols" />
									<xsl:with-param name="langId" select="$langId" />
									<xsl:with-param name="validator" select="$validator" />
								</xsl:call-template>
							</xsl:for-each>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$widget" />
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="attrs">
			<xsl:for-each select="gco:*/@*">
				<xsl:value-of select="name(.)"/>
			</xsl:for-each>
		</xsl:variable>


		<xsl:choose>
		<xsl:when test="normalize-space($attrs)!=''">
			<xsl:apply-templates mode="complexElement" select=".">
		  	<xsl:with-param name="schema"   select="$schema"/>
				<xsl:with-param name="edit"     select="$edit"/>
				<xsl:with-param name="title"    select="$title"/>
				<xsl:with-param name="helpLink" select="$helpLink"/>
				<xsl:with-param name="content">

				<!-- existing attributes -->
				<xsl:for-each select="gco:*/@*">
					<xsl:apply-templates mode="simpleElement" select=".">
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="edit"   select="$edit"/>
					</xsl:apply-templates>
				</xsl:for-each>

				<!-- existing content -->
				<xsl:apply-templates mode="simpleElement" select=".">
					<xsl:with-param name="schema"   select="$schema"/>
					<xsl:with-param name="edit"     select="$edit"/>
					<xsl:with-param name="title"    select="$title"/>
					<xsl:with-param name="helpLink" select="$helpLink"/>
					<xsl:with-param name="text"     select="$text"/>
				</xsl:apply-templates>
				</xsl:with-param>
			</xsl:apply-templates>
		</xsl:when>
		<xsl:otherwise>
			<xsl:apply-templates mode="simpleElement" select=".">
				<xsl:with-param name="schema"   select="$schema"/>
				<xsl:with-param name="edit"     select="$edit"/>
				<xsl:with-param name="title"    select="$title"/>
				<xsl:with-param name="helpLink" select="$helpLink"/>
				<xsl:with-param name="text"     select="$text"/>
			</xsl:apply-templates>
		</xsl:otherwise>
		</xsl:choose>

	</xsl:template>

	<!-- ==================================================================== -->

	<xsl:template mode="iso19139" match="gco:ScopedName|gco:LocalName">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>

		<xsl:variable name="text">
			<xsl:call-template name="getElementText">
				<xsl:with-param name="edit"   select="$edit"/>
				<xsl:with-param name="schema" select="$schema"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:apply-templates mode="simpleElement" select=".">
			<xsl:with-param name="schema"   select="$schema"/>
			<xsl:with-param name="edit"     select="$edit"/>
			<xsl:with-param name="title"    select="'Name'"/>
			<xsl:with-param name="text"     select="$text"/>
		</xsl:apply-templates>
	</xsl:template>

	<!-- ================================================================= -->
	<!-- some elements that have both attributes and content               -->
	<!-- ================================================================= -->

	<xsl:template mode="iso19139" match="gml:identifier|gml:axisDirection|gml:descriptionReference">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>

		<xsl:apply-templates mode="complexElement" select=".">
			<xsl:with-param name="schema"   select="$schema"/>
			<xsl:with-param name="edit"   	select="$edit"/>
			<xsl:with-param name="content">
		
				<!-- existing attributes -->
				<xsl:apply-templates mode="simpleElement" select="@*">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
		
				<!-- existing content -->
				<xsl:apply-templates mode="simpleElement" select=".">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>

			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>


	<!-- gmx:FileName could be used as substitution of any
		gco:CharacterString. To turn this on add a schema 
		suggestion.
		-->
	<xsl:template mode="iso19139" name="file-upload" match="*[gmx:FileName]">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:apply-templates mode="complexElement" select=".">
			<xsl:with-param name="schema"   select="$schema"/>
			<xsl:with-param name="edit"   	select="$edit"/>
			<xsl:with-param name="content">
				
				<xsl:choose>
					<xsl:when test="$edit">
						<xsl:variable name="id" select="generate-id(.)"/>
						<div id="{$id}"/>
						
						<xsl:variable name="ref" select="gmx:FileName/geonet:element/@ref"/>
						<xsl:variable name="value" select="gmx:FileName"/>
						<xsl:variable name="button" select="normalize-space(gmx:FileName)!=''"/>
						
						<xsl:call-template name="simpleElementGui">
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit" select="$edit"/>
							<xsl:with-param name="title" select="/root/gui/strings/file"/>
							<xsl:with-param name="text">
								<button class="content" onclick="startFileUpload({/root/gmd:MD_Metadata/geonet:info/id}, '{$ref}');" type="button">
									<xsl:value-of select="/root/gui/strings/insertFileMode"/>
								</button>
							</xsl:with-param>
							<xsl:with-param name="id" select="concat('db_',$ref)"/>
							<xsl:with-param name="visible" select="not($button)"/>
						</xsl:call-template>
						
						<xsl:if test="$button">
							<xsl:apply-templates mode="iso19139FileRemove" select="gmx:FileName">
								<xsl:with-param name="access" select="'private'"/>
								<xsl:with-param name="id" select="$id"/>
							</xsl:apply-templates>
						</xsl:if>
						
						<xsl:call-template name="simpleElementGui">
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit" select="$edit"/>
							<xsl:with-param name="title">
								<xsl:call-template name="getTitle">
									<xsl:with-param name="name"   select="name(.)"/>
									<xsl:with-param name="schema" select="$schema"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="text">
								<input id="_{$ref}" class="md" type="text" name="_{$ref}" value="{$value}" size="40" />
							</xsl:with-param>
							<xsl:with-param name="id" select="concat('di_',$ref)"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<!-- Add an hyperlink in view mode -->						
						<xsl:call-template name="simpleElementGui">
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit" select="$edit"/>
							<xsl:with-param name="title">
								<xsl:call-template name="getTitle">
									<xsl:with-param name="name"   select="name(.)"/>
									<xsl:with-param name="schema" select="$schema"/>
								</xsl:call-template>
							</xsl:with-param>
							<xsl:with-param name="text">
								<a href="{gmx:FileName/@src}"><xsl:value-of select="gmx:FileName"/></a>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>



	<!-- ================================================================= -->
	<!-- codelists -->
	<!-- ================================================================= -->

	<xsl:template mode="iso19139" match="gmd:*[*/@codeList]|srv:*[*/@codeList]">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:call-template name="iso19139Codelist">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
		</xsl:call-template>
	</xsl:template>

	
	<!-- ============================================================================= -->

	<xsl:template name="iso19139Codelist">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:apply-templates mode="simpleElement" select=".">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="text">
				<xsl:apply-templates mode="iso19139GetAttributeText" select="*/@codeListValue">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>
	
	
	<!-- LanguageCode is a codelist, but retrieving
	the list of language as defined in the language database table
	allows to create the list for selection.
	
	This table is also used for gmd:language element.
	-->
	<xsl:template mode="iso19139" match="gmd:LanguageCode" priority="2">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:variable name="value" select="@codeListValue" />
		<xsl:variable name="lang" select="/root/gui/language" />
		<xsl:choose>
			<xsl:when test="$edit=true()">
				<select class="md" name="_{geonet:element/@ref}_codeListValue"
					size="1">
					<option name="" />
					
					<xsl:for-each select="/root/gui/isoLang/record">
						<xsl:sort select="label/child::*[name() = $lang]"/>
						<option value="{code}">
							<xsl:if test="code = $value">
								<xsl:attribute name="selected" />
							</xsl:if>
							<xsl:value-of select="label/child::*[name() = $lang]" />
						</option>
					</xsl:for-each>
				</select>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of
					select="/root/gui/isoLang/record[code=$value]/label/child::*[name() = $lang]" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--  Do not allow editing of id to end user. Id is based on language selection
	and iso code.-->
	<xsl:template mode="iso19139" match="gmd:PT_Locale/@id"
		priority="2">
		<xsl:param name="schema" />
		<xsl:param name="edit" />
		
		<xsl:apply-templates mode="simpleElement" select=".">
			<xsl:with-param name="schema" select="$schema" />
			<xsl:with-param name="edit" select="false()" />
		</xsl:apply-templates>
	</xsl:template>
	
	
	<!-- ============================================================================= -->

	<xsl:template mode="iso19139GetAttributeText" match="@*">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:variable name="name"     select="local-name(..)"/>
		<xsl:variable name="qname"    select="name(..)"/>
		<xsl:variable name="value"    select="../@codeListValue"/>
		
		<xsl:choose>
			<xsl:when test="$qname='gmd:LanguageCode'">
				<xsl:apply-templates mode="iso19139" select="..">
					<xsl:with-param name="edit" select="$edit"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<!--
					Get codelist from profil first and use use default one if not
					available.
				-->
				<xsl:variable name="codelistProfil">
					<xsl:choose>
						<xsl:when test="starts-with($schema,'iso19139.')">
							<xsl:copy-of
								select="/root/gui/*[name(.)=$schema]/codelist[@name = $qname]/*" />
						</xsl:when>
						<xsl:otherwise />
					</xsl:choose>
				</xsl:variable>
				
				<xsl:variable name="codelistCore">
					<xsl:choose>
						<xsl:when test="normalize-space($codelistProfil)!=''">
							<xsl:copy-of select="$codelistProfil" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of
								select="/root/gui/*[name(.)='iso19139']/codelist[@name = $qname]/*" />
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>
				
				<xsl:variable name="codelist" select="exslt:node-set($codelistCore)" />
				<xsl:variable name="isXLinked" select="count(ancestor-or-self::node()[@xlink:href]) > 0" />

				<xsl:choose>
					<xsl:when test="$edit=true()">
						<!-- codelist in edit mode -->
						<select class="md" name="_{../geonet:element/@ref}_{name(.)}" id="_{../geonet:element/@ref}_{name(.)}" size="1">
							<!-- Check element is mandatory or not -->
							<xsl:if test="../../geonet:element/@min='1' and $edit">
								<xsl:attribute name="onchange">validateNonEmpty(this);</xsl:attribute>
							</xsl:if>
							<xsl:if test="$isXLinked">
								<xsl:attribute name="disabled">disabled</xsl:attribute>
							</xsl:if>
							<option name=""/>
							<xsl:for-each select="$codelist/entry[not(@hideInEditMode)]">
								<xsl:sort select="label"/>
								<option>
									<xsl:if test="code=$value">
										<xsl:attribute name="selected"/>
									</xsl:if>
									<xsl:attribute name="value"><xsl:value-of select="code"/></xsl:attribute>
									<xsl:value-of select="label"/>
								</option>
							</xsl:for-each>
						</select>
					</xsl:when>
					<xsl:otherwise>
						<!-- codelist in view mode -->
						<xsl:if test="normalize-space($value)!=''">
							<b><xsl:value-of select="$codelist/entry[code = $value]/label"/></b>
							<xsl:value-of select="concat(': ',$codelist/entry[code = $value]/description)"/>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
		<!--
		<xsl:call-template name="getAttributeText">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
		</xsl:call-template>
		-->
	</xsl:template>
	
	<!-- ============================================================================= -->
	<!--
	make the following fields always not editable:
	dateStamp
	metadataStandardName
	metadataStandardVersion
	fileIdentifier
	characterSet
	-->
	<!-- ============================================================================= -->

	<xsl:template mode="iso19139" match="gmd:dateStamp|gmd:fileIdentifier" priority="2">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:apply-templates mode="simpleElement" select=".">
			<xsl:with-param name="schema"  select="$schema"/>
			<xsl:with-param name="edit"    select="false()"/>
			<xsl:with-param name="text">
				<xsl:choose>
					<xsl:when test="normalize-space(gco:*)=''">
						<span class="info">
							- <xsl:value-of select="/root/gui/strings/setOnSave"/> - 
						</span>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="gco:*"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>

	<!-- Attributes 
	 * gmd:PT_Locale/@id is set by update-fixed-info using first 2 letters.
	-->
	<xsl:template mode="iso19139" match="gmd:PT_Locale/@id"
		priority="2">
		<xsl:param name="schema" />
		<xsl:param name="edit" />
		
		<xsl:apply-templates mode="simpleElement" select=".">
			<xsl:with-param name="schema" select="$schema" />
			<xsl:with-param name="edit" select="false()" />
		</xsl:apply-templates>
	</xsl:template>
	

	<xsl:template mode="iso19139" match="//gmd:MD_Metadata/gmd:characterSet|//*[@gco:isoType='gmd:MD_Metadata']/gmd:characterSet" priority="2">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:call-template name="iso19139Codelist">
			<xsl:with-param name="schema"  select="$schema"/>
			<xsl:with-param name="edit"    select="false()"/>
		</xsl:call-template>
	</xsl:template>
	
	<!-- ============================================================================= -->
	<!-- electronicMailAddress -->
	<!-- ============================================================================= -->

	<xsl:template mode="iso19139" match="gmd:electronicMailAddress" priority="2">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:choose>
			<xsl:when test="$edit=true()">
				<xsl:call-template name="iso19139String">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="true()"/>
					<xsl:with-param name="validator" select="'validateEmail(this);'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates mode="simpleElement" select=".">
					<xsl:with-param name="schema"  select="$schema"/>
					<xsl:with-param name="text">
						<a href="mailto:{string(.)}"><xsl:value-of select="string(.)"/></a>
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- ============================================================================= -->
	<!-- descriptiveKeywords -->
	<!-- ============================================================================= -->
	<xsl:template mode="iso19139" match="gmd:descriptiveKeywords">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:choose>
			<xsl:when test="$edit=true()">
		
				<xsl:variable name="content">
					<xsl:for-each select="gmd:MD_Keywords">
					<tr>
						<td class="padded-content" width="100%" colspan="2">
							<table width="100%">
								<tr>
									<td width="50%" valign="top">
										<table width="100%">
											<xsl:apply-templates mode="elementEP" select="gmd:keyword|geonet:child[string(@name)='keyword']">
												<xsl:with-param name="schema" select="$schema"/>
												<xsl:with-param name="edit"   select="$edit"/>
											</xsl:apply-templates>
											<xsl:apply-templates mode="elementEP" select="gmd:type|geonet:child[string(@name)='type']">
												<xsl:with-param name="schema" select="$schema"/>
												<xsl:with-param name="edit"   select="$edit"/>
											</xsl:apply-templates>
										</table>
									</td>
									<td valign="top">
										<table width="100%">
											<xsl:apply-templates mode="elementEP" select="gmd:thesaurusName|geonet:child[string(@name)='thesaurusName']">
												<xsl:with-param name="schema" select="$schema"/>
												<xsl:with-param name="edit"   select="$edit"/>
											</xsl:apply-templates>
										</table>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					</xsl:for-each>
				</xsl:variable>
				
				<xsl:apply-templates mode="complexElement" select=".">
					<xsl:with-param name="schema"  select="$schema"/>
					<xsl:with-param name="edit"    select="$edit"/>
					<xsl:with-param name="content" select="$content"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates mode="simpleElement" select=".">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="text">
						<xsl:variable name="value">
							<xsl:for-each select="gmd:MD_Keywords/gmd:keyword">
								<xsl:if test="position() &gt; 1"><xsl:text>, </xsl:text></xsl:if>
								<xsl:value-of select="."/>
							</xsl:for-each>
							<xsl:if test="gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue!=''">
								<xsl:text> (</xsl:text>
								<xsl:value-of select="gmd:MD_Keywords/gmd:type/gmd:MD_KeywordTypeCode/@codeListValue"/>
								<xsl:text>)</xsl:text>
							</xsl:if>
							<xsl:text>.</xsl:text>
						</xsl:variable>
						<!-- Clean new lines which may be added by formatting. -->
						<xsl:value-of select="normalize-space($value)"/>
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ============================================================================= -->
	<!-- place keyword; only called in edit mode (see descriptiveKeywords template) -->
	<!-- ============================================================================= -->

	<xsl:template mode="iso19139" match="gmd:keyword[following-sibling::gmd:type/gmd:MD_KeywordTypeCode/@codeListValue='place']">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:variable name="text">
			<xsl:variable name="ref" select="gco:CharacterString/geonet:element/@ref"/>
			<xsl:variable name="keyword" select="gco:CharacterString/text()"/>
			
			<input class="md" type="text" name="_{$ref}" value="{gco:CharacterString/text()}" size="40" />

			<!-- regions combobox -->

			<xsl:variable name="lang" select="/root/gui/language"/>
			<xsl:text> </xsl:text>
			<select name="place" size="1" onChange="document.mainForm._{$ref}.value=this.options[this.selectedIndex].text" class="md">
				<option value=""/>
				<xsl:for-each select="/root/gui/regions/record">
					<xsl:sort select="label/child::*[name() = $lang]" order="ascending"/>
					<option value="{id}">
						<xsl:if test="string(label/child::*[name() = $lang])=$keyword">
							<xsl:attribute name="selected"/>
						</xsl:if>
						<xsl:value-of select="label/child::*[name() = $lang]"/>
					</option>
				</xsl:for-each>
			</select>
		</xsl:variable>
		<xsl:apply-templates mode="simpleElement" select=".">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="true()"/>
			<xsl:with-param name="text"   select="$text"/>
		</xsl:apply-templates>
	</xsl:template>
			

	<!-- ============================================================================= -->
	<!--
	dateTime (format = %Y-%m-%dT%H:%M:00)
	usageDateTime
	plannedAvailableDateTime
	-->
	<!-- ============================================================================= -->

	<xsl:template mode="iso19139" match="gmd:dateTime|gmd:usageDateTime|gmd:plannedAvailableDateTime" priority="2">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:choose>
			<xsl:when test="$edit=true()">
				<xsl:apply-templates mode="simpleElement" select=".">
					<xsl:with-param name="schema"  select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="text">
						<xsl:variable name="ref" select="gco:Date/geonet:element/@ref|gco:DateTime/geonet:element/@ref"/>
						<xsl:variable name="format">
							<xsl:choose>
								<xsl:when test="gco:Date"><xsl:text>%Y-%m-%d</xsl:text></xsl:when>
								<xsl:otherwise><xsl:text>%Y-%m-%dT%H:%M:00</xsl:text></xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						
						<xsl:call-template name="calendar">
							<xsl:with-param name="ref" select="$ref"/>
							<xsl:with-param name="date" select="gco:DateTime/text()|gco:Date/text()"/>
							<xsl:with-param name="format" select="$format"/>
						</xsl:call-template>
						
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="iso19139String">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ============================================================================= -->
	<!--
	date (format = %Y-%m-%d)
	editionDate
	dateOfNextUpdate
	mdDateSt is not editable (!we use DateTime instead of only Date!)
	-->
	<!-- ============================================================================= -->

	<xsl:template mode="iso19139" match="gmd:date[gco:DateTime|gco:Date]|gmd:editionDate|gmd:dateOfNextUpdate" priority="2">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:choose>
			<xsl:when test="$edit=true()">
				<xsl:apply-templates mode="simpleElement" select=".">
					<xsl:with-param name="schema"  select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="text">
						<xsl:variable name="ref" select="gco:DateTime/geonet:element/@ref|gco:Date/geonet:element/@ref"/>
						<xsl:variable name="format">
							<xsl:choose>
								<xsl:when test="gco:Date"><xsl:text>%Y-%m-%d</xsl:text></xsl:when>
								<xsl:otherwise><xsl:text>%Y-%m-%dT%H:%M:00</xsl:text></xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						
						<xsl:call-template name="calendar">
							<xsl:with-param name="ref" select="$ref"/>
							<xsl:with-param name="date" select="gco:DateTime/text()|gco:Date/text()"/>
							<xsl:with-param name="format" select="$format"/>
						</xsl:call-template>
						
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="iso19139String">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ===================================================================== -->
	<!-- gml:TimePeriod (format = %Y-%m-%dThh:mm:ss) -->
	<!-- ===================================================================== -->

	<xsl:template mode="iso19139" match="gml:*[gml:beginPosition|gml:endPosition]|gml:TimeInstant[gml:timePosition]" priority="2">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:for-each select="gml:beginPosition|gml:endPosition|gml:timePosition">
		<xsl:choose>
			<xsl:when test="$edit=true()">
				<xsl:apply-templates mode="simpleElement" select=".">
					<xsl:with-param name="schema"  select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="text">
						<xsl:variable name="ref" select="geonet:element/@ref"/>
						<xsl:variable name="format"><xsl:text>%Y-%m-%dT%H:%M:00</xsl:text></xsl:variable>
						
						<xsl:call-template name="calendar">
							<xsl:with-param name="ref" select="$ref"/>
							<xsl:with-param name="date" select="text()"/>
							<xsl:with-param name="format" select="$format"/>
						</xsl:call-template>
												
						<xsl:if test="@indeterminatePosition">
							<xsl:apply-templates mode="simpleElement" select="@indeterminatePosition">
								<xsl:with-param name="schema" select="$schema"/>
								<xsl:with-param name="edit"   select="$edit"/>
							</xsl:apply-templates>
						</xsl:if>
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates mode="simpleElement" select=".">
					<xsl:with-param name="schema"  select="$schema"/>
					<xsl:with-param name="text">
						<xsl:choose>
							<xsl:when test="normalize-space(.)=''">
								<xsl:value-of select="@indeterminatePosition"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="text()"/>
								<xsl:if test="@indeterminatePosition">
									&#160;
									<xsl:value-of select="concat('Qualified by indeterminatePosition',': ',@indeterminatePosition)"/>
								</xsl:if>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
		</xsl:for-each>
	</xsl:template>
	
	<!-- =================================================================== -->
	<!-- subtemplates -->
	<!-- =================================================================== -->

	<xsl:template mode="iso19139" match="*[geonet:info/isTemplate='s']" priority="3">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:apply-templates mode="element" select=".">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- =================================================================== -->
	<!--
	placeholder
	<xsl:template mode="iso19139" match="TAG">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		BODY
	</xsl:template>
	-->
	<!-- ==================================================================== -->

	<xsl:template mode="iso19139" match="@gco:isoType"/>

	<!-- ==================================================================== -->
	<!-- Metadata -->
	<!-- ==================================================================== -->

	<xsl:template mode="iso19139" match="gmd:MD_Metadata|*[@gco:isoType='gmd:MD_Metadata']">
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

	<!-- ============================================================================= -->

	<xsl:template name="isotabs">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:param name="dataset"/>
		<xsl:param name="core"/>

		<!-- dataset or resource info in its own box -->
	
		<xsl:for-each select="gmd:identificationInfo/gmd:MD_DataIdentification|
						gmd:identificationInfo/srv:SV_ServiceIdentification|
						gmd:identificationInfo/*[@gco:isoType='gmd:MD_DataIdentification']|
						gmd:identificationInfo/*[@gco:isoType='srv:SV_ServiceIdentification']">
			<xsl:call-template name="complexElementGuiWrapper">
				<xsl:with-param name="title">
				<xsl:choose>
					<xsl:when test="$dataset=true()">
						<xsl:value-of select="/root/gui/iso19139/element[@name='gmd:MD_DataIdentification']/label"/>
					</xsl:when>
					<xsl:when test="local-name(.)='SV_ServiceIdentification' or @gco:isoType='srv:SV_ServiceIdentification'">
						<xsl:value-of select="/root/gui/iso19139/element[@name='srv:SV_ServiceIdentification']/label"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="'Resource Identification'"/><!-- FIXME i18n-->
					</xsl:otherwise>
				</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="content">
		
				<xsl:apply-templates mode="elementEP" select="gmd:citation/gmd:CI_Citation/gmd:title|gmd:citation/gmd:CI_Citation/geonet:child[string(@name)='title']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>

				<xsl:apply-templates mode="elementEP" select="gmd:citation/gmd:CI_Citation/gmd:date|gmd:citation/gmd:CI_Citation/geonet:child[string(@name)='date']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>

				<xsl:apply-templates mode="elementEP" select="gmd:abstract|geonet:child[string(@name)='abstract']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>

				<xsl:apply-templates mode="elementEP" select="gmd:pointOfContact|geonet:child[string(@name)='pointOfContact']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>

				<xsl:apply-templates mode="elementEP" select="gmd:descriptiveKeywords|geonet:child[string(@name)='descriptiveKeywords']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>

				<xsl:if test="$core and $dataset">
					<xsl:apply-templates mode="elementEP" select="gmd:spatialRepresentationType|geonet:child[string(@name)='spatialRepresentationType']">
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="edit"   select="$edit"/>
					</xsl:apply-templates>

					<xsl:apply-templates mode="elementEP" select="gmd:spatialResolution|geonet:child[string(@name)='spatialResolution']">
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="edit"   select="$edit"/>
					</xsl:apply-templates>
				</xsl:if>

				<xsl:apply-templates mode="elementEP" select="gmd:language|geonet:child[string(@name)='language']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>

				<xsl:apply-templates mode="elementEP" select="gmd:characterSet|geonet:child[string(@name)='characterSet']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>

				<xsl:apply-templates mode="elementEP" select="gmd:topicCategory|geonet:child[string(@name)='topicCategory']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>

				<xsl:if test="$dataset">
					<xsl:for-each select="gmd:extent/gmd:EX_Extent">
						<xsl:call-template name="complexElementGuiWrapper">
							<xsl:with-param name="title" select="/root/gui/iso19139/element[@name='gmd:EX_Extent']/label"/>
							<xsl:with-param name="content">
								<xsl:apply-templates mode="elementEP" select="*">
									<xsl:with-param name="schema" select="$schema"/>
									<xsl:with-param name="edit"   select="$edit"/>
								</xsl:apply-templates>
							</xsl:with-param>
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit"   select="$edit"/>
							<xsl:with-param name="realname"   select="'gmd:EX_Extent'"/>
						</xsl:call-template>
					</xsl:for-each>
				</xsl:if>

				</xsl:with-param>
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
				<xsl:with-param name="realname"   select="name(.)"/>
			</xsl:call-template>
		</xsl:for-each>

		<xsl:if test="$core and $dataset">

		<!-- scope and lineage in their own box -->
		
			<xsl:call-template name="complexElementGuiWrapper">
				<xsl:with-param name="title" select="/root/gui/iso19139/element[@name='gmd:LI_Lineage']/label"/>
				<xsl:with-param name="id" select="generate-id(/root/gui/iso19139/element[@name='gmd:LI_Lineage']/label)"/>
				<xsl:with-param name="content">

					<xsl:for-each select="gmd:dataQualityInfo/gmd:DQ_DataQuality">
						<xsl:apply-templates mode="elementEP" select="gmd:scope|geonet:child[string(@name)='scope']">
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit"   select="$edit"/>
						</xsl:apply-templates>

						<xsl:apply-templates mode="elementEP" select="gmd:lineage|geonet:child[string(@name)='lineage']">
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit"   select="$edit"/>
						</xsl:apply-templates>
					</xsl:for-each>

				</xsl:with-param>
				<xsl:with-param name="schema" select="$schema"/>
      	<xsl:with-param name="group" select="/root/gui/strings/dataQualityTab"/>
      	<xsl:with-param name="edit" select="$edit"/>
				<xsl:with-param name="realname"   select="'gmd:DataQualityInfo'"/>
			</xsl:call-template>

		<!-- referenceSystemInfo in its own box -->
		
			<xsl:call-template name="complexElementGuiWrapper">
				<xsl:with-param name="title" select="/root/gui/iso19139/element[@name='gmd:referenceSystemInfo']/label"/>
				<xsl:with-param name="id" select="generate-id(/root/gui/iso19139/element[@name='gmd:referenceSystemInfo']/label)"/>
				<xsl:with-param name="content">

				<xsl:for-each select="gmd:referenceSystemInfo/gmd:MD_ReferenceSystem">
					<xsl:apply-templates mode="elementEP" select="gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:code|gmd:referenceSystemIdentifier/gmd:RS_Identifier/geonet:child[string(@name)='code']">
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="edit"   select="$edit"/>
					</xsl:apply-templates>

					<xsl:apply-templates mode="elementEP" select="gmd:referenceSystemIdentifier/gmd:RS_Identifier/gmd:codeSpace|gmd:referenceSystemIdentifier/gmd:RS_Identifier/geonet:child[string(@name)='codeSpace']">
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="edit"   select="$edit"/>
					</xsl:apply-templates>
				</xsl:for-each>

				</xsl:with-param>
				<xsl:with-param name="schema" select="$schema"/>
      	<xsl:with-param name="group" select="/root/gui/strings/refSysTab"/>
      	<xsl:with-param name="edit" select="$edit"/>
				<xsl:with-param name="realname"   select="'gmd:referenceSystemInfo'"/>
			</xsl:call-template>

			<!-- distribution Format and onlineResource(s) in their own box -->

    	<xsl:call-template name="complexElementGuiWrapper">
    		<xsl:with-param name="title" select="/root/gui/iso19139/element[@name='gmd:distributionInfo']/label"/>
    		<xsl:with-param name="id" select="generate-id(/root/gui/iso19139/element[@name='gmd:distributionInfo']/label)"/>
      	<xsl:with-param name="content">

				<xsl:for-each select="gmd:distributionInfo">
        	<xsl:apply-templates mode="elementEP" select="*/gmd:distributionFormat|*/geonet:child[string(@name)='distributionFormat']">
          	<xsl:with-param name="schema" select="$schema"/>
          	<xsl:with-param name="edit"   select="$edit"/>
        	</xsl:apply-templates>

        	<xsl:apply-templates mode="elementEP" select="*/gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine|*/gmd:transferOptions/gmd:MD_DigitalTransferOptions/geonet:child[string(@name)='onLine']">
          	<xsl:with-param name="schema" select="$schema"/>
          	<xsl:with-param name="edit"   select="$edit"/>
        	</xsl:apply-templates>
				</xsl:for-each>

      	</xsl:with-param>
      	<xsl:with-param name="schema" select="$schema"/>
      	<xsl:with-param name="group" select="/root/gui/strings/distributionTab"/>
      	<xsl:with-param name="edit" select="$edit"/>
      	<xsl:with-param name="realname" select="gmd:distributionInfo"/>
    	</xsl:call-template>
			
		</xsl:if>

		<!-- metadata info in its own box -->

		<xsl:call-template name="complexElementGuiWrapper">
			<xsl:with-param name="title" select="/root/gui/iso19139/element[@name='gmd:MD_Metadata']/label"/>
			<xsl:with-param name="id" select="generate-id(/root/gui/iso19139/element[@name='gmd:MD_Metadata']/label)"/>
			<xsl:with-param name="content">

			<xsl:apply-templates mode="elementEP" select="gmd:fileIdentifier|geonet:child[string(@name)='fileIdentifier']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:language|geonet:child[string(@name)='language']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:characterSet|geonet:child[string(@name)='characterSet']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>

			<xsl:apply-templates mode="elementEP" select="gmd:parentIdentifier|geonet:child[string(@name)='parentIdentifier']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:hierarchyLevel|geonet:child[string(@name)='hierarchyLevel']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
	
			<xsl:apply-templates mode="elementEP" select="gmd:hierarchyLevelName|geonet:child[string(@name)='hierarchyLevelName']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>

			<!-- metadata contact info in its own box -->

			<xsl:for-each select="gmd:contact">

				<xsl:call-template name="complexElementGuiWrapper">
					<xsl:with-param name="title" select="/root/gui/iso19139/element[@name='gmd:contact']/label"/>
					<xsl:with-param name="content">

						<xsl:apply-templates mode="elementEP" select="*/gmd:individualName|*/geonet:child[string(@name)='individualName']">
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit"   select="$edit"/>
						</xsl:apply-templates>

						<xsl:apply-templates mode="elementEP" select="*/gmd:organisationName|*/geonet:child[string(@name)='organisationName']">
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit"   select="$edit"/>
						</xsl:apply-templates>

						<xsl:apply-templates mode="elementEP" select="*/gmd:positionName|*/geonet:child[string(@name)='positionName']">
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit"   select="$edit"/>
						</xsl:apply-templates>

						<xsl:if test="$core and $dataset">
							<xsl:apply-templates mode="elementEP" select="*/gmd:contactInfo|*/geonet:child[string(@name)='contactInfo']">
            		<xsl:with-param name="schema" select="$schema"/>
            		<xsl:with-param name="edit"   select="$edit"/>
        			</xsl:apply-templates>
						</xsl:if>

						<xsl:apply-templates mode="elementEP" select="*/gmd:role|*/geonet:child[string(@name)='role']">
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit"   select="$edit"/>
						</xsl:apply-templates>

					</xsl:with-param>
					<xsl:with-param name="schema" select="$schema"/>
      		<xsl:with-param name="group" select="/root/gui/strings/metadata"/>
      		<xsl:with-param name="edit" select="$edit"/>
				</xsl:call-template>
		
			</xsl:for-each>

			<!-- more metadata elements -->

			<xsl:apply-templates mode="elementEP" select="gmd:dateStamp|geonet:child[string(@name)='dateStamp']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:if test="$core and $dataset">
				<xsl:apply-templates mode="elementEP" select="gmd:metadataStandardName|geonet:child[string(@name)='metadataStandardName']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
	
				<xsl:apply-templates mode="elementEP" select="gmd:metadataStandardVersion|geonet:child[string(@name)='metadataStandardVersion']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:if>

			</xsl:with-param>
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="group" select="/root/gui/strings/metadataTab"/>
			<xsl:with-param name="edit" select="$edit"/>
		</xsl:call-template>
		
	</xsl:template>

	<!-- ============================================================================= 
	Create a complex element with the content param in it.
	
	@param id : If using complexElementGuiWrapper in a same for-each statement, generate-id function will
	be identical for all call to this template (because id is computed on base node).
	In some situation it could be better to define id parameter when calling the template
	to override default values (eg. id are used for collapsible fieldset).
	-->

	<xsl:template name="complexElementGuiWrapper">
		<xsl:param name="title"/>
		<xsl:param name="content"/>
		<xsl:param name="schema"/>
		<xsl:param name="group"/>
		<xsl:param name="edit"/>
		<xsl:param name="realname" select="name(.)"/>
		<xsl:param name="id" select="generate-id(.)"/>
		
		<!-- do not show empty elements when editing -->

		<xsl:choose>
		<xsl:when test="normalize-space($content)!=''">
			<xsl:call-template name="complexElementGui">
				<xsl:with-param name="title" select="$title"/>
				<xsl:with-param name="content" select="$content"/>
				<xsl:with-param name="helpLink">
					<xsl:call-template name="getHelpLink">
						<xsl:with-param name="name"   select="$realname"/>
						<xsl:with-param name="schema" select="$schema"/>
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="id" select="$id"/>
			</xsl:call-template>
		</xsl:when>
		<xsl:otherwise>
			<xsl:choose>
				<xsl:when test="$edit">
					<xsl:call-template name="complexElementGui">
						<xsl:with-param name="title" select="$title"/>
						<xsl:with-param name="content">
							<span class="missing"> - <xsl:value-of select="/root/gui/strings/missingSeeTab"/> "<xsl:value-of select="$group"/>" - </span>
						</xsl:with-param>
						<xsl:with-param name="helpLink">
							<xsl:call-template name="getHelpLink">
								<xsl:with-param name="name"   select="$realname"/>
								<xsl:with-param name="schema" select="$schema"/>
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="id" select="$id"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:call-template name="complexElementGui">
						<xsl:with-param name="title" select="$title"/>
						<xsl:with-param name="helpLink">
							<xsl:call-template name="getHelpLink">
								<xsl:with-param name="name"   select="$realname"/>
								<xsl:with-param name="schema" select="$schema"/>
							</xsl:call-template>
						</xsl:with-param>
						<xsl:with-param name="content">
							<span class="missing"> - <xsl:value-of select="/root/gui/strings/missing"/> - </span>
						</xsl:with-param>
						<xsl:with-param name="schema" select="$schema"/>
						<xsl:with-param name="id" select="$id"/>
					</xsl:call-template>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:otherwise>
		</xsl:choose>
				
	</xsl:template>

	<!-- ================================================================== -->
	<!-- complete mode we just display everything - tab = complete          -->
	<!-- ================================================================== -->

	<xsl:template name="iso19139Complete">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>

		<xsl:apply-templates mode="elementEP" select="gmd:identificationInfo|geonet:child[string(@name)='identificationInfo']">
      <xsl:with-param name="schema" select="$schema"/>
      <xsl:with-param name="edit"   select="$edit"/>
    </xsl:apply-templates>

		<xsl:apply-templates mode="elementEP" select="gmd:spatialRepresentationInfo|geonet:child[string(@name)='spatialRepresentationInfo']">
		  <xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
		</xsl:apply-templates>
	
		<xsl:apply-templates mode="elementEP" select="gmd:referenceSystemInfo|geonet:child[string(@name)='referenceSystemInfo']">
		  <xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
		</xsl:apply-templates>

		<xsl:apply-templates mode="elementEP" select="gmd:contentInfo|geonet:child[string(@name)='contentInfo']">
      <xsl:with-param name="schema" select="$schema"/>
      <xsl:with-param name="edit"   select="$edit"/>
    </xsl:apply-templates>

		<xsl:apply-templates mode="elementEP" select="gmd:distributionInfo|geonet:child[string(@name)='distributionInfo']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
		</xsl:apply-templates> 

		<xsl:apply-templates mode="elementEP" select="gmd:dataQualityInfo|geonet:child[string(@name)='dataQualityInfo']">
      <xsl:with-param name="schema" select="$schema"/>
      <xsl:with-param name="edit"   select="$edit"/>
    </xsl:apply-templates>

		<xsl:apply-templates mode="elementEP" select="gmd:portrayalCatalogueInfo|geonet:child[string(@name)='portrayalCatalogueInfo']">
      <xsl:with-param name="schema" select="$schema"/>
      <xsl:with-param name="edit"   select="$edit"/>
    </xsl:apply-templates>

		<xsl:apply-templates mode="elementEP" select="gmd:metadataConstraints|geonet:child[string(@name)='metadataConstraints']">
      <xsl:with-param name="schema" select="$schema"/>
      <xsl:with-param name="edit"   select="$edit"/>
    </xsl:apply-templates>

		<xsl:apply-templates mode="elementEP" select="gmd:applicationSchemaInfo|geonet:child[string(@name)='applicationSchemaInfo']">
      <xsl:with-param name="schema" select="$schema"/>
      <xsl:with-param name="edit"   select="$edit"/>
    </xsl:apply-templates>

		<xsl:apply-templates mode="elementEP" select="gmd:metadataMaintenance|geonet:child[string(@name)='metadataMaintenance']">
      <xsl:with-param name="schema" select="$schema"/>
      <xsl:with-param name="edit"   select="$edit"/>
    </xsl:apply-templates>

		<xsl:call-template name="complexElementGuiWrapper">
			<xsl:with-param name="title" select="'Metadata Info'"/>
			<xsl:with-param name="content">

			<xsl:apply-templates mode="elementEP" select="gmd:fileIdentifier|geonet:child[string(@name)='fileIdentifier']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:language|geonet:child[string(@name)='language']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:characterSet|geonet:child[string(@name)='characterSet']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:parentIdentifier|geonet:child[string(@name)='parentIdentifier']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:hierarchyLevel|geonet:child[string(@name)='hierarchyLevel']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:hierarchyLevelName|geonet:child[string(@name)='hierarchyLevelName']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>

			<xsl:apply-templates mode="elementEP" select="gmd:contact|geonet:child[string(@name)='contact']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:dateStamp|geonet:child[string(@name)='dateStamp']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:metadataStandardName|geonet:child[string(@name)='metadataStandardName']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:metadataStandardVersion|geonet:child[string(@name)='metadataStandardVersion']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:dataSetURI|geonet:child[string(@name)='dataSetURI']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:locale|geonet:child[string(@name)='locale']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>

			<xsl:apply-templates mode="elementEP" select="gmd:series|geonet:child[string(@name)='series']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:describes|geonet:child[string(@name)='describes']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:propertyType|geonet:child[string(@name)='propertyType']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:featureType|geonet:child[string(@name)='featureType']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>
		
			<xsl:apply-templates mode="elementEP" select="gmd:featureAttribute|geonet:child[string(@name)='featureAttribute']">
				<xsl:with-param name="schema" select="$schema"/>
				<xsl:with-param name="edit"   select="$edit"/>
			</xsl:apply-templates>

			</xsl:with-param>
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="group" select="/root/gui/strings/metadataTab"/>
			<xsl:with-param name="edit" select="$edit"/>
		</xsl:call-template>

<!-- metadata Extension Information - dead last because its boring and
     can clutter up the rest of the metadata record! -->

		<xsl:apply-templates mode="elementEP" select="gmd:metadataExtensionInfo|geonet:child[string(@name)='metadataExtensionInfo']">
      <xsl:with-param name="schema" select="$schema"/>
      <xsl:with-param name="edit"   select="$edit"/>
    </xsl:apply-templates>

	</xsl:template>
	
	
	<!-- ============================================================================= -->

  <xsl:template name="iso19139Metadata">
    <xsl:param name="schema"/>
    <xsl:param name="edit"/>
  	
  	<xsl:variable name="ref" select="concat('#_',geonet:element/@ref)"/>  	
  	<xsl:variable name="validationLink">
  		<xsl:call-template name="validationLink">
  			<xsl:with-param name="ref" select="$ref"/>
  		</xsl:call-template>  		
  	</xsl:variable>
  	
  	<xsl:call-template name="complexElementGui">
  		<xsl:with-param name="title" select="/root/gui/strings/metadata"/>
  		<xsl:with-param name="validationLink" select="$validationLink"/>
  		<xsl:with-param name="edit" select="true()"/>
  		<xsl:with-param name="content">
  	
			<!-- if the parent is root then display fields not in tabs -->
				<xsl:choose>
	    		<xsl:when test="name(..)='root'">
			    <xsl:apply-templates mode="elementEP" select="gmd:fileIdentifier|geonet:child[string(@name)='fileIdentifier']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:language|geonet:child[string(@name)='language']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:characterSet|geonet:child[string(@name)='characterSet']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:parentIdentifier|geonet:child[string(@name)='parentIdentifier']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:hierarchyLevel|geonet:child[string(@name)='hierarchyLevel']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:hierarchyLevelName|geonet:child[string(@name)='hierarchyLevelName']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:dateStamp|geonet:child[string(@name)='dateStamp']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
					<xsl:apply-templates mode="elementEP" select="gmd:metadataStandardName|geonet:child[string(@name)='metadataStandardName']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:metadataStandardVersion|geonet:child[string(@name)='metadataStandardVersion']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:contact|geonet:child[string(@name)='contact']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:dataSetURI|geonet:child[string(@name)='dataSetURI']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:locale|geonet:child[string(@name)='locale']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:series|geonet:child[string(@name)='series']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:describes|geonet:child[string(@name)='describes']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:propertyType|geonet:child[string(@name)='propertyType']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
				<xsl:apply-templates mode="elementEP" select="gmd:featureType|geonet:child[string(@name)='featureType']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
		
		    	<xsl:apply-templates mode="elementEP" select="gmd:featureAttribute|geonet:child[string(@name)='featureAttribute']">
		      	<xsl:with-param name="schema" select="$schema"/>
		      	<xsl:with-param name="edit"   select="$edit"/>
		    	</xsl:apply-templates>
			</xsl:when>
			<!-- otherwise, display everything because we have embedded MD_Metadata -->
			<xsl:otherwise>
				<xsl:apply-templates mode="elementEP" select="*">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:otherwise>
			</xsl:choose>

  		</xsl:with-param>
  		<xsl:with-param name="schema" select="$schema"/>
  	</xsl:call-template>
  	
  </xsl:template>
	
	<!-- ============================================================================= -->
	<!--
	simple mode; ISO order is:
	- gmd:fileIdentifier
	- gmd:language
	- gmd:characterSet
	- gmd:parentIdentifier
	- gmd:hierarchyLevel
	- gmd:hierarchyLevelName
	- gmd:contact
	- gmd:dateStamp
	- gmd:metadataStandardName
	- gmd:metadataStandardVersion
	+ gmd:dataSetURI
	+ gmd:locale
	- gmd:spatialRepresentationInfo
	- gmd:referenceSystemInfo
	- gmd:metadataExtensionInfo
	- gmd:identificationInfo
	- gmd:contentInfo
	- gmd:distributionInfo
	- gmd:dataQualityInfo
	- gmd:portrayalCatalogueInfo
	- gmd:metadataConstraints
	- gmd:applicationSchemaInfo
	- gmd:metadataMaintenance
	+ gmd:series
	+ gmd:describes
	+ gmd:propertyType
	+ gmd:featureType
	+ gmd:featureAttribute
	-->
	<!-- ============================================================================= -->

	<xsl:template name="iso19139Simple">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:param name="flat"/>

		<xsl:apply-templates mode="elementEP" select="gmd:identificationInfo|geonet:child[string(@name)='identificationInfo']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:distributionInfo|geonet:child[string(@name)='distributionInfo']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:spatialRepresentationInfo|geonet:child[string(@name)='spatialRepresentationInfo']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:referenceSystemInfo|geonet:child[string(@name)='referenceSystemInfo']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:applicationSchemaInfo|geonet:child[string(@name)='applicationSchemaInfo']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:portrayalCatalogueInfo|geonet:child[string(@name)='portrayalCatalogueInfo']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:dataQualityInfo|geonet:child[string(@name)='dataQualityInfo']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:metadataConstraints|geonet:child[string(@name)='metadataConstraints']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:call-template name="complexElementGui">
			<xsl:with-param name="title" select="/root/gui/strings/metadata"/>
			<xsl:with-param name="content">
				<xsl:call-template name="iso19139Simple2">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
					<xsl:with-param name="flat"   select="$flat"/>
				</xsl:call-template>
			</xsl:with-param>
			<xsl:with-param name="schema" select="$schema"/>
		</xsl:call-template>
		
		<xsl:apply-templates mode="elementEP" select="gmd:contentInfo|geonet:child[string(@name)='contentInfo']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:metadataExtensionInfo|geonet:child[string(@name)='metadataExtensionInfo']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
	</xsl:template>
	
	<!-- ============================================================================= -->

	<xsl:template mode="iso19139" match="//gmd:language" priority="20">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>

		<xsl:apply-templates mode="simpleElement" select=".">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="text">
				<xsl:apply-templates mode="iso19139GetIsoLanguage" select="gco:CharacterString">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="$edit"/>
				</xsl:apply-templates>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- ============================================================================= -->

	<xsl:template mode="iso19139GetIsoLanguage" match="*">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:variable name="lang"  select="/root/gui/language"/>
		<xsl:variable name="value" select="string(.)"/>
		
		<xsl:choose>
			<xsl:when test="$edit=true()">
				<select class="md" name="_{geonet:element/@ref}" size="1">
					<option name=""/>

					<xsl:for-each select="/root/gui/isoLang/record">
						<option value="{code}">
							<xsl:if test="code = $value">
								<xsl:attribute name="selected"/>
							</xsl:if>							
							<xsl:value-of select="label/child::*[name() = $lang]"/>
						</option>
					</xsl:for-each>
				</select>
			</xsl:when>

			<xsl:otherwise>
				<xsl:value-of select="/root/gui/isoLang/record[code=$value]/label/child::*[name() = $lang]"/>
			</xsl:otherwise>
		</xsl:choose>		
	</xsl:template>

	<!-- ============================================================================= -->

	<xsl:template name="iso19139Simple2">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		<xsl:param name="flat"/>
		
		<xsl:apply-templates mode="elementEP" select="gmd:fileIdentifier|geonet:child[string(@name)='fileIdentifier']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:language|geonet:child[string(@name)='language']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:characterSet|geonet:child[string(@name)='characterSet']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:parentIdentifier|geonet:child[string(@name)='parentIdentifier']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:hierarchyLevel|geonet:child[string(@name)='hierarchyLevel']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:hierarchyLevelName|geonet:child[string(@name)='hierarchyLevelName']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:dateStamp|geonet:child[string(@name)='dateStamp']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:metadataStandardName|geonet:child[string(@name)='metadataStandardName']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:metadataStandardVersion|geonet:child[string(@name)='metadataStandardVersion']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:metadataConstraints|geonet:child[string(@name)='metadataConstraints']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:metadataMaintenance|geonet:child[string(@name)='metadataMaintenance']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:contact|geonet:child[string(@name)='contact']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:dataSetURI|geonet:child[string(@name)='dataSetURI']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:locale|geonet:child[string(@name)='locale']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:series|geonet:child[string(@name)='series']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:describes|geonet:child[string(@name)='describes']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:propertyType|geonet:child[string(@name)='propertyType']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:featureType|geonet:child[string(@name)='featureType']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
		<xsl:apply-templates mode="elementEP" select="gmd:featureAttribute|geonet:child[string(@name)='featureAttribute']">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
			<xsl:with-param name="flat"   select="$flat"/>
		</xsl:apply-templates>
		
	</xsl:template>

	<!-- ============================================================================= -->

	<xsl:template mode="iso19139" match="gmd:transferOptions">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>

		
		<xsl:if test="$edit=false()">
			<xsl:if test="count(gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:protocol/gco:CharacterString[contains(string(.),'download')])>1 and
									//geonet:info/download='true'">
				<xsl:call-template name="complexElementGui">
					<xsl:with-param name="title" select="/root/gui/strings/downloadSummary"/>
					<xsl:with-param name="content">
						<tr>
							<td  align="center">
								<button class="content" onclick="javascript:runFileDownloadSummary('{//geonet:info/uuid}','{/root/gui/strings/downloadSummary}')" type="button">
									<xsl:value-of select="/root/gui/strings/showFileDownloadSummary"/>	
								</button>
							</td>
						</tr>
					</xsl:with-param>
					<xsl:with-param name="helpLink">
						<xsl:call-template name="getHelpLink">
							<xsl:with-param name="name"   select="name(.)"/>
							<xsl:with-param name="schema" select="$schema"/>
						</xsl:call-template>
					</xsl:with-param>
					<xsl:with-param name="schema" select="$schema"/>
				</xsl:call-template>
			</xsl:if>
		</xsl:if>
		<xsl:apply-templates mode="complexElement" select=".">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="$edit"/>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- ============================================================================= -->

	<xsl:template mode="iso19139" match="gmd:contact|gmd:pointOfContact|gmd:citedResponsibleParty">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>

		<xsl:call-template name="contactTemplate">
			<xsl:with-param name="edit" select="$edit"/>
			<xsl:with-param name="schema" select="$schema"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="contactTemplate">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:variable name="content">
			<xsl:for-each select="gmd:CI_ResponsibleParty">
			<tr>
				<td class="padded-content" width="100%" colspan="2">
					<table width="100%">
						<tr>
							<td width="50%" valign="top">
								<table width="100%">
									<xsl:apply-templates mode="elementEP" select="../@xlink:href">
										<xsl:with-param name="schema" select="$schema"/>
										<xsl:with-param name="edit"   select="$edit"/>
									</xsl:apply-templates>
									
									<xsl:apply-templates mode="elementEP" select="gmd:individualName|geonet:child[string(@name)='individualName']">
										<xsl:with-param name="schema" select="$schema"/>
										<xsl:with-param name="edit"   select="$edit"/>
									</xsl:apply-templates>
									
									<xsl:apply-templates mode="elementEP" select="gmd:organisationName|geonet:child[string(@name)='organisationName']">
										<xsl:with-param name="schema" select="$schema"/>
										<xsl:with-param name="edit"   select="$edit"/>
									</xsl:apply-templates>
									
									<xsl:apply-templates mode="elementEP" select="gmd:positionName|geonet:child[string(@name)='positionName']">
										<xsl:with-param name="schema" select="$schema"/>
										<xsl:with-param name="edit"   select="$edit"/>
									</xsl:apply-templates>
									
									<xsl:apply-templates mode="elementEP" select="gmd:role|geonet:child[string(@name)='role']">
										<xsl:with-param name="schema" select="$schema"/>
										<xsl:with-param name="edit"   select="$edit"/>
									</xsl:apply-templates>
									
								</table>
							</td>
							<td valign="top">
								<table width="100%">
									<xsl:apply-templates mode="elementEP" select="gmd:contactInfo|geonet:child[string(@name)='contactInfo']">
										<xsl:with-param name="schema" select="$schema"/>
										<xsl:with-param name="edit"   select="$edit"/>
									</xsl:apply-templates>
								</table>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			</xsl:for-each>
		</xsl:variable>
		
		<xsl:apply-templates mode="complexElement" select=".">
			<xsl:with-param name="schema"  select="$schema"/>
			<xsl:with-param name="edit"    select="$edit"/>
			<xsl:with-param name="content" select="$content"/>
		</xsl:apply-templates>
		
	</xsl:template>

	<!-- ============================================================================= -->
	<!-- online resources -->
	<!-- ============================================================================= -->

	<xsl:template mode="iso19139" match="gmd:CI_OnlineResource" priority="2">
		<xsl:param name="schema"/>
		<xsl:param name="edit"/>
		
		<xsl:variable name="langId">
			<xsl:call-template name="getLangId">
				<xsl:with-param name="langGui" select="/root/gui/language" />
				<xsl:with-param name="md"
					select="ancestor-or-self::*[name(.)='gmd:MD_Metadata' or @gco:isoType='gmd:MD_Metadata']" />
			</xsl:call-template>
		</xsl:variable>

		<xsl:variable name="linkage" select="gmd:linkage/gmd:URL" />
		<xsl:variable name="name">
			<xsl:for-each select="gmd:name">
				<xsl:call-template name="localised">
					<xsl:with-param name="langId" select="$langId"/>
				</xsl:call-template>
			</xsl:for-each>
		</xsl:variable>
		<xsl:variable name="description">
			<xsl:for-each select="gmd:description">
				<xsl:call-template name="localised">
					<xsl:with-param name="langId" select="$langId"/>
				</xsl:call-template>
			</xsl:for-each>
		</xsl:variable>
		
		<xsl:choose>
			<xsl:when test="$edit=true()">
				<xsl:apply-templates mode="iso19139EditOnlineRes" select=".">
					<xsl:with-param name="schema" select="$schema"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="string($linkage)!=''">
				<xsl:apply-templates mode="simpleElement" select=".">
					<xsl:with-param name="schema"  select="$schema"/>
					<xsl:with-param name="text">
						<a href="{$linkage}" target="_new">
							<xsl:choose>
								<xsl:when test="string($description)!=''">
									<xsl:value-of select="$description"/>
								</xsl:when>
								<xsl:when test="string($name)!=''">
									<xsl:value-of select="$name"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$linkage"/>
								</xsl:otherwise>
							</xsl:choose>
						</a>
					</xsl:with-param>
				</xsl:apply-templates>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<!-- ============================================================================= -->

	<xsl:template mode="iso19139EditOnlineRes" match="*">
		<xsl:param name="schema"/>
	
		<xsl:variable name="id" select="generate-id(.)"/>
		<tr><td colspan="2"><div id="{$id}"/></td></tr>
		<xsl:apply-templates mode="complexElement" select=".">
			<xsl:with-param name="schema" select="$schema"/>
			<xsl:with-param name="edit"   select="true()"/>
			<xsl:with-param name="content">
				
				<xsl:apply-templates mode="elementEP" select="gmd:linkage|geonet:child[string(@name)='linkage']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="true()"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="gmd:protocol|geonet:child[string(@name)='protocol']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="true()"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="gmd:applicationProfile|geonet:child[string(@name)='applicationProfile']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="true()"/>
				</xsl:apply-templates>

				<xsl:choose>
					<xsl:when test="(string(gmd:protocol/gco:CharacterString)='WWW:DOWNLOAD-1.0-http--download' and string(gmd:name/gco:CharacterString|gmd:name/gmx:MimeFileType)!='') or (string(gmd:protocol/gco:CharacterString)='OGC:WMC-1.1.0-http-get-capabilities' and string(gmd:name/gco:CharacterString|gmd:name/gmx:MimeFileType)!='')">
						<xsl:apply-templates mode="iso19139FileRemove" select="gmd:name/gco:CharacterString|gmd:name/gmx:MimeFileType">
							<xsl:with-param name="access" select="'private'"/>
							<xsl:with-param name="id" select="$id"/>
						</xsl:apply-templates>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates mode="elementEP" select="gmd:name|geonet:child[string(@name)='name']">
							<xsl:with-param name="schema" select="$schema"/>
							<xsl:with-param name="edit"   select="true()"/>
						</xsl:apply-templates>
					</xsl:otherwise>
				</xsl:choose>

				<xsl:apply-templates mode="elementEP" select="gmd:description|geonet:child[string(@name)='description']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="true()"/>
				</xsl:apply-templates>
				
				<xsl:apply-templates mode="elementEP" select="gmd:function|geonet:child[string(@name)='function']">
					<xsl:with-param name="schema" select="$schema"/>
					<xsl:with-param name="edit"   select="true()"/>
				</xsl:apply-templates>
			</xsl:with-param>
		</xsl:apply-templates>
	</xsl:template>
	
	<!-- ============================================================================= -->
	<!-- online resources: WMS get map -->
	<!-- ============================================================