package org.motechproject.scheduletrackingdemo.listeners;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.model.Time;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.domain.exception.DefaultedMilestoneFulfillmentException;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.domain.exception.NoMoreMilestonesToFulfillException;
import org.motechproject.scheduletracking.api.events.DefaultmentCaptureEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.scheduletrackingdemo.OpenMrsClient;
import org.motechproject.scheduletrackingdemo.DAO.MRSPatientDAO;
import org.motechproject.scheduletrackingdemo.model.Patient;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.sms.api.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MilestoneListener {

    private static Logger logger = LoggerFactory
            .getLogger(MilestoneListener.class);

    @Autowired
    private IVRService voxeoService;

    @Autowired
    private OpenMrsClient openmrsClient;

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    private MRSPatientDAO patientDAO;

    @Autowired
    private CMSLiteService cmsliteService;

    @Autowired
    private SmsService smsService;

    @MotechListener(subjects = { EventSubjects.MILESTONE_ALERT })
    public void execute(org.motechproject.scheduler.domain.MotechEvent event) {
        MilestoneEvent mEvent = new MilestoneEvent(event);
        
        logger.debug("Handled milestone event for: " + mEvent.getExternalId() + " --- " + mEvent.getScheduleName()
                + " --- " + mEvent.getWindowName());
        
        Schedule schedule = scheduleTrackingService.getSchedule(mEvent.getScheduleName());
        Milestone milestone = schedule.getMilestone(mEvent.getMilestoneName());
        Map<String, String> milestoneData = milestone.getData();
        
        String milestoneConceptName = milestoneData.get("conceptName");

        if (milestoneConceptName == null)
            return; // This method does not handle events without conceptName

        boolean hasFulfilledMilestone = openmrsClient.hasConcept(mEvent.getExternalId(), milestoneConceptName);

        if (hasFulfilledMilestone && fulfilledWithinScheduleEnrollment(mEvent, milestoneConceptName)) {
            logger.debug("Fulfilling milestone for: " + mEvent.getExternalId() + " with schedule: "
                    + mEvent.getScheduleName());

            try {
                LocalTime time = LocalTime.now();
                scheduleTrackingService.fulfillCurrentMilestone(mEvent.getExternalId(), mEvent.getScheduleName(),
                        LocalDate.now(), new Time(time.getHourOfDay(), time.getMinuteOfHour()));
            } catch (InvalidEnrollmentException e) {

            } catch (DefaultedMilestoneFulfillmentException e2) {

            } catch (NoMoreMilestonesToFulfillException e3) {

            }
        } else if (!mEvent.getWindowName().equals("max")) {
            logger.debug("Sending a message for milestone event");
            // Place calls and/or text messages, but not for the max alerts
            List<Patient> patientList = patientDAO.findByExternalid(mEvent.getExternalId());

            if (patientList.size() > 0) {
                String IVRFormat = milestoneData.get("IVRFormat");
                String SMSFormat = milestoneData.get("SMSFormat");
                String language = milestoneData.get("language");
                String messageName = milestoneData.get("messageName");

                if ("true".equals(IVRFormat) && language != null && messageName != null) {
                    String ivrMessageName = messageName.concat("IVR");
                    this.placeCall(patientList.get(0), language, ivrMessageName, mEvent.getWindowName());
                }

                if ("true".equals(SMSFormat) && language != null && messageName != null) {
                    String smsMessageName = messageName.concat("SMS");
                    this.sendSMS(patientList.get(0), language, smsMessageName, mEvent.getWindowName());
                }
            }
        }
    }

    private boolean fulfilledWithinScheduleEnrollment(MilestoneEvent mEvent, String milestoneConceptName) {
        // The OpenMRS (at least version 1.8.3) does not store the time for an observation, only the date
        // therefore we can only verify the day on which the observation happened
        // to make matters worse, the mobile client sends the date of the observation in UTC time at midnight
        // depending on the time zone, this could result in the date being interpreted as the day before
        DateTime referenceDate = mEvent.getReferenceDateTime();
        DateTime dayBeforeAtMidnight = referenceDate.minusDays(1).toDateMidnight().toDateTime();
        DateTime fulfilled = openmrsClient.lastTimeFulfilledDateTimeObs(mEvent.getExternalId(), milestoneConceptName);
        return (dayBeforeAtMidnight.isBefore(fulfilled) || dayBeforeAtMidnight.isEqual(fulfilled));
    }

    @MotechListener(subjects = { EventSubjects.DEFAULTMENT_CAPTURE })
    public void defaulted(MotechEvent event) {
        logger.debug("Handled milestone defaultment capture event");
        DefaultmentCaptureEvent mEvent = new DefaultmentCaptureEvent(event);
        List<Patient> patientList = patientDAO.findByExternalid(mEvent.getExternalId());

        if (patientList.size() > 0) {
            this.placeCall(patientList.get(0), "en", "defaulted-demo-message-ivr", "");
            this.sendSMS(patientList.get(0), "en", "defaulted-demo-message", "");
        }
    }

    private void placeCall(Patient patient, String language, String messageName, String windowName) {
        if (cmsliteService.isStringContentAvailable(language, messageName + windowName)) {
            StringContent content = null;
            try {
                content = cmsliteService.getStringContent(language, messageName + windowName);
            } catch (ContentNotFoundException e) {
                logger.error("Failed to retrieve IVR content for language: " + language + " and name: " + messageName
                        + windowName);
                return;
            }

            CallRequest request = new CallRequest(patient.getPhoneNum(), 119, content.getValue());
            request.getPayload().put("USER_ID", patient.getExternalid());
            request.getPayload().put("applicationName", "ScheduleTrackingDemo");
            voxeoService.initiateCall(request);
        } else {
            logger.error("Could not find IVR content for language: " + language + " and name: " + messageName
                    + windowName);
        }
    }

    private void sendSMS(Patient patient, String language, String messageName, String windowName) {
        if (cmsliteService.isStringContentAvailable(language, messageName + windowName)) {
            StringContent content = null;
            try {
                content = cmsliteService.getStringContent(language, messageName + windowName);
            } catch (ContentNotFoundException e) {
                logger.error("Failed to retrieve SMS content for language: " + language + " and name: " + messageName
                        + windowName);
                return;
            }
            smsService.sendSMS(patient.getPhoneNum(), content.getValue());
        } else { // no content, don't send SMS
            logger.error("Could not find SMS content for language: " + language + " and name: " + messageName
                    + windowName);
        }
    }
}
