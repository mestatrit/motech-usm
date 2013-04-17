package org.motechproject.mapper.util;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.joda.time.DateTime;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.domain.MRSUser;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSEncounterDto;
import org.motechproject.mrs.model.MRSObservationDto;
import org.motechproject.mrs.model.MRSPersonDto;
import org.motechproject.mrs.model.MRSProviderDto;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSUserAdapter;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MRSUtil {

    private static final String MAPPING_CONFIGURATION_FILE_NAME = "mappingConfiguration.properties";
    private static final String DESTINATION = "destination";

    private Logger logger = LoggerFactory.getLogger("commcare-openmrs-mapper");

    @Autowired
    private MRSEncounterAdapter mrsEncounterAdapter;

    @Autowired
    private MRSFacilityAdapter mrsFacilityAdapter;

    @Autowired
    private MRSUserAdapter mrsUserAdapter;

    @Autowired
    private MRSPatientAdapter mrsPatientAdapter;

    @Autowired
    private SettingsFacade settings;

    public MRSPerson findProvider(String providerName) {
        //CouchDB module does not have a user service yet
        String destination = settings.getProperties(MAPPING_CONFIGURATION_FILE_NAME).getProperty(DESTINATION);

        if (FormMappingConstants.DESTINATION_COUCHDB.equals(destination)) {
            return null;
        }

        MRSUser provider = mrsUserAdapter.getUserByUserName(providerName);

        if (provider == null) {
            return null;
        }

        return provider.getPerson();
    }

    public MRSFacility findFacility(String location) {
        List<? extends MRSFacility> facilities = null;
        try {
            facilities = mrsFacilityAdapter.getFacilities(location);
        } catch (MRSException e) {
            return null;
        }
        if (facilities.size() == 0) {
            return null;
        } else if (facilities.size() > 1) {
            logger.info("Multiple facilities, returning facility with ID: " + facilities.get(0).getFacilityId());
        }

        return facilities.get(0);
    }
    
    public MRSPatient getPatientByMotechId(String motechId) {
        return mrsPatientAdapter.getPatientByMotechId(motechId);
    }

    public void addEncounter(MRSPatient patient, Set<MRSObservationDto> observations, String providerName,
            DateTime encounterDate, String facilityName, String encounterType) {

        MRSFacility facility = findFacility(facilityName);

        MRSProviderDto provider = new MRSProviderDto();

        MRSPerson providerPerson = findProvider(providerName);

        if (providerPerson == null) {
            providerPerson = new MRSPersonDto();
            providerPerson.setPersonId("UnknownProvider");
        }

        provider.setPerson(providerPerson);
        provider.setProviderId(providerPerson.getPersonId());

        logger.info("Using provider: " + provider);

        MRSEncounter mrsEncounter = new MRSEncounterDto();
        mrsEncounter.setFacility(facility);
        mrsEncounter.setDate(encounterDate);
        mrsEncounter.setPatient(patient);
        mrsEncounter.setProvider(provider);
        mrsEncounter.setEncounterType(encounterType);
        mrsEncounter.setObservations(observations);
        mrsEncounter.setEncounterId(UUID.randomUUID().toString());

        try {
            mrsEncounterAdapter.createEncounter(mrsEncounter);
            logger.info("Encounter saved");
        } catch (MRSException e) {
            logger.warn("Could not save encounter");
        }
    }
}
