package org.motechproject.mapper.builder;

import org.motechproject.commcare.domain.FormValueElement;

public class FormValueElementBuilder {

    private FormValueElement formValueElement;

    public FormValueElementBuilder(String elementName) {
        formValueElement = new FormValueElement();
        formValueElement.setElementName(elementName);
    }

    public FormValueElementBuilder withAttribute(String name, String value) {
        formValueElement.getAttributes().put(name, value);
        return this;
    }

    public FormValueElementBuilder withSubElement(FormValueElement subElement) {
        formValueElement.getSubElements().put(subElement.getElementName(), subElement);
        return this;
    }

    public FormValueElementBuilder withValue(String value) {
        formValueElement.setValue(value);
        return this;
    }

    public FormValueElement build() {
        return formValueElement;
    }
}