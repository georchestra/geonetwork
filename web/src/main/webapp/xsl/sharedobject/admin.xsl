<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:import href="../header.xsl"/>
    <xsl:include href="../banner.xsl"/>

       <!-- parameter 'translate' will indicate which strings are translated strings -->
       <!-- parameter 'debug' will use non-minified javascript
            debug=app (or just debug) add test javascript and app javascript (all non-minified)
            debug=all will load all non-minified JS
       -->
       <xsl:template name="jslibs">
           <script language="JavaScript"  type="text/javascript">
               var strings = {
                   <xsl:apply-templates mode="js-translations" select="/root/gui/strings/*[@js='true' and not(*) and not(@id)]"/>
               };
               var translate = function (key) {
                   var translation = strings[key];
                   <xsl:choose>
                       <xsl:when test="/root/request/translate">
                           if( translation === undefined) return '*'+key+'*';
                           else return '-:) '+translation;
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

           <script type="text/javascript" src="{/root/gui/url}/scripts/sharedobject/Admin.js"></script>
           <script type="text/javascript" src="{/root/gui/url}/scripts/sharedobject/ObjectGrid.js"></script>

       </xsl:template>

       <xsl:template mode="script" match="/"/>
       <xsl:template mode="css" match="/"/>


       <xsl:template name="edit-css">
           <link rel="stylesheet" type="text/css"
                 href="{/root/gui/url}/scripts/app/external/extjs/resources/css/ext-all.css"/>

           <link rel="stylesheet" type="text/css" href="{/root/gui/url}/scripts/app/css/customization.css" />

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
                    <xsl:call-template name="banner"/>
                    </td></tr>
                </table>
            </body>
        </html>
    </xsl:template>
    <xsl:template name="jsHeader"/>

    </xsl:stylesheet>
