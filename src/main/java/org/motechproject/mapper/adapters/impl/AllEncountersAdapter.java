package org.motechproject.mapper.adapters.impl;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.joda.time.DateTime;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSEncounterActivity;
import org.motechproject.mapper.util.IdentityResolver;
import org.motechproject.mapper.util.MRSUtil;
import org.motechproject.mapper.util.ObservationsHelper;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.model.MRSObservationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class AllEncountersAdapter extends ActivityFormAdapter {

    private static Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");
    @Autowired
    private MRSUtil mrsUtil;
    @Autowired
    private IdentityResolver idResolver;

    @Override
    public void adaptForm(CommcareForm form, MRSActivity activity) {

        String startElement = activity.getFormMapperProperties().getStartElement();
        Multimap<String, FormValueElement> rootElementMap = new LinkedHashMultimap<>();

        FormValueElement rootElement = form.getForm().getElementByName(startElement);
        if (rootElement == null) {
            logger.info("Cannot find the start node in the form: " + startElement);
            return;
        }
        if (activity.getFormMapperProperties().getMultiple()) {
            rootElementMap.putAll(rootElement.getSubElements());
        } else {
            rootElementMap.put(startElement, rootElement);
        }

        for (Map.Entry<String, FormValueElement> topFormElement : rootElementMap.entries()) {
            FormValueElement element = topFormElement.getValue();
            MRSEncounterActivity encounterActivity = (MRSEncounterActivity) activity;

            Map<String, String> patientIdScheme = encounterActivity.getPatientIdScheme();
            Map<String, String> facilityIdScheme = encounterActivity.getFacilityScheme();
            Map<String, String> providerIdScheme = encounterActivity.getProviderScheme();

            Map<String, String> encounterMappings = encounterActivity.getEncounterMappings();

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

            Set<MRSObservationDto> observations = ObservationsHelper.generateObservations(form.getForm(),
                    encounterActivity.getObservationMappings());

            String facilityNameField = null;

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
                facilityName = idResolver.retrieveId(facilityIdScheme, form, element);
            }

            if (facilityName == null) {
                logger.warn("No facility name provided, using " + FormMappingConstants.DEFAULT_FACILITY);
                facilityName = FormMappingConstants.DEFAULT_FACILITY;
            }

            mrsUtil.addEncounter(patient, observations, providerId, dateReceived, facilityName,
                    encounterActivity.getEncounterType());
        }
    }
}

