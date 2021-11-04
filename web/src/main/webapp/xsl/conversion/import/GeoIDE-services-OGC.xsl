<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:gmd="http://www.isotc211.org/2005/gmd"
                xmlns:gco="http://www.isotc211.org/2005/gco"
                version="2.0">

    <!-- par défaut les éléments sont copiés tels quels -->
    <xsl:template match="@*|node()">
        <xsl:copy copy-namespaces="no">
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <!-- traitement spécial pour les éléments gmd:online -->
    <xsl:template match="gmd:onLine">
        <xsl:variable name="url" select="gmd:CI_OnlineResource/gmd:linkage/gmd:URL"/>
        <xsl:variable name="layerName" select="/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:alternateTitle"/>

        <!-- il s'agit d'une url de base de services WxS : créer deux liens explicites pour WMS et WFS -->
        <xsl:if test="contains($url, 'ogc.geo-ide.developpement-durable.gouv.fr/wxs')">
            <gmd:onLine>
                <gmd:CI_OnlineResource>
                    <gmd:linkage>
                        <gmd:URL><xsl:value-of select="$url"/></gmd:URL>
                    </gmd:linkage>
                    <gmd:protocol>
                        <gco:CharacterString>OGC:WMS</gco:CharacterString>
                    </gmd:protocol>
                    <gmd:name>
                        <gco:CharacterString><xsl:value-of select="$layerName"/></gco:CharacterString>
                    </gmd:name>
                    <gmd:description>
                        <gco:CharacterString>Service de visualisation</gco:CharacterString>
                    </gmd:description>
                </gmd:CI_OnlineResource>
            </gmd:onLine>
            <gmd:onLine>
                <gmd:CI_OnlineResource>
                    <gmd:linkage>
                        <gmd:URL><xsl:value-of select="$url"/></gmd:URL>
                    </gmd:linkage>
                    <gmd:protocol>
                        <gco:CharacterString>OGC:WFS</gco:CharacterString>
                    </gmd:protocol>
                    <gmd:name>
                        <gco:CharacterString><xsl:value-of select="$layerName"/></gco:CharacterString>
                    </gmd:name>
                    <gmd:description>
                        <gco:CharacterString>Service de téléchargement</gco:CharacterString>
                    </gmd:description>
                </gmd:CI_OnlineResource>
            </gmd:onLine>
        </xsl:if>
        <gmd:onLine>
            <xsl:apply-templates select="@*|node()"/>
        </gmd:onLine>
    </xsl:template>
</xsl:stylesheet>
