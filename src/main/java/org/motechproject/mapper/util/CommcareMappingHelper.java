package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public class CommcareMappingHelper {

    private FormValueElement rootElement;
    private FormValueElement startElement;
    private List<String> restrictedElements;

    public CommcareMappingHelper(CommcareForm form, FormValueElement startElement, List<String> restrictedElements) {
        this.startElement = startElement;
        this.restrictedElements = restrictedElements;
        rootElement = form.getForm();

    }

    public FormValueElement search(String lookupPath) {
        return SearchStrategyChooser.getFor(lookupPath).search(startElement, rootElement, restrictedElements);
    }

    public FormValueElement getStartElement() {
        return startElement;
    }
}