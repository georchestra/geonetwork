<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../header.xsl"/>

    <!-- parameter 'translate' will indicate which strings are translated strings -->
    <!-- parameter 'debug' will use non-minified javascript 
         debug=app (or just debug) add test javascript and app javascript (all non-minified)
         debug=all will load all non-minified JS
         debug=test will run tests before loading page
    -->
    <xsl:template name="jslibs">
        <script language="JavaScript"  type="text/javascript">
            var strings = {
                <xsl:apply-templates mode="js-translations" select="/root/gui/strings/*"/>
            };
            var translate = function (key) {
                var translation = strings[key];
            <xsl:choose>
                <xsl:when test="/root/request/translate">
                    if( translation === undefined) return '*'+key+'*';
                    else return ':) '+translation;
                </xsl:when>
                <xsl:otherwise>
                    if(translation === undefined) return '-'+key+'-';
                    else return translation;
                </xsl:otherwise>
            </xsl:choose>
            }
        </script>
        
        <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/modernizr-1.1.min.js"></script>
        <xsl:choose>
            <xsl:when test="/root/request/debug = 'all'">
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/extjs/adapter/ext/ext-base-debug-w-comments.js"></script>
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/extjs/ext-all-debug-w-comments.js"></script>
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/OpenLayers/lib/OpenLayers.js" />
            </xsl:when>
            <xsl:when test="/root/request/debug = 'ext'">
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/extjs/adapter/ext/ext-base-debug-w-comments.js"></script>
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/extjs/ext-all-debug-w-comments.js"></script>
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/OpenLayers/OpenLayers.js" />
            </xsl:when>
            <xsl:when test="/root/request/debug = 'ol'">
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/extjs/adapter/ext/ext-base.js"></script>
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/extjs/ext-all.js"></script>
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/OpenLayers/lib/OpenLayers.js" />
            </xsl:when>
            <xsl:otherwise>
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/extjs/adapter/ext/ext-base.js"></script>
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/extjs/ext-all.js"></script>
                <script type="text/javascript" src="{/root/gui/url}/scripts/app/external/OpenLayers/OpenLayers.js" />
            </xsl:otherwise>
        </xsl:choose>
        <!-- TODO when minified this should be in the preceding choose block -->
        <script type="text/javascript" src="{/root/gui/url}/scripts/app/jsimport.js"></script>

        <xsl:if test="/root/request/debug or /root/request/test">
            <script type="text/javascript" src="{/root/gui/url}/scripts/app/tests/TestBootstrap.js" />                
        </xsl:if>

        <script type="text/javascript" src="{/root/gui/url}/scripts/app/ExtGeoNet.js"></script>

        <!--  ExtGeoNet must be imported before config.js because some widgets defined in config.js depend on the definition 
              of support widgets in ExtGeoNet -->
        
        <xsl:call-template name="configScripts"/>
              
        <xsl:choose>
            <xsl:when test="/root/request/test">
                <script language="JavaScript"  type="text/javascript">
                    Ext.onReady(function() {ExtGeoNet.test.runTests();});
                </script>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="onReady"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="configScripts">
        <script type="text/javascript" src="{/root/gui/url}/scripts/app/iso19139/config.js"></script>
        <script type="text/javascript" src="{/root/gui/url}/scripts/app/loc/Languages.js"></script>
    </xsl:template>
    <xsl:template name="onReady">
        <script language="JavaScript"  type="text/javascript">
            Ext.onReady(function() {ExtGeoNet.Main.onlineInit(<xsl:value-of select="/root/request/id"/>)});
        </script>
    </xsl:template>    

    <xsl:template mode="script" match="/"/>
    <xsl:template mode="css" match="/"/>
    
    
    <xsl:template name="edit-css">
        <link rel="stylesheet" type="text/css"
              href="{/root/gui/url}/scripts/app/external/extjs/resources/css/ext-all.css"/>

        <link rel="stylesheet" type="text/css" href="{/root/gui/url}/scripts/app/external/extjs/ux/css/GroupTab.css" /> 
        <link rel="stylesheet" type="text/css" href="{/root/gui/url}/scripts/app/css/customization.css" /> 

        <link rel="stylesheet" type="text/css" href="{/root/gui/url}/scripts/ext-ux/MultiselectItemSelector-3.0/Multiselect.css" /> 
        
    </xsl:template>
    
    <xsl:template name="banner-override">
        <!-- override this to have a banner -->
    </xsl:template>
    
    <xsl:template match="/">
        <html>
            <head>
                <xsl:call-template name="edit-css"/>
                <xsl:call-template name="header"/>
                <xsl:call-template name="jslibs"/>
            </head>
            <body>
                <table id="north" width="100%" height="100%">
                    <!-- banner -->
                    <tr><td>
                    <xsl:call-template name="banner-override"/>
                    </td></tr>
                </table>
            </body>
        </html>
    </xsl:template>
    <xsl:template name="jsHeader"/>
    
</xsl:stylesheet>