package org.motechproject.mapper.util;

import org.junit.Test;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SearchStrategyChooserTest {

    @Test
    public void shouldSearchAllElementsInsideStartNode() {
        FormValueElement startElement = mock(FormValueElement.class);
        FormValueElement rootElement = mock(FormValueElement.class);
        List<String> restrictedElements = new ArrayList<>();
        String searchElement = "fieldName";

        SearchStrategy strategy = SearchStrategyChooser.getFor(searchElement);
        strategy.search(startElement, rootElement, restrictedElements);

        verify(startElement).getElement(searchElement, restrictedElements);
    }

    @Test
    public void shouldSearchAllElementsFromRootNode() {
        FormValueElement startElement = mock(FormValueElement.class);
        FormValueElement rootElement = mock(FormValueElement.class);
        when(rootElement.getElementName()).thenReturn("fieldName");
        List<String> restrictedElements = new ArrayList<>();
        String searchElement = "/fieldName/anotherField";
        String actualSearchString = "//anotherField";

        SearchStrategy strategy = SearchStrategyChooser.getFor(searchElement);
        strategy.search(startElement, rootElement, restrictedElements);

        verify(rootElement).search(actualSearchString, restrictedElements);
    }

    @Test
    public void shouldSearchAllElementsFromCurrentNode() {
        FormValueElement startElement = mock(FormValueElement.class);
        FormValueElement rootElement = mock(FormValueElement.class);
        List<String> restrictedElements = new ArrayList<>();
        String searchElement = "//fieldName";

        SearchStrategy strategy = SearchStrategyChooser.getFor(searchElement);
        strategy.search(startElement, rootElement, restrictedElements);

        verify(startElement).search(searchElement, restrictedElements);
    }
}
