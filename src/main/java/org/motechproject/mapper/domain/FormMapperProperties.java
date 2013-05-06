package org.motechproject.mapper.domain;

import java.util.List;

public class FormMapperProperties {

    private String startElement;
    private List<String> restrictedElements;
    private Boolean isMultiple;

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

    public Boolean getMultiple() {
        return isMultiple;
    }

    public void setMultiple(Boolean multiple) {
        isMultiple = multiple;
    }
}
