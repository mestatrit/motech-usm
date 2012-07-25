package org.motechproject.openmrs.atomfeed.service.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.MotechException;
import org.motechproject.openmrs.atomfeed.OpenMrsHttpClient;
import org.motechproject.openmrs.atomfeed.builder.ConceptEvent;
import org.motechproject.openmrs.atomfeed.builder.EncounterEvent;
import org.motechproject.openmrs.atomfeed.builder.ObservationEvent;
import org.motechproject.openmrs.atomfeed.builder.PatientEvent;
import org.motechproject.openmrs.atomfeed.model.Entry;
import org.motechproject.openmrs.atomfeed.model.Feed;
import org.motechproject.openmrs.atomfeed.model.Link;
import org.motechproject.openmrs.atomfeed.repository.AtomFeedDao;
import org.motechproject.openmrs.atomfeed.service.AtomFeedService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.event.EventRelay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.XStream;

@Component("atomFeedService")
public class AtomFeedServiceImpl implements AtomFeedService {

    private static final int CHARACTERS_TO_TIMEZONE = 19;

    private static final Logger LOGGER = Logger.getLogger(AtomFeedServiceImpl.class);

    private final OpenMrsHttpClient client;
    private final XStream xstream;
    private final EventRelay eventRelay;
    private final AtomFeedDao atomFeedDao;

    @Autowired
    public AtomFeedServiceImpl(OpenMrsHttpClient client, EventRelay eventRelay, AtomFeedDao atomFeedDao) {
        this.client = client;
        this.eventRelay = eventRelay;
        this.atomFeedDao = atomFeedDao;

        xstream = new XStream();
        xstream.setClassLoader(getClass().getClassLoader());

        xstream.processAnnotations(Feed.class);
        xstream.processAnnotations(Entry.class);
        xstream.processAnnotations(Entry.Author.class);
        xstream.processAnnotations(Link.class);

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
            LOGGER.debug("No XML found from OpenMRS Atom Feed");
            return;
        }

        parseChanges(feed, lastId);
    }

    private void parseChanges(String feedXml, String lastId) {
        Feed feed = (Feed) xstream.fromXML(feedXml);
        List<Entry> entries = feed.getEntry();

        if (entries == null || entries.isEmpty()) {
            LOGGER.debug("No entries present");
            return;
        }

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
                    LOGGER.debug("Found a patient change");
                    event = handlePatientEntry(entry);
                } else if ("org.openmrs.Concept".equals(entry.getClassname())) {
                    LOGGER.debug("Found a concept change");
                    event = handleConceptEntry(entry);
                } else if ("org.openmrs.Encounter".equals(entry.getClassname())) {
                    LOGGER.debug("Found a encounter change");
                    event = handleEncounterEntry(entry);
                } else if ("org.openmrs.Obs".equals(entry.getClassname())) {
                    LOGGER.debug("Found a observation change");
                    event = handleObservationEntry(entry);
                }

                eventRelay.sendEventMessage(event);
                lastProcessedEntryUpdateTime = entry.getUpdated();
                lastProcessedId = entry.getId();
            }
        } catch (Exception e) {
            LOGGER.error("There was a problem processing an OpenMRS Atom Feed entry: " + e.getMessage());
            throw new MotechException("Problem processing an OpenMRS Atom Feed entry", e);
        } finally {
            if (StringUtils.isNotBlank(lastProcessedEntryUpdateTime)) {
                // the OpenMRS Atom Feed module does not currently recognize the time zone
                lastProcessedEntryUpdateTime = lastProcessedEntryUpdateTime.substring(0, CHARACTERS_TO_TIMEZONE);
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
        LOGGER.debug("Fetching OpenMRS Atom Feed since: " + sinceDateTime);

        String feed = null;

        if (StringUtils.isNotBlank(sinceDateTime)) {
            feed = client.getOpenMrsAtomFeedSinceDate(sinceDateTime);
        } else {
            feed = client.getOpenMrsAtomFeed();
        }

        parseFeed(feed, lastId);
    }
}
