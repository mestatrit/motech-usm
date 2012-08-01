package org.motechproject.CampaignDemo.dao;

import java.io.InputStream;
import java.util.Properties;

import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/*
 * Class to initialize CMSlite content in the DB upon server startup Currently,
 * the content is the name of the voice XML file to be run by Voxeo.
 */

public class ContentInitiator {

    @Autowired
    private CMSLiteService cmsLiteService;

    @Autowired
    @Qualifier(value = "pregnancyMessages")
    private Properties properties;

    public void bootstrap() throws CMSLiteException {
        for (int i = 5; i <= 40; i++) {
            InputStream ghanaMessageStream = this.getClass()
                    .getResourceAsStream("/week" + i + ".wav");

            StreamContent ghanaFile = new StreamContent("en",
                    "ghanaPregnancyWeek" + i, ghanaMessageStream, "checksum"
                            + i, "audio/wav"); // IVR
            try {
                cmsLiteService.addContent(ghanaFile);
            } catch (CMSLiteException e) {
            }
            StringContent pregnancyCampaignIVR = new StringContent("en",
                    "ghanaPregnancyWeekNum" + i, "week" + i + ".xml"); // IVR
            StringContent pregnancyCampaignSMS = new StringContent("en",
                    "pregnancy-info-week-" + i, getPregnancyMessage(i)); // SMS

            try {
                cmsLiteService.addContent(pregnancyCampaignIVR);
                cmsLiteService.addContent(pregnancyCampaignSMS);
            } catch (CMSLiteException e) {
            }
        }

        InputStream inputStreamToResource1 = this.getClass()
                .getResourceAsStream("/cronmessage.wav");
        StreamContent cronIVR = new StreamContent("en", "test",
                inputStreamToResource1, "checksum1", "audio/wav"); // IVR
        cmsLiteService.addContent(cronIVR);

        StringContent cronVxmlIVR = new StringContent("en", "cronIVRMessage",
                "cron.xml");
        cmsLiteService.addContent(cronVxmlIVR); // IVR

        StringContent hardCronSMS = new StringContent("en", "cron-message",
                "This is an SMS cron message that will repeat every "
                        + "two minutes until you unenroll");
        cmsLiteService.addContent(hardCronSMS); // SMS

    }

    private String getPregnancyMessage(int messageNumber) {
        return this.properties.getProperty("messageWeek" + messageNumber);
    }

}
