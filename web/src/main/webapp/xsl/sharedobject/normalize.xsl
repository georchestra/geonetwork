<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <result>
            <xsl:apply-templates select="." mode="allText"/>
        </result>
    </xsl:template>

    <xsl:template match="*" mode="allText">
    <xsl:for-each select="@*">
        <xsl:if test="name(.) != 'codeList' ">
            <xsl:value-of select="concat(string(.),' ')"/>
        </xsl:if>
    </xsl:for-each>

    <xsl:choose>
        <xsl:when test="*"><xsl:apply-templates select="*" mode="allText"/></xsl:when>
        <xsl:otherwise><xsl:value-of select="concat(string(.),' ')"/></xsl:otherwise>
    </xsl:choose>
</xsl:template>

</xsl:stylesheet>
