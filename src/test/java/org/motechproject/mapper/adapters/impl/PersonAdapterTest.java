package org.motechproject.mapper.adapters.impl;


import org.joda.time.DateTime;
import org.joda.time.Years;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commcare.domain.FormValueAttribute;
import org.motechproject.mapper.builder.RegistrationActivityBuilder;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.service.PersonFieldUpdateStrategy;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSAttributeDto;
import org.motechproject.mrs.model.MRSPersonDto;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.now;
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
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class PersonAdapterTest {

    @Mock
    private PersonFieldUpdateStrategy updateStrategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldDefaultValuesWhenCreatingAPerson() {
        String nameValueInForm = "amy";

        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);
        MRSRegistrationActivity activity = mock(MRSRegistrationActivity.class);
        when(activity.getValueFor(FIRST_NAME_FIELD, beneficiarySegment, String.class)).thenReturn(nameValueInForm);

        MRSPerson actualPerson = new PersonAdapter().createPerson(activity, beneficiarySegment, updateStrategy);

        assertFalse(actualPerson.isDead());
        assertFalse(actualPerson.getBirthDateEstimated());
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
        List<MRSAttribute> mrsAttributes = new ArrayList<>();

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

        Integer expectedAge = Years.yearsBetween(dob, new DateTime(now())).getYears();

        when(updateStrategy.canUpdateField(anyString(), anyString())).thenReturn(true);

        MRSPerson actualPerson = new PersonAdapter().createPerson(activity, beneficiarySegment, updateStrategy);
        assertEquals(gender, actualPerson.getGender());
        assertEquals(dob, actualPerson.getDateOfBirth());
        assertEquals(firstName, actualPerson.getFirstName());
        assertEquals(lastName, actualPerson.getLastName());
        assertEquals(middleName, actualPerson.getMiddleName());
        assertEquals(preferredName, actualPerson.getPreferredName());
        assertEquals(address, actualPerson.getAddress());
        assertEquals(expectedAge, actualPerson.getAge());
        assertTrue(actualPerson.getBirthDateEstimated());
        assertTrue(actualPerson.isDead());
        assertEquals(deathDate, actualPerson.getDeathDate());
    }

    @Test
    public void shouldNotUpdateIfFieldIsNull() {
        String id = "myid";
        DateTime oldDob = new DateTime(2014, 12, 12, 0, 0);
        DateTime dob = null;
        DateTime deathDate = new DateTime(2013, 11, 12, 0, 0);
        String gender = "mygender";
        String firstName = "myfirstnmae";
        String lastName = null;
        String middleName = "mymiddlename";
        String preferredName = "mypreferredname";
        String address = null;
        Integer age = 1;
        List<MRSAttribute> mrsAttributes = new ArrayList<>();

        MRSPerson person = new MRSPersonDto();
        person.setPersonId(id);
        person.setLastName("oldlastname");
        person.setGender("oldgender");
        person.setMiddleName("oldmiddlename");
        person.setDead(true);
        person.setBirthDateEstimated(false);
        person.setDateOfBirth(oldDob);

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
        when(activity.getValueFor(BIRTH_DATE_ESTIMATED_FIELD, beneficiarySegment, Boolean.class)).thenReturn(null);
        when(activity.getValueFor(IS_DEAD_FIELD, beneficiarySegment, Boolean.class)).thenReturn(null);
        when(activity.getValueFor(DEATH_DATE_FIELD, beneficiarySegment, DateTime.class)).thenReturn(deathDate);
        when(activity.getMRSAttributes(beneficiarySegment)).thenReturn(mrsAttributes);

        when(updateStrategy.canUpdateField(anyString(), isNotNull())).thenReturn(true);
        when(updateStrategy.canUpdateField(anyString(), isNull())).thenReturn(false);
        new PersonAdapter().updatePerson(person, activity, beneficiarySegment, updateStrategy);

        assertEquals(id, person.getPersonId());
        assertEquals(gender, person.getGender());
        assertEquals(oldDob, person.getDateOfBirth());
        assertEquals(firstName, person.getFirstName());
        assertEquals("oldlastname", person.getLastName());
        assertEquals(middleName, person.getMiddleName());
        assertEquals(preferredName, person.getPreferredName());
        assertEquals(address, person.getAddress());
        assertFalse(person.getBirthDateEstimated());
        assertTrue(person.isDead());
        assertEquals(deathDate, person.getDeathDate());
    }

    @Test
    public void shouldUpdateAnAttributeIfExistsAlreadyOrAddNew() {
        MRSPersonDto person = new MRSPersonDto();
        final MRSAttribute existingAttribute1 = new MRSAttributeDto("firstName", "myOldFirstName");
        final MRSAttribute existingAttribute2 = new MRSAttributeDto("lastName", "myOldLastName");
        final MRSAttribute existingAttribute3 = new MRSAttributeDto("preferredName", "myOldPreferredName");
        person.setAttributes(new ArrayList<MRSAttribute>() {{
            add(existingAttribute1);
            add(existingAttribute2);
            add(existingAttribute3);
        }});

        CommcareFormSegment commcareFormSegment = mock(CommcareFormSegment.class);
        when(commcareFormSegment.search("fname")).thenReturn(new FormValueAttribute("myNewFirstName"));
        when(commcareFormSegment.search("ph")).thenReturn(new FormValueAttribute("myNewPhoneNumber"));
        when(commcareFormSegment.search("pfname")).thenReturn(new FormValueAttribute("myNewPreferredName"));

        when(updateStrategy.canUpdateField(eq("firstName"), any())).thenReturn(true);
        when(updateStrategy.canUpdateField(eq("phoneNumber"), any())).thenReturn(true);

        MRSRegistrationActivity registrationActivity = new RegistrationActivityBuilder()
                .withAttributes("firstName", "fname")
                .withAttributes("lastName", "lname")
                .withAttributes("preferredName", "pfname")
                .withAttributes("phoneNumber", "ph")
                .getActivity();
        new PersonAdapter().updatePerson(person, registrationActivity, commcareFormSegment, updateStrategy);

        assertEquals(4, person.getAttributes().size());
        assertReflectionEquals(new MRSAttributeDto("firstName", "myNewFirstName"), person.getAttributes().get(0));
        assertReflectionEquals(new MRSAttributeDto("lastName", "myOldLastName"), person.getAttributes().get(1));
        assertReflectionEquals(new MRSAttributeDto("preferredName", "myOldPreferredName"), person.getAttributes().get(2));
        assertReflectionEquals(new MRSAttributeDto("phoneNumber", "myNewPhoneNumber"), person.getAttributes().get(3));
    }
}
