<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        <xsl:template match="/">
        <fulltext>
                <xsl:apply-templates select="*" mode="allText" />
        </fulltext>
        </xsl:template>

        <xsl:template match="*" mode="allText">
                <xsl:choose>
                        <xsl:when test="*">
                                <xsl:apply-templates select="*" mode="allText" />
                        </xsl:when>
                        <xsl:otherwise>
                                <xsl:value-of select="concat(string(.),' ')" />
                        </xsl:otherwise>
                </xsl:choose>
        </xsl:template>
</xsl:stylesheet>
