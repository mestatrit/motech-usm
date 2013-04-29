package org.motechproject.mapper.listeners;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.commcare.service.CommcareFormService;
import org.motechproject.event.MotechEvent;
import org.motechproject.mapper.adapters.impl.AllFormsAdapter;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class FormListenerTest {

    @Mock
    private CommcareFormService commcareFormService;
    @Mock
    private AllFormsAdapter allFormsAdapter;
    private FormListener formListener;

    @Before
    public void setup() throws Exception {
        formListener = new FormListener(commcareFormService, allFormsAdapter);
    }

    @Test
    public void testShouldRetrieveFormAndAdaptItForAValidForm() {
        String formId = "formId";
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(EventDataKeys.FORM_ID, formId);
        MotechEvent event = new MotechEvent("subject", properties);
        CommcareForm commcareForm = new CommcareForm();
        commcareForm.setForm(new FormValueElement());
        when(commcareFormService.retrieveForm(formId)).thenReturn(commcareForm);

        formListener.handleFormEvent(event);

        verify(commcareFormService).retrieveForm(formId);
        verify(allFormsAdapter).adaptForm(commcareForm);
    }

    @Test
    public void testShouldNotRetrieveFormIfFormIdIsBlank() {
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(EventDataKeys.FORM_ID, "");
        MotechEvent event = new MotechEvent("subject", properties);

        formListener.handleFormEvent(event);

        verify(commcareFormService, never()).retrieveForm(anyString());
        verify(allFormsAdapter, never()).adaptForm(any(CommcareForm.class));
    }

    @Test
    public void testShouldNotAdaptIfThereIsNoForm() {
        String formId = "formId";
        HashMap<String, Object> properties = new HashMap<>();
        properties.put(EventDataKeys.FORM_ID, formId);
        MotechEvent event = new MotechEvent("subject", properties);
        CommcareForm commcareForm = new CommcareForm();
        when(commcareFormService.retrieveForm(formId)).thenReturn(commcareForm);

        formListener.handleFormEvent(event);

        verify(commcareFormService).retrieveForm(formId);
        verify(allFormsAdapter, never()).adaptForm(any(CommcareForm.class));
    }
}
