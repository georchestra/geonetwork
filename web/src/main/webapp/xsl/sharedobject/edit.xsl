<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:geonet="http://www.fao.org/geonetwork"
	exclude-result-prefixes="geonet">

	<xsl:import href="../ext/edit.xsl"/>
    <xsl:include href="../banner.xsl"/>

    <xsl:template name="configScripts">
        <script type="text/javascript" src="{/root/gui/url}/scripts/app/iso19139/config.js"></script>
        <script type="text/javascript" src="{/root/gui/url}/scripts/app/loc/Languages.js"></script>
        <script language="JavaScript"  type="text/javascript">
/** override the Layouts configuration of the full metadata editor */
ExtGeoNet.config.Layouts = {};
            
ExtGeoNet.config.Layouts.UI = {
    title: 'UI',
    fields: {
        Contact : 'gmd:CI_ResponsibleParty'
    }
};

ExtGeoNet.config.Layouts.Xml = {
    title: 'Xml',
    fields: {
        mapping : 'XML',
        xpath : '/'
    }
};

/*
    TODO Enable when different panels can sync together
    ExtGeoNet.config.Layouts.items = [ExtGeoNet.config.Layouts.UI, ExtGeoNet.config.Layouts.Xml];
*/
ExtGeoNet.config.Layouts.items = [ExtGeoNet.config.Layouts.UI];

ExtGeoNet.config.isMultilingual = false;
ExtGeoNet.config.hideBanner = false;

/* Change the url for fetching the sharedobject */
ExtGeoNet.config.getMetadataURL = function(mdId){
    return Env.locService+'/xml.sharedobject.get?id='+mdId+'&amp;type=<xsl:value-of select="/root/request/type"/>';
}
ExtGeoNet.config.saveMetadataURL = function(mdId){
    return Env.locService+'/sharedobject.upload';
}
ExtGeoNet.config.saveParams = function(mdId){
	return 'id="'+mdId+'" type="<xsl:value-of select="/root/request/type"/>"';
}
        </script>
    </xsl:template>
    
    <xsl:template name="banner-override">
        <xsl:call-template name="banner"/>
    </xsl:template>
    
</xsl:stylesheet>
