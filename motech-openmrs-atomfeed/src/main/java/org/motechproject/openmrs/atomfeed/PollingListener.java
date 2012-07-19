package org.motechproject.openmrs.atomfeed;

import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.motechproject.openmrs.atomfeed.service.AtomFeedService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Listens for the {@link EventSubjects#POLLING_SUBJECT}, then invokes the
 * {@link AtomFeedService#fetchNewOpenMrsEvents()} to retrieve latest events
 * from the OpenMRS
 */
public class PollingListener {
    
    private final AtomFeedService atomFeedService;
    
    @Autowired
    public PollingListener(AtomFeedService atomFeedService) {
        this.atomFeedService = atomFeedService;
    }

    @MotechListener(subjects = { EventSubjects.POLLING_SUBJECT })
    public void onPollingEvent(MotechEvent event) {
        atomFeedService.fetchNewOpenMrsEvents(true);
    }
}
