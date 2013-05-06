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
import org.motechproject.mapper.validation.ValidationManager;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.services.MRSPatientAdapter;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void testShouldGetPreDefinedFields() {
        AllRegistrationsAdapter childRegistrationAdapter = new AllRegistrationsAdapter(mrsUtil, idResolver, mrsPatientAdapter, validator);
        String nameFieldInForm = "name";
        String nameValueInForm = "amy";
        String topElementName = "form";
        CommcareForm form = new FormBuilder(topElementName).with(nameFieldInForm, nameValueInForm).getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topElementName);
        formMapperProperties.setMultiple(false);
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withRegistrationMapping(FIRST_NAME_FIELD, nameFieldInForm).withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn("motech-id");


        childRegistrationAdapter.adaptForm(form, activity);

        ArgumentCaptor<MRSPatient> patientCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        verify(mrsPatientAdapter).savePatient(patientCaptor.capture());
        String actualName = patientCaptor.getValue().getPerson().getFirstName();
        assertEquals(nameValueInForm, actualName);
    }

    @Test
    public void testShouldGetDOB() {
        AllRegistrationsAdapter childRegistrationAdapter = new AllRegistrationsAdapter(mrsUtil, idResolver, mrsPatientAdapter, validator);
        String dobField = "dob";
        String topElementName = "form";
        DateTime dobValue = new DateTime().withDate(2011, 8, 14).withTime(0, 0, 0, 0);
        CommcareForm form = new FormBuilder(topElementName).with(dobField, "2011-8-14").getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topElementName);
        formMapperProperties.setMultiple(false);
        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withRegistrationMapping(DOB_FIELD, dobField).withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn("motech-id");

        childRegistrationAdapter.adaptForm(form, activity);

        ArgumentCaptor<MRSPatient> patientCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        verify(mrsPatientAdapter).savePatient(patientCaptor.capture());
        DateTime dateOfBirth = patientCaptor.getValue().getPerson().getDateOfBirth();
        assertEquals(dobValue, dateOfBirth);
    }

    @Test
    public void testShouldGetBooleanValue() {
        AllRegistrationsAdapter childRegistrationAdapter = new AllRegistrationsAdapter(mrsUtil, idResolver, mrsPatientAdapter, validator);
        String isDead = "dead";
        String topElementName = "form";

        Boolean aBoolean = Boolean.FALSE;
        CommcareForm form = new FormBuilder("form").with(isDead, "false").getForm();
        FormMapperProperties formMapperProperties = new FormMapperProperties();
        formMapperProperties.setStartElement(topElementName);
        formMapperProperties.setMultiple(false);

        MRSRegistrationActivity activity = new RegistrationActivityBuilder().withRegistrationMapping(IS_DEAD_FIELD, isDead).withFormMapperProperties(formMapperProperties).getActivity();
        when(idResolver.retrieveId(anyMap(), eq(form), any(FormValueElement.class))).thenReturn("motech-id");

        childRegistrationAdapter.adaptForm(form, activity);

        ArgumentCaptor<MRSPatient> patientCaptor = ArgumentCaptor.forClass(MRSPatient.class);
        verify(mrsPatientAdapter).savePatient(patientCaptor.capture());
        Boolean DeadTrue = patientCaptor.getValue().getPerson().isDead();
        assertEquals(aBoolean, DeadTrue);
    }

    @Test
    public void testShouldAllRegistrationMappingFields() {
        AllRegistrationsAdapter childRegistrationAdapter = new AllRegistrationsAdapter(mrsUtil, idResolver, mrsPatientAdapter, validator);
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

        childRegistrationAdapter.adaptForm(form, activity);

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

}
