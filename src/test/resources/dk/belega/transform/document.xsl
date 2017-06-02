<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="text" encoding="UTF-8"/>

    <xsl:param name="document-name"/>

    <xsl:template match="/">
        <xsl:apply-templates select="document($document-name)/document/head/title"/>
    </xsl:template>

</xsl:stylesheet>