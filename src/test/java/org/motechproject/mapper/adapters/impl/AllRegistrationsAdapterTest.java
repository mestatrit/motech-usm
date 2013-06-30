package org.motechproject.mapper.adapters.impl;

import com.google.common.collect.HashMultimap;
import org.apache.log4j.Level;
import org.hamcrest.core.IsAnything;
import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.builder.FormBuilder;
import org.motechproject.mapper.builder.RegistrationActivityBuilder;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.FormMapperProperties;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.util.AllElementSearchStrategies;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.mapper.util.IdentityResolver;
import org.motechproject.mapper.util.MRSUtil;
import org.motechproject.mapper.util.TestAppender;
import org.motechproject.mapper.validation.ValidationError;
import org.motechproject.mapper.validation.ValidationManager;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.model.MRSPersonDto;
import org.motechproject.mrs.services.MRSPatientAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mapper.constants.FormMappingConstants.FIRST_NAME_FIELD;

public class AllRegistrationsAdapterTest {
    @Mock
    private MRSUtil mrsUtil;
    @Mock
    private MRSPatientAdapter mrsPatientAdapter;
    @Mock
    private IdentityResolver idResolver;
    @Mock
    private ValidationManager validator;

    private AllRegistrationsAdapter registrationAdapter;

    @Before
    public void setUp() {
        initMocks(this);
        registrationAdapter = new AllRegistrationsAdapter(mrsUtil, idResolver, mrsPatientAdapter, validator,new AllElementSearchStrategies(), new PersonAdapter());
    }

    @After
    public void tearDown() {
        TestAppender.clear();
    }

    @Test
    public void shouldGetTopElementForMultipleStartNodes() {
        FormValueElement element1 = new FormValueElement();
        FormValueElement element2 = new FormValueElement();
        element1.setElementName("child_info_1");
        element2.setElementName("child_info_2");
        String startElement = "child_info";
        CommcareForm form = new FormBuilder(startElement).withSubElement(element1).withSubElement(element2).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(startElement);
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn("motech-id");
        registrationAdapter.adaptForm(form, activity);
        verify(mrsPatientAdapter, times(1)).savePatient(any(MRSPatientDto.class));
    }

    @Test
    public void shouldGetTopElementForSingleStartNode() {
        FormValueElement element1 = new FormValueElement();
        element1.setElementName("child_info");
        String startElement = "form";
        CommcareForm form = new FormBuilder(startElement).withSubElement(element1).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(startElement);
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, activity);

