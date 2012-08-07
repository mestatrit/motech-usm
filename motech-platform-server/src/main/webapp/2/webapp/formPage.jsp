<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Offset Campaign Demo</title>
<style>
* {
	font-size: 16px;
	font-family: tahoma;
}
</style>
</head>
<body>

	<h4>About the Offset Message Campaign</h4>
	
	<p>In order to enroll into the Offset Message Campaign, you must first register a 
	three-digit patient ID into the system, i.e. 111, 456, 987, etc. You must also register your phone number.
	If your chosen ID has already been registered into the system at an earlier date, the new phone number submitted
	will overwrite the old phone number associated with that ID. Once you have successfully registered, you will see 
	your patient ID at the list at the bottom of the page, along with all
	other patient IDs that have already been registered. </p>
	
	<p>The Offset Message Campaign is based off of a pregnancy message campaign in Ghana. You may choose to enroll in a campaign to receive 
	IVR phone calls or SMS messages. The offset value submitted will indicate your entry point into the campaign. The first message 
	in the campaign corresponds to two minutes from an offset value of zero. This means that an offset value of zero will result in the first 
	message being sent after a two minute wait. The first message corresponds to week five of the pregnancy. After the first message, there 
	is a new message sent every two minutes that corresponds to the next week of pregnancy. Messages are sent until 72 minutes have passed, at 
	which point the final pregnancy week 40 message is sent.</p>
		
		<p>Entering 71 or 72 for an offset will queue you for only the final message. Entering a negative number will offset you further back. 
		No offset or an invalid offset will start you at the default offset (0). Larger numbers will offset you out of the campaign, and you will 
		receive no messages. Once you have enrolled, you will continue to receive messages until you choose to unenroll 
		from the chosen campaign (IVR or SMS), completely unregister from the system, or until the completion of the 
		Offset Message Campaign schedule after 72 minutes.</p>
		
	<h4>Register a Patient into the System</h4>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/user/addOffsetUser">
		ID:<input type="text" name="externalId" size="12" maxlength="12" />
		Phone Number (with no special characters, reflecting the format 2071234567):<input
			type="text" name="phoneNum" size="24" maxlength="24" /> <input
			type="submit" value="Register Patient" />
	</form>

	<h4>Enroll a Patient in an Offset SMS Message Campaign</h4>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/start">
		ID:<input type="text" name="externalId" size="12" maxlength="12" />
		Offset:<input type="text" name="offset" size="12" maxlength="12" />
		<input type="hidden" name="campaignName"
			value="Ghana Pregnancy SMS Program" /> <input type="submit"
			value="Enroll in the Campaign" />
	</form>
	
	<h4>Enroll a Patient in an Offset IVR Message Campaign</h4>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/start">
		ID:<input type="text" name="externalId" size="12" maxlength="12" />
		Offset:<input type="text" name="offset" size="12" maxlength="12" />
		<input type="hidden" name="campaignName"
			value="Ghana Pregnancy IVR Program" /> <input type="submit"
			value="Enroll in the Campaign" />
	</form>
	
	<h4>Unenroll a Patient from the SMS campaign</h4>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/stop">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="hidden" name="campaignName"
			value="Ghana Pregnancy SMS Program" /> <input type="submit"
			value="Unenroll from the Campaign" />
	</form>

	<h4>Unenroll a Patient from the IVR campaign</h4>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/stop">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="hidden" name="campaignName"
			value="Ghana Pregnancy IVR Program" /> <input type="submit"
			value="Unenroll from the Campaign" />
	</form>

	<h4>Unregister a Patient from the System</h4>
	<form method="post"
		action="/motech-platform-server/module/campaigndemo/user/removeOffsetUser">
		ID:<input type="text" name="externalId" size="12" maxlength="12" /> <input
			type="submit" value="Unregister Patient" />
	</form>
	<h4>List of All Registered Patients (by ID)</h4>
	<table>
		<c:forEach var="patients" items="${patients}">
			<tr>
				<td>${patients.externalid}</td>
			</tr>
		</c:forEach>
	</table>


</body>
</html>