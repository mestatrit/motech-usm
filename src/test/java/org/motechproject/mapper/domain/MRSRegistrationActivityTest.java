package org.motechproject.mapper.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commcare.domain.FormValueAttribute;
import org.motechproject.mapper.util.CommcareFormSegment;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class MRSRegistrationActivityTest {

    @Test
    public void shouldGetRegistrationDate() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSRegistrationActivity activity = new MRSRegistrationActivity();
        HashMap<String, String> registrationMappings = new HashMap<String, String>(){{
            put("registrationDate", "dateFieldPath");
        }};
        activity.setRegistrationMappings(registrationMappings);

        DateTime activityDate = new DateTime(2000, 1, 1, 1, 1, 1);
        when(beneficiarySegment.search("dateFieldPath")).thenReturn(new FormValueAttribute(activityDate.toString()));

        assertEquals(activityDate.toString(), activity.getActivityDate(beneficiarySegment).toString());
    }

    @Test
    public void shouldReturnCurrentDateAsRegistrationDateIfRegistrationMappingsAreNull() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSRegistrationActivity activity = new MRSRegistrationActivity();
        activity.setRegistrationMappings(null);

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment, never()).search(anyString());
    }

    @Test
    public void shouldReturnCurrentDateAsRegistrationDateIfRegistrationDateFieldIsNotProvided() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSRegistrationActivity activity = new MRSRegistrationActivity();
        HashMap<String, String> registrationMappings = new HashMap<String, String>(){{
        }};
        activity.setRegistrationMappings(registrationMappings);

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment, never()).search(anyString());
    }

    @Test
    public void shouldReturnCurrentDateAsRegistrationDateIfRegistrationDateFieldIsEmpty() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSRegistrationActivity activity = new MRSRegistrationActivity();
        HashMap<String, String> registrationMappings = new HashMap<String, String>(){{
            put("registrationDate", "");
        }};
        activity.setRegistrationMappings(registrationMappings);

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment, never()).search(anyString());
    }

    @Test
    public void shouldReturnCurrentDateAsRegistrationDateIfDateFieldIsNotFoundInForm() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSRegistrationActivity activity = new MRSRegistrationActivity();
        HashMap<String, String> registrationMappings = new HashMap<String, String>(){{
            put("registrationDate", "dateFieldPath");
        }};
        activity.setRegistrationMappings(registrationMappings);
        when(beneficiarySegment.search("dateFieldPath")).thenReturn(null);

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment).search("dateFieldPath");
    }

    @Test
    public void shouldReturnCurrentDateAsRegistrationDateIfDateFieldIsNullInForm() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSRegistrationActivity activity = new MRSRegistrationActivity();
        HashMap<String, String> registrationMappings = new HashMap<String, String>(){{
            put("registrationDate", "dateFieldPath");
        }};
        activity.setRegistrationMappings(registrationMappings);
        when(beneficiarySegment.search("dateFieldPath")).thenReturn(new FormValueAttribute(null));

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment).search("dateFieldPath");
    }

    @Test
    public void shouldReturnCurrentDateAsRegistrationDateIfDateFieldIsEmptyInForm() {
        CommcareFormSegment beneficiarySegment = mock(CommcareFormSegment.class);

        MRSRegistrationActivity activity = new MRSRegistrationActivity();
        HashMap<String, String> registrationMappings = new HashMap<String, String>(){{
            put("registrationDate", "dateFieldPath");
        }};
        activity.setRegistrationMappings(registrationMappings);
        when(beneficiarySegment.search("dateFieldPath")).thenReturn(new FormValueAttribute(""));

        DateTime now = DateTime.now();

        assertFalse(activity.getActivityDate(beneficiarySegment).isBefore(now));

        verify(beneficiarySegment).search("dateFieldPath");
    }
}
