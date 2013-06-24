package org.motechproject.mapper.util;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.ObservationMapping;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.model.MRSObservationDto;

import java.util.*;

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
                if (elements.size() > 0) {
                    FormValueElement element = elements.get(0);
                    if (!StringUtils.isBlank(element.getValue())) {
                        observations.addAll(addObservations(obs, element, patient, encounterIdGenerationStrategy, observationDate));
                    }
                }
            } else {
                String elementName = obs.getElementName();
                if (elementName != null) {
                    FormNode element = beneficiarySegment.search(elementName);
                    if (element != null && !StringUtils.isBlank(element.getValue())) {
                        observations.addAll(addObservations(obs, element, patient, encounterIdGenerationStrategy, observationDate));
                    }
                }
            }
        }
        return observations;
    }

    private static Collection<MRSObservationDto> addObservations(ObservationMapping obs, FormNode element, MRSPatient patient, EncounterIdGenerationStrategy encounterIdGenerationStrategy, Date observationDate) {
        Set<MRSObservationDto> observations = new HashSet<>();

        if (FormMappingConstants.LIST_TYPE.equals(obs.getType())) {
            observations.addAll(adaptList(obs, element, patient, encounterIdGenerationStrategy, observationDate));
        } else {
            Map<String, String> valueMappings = obs.getValues();
            String mappedValue = null;
            if (valueMappings != null) {
                mappedValue = valueMappings.get(element.getValue());
            }
            String conceptName = obs.getConceptName();
            MRSObservationDto observation;
            if (mappedValue != null) {
                observation = new MRSObservationDto(observationDate, conceptName, patient.getMotechId(), mappedValue);
            } else {
                observation = new MRSObservationDto(observationDate, conceptName, patient.getMotechId(), element.getValue());
            }
            observation.setObservationId(encounterIdGenerationStrategy.generateConceptId(conceptName));
            observations.add(observation);
        }
        return observations;
    }

    private static Collection<MRSObservationDto> adaptList(ObservationMapping obs, FormNode element, MRSPatient patient,
                                                           EncounterIdGenerationStrategy encounterIdGenerationStrategy, Date observationDate) {
        Set<MRSObservationDto> observations = new HashSet<>();

        String[] values = element.getValue().split(FormMappingConstants.LIST_DELIMITER);
        Map<String, String> valueMappings = obs.getValues();
        String conceptName = obs.getConceptName();

        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            String value = values[i];
            String mappedValue = null;

            if (valueMappings != null) {
                mappedValue = valueMappings.get(value);
            }
            MRSObservationDto observation;
            if (mappedValue != null) {
                observation = new MRSObservationDto(observationDate, conceptName, patient.getMotechId(), mappedValue);
            } else {
                observation = new MRSObservationDto(observationDate, conceptName, patient.getMotechId(), value);
            }
            observation.setObservationId(encounterIdGenerationStrategy.generateConceptId(conceptName, i));
            observations.add(observation);
        }

        return observations;
    }
}
