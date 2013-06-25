package org.motechproject.mapper.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.util.EncounterIdGenerationStrategy;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ObservationMappingTest {
    @Mock
    private EncounterIdGenerationStrategy encounterIdGenerationStrategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldReturnMappedValue() {
        ObservationMapping mapping = new ObservationMapping();
        mapping.setConceptName("conceptName");
        mapping.setMissingValue("missingValue");
        mapping.setEmptyValue("emptyValue");
        mapping.setValues(new HashMap<String, String>(){{
            put("value1", "mappedValue1");
            put("value2", null);
            put("value4", "    ");
        }});

        when(encounterIdGenerationStrategy.generateObservationId("conceptName")).thenReturn("myObservationId");
        FormValueElement formNode = new FormValueElement();

        formNode.setValue("value1");

        Map<String,String> actualMappings = mapping.mapValue(formNode, encounterIdGenerationStrategy);

        assertEquals(1, actualMappings.size());
        assertEquals("mappedValue1", actualMappings.get("myObservationId"));

        formNode.setValue("value2");
        assertTrue(mapping.mapValue(formNode, encounterIdGenerationStrategy).isEmpty());

        formNode.setValue("value4");
        assertTrue(mapping.mapValue(formNode, encounterIdGenerationStrategy).isEmpty());
    }

    @Test
    public void shouldReturnFormValueElementValueWhenMappingsAreNull() {
        ObservationMapping mapping = new ObservationMapping();
        mapping.setConceptName("conceptName");

        when(encounterIdGenerationStrategy.generateObservationId("conceptName")).thenReturn("myObservationId");
        FormValueElement formNode = new FormValueElement();

        formNode.setValue("value1");

        Map<String,String> actualMappings = mapping.mapValue(formNode, encounterIdGenerationStrategy);

        assertEquals(1, actualMappings.size());
        assertEquals("value1", actualMappings.get("myObservationId"));
    }

    @Test
    public void shouldReturnMissingValueIfElementIsNull() {
        ObservationMapping mapping = new ObservationMapping();
        mapping.setConceptName("conceptName");
        mapping.setMissingValue("missingValue");
        mapping.setEmptyValue("emptyValue");
        mapping.setValues(new HashMap<String, String>(){{
            put("missingValue", "mappedMissingValue");
            put("nullValue", "mappedNullValue");
        }});

        when(encounterIdGenerationStrategy.generateObservationId("conceptName")).thenReturn("myObservationId");

        Map<String, String> actualMappings = mapping.mapValue(null, encounterIdGenerationStrategy);

        assertEquals(1, actualMappings.size());
        assertEquals("missingValue", actualMappings.get("myObservationId"));


        mapping.setMissingValue(null);

        assertTrue(mapping.mapValue(null, encounterIdGenerationStrategy).isEmpty());
    }

    @Test
    public void shouldReturnEmptyValueValueIfElementValueIsNull() {
        ObservationMapping mapping = new ObservationMapping();
        mapping.setConceptName("conceptName");
        mapping.setMissingValue("missingValue");
        mapping.setEmptyValue("emptyValue");
        mapping.setValues(new HashMap<String, String>(){{
            put("missingValue", "mappedMissingValue");
            put("emptyValue", "mappedNullValue");
        }});

        when(encounterIdGenerationStrategy.generateObservationId("conceptName")).thenReturn("myObservationId");

        Map<String, String> actualMappings = mapping.mapValue(new FormValueElement(), encounterIdGenerationStrategy);

        assertEquals(1, actualMappings.size());
        assertEquals("emptyValue", actualMappings.get("myObservationId"));

        mapping.setEmptyValue(null);

        assertTrue(mapping.mapValue(new FormValueElement(), encounterIdGenerationStrategy).isEmpty());
    }

    @Test
    public void shouldSplitTheValueAndReturnMappedValuesIfObservationIsOfTypeList() {
        ObservationMapping mapping = new ObservationMapping();
        mapping.setConceptName("conceptName");
        mapping.setMissingValue("missingValue");
        mapping.setEmptyValue("emptyValue");
        mapping.setValues(new HashMap<String, String>(){{
            put("value1", "mappedValue1");
            put("value2", null);
        }});

        mapping.setType(FormMappingConstants.LIST_TYPE);

        FormValueElement formNode = new FormValueElement();
        formNode.setValue("value1 value2 value3");

        when(encounterIdGenerationStrategy.generateObservationId("conceptName", 0)).thenReturn("myObservationId-0");
        when(encounterIdGenerationStrategy.generateObservationId("conceptName", 1)).thenReturn("myObservationId-1");
        when(encounterIdGenerationStrategy.generateObservationId("conceptName", 2)).thenReturn("myObservationId-2");

        Map<String, String> actualMappings = mapping.mapValue(formNode, encounterIdGenerationStrategy);

        assertEquals(2, actualMappings.size());
        assertEquals("mappedValue1", actualMappings.get("myObservationId-0"));
        assertEquals("value3", actualMappings.get("myObservationId-2"));
    }

    @Test
    public void shouldSplitMissingValueAndReturnMappedValuesIfObservationIsOfTypeListAndElementIsMissing() {
        ObservationMapping mapping = new ObservationMapping();
        mapping.setConceptName("conceptName");
        mapping.setMissingValue("missingValue value1");
        mapping.setEmptyValue("emptyValue");
        mapping.setValues(new HashMap<String, String>(){{
            put("value1", "mappedValue1");
            put("value2", null);
        }});

        mapping.setType(FormMappingConstants.LIST_TYPE);

        when(encounterIdGenerationStrategy.generateObservationId("conceptName", 0)).thenReturn("myObservationId-0");
        when(encounterIdGenerationStrategy.generateObservationId("conceptName", 1)).thenReturn("myObservationId-1");

        Map<String, String> actualMappings = mapping.mapValue(null, encounterIdGenerationStrategy);

        assertEquals(2, actualMappings.size());
        assertEquals("missingValue", actualMappings.get("myObservationId-0"));
        assertEquals("mappedValue1", actualMappings.get("myObservationId-1"));
    }

    @Test
    public void shouldSplitNullValueAndReturnMappedValuesIfObservationIsOfTypeListAndElementValueIsNull() {
        ObservationMapping mapping = new ObservationMapping();
        mapping.setConceptName("conceptName");
        mapping.setMissingValue("missingValue");
        mapping.setEmptyValue("emptyValue value1 value2");
        mapping.setValues(new HashMap<String, String>() {{
            put("value1", "mappedValue1");
            put("value2", null);
        }});

        mapping.setType(FormMappingConstants.LIST_TYPE);

        when(encounterIdGenerationStrategy.generateObservationId("conceptName", 0)).thenReturn("myObservationId-0");
        when(encounterIdGenerationStrategy.generateObservationId("conceptName", 1)).thenReturn("myObservationId-1");
        when(encounterIdGenerationStrategy.generateObservationId("conceptName", 2)).thenReturn("myObservationId-2");

        Map<String, String> actualMappings = mapping.mapValue(new FormValueElement(), encounterIdGenerationStrategy);

        assertEquals(2, actualMappings.size());
        assertEquals("emptyValue", actualMappings.get("myObservationId-0"));
        assertEquals("mappedValue1", actualMappings.get("myObservationId-1"));
    }
}
