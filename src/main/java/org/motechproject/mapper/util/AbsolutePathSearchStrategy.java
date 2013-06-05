package org.motechproject.mapper.util;


import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public class AbsolutePathSearchStrategy implements ElementSearchStrategy {
    @Override
    public boolean canSearch(String searchPath) {
        return searchPath.startsWith(FormNode.PREFIX_SEARCH_FROM_ROOT) && !searchPath.startsWith(FormNode.PREFIX_SEARCH_RELATIVE);
    }

    @Override
    public FormNode searchFirst(String searchPath, FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements) {
        return rootElement.searchFirst(searchPath.replace("/" + rootElement.getElementName(), "/"));
    }

    @Override
    public List<FormNode> search(String searchPath, FormValueElement startElement, final FormValueElement rootElement, List<String> restrictedElements) {
        return rootElement.search(searchPath.replace("/" + rootElement.getElementName(), "/"));
    }
}