        verify(mrsPatientAdapter, times(1)).savePatient(any(MRSPatientDto.class));
    }

   @Test
    public void shouldHandleIfStartElementIsNull() {
        FormValueElement element1 = new FormValueElement();
        element1.setElementName("child_info");
        String startElement = "form";
        CommcareForm form = new FormBuilder(startElement).withSubElement(element1).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("invalid_start_node");
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, activity);

        verify(mrsPatientAdapter, times(0)).savePatient(any(MRSPatientDto.class));
    }

    @Test
    public void shouldGetPreDefinedFields() {
        String nameFieldInForm = "name";
        String nameValueInForm = "amy";
        String topElementName = "form";
        CommcareForm form = new FormBuilder(topElementName).withSubElement(nameFieldInForm, nameValueInForm).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topElementName);
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withRegistrationMapping(FIRST_NAME_FIELD, nameFieldInForm).withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, activity);

        ArgumentCaptor<MRSPatient> patientCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        verify(mrsPatientAdapter,times(1)).savePatient(patientCaptor.capture());
        String actualName = patientCaptor.getValue().getPerson().getFirstName();
        assertEquals(nameValueInForm, actualName);
    }

    @Test
    public void shouldSavePatient() {
        String topFormElement = "form";
        FormValueElement formValueElement = new FormValueElement();
        String child_info = "child_info";
        formValueElement.setElementName(child_info);
        FormValueElement anotherFormValueElement = new FormValueElement();
        anotherFormValueElement.setElementName(child_info);
        CommcareForm form = new FormBuilder(topFormElement).withSubElement(formValueElement).withSubElement(anotherFormValueElement).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topFormElement);
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().
                withFormMapperProperties(formMapperProperties).getActivity();
        AllRegistrationsAdapter registrationsAdapter = new AllRegistrationsAdapter(mrsUtil, idResolver, mrsPatientAdapter, validator, new AllElementSearchStrategies(), new PersonAdapter());
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn("motech-id");

        registrationsAdapter.adaptForm(form, registrationActivity);

        verify(mrsPatientAdapter, times(1)).savePatient(any(MRSPatientDto.class));
    }

    @Test
    public void shouldSaveSinglePatientIfMultiplePropertyIsNotSet() {
        String topFormElement = "form";
        FormValueElement formValueElement = new FormValueElement();
        String child_info = "child_info";
        formValueElement.setElementName(child_info);
        FormValueElement anotherFormValueElement = new FormValueElement();
        anotherFormValueElement.setElementName(child_info);
        CommcareForm form = new FormBuilder(topFormElement).withSubElement(formValueElement).withSubElement(anotherFormValueElement).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topFormElement);
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, registrationActivity);

        verify(mrsPatientAdapter, times(1)).savePatient(any(MRSPatientDto.class));
    }

    @Test
    public void shouldNotSavePatientIfMotechIdNotFoundAndLogError() {
        String instanceId = "myInstanceId";
        String topFormElement = "form";
        FormValueElement formValueElement = new FormValueElement();
        String child_info = "child_info";
        formValueElement.setElementName(child_info);
        CommcareForm form = new FormBuilder(topFormElement).withSubElement(formValueElement).getForm();

        form.setId(instanceId);

        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topFormElement);
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();

        HashMap<String, String> patientIdScheme = new HashMap<>();
        registrationActivity.setPatientIdScheme(patientIdScheme);

        when(idResolver.retrieveId(eq(patientIdScheme), any(CommcareFormSegment.class))).thenReturn(null);

        registrationAdapter.adaptForm(form, registrationActivity);

        verify(mrsPatientAdapter, never()).getPatientByMotechId(anyString());
        verify(mrsPatientAdapter, never()).savePatient(any(MRSPatientDto.class));
        assertNotNull(TestAppender.findMatching(new IsEqual(Level.ERROR), new IsEqual<>(String.format("Motech id is empty for form(%s). Ignoring this form.", instanceId))));
    }

    @Test
    public void shouldNotSavePatientIfMotechIdNotFoundAndDoNotLogErrorIfSkipMappingIsSetToTrue() {
        String instanceId = "myInstanceId";
        String topFormElement = "form";
        FormValueElement formValueElement = new FormValueElement();
        String child_info = "child_info";
        formValueElement.setElementName(child_info);
        CommcareForm form = new FormBuilder(topFormElement).withSubElement(formValueElement).getForm();

        form.setId(instanceId);

        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topFormElement);
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();

        HashMap<String, String> patientIdScheme = new HashMap<String, String>(){{
            put(FormMappingConstants.REPORT_MISSING_ID, "False");
        }};
        registrationActivity.setPatientIdScheme(patientIdScheme);

        when(idResolver.retrieveId(eq(patientIdScheme), any(CommcareFormSegment.class))).thenReturn(null);

        registrationAdapter.adaptForm(form, registrationActivity);

        verify(mrsPatientAdapter, never()).getPatientByMotechId(anyString());
        verify(mrsPatientAdapter, never()).savePatient(any(MRSPatientDto.class));
        assertNull(TestAppender.findMatching(new IsEqual(Level.ERROR), new IsAnything<String>()));
    }

    @Test
     public void shouldSavePatientWithAttributes() {
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        String startElement = "form";
        formMapperProperties.setStartElement(startElement);
        String value = "value";
        CommcareForm form = new FormBuilder(startElement).withSubElement("attribute", value).getForm();
        String description = "Attribute description";
        MRSRegistrationActivity attribute = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).withAttributes(description, "attribute").getActivity();
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, attribute);

        ArgumentCaptor<MRSPatientDto> patientCaptor = ArgumentCaptor.forClass(MRSPatientDto.class);
        verify(mrsPatientAdapter).savePatient(patientCaptor.capture());
        List<MRSAttribute> attributes = patientCaptor.getValue().getPerson().getAttributes();
        assertEquals(1, attributes.size());
        assertEquals(description, attributes.get(0).getName());
        assertEquals(value, attributes.get(0).getValue());
    }

    @Test
    public void shouldUpdateThePatientsDetails() {
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        String startElement = "form";
        formMapperProperties.setStartElement(startElement);
        FormValueElement formValueElement = new FormValueElement();
        String child_info = "child_info";
        formValueElement.setElementName(child_info);
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();
        CommcareForm form = new FormBuilder(startElement).withSubElement(formValueElement).getForm();
        String motechId = "motech-id";
        MRSPatientDto patient = new MRSPatientDto();
        MRSPersonDto person = new MRSPersonDto();
        patient.setPerson(person);
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn(motechId);
        when(mrsPatientAdapter.getPatientByMotechId(motechId)).thenReturn(patient);

        registrationAdapter.adaptForm(form, activity);

        verify(mrsPatientAdapter, never()).savePatient(any(MRSPatientDto.class));
        verify(mrsPatientAdapter).updatePatient(any(MRSPatient.class));
    }

    @Test
    public void shouldNotSaveIfValidationErrorsArePresent() {
        String topFormElement = "form";
        FormValueElement formValueElement = new FormValueElement();
        String child_info = "child_info";
        formValueElement.setElementName(child_info);
        CommcareForm form = new FormBuilder(topFormElement).withSubElement(formValueElement).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topFormElement);
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn(null);
        List<ValidationError> validationErrors = new ArrayList<>();
        validationErrors.add(new ValidationError("error type", "error message"));
        when(validator.validatePatient(any(MRSPatientDto.class))).thenReturn(validationErrors);

        registrationAdapter.adaptForm(form, registrationActivity);

        verify(mrsPatientAdapter, never()).getPatientByMotechId(anyString());
        verify(mrsPatientAdapter, never()).savePatient(any(MRSPatientDto.class));
    }

    @Test
    public void shouldHandleNullActivityFields() {
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().getActivity();
        registrationActivity.setRegistrationMappings(null);
        registrationActivity.setAttributes(null);
        registrationActivity.setStaticMappings(null);
        registrationActivity.setFacilityScheme(null);
        registrationActivity.setPatientIdScheme(null);
        registrationActivity.setProviderScheme(null);
        registrationActivity.setType(null);
        FormValueElement element1 = new FormValueElement();
        String startElement = "form";
        element1.setElementName("child_info");
        CommcareForm form = new FormBuilder(startElement).withSubElement(element1).getForm();
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, registrationActivity);

        verify(mrsPatientAdapter, times(1)).savePatient(any(MRSPatientDto.class));
    }

    @Test
    public void shouldGetStartElementByPathExpression() {
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("//case");
        FormValueElement element1 = new FormValueElement();
        HashMultimap<String, FormValueElement> subElements = new HashMultimap<>();
        FormValueElement subElement = new FormValueElement();
        subElement.setElementName("case");
        subElements.put("case", subElement);
        element1.setSubElements(subElements);
        element1.getAttributes().put("myAttribute", "myValue");
        element1.setElementName("case");
        CommcareForm form = new FormBuilder("form").withSubElement(element1).getForm();
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).withAttributes("value field", "//@myAttribute").getActivity();
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, registrationActivity);

        ArgumentCaptor<MRSPatientDto> patientCaptor = ArgumentCaptor.forClass(MRSPatientDto.class);
        verify(mrsPatientAdapter,times(1)).savePatient(patientCaptor.capture());
        MRSPatientDto actualPatient = patientCaptor.getValue();
        assertEquals(1, actualPatient.getPerson().getAttributes().size());
        assertEquals("myValue", actualPatient.getPerson().getAttributes().get(0).getValue());
    }

    @Test
    public void shouldGetEmptySetWhenStartElementIsNotFound() {
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement("//cccc");
        FormValueElement element1 = new FormValueElement();
        element1.setElementName("case");
        HashMultimap<String, FormValueElement> subElements = new HashMultimap<>();
        FormValueElement subElement = new FormValueElement();
        subElement.setElementName("case");
        subElement.setValue("value");
        subElements.put("element", subElement);
        element1.setSubElements(subElements);
        CommcareForm form = new FormBuilder("form").withSubElement(element1).getForm();
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).withAttributes("value field", "//element").getActivity();
        when(idResolver.retrieveId(anyMap(), any(CommcareFormSegment.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, registrationActivity);

        ArgumentCaptor<MRSPatientDto> patientCaptor = ArgumentCaptor.forClass(MRSPatientDto.class);
        verify(mrsPatientAdapter, times(0)).savePatient(patientCaptor.capture());

    }
}
