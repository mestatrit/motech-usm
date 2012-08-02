<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Campaign Demonstration</title>
<h2><b>Cron Campaign Demonstration using Voxeo</b></h2>
<style>
* {
	font-size: 14px;
	font-family: tahoma;
}
</style>
</head>
<body>
	<p>**Note: Registering a duplicate ID overwrites its phone number,
		resulting in the redirection of all campaign messages to the new
		number.</p>
		
	<b>About the Campaign</b>
	<p>The cron campaign will send a message every two minutes until
		the user is unregistered in the campaign or the user is removed from
		the system.</p>

	<b>Register a user into the system</b>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/user/addCronUser">
		ID:<input type="text" name="externalId" size="12" maxlength="12" />
		Phone Number (with no special characters, reflecting the format 2071234567):<input
			type="text" name="phoneNum" size="24" maxlength="24" /> <input
			type="submit" value="Register User" />
	</form>
	<br>
	<b>Unregister a user from the system</b>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/user/removeCronUser">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="submit" value="Unregister User" />
	</form>
	<br>
	<b>Register a user in a cron (periodic) IVR campaign (Call every
	2 minutes)</b>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/start">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="hidden" name="campaignName" value="Cron based IVR Program" />
		<input type="submit" value="Register in campaign" />
	</form>
	<br>
	<b>Register a user in a cron (periodic) SMS campaign (Text every
	2 minutes)</b>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/start">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="hidden" name="campaignName" value="Cron based SMS Program" />
		<input type="submit" value="Register in campaign" />
	</form>
	<br>
	<b>Unregister a user from the scheduled IVR campaign</b>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/stop">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="hidden" name="campaignName" value="Cron based IVR Program" />
		<input type="submit" value="Unregister" />
	</form>
	<br>
	<b>Unregister a User from the scheduled SMS campaign</b>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/stop">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="hidden" name="campaignName" value="Cron based SMS Program" />
		<input type="submit" value="Unregister" />
	</form>
	<br> The list of all registered patients (by ID)
	<table>
		<c:forEach var="patients" items="${patients}">
			<tr>
				<td>${patients.externalid}</td>
			</tr>
		</c:forEach>
	</table>

</body>
</html>