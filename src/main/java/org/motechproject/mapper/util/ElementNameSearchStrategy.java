package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public class ElementNameSearchStrategy implements ElementSearchStrategy {
    @Override
    public boolean canSearch(String searchPath) {
        return ! searchPath.startsWith(FormNode.PREFIX_SEARCH_RELATIVE) && ! searchPath.startsWith(FormNode.PREFIX_SEARCH_FROM_ROOT);
    }

    @Override
    public FormNode searchFirst(String searchPath, FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements) {
        return startElement.getElement(searchPath, restrictedElements);
    }

    @Override
    public List<FormNode> search(String searchPath, FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements) {
        return (List) startElement.getAllElements(searchPath, restrictedElements);
    }
}