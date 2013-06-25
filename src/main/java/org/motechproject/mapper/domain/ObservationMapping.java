package org.motechproject.mapper.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.util.EncounterIdGenerationStrategy;

import java.util.HashMap;
import java.util.Map;

public class ObservationMapping {

    private String conceptId;
    private String elementName;
    private String conceptName;
    private String type;
    private Map<String, String> values;
    private String emptyValue;
    private String missingValue;

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getConceptId() {
        return conceptId;
    }

    public void setConceptId(String conceptId) {
        this.conceptId = conceptId;
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmptyValue() {
        return emptyValue;
    }

    public void setEmptyValue(String emptyValue) {
        this.emptyValue = emptyValue;
    }

    public String getMissingValue() {
        return missingValue;
    }

    public void setMissingValue(String missingValue) {
        this.missingValue = missingValue;
    }

    public Map<String, String> mapValue(FormNode formNode, EncounterIdGenerationStrategy encounterIdGenerationStrategy) {
        String value = getFormNodeValue(formNode);

        if(FormMappingConstants.LIST_TYPE.equals(type)) {
            return splitAndMapValues(value, encounterIdGenerationStrategy);
        }

        Map<String, String> observationValuesMap = new HashMap<>();

        if(formNode != null && formNode.getValue() != null) {
            value = map(value);
        }

        addToObservationValuesMap(observationValuesMap, encounterIdGenerationStrategy.generateObservationId(conceptName), value);
        return observationValuesMap;
    }

    private Map<String, String> splitAndMapValues(String value, EncounterIdGenerationStrategy encounterIdGenerationStrategy) {
        Map<String, String> observationValuesMap = new HashMap<>();
        if(StringUtils.isBlank(value)) {
            return observationValuesMap;
        }
        String[] values = value.split(FormMappingConstants.LIST_DELIMITER);

        for (int i = 0, valuesLength = values.length; i < valuesLength; i++) {
            String s = values[i];
            addToObservationValuesMap(observationValuesMap, encounterIdGenerationStrategy.generateObservationId(conceptName, i), map(s));
        }
        return observationValuesMap;
    }

    private String getFormNodeValue(FormNode formNode) {
        if(formNode == null) {
            return missingValue;
        }
        String value = formNode.getValue();
        return value == null ? emptyValue : value;
    }

    private void addToObservationValuesMap(Map<String, String> observationValuesMap, String observationId, String observationValue) {
        if(!StringUtils.isBlank(observationValue)) {
            observationValuesMap.put(observationId, observationValue);
        }
    }

    private String map(String value) {
        if(values != null && values.containsKey(value)) {
            return values.get(value);
        }

        return value;
    }
}
