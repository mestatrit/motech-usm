package org.motechproject.server.messagecampaign.scheduler;

import java.util.List;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndOfCampaignHandler {

    private AllCampaignEnrollments allCampaignEnrollments;

    @Autowired
    private CampaignEnrollmentService service;
    
    @Autowired
    public EndOfCampaignHandler(AllCampaignEnrollments allCampaignEnrollments){
        this.allCampaignEnrollments = allCampaignEnrollments;
    }

    @MotechListener(subjects = EventKeys.MESSAGE_CAMPAIGN_COMPLETED_EVENT_SUBJECT)
    public void handle(MotechEvent event) {
        CampaignEnrollmentsQuery query = new CampaignEnrollmentsQuery();
        String externalId = (String)event.getParameters().get(EventKeys.ENROLLMENT_KEY);
        query.withExternalId(externalId);
        List<CampaignEnrollment> enrollments = service.search(query);
        CampaignEnrollment enrollment = enrollments.get(0);
        enrollment.setStatus(CampaignEnrollmentStatus.COMPLETED);
        allCampaignEnrollments.update(enrollment);
    }
}
