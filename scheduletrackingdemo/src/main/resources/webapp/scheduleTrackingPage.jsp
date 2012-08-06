<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Schedule tracking demo with Open MRS</title>
	<style>
        .demo-option {
            background-color:#FBFBFB;
            border:1px solid #F4F4F4;
        }
        
        .demo-box {
            margin-bottom:1em;
        }
	</style>
</head>
<body>
	<h1>Schedule Tracking Demo</h1>
	<p>
        To use this demo with a mobile device, you will need the mobile application:
	</p>
    <ul>
        <li>
            If you are using a J2ME device, you can download the mobile application over-the-air by visiting:
            <a href="http://motech.rcg.usm.maine.edu/stdemo/module/scheduletrackingdemo/mobileapp/motech-mforms-tiny.jad">
                http://motech.rcg.usm.maine.edu/stdemo/module/scheduletrackingdemo/mobileapp/motech-mforms-tiny.jad
            </a>
        </li>
        <li>
            If you do not have a J2ME device, then:
	        <ul>
	            <li>
	                Download the Java WTK emulator: 
	                <a href="http://www.oracle.com/technetwork/java/download-135801.html" target="_blank">
	                    http://www.oracle.com/technetwork/java/download-135801.html
	                </a>
	            </li>
	            <li>
	                Download the Motech application:
	                <a href="http://motech.rcg.usm.maine.edu/stdemo/module/scheduletrackingdemo/mobileapp/mobile-app.zip" target="_blank">
	                    http://motech.rcg.usm.maine.edu/stdemo/module/scheduletrackingdemo/mobileapp/mobile-app.zip
	                </a>                        
	            </li> 
	            <li>
	                Unzip the contents in motech-app.zip into a folder, then double click motech-mforms-tiny.jad
	            </li>
	        </ul>
        </li>
    </ul>
	<p>
		Registering a duplicate id will overwrite its phone number
		which would result in redirecting the messages to that number.
	</p>

    <div class="demo-option demo-box">
		<h3>Register a patient into Motech</h3>
		<form method="post" action="${pageContext.request.contextPath}/scheduletrackingdemo/patient/add">
	        <table>
	            <tr>
	                <td>ID:</td>
	                <td><input type="text" name="externalId" size="12" maxlength="12" /></td>
	                <td>Phone Number:</td>
	                <td><input type="text" name="phoneNum" size="24" maxlength="24" /> (with no special characters, like in 2071234567)</td>
	            </tr>
	            <tr>
	                <td colspan="2"><input type="submit" value="Register Motech Patient" /></td>
	            </tr>
			</table>
		</form>
	</div>
	
    <div class="demo-option demo-box">
        <h3> The list of all registered patients (by ID)</h3>
        <table style="width:100%">
            <tr>
                <td><strong>External Id</strong></td>
                <td><strong>Current Milestone</strong></td>
                <td><strong>Enrollment Status</strong></td>
                <td>&nbsp;</td>
                <td>&nbsp;</td>
            </tr>
            <c:forEach var="bean" items="${patientsList}">
                <tr>
                    <td>${bean.patient.externalid}</td>
                    <td>${bean.currentMilestone}</td>
                    <td>${bean.currentlyEnrolled}</td>
                    <c:if test="${bean.currentlyEnrolled}">
                        <td>
					        <form method="post" action="${pageContext.request.contextPath}/scheduletrackingdemo/enroll/stop">
					            <input type="hidden" name="externalID" value="${bean.patient.externalid}" />
					            <input type="hidden" name="scheduleName" value="Demo Concept Schedule">
					            <input type="submit" value="Unenroll User" />
					        </form>                        
                        </td>                        
                        <td>
                            <button disabled="disabled">Unregister Patient</button>
                        </td>
                    </c:if>
                    <c:if test="${not bean.currentlyEnrolled}">
                        <td>
					        <form method="post" action="${pageContext.request.contextPath}/scheduletrackingdemo/enroll/start">
					            <input type="hidden" name="externalID" value="${bean.patient.externalid}" /> 
					            <input type="hidden" name="scheduleName" value="Demo Concept Schedule">
					            <input type="submit" value="Enroll User" />
					        </form>                        
                        </td>
                        <td>
					        <form method="post" action="${pageContext.request.contextPath}/scheduletrackingdemo/patient/remove">
					            <input type="hidden" name="externalId" value="${bean.patient.externalid}" />
					            <input type="submit" value="Unregister Motech Patient" />
					        </form>
                        </td>
                    </c:if>
                </tr>
            </c:forEach>
        </table>
    </div>
</body>
</html>
