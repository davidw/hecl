<?xml version="1.0" encoding="ISO-8859-1"?>
  <xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<!--
   Copyright 2004-2005 David N. Welton <davidw@dedasys.com>

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

   	http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->

  <xsl:param name="use.id.as.filename" select="1"/>
  <xsl:param name="header.rule" select="0"/>

  <xsl:param name="chunk.section.depth" select="2" />

  <xsl:param name="navig.graphics" select="1"/>
  <xsl:param name="navig.graphics.extension" select="'.png'"/>
  <xsl:param name="admon.graphics" select="1"/>
  <xsl:param name="generate.section.toc.level" select="1"/>
  <xsl:param name="refentry.separator" select="1"/>

  <xsl:param name="toc.section.depth" select="2"/>

<!--
  <xsl:variable name="arg.choice.opt.open.str">?</xsl:variable>
  <xsl:variable name="arg.choice.opt.close.str">?</xsl:variable>
-->

  <xsl:variable name="arg.choice.req.open.str"></xsl:variable>
  <xsl:variable name="arg.choice.req.close.str"></xsl:variable>
<!--
  <xsl:variable name="group.choice.opt.open.str">(</xsl:variable>
  <xsl:variable name="group.choice.opt.close.str">)</xsl:variable>

  <xsl:variable name="arg.choice.def.open.str"></xsl:variable>
  <xsl:variable name="arg.choice.def.close.str"></xsl:variable>
  <xsl:variable name="group.choice.def.open.str"></xsl:variable>
  <xsl:variable name="group.choice.def.close.str"></xsl:variable>
  <xsl:variable name="group.choice.req.open.str">(</xsl:variable>
  <xsl:variable name="group.choice.req.close.str">)</xsl:variable>
-->

  <xsl:variable name="group.rep.repeat.str">...</xsl:variable>

  <xsl:variable name="arg.or.sep"> | </xsl:variable>

  <xsl:variable name="mycounter" select="1000"/>

  <xsl:template name="object.id">
    <xsl:param name="object" select="."/>
    <xsl:choose>
      <xsl:when test="$object/@id">
	<xsl:value-of select="$object/@id"/>
      </xsl:when>
      <xsl:when test="$object/@xml:id">
	<xsl:value-of select="$object/@xml:id"/>
      </xsl:when>
      <xsl:otherwise>
	id-<xsl:number level="multiple" count="*"/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- This code makes it so that the main index does not contain the entire list of Hecl commands
  from the Hecl commands section. -->
  <xsl:template match="section" mode="toc">
    <xsl:param name="toc-context" select="."/>

    <xsl:call-template name="subtoc">
      <xsl:with-param name="toc-context" select="$toc-context"/>
      <xsl:with-param name="nodes" select="section
                                           |simplesect[$simplesect.in.toc != 0]
                                           |bridgehead[$bridgehead.in.toc != 0]"/>
    </xsl:call-template>
  </xsl:template>


</xsl:stylesheet>
