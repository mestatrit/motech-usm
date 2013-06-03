package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public class CommcareFormBeneficiarySegment {

    private CommcareForm commcareForm;
    private FormValueElement startElement;
    private List<String> restrictedElements;

    public CommcareFormBeneficiarySegment(CommcareForm commcareForm, FormValueElement startElement, List<String> restrictedElements) {
        this.startElement = startElement;
        this.restrictedElements = restrictedElements;
        this.commcareForm = commcareForm;
    }

    public FormNode search(String lookupPath) {
        return SearchStrategyChooser.getFor(lookupPath).search(startElement, commcareForm.getForm(), restrictedElements);
    }

    public List<FormValueElement> getElementsByAttribute(String attribute, String value) {
        return startElement.getElementsByAttribute(attribute, value, restrictedElements);
    }

    public String getMetadata(String name) {
        return commcareForm.getMetadata().get(name);
    }
}