package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public class FormTraversalProperty {

    private FormValueElement rootElement;
    private FormValueElement startElement;
    private List<String> restrictedElements;

    public FormTraversalProperty(CommcareForm form, FormValueElement startElement, List<String> restrictedElements) {
        this.startElement = startElement;
        this.restrictedElements = restrictedElements;
        rootElement = form.getForm();

    }

    public FormNode search(String lookupPath) {
        return SearchStrategyChooser.getFor(lookupPath).search(startElement, rootElement, restrictedElements);
    }

    public FormValueElement getStartElement() {
        return startElement;
    }
}