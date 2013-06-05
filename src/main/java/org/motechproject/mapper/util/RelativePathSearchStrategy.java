package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public class RelativePathSearchStrategy implements ElementSearchStrategy {

    @Override
    public boolean canSearch(String searchPath) {
        return searchPath.startsWith(FormNode.PREFIX_SEARCH_RELATIVE);
    }

    @Override
    public FormNode searchFirst(String searchPath, FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements) {
        return startElement.searchFirst(searchPath);
    }

    @Override
    public List<FormNode> search(String searchPath, final FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements) {
        return startElement.search(searchPath);
    }
}
