package org.motechproject.mapper.builder;

import com.google.common.collect.HashMultimap;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.HashMap;
import java.util.Map;

public class FormBuilder {

    private CommcareForm form;

    public FormBuilder(String topFormElementName) {
        this(new FormValueElement());
        FormValueElement formValueElement = form.getForm();
        formValueElement.setElementName(topFormElementName);
        HashMultimap<String, FormValueElement> subElements = new HashMultimap<>();
        formValueElement.setSubElements(subElements);
    }

    public FormBuilder(FormValueElement topFormElement) {
        form = new CommcareForm();
        Map<String, String> meta = new HashMap<>();
        form.setMetadata(meta);
        form.setForm(topFormElement);
    }

    public FormBuilder withSubElement(String field, String value) {
        FormValueElement element = new FormValueElement();
        element.setValue(value);
        element.setElementName(field);
        this.form.getForm().getSubElements().put(field, element);
        return this;
    }

    public CommcareForm getForm() {
        return form;
    }

    public FormBuilder withSubElement(FormValueElement subElement) {
        form.getForm().getSubElements().put(subElement.getElementName(), subElement);
        return this;
    }

    public FormBuilder withMeta(String key, String value) {
        form.getMetadata().put(key, value);
        return this;
    }
}
