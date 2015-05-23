<html>
	<head>
		<title>Add Event - Irrigation</title>
	</head>
	<body>
		<h2>Add Quick Once Off Event - Irrigation</h2>
		
		<h2>Irrigation (<a href="irrigationAddEvent.jsp">Quick Event Add</a>)</h2>
		
		<form action="index.jsp" method="POST">
			<input type="hidden" name="action" value="irrigation_quickaddevent"/>
			Duration: <input type="text" name="time" value="5"/>
			<br/><br/>
			
			<input type="radio" name="valve" value="6" checked>Bottom - House
			<br/>
			<input type="radio" name="valve" value="7">Bottom - Wall
			<br/>
			<input type="radio" name="valve" value="4">Top - Driveway
			<br/>
			<input type="radio" name="valve" value="2">Top - Gate
			<br/>
			<input type="radio" name="valve" value="3">Middle (Weak)
			
			<br/><br/>
			
			<input type="submit" value="Add Event"/>
		</form>
	</body>
</html>