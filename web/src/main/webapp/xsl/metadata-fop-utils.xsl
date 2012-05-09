<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xalan="http://xml.apache.org/xalan"
	exclude-result-prefixes="xalan"
	xmlns:gco="http://www.isotc211.org/2005/gco"
	xmlns:gmd="http://www.isotc211.org/2005/gmd"
	xmlns:geonet="http://www.fao.org/geonetwork"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">


	<!--
		Display Thumbnails
	-->
	<xsl:template name="thumbnail">
		<xsl:param name="server"/>
		
        <xsl:variable name="info" select="geonet:info"/>
        <xsl:variable name="id" select="geonet:info/id"/>
        <xsl:variable name="size">4</xsl:variable>
        
		<xsl:variable name="graphicOverviews"
			select="./gmd:identificationInfo/*/gmd:graphicOverview/gmd:MD_BrowseGraphic" />				
						
        <!-- TODO : handle different standard  -->
		<xsl:for-each select="$graphicOverviews">

			<xsl:variable name="bgFileName" select="./gmd:fileName/gco:CharacterString" />
			<xsl:variable name="bgFileDesc"
				select="./gmd:fileDescription/gco:CharacterString" />
							
            <xsl:if test="$bgFileName != ''">
				<xsl:choose>

					<!-- the thumbnail is an url -->

					<xsl:when test="contains($bgFileName ,'://')">
						<fo:external-graphic 
							content-width="{$size}cm">
							<xsl:attribute name="src">
                                <xsl:text>url('</xsl:text><xsl:value-of select="$bgFileName" /><xsl:text>')"</xsl:text>
                            </xsl:attribute>
						</fo:external-graphic>
					</xsl:when>

					<!-- small thumbnail -->

					<xsl:when test="string($bgFileDesc)='thumbnail'">
						<xsl:choose>
							<xsl:when test="$info/isHarvested = 'y'">
								<xsl:if
									test="$info/harvestInfo/smallThumbnail">
									<fo:external-graphic
										content-width="{$size}cm">
										<xsl:attribute name="src"><xsl:text>url('</xsl:text><xsl:value-of
												select="concat($server, $info/harvestInfo/smallThumbnail, $bgFileName)" /><xsl:text>')"</xsl:text></xsl:attribute>
									</fo:external-graphic>
								</xsl:if>
							</xsl:when>
							<xsl:otherwise>
								<fo:external-graphic
									content-width="{$size}cm">
									<xsl:attribute name="src"><xsl:text>url('</xsl:text><xsl:value-of
											select="concat($server, /root/gui/locService,'/resources.get?id=',$id,'&amp;fname=', $bgFileName,'&amp;access=public')" /><xsl:text>')"</xsl:text></xsl:attribute>
								</fo:external-graphic>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					
					
				</xsl:choose>
			</xsl:if>
		</xsl:for-each>
		
	</xsl:template>
	
	
	<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
	<!-- callbacks from schema templates -->
	<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

	<xsl:template mode="element" match="*|@*">
		<xsl:param name="schema" />
		<xsl:param name="geonetUri"/>
        <xsl:choose>

			<!-- has children or attributes, existing or potential -->
			<xsl:when test="*[namespace-uri(.)!=$geonetUri]|*/@*|geonet:child|geonet:element/geonet:attribute">
			     <!-- if it does not have children show it as a simple element -->
                        <xsl:if test="not(*[namespace-uri(.)!=$geonetUri]|geonet:child|geonet:element/geonet:attribute)">
                            <xsl:apply-templates mode="simpleElement" select=".">
                                <xsl:with-param name="schema" select="$schema"/>
                            </xsl:apply-templates>
                        </xsl:if>
                        <!-- existing attributes -->
                        <xsl:apply-templates mode="simpleElement" select="*/@*">
                            <xsl:with-param name="schema" select="$schema"/>
                        </xsl:apply-templates>
                       
                        <!-- existing and new children -->
                        <xsl:apply-templates mode="elementEP" select="*[namespace-uri(.)!=$geonetUri]|geonet:child">
                            <xsl:with-param name="schema" select="$schema"/>
                        </xsl:apply-templates>
            </xsl:when>
            
			<!-- neither children nor attributes, just text -->
			<xsl:otherwise>
			    <xsl:apply-templates mode="simpleElement" select=".">
					<xsl:with-param name="schema" select="$schema" />
				</xsl:apply-templates>
			</xsl:otherwise>

		</xsl:choose>
	</xsl:template>

	<xsl:template mode="simpleElement" match="*">
		<xsl:param name="schema" />

		<xsl:param name="title">
			<xsl:call-template name="getTitle">
				<xsl:with-param name="name" select="name(.)" />
				<xsl:with-param name="parent" select="name(..)" />
				<xsl:with-param name="schema" select="$schema" />				
			</xsl:call-template>
		</xsl:param>
		<xsl:param name="text">
			<xsl:call-template name="getElementText">
				<xsl:with-param name="schema" select="$schema" />
			</xsl:call-template>
		</xsl:param>
		<xsl:param name="helpLink">
			<xsl:call-template name="getHelpLink">
				<xsl:with-param name="name" select="name(.)" />
				<xsl:with-param name="schema" select="$schema" />
			</xsl:call-template>
		</xsl:param>
		<xsl:call-template name="showSimpleElement">
			<xsl:with-param name="schema" select="$schema" />
			<xsl:with-param name="title" select="$title" />
			<xsl:with-param name="text" select="$text" />
			<xsl:with-param name="helpLink" select="$helpLink" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template mode="simpleElement" match="*/@*">
		<xsl:param name="schema" />
		<xsl:param name="title">
			<xsl:call-template name="getTitle">
				<xsl:with-param name="name" select="name(..)" />
				<xsl:with-param name="schema" select="$schema" />
			</xsl:call-template>
		</xsl:param>
		<xsl:param name="text">
			<xsl:call-template name="getAttributeText">
				<xsl:with-param name="schema" select="$schema" />
			</xsl:call-template>
		</xsl:param>
		<xsl:param name="helpLink">
			<xsl:call-template name="getHelpLink">
				<xsl:with-param name="name" select="name(..)" />
				<xsl:with-param name="schema" select="$schema" />
			</xsl:call-template>
		</xsl:param>
		
		<xsl:call-template name="showSimpleElement">
			<xsl:with-param name="schema" select="$schema" />
			<xsl:with-param name="title" select="$title" />
			<xsl:with-param name="text" select="$text" />
			<xsl:with-param name="helpLink" select="$helpLink" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template mode="complexElement" match="*">
		<xsl:param name="schema" />
		<xsl:param name="title">
			<xsl:call-template name="getTitle">
				<xsl:with-param name="name" select="name(.)" />
				<xsl:with-param name="schema" select="$schema" />
			</xsl:call-template>
		</xsl:param>
		<xsl:param name="content">
			<xsl:call-template name="getContent">
				<xsl:with-param name="schema" select="$schema" />
			</xsl:call-template>
		</xsl:param>
		<xsl:param name="helpLink">
			<xsl:call-template name="getHelpLink">
				<xsl:with-param name="name" select="name(.)" />
				<xsl:with-param name="schema" select="$schema" />
			</xsl:call-template>
		</xsl:param>
		

		<xsl:call-template name="showComplexElement">
			<xsl:with-param name="schema" select="$schema" />
			<xsl:with-param name="title" select="$title" />
			<xsl:with-param name="content" select="$content" />
			<xsl:with-param name="helpLink" select="$helpLink" />
		</xsl:call-template>

	</xsl:template>


	<!--
		prevent drawing of geonet:* elements
		-->
		<xsl:template mode="element" match="geonet:element|geonet:info|geonet:attribute|geonet:schematronerrors"/>
		<xsl:template mode="simpleElement" match="geonet:element|geonet:info|geonet:attribute|geonet:schematronerrors"/>
		<xsl:template mode="complexElement" match="geonet:element|geonet:info|geonet:attribute|geonet:schematronerrors"/>
	
	<!--
		prevent drawing of attributes starting with "_", used in old GeoNetwork versions
	-->
	<xsl:template mode="simpleElement"
		match="@*[starts-with(name(.),'_')]" />



	<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
	<!-- elements/attributes templates -->
	<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

	<!--
		shows a simple element
	-->
	<xsl:template name="showSimpleElement">
		<xsl:param name="schema" />
		<xsl:param name="title" />
		<xsl:param name="text" />
		<xsl:param name="helpLink" />
		<!-- PMT GeoBretagne : don't print out when datas are not available -->
		<xsl:if test="$title != ''">
		 <xsl:if test="$text != ''">
		<xsl:call-template name="simpleElementFop">
			<xsl:with-param name="title" select="$title" />
			<xsl:with-param name="text" select="$text" />
			<xsl:with-param name="helpLink" select="$helpLink" />
		</xsl:call-template>
		</xsl:if>
		</xsl:if>
	</xsl:template>

	<!--
		shows a complex element
	-->
	<xsl:template name="showComplexElement">
		<xsl:param name="schema" />
		<xsl:param name="title" />
		<xsl:param name="content" />
		<xsl:param name="helpLink" />
		<xsl:call-template name="complexElementFop">
			<xsl:with-param name="title" select="$title" />
			<xsl:with-param name="text" select="text()" />
			<xsl:with-param name="content" select="$content" />
			<xsl:with-param name="helpLink" select="$helpLink" />
			<xsl:with-param name="schema" select="$schema" />
		</xsl:call-template>
	</xsl:template>



	<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->
	<!-- utility templates -->
	<!-- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

	<!--
		returns the title of an element
	-->
	<xsl:template name="getTitle">
		<xsl:param name="name" />
		<xsl:param name="schema" />
		<xsl:param name="parent" />
		
		<!-- if parent is null, we are not using it for sure -->
		<xsl:choose>
			<xsl:when test="$parent = ''">
				<xsl:value-of 
					select="/root/gui/*[name(.)=$schema][1]/element[@name=$name][1]/label[1] |
					 		/root/gui/iso19139/element[@name=$name][1]/label[1] |
                            /root/gui/iso19139.fra/element[@name=$name][1]/label[1]" />
			</xsl:when>
			
			<!--  parent not null, and current node not a gco:*** garbage
					We select then the parent translated value
			 -->
			<xsl:when test="$parent != ''">
				<xsl:value-of
					select="/root/gui/*[name(.)=$schema][1]/element[@name=$parent][1]/label[1] |
					        /root/gui/iso19139/element[@name=$parent][1]/label[1] |
                            /root/gui/iso19139.fra/element[@name=$parent][1]/label[1]" />
			</xsl:when>
			<!-- Fallback on the node title -->
			<xsl:otherwise>
				<xsl:value-of select="$name" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		returns the text of an element
	-->
	<xsl:template name="getElementText">
		<xsl:param name="schema" />
		<xsl:param name="rows" select="1" />
		<xsl:param name="cols" select="50" />

		<xsl:variable name="name" select="name(.)" />
		<xsl:variable name="value" select="string(.)" />
		<xsl:variable name="parent" select="name(..)" />
		
		<xsl:choose>
			<!-- list of values -->
			<xsl:when test="geonet:element/geonet:text">
				<select class="md" name="_{geonet:element/@ref}"
					size="1">
					<option name="" />
					<xsl:for-each select="geonet:element/geonet:text">
						<option>
							<xsl:if test="@value=$value">
								<xsl:attribute name="selected" />
							</xsl:if>
							<xsl:variable name="choiceValue"
								select="string(@value)" />
							<xsl:attribute name="value"><xsl:value-of
									select="$choiceValue" />
							</xsl:attribute>

							<!-- it seems that this code is run only under FGDC -->
							<xsl:variable name="label"
								select="/root/gui/*[name(.)=$schema]/codelist[@name = $name]/entry[code = $choiceValue]/label" />
							<xsl:choose>
								<xsl:when test="$label">
									<xsl:value-of select="$label" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$choiceValue" />
								</xsl:otherwise>
							</xsl:choose>
						</option>
					</xsl:for-each>
				</select>
			</xsl:when>
			<xsl:when test="$rows!=1">
				<xsl:call-template name="preformatted">
					<xsl:with-param name="text" select="$value" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains($parent,'gmd:language')">
				<xsl:call-template name="getIsoLanguage">
					<xsl:with-param name="value" select="$value"/>
				</xsl:call-template>		
			</xsl:when>
			<xsl:otherwise>
				<!-- not editable text/codelists -->
				<xsl:variable name="label"
					select="/root/gui/*[name(.)=$schema]/codelist[@name = $name]/entry[code=$value]/label" />
				<xsl:choose>
					<xsl:when test="$label">
						<xsl:value-of select="$label" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$value" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		returns the text of an attribute
	-->
	<xsl:template name="getAttributeText">
		<xsl:param name="schema" />
		<xsl:param name="rows" select="1" />
		<xsl:param name="cols" select="50" />

		<xsl:variable name="name" select="name(.)" />
		<xsl:variable name="value" select="string(.)" />
		<xsl:variable name="parent" select="name(..)" />
		
		<!-- codelist in view mode -->
		<xsl:variable name="label"
			select="/root/gui/*[name(.)=$schema]/codelist[@name = $parent]/entry[code = $value]/label" />
		<xsl:choose>
			<xsl:when test="$label">
				<xsl:value-of select="$label" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
	<!-- ============================================================================= -->
	
	<xsl:template name="getIsoLanguage">
		<xsl:param name="value"/>
		<xsl:variable name="lang"  select="/root/gui/language"/>
		<xsl:value-of select="/root/gui/isoLang/record[code=$value]/label/child::*[name() = $lang]"/>
	</xsl:template>
	
	<!-- ============================================================================= -->

	<!--
		returns the content of a complex element
	-->
	<xsl:template name="getContent">
		<xsl:param name="schema" />
		<xsl:apply-templates mode="elementEP" select="@*">
			<xsl:with-param name="schema" select="$schema" />
			<xsl:with-param name="edit" select="false()" />
		</xsl:apply-templates>
		<xsl:apply-templates mode="elementEP" select="*">
			<xsl:with-param name="schema" select="$schema" />
			<xsl:with-param name="edit" select="false()" />
		</xsl:apply-templates>
	</xsl:template>

	<!-- ================================================================================ -->
	<!-- returns the help url -->
	<!-- ================================================================================ -->

	<xsl:template name="getHelpLink">
		<xsl:param name="name" />
		<xsl:param name="schema" />

		<xsl:choose>
			<xsl:when test="contains($name,'_ELEMENT')">
				<xsl:value-of select="''" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat($schema,'|', $name)" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- ================================================================================ -->

	<xsl:template name="getButtons">
		<xsl:param name="addLink" />
		<xsl:param name="removeLink" />
		<xsl:param name="upLink" />
		<xsl:param name="downLink" />
		<xsl:param name="schematronLink" />

		<!-- add button -->
		<xsl:if test="normalize-space($addLink)">
			<xsl:text> </xsl:text>
			<a href="{$addLink}">
				<img src="{/root/gui/url}/images/plus.gif"
					alt="{/root/gui/strings/add}" />
			</a>
		</xsl:if>
		<!-- remove button -->
		<xsl:if test="normalize-space($removeLink)">
			<xsl:text> </xsl:text>
			<a href="{$removeLink}">
				<img src="{/root/gui/url}/images/del.gif"
					alt="{/root/gui/strings/del}" />
			</a>
		</xsl:if>
		<!-- up button -->
		<xsl:if test="normalize-space($upLink)">
			<xsl:text> </xsl:text>
			<a href="{$upLink}">
				<img src="{/root/gui/url}/images/up.gif"
					alt="{/root/gui/strings/up}" />
			</a>
		</xsl:if>
		<!-- down button -->
		<xsl:if test="normalize-space($downLink)">
			<xsl:text> </xsl:text>
			<a href="{$downLink}">
				<img src="{/root/gui/url}/images/down.gif"
					alt="{/root/gui/strings/down}" />
			</a>
		</xsl:if>
		<!-- schematron button -->
		<xsl:if test="normalize-space($schematronLink)">
			<xsl:text> </xsl:text>
			<a
				href="javascript:alert('schematron message: {$schematronLink}');">
				<img src="{/root/gui/url}/images/schematron.gif" />
			</a>
		</xsl:if>
	</xsl:template>
	
	<!--
		translates CR-LF sequences into HTML newlines <p/>
	-->
	<xsl:template name="preformatted">
		<xsl:param name="text" />

		<xsl:choose>
			<xsl:when test="contains($text,'&#13;&#10;')">
				<xsl:value-of
					select="substring-before($text,'&#13;&#10;')" />
				<br />
				<xsl:call-template name="preformatted">
					<xsl:with-param name="text"
						select="substring-after($text,'&#13;&#10;')" />
					</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$text" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>