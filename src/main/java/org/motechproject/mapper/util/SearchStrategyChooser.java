package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public class SearchStrategyChooser {

    public static SearchStrategy getFor(final String elementPath) {
        if (elementPath.startsWith(FormNode.PREFIX_SEARCH_RELATIVE)) {
            return new SearchStrategy() {
                @Override
                public FormNode search(FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements) {
                    return startElement.search(elementPath, restrictedElements);
                }
            };
        }

        if (elementPath.startsWith(FormNode.PREFIX_SEARCH_FROM_ROOT)) {
            return new SearchStrategy() {
                @Override
                public FormNode search(FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements) {
                    return rootElement.search(elementPath.replace("/" + rootElement.getElementName(), "/"), restrictedElements);
                }
            };
        }

        return new SearchStrategy() {
            @Override
            public FormNode search(FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements) {
                return startElement.getElement(elementPath, restrictedElements);
            }
        };
    }

}
