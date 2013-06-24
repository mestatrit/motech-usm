package org.motechproject.mapper.util;

import org.junit.Before;
import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.builder.FormBuilder;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.ObservationMapping;
import org.motechproject.mrs.model.MRSObservationDto;
import org.motechproject.mrs.model.MRSPatientDto;

import java.util.*;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;
import static org.mockito.MockitoAnnotations.initMocks;


public class ObservationsGeneratorTest {

    @Mock
    private EncounterIdGenerationStrategy encounterIdGenerationStrategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldAddPatientId() {
        String patientId = "motech Id";
        MRSPatientDto patient = new MRSPatientDto();
        patient.setMotechId(patientId);
        List<ObservationMapping> observationMappings = new ArrayList<>();
        ObservationMapping observationMapping = new ObservationMapping();
        String conceptName = "Age of Patient";
        String fieldName = "age";
        DateTime encounterDate = DateTime.now();
        observationMapping.setConceptName(conceptName);
        observationMapping.setElementName(fieldName);
        observationMappings.add(observationMapping);
        FormValueElement element = new FormValueElement();
        element.setElementName(fieldName);
        String value = "21";
        element.setValue(value);
        CommcareForm form = new FormBuilder("form").with(fieldName, element).getForm();
        CommcareFormSegment beneficiarySegment = new CommcareFormSegment(form, form.getForm(),
                new ArrayList<String>(), new AllElementSearchStrategies());
        when(encounterIdGenerationStrategy.generateConceptId(any(String.class))).thenReturn("observationId-concept");

        Set<MRSObservationDto> observations = ObservationsGenerator.generate(observationMappings, beneficiarySegment, patient, encounterIdGenerationStrategy, encounterDate);

        MRSObservationDto actualObservation = observations.iterator().next();
        assertEquals(1, observations.size());
        assertEquals(conceptName, actualObservation.getConceptName());
        assertEquals(value, actualObservation.getValue());
        assertEquals(patientId, actualObservation.getPatientId());
        assertEquals("observationId-concept", actualObservation.getObservationId());
        assertEquals(encounterDate, actualObservation.getDate());
    }

    @Test
    public void shouldAddMultipleObservationsForListType() {
        String patientId = "motech Id";
        MRSPatientDto patient = new MRSPatientDto();
        patient.setMotechId(patientId);
        List<ObservationMapping> observationMappings = new ArrayList<>();
        ObservationMapping observationMapping = new ObservationMapping();
        String conceptName = "Child Names";
        DateTime encounterDate = DateTime.now();
        String fieldName = "name";
        observationMapping.setConceptName(conceptName);
        observationMapping.setElementName(fieldName);
        observationMapping.setType(FormMappingConstants.LIST_TYPE);
        observationMappings.add(observationMapping);
        FormValueElement element = new FormValueElement();
        element.setElementName(fieldName);
        String value = "raj taj";
        element.setValue(value);
        CommcareForm form = new FormBuilder("form").with(fieldName, element).getForm();
        CommcareFormSegment beneficiarySegment = new CommcareFormSegment(form, form.getForm(), new ArrayList<String>(), new AllElementSearchStrategies());

        when(encounterIdGenerationStrategy.generateConceptId("Child Names", 0)).thenReturn("observationId-concept-0");
        when(encounterIdGenerationStrategy.generateConceptId("Child Names", 1)).thenReturn("observationId-concept-1");

        Set<MRSObservationDto> observationSet = ObservationsGenerator.generate(observationMappings, beneficiarySegment, patient, encounterIdGenerationStrategy, encounterDate);

        assertEquals(2, observationSet.size());
        Iterator<MRSObservationDto> observations = observationSet.iterator();

        MRSObservationDto observation1 = observations.next();
        assertEquals(conceptName, observation1.getConceptName());
        assertTrue(value.contains(observation1.getValue().toString()));
        assertEquals(patientId, observation1.getPatientId());
        assertEquals(encounterDate, observation1.getDate());
        MRSObservationDto observation2 = observations.next();
        assertEquals(conceptName, observation2.getConceptName());
        assertTrue(value.contains(observation2.getValue().toString()));
        assertEquals(patientId, observation2.getPatientId());
        assertTrue(asList(observation1.getObservationId(), observation2.getObservationId()).contains("observationId-concept-0"));
        assertTrue(asList(observation1.getObservationId(), observation2.getObservationId()).contains("observationId-concept-1"));
        assertEquals(encounterDate, observation2.getDate());
    }

    @Test
    public void shouldSaveObservationDateAsNullIfEncounterDateIsNull() {
        ObservationMapping observationMapping = new ObservationMapping();
        String fieldName = "name";
        observationMapping.setElementName(fieldName);
        FormValueElement element = new FormValueElement();
        element.setElementName(fieldName);
        element.setValue("value");
        CommcareForm form = new FormBuilder("form").with(fieldName, element).getForm();
        CommcareFormSegment beneficiarySegment = new CommcareFormSegment(form, form.getForm(), new ArrayList<String>(), new AllElementSearchStrategies());

        Set<MRSObservationDto> actualObservations = ObservationsGenerator.generate(Arrays.asList(observationMapping), beneficiarySegment, new MRSPatientDto(), encounterIdGenerationStrategy, null);

        assertEquals(1, actualObservations.size());
        assertNull(actualObservations.iterator().next().getDate());
    }
}
