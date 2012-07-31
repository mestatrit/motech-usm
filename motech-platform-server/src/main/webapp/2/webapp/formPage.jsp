<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Campaign Demonstration</title>
<h5>Message Campaign Demonstration</h5>
<!-- CSS -->
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/light-blue.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/padding.css" />
<link rel="stylesheet" type="text/css" media="screen"
	href="/css/prettyPhoto.css" />
<style>
* {
	font-size: 14px;
	font-family: tahoma;
}
</style>
</head>
<body>

	<p>This is a demonstration of an offset message campaign. It uses
		the core platform (server, server-api, common) along with scheduler,
		message-campaign, cmslite, sms-api, sms-http, and voxeo.</p>
	<p>**Note: Registering a duplicate ID overwrites its phone number,
		resulting in the redirection of all campaign messages to the new
		number.</p>
	<h4>About the Campaign</h4>
	<p>The offset campaign is based off of a pregnancy message campaign
		in Ghana. Two minutes corresponds to the first message, which starts
		at the 5th week. After that there is a message every two minutes that
		corresponds to the next week's message. The offset duration determines
		the entry point into the campaign. Messages at 2 minute intervals
		follow thereafter until 72 minutes have passed, at which point the
		final week 40 message is sent. Entering 71 or 72 for an offset will
		queue you for only the final message. Entering a negative number will
		offset you further back. No offset or an invalid offset will start you
		at the default (0). Larger numbers will offset you out of the
		campaign, and you will receive no messages. Overall the campaign runs
		messages from week 5 to week 40.</p>
	<h4>Register a user into the system</h4>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/user/add">
		ID:<input type="text" name="externalId" size="12" maxlength="12" />
		Phone Number (with no special characters, like in 2071234567):<input
			type="text" name="phoneNum" size="24" maxlength="24" /> <input
			type="submit" value="Register User" />
	</form>
	<h4>Unregister a user from the system</h4>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/user/remove">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="submit" value="Unregister User" />
	</form>

	<h4>Register a user in an offset campaign</h4>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/start">
		ID:<input type="text" name="externalId" size="12" maxlength="12" />
		Offset time:<input type="text" name="offset" size="12" maxlength="12" />
		<input type="hidden" name="campaignName"
			value="Ghana Pregnancy Message Program" /> <input type="submit"
			value="Register in campaign" />
	</form>

	<h4>Unregister a user from the campaign</h4>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/stop">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="hidden" name="campaignName"
			value="Ghana Pregnancy Message Program" /> <input type="submit"
			value="Unregister" />
	</form>

	<h4>The list of all registered patients (by ID)</h4>
	<table>
		<c:forEach var="patients" items="${patients}">
			<tr>
				<td>${patients.externalid}</td>
			</tr>
		</c:forEach>
	</table>


</body>
</html>