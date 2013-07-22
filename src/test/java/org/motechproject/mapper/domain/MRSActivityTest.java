package org.motechproject.mapper.domain;

import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.util.CommcareFormSegment;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MRSActivityTest {

    @Test
    public void shouldReturnDateModifiedFromMappingsIfItIsPresent() {
        MRSActivity mrsActivity = new MRSActivity();
        CommcareFormSegment formSegment = mock(CommcareFormSegment.class);
        final String expectedDate = DateTime.now().minusDays(5).toString();
        final String searchPath = "searchPath";
        Map<String, String> mappings = new HashMap<String, String>() {{
            put(FormMappingConstants.REGISTRATION_DATE_FIELD, searchPath);
        }};
        when(formSegment.search(searchPath)).thenReturn(new FormNode() {
            @Override
            public String getValue() {
                return expectedDate;
            }
        });

        DateTime activityDate = mrsActivity.getActivityDate(formSegment, mappings, FormMappingConstants.REGISTRATION_DATE_FIELD);

        assertEquals(expectedDate, activityDate.toString());
    }

    @Test
    public void shouldReturnReceivedOnIfMappingIsNotPresent() {
        MRSActivity mrsActivity = new MRSActivity();
        CommcareFormSegment formSegment = mock(CommcareFormSegment.class);
        String receivedOn = "2013-07-15";
        when(formSegment.getReceivedOn()).thenReturn(receivedOn);

        DateTime activityDate = mrsActivity.getActivityDate(formSegment, null, FormMappingConstants.REGISTRATION_DATE_FIELD);

        assertEquals(DateTime.parse(receivedOn), activityDate);
    }

    @Test
    public void shouldReturnCurrentTimeIfMappingAndReceivedOnIsNotPresent() {
        MRSActivity mrsActivity = new MRSActivity();
        CommcareFormSegment formSegment = mock(CommcareFormSegment.class);
        when(formSegment.getReceivedOn()).thenReturn(null);

        DateTime activityDate = mrsActivity.getActivityDate(formSegment, new HashMap<String, String>(), FormMappingConstants.ENCOUNTER_DATE_FIELD);

        assertTrue(!activityDate.isAfterNow());
    }

}
