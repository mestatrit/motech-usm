package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public interface SearchStrategy {
    FormValueElement search(FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements);
}
