package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public class SearchStrategyChooser {

    public static SearchStrategy getFor(final String elementPath) {
        if (elementPath.startsWith("//")) {
            return new SearchStrategy() {
                @Override
                public FormNode search(FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements) {
                    return startElement.getNode(elementPath, restrictedElements);
                }
            };
        } else if (elementPath.startsWith("/")) {
            return new SearchStrategy() {
                @Override
                public FormNode search(FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements) {
                    return rootElement.getNode(elementPath.replace("/" + rootElement.getElementName(), "/"), restrictedElements);
                }
            };
        } else {
            return new SearchStrategy() {
                @Override
                public FormNode search(FormValueElement startElement, FormValueElement rootElement, List<String> restrictedElements) {
                    return startElement.getElementByName(elementPath, restrictedElements);
                }
            };
        }
    }
}
