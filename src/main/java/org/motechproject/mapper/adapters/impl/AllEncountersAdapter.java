package org.motechproject.mapper.adapters.impl;

import org.joda.time.DateTime;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSEncounterActivity;
import org.motechproject.mapper.domain.ObservationMapping;
import org.motechproject.mapper.util.*;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.model.MRSObservationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class AllEncountersAdapter extends ActivityFormAdapter {

    private static Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");
    private MRSUtil mrsUtil;
    private IdentityResolver idResolver;

    @Autowired
    public AllEncountersAdapter(MRSUtil mrsUtil, IdentityResolver idResolver, AllElementSearchStrategies allElementSearchStrategies) {
        super(allElementSearchStrategies);
        this.mrsUtil = mrsUtil;
        this.idResolver = idResolver;
    }

    @Override
    public void adaptForm(CommcareForm form, MRSActivity activity) {

        MRSEncounterActivity encounterActivity = (MRSEncounterActivity) activity;
        Map<String, String> patientIdScheme = encounterActivity.getPatientIdScheme();
        Map<String, String> providerIdScheme = encounterActivity.getProviderScheme();
        List<ObservationMapping> observationMappings = encounterActivity.getObservationMappings();

        for (CommcareFormSegment beneficiarySegment : getAllBeneficiarySegments(form, activity)) {
            String providerId = idResolver.retrieveId(providerIdScheme, beneficiarySegment);
            String motechId = idResolver.retrieveId(patientIdScheme, beneficiarySegment);

            MRSPatient patient = mrsUtil.getPatientByMotechId(motechId);
            if (patient == null) {
                logger.error(String.format("Patient(%s) does not exist, failed to handle form", form.getId()));
                return;
            } else {
                logger.info(String.format("Adding encounter for patient(%s)", motechId));
            }
            DateTime dateReceived = DateTime.parse(form.getMetadata().get(FormMappingConstants.FORM_TIME_END));
            Set<MRSObservationDto> observations = ObservationsGenerator.generate(observationMappings, beneficiarySegment, patient);
            String facilityName = getFacility(encounterActivity, beneficiarySegment);
            mrsUtil.addEncounter(patient, observations, providerId, dateReceived, facilityName,
                    encounterActivity.getEncounterType());
        }
    }

    private String getFacility(MRSEncounterActivity encounterActivity, CommcareFormSegment beneficiarySegment) {
        String facilityNameField = null;
        String facilityName = encounterActivity.getFacilityName();
        Map<String, String> encounterMappings = encounterActivity.getEncounterMappings();
        if (encounterMappings != null) {
            facilityNameField = encounterMappings.get(FormMappingConstants.FACILITY_NAME_FIELD);
        }
        if (facilityNameField != null && facilityName == null) {
            FormNode facilityElement = beneficiarySegment.search(facilityNameField);
            if (facilityElement != null) {
                facilityName = facilityElement.getValue();
            }
        }
        if (facilityName == null) {
            facilityName = idResolver.retrieveId(encounterActivity.getFacilityScheme(), beneficiarySegment);
        }

        if (facilityName == null) {
            logger.warn("No facility name provided, using " + FormMappingConstants.DEFAULT_FACILITY);
            facilityName = FormMappingConstants.DEFAULT_FACILITY;
        }
        return facilityName;
    }
}

