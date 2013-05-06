package org.motechproject.mapper.builder;

import com.google.common.collect.HashMultimap;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;

public class FormBuilder {

    private CommcareForm form;

    public FormBuilder(String topFormElementName) {
        form = new CommcareForm();
        FormValueElement formValueElement = new FormValueElement();
        formValueElement.setElementName(topFormElementName);
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

    public FormBuilder with(String child_info, FormValueElement formValueElement) {
        form.getForm().getSubElements().put(child_info, formValueElement);
        return this;
    }
}
