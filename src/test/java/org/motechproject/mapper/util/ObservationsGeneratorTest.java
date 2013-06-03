package org.motechproject.mapper.util;

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

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ObservationsGeneratorTest {

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
        CommcareFormBeneficiarySegment beneficiarySegment = new CommcareFormBeneficiarySegment(form, form.getForm(), new ArrayList<String>());

        Set<MRSObservationDto> observations = ObservationsGenerator.generate(observationMappings, beneficiarySegment, patient);

        assertEquals(1, observations.size());
        MRSObservationDto actualObservation = observations.iterator().next();
        assertEquals(conceptName, actualObservation.getConceptName());
        assertEquals(value, actualObservation.getValue());
        assertEquals(patientId, actualObservation.getPatientId());
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
        CommcareFormBeneficiarySegment beneficiarySegment = new CommcareFormBeneficiarySegment(form, form.getForm(), new ArrayList<String>());

        Set<MRSObservationDto> observationSet = ObservationsGenerator.generate(observationMappings, beneficiarySegment, patient);

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
    }
}
