package org.motechproject.mapper.listeners;


import org.motechproject.commcare.builder.CommcareFormBuilder;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mapper.adapters.impl.AllFormsAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FormListener {

    private AllFormsAdapter formsAdapter;

    @Autowired
    public FormListener(AllFormsAdapter formsAdapter) {
        this.formsAdapter = formsAdapter;
    }

    @MotechListener(subjects = EventSubjects.FORMS_EVENT)
    public void handleFullFormEvent(MotechEvent motechEvent) {
        CommcareForm commcareForm = new CommcareFormBuilder().buildFrom(motechEvent);
        formsAdapter.adaptForm(commcareForm);
    }
}
