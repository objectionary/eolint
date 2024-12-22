<?xml version="1.0" encoding="UTF-8"?>
<!--
The MIT License (MIT)

Copyright (c) 2016-2024 Objectionary.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included
in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0" id="object-has-data">
  <xsl:output encoding="UTF-8" method="xml"/>
  <xsl:template match="/">
    <defects>
      <xsl:apply-templates select="//o" mode="with-data"/>
    </defects>
  </xsl:template>
  <xsl:template match="o" mode="with-data">
    <xsl:variable name="data" select="normalize-space(string-join(text(), ''))"/>
    <xsl:if test="$data != '' and not(@base)">
      <defect>
        <xsl:attribute name="line">
          <xsl:value-of select="if (@line) then @line else '0'"/>
        </xsl:attribute>
        <xsl:attribute name="severity">critical</xsl:attribute>
        <xsl:text>Only objects with their @base attributes containing 'org.eolang.bytes' may contain data, while this object doesn't have @base attribute at all</xsl:text>
      </defect>
    </xsl:if>
    <xsl:if test="$data != '' and @base and not(@base = 'bytes' or @base = 'org.eolang.bytes')">
      <defect>
        <xsl:attribute name="line">
          <xsl:value-of select="if (@line) then @line else '0'"/>
        </xsl:attribute>
        <xsl:attribute name="severity">critical</xsl:attribute>
        <xsl:text>Only objects with their @base attributes containing 'org.eolang.bytes' may contain data, while this object contains "</xsl:text>
        <xsl:value-of select="$data"/>
        <xsl:text>"</xsl:text>
      </defect>
    </xsl:if>
  </xsl:template>
</xsl:stylesheet>