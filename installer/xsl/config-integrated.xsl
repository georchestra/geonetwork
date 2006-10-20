<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- === debug mode ======================================= -->
	
	<xsl:template match="general/debug">
		<debug>false</debug>
	</xsl:template>

	<!-- === application handler ======================================= -->
	
	<xsl:template match="appHandler" xml:space="preserve">		
		<appHandler class="org.wfp.vam.intermap.Intermap">
			<param name="mapServers" value="WEB-INF/mapServers.xml" />
			<param name="axlRequests" value="WEB-INF/axl" />
			<param name="wmsTransform" value="WEB-INF/wms" />
			
			<!-- proxy server -->
	
			<param name="useProxy" value="$USE_PROXY" />
			<param name="proxyHost" value="$PROXY_HOST" />
			<param name="proxyPort" value="$PROXY_PORT" />
	

			<!-- screen DPI -->
			<param value="96" name="screenDpi" />
			
			<!-- image size -->
	
			<param name="smallImageWidth" value="400" />
			<param name="smallImageHeight" value="300" />
			<param name="bigImageWidth" value="800" />
			<param name="bigImageHeight" value="600" />
			<param name="defaultImageSize" value="small" />
	
			<!-- temp files -->
			
			<param name="tempDir" value="$INSTALL_PATH/web-intermap/tmp/" />
			<param name="tempDeleteMinutes" value="15" />
			<param name="tempUrl" value="/$SERVLET/intermap/tmp" />
	
			<!-- http cache -->
			
			<param name="httpCacheDir" value="$INSTALL_PATH/web-intermap/httpCache/" />
			<param name="httpCacheDeleteEvery" value="60" />
		</appHandler>
	</xsl:template>
		
	<!-- === element copying =========================================== -->
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*|node()"/>
		</xsl:copy>
	</xsl:template>

</xsl:stylesheet>
