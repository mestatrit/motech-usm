package org.motechproject.mapper.listeners;

import org.apache.commons.lang.StringUtils;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.events.constants.EventDataKeys;
import org.motechproject.commcare.events.constants.EventSubjects;
import org.motechproject.commcare.service.CommcareFormService;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mapper.adapters.impl.AllFormsAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FormListener {

    private CommcareFormService formService;
    private AllFormsAdapter formsAdapter;
    private Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

    @Autowired
    public FormListener(CommcareFormService formService, AllFormsAdapter formsAdapter) {
        this.formService = formService;
        this.formsAdapter = formsAdapter;
    }

    @MotechListener(subjects = EventSubjects.FORM_STUB_EVENT)
    public void handleFormEvent(MotechEvent event) {

        Map<String, Object> parameters = event.getParameters();

        String formId = (String) parameters.get(EventDataKeys.FORM_ID);

        logger.info("Received form: " + formId);

        CommcareForm form = null;

        if (StringUtils.isBlank(formId)) {
            logger.info("Form Id was null");
            return;
        }
        form = formService.retrieveForm(formId);

        FormValueElement rootElement = null;
        if (form == null) {
            logger.error("Could not fetch the form");
            return;
        }
        rootElement = form.getForm();
        if (rootElement != null) {
            formsAdapter.adaptForm(form);
        } else {
            logger.info("Unable to adapt form");
        }
    }
}
