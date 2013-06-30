package org.motechproject.mapper.adapters.impl;

import org.apache.log4j.Level;
import org.hamcrest.core.IsAnything;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.mapper.builder.EncounterActivityBuilder;
import org.motechproject.mapper.builder.FormBuilder;
import org.motechproject.mapper.builder.FormValueElementBuilder;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.FormMapperProperties;
import org.motechproject.mapper.domain.MRSEncounterActivity;
import org.motechproject.mapper.domain.ObservationMapping;
import org.motechproject.mapper.util.AllElementSearchStrategies;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.mapper.util.EncounterIdGenerationStrategy;
import org.motechproject.mapper.util.IdentityResolver;
import org.motechproject.mapper.util.MRSUtil;
import org.motechproject.mapper.util.ObservationsGenerator;
import org.motechproject.mapper.util.TestAppender;
import org.motechproject.mapper.validation.ValidationManager;
import org.motechproject.mrs.model.MRSObservationDto;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.services.MRSPatientAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
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
    private EncounterIdGenerationStrategy observationIdGenerationStrategy;

    @Before
    public void setUp() {
        initMocks(this);
        encountersAdapter = new AllEncountersAdapter(mrsUtil, idResolver, new AllElementSearchStrategies());
    }

    @After
    public void tearDown() {
        TestAppender.clear();
    }

    @Test
    public void shouldAddObservations() {
        String elementName = "field";
        String observationValue = "value";
        String patientId = "motech Id";
        MRSPatientDto patient = new MRSPatientDto();
        patient.setMotechId(patientId);
        CommcareForm form = new FormBuilder("form").withSubElement(elementName, observationValue).withMeta(FormMappingConstants.FORM_TIME_END, "2013-12-12").getForm();
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
        DateTime encounterDate = DateTime.now();
        Set<MRSObservationDto> observations = ObservationsGenerator.generate(observationMappings, beneficiarySegment, patient, observationIdGenerationStrategy, encounterDate);

        MRSObservationDto actualObservation = observations.iterator().next();
        assertEquals(conceptName, actualObservation.getConceptName());
        assertEquals(observationValue, actualObservation.getValue());
        assertEquals(encounterDate, actualObservation.getDate());
    }

    @Test
    public void shouldCheckIfObservationMappingsAreNull() {
        String elementName = "field";
        String observationValue = "value";
        String instanceId = "myinstanceid";
        String patientId = "mypatientid";

        CommcareForm form = new FormBuilder("form").withSubElement(elementName, observationValue).withMeta(FormMappingConstants.FORM_TIME_END, "2013-12-12").getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("form");

        MRSEncounterActivity activity = new EncounterActivityBuilder().getActivity();
        activity.setObservationMappings(null);

        HashMap<String, String> encounterIdScheme = new HashMap<>();
        activity.setEncounterIdScheme(encounterIdScheme);

        HashMap<String, String> patientIdScheme = new HashMap<>();
        activity.setPatientIdScheme(patientIdScheme);

        when(idResolver.retrieveId(same(encounterIdScheme), any(CommcareFormSegment.class))).thenReturn(instanceId);
        when(idResolver.retrieveId(same(patientIdScheme) , any(CommcareFormSegment.class))).thenReturn(patientId);
        when(mrsUtil.getPatientByMotechId(patientId)).thenReturn(new MRSPatientDto());

        encountersAdapter.adaptForm(form, activity);

        verify(mrsUtil).addEncounter(eq(patientId + "-" + instanceId), any(MRSPatientDto.class), observationCaptor.capture(), anyString(), any(DateTime.class), anyString(), anyString());
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

        verify(mrsUtil, times(0)).addEncounter(any(String.class), any(MRSPatientDto.class), observationCaptor.capture(), anyString(), any(DateTime.class), anyString(), anyString());
    }

    @Test
    public void shouldIgnoreMappingIfFormDoesNotHaveMotechIdAndLogError() {
        String instanceId = "myInstanceId";

        DateTime encounterDate = DateTime.now();
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("case")
                .withAttribute("date_modified", encounterDate.toString());
        CommcareForm form = new FormBuilder("form").withSubElement(rootElementBuilder.build()).getForm();
        form.setId(instanceId);

        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("form");
        MRSEncounterActivity activity = new EncounterActivityBuilder().getActivity();
        activity.setObservationMappings(null);

        HashMap<String, String> patientIdScheme = new HashMap<>();
        activity.setPatientIdScheme(patientIdScheme);

        activity.setEncounterMappings(new HashMap<String, String>() {{
            put(FormMappingConstants.ENCOUNTER_DATE_FIELD, "//case/@date_modified");
        }});

        when(idResolver.retrieveId(eq(patientIdScheme), any(CommcareFormSegment.class))).thenReturn(null);

        assertNull(TestAppender.findMatching(new IsEqual(Level.ERROR), new IsEqual<>(String.format("Motech id is empty for form(%s). Ignoring this form.", instanceId))));

        encountersAdapter.adaptForm(form, activity);

        verifyZeroInteractions(mrsUtil);
    }

    @Test
    public void shouldIgnoreMappingIfFormDoesNotHaveMotechIdAndDoNotLogError() {
        DateTime encounterDate = DateTime.now();
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("case")
                .withAttribute("date_modified", encounterDate.toString());
        CommcareForm form = new FormBuilder("form").withSubElement(rootElementBuilder.build()).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("form");
        MRSEncounterActivity activity = new EncounterActivityBuilder().getActivity();
        activity.setObservationMappings(null);

        HashMap<String, String> patientIdScheme = new HashMap<String, String>(){{
            put(FormMappingConstants.REPORT_MISSING_ID, "False");
        }};

        activity.setPatientIdScheme(patientIdScheme);

        activity.setEncounterMappings(new HashMap<String, String>() {{
            put(FormMappingConstants.ENCOUNTER_DATE_FIELD, "//case/@date_modified");
        }});

        when(idResolver.retrieveId(eq(patientIdScheme), any(CommcareFormSegment.class))).thenReturn(null);

        encountersAdapter.adaptForm(form, activity);

        verifyZeroInteractions(mrsUtil);
        assertNull(TestAppender.findMatching(new IsEqual(Level.ERROR), new IsAnything<String>()));
    }

    @Test
    public void shouldGetEncounterDateAndAddEncounter() {
        DateTime encounterDate = DateTime.now();
        String caseId = "myCaseId";
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("case")
                .withAttribute("date_modified", encounterDate.toString());
        CommcareForm form = new FormBuilder("form").withSubElement(rootElementBuilder.build()).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("form");
        MRSEncounterActivity activity = new EncounterActivityBuilder().getActivity();
        activity.setObservationMappings(null);

        HashMap<String, String> patientIdScheme = new HashMap<>();
        activity.setPatientIdScheme(patientIdScheme);

        activity.setEncounterMappings(new HashMap<String, String>() {{
            put(FormMappingConstants.ENCOUNTER_DATE_FIELD, "//case/@date_modified");
        }});
        when(mrsUtil.getPatientByMotechId(caseId)).thenReturn(new MRSPatientDto());
        when(idResolver.retrieveId(eq(patientIdScheme), any(CommcareFormSegment.class))).thenReturn(caseId);

        encountersAdapter.adaptForm(form, activity);

        ArgumentCaptor<DateTime> encounterDateCaptor = ArgumentCaptor.forClass(DateTime.class);
        verify(mrsUtil).addEncounter(anyString(), any(MRSPatientDto.class), any(Set.class), anyString(), encounterDateCaptor.capture(), anyString(), anyString());
        assertEquals(encounterDate.getMillis(), encounterDateCaptor.getValue().getMillis());
    }

    @Test
    public void shouldAddEncounterWithEncounterDateAsCurrentTimeIfEncounterMappingsIsNotProvided() {
        DateTime encounterDate = DateTime.now();
        String caseId = "myCaseId";
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("case")
                .withAttribute("date_modified", encounterDate.toString());
        CommcareForm form = new FormBuilder("form").withSubElement(rootElementBuilder.build()).getForm();FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("form");

        MRSEncounterActivity activity = new EncounterActivityBuilder().getActivity();

        HashMap<String, String> patientIdScheme = new HashMap<>();
        activity.setPatientIdScheme(patientIdScheme);

        activity.setObservationMappings(null);
        when(mrsUtil.getPatientByMotechId(caseId)).thenReturn(new MRSPatientDto());
        when(idResolver.retrieveId(eq(patientIdScheme), any(CommcareFormSegment.class))).thenReturn(caseId);

        encountersAdapter.adaptForm(form, activity);

        ArgumentCaptor<DateTime> encounterDateCaptor = ArgumentCaptor.forClass(DateTime.class);
        verify(mrsUtil).addEncounter(anyString(), any(MRSPatientDto.class), any(Set.class), anyString(), encounterDateCaptor.capture(), anyString(), anyString());
        assertEquals(DateTime.now().withMillisOfSecond(0), encounterDateCaptor.getValue().withMillisOfSecond(0));
    }

    @Test
    public void shouldAddEncounterWithEncounterDateAsCurrentTimeIfEncounterDateFieldMappingIsNotFound() {
        DateTime encounterDate = DateTime.now();
        String caseId = "myCaseId";
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("case")
                .withAttribute("date_modified", encounterDate.toString())
                .withAttribute("case_id", caseId);
        CommcareForm form = new FormBuilder("form").withSubElement(rootElementBuilder.build()).getForm();FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("form");
        MRSEncounterActivity activity = new EncounterActivityBuilder().getActivity();

        HashMap<String, String> patientIdScheme = new HashMap<>();
        activity.setPatientIdScheme(patientIdScheme);

        activity.setObservationMappings(null);
        activity.setEncounterMappings(new HashMap<String, String>() {{
            put("someKey", "//someValue");
        }});
        when(mrsUtil.getPatientByMotechId(caseId)).thenReturn(new MRSPatientDto());
        when(idResolver.retrieveId(eq(patientIdScheme), any(CommcareFormSegment.class))).thenReturn(caseId);

        encountersAdapter.adaptForm(form, activity);

        ArgumentCaptor<DateTime> encounterDateCaptor = ArgumentCaptor.forClass(DateTime.class);
        verify(mrsUtil).addEncounter(anyString(), any(MRSPatientDto.class), any(Set.class), anyString(), encounterDateCaptor.capture(), anyString(), anyString());
        assertEquals(DateTime.now().withMillisOfSecond(0), encounterDateCaptor.getValue().withMillisOfSecond(0));
    }

    @Test
    public void shouldAddEncounterWithEncounterDateAsCurrentTimeIfDateModifiedIsNotFoundInForm() {
        DateTime encounterDate = DateTime.now();
        String caseId = "myCaseId";
        FormValueElementBuilder rootElementBuilder = new FormValueElementBuilder("case")
                .withAttribute("date_modified", encounterDate.toString())
                .withAttribute("case_id", caseId);
        CommcareForm form = new FormBuilder("form").withSubElement(rootElementBuilder.build()).getForm();FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("form");
        MRSEncounterActivity activity = new EncounterActivityBuilder().getActivity();

        HashMap<String, String> patientIdScheme = new HashMap<>();
        activity.setPatientIdScheme(patientIdScheme);

        activity.setObservationMappings(null);
        activity.setEncounterMappings(new HashMap<String, String>() {{
            put(FormMappingConstants.ENCOUNTER_DATE_FIELD, "//somefield/@date_modified");
        }});
        when(mrsUtil.getPatientByMotechId(caseId)).thenReturn(new MRSPatientDto());
        when(idResolver.retrieveId(eq(patientIdScheme), any(CommcareFormSegment.class))).thenReturn(caseId);

        encountersAdapter.adaptForm(form, activity);

        ArgumentCaptor<DateTime> encounterDateCaptor = ArgumentCaptor.forClass(DateTime.class);
        verify(mrsUtil).addEncounter(anyString(), any(MRSPatientDto.class), any(Set.class), anyString(), encounterDateCaptor.capture(), anyString(), anyString());
        assertEquals(DateTime.now().withMillisOfSecond(0), encounterDateCaptor.getValue().withMillisOfSecond(0));
    }
}
