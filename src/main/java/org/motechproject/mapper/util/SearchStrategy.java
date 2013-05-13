package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public interface SearchStrategy {
    FormNode search(FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements);
}
