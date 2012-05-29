<%-- 
    Document   : index
    Created on : May 11, 2012, 3:10:35 AM
    Author     : cfmak
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
	<title>The servlet example </title>
	</head>
	<body>
		<h1>A simple web application</h1>
		<form method="POST" action="InvertedFileServlet">
			<label for="name">Enter your name </label>
			<input type="text" id="name" name="name"/><br><br>
			<input type="submit" value="Submit Form"/>
			<input type="reset" value="Reset Form"/>
		</form>
	</body>
</html>