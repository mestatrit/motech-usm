package org.motechproject.openmrs.atomfeed.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.motechproject.openmrs.atomfeed.AtomFeedDao;
import org.motechproject.openmrs.atomfeed.ConceptEvent;
import org.motechproject.openmrs.atomfeed.OpenMrsHttpClient;
import org.motechproject.openmrs.atomfeed.PatientEvent;
import org.motechproject.openmrs.atomfeed.model.Entry;
import org.motechproject.openmrs.atomfeed.model.Feed;
import org.motechproject.openmrs.atomfeed.service.AtomFeedService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;

@Component
public class AtomFeedServiceImpl implements AtomFeedService {

    private final OpenMrsHttpClient client;
    private final XStream xstream;
    private final OutboundEventGateway outboundGateway;
    private final AtomFeedDao atomFeedDao;

    public AtomFeedServiceImpl(OpenMrsHttpClient client, OutboundEventGateway outboundGateway, AtomFeedDao atomFeedDao) {
        this.client = client;
        this.outboundGateway = outboundGateway;
        this.atomFeedDao = atomFeedDao;
        xstream = new XStream();
        xstream.processAnnotations(Feed.class);
        xstream.omitField(Entry.class, "summary");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.motechproject.openmrs.atomfeed.AtomFeedClient#fetchNewOpenMrsEvents()
     */
    @Override
    public void fetchNewOpenMrsEvents(boolean sinceLastUpdate) {
        String feed = client.getOpenMrsAtomFeed();
        parseFeed(feed);
    }

    private void parseFeed(String feed) {
        if (StringUtils.isEmpty(feed)) {
            return;
        }

        parseChanges(feed);
    }

    private void parseChanges(String feedXml) {
        Feed feed = (Feed) xstream.fromXML(feedXml);
        List<Entry> entries = feed.getEntry();

        // entries from the atom feed come in descending order
        // reversing puts them in ascending order so if there is
        // an exception that occurs during the processing of entries
        // the update time can be recovered during a later request
        String lastProcessedEntryUpdateTime = null;
        Collections.reverse(entries);

        try {
            for (Entry entry : entries) {
                MotechEvent event = null;
                if ("org.openmrs.Patient".equals(entry.getClassname())) {
                    event = handlePatientEntry(entry);
                } else if ("org.openmrs.Concept".equals(entry.getClassname())) {
                    event = handleConceptEntry(entry);
                } else if ("org.openmrs.Encounter".equals(entry.getClassname())) {
                    event = handleEncounterEntry(entry);
                } else if ("org.openmrs.Obs".equals(entry.getClassname())) {
                    event = handleObservationEntry(entry);
                }

                outboundGateway.sendEventMessage(event);
                lastProcessedEntryUpdateTime = entry.getUpdated();
            }
        } catch (Exception e) {
            //
        } finally {
            if (StringUtils.isNotBlank(lastProcessedEntryUpdateTime)) {
                atomFeedDao.setLastUpdateTime(lastProcessedEntryUpdateTime);
            }
        }
    }

    private MotechEvent handlePatientEntry(Entry entry) {
        return new PatientEvent(entry).toEvent();
    }

    private MotechEvent handleConceptEntry(Entry entry) {
        return new ConceptEvent(entry).toEvent();
    }

    private MotechEvent handleEncounterEntry(Entry entry) {
        return new EncounterEvent(entry).toEvent();
    }

    private MotechEvent handleObservationEntry(Entry entry) {
        return new ObservationEvent(entry).toEvent();
    }

    @Override
    public void fetchOpenMrsChangesSinceLastUpdate() {
        String lastUpdateTime = atomFeedDao.getLastUpdateTime();
        String feed = null;
        
        if (StringUtils.isNotEmpty(lastUpdateTime)) {
            feed = client.getOpenMrsAtomFeedSinceDate(lastUpdateTime);
        } else {
            feed = client.getOpenMrsAtomFeed();
        }
        
        parseFeed(feed);
    }
}
