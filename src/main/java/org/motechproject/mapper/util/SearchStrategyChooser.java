package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.FormValueElement;

import java.util.List;

public class SearchStrategyChooser {

    public static SearchStrategy getFor(final FormValueElement rootElement, final String elementName, final List<String> restrictedElements) {
        if (elementName.startsWith("//")) {
            return new SearchStrategy() {
                @Override
                public FormValueElement search(FormValueElement formValueElement) {
                    return formValueElement.getElementByPathFromCurrentElement(elementName, restrictedElements);
                }
            };
        } else if (elementName.startsWith("/")) {
            return new SearchStrategy() {
                @Override
                public FormValueElement search(FormValueElement formValueElement) {
                    return formValueElement.getElementByPathFromRoot(elementName, restrictedElements, rootElement);
                }
            };
        } else {
            return new SearchStrategy() {
                @Override
                public FormValueElement search(FormValueElement formValueElement) {
                    return formValueElement.getElementByName(elementName, restrictedElements);
                }
            };

        }
    }
}
