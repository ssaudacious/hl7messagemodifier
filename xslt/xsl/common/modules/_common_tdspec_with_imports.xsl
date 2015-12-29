<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xi="http://www.w3.org/2001/XInclude" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:util="http://hl7.nist.gov/data-specs/util" xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="xs" version="2.0">
	<!-- param: output   values: json | jquery-tab-html | ng-tab-html    default: plain-html -->
	<!--xsl:param name="output" select="'json'" /-->
	<!--xsl:param name="output" select="'jquery-tab-html'" -->
	<!--xsl:param name="output" select="'plain-html'"/ -->
	<xsl:param name="output" select="'ng-tab-html'"/>
	<xsl:variable name="version" select="'2.9.8'"/>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- Release notes author:sriniadhi.work@gmail.com

	2.1:  Tabset support
	2.2:  Adding support for SS and major refactoring into imports and includes
	2.8:  Bugfixes for SS
	2.9:  Adding LRI support (.1 = fixed OBR.28[1], .2 bugfixes for SS, .3 LRI repeating elements .4: bug fix regarding repeating race/ethnicgroup/address) .5 added qpd.3.1 patient identifier, also fixing QPD.address info .6 more birth order stuff z22 as well
.7 fixed a bug in plain-html
.8 - Multiple birth indicator, Immunization Registry status


	-->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- character map is used for being able to output these special html entities directly after escaping -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:character-map name="tags">
		<xsl:output-character character="&lt;" string="&lt;"/>
		<xsl:output-character character="&gt;" string="&gt;"/>
	</xsl:character-map>
	<xsl:output method="html" use-character-maps="tags"/>
	<xsl:variable name="generate-plain-html" select="$output = 'plain-html' or $output = 'ng-tab-html'"/>
	<!--  Use this section for supportd profiles -->
	<xsl:variable name="VXU" select="'VXU'"/>
	<xsl:variable name="QBP" select="'QBP'"/>
	<xsl:variable name="ACK" select="'ACK'"/>
	<xsl:variable name="RSP" select="'RSP'"/>
	<xsl:variable name="SS" select="'SS'"/>
	<xsl:variable name="LRI" select="'LRI'"/>
	<!--  This is the example format for the JSON output -->
	<!-- Example format: { "tables": [ { "title": "Patient Information", "elements": 
		[ { "element" : "Patient Name", "data" : "Madelynn Ainsley" }, { "element" 
		: "Mother's Maiden Name", "data" : "Morgan" }, { "element" : "ID Number", 
		"data" : "A26376273 D26376273 " }, { "element" : "Date/Time of Birth", "data" 
		: "07/02/2015" }, ... ] }, ] }
	 -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!--  ROOT TEMPLATE. Call corresponding sub templates based on the output desired (parametrized) -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template match="*">
		<xsl:choose>
			<xsl:when test="$output = 'json'">
				<xsl:call-template name="main"/>
			</xsl:when>
			<xsl:when test="$output = 'plain-html'">
				<xsl:call-template name="plain-html"/>
			</xsl:when>
			<xsl:when test="$output = 'ng-tab-html'">
				<xsl:call-template name="ng-tab-html"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="main-html"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- This generates the structured DATA (json if output is 'json' and html if it is 'plain-html'. Note that the main-html/jquery-tab-html call this in return -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template name="main">
		<!-- Add profile information if it is json -->
		<xsl:value-of select="util:start(name(.), 'test-data-specs-main')"/>
		<!-- - - - programatically determine if it is a VXU or a QBP - -->
		<xsl:if test="$output = 'ng-tab-html'">
			<xsl:variable name="full">
				<xsl:call-template name="_main"/>
			</xsl:variable>
			<xsl:value-of select="util:begin-tab('FULL', 'All Segments', '', false())"/>
			<xsl:value-of select="util:strip-tabsets($full)"/>
			<xsl:value-of select="util:end-tab($ind1, false())"/>
		</xsl:if>
		<xsl:call-template name="_main"/>
		<xsl:value-of select="util:end($ind1)"/>
	</xsl:template>
	<xsl:template name="_main">
		<xsl:variable name="message-type">
			<xsl:choose>
				<xsl:when test="starts-with(name(.), 'ADT_A0')">
					<xsl:value-of select="$SS"/>
				</xsl:when>
				<xsl:when test="starts-with(name(.), 'ACK')">
					<xsl:value-of select="$ACK"/>
				</xsl:when>
				<xsl:when test="starts-with(name(.), 'QBP')">
					<xsl:value-of select="$QBP"/>
				</xsl:when>
				<xsl:when test="starts-with(name(.), 'RSP')">
					<xsl:value-of select="$ACK"/>
				</xsl:when>
				<xsl:when test="starts-with(name(.), 'VXU')">
					<xsl:value-of select="$VXU"/>
				</xsl:when>
				<xsl:when test="starts-with(name(.), 'ORU')">
					<xsl:value-of select="$LRI"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - - -  QBP - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<xsl:if test="$message-type = $QBP">
			<xsl:call-template name="display-repeating-segment-in-accordion">
				<xsl:with-param name="segments" select="//QPD"/>
			</xsl:call-template>
		</xsl:if>
		<!-- - - - - - Acknolwedgement profiles: showing a template saying that it is supplied by the system- - - - - - - - - - - -->
		<xsl:if test="$message-type = $ACK or $message-type = $RSP">
			<xsl:value-of select="util:title('title', 'Patient Information', 'Patient Information', $ind1, false(), false(), false())"/>
			<xsl:value-of select="util:elements($ind1)"/>
			<xsl:value-of select="util:single-element('This information will be automatically supplied by the System', $ind1)"/>
			<xsl:value-of select="util:end-elements($ind1, false(), false())"/>
		</xsl:if>
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - VXU segments - - - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<xsl:if test="$message-type = $VXU">
			<!-- - - - - - Patient information  - - - - - - - - - - - -->
			<xsl:call-template name="display-repeating-segment-in-accordion">
				<xsl:with-param name="segments" select="//PID"/>
			</xsl:call-template>
			<!-- - - - - - Immunization Registry information - - - - - - - - - - - -->
			<xsl:call-template name="display-repeating-segment-in-accordion">
				<xsl:with-param name="segments" select="//PD1"/>
			</xsl:call-template>
			<xsl:call-template name="display-repeating-segment-in-accordion">
				<xsl:with-param name="segments" select="//NK1"/>
			</xsl:call-template>
			<!-- - - - - - Vaccine Administration Information - - - - - - - - -->
			<xsl:call-template name="display-repeating-segment-in-accordion">
				<xsl:with-param name="segments" select="//RXA"/>
			</xsl:call-template>
		</xsl:if>
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - Syndoromic segments - - - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<xsl:if test="$message-type = $SS">
			<!-- - - - - - Immunization Registry information - - - - - - - - - - - -->
			<xsl:call-template name="display-repeating-segment-in-accordion">
				<xsl:with-param name="segments" select="//PID"/>
				<xsl:with-param name="mode" select="'Syndromic'"/>
			</xsl:call-template>
			<xsl:call-template name="display-repeating-segment-in-accordion">
				<xsl:with-param name="segments" select="//PV1"/>
				<xsl:with-param name="mode" select="'Syndromic'"/>
			</xsl:call-template>
			<xsl:call-template name="display-repeating-segment-in-accordion">
				<xsl:with-param name="segments" select="//OBX"/>
				<xsl:with-param name="mode" select="'Syndromic'"/>
			</xsl:call-template>
		</xsl:if>
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - LRI display- - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
		<xsl:if test="$message-type = $LRI">
			<!-- - - - - - Immunization Registry information - - - - - - - - - - - -->
			<xsl:call-template name="display-repeating-segment-in-accordion">
				<xsl:with-param name="segments" select="//PID"/>
				<xsl:with-param name="mode" select="'LRI'"/>
			</xsl:call-template>
			<xsl:call-template name="display-repeating-segment-in-accordion">
				<xsl:with-param name="segments" select="//ORC"/>
				<xsl:with-param name="mode" select="'LRI'"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- Indentation values so that the output is readable -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:variable name="ind1" select="'&#9;&#9;'"/>
	<xsl:variable name="ind2" select="'&#9;&#9;&#9;&#9;&#9;'"/>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - display-segment-in-groups - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template name="display-repeating-segment-in-accordion">
		<xsl:param name="segments"/>
		<xsl:param name="mode"/>
		<xsl:variable name="multiple-segs" as="xs:boolean">
			<xsl:value-of select="count($segments) > 1"/>
		</xsl:variable>
		<xsl:if test="$multiple-segs">
			<xsl:value-of select="util:title('title', concat(util:segdesc(name($segments[1])), '[*]'),  concat(util:segdesc(name($segments[1])), '[*]'), $ind1, false(), false(), false())"/>
			<xsl:value-of select="util:tag('accordion', '')"/>
		</xsl:if>
		<xsl:for-each select="$segments">
			<xsl:variable name="index">
				<xsl:if test="$multiple-segs">
					<xsl:value-of select="concat(' - ', position())"/>
				</xsl:if>
			</xsl:variable>
			<xsl:call-template name="segments">
				<xsl:with-param name="vertical-orientation" as="xs:boolean" select="$multiple-segs"/>
				<xsl:with-param name="counter" select="$index"/>
				<xsl:with-param name="mode" select="$mode"/>
			</xsl:call-template>
		</xsl:for-each>
		<xsl:if test="$multiple-segs">
			<xsl:value-of select="util:tag('/accordion', '')"/>
			<xsl:value-of select="util:end-tab($ind1, false())"/>
		</xsl:if>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!--  since mode parameter cannot be dynamic, using this approach which simply expands with xsl:when and calls the segments with different modes as constants -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template name="segments">
		<xsl:param name="vertical-orientation" as="xs:boolean"/>
		<xsl:param name="counter"/>
		<xsl:param name="mode"/>
		<xsl:choose>
			<xsl:when test="$mode = ''">
				<xsl:apply-templates select=".">
					<xsl:with-param name="vertical-orientation" as="xs:boolean" select="$vertical-orientation"/>
					<xsl:with-param name="counter" select="$counter"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="$mode = 'Syndromic'">
				<xsl:apply-templates select="." mode="Syndromic">
					<xsl:with-param name="vertical-orientation" as="xs:boolean" select="$vertical-orientation"/>
					<xsl:with-param name="counter" select="$counter"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:when test="$mode = 'LRI'">
				<xsl:apply-templates select="." mode="LRI">
					<xsl:with-param name="vertical-orientation" as="xs:boolean" select="$vertical-orientation"/>
					<xsl:with-param name="counter" select="$counter"/>
				</xsl:apply-templates>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - Patient information - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template match="PID">
		<xsl:param name="vertical-orientation" as="xs:boolean"/>
		<xsl:param name="counter"/>
		<xsl:value-of select="util:title('title', concat('Patient Information', $counter), 'Patient Information', $ind1, false(), $vertical-orientation, false())"/>
		<xsl:value-of select="util:elements($ind1)"/>
		<xsl:value-of select="util:element('Patient Name', concat(util:format-with-space(.//PID.5.2), util:format-with-space(.//PID.5.3),.//PID.5.1.1), $ind1)"/>
		<xsl:value-of select="util:element('Mother''s Maiden Name', concat(util:format-with-space(.//PID.6.2), .//PID.6.1.1), $ind1)"/>
		<xsl:value-of select="util:element('ID Number', concat(util:format-with-space(.//PID.3.1[1]), .//PID.3.1[2]), $ind1)"/>
		<xsl:value-of select="util:element('Date/Time of Birth',util:format-date(.//PID.7.1), $ind1)"/>
		<xsl:value-of select="util:element('Administrative Sex', util:admin-sex(.//PID.8), $ind1)"/>
		<xsl:for-each select="PID.11">
			<xsl:value-of select="util:element(concat('Patient Address', ' ', util:blank-if-1(position(), count(..//PID.11))), util:format-address(PID.11.1/PID.11.1.1, PID.11.3, PID.11.4, PID.11.5, PID.11.6), $ind1)"/>
		</xsl:for-each>
		<xsl:for-each select="PID.13">
			<xsl:choose>
				<xsl:when test="PID.13.2 = 'NET'">
					<xsl:value-of select="util:element('Email', PID.13.4, $ind1)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="util:element('Local Number', util:format-tel(PID.13.6, PID.13.7), $ind1)"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
		<xsl:for-each select="PID.10">
			<xsl:value-of select="util:element(concat('Race', util:blank-if-1(position(), count(..//PID.10))), .//PID.10.2, $ind1)"/>
		</xsl:for-each>
		<xsl:for-each select="PID.22">
			<xsl:value-of select="util:element(concat('Ethnic Group', util:blank-if-1(position(), count(..//PID.22))), .//PID.22.2, $ind1)"/>
		</xsl:for-each>
		<xsl:value-of select="util:element('Multiple Birth Indicator',util:yes-no(.//PID.24), $ind1)"/>
		<xsl:value-of select="util:last-element('Birth Order',.//PID.25, $ind1, $vertical-orientation, false())"/>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - Patient information for QPD - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template match="QPD">
		<xsl:param name="vertical-orientation" as="xs:boolean"/>
		<xsl:param name="counter"/>
		<xsl:value-of select="util:title('title', concat('Patient Information', $counter), 'Patient Information', $ind1, false(), $vertical-orientation, false())"/>
		<xsl:value-of select="util:elements($ind1)"/>
		<xsl:value-of select="util:element('Patient Name', concat(util:format-with-space (.//QPD.4.2), util:format-with-space(.//QPD.4.3), .//QPD.4.1.1), $ind1)"/>
		<xsl:value-of select="util:element('Mother''s Maiden Name', .//QPD.5.1.1, $ind1)"/>
		<xsl:value-of select="util:element('ID Number', concat(util:format-with-space (.//QPD.3.1[1]), .//QPD.3.1[2]), $ind1)"/>
		<xsl:value-of select="util:element('Date/Time of Birth', util:format-date (.//QPD.6.1), $ind1)"/>
		<xsl:value-of select="util:element('Sex', util:admin-sex(.//QPD.7), $ind1)"/>
		<xsl:value-of select="util:element('Patient Address', util:format-address(.//QPD.8.1.1, .//QPD.8.3, .//QPD.8.4, .//QPD.8.5, .//QPD.8.6), $ind1)"/>
		<xsl:value-of select="util:element('Patient Phone', util:format-tel (.//QPD.9.6, .//QPD.9.7), $ind1)"/>
		<xsl:value-of select="util:element('Birth Indicator',util:yes-no(.//QPD.10), $ind1)"/>
		<xsl:value-of select="util:last-element('Birth Order',.//QPD.11, $ind1, $vertical-orientation, false())"/>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - Immunization Registry information - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template match="PD1">
		<xsl:param name="vertical-orientation" as="xs:boolean"/>
		<xsl:param name="counter"/>
		<xsl:value-of select="util:title('title', concat('Immunization Registry Information', $counter), 'Immunization Registry Information', $ind1, true(), $vertical-orientation, false())"/>
		<xsl:value-of select="util:elements($ind1)"/>
		<xsl:value-of select="util:element('Immunization Registry Status', util:imm-reg-status(.//PD1.16), $ind1)"/>
		<xsl:value-of select="util:element('Immunization Registry Status Effective Date', util:format-date(.//PD1.17), $ind1)"/>
		<xsl:value-of select="util:element('Publicity Code', .//PD1.11.2, $ind1)"/>
		<xsl:value-of select="util:element('Publicity Code Effective Date', util:format-date(.//PD1.18), $ind1)"/>
		<xsl:value-of select="util:element('Protection Indicator', util:protection-indicator(.//PD1.12), $ind1)"/>
		<xsl:value-of select="util:last-element('Protection Indicator Effective Date', util:format-date(.//PD1.13), $ind1, $vertical-orientation, false())"/>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - Guardian or Responsible Party - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template match="NK1">
		<xsl:param name="vertical-orientation" as="xs:boolean"/>
		<xsl:param name="counter"/>
		<xsl:value-of select="util:title('title', concat('Guardian or Responsible Party', $counter), 'Guardian or Responsible Party', $ind1, true(), $vertical-orientation, false())"/>
		<xsl:value-of select="util:elements($ind1)"/>
		<xsl:value-of select="util:element('Name', concat(util:format-with-space(.//NK1.2.2), util:format-with-space(.//NK1.2.3), .//NK1.2.1.1), $ind1)"/>
		<xsl:value-of select="util:element('Relationship', .//NK1.3.2, $ind1)"/>
		<xsl:for-each select="NK1.4">
			<xsl:value-of select="util:element(concat('Address', util:blank-if-1(position(), count(..//NK1.4))), util:format-address(.//NK1.4.1.1, .//NK1.4.3, .//NK1.4.4, .//NK1.4.5, .//NK1.4.6), $ind1)"/>
		</xsl:for-each>
		<xsl:for-each select="NK1.5">
			<xsl:value-of select="util:element('Phone Number', util:format-tel(NK1.5.6, NK1.5.7), $ind1)"/>
		</xsl:for-each>
		<xsl:value-of select="util:end-elements($ind1, $vertical-orientation, false())"/>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - Patient information - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template match="PID" mode="Syndromic">
		<xsl:param name="vertical-orientation" as="xs:boolean"/>
		<xsl:param name="counter"/>
		<xsl:value-of select="util:title('title', concat('Patient Information', $counter), 'Patient Information', $ind1, false(), $vertical-orientation, false())"/>
		<xsl:value-of select="util:elements($ind1)"/>
		<xsl:value-of select="util:element('Name', util:valueset(.//PID.5[2]/PID.5.7, 'HL70200'), $ind1)"/>
		<xsl:value-of select="util:element('Sex', util:valueset(.//PID.8, 'PHVS_AdministrativeSex_HL7_2x'), $ind1)"/>
		<xsl:for-each select="PID.10">
			<xsl:value-of select="util:element(concat('Race', util:blank-if-1(position(), count(..//PID.10))), util:value-or-valueset(.//PID.10.2, .//PID.10.1, 'CDCREC'), $ind1)"/>
		</xsl:for-each>
		<xsl:for-each select="PID.22">
			<xsl:value-of select="util:element(concat('Ethnic Group', util:blank-if-1(position(), count(..//PID.22))), util:value-or-valueset(.//PID.22.2, .//PID.22.1, 'CDCREC'), $ind1)"/>
		</xsl:for-each>
		<xsl:value-of select="util:element('City', .//PID.11.3, $ind1)"/>
		<xsl:value-of select="util:element('State', util:valueset(.//PID.11.4, 'PHVS_State_FIPS_5-2'), $ind1)"/>
		<xsl:value-of select="util:element('Zip Code', .//PID.11.5, $ind1)"/>
		<xsl:value-of select="util:element('Country', util:valueset(//PID.11.6, 'PHVS_Country_ISO_3166-1'), $ind1)"/>
		<xsl:value-of select="util:element('County/Parish Code', .//PID.11.9, $ind1)"/>
		<xsl:if test="not(//MSH.9.2 = 'A01')">
			<xsl:value-of select="util:element('Patient Death  Date and Time', util:format-time(.//PID.29.1), $ind1)"/>
			<!-- - - - - - time? - - - - - - - - - - - -->
			<xsl:variable name="yes" as="xs:boolean" select=".//PID.30 = 'Y' or .//PID.30 = 'y'"/>
			<xsl:value-of select="util:element('Patient Death Indicator', util:IfThenElse($yes, 'Yes', .//PID.30), $ind1)"/>
		</xsl:if>
		<xsl:value-of select="util:end-elements($ind1, $vertical-orientation, false())"/>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - Patient information - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template match="PID" mode="LRI">
		<xsl:param name="vertical-orientation" as="xs:boolean"/>
		<xsl:param name="counter"/>
		<xsl:value-of select="util:title('title', concat('Patient Information', $counter), 'Patient Information', $ind1, false(), $vertical-orientation, false())"/>
		<xsl:value-of select="util:elements($ind1)"/>
		<xsl:value-of select="util:element('Name', concat(util:format-with-space(.//PID.5.2), util:format-with-space(.//PID.5.3), util:format-with-space(.//PID.5.1.1), .//PID.5.4), $ind1)"/>
		<xsl:value-of select="util:element('Date/Time of Birth',util:format-date(.//PID.7.1), $ind1)"/>
		<xsl:value-of select="util:element('Administrative Sex', util:admin-sex(.//PID.8), $ind1)"/>
		<xsl:for-each select="PID.10">
			<xsl:value-of select="util:element(concat('Race', util:blank-if-1-variant2(position(), count(..//PID.10))), .//PID.10.2, $ind1)"/>
			<xsl:value-of select="util:element(concat('Alt Race', util:blank-if-1-variant2(position(), count(..//PID.10))), .//PID.10.5, $ind1)"/>
		</xsl:for-each>
		<xsl:value-of select="util:end-elements($ind1, $vertical-orientation, false())"/>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - Patient Visit information - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template match="PV1" mode="Syndromic">
		<xsl:param name="vertical-orientation" as="xs:boolean"/>
		<xsl:param name="counter"/>
		<xsl:value-of select="util:title('title', concat('Visit Information', $counter), 'Visit Information', $ind1, false(), $vertical-orientation, false())"/>
		<xsl:value-of select="util:elements($ind1)"/>
		<xsl:message>
					<xsl:value-of select="'------------------------------------------'"/>
					<xsl:value-of select="..//PV2.3.2"/>
					<xsl:value-of select="'------------------------------------------'"/>
					</xsl:message>
		<xsl:variable name="encounter-reason">
			<xsl:choose>
				<xsl:when test="..//PV2.3.2  != ''">
					<xsl:value-of select="..//PV2.3.2"/>
				</xsl:when>
				<xsl:when test="..//PV2.3.3 = 'I9CDX'">
					<xsl:value-of select="util:valueset(..//PV2.3.1, 'PHVS_AdministrativeDiagnosis_CDC_ICD-9CM')"/>
				</xsl:when>
				<xsl:when test="..//PV2.3.3 = 'I10'">
					<xsl:value-of select="util:valueset(..//PV2.3.1, 'PHVS_CauseOfDeath_ICD-10_CDC ')"/>
				</xsl:when>
				<xsl:when test="..//PV2.3.3 = 'SCT'">
					<xsl:value-of select="util:valueset(..//PV2.3.1, 'PHVS_Disease_CDC')"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="util:element('Admit or Encounter Reason', $encounter-reason, $ind1)"/>
		<xsl:value-of select="util:element('Admit Date and Time', util:format-time(.//PV1.44.1), $ind1)"/>
		<!-- - - - - - time? - - - - - - - - - - - -->
		<xsl:value-of select="util:element('Patient Class', util:valueset(.//PV1.2, 'PHVS_PatientClass_SyndromicSurveillance'), $ind1)"/>
		<xsl:if test="not(//MSH.9.2 = 'A01' or //MSH.9.2 = 'A04')">
			<xsl:value-of select="util:element('Discharge Disposition', util:valueset(.//PV1.36, 'HL70112'), $ind1)"/>
			<xsl:value-of select="util:element('Discharge Date/Time', util:format-time(.//PV1.45.1), $ind1)"/>
		</xsl:if>
		<xsl:value-of select="util:element('Diagnosis Type', util:valueset(..//DG1.6, 'HL70052'), $ind1)"/>
		<xsl:for-each select="..//DG1">
			<xsl:variable name="diagnosis">
				<xsl:choose>
					<xsl:when test=".//DG1.3.2  != ''">
						<xsl:value-of select=".//DG1.3.2"/>
					</xsl:when>
					<xsl:when test=".//DG1.3.3  = 'I9CDX'">
						<xsl:value-of select="util:valueset(.//DG1.3.1, 'PHVS_AdministrativeDiagnosis_CDC_ICD-9CM')"/>
					</xsl:when>
					<xsl:when test=".//DG1.3.3 = 'I10'">
						<xsl:value-of select="util:valueset(.//DG1.3.1, 'PHVS_CauseOfDeath_ICD-10_CDC ')"/>
					</xsl:when>
					<xsl:when test=".//DG1.3.3 = 'SCT'">
						<xsl:value-of select="util:valueset(.//DG1.3.1, 'PHVS_Disease_CDC')"/>
					</xsl:when>
				</xsl:choose>
			</xsl:variable>
			<xsl:value-of select="util:element('Diagnosis', $diagnosis, $ind1)"/>
		</xsl:for-each>
		<xsl:value-of select="util:end-elements($ind1, $vertical-orientation, false())"/>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - Vaccine Administration Information - - - - - - - - - - - -->
	<!-- Note the OBX subtable. Also, that the grouping based on OBX.4 -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template match="RXA">
		<xsl:param name="vertical-orientation" as="xs:boolean"/>
		<xsl:param name="counter"/>
		<xsl:value-of select="util:title('title', concat('Vaccine Administration Information', $counter), 'Vaccine Administration Information', $ind1, true(), $vertical-orientation, false())"/>
		<xsl:value-of select="util:elements($ind1)"/>
		<xsl:value-of select="util:element('Administered Vaccine', .//RXA.5.2, $ind1)"/>
		<xsl:value-of select="util:element('Date/Time Start of Administration', util:format-date(.//RXA.3.1), $ind1)"/>
		<xsl:value-of select="util:element('Administered Amount', .//RXA.6, $ind1)"/>
		<xsl:value-of select="util:element('Administered Units', .//RXA.7.2, $ind1)"/>
		<xsl:value-of select="util:element('Administration Notes', 	.//RXA.9.2, $ind1)"/>
		<xsl:value-of select="util:element('Administering Provider', concat(util:format-with-space(.//RXA.10.3), .//RXA.10.2.1), $ind1)"/>
		<xsl:value-of select="util:element('Substance Lot Number', .//RXA.15, $ind1)"/>
		<xsl:value-of select="util:element('Substance Expiration Date',	util:format-date(.//RXA.16.1), $ind1)"/>
		<xsl:value-of select="util:element('Substance Manufacturer Name', .//RXA.17.2, $ind1)"/>
		<xsl:value-of select="util:element('Substance/Treatment Refusal Reason', util:sub-refusal-reason(.//RXA.18.2), $ind1)"/>
		<xsl:value-of select="util:element('Completion Status', util:completion-status(.//RXA.20), $ind1)"/>
		<xsl:value-of select="util:element('Action Code', util:action-code(.//RXA.21), $ind1)"/>
		<xsl:value-of select="util:element('Route', ..//RXR.1.2, $ind1)"/>
		<xsl:value-of select="util:element('Administration Site', ..//RXR.2.2, $ind1)"/>
		<xsl:value-of select="util:element('Entering Organization', ..//ORC.17.2, $ind1)"/>
		<xsl:value-of select="util:element('Entered By', concat(util:format-with-space(..//ORC.10.3),..//ORC.10.2.1), $ind1)"/>
		<xsl:value-of select="util:element('Ordered By', concat(util:format-with-space(..//ORC.12.3),..//ORC.12.2.1), $ind1)"/>
		<xsl:choose>
			<xsl:when test="count(..//OBX) > 0">
				<xsl:value-of select="util:end-table($ind1)"/>
				<xsl:value-of select="util:begin-sub-table($ind2)"/>
				<xsl:variable name="recordname" select="replace(.//RXA.9.2, '^(New immunization record)|(New record)$', 'Observations', 'i')"/>
				<xsl:value-of select="util:title-no-tab('title', $recordname, $recordname, $ind2, false())"/>
				<xsl:value-of select="util:elements($ind2)"/>
				<xsl:for-each-group select="..//OBX" group-by="OBX.4">
					<xsl:for-each select="current-group()">
						<xsl:apply-templates select="."/>
					</xsl:for-each>
					<!-- grey line after the group -->
					<xsl:if test="position () != last()">
						<xsl:value-of select="util:end-obx-group($ind2)"/>
					</xsl:if>
				</xsl:for-each-group>
				<xsl:choose>
					<xsl:when test="$generate-plain-html">
						<xsl:value-of select="util:end-sub-table($ind2, $vertical-orientation)"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat($nl, $indent, util:end-sub-table($ind2, $vertical-orientation), $ind2, '}', $nl, $ind2, '}', $nl, $ind1, ']', $nl)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="util:end-elements($ind1, $vertical-orientation, false())"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!--  Observation Table: The title is from RXA -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template match="OBX">
		<xsl:variable name="obX.value">
			<xsl:choose>
				<xsl:when test=".//OBX.2= 'CE'">
					<xsl:value-of select=".//OBX.5.2"/>
				</xsl:when>
				<xsl:when test=".//OBX.2 = 'TS' or .//OBX.2 = 'DT'">
					<xsl:value-of select="util:format-date(.//OBX.5.1)"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="util:element(.//OBX.3.2, $obX.value, $ind2)"/>
	</xsl:template>
	<xsl:template match="OBX" mode="Syndromic">
		<xsl:param name="vertical-orientation" as="xs:boolean"/>
		<xsl:param name="counter"/>
		<xsl:value-of select="util:title('title', concat('Observation Results Information', $counter), 'Observation Results Information', $ind1, true(), $vertical-orientation, false())"/>
		<xsl:value-of select="util:elements($ind1)"/>
		<xsl:value-of select="util:element('Observation  Identifier', util:valueset(.//OBX.3.1, 'Observation Identifier Syndromic Surveillance'), $ind1)"/>
		<xsl:variable name="observation-value">
			<xsl:choose>
				<xsl:when test=".//OBX.3.1 = 'SS003' and (..//PV1.2 = 'O' or ..//PV1.2 = 'E')">
					<xsl:value-of select="util:value-or-valueset(.//OBX.5.2, .//OBX.5.1, 'PHVS_FacilityVisitType_SyndromicSurveillance')"/>
				</xsl:when>
				<xsl:when test=".//OBX.3.1 = '72166-2'">
					<xsl:value-of select="util:value-or-valueset(.//OBX.5.2, .//OBX.5.1, 'PHVS_SmokingStatus_MU')"/>
				</xsl:when>
				<xsl:when test=".//OBX.3.1 = '56816-2'">
					<xsl:value-of select="util:value-or-valueset(.//OBX.5.2, .//OBX.5.1, 'NHSNHealthcareServiceLocationCode')"/>
				</xsl:when>
				<xsl:when test=".//OBX.3.1 = 'SS002'">
					<xsl:value-of select="concat(.//OBX.5.1, .//OBX.5.3, util:valueset(.//OBX.5.4, 'PHVS_State_FIPS_5-2'), .//OBX.5.5, util:valueset(.//OBX.5.6, 'PHVS_Country_ISO_3166-1'), .//OBX.5.9)"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select=".//OBX.5"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="util:element('Observation  Value', $observation-value, $ind1)"/>
		<xsl:variable name="units">
			<xsl:choose>
				<xsl:when test=".//OBX.3.1 = '21612-7'">
					<xsl:value-of select="util:value-or-valueset(.//OBX.6.2 , .//OBX.6.1, 'PHVS_AgeUnit_SyndromicSurveillance')"/>
				</xsl:when>
				<xsl:when test=".//OBX.3.1 = '8302-2'">
					<xsl:value-of select="util:value-or-valueset(.//OBX.6.2 , .//OBX.6.1, 'PHVS_HeightUnit_UCUM')"/>
				</xsl:when>
				<xsl:when test=".//OBX.3.1 = '3141-9'">
					<xsl:value-of select="util:value-or-valueset(.//OBX.6.2 , .//OBX.6.1, 'PHVS_WeightUnit_UCUM')"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="util:element('Units', $units, $ind1)"/>
		<xsl:value-of select="util:last-element('Observation Results Status', util:valueset(.//OBX.11, 'HL70085'), $ind1, $vertical-orientation, false())"/>
		<!--filter mask="OBX.6.1" title="Units" codeSystems="PHVS_TemperatureUnit_UCUM:PHVS_PulseOximetryUnit_UCUM:PHVS_AgeUnit_SyndromicSurveillance"/-->
	</xsl:template>
	<xsl:template match="ORC" mode="LRI">
		<xsl:param name="vertical-orientation" as="xs:boolean"/>
		<xsl:param name="counter"/>
		<xsl:value-of select="util:title('title', concat('Order Observation', $counter), 'Ordering Provider', $ind1, true(), $vertical-orientation, false())"/>
		<xsl:value-of select="util:elements($ind1)"/>
		<xsl:value-of select="util:element('Name', concat(util:format-with-space(.//ORC.12.2.6), util:format-with-space(.//ORC.12.2.3), util:format-with-space(.//ORC.12.2.4), util:format-with-space(.//ORC.12.2.2.1), .//ORC.12.2.5), $ind1)"/>
		<xsl:value-of select="util:element('Identifier number', .//ORC.12.1, $ind1)"/>
		<xsl:value-of select="util:end-table-fieldset($ind1)"/>
		<xsl:value-of select="util:begin-sub-table($ind2)"/>
		<xsl:value-of select="util:title-no-tab('title', 'Observation Details', 'Observation Details', $ind2, false())"/>
		<xsl:value-of select="util:elements($ind2)"/>
		<xsl:value-of select="util:single-element('Observation General Information', $ind1)"/>
		<xsl:value-of select="util:element('Placer Order Number', .//ORC.2.1, $ind1)"/>
		<xsl:value-of select="util:element('Filler Order Number', .//ORC.3.1, $ind1)"/>
		<xsl:value-of select="util:element('Placer Group Number', .//ORC.4.1, $ind1)"/>
		<xsl:value-of select="util:element('&#160;', '&#160;',  $ind1)"/>
		<xsl:value-of select="util:single-element('Parent Universal Service Identifier', $ind1)"/>
		<xsl:value-of select="util:element('Identifier', .//ORC.31.1, $ind1)"/>
		<xsl:value-of select="util:element('Text', .//ORC.31.2, $ind1)"/>
		<xsl:value-of select="util:element('Alt Identifier', .//ORC.31.4, $ind1)"/>
		<xsl:value-of select="util:element('Alt Text', .//ORC.31.5, $ind1)"/>
		<xsl:value-of select="util:element('Original Text', .//ORC.31.9, $ind1)"/>
		<xsl:value-of select="util:element('&#160;', '&#160;',  $ind1)"/>
		<xsl:value-of select="util:single-element('Observation Details', $ind1)"/>
		<xsl:value-of select="util:element('Universal Service Identifier', ..//OBR.4.2, $ind1)"/>
		<xsl:value-of select="util:element('Observation Date/Time', util:format-time(..//OBR.7.1), $ind1)"/>
		<xsl:value-of select="util:element('Observation end Date/Time', util:format-time(..//OBR.8.1), $ind1)"/>
		<xsl:value-of select="util:element('Specimen Action Code', ..//OBR.11, $ind1)"/>
		<xsl:value-of select="util:element('Relevant Clinical Information', ..//OBR.13.2, $ind1)"/>
		<xsl:value-of select="util:element('Relevant Clinical Information Original Text', ..//OBR.13.9, $ind1)"/>
		<xsl:value-of select="util:element('&#160;', '&#160;',  $ind1)"/>
		<xsl:value-of select="util:single-element('Observation Result Information', $ind1)"/>
		<xsl:value-of select="util:element('Result Status', ..//OBR.25, $ind1)"/>
		<xsl:value-of select="util:element('Results Report/Status Change - Date/Time', util:format-time(..//OBR.22.1), $ind1)"/>
		<xsl:value-of select="util:element('&#160;', '&#160;',  $ind1)"/>
		
		
		<xsl:for-each select="..//OBR.28">
			<xsl:value-of select="util:single-element(concat('Results Copy To', util:blank-if-1-variant2(position(), count(..//OBR.28))), $ind1)"/>
			<xsl:value-of select="util:element('Name', concat(util:format-with-space(OBR.28.6), util:format-with-space(OBR.28.3), util:format-with-space(OBR.28.4), util:format-with-space(OBR.28.2.1), OBR.28.5), $ind1)"/>
			<xsl:value-of select="util:element('Identifier', OBR.28.1, $ind1)"/>
			<xsl:value-of select="util:element('&#160;', '&#160;',  $ind1)"/>
		</xsl:for-each>
		
		<xsl:value-of select="util:single-element('Results Handling', $ind1)"/>
		<xsl:value-of select="util:element('Standard', ..//OBR.49.9, $ind1)"/>
		<xsl:value-of select="util:element('&#160;', '&#160;',  $ind1)"/>
		<xsl:value-of select="util:single-element('Observation Notes', $ind1)"/>
		<xsl:for-each select="../NTE">
			<xsl:value-of select="util:element('Notes and comments', .//NTE.3, $ind1)"/>
		</xsl:for-each>
		<xsl:value-of select="util:single-element('', $ind1)"/>
		<xsl:value-of select="util:end-table($ind1)"/>
		<xsl:value-of select="util:begin-sub-table($ind1)"/>
		<xsl:value-of select="util:title-no-tab('title', 'Timing/Quantity Information', 'Timing/Quantity Information', $ind1, false())"/>
		<xsl:value-of select="util:elements($ind2)"/>
		<xsl:value-of select="util:element('Priority', ..//TQ1-9.2, $ind2)"/>
		<xsl:value-of select="util:element('Start Date/time	', ..//TQ1-7.1, $ind2)"/>
		<xsl:value-of select="util:element('End Date/time', ..//TQ1-8.1, $ind2)"/>
		<xsl:value-of select="util:end-table-fieldset($ind1)"/>
		<xsl:variable name="multiple-obx" select="count(..//OBX) > 1"/>
		<xsl:for-each select="..//OBX">
			<xsl:variable name="index">
				<xsl:if test="$multiple-obx">
					<xsl:value-of select="concat(' - ', position())"/>
				</xsl:if>
			</xsl:variable>
			<xsl:value-of select="util:begin-sub-table($ind2)"/>
			<xsl:value-of select="util:title-no-tab('title', concat('Results Performing Laboratory', $index), concat('Results Performing Laboratory',  $index), $ind2, false())"/>
			<xsl:value-of select="util:elements($ind2)"/>
			<xsl:value-of select="util:element('Laboratory Name', .//OBX.23.1, $ind1)"/>
			<xsl:value-of select="util:element('Organization identifier', .//OBX.23.10, $ind1)"/>
			<xsl:value-of select="util:element('Address', util:format-address(.//OBX.24.1.1, .//OBX.24.2, concat(.//OBX.24.3, ' ', .//OBX.24.4), .//OBX.24.5, .//OBX.24.6), $ind1)"/>
			<xsl:value-of select="util:element('Director Name', concat(util:format-with-space(.//OBX.25.6), util:format-with-space(.//OBX.25.3), util:format-with-space(.//OBX.25.4), util:format-with-space(.//OBX.25.2.1), util:format-with-space(.//OBX.25.5)), $ind1)"/>
			<xsl:value-of select="util:element('Director identifier', .//OBX.25.1, $ind1)"/>
			<xsl:value-of select="util:end-table-fieldset($ind1)"/>
		</xsl:for-each>
		<xsl:variable name="multiple-specimens" select="count(..//SPM) > 1"/>
		<xsl:for-each select="..//SPM">
			<xsl:variable name="index">
				<xsl:if test="$multiple-specimens">
					<xsl:value-of select="concat(' - ', position())"/>
				</xsl:if>
			</xsl:variable>
			<xsl:value-of select="util:begin-sub-table($ind2)"/>
			<xsl:value-of select="util:title-no-tab('title', concat('Specimen Information', $index), concat('Specimen Information', $index), $ind2, false())"/>
			<xsl:value-of select="util:elements($ind2)"/>
			<xsl:value-of select="util:element('Specimen Type', .//SPM.4.2, $ind1)"/>
			<xsl:value-of select="util:element('Alt Specimen Type', .//SPM.4.5, $ind1)"/>
			<xsl:value-of select="util:element('Specimen Original Text', .//SPM.4.9, $ind1)"/>
			<xsl:value-of select="util:element('Start date/time', .//SPM.17.1.1, $ind1)"/>
			<xsl:for-each select="..//SPM.21">
				<xsl:value-of select="util:element(concat('Specimen Reject Reason', util:blank-if-1-variant2(position(), count(..//SPM.21))), SPM.21.2, $ind1)"/>
				<xsl:value-of select="util:element(concat('Alt Specimen Reject Reason', util:blank-if-1-variant2(position(), count(..//SPM.21))), SPM.21.5, $ind1)"/>
				<xsl:value-of select="util:element(concat('Reject Reason Original Text', util:blank-if-1-variant2(position(), count(..//SPM.21))), SPM.21.9, $ind1)"/>
			</xsl:for-each>

			<xsl:for-each select="..//SPM.24">
				<xsl:value-of select="util:element(concat('Specimen Condition', util:blank-if-1-variant2(position(), count(..//SPM.24))), SPM.24.2, $ind1)"/>
				<xsl:value-of select="util:element(concat('Alt Specimen Condition', util:blank-if-1-variant2(position(), count(..//SPM.24))), SPM.24.5, $ind1)"/>
				<xsl:value-of select="util:element(concat('Condition Original Text', util:blank-if-1-variant2(position(), count(..//SPM.24))), SPM.24.9, $ind1)"/>
			</xsl:for-each>

			<xsl:value-of select="util:end-table-fieldset($ind1)"/>
		</xsl:for-each>
		<xsl:value-of select="util:end-tab($ind1, $vertical-orientation)"/>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!--  Observation Table: The title is from RXA -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:template match="OBX">
		<xsl:variable name="obX.value">
			<xsl:choose>
				<xsl:when test=".//OBX.2= 'CE'">
					<xsl:value-of select=".//OBX.5.2"/>
				</xsl:when>
				<xsl:when test=".//OBX.2 = 'TS' or .//OBX.2 = 'DT'">
					<xsl:value-of select="util:format-date(.//OBX.5.1)"/>
				</xsl:when>
			</xsl:choose>
		</xsl:variable>
		<xsl:value-of select="util:element(.//OBX.3.2, $obX.value, $ind2)"/>
	</xsl:template>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  Iincludes - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<xsl:include href="_util.xsl"/>
	<xsl:include href="_plain-html.xsl"/>
	<xsl:include href="_ng-tab-html.xsl"/>
	<xsl:include href="_main-html.xsl"/>
	<xsl:include href="_css_tds.xsl"/>
	<xsl:include href="_hl7tables_min2.xsl"/>
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!-- - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  - - - - - - - - - - - - -->
	<!--  to nullify the effects of the unmatched segments due to apply-templates -->
	<xsl:template match="*" mode="Syndromic">
	</xsl:template>
	<xsl:template match="*" mode="LRI">
	</xsl:template>
</xsl:stylesheet>
