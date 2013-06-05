package org.motechproject.mapper.util;


import org.junit.Before;
import org.junit.Test;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AbsolutePathSearchStrategyTest {

    private AbsolutePathSearchStrategy searchStrategy;
    @Before
    public void setup() {
        searchStrategy = new AbsolutePathSearchStrategy();
    }

    @Test
    public void shouldBeAppliedOnPathStartingWithRelativePathPrefix() {
        assertTrue(searchStrategy.canSearch("/path"));
        assertTrue(searchStrategy.canSearch("/path/@attribute"));
        assertTrue(searchStrategy.canSearch("/path/#value"));
        assertTrue(searchStrategy.canSearch("/"));
        assertFalse(searchStrategy.canSearch("//"));
        assertFalse(searchStrategy.canSearch("something"));
    }

    @Test
    public void shouldLookupForANodeInRootElement() {
        FormValueElement startElement = mock(FormValueElement.class);
        FormValueElement rootElement = mock(FormValueElement.class);
        List<String> restrictedElements = new ArrayList<>();

        FormValueElement expectedResult = mock(FormValueElement.class);
        List<FormNode> expectedResultList = mock(List.class);

        String searchPath = "/fieldName";

        when(rootElement.searchFirst(searchPath)).thenReturn(expectedResult);
        when(rootElement.search(searchPath)).thenReturn(expectedResultList);

        FormNode actualResult = searchStrategy.searchFirst(searchPath, startElement, rootElement, restrictedElements);
        assertEquals(expectedResult, actualResult);

        List<FormNode> actualResultList = searchStrategy.search(searchPath, startElement, rootElement, restrictedElements);
        assertEquals(expectedResultList, actualResultList);
    }

}
