package org.motechproject.mapper.util;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.ObservationMapping;
import org.motechproject.mrs.model.MRSObservationDto;

import java.util.*;

public final class ObservationsGenerator {

    public static Set<MRSObservationDto> generate(List<ObservationMapping> observationMappings, CommcareMappingHelper mappingHelper) {
        Set<MRSObservationDto> observations = new HashSet<MRSObservationDto>();
        for (ObservationMapping obs : observationMappings) {
            String conceptId = obs.getConceptId();
            if (!StringUtils.isBlank(conceptId)) {
                List<FormValueElement> elements = mappingHelper.getStartElement().getElementsByAttribute(FormMappingConstants.CONCEPT_ID_ATTRIBUTE, conceptId);
                if (elements.size() > 0) {
                    FormValueElement element = elements.get(0);
                    if (!StringUtils.isBlank(element.getValue())) {
                        observations.addAll(addObservations(obs, element));
                    }
                }
            } else {
                String elementName = obs.getElementName();
                if (elementName != null) {
                    FormValueElement element = mappingHelper.search(elementName);
                    if (element != null && !StringUtils.isBlank(element.getValue())) {
                        observations.addAll(addObservations(obs, element));
                    }
                }
            }
        }
        return observations;
    }

    private static Collection<MRSObservationDto> addObservations(ObservationMapping obs, FormValueElement element) {
        Set<MRSObservationDto> observations = new HashSet<MRSObservationDto>();

        if (FormMappingConstants.LIST_TYPE.equals(obs.getType())) {
            observations.addAll(adaptList(obs, element));
        } else {
            Map<String, String> valueMappings = obs.getValues();
            String mappedValue = null;
            if (valueMappings != null) {
                mappedValue = valueMappings.get(element.getValue());
            }
            String conceptName = obs.getConceptName();
            MRSObservationDto observation;
            if (mappedValue != null) {
                observation = new MRSObservationDto(new Date(), conceptName, mappedValue);
            } else {
                observation = new MRSObservationDto(new Date(), conceptName, element.getValue());
            }
            observations.add(observation);
        }
        return observations;
    }

    private static Collection<MRSObservationDto> adaptList(ObservationMapping obs, FormValueElement element) {
        Set<MRSObservationDto> observations = new HashSet<MRSObservationDto>();

        String[] values = element.getValue().split(FormMappingConstants.LIST_DELIMITER);
        Map<String, String> valueMappings = obs.getValues();
        String conceptName = obs.getConceptName();

        for (String value : values) {
            String mappedValue = null;

            if (valueMappings != null) {
                mappedValue = valueMappings.get(value);
            }
            MRSObservationDto observation;
            if (mappedValue != null) {
                observation = new MRSObservationDto(new Date(), conceptName, mappedValue);
            } else {
                observation = new MRSObservationDto(new Date(), conceptName, value);
            }
            observations.add(observation);
        }

        return observations;
    }
}
