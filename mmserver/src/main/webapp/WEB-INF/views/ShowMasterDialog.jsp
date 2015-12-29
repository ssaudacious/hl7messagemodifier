<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Home</title>


<!--  todo: add these dynamically, after determining the version of the jquery ui already present -->
<link type="text/css" rel="stylesheet"
	href="${appuri}/resources/core-css/jquery.ui-1.8.23/jquery.ui.tabs.css" />
<link type="text/css" rel="stylesheet"
	href="${appuri}/resources/core-css/jquery.ui-1.8.23/jquery.ui.dialog.css" />
<link type="text/css" rel="stylesheet"
	href="${appuri}/resources/core-css/mmserver/editableSelect.css" />

<script type="text/javascript"
	src="${appuri}/resources/core-js/jquery.ui-1.8.23/jquery.ui.tabs.js"></script>
<script type="text/javascript"
	src="${appuri}/resources/core-js/jquery.ui-1.8.23/jquery.ui.dialog.js"></script>
<script type="text/javascript"
	src="${appuri}/resources/core-js/mmserver/editableSelect.js"></script>



<script>
	jQuery(document).ready(function($) {
		/* turn mm_debug on from the caller's side to enable debug alerts */
		if (mm_app.debug)
			alert("Loading from the plugin: " + $("#mm_appuri").text());

		// create tabs
		$("#mm_tabs").tabs();

		// event handlers
		// button to transform message
		$("#mm_transform").click(function() {
			if (mm_app.debug)
				alert('starting uri:' + $("#mm_appuri").text())
			//alert("openmodule" + mm_app.openmodule + "Profile: "  + $("#mm_profile_" + mm_app.openmodule).text());
			$.ajax({
				url : $("#mm_appuri").text() + "/ApplyCardinality.html",
				//contentType: "application/json",
				type : 'POST',
				dataType : 'html',
				data : {
					message : $("#mm_message").val(),
					profile : $("#mm_profile_" + mm_app.openmodule).text(),
					element: $("#mm_element").val(),
					option: $("#mm_option").val(),
					occurrences: $("#mm_occurrences").val(),
					datasource: $("#mm_datasource").val(),
					custdata: $("#mm_customdata").val()
				},
				beforeSend : function() {
				},
				complete : function() {
				},
				error : function(jqHR) {
					var str = "";
					$.each(jqHR, function(key, element) {
						str += 'key: ' + key + '\n' + 'value: ' + element;
					});
					if (mm_app.debug)
						alert("Error!" + str);
					else
						alert("Error modifying the message!");
					//todo: add a regular dialog
					mm_showstatus("Error modifiying the message!");
				},
				success : function(data) {
					$("#mm_message").val($(data).filter('#mm_ajax').text());
					mm_showstatus("Successfully modified the message!");
				}
			});
		});

		// initialize the basic dialog
		// the extra .dialog({}) can be removed
		mm_app.dialog_main = $("#mm_dialog_main").dialog({}).dialog({
			resizable : true,
			height : 700,
			width : 900,
			modal : true,
			resize: function(event, ui) { setMessageWidth(); }, // reset the contents
			buttons : {
				"Copy Message" : function() {  // need to copy it back to the parent app
					mm_app.editor.getEditor().setValue($("#mm_message").val());
					//mm_app.editor.rebuild();
					mm_app.dialog_main.dialog("close");
				},
				"Refresh Message" : function() {  // need to copy it back to the parent app
					if (!/\S/.test(mm_app.content.val())) {
						mm_showstatus("Content empty. Setting it to sample message  IZ_1_1.1_Max for test purposes");
						$("#mm_message").val($("#mm_originalmessage").val());
					} else {
						$("#mm_message").val(mm_app.content.val());		
						mm_showstatus("Message refreshed from the application window!");
					}
				},
				Cancel : function() {  // nothing to do
					mm_app.dialog_main.dialog("close");
				}
			}
		});
		
		setMessageWidth();  // expand the message to fill the tab
		function setMessageWidth() {
			$("#mm_message").width($("#mm_tabs-card").width() - 10 /* scroll bar : should we get the scrollbar width?*/);
		}
	});
	
	// used by editable select
	var mm_appuri = $("#mm_appuri").text(); 
	arrowImage = mm_appuri + '/resources/core-images/select_arrow.gif';	// Regular arrow
	arrowImageOver = mm_appuri + '/resources/core-images/select_arrow_over.gif';	// Mouse over
	arrowImageDown = mm_appuri + '/resources/core-images/select_arrow_down.gif';	// Mouse down
	
	createEditableSelect($("#mm_element").get(0));
	createEditableSelect($("#mm_option").get(0));
	
	// customdata's visibility based on the choice
	$("#mm_customdata").hide();
	$("#mm_datasource").change(function() { 
		if ($("#mm_datasource").val() == "cust")
			$("#mm_customdata").show();
		else
			$("#mm_customdata").hide();
	})
	
	function mm_showstatus(msg) {
		$("#mm_status_message").html("<br/>" + msg).show().delay(5000).fadeOut();
		// show the default after 6 seconds
		setTimeout( function() {
			$("#mm_status_message").html("<br/>Please enter the parameters and click Modify Message!").show(); },
			6000);
	}
