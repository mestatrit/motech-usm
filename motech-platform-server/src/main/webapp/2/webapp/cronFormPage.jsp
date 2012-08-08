<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Periodic Campaign Demo</title>
<style>
* {
	font-size: 15px;
	font-family: tahoma;
}
</style>
</head>
<body>		
	<b>About the Periodic Message Campaign</b>
	
	<p>In order to enroll into the Periodic Message Campaign, you must first register a 
	three-digit patient ID into the system, i.e. 111, 456, 987, etc. You must also register your phone number.
	If your chosen ID has already been registered into the system at an earlier date, the new phone number submitted
	will overwrite the old phone number associated with that ID.
	Once you have successfully registered, you will see your patient ID at the list at the bottom of the page, along with all
	other patient IDs that have already been registered. </p>
	
	<p>You may choose to enroll in a campaign to receive IVR phone calls or SMS messages. Once you 
	have enrolled, you will begin receiving messages every two minutes until you choose to either unenroll 
	from the IVR or SMS campaign or completely unregister from the system.</p>
	
	<p>The message content for the periodic campaign is non patient-specific and serves as a model for routine, 
	program-specific health information.</p>

	<b>Register a Patient into the System</b>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/user/addCronUser">
		ID:<input type="text" name="externalId" size="12" maxlength="12" />
		Phone Number (with no special characters, reflecting the format 2071234567):<input
			type="text" name="phoneNum" size="24" maxlength="24" /> <input
			type="submit" value="Register Patient" />
	</form>
	<br>
	<br>
	<b>Enroll a Patient into a Periodic IVR campaign (Call every
	2 minutes)</b>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/start">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="hidden" name="campaignName" value="Cron based IVR Program" />
		<input type="submit" value="Register in the campaign" />
	</form>
	<br>
	<br>
	<b>Enroll a Patient into a Periodic SMS campaign (Text every
	2 minutes)</b>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/start">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="hidden" name="campaignName" value="Cron based SMS Program" />
		<input type="submit" value="Enroll in the Campaign" />
	</form>
	<br>
	<br>
	<b>Unenroll a Patient from the Scheduled IVR Campaign</b>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/stop">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="hidden" name="campaignName" value="Cron based IVR Program" />
		<input type="submit" value="Unenroll from the Campaign" />
	</form>
	<br>
	<br>
	<b>Unenroll a Patient from the Scheduled SMS Campaign</b>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/stop">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="hidden" name="campaignName" value="Cron based SMS Program" />
		<input type="submit" value="Unenroll from the Campaign" />
	</form>
		<br>
	<h4>Unregister a Patient from the system</h4>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/user/removeCronUser">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="submit" value="Unregister Patient" />
	</form>
	<br> <b>List of All Registered Patients (by ID)</b>
	<table>
		<c:forEach var="patients" items="${patients}">
			<tr>
				<td>${patients.externalid}</td>
			</tr>
		</c:forEach>
	</table>

</body>
</html>