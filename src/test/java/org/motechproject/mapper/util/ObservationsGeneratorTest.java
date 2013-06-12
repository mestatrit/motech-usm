package org.motechproject.mapper.util;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.builder.FormBuilder;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.ObservationMapping;
import org.motechproject.mrs.model.MRSObservationDto;
import org.motechproject.mrs.model.MRSPatientDto;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;
import static org.mockito.MockitoAnnotations.initMocks;


public class ObservationsGeneratorTest {

    @Mock
    private ObservationIdGenerationStrategy observationIdGenerationStrategy;

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
        when(observationIdGenerationStrategy.generate(any(String.class))).thenReturn("observationId-concept");

        Set<MRSObservationDto> observations = ObservationsGenerator.generate(observationMappings, beneficiarySegment, patient, observationIdGenerationStrategy);

        MRSObservationDto actualObservation = observations.iterator().next();
        assertEquals(1, observations.size());
        assertEquals(conceptName, actualObservation.getConceptName());
        assertEquals(value, actualObservation.getValue());
        assertEquals(patientId, actualObservation.getPatientId());
        assertEquals("observationId-concept", actualObservation.getObservationId());
    }

    @Test
    public void shouldAddMultipleObservationsForListType() {
        String patientId = "motech Id";
        MRSPatientDto patient = new MRSPatientDto();
        patient.setMotechId(patientId);
        List<ObservationMapping> observationMappings = new ArrayList<>();
        ObservationMapping observationMapping = new ObservationMapping();
        String conceptName = "Child Names";
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

        when(observationIdGenerationStrategy.generate("Child Names", 0)).thenReturn("observationId-concept-0");
        when(observationIdGenerationStrategy.generate("Child Names", 1)).thenReturn("observationId-concept-1");

        Set<MRSObservationDto> observationSet = ObservationsGenerator.generate(observationMappings, beneficiarySegment, patient, observationIdGenerationStrategy);

        assertEquals(2, observationSet.size());
        Iterator<MRSObservationDto> observations = observationSet.iterator();

        MRSObservationDto observation1 = observations.next();
        assertEquals(conceptName, observation1.getConceptName());
        assertTrue(value.contains(observation1.getValue().toString()));
        assertEquals(patientId, observation1.getPatientId());

        MRSObservationDto observation2 = observations.next();
        assertEquals(conceptName, observation2.getConceptName());
        assertTrue(value.contains(observation2.getValue().toString()));
        assertEquals(patientId, observation2.getPatientId());

        assertTrue(asList(observation1.getObservationId(), observation2.getObservationId()).contains("observationId-concept-0"));
        assertTrue(asList(observation1.getObservationId(), observation2.getObservationId()).contains("observationId-concept-1"));
    }
}
