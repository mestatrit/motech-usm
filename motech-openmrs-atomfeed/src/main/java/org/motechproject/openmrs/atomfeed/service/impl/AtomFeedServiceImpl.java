package org.motechproject.openmrs.atomfeed.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.motechproject.openmrs.atomfeed.OpenMrsHttpClient;
import org.motechproject.openmrs.atomfeed.builder.ConceptEvent;
import org.motechproject.openmrs.atomfeed.builder.EncounterEvent;
import org.motechproject.openmrs.atomfeed.builder.ObservationEvent;
import org.motechproject.openmrs.atomfeed.builder.PatientEvent;
import org.motechproject.openmrs.atomfeed.model.Entry;
import org.motechproject.openmrs.atomfeed.model.Feed;
import org.motechproject.openmrs.atomfeed.repository.AtomFeedDao;
import org.motechproject.openmrs.atomfeed.service.AtomFeedService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.event.EventRelay;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;

@Component
public class AtomFeedServiceImpl implements AtomFeedService {

    private final OpenMrsHttpClient client;
    private final XStream xstream;
    private final EventRelay eventRelay;
    private final AtomFeedDao atomFeedDao;

    public AtomFeedServiceImpl(OpenMrsHttpClient client, EventRelay eventRelay, AtomFeedDao atomFeedDao) {
        this.client = client;
        this.eventRelay = eventRelay;
        this.atomFeedDao = atomFeedDao;
        xstream = new XStream();
        xstream.processAnnotations(Feed.class);
        xstream.omitField(Entry.class, "summary");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.motechproject.openmrs.atomfeed.AtomFeedClient#fetchNewOpenMrsEvents()
     */
    @Override
    public void fetchAllOpenMrsChanges() {
        String feed = client.getOpenMrsAtomFeed();
        parseFeed(feed, null);
    }

    private void parseFeed(String feed, String lastId) {
        if (StringUtils.isEmpty(feed)) {
            return;
        }

        parseChanges(feed, lastId);
    }

    private void parseChanges(String feedXml, String lastId) {
        Feed feed = (Feed) xstream.fromXML(feedXml);
        List<Entry> entries = feed.getEntry();

        // entries from the atom feed come in descending order
        // reversing puts them in ascending order so if there is
        // an exception that occurs during the processing of entries
        // the update time can be recovered during a later request
        String lastProcessedEntryUpdateTime = null;
        String lastProcessedId = null;
        Collections.reverse(entries);

        try {
            for (Entry entry : entries) {
                if (StringUtils.isNotBlank(lastId) && lastId.equals(entry.getId())) {
                    continue;
                }
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

                eventRelay.sendEventMessage(event);
                lastProcessedEntryUpdateTime = entry.getUpdated();
                lastProcessedId = entry.getId();
            }
        } catch (Exception e) {
            //
        } finally {
            if (StringUtils.isNotBlank(lastProcessedEntryUpdateTime)) {
                // the OpenMRS Atom Feed module does not currently recognize the time zone
                // an issue has been filed in the OpenMRS Issue tracker to address this
                lastProcessedEntryUpdateTime = lastProcessedEntryUpdateTime.substring(0, 19);
                atomFeedDao.setLastUpdateTime(lastProcessedId, lastProcessedEntryUpdateTime);
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
        String lastId = atomFeedDao.getLastId();
        fetchOpenMrsChangesSince(lastUpdateTime, lastId);
    }

    @Override
    public void fetchOpenMrsChangesSince(String sinceDateTime, String lastId) {
        String feed = null;

        if (StringUtils.isNotBlank(sinceDateTime)) {
            feed = client.getOpenMrsAtomFeedSinceDate(sinceDateTime);
        } else {
            feed = client.getOpenMrsAtomFeed();
        }

        parseFeed(feed, lastId);
    }
}
