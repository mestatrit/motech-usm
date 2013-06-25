package org.motechproject.mapper.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.ObservationMapping;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.model.MRSObservationDto;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ObservationsGenerator {

    public static Set<MRSObservationDto> generate(List<ObservationMapping> observationMappings,
                                                  CommcareFormSegment beneficiarySegment, MRSPatient patient,
                                                  EncounterIdGenerationStrategy encounterIdGenerationStrategy, DateTime encounterDate) {
        Set<MRSObservationDto> observations = new HashSet<>();
        if (observationMappings == null) {
            return observations;
        }
        Date observationDate = encounterDate != null ? encounterDate.toDate() : null;
        for (ObservationMapping obs : observationMappings) {
            if (obs == null) {
                continue;
            }
            String conceptId = obs.getConceptId();
            if (!StringUtils.isBlank(conceptId)) {
                List<FormValueElement> elements = beneficiarySegment.getElementsByAttribute(FormMappingConstants.CONCEPT_ID_ATTRIBUTE, conceptId);
                FormValueElement element = elements.isEmpty() ? null : elements.get(0);
                observations.addAll(addObservations(obs, element, patient, encounterIdGenerationStrategy, observationDate));
            } else {
                String elementName = obs.getElementName();
                if (elementName == null) {
                    continue;
                }
                FormNode element = beneficiarySegment.search(elementName);
                observations.addAll(addObservations(obs, element, patient, encounterIdGenerationStrategy, observationDate));
            }
        }
        return observations;
    }

    private static Collection<MRSObservationDto> addObservations(ObservationMapping obs, FormNode element, MRSPatient patient, EncounterIdGenerationStrategy encounterIdGenerationStrategy, Date observationDate) {
        Map<String, String> observationValues = obs.mapValue(element, encounterIdGenerationStrategy);

        Set<MRSObservationDto> observations = new HashSet<>();

        for (Map.Entry<String, String> entry : observationValues.entrySet()) {
            observations.add(createObservation(obs, entry.getValue(), patient, entry.getKey(), observationDate));
        }

        return observations;
    }

    private static MRSObservationDto createObservation(ObservationMapping obs, String observationValue, MRSPatient patient, String observationId, Date observationDate) {
        String conceptName = obs.getConceptName();
        MRSObservationDto observation = new MRSObservationDto(observationDate, conceptName, patient.getMotechId(), observationValue);
        observation.setObservationId(observationId);
        return observation;
    }


}
