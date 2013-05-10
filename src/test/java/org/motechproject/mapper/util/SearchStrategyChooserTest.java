package org.motechproject.mapper.util;

import org.junit.Test;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SearchStrategyChooserTest {

    @Test
    public void shouldSearchAllElementsInsideStartNode() {
        FormValueElement formValueElement = mock(FormValueElement.class);
        FormValueElement rootElement = mock(FormValueElement.class);
        List<String> restrictedElements = new ArrayList<>();
        String searchElement = "fieldName";

        SearchStrategy strategy = SearchStrategyChooser.getFor(rootElement, searchElement, restrictedElements);
        strategy.search(formValueElement);

        verify(formValueElement).getElementByName(searchElement, restrictedElements);
    }

    @Test
    public void shouldSearchAllElementsFromRootNode() {
        FormValueElement formValueElement = mock(FormValueElement.class);
        FormValueElement rootElement = mock(FormValueElement.class);
        List<String> restrictedElements = new ArrayList<>();
        String searchElement = "/fieldName";

        SearchStrategy strategy = SearchStrategyChooser.getFor(rootElement, searchElement, restrictedElements);
        strategy.search(formValueElement);

        verify(formValueElement).getElementByPathFromRoot(searchElement, restrictedElements, rootElement);
    }

    @Test
    public void shouldSearchAllElementsFromCurrentNode() {
        FormValueElement formValueElement = mock(FormValueElement.class);
        FormValueElement rootElement = mock(FormValueElement.class);
        List<String> restrictedElements = new ArrayList<>();
        String searchElement = "//fieldName";

        SearchStrategy strategy = SearchStrategyChooser.getFor(rootElement, searchElement, restrictedElements);
        strategy.search(formValueElement);

        verify(formValueElement).getElementByPathFromCurrentElement(searchElement, restrictedElements);
    }
}
