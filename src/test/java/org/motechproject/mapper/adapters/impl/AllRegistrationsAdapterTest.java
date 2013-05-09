package org.motechproject.mapper.adapters.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.builder.FormBuilder;
import org.motechproject.mapper.builder.RegistrationActivityBuilder;
import org.motechproject.mapper.domain.FormMapperProperties;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.util.IdentityResolver;
import org.motechproject.mapper.util.MRSUtil;
import org.motechproject.mapper.validation.ValidationError;
import org.motechproject.mapper.validation.ValidationManager;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.model.MRSPersonDto;
import org.motechproject.mrs.services.MRSPatientAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mapper.constants.FormMappingConstants.*;

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
        registrationAdapter = new AllRegistrationsAdapter(mrsUtil, idResolver, mrsPatientAdapter, validator);
    }

    @Test
    public void shouldGetPreDefinedFields() {
        String nameFieldInForm = "name";
        String nameValueInForm = "amy";
        String topElementName = "form";
        CommcareForm form = new FormBuilder(topElementName).with(nameFieldInForm, nameValueInForm).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topElementName);
        formMapperProperties.setMultiple(false);
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withRegistrationMapping(FIRST_NAME_FIELD, nameFieldInForm).withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn("motech-id");


        registrationAdapter.adaptForm(form, activity);

        ArgumentCaptor<MRSPatient> patientCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        verify(mrsPatientAdapter).savePatient(patientCaptor.capture());
        String actualName = patientCaptor.getValue().getPerson().getFirstName();
        assertEquals(nameValueInForm, actualName);
    }

    @Test
    public void shouldGetDOB() {
        String dobField = "dob";
        String topElementName = "form";
        DateTime dobValue = new DateTime().withDate(2011, 8, 14).withTime(0, 0, 0, 0);
        CommcareForm form = new FormBuilder(topElementName).with(dobField, "2011-8-14").getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topElementName);
        formMapperProperties.setMultiple(false);
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withRegistrationMapping(DOB_FIELD, dobField).withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, activity);

        ArgumentCaptor<MRSPatient> patientCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        verify(mrsPatientAdapter).savePatient(patientCaptor.capture());
        DateTime dateOfBirth = patientCaptor.getValue().getPerson().getDateOfBirth();
        assertEquals(dobValue, dateOfBirth);
    }

    @Test
    public void shouldGetBooleanValue() {
        String isDead = "dead";
        String topElementName = "form";

        Boolean aBoolean = Boolean.FALSE;
        CommcareForm form = new FormBuilder("form").with(isDead, "false").getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topElementName);
        formMapperProperties.setMultiple(false);

        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withRegistrationMapping(IS_DEAD_FIELD, isDead).withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, activity);

        ArgumentCaptor<MRSPatient> patientCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        verify(mrsPatientAdapter).savePatient(patientCaptor.capture());
        Boolean DeadTrue = patientCaptor.getValue().getPerson().isDead();
        assertEquals(aBoolean, DeadTrue);
    }

    @Test
    public void shouldAllRegistrationMappingFields() {
        String firstNameField = "first_name_field";
        String firstName = "firstName";
        String middleNameField = "middleNameField";
        String middleName = "middleName";
        String lastNameField = "lastNameField";
        String lastName = "lastName";
        String preferredNameField = "preferred_name_field";
        String preferredName = "preferredName";
        String genderField = "gender_field";
        String gender = "gender";
        String addressField = "address_field";
        String address = "address";
        String ageField = "age_field";
        String age = "11";
        String birthDateEstimatedField = "birth_date_estimated_field";
        String birthDateEstimated = "false";
        String topElementName = "form";

        Boolean expectedIsAgeCalculated = Boolean.FALSE;
        String isDeadField = "is_dead_field";
        String isDeadValue = "true";
        Boolean expectedIsDead = Boolean.TRUE;
        String deathDateField = "is_death_date_field";
        String deathDate = "2011-01-01";
        DateTime expectedDeathDate = new DateTime().withDate(2011, 1, 1).withTime(0, 0, 0, 0);
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topElementName);
        formMapperProperties.setMultiple(false);

        CommcareForm form = new FormBuilder("form").with(firstNameField, firstName)
                .with(middleNameField, middleName).with(lastNameField, lastName)
                .with(preferredNameField, preferredName).with(genderField, gender)
                .with(addressField, address).with(ageField, age).with(birthDateEstimatedField, birthDateEstimated)
                .with(isDeadField, isDeadValue).with(deathDateField, deathDate).getForm();
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withRegistrationMapping(IS_DEAD_FIELD, isDeadField)
                .withRegistrationMapping(FIRST_NAME_FIELD, firstNameField)
                .withRegistrationMapping(MIDDLE_NAME_FIELD, middleNameField)
                .withRegistrationMapping(LAST_NAME_FIELD, lastNameField)
                .withRegistrationMapping(PREFERRED_NAME_FIELD, preferredNameField)
                .withRegistrationMapping(GENDER_FIELD, genderField)
                .withRegistrationMapping(ADDRESS_FIELD, addressField)
                .withRegistrationMapping(AGE_FIELD, ageField)
                .withRegistrationMapping(BIRTH_DATE_ESTIMATED_FIELD, birthDateEstimatedField)
                .withRegistrationMapping(DEATH_DATE_FIELD, deathDateField)
                .withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, activity);

        ArgumentCaptor<MRSPatient> patientCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        verify(mrsPatientAdapter).savePatient(patientCaptor.capture());
        MRSPerson person = patientCaptor.getValue().getPerson();
        String actualFirstName = person.getFirstName();
        String actualMiddleName = person.getMiddleName();
        String actualLastName = person.getLastName();
        String actualPreferredName = person.getPreferredName();
        String actualGender = person.getGender();
        String actualAddress = person.getAddress();
        Integer actualAge = person.getAge();
        Boolean actualBirthDateEstimated = person.getBirthDateEstimated();
        DateTime actualDeathDate = person.getDeathDate();
        Boolean actualIsDead = person.isDead();

        assertEquals(firstName, actualFirstName);
        assertEquals(middleName, actualMiddleName);
        assertEquals(lastName, actualLastName);
        assertEquals(preferredName, actualPreferredName);
        assertEquals(gender, actualGender);
        assertEquals(address, actualAddress);
        assertEquals(age, actualAge.toString());
        assertEquals(expectedIsAgeCalculated, actualBirthDateEstimated);
        assertEquals(expectedDeathDate, actualDeathDate);
        assertEquals(expectedIsDead, actualIsDead);
    }

    @Test
    public void shouldSaveMultiplePatients() {
        String topFormElement = "form";
        FormValueElement formValueElement = new FormValueElement();
        String child_info = "child_info";
        formValueElement.setElementName(child_info);
        FormValueElement anotherFormValueElement = new FormValueElement();
        anotherFormValueElement.setElementName(child_info);
        CommcareForm form = new FormBuilder(topFormElement).with(child_info, formValueElement).with(child_info, anotherFormValueElement).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topFormElement);
        formMapperProperties.setMultiple(true);
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();
        AllRegistrationsAdapter registrationsAdapter = new AllRegistrationsAdapter(mrsUtil, idResolver, mrsPatientAdapter, validator);
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn("motech-id");

        registrationsAdapter.adaptForm(form, registrationActivity);

        verify(mrsPatientAdapter, times(2)).savePatient(any(MRSPatientDto.class));

    }

    @Test
    public void shouldSaveSinglePatientIfMultiplePropertyIsNotSet() {
        String topFormElement = "form";
        FormValueElement formValueElement = new FormValueElement();
        String child_info = "child_info";
        formValueElement.setElementName(child_info);
        FormValueElement anotherFormValueElement = new FormValueElement();
        anotherFormValueElement.setElementName(child_info);
        CommcareForm form = new FormBuilder(topFormElement).with(child_info, formValueElement).with(child_info, anotherFormValueElement).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topFormElement);
        formMapperProperties.setMultiple(false);
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, registrationActivity);

        verify(mrsPatientAdapter, times(1)).savePatient(any(MRSPatientDto.class));

    }

    @Test
    public void shouldNotSaveThePatientsInfoIfIdNotFound() {
        String topFormElement = "form";
        FormValueElement formValueElement = new FormValueElement();
        String child_info = "child_info";
        formValueElement.setElementName(child_info);
        CommcareForm form = new FormBuilder(topFormElement).with(child_info, formValueElement).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topFormElement);
        formMapperProperties.setMultiple(false);
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn(null);

        registrationAdapter.adaptForm(form, registrationActivity);

        verify(mrsPatientAdapter, never()).getPatientByMotechId(anyString());
        verify(mrsPatientAdapter, never()).savePatient(any(MRSPatientDto.class));

    }

    @Test
    public void shouldSavePatientWithAttributes() {
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        String startElement = "form";
        formMapperProperties.setStartElement(startElement);
        formMapperProperties.setMultiple(false);
        String value = "value";
        CommcareForm form = new FormBuilder(startElement).with("attribute", value).getForm();
        String description = "Attribute description";
        MRSRegistrationActivity attribute = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).withAttributes(description, "attribute").getActivity();
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn("motech-id");

        registrationAdapter.adaptForm(form, attribute);

        ArgumentCaptor<MRSPatientDto> patientCaptor = ArgumentCaptor.forClass(MRSPatientDto.class);
        verify(mrsPatientAdapter).savePatient(patientCaptor.capture());
        List<MRSAttribute> attributes = patientCaptor.getValue().getPerson().getAttributes();
        assertEquals(1, attributes.size());
        assertEquals(description, attributes.get(0).getName());
        assertEquals(value, attributes.get(0).getValue());
    }

    @Test
    public void shouldSaveParentIdSchemeIfParentSchemeIsTheir() {
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        String startElement = "form";
        formMapperProperties.setStartElement(startElement);
        formMapperProperties.setMultiple(false);
        FormValueElement elementWithCaseId = new FormValueElement();
        String elementForParentScheme = "child_info";
        elementWithCaseId.setElementName(elementForParentScheme);
        HashMap<String, String> attributes = new HashMap<>();
        String caseId = "some random case id";
        String attributeForParentScheme = "case_id";
        attributes.put(attributeForParentScheme, caseId);
        elementWithCaseId.setAttributes(attributes);
        CommcareForm form = new FormBuilder(startElement).with(elementForParentScheme, elementWithCaseId).getForm();
        HashMap<String, String> parentIdScheme = new HashMap<>();
        parentIdScheme.put(ID_PARENT_START_ELEMENT, elementForParentScheme);
        parentIdScheme.put(ID_SCHEME_TYPE, ID_FROM_FORM_SCHEME);
        parentIdScheme.put(ID_SCHEME_FIELD, elementForParentScheme);
        parentIdScheme.put(ID_SCHEME_ATTRIBUTE, attributeForParentScheme);
        HashMap<String, String> patientIdScheme = new HashMap<>();
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).withParentIdScheme(parentIdScheme).withPatientIdScheme(patientIdScheme).getActivity();
        when(idResolver.retrieveId(patientIdScheme, form, form.getForm())).thenReturn("motech-id");
        Collection<FormValueElement> entries = form.getForm().getSubElements().get(elementForParentScheme);
        FormValueElement formValueElement = entries.iterator().next();

        when(idResolver.retrieveId(parentIdScheme, form, formValueElement)).thenReturn(caseId);

        registrationAdapter.adaptForm(form, activity);

        ArgumentCaptor<MRSPatientDto> patientCaptor = ArgumentCaptor.forClass(MRSPatientDto.class);
        verify(mrsPatientAdapter).savePatient(patientCaptor.capture());
        List<MRSAttribute> actualAttributes = patientCaptor.getValue().getPerson().getAttributes();
        assertEquals(1, attributes.size());
        assertEquals(PARENT_ID, actualAttributes.get(0).getName());
        assertEquals(caseId, actualAttributes.get(0).getValue());

    }

    @Test
    public void shouldUpdateThePatientsDetails() {
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        String startElement = "form";
        formMapperProperties.setStartElement(startElement);
        formMapperProperties.setMultiple(false);
        FormValueElement formValueElement = new FormValueElement();
        String child_info = "child_info";
        formValueElement.setElementName(child_info);
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();
        CommcareForm form = new FormBuilder(startElement).with(child_info, formValueElement).getForm();
        String motechId = "motech-id";
        MRSPatientDto patient = new MRSPatientDto();
        MRSPersonDto person = new MRSPersonDto();
        patient.setPerson(person);
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn(motechId);
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
        CommcareForm form = new FormBuilder(topFormElement).with(child_info, formValueElement).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topFormElement);
        formMapperProperties.setMultiple(false);
        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder().withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn(null);
        List<ValidationError> validationErrors = new ArrayList<>();
        validationErrors.add(new ValidationError("error type", "error message"));
        when(validator.validatePatient(any(MRSPatientDto.class))).thenReturn(validationErrors);

        registrationAdapter.adaptForm(form, registrationActivity);

        verify(mrsPatientAdapter, never()).getPatientByMotechId(anyString());
        verify(mrsPatientAdapter, never()).savePatient(any(MRSPatientDto.class));

    }

}
