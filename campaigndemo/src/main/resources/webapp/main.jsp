<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Campaign Demonstration</title>
<div style="
	font-size: 20px;
	font-family: tahoma;">
<h2>
	<b>Message Campaign Demonstration using Voxeo</b>
</h2>
</div>
</head>
<body>
	<div style="font-size: 16px; font-family: tahoma;">

		<p>This is a demonstration of message campaigns with voice calling
			and SMS messaging via the Voxeo server.<br> It uses the core platform modules
			(Server, Server API, and Common) along with Scheduler, Message Campaign,<br>
			CMSLite, SMS API, SMS HTTP, and Voxeo modules, all of which are
			running in an OSGi environment.</p>
		<p>This demo illustrates the enrollment and fulfillment of two
			different types of message campaigns: cron-based and offset-based.</p>
		<a href="/motech-platform-server/module/campaigndemo/form/offset">Click
			here to learn more about and enroll in an offset campaign.</a> <br>
		<br> <a
			href="/motech-platform-server/module/campaigndemo/form/cron">Click
			here to learn more about and enroll in a cron campaign.</a>

	</div>
</body>
</html>
