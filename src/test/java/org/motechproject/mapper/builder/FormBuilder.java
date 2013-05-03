package org.motechproject.mapper.builder;

import com.google.common.collect.HashMultimap;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;

public class FormBuilder {

    private CommcareForm form;

    public FormBuilder() {
        form = new CommcareForm();
        FormValueElement formValueElement = new FormValueElement();
        HashMultimap<String, FormValueElement> subElements = new HashMultimap<>();
        formValueElement.setSubElements(subElements);
        form.setForm(formValueElement);
    }

    public FormBuilder with(String field, String value) {
        FormValueElement element = new FormValueElement();
        element.setValue(value);
        element.setElementName(field);
        this.form.getForm().getSubElements().put(field, element);
        return this;
    }

    public CommcareForm getForm() {
        return form;
    }
}
