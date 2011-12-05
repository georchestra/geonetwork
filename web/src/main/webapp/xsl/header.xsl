<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:include href="geo/utils.xsl"/>
	
	<!--
	main html header
	-->
	<xsl:template name="header">
		
		<!-- title -->
		<title><xsl:value-of select="/root/gui/strings/title"/></title>
		<link href="{/root/gui/url}/favicon.ico" rel="shortcut icon" type="image/x-icon" />
		<link href="{/root/gui/url}/favicon.ico" rel="icon" type="image/x-icon" />

		<!-- Recent updates newsfeed -->
		<link href="{/root/gui/locService}/rss.latest?georss=gml" rel="alternate" type="application/rss+xml" title="GeoNetwork opensource GeoRSS | {/root/gui/strings/recentAdditions}" />
		<link href="{/root/gui/locService}/portal.opensearch" rel="search" type="application/opensearchdescription+xml">
		<xsl:attribute name="title"><xsl:value-of select="//site/name"/> (GeoNetwork)</xsl:attribute>
		</link>

		<!-- meta tags -->
		<xsl:copy-of select="/root/gui/strings/header_meta/meta"/>
		<META HTTP-EQUIV="Pragma"  CONTENT="no-cache"/>
		<META HTTP-EQUIV="Expires" CONTENT="-1"/>
		
		<script language="JavaScript" type="text/javascript">
			var Env = new Object();

			Env.host = "http://<xsl:value-of select="/root/gui/env/server/host"/>:<xsl:value-of select="/root/gui/env/server/port"/>";
			Env.locService= "<xsl:value-of select="/root/gui/locService"/>";
			Env.locUrl    = "<xsl:value-of select="/root/gui/locUrl"/>";
			Env.url       = "<xsl:value-of select="/root/gui/url"/>";
			Env.lang      = "<xsl:value-of select="/root/gui/language"/>";
            Env.proxy     = "<xsl:value-of select="/root/gui/config/proxy-url"/>";
			
			window.javascriptsLocation = "<xsl:value-of select="/root/gui/url"/>/scripts/";
			

			
			<!-- Adding the security headers stuff -->
			<!--  note: ExtJS is not loaded yet, using classic namespace instead -->
			if (typeof Geonetwork === 'undefined') { 
				Geonetwork = {};
			}
			if (typeof Geonetwork.user === 'undefined') {
				Geonetwork.user = {};
			}
			if (typeof Geonetwork.dlform === 'undefined') {
				Geonetwork.dlform = {};
			}
			Geonetwork.user.username = "<xsl:value-of select="/root/gui/env/security-proxy/sec-username"/>";
			Geonetwork.user.first_name = "<xsl:value-of select="/root/gui/env/security-proxy/sec-firstname"/>";
			Geonetwork.user.last_name = "<xsl:value-of select="/root/gui/env/security-proxy/sec-lastname"/>";
			Geonetwork.user.company = "<xsl:value-of select="/root/gui/env/security-proxy/sec-org"/>";
			Geonetwork.user.email = "<xsl:value-of select="/root/gui/env/security-proxy/sec-email"/>";
			Geonetwork.user.tel = "<xsl:value-of select="/root/gui/env/security-proxy/sec-tel"/>";
			Geonetwork.dlform.activated = "<xsl:value-of select="/root/gui/env/downloadform/activated"/>";			
			Geonetwork.dlform.pdf_url = "<xsl:value-of select="/root/gui/env/downloadform/pdf_url"/>";
			
			
			
			<xsl:if test="//service/@name = 'main.home'">
            document.onkeyup = alertkey;
            
            function alertkey(e) {
             if (!e) {
                 if (window.event) {
                     e = window.event;
                 } else {
                     return;
                 }
             }
             
             if (e.keyCode == 13) {
                  <xsl:if test="string(/root/gui/login/userId)=''">
                  if (($('username') != null) &amp;&amp; ($('username').value != '')) { // login action
                    goSubmit('login')
                    return;
                  }
                  </xsl:if>
                  if (document.cookie.indexOf("search=advanced")!=-1)
                    runAdvancedSearch();
                  else
                    runSimpleSearch();
             }
            };
			</xsl:if>
		</script>		
		
		<!-- stylesheet -->
		<link rel="stylesheet" type="text/css" href="{/root/gui/url}/geonetwork.css"/>
		<link rel="stylesheet" type="text/css" href="{/root/gui/url}/modalbox.css"/>
		<xsl:apply-templates mode="css" select="/"/>
		<!-- ExtJS gray theme -->
		<link rel="stylesheet" type="text/css" href="{/root/gui/url}/scripts/ext/resources/css/xtheme-gray.css" />
		
		<!-- JS -->
		<xsl:call-template name="jsHeader"/>

	</xsl:template>
	
	<!--
		All element from localisation files having an attribute named js
		(eg. <key js="true">value</key>) is added to a global JS table. 
		The content of the value could be accessed in JS using the translate 
		function (ie. translate('key');).
	-->
	<xsl:template match="*" mode="js-translations">
		"<xsl:value-of select="name(.)"/>":"<xsl:value-of select="normalize-space(translate(.,'&quot;', '`'))"/>"
		<xsl:if test="position()!=last()">,</xsl:if>
	</xsl:template>
</xsl:stylesheet>
