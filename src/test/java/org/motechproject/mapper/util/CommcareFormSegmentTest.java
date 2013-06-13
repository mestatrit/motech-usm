package org.motechproject.mapper.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueAttribute;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class CommcareFormSegmentTest {

    @Mock
    private AllElementSearchStrategies allElementSearchStrategies;
    @Mock
    private FormValueElement startElement;
    @Mock
    private CommcareForm commcareForm;
    @Mock
    private  FormValueElement rootElement;

    private CommcareFormSegment commcareFormSegment;

    @Before
    public void setUp() {
        initMocks(this);
        when(commcareForm.getForm()).thenReturn(rootElement);
        commcareFormSegment = new CommcareFormSegment(commcareForm, startElement, null, allElementSearchStrategies);
    }

    @Test
    public void shouldReturnCommaSeparatedListIfLookupPathIsForArray() {
        when(allElementSearchStrategies.search("path", startElement, rootElement, null)).thenReturn(Arrays.<FormNode>asList(new FormValueAttribute("value1"), new FormValueAttribute("value2")));
        assertEquals("value1,value2", commcareFormSegment.search("path[]").getValue());
    }
}
