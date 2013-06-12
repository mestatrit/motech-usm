package org.motechproject.mapper.adapters.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.mapper.builder.EncounterActivityBuilder;
import org.motechproject.mapper.builder.FormBuilder;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.FormMapperProperties;
import org.motechproject.mapper.domain.MRSEncounterActivity;
import org.motechproject.mapper.domain.ObservationMapping;
import org.motechproject.mapper.util.*;
import org.motechproject.mapper.validation.ValidationManager;
import org.motechproject.mrs.model.MRSObservationDto;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.services.MRSPatientAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class AllEncountersAdapterTest {

    @Mock
    private MRSUtil mrsUtil;
    @Mock
    private MRSPatientAdapter mrsPatientAdapter;
    @Mock
    private IdentityResolver idResolver;
    @Mock
    private ValidationManager validator;
    private AllEncountersAdapter encountersAdapter;
    @Captor
    private ArgumentCaptor<Set<MRSObservationDto>> observationCaptor;

    @Mock
    private ObservationIdGenerationStrategy observationIdGenerationStrategy;

    @Before
    public void setUp() {
        initMocks(this);
        encountersAdapter = new AllEncountersAdapter(mrsUtil, idResolver,new AllElementSearchStrategies());
    }

    @Test
    public void shouldAddObservations() {
        String elementName = "field";
        String observationValue = "value";
        String patientId = "motech Id";
        MRSPatientDto patient = new MRSPatientDto();
        patient.setMotechId(patientId);
        CommcareForm form = new FormBuilder("form").with(elementName, observationValue).withMeta(FormMappingConstants.FORM_TIME_END, "2013-12-12").getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("form");
        ObservationMapping observationMapping = new ObservationMapping();
        String conceptName = "name to be stored";
        observationMapping.setConceptName(conceptName);
        observationMapping.setElementName(elementName);

        List<ObservationMapping> observationMappings = new ArrayList<>();
        observationMappings.add(observationMapping);
        CommcareFormSegment beneficiarySegment = new CommcareFormSegment(form, form.getForm(), new ArrayList<String>(), new AllElementSearchStrategies());

        MRSEncounterActivity activity = new EncounterActivityBuilder().withFormMapperProperties(formMapperProperties).withObservationMappings(observationMapping).getActivity();
        when(mrsUtil.getPatientByMotechId(anyString())).thenReturn(new MRSPatientDto());

        encountersAdapter.adaptForm(form, activity);
        Set<MRSObservationDto> observations = ObservationsGenerator.generate(observationMappings, beneficiarySegment, patient, observationIdGenerationStrategy);

        MRSObservationDto actualObservation = observations.iterator().next();
        assertEquals(conceptName, actualObservation.getConceptName());
        assertEquals(observationValue, actualObservation.getValue());
    }

    @Test
    public void shouldCheckIfObservationMappingsAreNull() {
        String elementName = "field";
        String observationValue = "value";
        String encounterId = "myencounterid";

        CommcareForm form = new FormBuilder("form").with(elementName, observationValue).withMeta(FormMappingConstants.FORM_TIME_END, "2013-12-12").getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("form");

        MRSEncounterActivity activity = new EncounterActivityBuilder().getActivity();
        activity.setObservationMappings(null);
        HashMap<String, String> encounterIdScheme = new HashMap<>();
        activity.setEncounterIdScheme(encounterIdScheme);

        when(idResolver.retrieveId(eq(encounterIdScheme), any(CommcareFormSegment.class))).thenReturn(encounterId);
        when(mrsUtil.getPatientByMotechId(anyString())).thenReturn(new MRSPatientDto());

        encountersAdapter.adaptForm(form, activity);

        verify(mrsUtil).addEncounter(eq(encounterId), any(MRSPatientDto.class), observationCaptor.capture(), anyString(), any(DateTime.class), anyString(), anyString());
        List<Set<MRSObservationDto>> observations = observationCaptor.getAllValues();
        int actualObservation = observations.get(0).size();
        assertEquals(0, actualObservation);
    }

    @Test
    public void shouldNotAddEncountersIfPatientDoesNotExists() {
        CommcareForm form = new FormBuilder("form").getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("form");
        MRSEncounterActivity activity = new EncounterActivityBuilder().getActivity();
        activity.setObservationMappings(null);
        when(mrsUtil.getPatientByMotechId(anyString())).thenReturn(null);

        encountersAdapter.adaptForm(form, activity);

        verify(mrsUtil,times(0)).addEncounter(any(String.class), any(MRSPatientDto.class), observationCaptor.capture(), anyString(), any(DateTime.class), anyString(), anyString());
    }
}
