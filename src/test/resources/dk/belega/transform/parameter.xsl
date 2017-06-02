<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="text" encoding="UTF-8"/>

    <xsl:param name="some-parameter"/>
    <xsl:param name="other-parameter"/>

    <xsl:template match="/">
        <xsl:value-of select="$some-parameter"/>
        <xsl:value-of select="$other-parameter"/>
    </xsl:template>

</xsl:stylesheet>