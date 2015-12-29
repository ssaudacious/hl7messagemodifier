
/* boot.js 
 * 
 * 
 * static javascript resource for accessing the ui from mmserver
 * 
 * jQuery and jQueryUI are dependencies
 * 
 */

var mm_app_call = new Object();  // global context
var mm_debug = true;
var mm_ports_tried = "";

jQuery(document).ready(function() {
	
	var hostString = "http://localhost:";
	var mmservice = "/mmserver/ShowMasterDialog.html";
	var content = "#cf_message_editor\\:cf_textarea";
	
	mm_app_call = function(port) {
		var currentport = location.port || (location.protocol === 'https:' ? '443' : '80');
		if (port == undefined)
			port = currentport;
		if (mm_ports_tried.indexOf(port) == -1)
			mm_ports_tried += (mm_ports_tried == "" ? "" : ";") + port;
		
		if (mm_debug) alert("Port1=" + port)
		if (jQuery("#mm_dialog_main").length)
			mm_dialog_main.dialog('open');
		else {
			if (mm_debug) alert("url = " + hostString + port + mmservice)
			$(content).append('<div style="display:none"/>')
					.load(
							hostString + port + mmservice,
							{},
							function(response, status,
									xhr) {
								if (status == "error") {
									if (port != currentport) {
										alert("Error accessing the Message Modifier Service at " + port + "! Trying " + currentport)
										mm_app_call(currentport + "")
									}
									else
										alert("Error accessing the Message Modifier Service! Tried " + mm_ports_tried +
												". The service should be run as a separate web application!");
								}
								else {
									//alert($(content).text());
									//$("#mm_message").text($(content).text());
									mm_dialog_main.dialog("close");
									mm_dialog_main.dialog("open");
								}
							}
						);
			}
		
	}

});