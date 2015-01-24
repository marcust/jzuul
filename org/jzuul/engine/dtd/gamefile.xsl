<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="/">
        <html>
            <head>
                <title>Zuul Save und Mapfile 1.0</title>
            </head>
            <body>
                <xsl:for-each select="gamefile/map">
                    <h2>Karte <xsl:value-of select="@name"/>
                    </h2>
                    <xsl:for-each select="room">
                        <p>
                            <table border="1" cellspacing="0" bordercolor="black" width="50%">
                                <tr>
                                    <td width="25%">Name:</td>
                                    <td>
                                        <xsl:value-of select="@name"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="25%">Beschreibung:</td>
                                    <td> Sie befinden sich <xsl:value-of select="description"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="25%">Gegenstände:</td>
                                    <td>
                                        <xsl:for-each select="contents/item">
                                            <xsl:value-of select="@class"/>
                                            <br/>
                                        </xsl:for-each>
                                    </td>
                                </tr>
                                <tr>
                                    <td width="25%">Personen:</td>
                                    <td>
                                        <xsl:for-each select="contents/character">
                                            <xsl:value-of select="@class"/>
                                            <br/>
                                        </xsl:for-each>
                                    </td>
                                </tr>
                            </table>
                        </p>
                    </xsl:for-each>
                </xsl:for-each>
                <h1>Spieler:</h1>
                <table border="1" cellspacing="0" bordercolor="black" width="50%">
                    <tr>
                        <td width="25%">Position:</td>
                        <td>
                            <xsl:value-of select="gamefile/player/@position"/>
                        </td>
                    </tr>
                    <tr>
                        <td width="25%">Inventar</td>
                        <td>
                            <xsl:for-each select="contents/item">
                                <xsl:value-of select="@class"/>
                                <br/>
                            </xsl:for-each>
                        </td>
                    </tr>
                </table>
                <h1>History:</h1>
                <table border="1" cellspacing="0" bordercolor="black" width="50%">
                    <xsl:for-each select="/gamefile/history/command">
                        <tr>
                            <td width="25%">
                                <xsl:value-of select="."/>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
