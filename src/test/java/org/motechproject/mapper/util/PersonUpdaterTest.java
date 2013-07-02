package org.motechproject.mapper.util;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mapper.service.PersonFieldUpdateStrategy;
import org.motechproject.mrs.domain.MRSAttribute;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.model.MRSAttributeDto;
import org.motechproject.mrs.model.MRSPersonDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

public class PersonUpdaterTest {

    @Mock
    private PersonFieldUpdateStrategy updateStrategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldUpdateExistingAttribute() {
        MRSPersonDto person = new MRSPersonDto();
        MRSAttribute existingAttribute1 = new MRSAttributeDto("firstName", "myOldFirstName");
        MRSAttribute existingAttribute2 = new MRSAttributeDto("lastName", "myOldLastName");
        person.setAttributes(new ArrayList<>(Arrays.asList(existingAttribute1, existingAttribute2)));

        when(updateStrategy.canUpdate("firstName", "myNewFirstName")).thenReturn(true);

        new PersonUpdater(person,  updateStrategy).addAttribute(new MRSAttributeDto("firstName", "myNewFirstName"));

        verify(updateStrategy).canUpdate("firstName", "myNewFirstName");
        verify(updateStrategy).markUpdated("firstName");

        List<MRSAttribute> updatedAttributes = person.getAttributes();

        assertEquals(2, updatedAttributes.size());
        assertReflectionEquals(new MRSAttributeDto("firstName", "myNewFirstName"), person.getAttributes().get(0));
        assertReflectionEquals(new MRSAttributeDto("lastName", "myOldLastName"), person.getAttributes().get(1));
    }

    @Test
    public void shouldCreateNewAttributeIfNotPresent() {
        MRSPersonDto person = new MRSPersonDto();
        MRSAttribute existingAttribute2 = new MRSAttributeDto("lastName", "myOldLastName");
        person.setAttributes(new ArrayList<>(Arrays.asList(existingAttribute2)));

        when(updateStrategy.canUpdate("firstName", "myNewFirstName")).thenReturn(true);
        new PersonUpdater(person,  updateStrategy).addAttribute(new MRSAttributeDto("firstName", "myNewFirstName"));

        verify(updateStrategy).canUpdate("firstName", "myNewFirstName");
        verify(updateStrategy).markUpdated("firstName");

        List<MRSAttribute> updatedAttributes = person.getAttributes();

        assertEquals(2, updatedAttributes.size());
        assertReflectionEquals(new MRSAttributeDto("lastName", "myOldLastName"), person.getAttributes().get(0));
        assertReflectionEquals(new MRSAttributeDto("firstName", "myNewFirstName"), person.getAttributes().get(1));
    }

    @Test
    public void shouldCreateNewAttributeIfNoAttributesArePresent() {
        MRSPersonDto person = new MRSPersonDto();
        person.setAttributes(null);

        when(updateStrategy.canUpdate("firstName", "myNewFirstName")).thenReturn(true);

        new PersonUpdater(person,  updateStrategy).addAttribute(new MRSAttributeDto("firstName", "myNewFirstName"));

        verify(updateStrategy).canUpdate("firstName", "myNewFirstName");
        verify(updateStrategy).markUpdated("firstName");

        List<MRSAttribute> updatedAttributes = person.getAttributes();

        assertEquals(1, updatedAttributes.size());
        assertReflectionEquals(new MRSAttributeDto("firstName", "myNewFirstName"), person.getAttributes().get(0));
    }

    @Test
    public void shouldNotUpdateAttributeIfUpdateStrategyReturnFalse() {
        MRSPersonDto person = new MRSPersonDto();
        MRSAttribute existingAttribute1 = new MRSAttributeDto("firstName", "myOldFirstName");
        MRSAttribute existingAttribute2 = new MRSAttributeDto("lastName", "myOldLastName");
        person.setAttributes(new ArrayList<>(Arrays.asList(existingAttribute1, existingAttribute2)));

        when(updateStrategy.canUpdate("firstName", "myNewFirstName")).thenReturn(false);

        new PersonUpdater(person,  updateStrategy).addAttribute(new MRSAttributeDto("firstName", "myNewFirstName"));

        verify(updateStrategy).canUpdate("firstName", "myNewFirstName");
        verify(updateStrategy, never()).markUpdated("firstName");

        List<MRSAttribute> updatedAttributes = person.getAttributes();

        assertEquals(2, updatedAttributes.size());
        assertReflectionEquals(new MRSAttributeDto("firstName", "myOldFirstName"), person.getAttributes().get(0));
        assertReflectionEquals(new MRSAttributeDto("lastName", "myOldLastName"), person.getAttributes().get(1));
    }

