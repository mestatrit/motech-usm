package org.motechproject.scheduletrackingdemo.DAO;

import java.io.InputStream;
import java.util.Properties;

import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.StreamContent;
import org.motechproject.cmslite.api.model.StringContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/*
 * Class to initialize CMSlite content in the DB upon server startup
 * Currently, the content is the name of the voice XML file to be run by Voxeo.
 */

public class ScheduleContentInitiator {

    @Autowired
    private CMSLiteService cmsliteService;

    @Autowired
    @Qualifier(value = "scheduleMessages")
    private Properties properties;

    public void bootstrap() throws CMSLiteException {
        for (int i = 1; i <= 4; i++) {
            InputStream demoMessageStream = this.getClass()
                    .getResourceAsStream("/duedemoconcept" + i + ".wav");
            StreamContent demoFile = new StreamContent("en",
                    "DemoConceptQuestionIVR" + i + "due", demoMessageStream,
                    "checksum" + i, "audio/wav"); // IVR
            InputStream demoMessageStream2 = this.getClass()
                    .getResourceAsStream("/latedemoconcept" + i + ".wav");
            StreamContent demoFile2 = new StreamContent("en",
                    "DemoConceptQuestionIVR" + i + "late", demoMessageStream2,
                    "checksum" + i, "audio/wav"); // IVR

            try {
                cmsliteService.addContent(demoFile);
                cmsliteService.addContent(demoFile2);
            } catch (CMSLiteException e) {
            }

            StringContent IVRDue = new StringContent("en", "DemoConceptQuestion" + i + "IVRdue", "due" + i + ".xml");
            cmsliteService.addContent(IVRDue);

            StringContent SMSDue = new StringContent("en", "DemoConceptQuestion" + i + "SMSdue", getDemoDueMessage(i));
            cmsliteService.addContent(SMSDue); 
            
            StringContent IVRLate = new StringContent("en", "DemoConceptQuestion" + i + "IVRlate", "late" + i + ".xml");
            cmsliteService.addContent(IVRLate); // IVR
            
            StringContent SMSLate = new StringContent("en", "DemoConceptQuestion" + i + "SMSlate", getDemoLateMessage(i));
            cmsliteService.addContent(SMSLate); 

        }

        InputStream inputStreamToResource1 = this.getClass().getResourceAsStream("/defaulteddemoschedule.wav");
        
        StreamContent defaulted = new StreamContent("en", "defaultedDemoSchedule",inputStreamToResource1, "checksum1", "audio/wav"); // IVR
        cmsliteService.addContent(defaulted);
        
        cmsliteService.addContent(new StringContent("en", "defaulted-demo-message", "defaulted.xml")); // IVR
        cmsliteService.addContent(new StringContent(
                        "en",
                        "defaulted-demo-message",
                        "You have "
                                + "defaulted on your Demo Concept Schedule. Please visit your doctor for more information.")); // SMS

    }

    private String getDemoDueMessage(int messageNumber) {
        return this.properties.getProperty("SMSDemoConceptQuestion"
                + messageNumber + "Due");
    }

    private String getDemoLateMessage(int messageNumber) {
        return this.properties.getProperty("SMSDemoConceptQuestion"
                + messageNumber + "Late");
    }

}
