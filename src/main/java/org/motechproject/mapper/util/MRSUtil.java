package org.motechproject.mapper.util;

import org.joda.time.DateTime;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.validation.ValidationError;
import org.motechproject.mapper.validation.ValidationManager;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSProvider;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSEncounterDto;
import org.motechproject.mrs.model.MRSObservationDto;
import org.motechproject.mrs.model.MRSPersonDto;
import org.motechproject.mrs.model.MRSProviderDto;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.motechproject.mrs.services.MRSProviderAdapter;
import org.motechproject.server.config.SettingsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class MRSUtil {

    private Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");
    private MRSEncounterAdapter mrsEncounterAdapter;
    private MRSFacilityAdapter mrsFacilityAdapter;
    private MRSProviderAdapter mrsProviderAdapter;
    private MRSPatientAdapter mrsPatientAdapter;
    private ValidationManager validator;
    private SettingsFacade settings;

    @Autowired
    public MRSUtil(MRSEncounterAdapter mrsEncounterAdapter, MRSFacilityAdapter mrsFacilityAdapter, MRSProviderAdapter mrsProviderAdapter,
                   MRSPatientAdapter mrsPatientAdapter, ValidationManager validator, @Qualifier("commcareMapperSettings") SettingsFacade settings) {
        this.mrsEncounterAdapter = mrsEncounterAdapter;
        this.mrsFacilityAdapter = mrsFacilityAdapter;
        this.mrsProviderAdapter = mrsProviderAdapter;
        this.mrsPatientAdapter = mrsPatientAdapter;
        this.validator = validator;
        this.settings = settings;
    }

    public MRSProvider findProviderOrCreate(String providerId) {
        if (providerId == null) return null;
        String destination = settings.getProperties(FormMappingConstants.MAPPING_CONFIGURATION_FILE_NAME).getProperty(FormMappingConstants.DESTINATION);
        if (FormMappingConstants.DESTINATION_COUCHDB.equals(destination)) {
            MRSProvider provider = mrsProviderAdapter.getProviderByProviderId(providerId);
            if (provider == null) {
                MRSPersonDto person = new MRSPersonDto();
                person.setPersonId(providerId);
                MRSProviderDto newProvider = new MRSProviderDto(providerId, person);
                mrsProviderAdapter.saveProvider(newProvider);
                logger.info("New Provider saved: " + newProvider.getProviderId());
                return newProvider;
            } else {
                return provider;
            }
        }
        return null;
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

    public void addEncounter(MRSPatient patient, Set<MRSObservationDto> observations, String providerId,
                             DateTime encounterDate, String facilityName, String encounterType) {

        MRSFacility facility = findFacility(facilityName);
        MRSProvider provider = findProviderOrCreate(providerId);
        MRSEncounter mrsEncounter = new MRSEncounterDto();
        mrsEncounter.setFacility(facility);
        mrsEncounter.setDate(encounterDate);
        mrsEncounter.setPatient(patient);
        mrsEncounter.setProvider(provider);
        mrsEncounter.setEncounterType(encounterType);
        mrsEncounter.setObservations(observations);
        mrsEncounter.setEncounterId(UUID.randomUUID().toString());
        List<ValidationError> validationErrors = validator.validateEncounter(mrsEncounter);
        if (validationErrors.size() != 0) {
            logger.info("Unable to save encounter due to validation errors");
            return;
        }
        try {
            mrsEncounterAdapter.createEncounter(mrsEncounter);
            logger.info(String.format("Encounter(%s) saved by %s", mrsEncounter.getEncounterId(), providerId));
        } catch (MRSException e) {
            logger.warn("Could not save encounter");
        }
    }
}
