package org.motechproject.mapper.listeners;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.commcare.service.CommcareFormService;
import org.motechproject.event.MotechEvent;
import org.motechproject.mapper.adapters.impl.AllFormsAdapter;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class FormListenerTest {

    @Mock
    private CommcareFormService commcareFormService;
    @Mock
    private AllFormsAdapter allFormsAdapter;
    private FormListener formListener;

    @Before
    public void setup() throws Exception {
        formListener = new FormListener(allFormsAdapter);
    }

    @Test
    public void shouldAdaptForFullFormEvent() {
        String elementName = "form";
        MotechEvent motechEvent = new MotechEvent();
        HashMap<String, String> attributes = new HashMap<>();
        motechEvent.getParameters().put(EventDataKeys.ELEMENT_NAME, elementName);
        motechEvent.getParameters().put(EventDataKeys.ATTRIBUTES, attributes);
        Multimap<String, Map<String, Object>> subElements = new LinkedHashMultimap<>();
        Map<String, Object> metaElement = new HashMap<>();
        metaElement.put(EventDataKeys.ELEMENT_NAME, "meta");
        Multimap<String, Map<String, Object>> metaSubElements = new LinkedHashMultimap<>();
        HashMap<String, Object> metaElement1 = new HashMap<>();
        String meta1 = "meta1";
        metaElement1.put(EventDataKeys.ELEMENT_NAME, meta1);
        String metaValue1 = "metaValue1";
        metaElement1.put(EventDataKeys.VALUE, metaValue1);
        metaSubElements.put(meta1, metaElement1);
        HashMap<String, Object> metaElement2 = new HashMap<>();
        String meta2 = "meta2";
        metaElement2.put(EventDataKeys.ELEMENT_NAME, meta2);
        String metaValue2 = "metaValue2";
        metaElement2.put(EventDataKeys.VALUE, metaValue2);
        metaSubElements.put(meta2, metaElement2);
        metaElement.put(EventDataKeys.SUB_ELEMENTS, metaSubElements);
        subElements.put("meta", metaElement);
        motechEvent.getParameters().put(EventDataKeys.SUB_ELEMENTS, subElements);

        formListener.handleFullFormEvent(motechEvent);

        ArgumentCaptor<CommcareForm> formCaptor = ArgumentCaptor.forClass(CommcareForm.class);
        verify(allFormsAdapter).adaptForm(formCaptor.capture());
        CommcareForm actualForm = formCaptor.getValue();
        FormValueElement rootElement = actualForm.getForm();
        assertEquals(elementName, rootElement.getElementName());
        assertEquals(attributes, rootElement.getAttributes());
        assertEquals(2, actualForm.getMetadata().size());
        assertEquals(metaValue1, actualForm.getMetadata().get(meta1));
        assertEquals(metaValue2, actualForm.getMetadata().get(meta2));
    }
}