</script>
</head>



<body>
	<h1>Message Modify</h1>
	<div id="mm_dialog_main" title="Modify Message">
		<span id="mm_appuri" style="display: none">${appuri}</span>
		<div id="mm_tabs">
			<ul>
				<li><a href="#mm_tabs-card">Cardinality</a></li>
				<li><a href="#mm_tabs-length">Length</a></li>
				<li><a href="#mm_tabs-error">Error</a></li>
				<li><a href="#mm_tabs-advanced">Advanced</a></li>
				<li><a href="#mm_tabs-about">About</a></li>
			</ul>

			<div id="mm_tabs-card">
				<!---------------------------------------------------------------->
				<label for="message">Message</label> <br />
				<textarea rows="15" cols="100" id="mm_message" name="message"
					wrap="off">${originalMessage}</textarea><br /> <br /> <br />
					 
				<!---------------------------------------------------------------->
				Enter or select element(s) for modifying cardinality: <br> 
				<input size=60
					id="mm_element" name="element" value= "${element }" 
					selectBoxOptions=";All Fields;All Segments;MSH-21;PID-3" />
				
				&nbsp;&nbsp;(e.g. OBR#2:OBX#3-5 refers to the 5th field in the 3rd OBX after the 2nd OBR)<br /> <br />

				<!---------------------------------------------------------------->
				Select cardinality options: 
				<input size=20 id="mm_option" value="${option }"
					name="cardinality" selectBoxOptions="MAX from Profile;MIN from Profile;RANDOM;AS ENTERED BELOW" /> 	&nbsp;&nbsp;			
				
				<!---------------------------------------------------------------->
				Limit: 
				<input size=10 id="mm_occurrences"
					name="occurrences" value="${occurrences }" /> 
					
				<br /> <br /> <br />

				<!---------------------------------------------------------------->
				<label for="mm_datasource">Select data source</label> <br /> <select
					id="mm_datasource" name="datasource">
					<option value="ds1">Example Data source 1 (no OBX)</option>
					<option value="ds2">Example Data source 2 (100 OBX)</option>
					<option value="%">Random Data Source (selects randomly from the above)</option>
					<option value="cust">Enter custom data manually</option>
				</select> <br/>
				
				<input size=60 id="mm_customdata" name="mm_customdata" /> <br /> <br />
				<!-- button id="mm_transform2">Transform</button-->
				<button id="mm_transform">Modify Message</button>

			</div>
			<div id="mm_tabs-length"></div>
			<div id="mm_tabs-error"></div>
			<div id="mm_tabs-advanced"></div>
			<div id="mm_tabs-about">
			Message Modifier <br/>
			Beta 1.0 <br/>
			Release date 04/30/205 <br/>			
			</div>

		</div>
		<div id="mm_status_message"><br/>Please enter the parameters and click Modify Message!</div>
	</div>
	<input type="hidden" id="mm_profile" name="mm_profile" value="${profile }" />
	<input type="hidden" id="mm_originalmessage" name="mm_originalmessage" value="${originalMessage}" />
</body>
</html>
