package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.Minutes;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.OffsetCampaign;
import org.motechproject.server.messagecampaign.domain.message.OffsetCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.util.DateUtil;
import org.motechproject.valueobjects.WallTime;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.max;
import static org.motechproject.util.DateUtil.newDateTime;

public class OffsetProgramScheduler extends
        MessageCampaignScheduler<OffsetCampaignMessage, OffsetCampaign> {

    public OffsetProgramScheduler(MotechSchedulerService schedulerService,
            CampaignRequest enrollRequest, OffsetCampaign campaign,
            CampaignEnrollmentService campaignEnrollmentService) {
        super(schedulerService, enrollRequest, campaign,
                campaignEnrollmentService);
    }

    @Override
    protected void scheduleJobFor(OffsetCampaignMessage offsetCampaignMessage) {
        // Time reminderTime = campaignRequest.reminderTime();
        LocalDate jobDate = jobDate(referenceDate(),
                offsetCampaignMessage.timeOffset());
        Time theTime = new Time();
        LocalTime localTime = null;

        if (campaignRequest.startOffset() == null) { // Proceed like offset is 0
            localTime = jobTime(offsetCampaignMessage.timeOffset(), 1);
        } else {
            localTime = jobTime(offsetCampaignMessage.timeOffset(),
                    campaignRequest.startOffset());
        }
        if (localTime == null) {
            return;
        }
        theTime.setHour(localTime.getHourOfDay());
        theTime.setMinute(localTime.getMinuteOfHour());
        scheduleJobOn(theTime, jobDate,
                jobParams(offsetCampaignMessage.messageKey()));
    }

    private LocalDate jobDate(LocalDate referenceDate, String timeOffset) {
        WallTime wallTime = new WallTime(timeOffset);
        int offSetMinutes = wallTime.inMinutes();
        LocalTime time = new LocalTime();
        LocalTime newTime = time.plusMinutes(offSetMinutes);
        LocalDate tempDate = referenceDate.plus(Minutes.minutes(offSetMinutes));
        return referenceDate.plus(Minutes.minutes(offSetMinutes));
    }

    private LocalTime jobTime(String timeOffset, int minutes) {
        WallTime wallTime = new WallTime(timeOffset);
        int offSetMinutes = wallTime.inMinutes();
        if (offSetMinutes - minutes < 0) {
            return null;
        } else if (offSetMinutes - minutes == 0) { // explicitly set so that
                                                   // start date is not in the
                                                   // past
            LocalTime time = new LocalTime().plusMinutes(1);
            return time;
        } else {
            LocalTime time = new LocalTime();
            LocalTime newTime = time.plusMinutes(offSetMinutes - minutes);
            return newTime;
        }

    }

    @Override
    protected DateTime getCampaignEnd() {
        List<Integer> timeOffsets = new ArrayList<Integer>();
        for (OffsetCampaignMessage message : campaign.messages())
            timeOffsets.add(offsetInDays(message.timeOffset()));

        LocalDate campaignEndDate = campaignRequest.referenceDate().plusDays(
                max(timeOffsets));
        return newDateTime(campaignEndDate, campaignRequest.reminderTime());
    }

    @Override
    protected String getCampaignMessageSubject(
            OffsetCampaignMessage offsetCampaignMessage) {
        return EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT;
    }

    private boolean isInFuture(LocalDate date, Time time) {
        return DateUtil.newDateTime(date, time).isAfter(DateUtil.now());
    }

    private int offsetInDays(String timeOffset) {
        WallTime wallTime = new WallTime(timeOffset);
        return wallTime.inDays();
    }
}
