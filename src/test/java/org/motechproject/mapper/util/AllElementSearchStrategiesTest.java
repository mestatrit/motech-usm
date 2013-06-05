package org.motechproject.mapper.util;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AllElementSearchStrategiesTest {

    private AllElementSearchStrategies allElementSearchStrategies;

    @Before
    public void setup() {
        allElementSearchStrategies = new AllElementSearchStrategies();
    }

    @Test
    public void shouldSearchAllElementsInsideStartNode() {
        FormValueElement startElement = mock(FormValueElement.class);
        FormValueElement rootElement = mock(FormValueElement.class);
        List<String> restrictedElements = new ArrayList<>();

        FormValueElement expectedResult = mock(FormValueElement.class);
        List<FormValueElement> expectedResultList = mock(List.class);

        String searchPath = "fieldName";

        when(startElement.getElement(searchPath, restrictedElements)).thenReturn(expectedResult);
        when(startElement.getAllElements(searchPath, restrictedElements)).thenReturn(expectedResultList);

        FormNode actualResult = allElementSearchStrategies.searchFirst(searchPath, startElement, rootElement, restrictedElements);
        assertEquals(expectedResult, actualResult);

        List<FormNode> actualResultList = allElementSearchStrategies.search(searchPath, startElement, rootElement, restrictedElements);
        assertEquals(expectedResultList, actualResultList);
    }

    @Test
    public void shouldSearchAllElementsFromRootNode() {
        FormValueElement startElement = mock(FormValueElement.class);
        FormValueElement rootElement = mock(FormValueElement.class);
        List<String> restrictedElements = new ArrayList<>();

        FormValueElement expectedResult = mock(FormValueElement.class);
        List<FormNode> expectedResultList = mock(List.class);

        String searchPath = "/root/fieldName";

        when(rootElement.getElementName()).thenReturn("root");
        when(rootElement.searchFirst("//fieldName")).thenReturn(expectedResult);
        when(rootElement.search("//fieldName")).thenReturn(expectedResultList);

        FormNode actualResult = allElementSearchStrategies.searchFirst(searchPath, startElement, rootElement, restrictedElements);
        assertEquals(expectedResult, actualResult);

        List<FormNode> actualResultList = allElementSearchStrategies.search(searchPath, startElement, rootElement, restrictedElements);
        assertEquals(expectedResultList, actualResultList);
    }

    @Test
    public void shouldSearchAllElementsFromCurrentNode() {
        FormValueElement startElement = mock(FormValueElement.class);
        FormValueElement rootElement = mock(FormValueElement.class);
        List<String> restrictedElements = new ArrayList<>();

        FormValueElement expectedResult = mock(FormValueElement.class);
        List<FormNode> expectedResultList = mock(List.class);

        String searchPath = "//fieldName";

        when(startElement.searchFirst(searchPath)).thenReturn(expectedResult);
        when(startElement.search(searchPath)).thenReturn(expectedResultList);

        FormNode actualResult = allElementSearchStrategies.searchFirst(searchPath, startElement, rootElement, restrictedElements);
        assertEquals(expectedResult, actualResult);

        List<FormNode> actualResultList = allElementSearchStrategies.search(searchPath, startElement, rootElement, restrictedElements);
        assertEquals(expectedResultList, actualResultList);
    }
}
