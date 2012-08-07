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
			and SMS messaging via the Voxeo server. This demo illustrates the enrollment and fulfillment of two
			different types of message campaigns: Periodic Message Campaigns and Offset Message Campaigns.</p>
			
		<p>If the patient enrolls into the Periodic Message Campaign, Voxeo will send a non patient-specific message 
		every two minutes until declared unenrollment. This serves as a model for routine, program-specific health information.</p>
					
		 <a
			href="/motech-platform-server/module/campaigndemo/form/cron">Click
			here to register a patient into the system and enroll in a Periodic Message Campaign.</a>
			<br>
			<br>
		<p>If the patient enrolls into the Offset Message Campaign, Voxeo will send a pre-defined schedule of messages with a patient-specific 
		offset that can range from 0 to 72 minutes. The patient receives the messages that remain from the time 
		of enrollment until declared unenrollment or the end of the schedule. This serves as a model for pregnancy messages.</p>
			
			<a href="/motech-platform-server/module/campaigndemo/form/offset">Click
			here to register a patient into the system and enroll in an Offset Message Campaign.</a> <br>
		<br>

	</div>
</body>
</html>
