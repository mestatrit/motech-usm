package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public interface ElementSearchStrategy {
    boolean canSearch(String searchPath);
    FormNode searchFirst(String searchPath, FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements);
    List<FormNode> search(String searchPath, FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements);
}
