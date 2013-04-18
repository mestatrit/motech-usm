package org.motechproject.mapper.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.adapters.mappings.ObservationMapping;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mrs.model.MRSObservationDto;

public final class ObservationsHelper {

    public static Set<MRSObservationDto> generateObservations(FormValueElement form, List<ObservationMapping> observationMappings) {
        Set<MRSObservationDto> observations = new HashSet<MRSObservationDto>();
        for (ObservationMapping obs : observationMappings) {
            String conceptId = obs.getConceptId();
            if (!StringUtils.isBlank(conceptId)) {
                List<FormValueElement> elements = form.getElementsByAttribute(FormMappingConstants.CONCEPT_ID_ATTRIBUTE, conceptId);
                if (elements.size() > 0) {
                    FormValueElement element = elements.get(0);
                    if (!StringUtils.isBlank(element.getValue())) {
                        observations.addAll(addObservations(obs, element));
                    }
                }
            } else {
                String elementName = obs.getElementName();
                if (elementName != null) {
                    List<FormValueElement> elements = form.getAllElementsByName(elementName);
                    FormValueElement element = null;
                    if (elements != null && elements.size() > 0) {
                        element = elements.get(0);
                    }
                    if (element != null && !StringUtils.isBlank(element.getValue())) {
                        observations.addAll(addObservations(obs, element));
                    }
                }
            }
        }
        return observations;
    }

    private static Collection<MRSObservationDto> addObservations(ObservationMapping obs, FormValueElement form) {
        Set<MRSObservationDto> observations = new HashSet<MRSObservationDto>();

        if (FormMappingConstants.LIST_TYPE.equals(obs.getType())) {
            observations.addAll(adaptList(obs, form));
        } else {
            Map<String, String> valueMappings = obs.getValues();
            String mappedValue = null;
            if (valueMappings != null) {
                mappedValue = valueMappings.get(form.getValue());
            }
            String conceptName = obs.getConceptName();
            MRSObservationDto observation;
            if (mappedValue != null) {
                observation = new MRSObservationDto(new Date(), conceptName, mappedValue);
            } else {
                observation = new MRSObservationDto(new Date(), conceptName, form.getValue());
            }
            observations.add(observation);
        }
        return observations;
    }

    private static Collection<MRSObservationDto> adaptList(ObservationMapping obs, FormValueElement form) {
        Set<MRSObservationDto> observations = new HashSet<MRSObservationDto>();

        String[] values = form.getValue().split(FormMappingConstants.LIST_DELIMITER);
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
