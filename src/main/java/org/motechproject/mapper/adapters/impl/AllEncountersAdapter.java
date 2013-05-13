package org.motechproject.mapper.adapters.impl;

import org.joda.time.DateTime;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSEncounterActivity;
import org.motechproject.mapper.domain.ObservationMapping;
import org.motechproject.mapper.util.CommcareMappingHelper;
import org.motechproject.mapper.util.IdentityResolver;
import org.motechproject.mapper.util.MRSUtil;
import org.motechproject.mapper.util.ObservationsGenerator;
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
    public AllEncountersAdapter(MRSUtil mrsUtil, IdentityResolver idResolver) {
        this.mrsUtil = mrsUtil;
        this.idResolver = idResolver;
    }

    @Override
    public void adaptForm(CommcareForm form, MRSActivity activity) {

        String startElementName = activity.getFormMapperProperties().getStartElement();

        FormValueElement rootElement = form.getForm();
        FormValueElement startElement = rootElement.getElementByName(startElementName);
        if (startElement == null) {
            logger.info("Cannot find the start node in the form: " + startElementName);
            return;
        }

        MRSEncounterActivity encounterActivity = (MRSEncounterActivity) activity;
        Map<String, String> patientIdScheme = encounterActivity.getPatientIdScheme();
        Map<String, String> facilityIdScheme = encounterActivity.getFacilityScheme();
        Map<String, String> providerIdScheme = encounterActivity.getProviderScheme();
        Map<String, String> encounterMappings = encounterActivity.getEncounterMappings();
        List<ObservationMapping> observationMappings = encounterActivity.getObservationMappings();

        for (CommcareMappingHelper mappingHelper : getAllMappingHelpers(form, activity)) {
            FormValueElement element = mappingHelper.getStartElement();
            String providerId = idResolver.retrieveId(providerIdScheme, form, element);
            String motechId = idResolver.retrieveId(patientIdScheme, form, element);

            MRSPatient patient = mrsUtil.getPatientByMotechId(motechId);
            if (patient == null) {
                logger.error("Patient " + motechId + " does not exist, failed to handle form " + form.getId());
                return;
            } else {
                logger.info("Adding encounter for patient: " + motechId);
            }

            DateTime dateReceived = DateTime.parse(form.getMetadata().get(FormMappingConstants.FORM_TIME_END));

            Set<MRSObservationDto> observations = ObservationsGenerator.generate(observationMappings, mappingHelper);

            String facilityName = getFacility(form, encounterActivity, element);

            mrsUtil.addEncounter(patient, observations, providerId, dateReceived, facilityName,
                    encounterActivity.getEncounterType());
        }
    }

    private String getFacility(CommcareForm form, MRSEncounterActivity encounterActivity, FormValueElement element) {
        String facilityNameField = null;

        Map<String, String> encounterMappings = encounterActivity.getEncounterMappings();
        if (encounterMappings != null) {
            facilityNameField = encounterMappings.get(FormMappingConstants.FACILITY_NAME_FIELD);
        }

        String facilityName = encounterActivity.getFacilityName();

        if (facilityNameField != null && facilityName == null) {
            FormValueElement facilityElement = element.getElementByName(facilityNameField);
            if (facilityElement != null) {
                facilityName = facilityElement.getValue();
            }
        }

        if (facilityName == null) {

            facilityName = idResolver.retrieveId(encounterActivity.getFacilityScheme(), form, element);
        }

        if (facilityName == null) {
            logger.warn("No facility name provided, using " + FormMappingConstants.DEFAULT_FACILITY);
            facilityName = FormMappingConstants.DEFAULT_FACILITY;
        }
        return facilityName;
    }
}

