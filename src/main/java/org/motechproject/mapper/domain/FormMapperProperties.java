package org.motechproject.mapper.domain;

import org.motechproject.commcare.domain.FormNode;

import java.util.ArrayList;
import java.util.List;

public class FormMapperProperties {

    private String startElement;
    private List<String> restrictedElements;

    public FormMapperProperties() {
        startElement = FormNode.PREFIX_SEARCH_FROM_ROOT;
        restrictedElements = new ArrayList<>();
    }

    public String getStartElement() {
        return startElement;
    }

    public void setStartElement(String startElement) {
        this.startElement = startElement;
    }

    public List<String> getRestrictedElements() {
        return restrictedElements;
    }

    public void setRestrictedElements(List<String> restrictedElements) {
        this.restrictedElements = restrictedElements;
    }
}
