/*package org.motechproject.CampaignDemo.listeners;

import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.CampaignDemo.dao.PatientDAO;
import org.motechproject.CampaignDemo.model.Patient;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.ivr.service.CallRequest;
import org.motechproject.ivr.service.IVRService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;

@RunWith(MockitoJUnitRunner.class)
public class TestListenerIT {

    private TestListener listener;

    @Mock
    private PatientDAO patientDAO;

    @Mock
    private IVRService ivrService;

    @Mock
    private MessageCampaignService service;

    @Mock
    private CMSLiteService cmsliteService;

    @Before
    public void initMocks() {
        listener = new TestListener(cmsliteService, patientDAO, ivrService,
                service);
    }

    @Test
    public void testWhenPatientExists() throws ContentNotFoundException {
        List<String> languages = new ArrayList<String>();
        languages.add("en");

        List<String> formats = new ArrayList<String>();
        formats.add("IVR");

        StringContent testContentIVR = new StringContent("en",
                "cron-messageIVR", "demo.xml");
        String campaignName = testContentIVR.getName();

        MotechEvent event = new MotechEvent(
                EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT);
        event.getParameters().put(EventKeys.CAMPAIGN_NAME_KEY, campaignName);
        event.getParameters().put(EventKeys.MESSAGE_KEY, "TestCampaignKey");
        event.getParameters().put(EventKeys.SCHEDULE_JOB_ID_KEY, "12345");
        event.getParameters().put(EventKeys.MESSAGE_LANGUAGES, languages);
        event.getParameters().put(EventKeys.MESSAGE_FORMATS, formats);

        List<Patient> patientList = new ArrayList<Patient>();
        Patient testPatient = new Patient("12345", "207");
        patientList.add(testPatient);

        Mockito.when(patientDAO.findByExternalid("12345")).thenReturn(
                patientList);
        Mockito.when(cmsliteService.getStringContent("en", "TestCampaignKey"))
                .thenReturn(testContentIVR);
        Mockito.when(
                cmsliteService
                        .isStringContentAvailable("en", "TestCampaignKey"))
                .thenReturn(true);
        listener.execute(event);

        verify(ivrService).initiateCall(Matchers.any(CallRequest.class));
    }

    @Test
    public void testWhenPatientDoesNotExist() throws ContentNotFoundException {

        List<String> languages = new ArrayList<String>();
        languages.add("en");

        List<String> formats = new ArrayList<String>();
        formats.add("IVR");

        StringContent testContentIVR = new StringContent("en",
                "cron-messageIVR", "demo.xml");
        String campaignName = testContentIVR.getName();

        MotechEvent event = new MotechEvent(
                EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT);
        event.getParameters().put(EventKeys.CAMPAIGN_NAME_KEY, campaignName);
        event.getParameters().put(EventKeys.MESSAGE_KEY, "TestCampaignKey");
        event.getParameters().put(EventKeys.SCHEDULE_JOB_ID_KEY, "12345");
        event.getParameters().put(EventKeys.MESSAGE_LANGUAGES, languages);
        event.getParameters().put(EventKeys.MESSAGE_FORMATS, formats);

        Mockito.when(patientDAO.findByExternalid("12345")).thenReturn(
                new ArrayList<Patient>());
        Mockito.when(cmsliteService.getStringContent("en", "TestCampaignKey"))
                .thenReturn(testContentIVR);
        Mockito.when(cmsliteService.getStringContent("en", "TestCampaignKey"))
                .thenReturn(testContentIVR);

        listener.execute(event);

        verify(service).stopAll(Matchers.any(CampaignRequest.class));
    }

}
*/