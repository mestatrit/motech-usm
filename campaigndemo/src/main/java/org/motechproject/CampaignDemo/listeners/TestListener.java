package org.motechproject.CampaignDemo.listeners;

import java.util.List;

import org.motechproject.CampaignDemo.dao.PatientDAO;
import org.motechproject.CampaignDemo.model.Patient;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ivr.model.CallInitiationException;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.sms.api.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * A listener class used to listen on fired campaign message events. This class
 * demonstrates how to listen in on events and take action based upon their
 * payload. Payloads are stored as a String-Object mapping pair, where the
 * String is found in an appropriate EventKey class and the Object is the
 * relevant data or information associated with the key. The payload information
 * should be known ahead of time.
 * 
 * AllMessageCampaigns accesses the simple-message-campaign.json file found in
 * the resource package in the demo. The json file defines the characteristics
 * of a campaign.
 */
public class TestListener {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CMSLiteService cmsliteService;

    @Autowired
    private PatientDAO patientDAO;

    // Defined in the voxeo module
    @Autowired
    private IVRService ivrService;

    @Autowired
    private MessageCampaignService service;

    @Autowired
    private SmsService smsService;

    public TestListener() {

    }

    public TestListener(CMSLiteService cmsliteService, PatientDAO patientDAO,
            IVRService ivrService, MessageCampaignService service) {
        this.cmsliteService = cmsliteService;
        this.patientDAO = patientDAO;
        this.ivrService = ivrService;
        this.service = service;
    }

    /**
     * Methods are registered as listeners on specific motech events. All motech
     * events have an associated subject, which is found in an appropriate
     * EventKeys class. When an event with that particular subject is relayed,
     * this method will be invoked. The payload parameters, in this case,
     * campaign name, message key and external id, must be known ahead of time.
     * 
     * @param event
     *            The Motech event relayed by the EventRelay
     * @throws ContentNotFoundException
     */
    @MotechListener(subjects = { EventKeys.MESSAGE_CAMPAIGN_FIRED_EVENT_SUBJECT })
    public void execute(MotechEvent event) throws ContentNotFoundException {

        String campaignName = (String) event.getParameters().get(
                EventKeys.CAMPAIGN_NAME_KEY);
        String messageKey = (String) event.getParameters().get(
                EventKeys.MESSAGE_KEY);
        String externalId = (String) event.getParameters().get(
                EventKeys.EXTERNAL_ID_KEY);
        @SuppressWarnings("unchecked")
        List<String> languages = ((List<String>) event.getParameters().get(
                EventKeys.MESSAGE_LANGUAGES));
        String language = languages.get(0);
        List<String> formats = ((List<String>) event.getParameters().get(
                EventKeys.MESSAGE_FORMATS));
        String format = formats.get(0);

        List<Patient> patientList = patientDAO.findByExternalid(externalId);

        if (patientList.size() == 0) { // In the event no patient was found, the
                                       // campaign is unscheduled
            CampaignRequest toRemove = new CampaignRequest();
            toRemove.setCampaignName(campaignName);
            toRemove.setExternalId(externalId);

            service.stopAll(toRemove); // See CampaignController for
                                       // documentation on
                                       // MessageCampaignService calls
                                       // To stop a specific message:
                                       // service.stopFor(toRemove,
                                       // messageKey);
            return;
        } else {

            String phoneNum = patientList.get(0).getPhoneNum();

            /*
             * If the format is IVR, send IVR
             */
            if (format.equals("IVR")) {
                if (cmsliteService.isStreamContentAvailable(language,
                        messageKey)) {
                    StreamContent content = cmsliteService.getStreamContent(
                            language, messageKey);

                    // Call requests are used to place IVR calls. They
                    // contain a
                    // phone number, timeout duration, and vxml URL for
                    // content.
                    CallRequest request = new CallRequest(phoneNum, 119,
                            content.getName());

                    request.getPayload().put("USER_ID",
                            patientList.get(0).getExternalid()); // put Id
                                                                 // in
                                                                 // the
                                                                 // payload
                    request.setOnBusyEvent(new MotechEvent("CALL_BUSY"));
                    request.setOnFailureEvent(new MotechEvent("CALL_FAIL"));
                    request.setOnNoAnswerEvent(new MotechEvent("CALL_NO_ANSWER"));
                    request.setOnSuccessEvent(new MotechEvent("CALL_SUCCESS"));

                    /*
                     * The Voxeo module sends a request to the Voxeo website, at
                     * which point control is passed to the ccxml file. The
                     * ccxml file will play the vxmlUrl defined in the
                     * CallRequest. The vxmlUrl contains the content of the
                     * voice message. The Voxeo website informs the motech voxeo
                     * module of transition state changes in the phone call.
                     */

                    try {
                        ivrService.initiateCall(request);
                    } catch (CallInitiationException e) {
                        log.error("Unable to place the call. ", e);
                    }
                } else { // no content, don't place IVR call
                    log.error("No content available");
                }
            }

            if (format.equals("SMS")) {
                if (cmsliteService.isStringContentAvailable(language,
                        messageKey)) { // SMS
                    StringContent content = cmsliteService.getStringContent(
                            language, messageKey); // sms
                    smsService.sendSMS(phoneNum, content.getValue());

                } else { // no content, don't send SMS
                    log.error("No content available");
                }
            }
        }
    }
}
