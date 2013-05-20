package org.motechproject.mapper.listeners;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
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

import java.util.HashMap;
import java.util.Map;

@Component
public class FormListener {

    public static final String DEFAULT_ROOT_ELEMENT = "form";
    private AllFormsAdapter formsAdapter;
    private Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

    @Autowired
    public FormListener(AllFormsAdapter formsAdapter) {
        this.formsAdapter = formsAdapter;
    }

    @MotechListener(subjects = EventSubjects.FORMS_EVENT)
    public void handleFullFormEvent(MotechEvent motechEvent) {
        CommcareForm commcareForm = constructForm(motechEvent);
        formsAdapter.adaptForm(commcareForm);
    }

    private CommcareForm constructForm(MotechEvent motechEvent) {
        Map<String, Object> eventParameters = motechEvent.getParameters();
        Map<String, Map<String, Object>> subElements = (Map<String, Map<String, Object>>) eventParameters.get(EventDataKeys.SUB_ELEMENTS);
        Map<String, String> attributes = (Map<String, String>) eventParameters.get(EventDataKeys.ATTRIBUTES);
        FormValueElement rootElement = new FormValueElement();
        rootElement.setElementName(DEFAULT_ROOT_ELEMENT);
        rootElement.setSubElements(getSubElements(subElements));
        rootElement.setAttributes(attributes);
        Map<String, String> formMetaData = getFormMeta(rootElement);

        CommcareForm form = new CommcareForm();
        form.setForm(rootElement);
        form.setMetadata(formMetaData);
        return form;
    }

    private Multimap<String, FormValueElement> getSubElements(Map<String, Map<String, Object>> elements) {
        if (elements == null) return null;
        Multimap<String, FormValueElement> subElements = new HashMultimap<>();
        for (Map.Entry<String, Map<String, Object>> elementSet : elements.entrySet()) {
            FormValueElement formValueElement = new FormValueElement();
            Map<String, Object> element = elementSet.getValue();
            formValueElement.setElementName((String) element.get(EventDataKeys.ELEMENT_NAME));
            formValueElement.setAttributes((Map<String, String>) element.get(EventDataKeys.ATTRIBUTES));
            formValueElement.setValue((String) element.get(EventDataKeys.VALUE));
            formValueElement.setSubElements(getSubElements((Map<String, Map<String, Object>>) element.get(EventDataKeys.SUB_ELEMENTS)));
            subElements.put(elementSet.getKey(), formValueElement);
        }
        return subElements;
    }

    private Map<String, String> getFormMeta(FormValueElement rootElement) {
        Map<String, String> formMeta = new HashMap<>();
        FormValueElement metaElement = rootElement.getElement("meta");
        for (Map.Entry<String, FormValueElement> metaEntry : metaElement.getSubElements().entries()) {
            formMeta.put(metaEntry.getKey(), metaEntry.getValue().getValue());
        }
        return formMeta;
    }
}
