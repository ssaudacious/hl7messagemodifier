<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<html>
<head>
<title>Home</title>
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.11.3/themes/smoothness/jquery-ui.css">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.3/jquery-ui.js"></script>
<script>
	$(function() {
		var availableTags = [
				"set context as Immunization",
				"select scenario Blank",
				"select scenario NIST MU2.1.1 - Admin Child",
				"select scenario NIST MU2.1.r - Admin Child (replica)",
				"select scenario NIST MU2.1.r - Admin Child (replica) - 2 Months Old",
				"select scenario NIST MU2.1.r - Admin Child (replica) - 2 Years Old",
				"select scenario NIST MU2.1.r - Admin Child (replica) - 4 Years Old",
				"select scenario NIST MU2.1.r - Admin Child (replica) - 12 Years Old",
				"select scenario NIST MU2.2.1 - Admin Adult",
				"select scenario NIST MU2.2.r - Admin Adult (replica)",
				"select scenario NIST MU2.3.1 - Historical Child",
				"select scenario NIST MU2.3.r - Historical Child (replica)",
				"select scenario NIST MU2.3.r - Historical Child (replica) - 2 Months Old",
				"select scenario NIST MU2.3.r - Historical Child (replica) - 2 Years Old",
				"select scenario NIST MU2.3.r - Historical Child (replica) - 4 Years Old",
				"select scenario NIST MU2.3.r - Historical Child (replica) - 12 Years Old",
				"select scenario NIST MU2.4.1 - Consented Child",
				"select scenario NIST MU2.4.r - Consented Child (replica)",
				"select scenario NIST MU2.5.1 - Refused Toddler",
				"select scenario NIST MU2.5.r - Refused Toddler (replica)",
				"select scenario NIST MU2.5.p - Refused Toddler (replica plus immunization)",
				"select scenario NIST MU2.6.1 - Varicella History Child",
				"select scenario NIST MU2.6.r - Varicella History Child (replica)",
				"select scenario NIST MU2.6.p - Varicella History Child (replica plus immunization)",
				"select scenario NIST MU2.7.1 - Complete Record",
				"select scenario NIST MU2.7.r - Complete Record (replica)",
				"select scenario Full Record for Profiling",
				"select scenario Add and then Delete - Admin Child",
				"select scenario MCIR MPI Test",
				"add quick transform 2.5.1",
				"add quick transform Boy",
				"add quick transform Girl",
				"add quick transform Boy_or_Girl",
				"add quick transform Father",
				"add quick transform Mother",
				"add quick transform DOB",
				"add quick transform Address",
				"add quick transform Phone",
				"add quick transform Race",
				"add quick transform Ethnicity",
				"add quick transform 2.5.1",
				"add quick transform 2.3.1",
				"add quick transform Vac1_Admin",
				"add quick transform Vac1_Delete",
				"add quick transform Vac1_Hist",
				"add quick transform Vac2_Admin",
				"add quick transform Vac3_Admin",
				"add quick transform Vac2_Hist",
				"add quick transform Vac3_Hist",
				"add quick transform Vac2_NA", "clean",
				"clean no last slash", "fix ampersand", "fix escape",
				"insert segment PV1 after PID",
				"insert segment PV1 before NK1",
				"insert segment RXR after RXA#2", "insert segment PV1 last",
				"insert segment BHS first",
				"insert segment NK1 after PID if missing",
				"remove segment PID", "remove segment RXA#2",
				"remove observation 64994-7", "remove empty observation", ];
		$("#editor").autocomplete({
			source : availableTags
		});
		
	});

	function append() {
		//alert($("#transform").val());
		//alert($("#editor").val());
		$("#transform").text($("#transform").val() + "\n" + $("#editor").val());
	}
</script>
</head>
<body>
	<h1>Message Modify</h1>

	<form action="TestAction" method="POST">
		<label for="message">Message</label> <br />
		<textarea rows="10" cols="100" id="message" name="message">${originalMessage}</textarea>

		<br /> <br /> 
		
		<label for="transform">Transform script</label>
		
		<br />
		<textarea rows="10" cols="100" id="transform" name="transform">${transformScript}</textarea>
		<br /> 
		
		
		<input id="editor" name="editor" size="80" />
		
		
		<button onclick="append(); return false;">Append</button>

		<br /> <br />
		<button type="submit">Transform</button>


	</form>


	<P>
		Transformed Message <br /> <br /> <pre> ${transformedMessage}. </pre>
	</P>
</body>
</html>
