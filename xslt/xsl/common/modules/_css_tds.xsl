<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">

	

	<!-- css template to be output in the head for html outputs -->
	<!-- contains style for graying out and separator -->
	<xsl:template name="css">
		<style type="text/css">
			@media screen {
			.test-data-specs-main legend {text-align:center;font-size:110%; font-weight:bold;}					
			.test-data-specs-main .nav-tabs {font-weight:bold;}					
			.test-data-specs-main .tds_obxGrpSpl {background:#B8B8B8;}
			.test-data-specs-main maskByMediaType {display:table;}
			.test-data-specs-main table tbody tr th {font-size:95%}
			.test-data-specs-main table tbody tr td {font-size:100%;}
			.test-data-specs-main table tbody tr th {text-align:left;background:#C6DEFF}
			.test-data-specs-main table thead tr th {text-align:center;}
			.test-data-specs-main fieldset {text-align:center;}
			.test-data-specs-main table { width:98%;border: 1px groove;table-layout: fixed; margin:0 auto;border-collapse: collapse;}
			.test-data-specs-main table  tr { border: 3px groove; }
			.test-data-specs-main table  th { border: 2px groove;}
			.test-data-specs-main table  td { border: 2px groove; }
			.test-data-specs-main table thead {border: 1px groove;background:#446BEC;text-align:left;}
			.test-data-specs-main .separator {background:rgb(240, 240, 255); text-align:left;}
			.test-data-specs-main table tbody tr td {text-align:left}
			.test-data-specs-main .noData {background:#B8B8B8;}
			.test-data-specs-main .childField {background:#B8B8B8;}
			.test-data-specs-main .title {text-align:left;}
			.test-data-specs-main h3 {text-align:center;page-break-inside: avoid;}
			.test-data-specs-main h2 {text-align:center;}
			.test-data-specs-main h1 {text-align:center;}
			.test-data-specs-main .pgBrk {padding-top:15px;}
			.test-data-specs-main .er7Msg {width:100%;}
			.test-data-specs-main .embSpace {padding-left:15px;}			
			.test-data-specs-main .accordion-heading { font-weight:bold; font-size:90%;}										
			.test-data-specs-main .accordion-heading i.fa:after { content: "\00a0 "; }									
			.test-data-specs-main  panel { margin: 10px 5px 5px 5px; }
			}
			
			@media print {
			.test-data-specs-main legend {text-align:center;font-size:110%; font-weight:bold;}					
			.test-data-specs-main .nav-tabs {font-weight:bold;}					
			.test-data-specs-main .obxGrpSpl {background:#B8B8B8;}
			.test-data-specs-main maskByMediaType {display:table;}
			.test-data-specs-main table tbody tr th {font-size:90%}
			.test-data-specs-main table tbody tr td {font-size:90%;}
			.test-data-specs-main table tbody tr th {text-align:left;background:#C6DEFF}
			.test-data-specs-main table thead tr th {text-align:center;background:#4682B4}
			.test-data-specs-main fieldset {text-align:center;page-break-inside: avoid;}
			.test-data-specs-main table { width:98%;border: 1px groove;table-layout: fixed; margin:0 auto;page-break-inside: avoid;border-collapse: collapse;}
			.test-data-specs-main table[id=vendor-labResults] thead tr {font-size:80%}
			.test-data-specs-main table[id=vendor-labResults] tbody tr {font-size:75%}
			.test-data-specs-main table  tr { border: 3px groove; }
			.test-data-specs-main table  th { border: 2px groove;}
			.test-data-specs-main table  td { border: 2px groove; }
			.test-data-specs-main table thead {border: 1px groove;background:#446BEC;text-align:left;}
			.test-data-specs-main .separator {background:rgb(240, 240, 255); text-align:left;}
			.test-data-specs-main table tbody tr td {text-align:left;}
			.test-data-specs-main .noData {background:#B8B8B8;}
			.test-data-specs-main .childField {background:#B8B8B8;}
			.test-data-specs-main .tds_title {text-align:left;margin-bottom:1%}
			.test-data-specs-main h3 {text-align:center;}
			.test-data-specs-main h2 {text-align:center;}
			.test-data-specs-main h1 {text-align:center;}
			.test-data-specs-main .tds_pgBrk {page-break-after:always;}
			.test-data-specs-main #er7Message table {border:0px;width:80%}
			.test-data-specs-main #er7Message td {background:#B8B8B8;font-size:65%;margin-top:6.0pt;border:0px;text-wrap:preserve-breaks;white-space:pre;}
			.test-data-specs-main .er7Msg {width:100%;font-size:80%;}
			.test-data-specs-main .er7MsgNote{width:100%;font-style:italic;font-size:80%;}
			.test-data-specs-main .embSpace {padding-left:15px;}
			.test-data-specs-main .embSubSpace {padding-left:25px;}
			.test-data-specs-main .accordion-heading { font-weight:bold; font-size:90%; }										
			.test-data-specs-main .accordion-heading i.fa:after { content: "\00a0 "; }									
			.test-data-specs-main  panel { margin: 10px 5px 5px 5px; }
			}
		</style>
	</xsl:template>
	
</xsl:stylesheet>
