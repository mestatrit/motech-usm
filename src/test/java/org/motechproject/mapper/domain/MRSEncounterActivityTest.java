package org.motechproject.mapper.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commcare.domain.FormValueAttribute;
import org.motechproject.mapper.util.CommcareFormSegment;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.*;

public class MRSEncounterActivityTest {

    @Test
    public void shouldGetEncounterDate() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSEncounterActivity activity = new MRSEncounterActivity();
        HashMap<String, String> encounterMappings = new HashMap<String, String>(){{
            put("encounterDate", "dateFieldPath");
        }};
        activity.setEncounterMappings(encounterMappings);

        DateTime activityDate = new DateTime(2000, 1, 1, 1, 1, 1);
        when(beneficiarySegment.search("dateFieldPath")).thenReturn(new FormValueAttribute(activityDate.toString()));

        assertEquals(activityDate.toString(), activity.getActivityDate(beneficiarySegment).toString());
    }

    @Test
    public void shouldReturnCurrentDateAsEncounterDateIfEncounterMappingsAreNull() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSEncounterActivity activity = new MRSEncounterActivity();
        activity.setEncounterMappings(null);

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment, never()).search(anyString());
    }

    @Test
    public void shouldReturnCurrentDateAsEncounterDateIfEncounterDateFieldIsNotProvided() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSEncounterActivity activity = new MRSEncounterActivity();
        HashMap<String, String> encounterMappings = new HashMap<String, String>(){{
        }};
        activity.setEncounterMappings(encounterMappings);

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment, never()).search(anyString());
    }

    @Test
    public void shouldReturnCurrentDateAsEncounterDateIfEncounterDateFieldIsEmpty() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSEncounterActivity activity = new MRSEncounterActivity();
        HashMap<String, String> encounterMappings = new HashMap<String, String>(){{
            put("encounterDate", "");
        }};
        activity.setEncounterMappings(encounterMappings);

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment, never()).search(anyString());
    }

    @Test
    public void shouldReturnCurrentDateAsEncounterDateIfDateFieldIsNotFoundInForm() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSEncounterActivity activity = new MRSEncounterActivity();
        HashMap<String, String> encounterMappings = new HashMap<String, String>(){{
            put("encounterDate", "dateFieldPath");
        }};
        activity.setEncounterMappings(encounterMappings);
        when(beneficiarySegment.search("dateFieldPath")).thenReturn(null);

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment).search("dateFieldPath");
    }

    @Test
    public void shouldReturnCurrentDateAsEncounterDateIfDateFieldIsNullInForm() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSEncounterActivity activity = new MRSEncounterActivity();
        HashMap<String, String> encounterMappings = new HashMap<String, String>(){{
            put("encounterDate", "dateFieldPath");
        }};
        activity.setEncounterMappings(encounterMappings);
        when(beneficiarySegment.search("dateFieldPath")).thenReturn(new FormValueAttribute(null));

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment).search("dateFieldPath");
    }

    @Test
    public void shouldReturnCurrentDateAsEncounterDateIfDateFieldIsEmptyInForm() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSEncounterActivity activity = new MRSEncounterActivity();
        HashMap<String, String> encounterMappings = new HashMap<String, String>(){{
            put("encounterDate", "dateFieldPath");
        }};
        activity.setEncounterMappings(encounterMappings);
        when(beneficiarySegment.search("dateFieldPath")).thenReturn(new FormValueAttribute(""));

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment).search("dateFieldPath");
    }
}
