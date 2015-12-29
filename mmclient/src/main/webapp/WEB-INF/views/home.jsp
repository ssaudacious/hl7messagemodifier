<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.3/jquery-ui.js"></script>	
</head>
<body>
<h1>
	NIST Tool 
</h1>

<P>  The time on the server is ${serverTime}. </P>

<button id="mmodify">Modify Message</button>

<div>
This is from the app:"
<div id="mm_div_placeholder"></div>"
</div>
<script>
//alert('starting')
$(document).ready(function() {
	//alert('starting')
	$("#mmodify").click(
		function() {
			//alert('starting')
			if ($("#mm_dialog_main").length)
				return mm_dialog_main.dialog('open');
			
			$.ajax({
		    url: "http://localhost:8080/mmserver/CardinalityTab",
		    type: 'get',
		    dataType: 'html',
		    beforeSend: function() {
		    },
		    complete: function() {
		    },      
		    success: function(html) {
		    		//$("#sub").html($(html).filter("#dialog-confirm").html());
		            $("#mm_div_placeholder").html($("<div/>").html(html)).text();;
		   }
		   });
		}
	);
});
</script>
</body>
</html>
