<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Campaign demonstration using Voxeo</title>
</head>
<body>

	This is a demonstration of an offset message campaign. It uses the core platform (server, server-api, common) along with scheduler, message-campaign, cmslite, sms-api, sms-http and voxeo.
	<br><br>
	Registering a duplicate id will overwrite its phone
	number which would result in redirecting the campaign messages to that
	number. The offset duration determines the entry point into the campaign. In this demo, there is a message offset by 2 minutes, corresponding to a week 5 pregnancy message, and messages at 2 minute intervals thereafter, until 72 minutes, which is the final week 40 message. Entering 71 or 72 for an offset will queue you for only the final message. Entering a negative number will offset you further back. No offset or an invalid offset will start you at the default (0). Larger numbers will offset you out of the campaign and you will receive no messages.
	<br><br>
	
</body>
</html>