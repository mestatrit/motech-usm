package org.motechproject.mapper.adapters.impl;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.service.PersonUpdaterFactory;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.mapper.util.PersonUpdater;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSAttributeDto;
import org.motechproject.mrs.model.MRSPersonDto;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.mapper.constants.FormMappingConstants.ADDRESS_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.AGE_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.BIRTH_DATE_ESTIMATED_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.DEATH_DATE_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.DOB_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.FIRST_NAME_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.GENDER_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.IS_DEAD_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.LAST_NAME_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.MIDDLE_NAME_FIELD;
import static org.motechproject.mapper.constants.FormMappingConstants.PREFERRED_NAME_FIELD;

public class PersonAdapterTest {

    @Mock
    private PersonUpdaterFactory personUpdaterFactory;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldDefaultValuesWhenCreatingAPersonAndNotSetModifiedTime() {
        String nameValueInForm = "amy";

        DateTime activityDate = new DateTime(2000, 1, 1, 1, 1, 1);

        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);
        MRSRegistrationActivity activity = mock(MRSRegistrationActivity.class);
        when(activity.getValueFor(FIRST_NAME_FIELD, beneficiarySegment, String.class)).thenReturn(nameValueInForm);
        when(activity.getActivityDate(beneficiarySegment)).thenReturn(activityDate);

        PersonUpdater personUpdater = mock(PersonUpdater.class);
        ArgumentCaptor<MRSPerson> personArgumentCaptor = ArgumentCaptor.forClass(MRSPerson.class);
        when(personUpdaterFactory.getPersonUpdater(personArgumentCaptor.capture(), eq(activityDate))).thenReturn(personUpdater);

        MRSPerson actualPerson = new PersonAdapter(personUpdaterFactory).createPerson(activity, beneficiarySegment);

        MRSPerson expectedPerson = personArgumentCaptor.getValue();
        assertEquals(expectedPerson, actualPerson);

        verify(personUpdater, never()).setDead(false);
        verify(personUpdater, never()).setBirthDateEstimated(false);

