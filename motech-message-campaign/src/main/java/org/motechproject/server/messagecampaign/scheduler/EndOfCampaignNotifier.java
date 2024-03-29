package org.motechproject.server.messagecampaign.scheduler;

import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.event.EventRelay;
import org.motechproject.server.event.annotations.MotechListener;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.quartz.TriggerKey.triggerKey;

public class EndOfCampaignNotifier {

    private Scheduler scheduler;
    private JobIdFactory jobIdFactory;
    private EventRelay eventRelay;
    private AllCampaignEnrollments allCampaignEnrollments;

    @Autowired
    public EndOfCampaignNotifier(SchedulerFactoryBean schedulerFactoryBean, JobIdFactory jobIdFactory, EventRelay eventRelay, AllCampaignEnrollments allCampaignEnrollments) {
        this.jobIdFactory = jobIdFactory;
        this.eventRelay = eventRelay;
        this.allCampaignEnrollments = allCampaignEnrollments;
        this.scheduler = schedulerFactoryBean.getScheduler();
    }

    @MotechListener(subjects = EventKeys.SEND_MESSAGE)
    public void handle(MotechEvent event) throws SchedulerException {
        String campaignName = (String) event.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY);
        String externalId = (String) event.getParameters().get(EventKeys.EXTERNAL_ID_KEY);
        String messageKey = (String) event.getParameters().get(EventKeys.MESSAGE_KEY);

        String jobId = jobIdFactory.getMessageJobIdFor(campaignName, externalId, messageKey);
        if (nextFireTimeFor(jobId) == null) {
            markEnrollmentAsComplete(externalId, campaignName);

            Map<String, Object> params = new HashMap<>();
            params.put(EventKeys.EXTERNAL_ID_KEY, externalId);
            params.put(EventKeys.CAMPAIGN_NAME_KEY, campaignName);
            MotechEvent endOfCampaignEvent = new MotechEvent(EventKeys.CAMPAIGN_COMPLETED, params);
            eventRelay.sendEventMessage(endOfCampaignEvent);
        }
    }

    private void markEnrollmentAsComplete(String externalId, String campaignName) {
        CampaignEnrollment enrollment = allCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName);
        enrollment.setStatus(CampaignEnrollmentStatus.COMPLETED);
        allCampaignEnrollments.update(enrollment);
    }

    private Date nextFireTimeFor(String triggerKey) throws SchedulerException {
        return scheduler.getTrigger(triggerKey(triggerKey, "default")).getNextFireTime();
    }
}
