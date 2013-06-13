package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public class CommcareFormSegment {

    private CommcareForm commcareForm;
    private AllElementSearchStrategies allElementSearchStrategies;
    private FormValueElement startElement;
    private List<String> restrictedElements;

    public CommcareFormSegment(CommcareForm commcareForm, FormValueElement startElement, List<String> restrictedElements, AllElementSearchStrategies allElementSearchStrategies) {
        this.startElement = startElement;
        this.restrictedElements = restrictedElements;
        this.commcareForm = commcareForm;
        this.allElementSearchStrategies = allElementSearchStrategies;
    }

    public FormNode search(String lookupPath) {
        if(!lookupPath.endsWith("[]")) {
            return allElementSearchStrategies.searchFirst(lookupPath, startElement, commcareForm.getForm(), restrictedElements);
        }
        return searchAndCombine(lookupPath);
    }

    private FormNode searchAndCombine(String lookupPath) {
        lookupPath = lookupPath.replace("[]", "");
        List<FormNode> nodes = allElementSearchStrategies.search(lookupPath, startElement, commcareForm.getForm(), restrictedElements);
        final StringBuilder sb = new StringBuilder();
        for (FormNode node : nodes) {
            if(sb.length() > 0) {
                sb.append(",");
            }
            sb.append(node.getValue());
        }
        return new FormNode() {
            @Override
            public String getValue() {
                return sb.toString();
            }
        };
    }

    public List<FormValueElement> getElementsByAttribute(String attribute, String value) {
        return startElement.getElementsByAttribute(attribute, value, restrictedElements);
    }

    public String getMetadata(String name) {
        return commcareForm.getMetadata().get(name);
    }
}