    @Test
    public void shouldNotUpdatePersonFieldsIfUpdateStrategyReturnsFalse() {
        MRSPerson person = mock(MRSPerson.class);
        DateTime deathDate = new DateTime(2000, 12, 12, 12, 12);
        DateTime dateOfBirth = new DateTime(2000, 12, 12, 12, 11);

        when(updateStrategy.canUpdate(anyString(), any(Object.class))).thenReturn(false);

        new PersonUpdater(person, updateStrategy).setAddress("newAddress");
        new PersonUpdater(person, updateStrategy).setAge(42);
        new PersonUpdater(person, updateStrategy).setBirthDateEstimated(true);
        new PersonUpdater(person, updateStrategy).setDead(true);
        new PersonUpdater(person, updateStrategy).setDeathDate(deathDate);
        new PersonUpdater(person, updateStrategy).setDateOfBirth(dateOfBirth);
        new PersonUpdater(person, updateStrategy).setFirstName("newFirstName");
        new PersonUpdater(person, updateStrategy).setGender("newGender");
        new PersonUpdater(person, updateStrategy).setLastName("newLastName");
        new PersonUpdater(person, updateStrategy).setMiddleName("newMiddleName");
        new PersonUpdater(person, updateStrategy).setPreferredName("newPreferredName");

        verify(updateStrategy).canUpdate("address", "newAddress");
        verify(updateStrategy).canUpdate("age", 42);
        verify(updateStrategy).canUpdate("birthDateEstimated", true);
        verify(updateStrategy).canUpdate("dead", true);
        verify(updateStrategy).canUpdate("deathDate", deathDate);
        verify(updateStrategy).canUpdate("dateOfBirth", dateOfBirth);
        verify(updateStrategy).canUpdate("firstName", "newFirstName");
        verify(updateStrategy).canUpdate("gender", "newGender");
        verify(updateStrategy).canUpdate("lastName", "newLastName");
        verify(updateStrategy).canUpdate("middleName", "newMiddleName");
        verify(updateStrategy).canUpdate("preferredName", "newPreferredName");

        verifyNoMoreInteractions(updateStrategy);

        verifyZeroInteractions(person);
    }

    @Test
    public void shouldUpdatePersonFieldsIfUpdateStrategyReturnsTrue() {
        MRSPerson person = mock(MRSPerson.class);
        DateTime deathDate = new DateTime(2000, 12, 12, 12, 12);
        DateTime dateOfBirth = new DateTime(2000, 12, 12, 12, 11);

        when(updateStrategy.canUpdate(anyString(), any(Object.class))).thenReturn(true);

        new PersonUpdater(person, updateStrategy).setAddress("newAddress");
        new PersonUpdater(person, updateStrategy).setAge(42);
        new PersonUpdater(person, updateStrategy).setBirthDateEstimated(true);
        new PersonUpdater(person, updateStrategy).setDead(true);
        new PersonUpdater(person, updateStrategy).setDeathDate(deathDate);
        new PersonUpdater(person, updateStrategy).setDateOfBirth(dateOfBirth);
        new PersonUpdater(person, updateStrategy).setFirstName("newFirstName");
        new PersonUpdater(person, updateStrategy).setGender("newGender");
        new PersonUpdater(person, updateStrategy).setLastName("newLastName");
        new PersonUpdater(person, updateStrategy).setMiddleName("newMiddleName");
        new PersonUpdater(person, updateStrategy).setPreferredName("newPreferredName");

        verify(updateStrategy).canUpdate("address", "newAddress");
        verify(updateStrategy).canUpdate("age", 42);
        verify(updateStrategy).canUpdate("birthDateEstimated", true);
        verify(updateStrategy).canUpdate("dead", true);
        verify(updateStrategy).canUpdate("deathDate", deathDate);
        verify(updateStrategy).canUpdate("dateOfBirth", dateOfBirth);
        verify(updateStrategy).canUpdate("firstName", "newFirstName");
        verify(updateStrategy).canUpdate("gender", "newGender");
        verify(updateStrategy).canUpdate("lastName", "newLastName");
        verify(updateStrategy).canUpdate("middleName", "newMiddleName");
        verify(updateStrategy).canUpdate("preferredName", "newPreferredName");

        verify(updateStrategy).markUpdated("address");
        verify(updateStrategy).markUpdated("age");
        verify(updateStrategy).markUpdated("birthDateEstimated");
        verify(updateStrategy).markUpdated("dead");
        verify(updateStrategy).markUpdated("deathDate");
        verify(updateStrategy).markUpdated("dateOfBirth");
        verify(updateStrategy).markUpdated("firstName");
        verify(updateStrategy).markUpdated("gender");
        verify(updateStrategy).markUpdated("lastName");
        verify(updateStrategy).markUpdated("middleName");
        verify(updateStrategy).markUpdated("preferredName");

        verifyNoMoreInteractions(updateStrategy);

        verify(person).setAddress("newAddress");
        verify(person).setAge(42);
        verify(person).setBirthDateEstimated(true);
        verify(person).setDead(true);
        verify(person).setDeathDate(deathDate);
        verify(person).setDateOfBirth(dateOfBirth);
        verify(person).setFirstName("newFirstName");
        verify(person).setGender("newGender");
        verify(person).setLastName("newLastName");
        verify(person).setMiddleName("newMiddleName");
        verify(person).setPreferredName("newPreferredName");

        verifyNoMoreInteractions(person);
    }
}