        assertFalse(expectedPerson.isDead());
        assertFalse(expectedPerson.getBirthDateEstimated());
    }

    @Test
    public void shouldCreateAPersonAndSetAllFields() {

        DateTime dob = new DateTime(2004, 12, 12, 0, 0);
        DateTime deathDate = new DateTime(2013, 11, 12, 0, 0);
        String gender = "mygender";
        String firstName = "myfirstnmae";
        String lastName = "mylastname";
        String middleName = "mymiddlename";
        String preferredName = "mypreferredname";
        String address = "myaddress";
        Integer age = 12;

        MRSAttribute mrsAttribute1 = new MRSAttributeDto("attr1", "value1");
        MRSAttribute mrsAttribute2 = new MRSAttributeDto("attr2", "value2");
        List<MRSAttribute> mrsAttributes = Arrays.asList(mrsAttribute1, mrsAttribute2);

        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);
        MRSRegistrationActivity activity = mock(MRSRegistrationActivity.class);
        when(activity.getValueFor(GENDER_FIELD, beneficiarySegment, String.class)).thenReturn(gender);
        when(activity.getValueFor(DOB_FIELD, beneficiarySegment, DateTime.class)).thenReturn(dob);
        when(activity.getValueFor(FIRST_NAME_FIELD, beneficiarySegment, String.class)).thenReturn(firstName);
        when(activity.getValueFor(LAST_NAME_FIELD, beneficiarySegment, String.class)).thenReturn(lastName);
        when(activity.getValueFor(MIDDLE_NAME_FIELD, beneficiarySegment, String.class)).thenReturn(middleName);
        when(activity.getValueFor(PREFERRED_NAME_FIELD, beneficiarySegment, String.class)).thenReturn(preferredName);
        when(activity.getValueFor(ADDRESS_FIELD, beneficiarySegment, String.class)).thenReturn(address);
        when(activity.getValueFor(AGE_FIELD, beneficiarySegment, Integer.class)).thenReturn(age);
        when(activity.getValueFor(BIRTH_DATE_ESTIMATED_FIELD, beneficiarySegment, Boolean.class)).thenReturn(true);
        when(activity.getValueFor(IS_DEAD_FIELD, beneficiarySegment, Boolean.class)).thenReturn(true);
        when(activity.getValueFor(DEATH_DATE_FIELD, beneficiarySegment, DateTime.class)).thenReturn(deathDate);
        when(activity.getMRSAttributes(beneficiarySegment)).thenReturn(mrsAttributes);

        MRSPersonDto expectedPerson = new MRSPersonDto();
        PersonUpdater personUpdater = mock(PersonUpdater.class);
        DateTime activityDate = new DateTime(2000, 1, 1, 1, 1, 1);
        when(activity.getActivityDate(beneficiarySegment)).thenReturn(activityDate);

        ArgumentCaptor<MRSPerson> personArgumentCaptor = ArgumentCaptor.forClass(MRSPerson.class);
        when(personUpdaterFactory.getPersonUpdater(personArgumentCaptor.capture(), eq(activityDate))).thenReturn(personUpdater);

        MRSPerson actualPerson = new PersonAdapter(personUpdaterFactory).createPerson(activity, beneficiarySegment);

        assertEquals(personArgumentCaptor.getValue(), actualPerson);

        verify(personUpdater).setGender(gender);
        verify(personUpdater).setDateOfBirth(dob);
        verify(personUpdater).setFirstName(firstName);
        verify(personUpdater).setLastName(lastName);
        verify(personUpdater).setMiddleName(middleName);
        verify(personUpdater).setPreferredName(preferredName);
        verify(personUpdater).setAddress(address);
        verify(personUpdater).setAge(age);
        verify(personUpdater).setBirthDateEstimated(true);
        verify(personUpdater).setDead(true);
        verify(personUpdater).setDeathDate(deathDate);

        verify(personUpdater).addAttribute(mrsAttribute1);
        verify(personUpdater).addAttribute(mrsAttribute2);
    }


    @Test
    public void shouldUpdateAPersonAndSetAllFields() {

        DateTime dob = new DateTime(2004, 12, 12, 0, 0);
        DateTime deathDate = new DateTime(2013, 11, 12, 0, 0);
        String gender = "mygender";
        String firstName = "myfirstnmae";
        String lastName = "mylastname";
        String middleName = "mymiddlename";
        String preferredName = "mypreferredname";
        String address = "myaddress";
        Integer age = 12;

        MRSAttribute mrsAttribute1 = new MRSAttributeDto("attr1", "value1");
        MRSAttribute mrsAttribute2 = new MRSAttributeDto("attr2", "value2");
        List<MRSAttribute> mrsAttributes = Arrays.asList(mrsAttribute1, mrsAttribute2);

        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);
        MRSRegistrationActivity activity = mock(MRSRegistrationActivity.class);
        when(activity.getValueFor(GENDER_FIELD, beneficiarySegment, String.class)).thenReturn(gender);
        when(activity.getValueFor(DOB_FIELD, beneficiarySegment, DateTime.class)).thenReturn(dob);
        when(activity.getValueFor(FIRST_NAME_FIELD, beneficiarySegment, String.class)).thenReturn(firstName);
        when(activity.getValueFor(LAST_NAME_FIELD, beneficiarySegment, String.class)).thenReturn(lastName);
        when(activity.getValueFor(MIDDLE_NAME_FIELD, beneficiarySegment, String.class)).thenReturn(middleName);
        when(activity.getValueFor(PREFERRED_NAME_FIELD, beneficiarySegment, String.class)).thenReturn(preferredName);
        when(activity.getValueFor(ADDRESS_FIELD, beneficiarySegment, String.class)).thenReturn(address);
        when(activity.getValueFor(AGE_FIELD, beneficiarySegment, Integer.class)).thenReturn(age);
        when(activity.getValueFor(BIRTH_DATE_ESTIMATED_FIELD, beneficiarySegment, Boolean.class)).thenReturn(true);
        when(activity.getValueFor(IS_DEAD_FIELD, beneficiarySegment, Boolean.class)).thenReturn(true);
        when(activity.getValueFor(DEATH_DATE_FIELD, beneficiarySegment, DateTime.class)).thenReturn(deathDate);
        when(activity.getMRSAttributes(beneficiarySegment)).thenReturn(mrsAttributes);

        MRSPersonDto person = new MRSPersonDto();
        PersonUpdater personUpdater = mock(PersonUpdater.class);
        DateTime activityDate = new DateTime(2000, 1, 1, 1, 1, 1);
        when(activity.getActivityDate(beneficiarySegment)).thenReturn(activityDate);

        ArgumentCaptor<MRSPerson> personArgumentCaptor = ArgumentCaptor.forClass(MRSPerson.class);
        when(personUpdaterFactory.getPersonUpdater(personArgumentCaptor.capture(), eq(activityDate))).thenReturn(personUpdater);

        new PersonAdapter(personUpdaterFactory).updatePerson(person, activity, beneficiarySegment);

        assertEquals(person, personArgumentCaptor.getValue());

        verify(personUpdater).setGender(gender);
        verify(personUpdater).setDateOfBirth(dob);
        verify(personUpdater).setFirstName(firstName);
        verify(personUpdater).setLastName(lastName);
        verify(personUpdater).setMiddleName(middleName);
        verify(personUpdater).setPreferredName(preferredName);
        verify(personUpdater).setAddress(address);
        verify(personUpdater).setAge(age);
        verify(personUpdater).setBirthDateEstimated(true);
        verify(personUpdater).setDead(true);
        verify(personUpdater).setDeathDate(deathDate);

        verify(personUpdater).addAttribute(mrsAttribute1);
        verify(personUpdater).addAttribute(mrsAttribute2);
    }
}
