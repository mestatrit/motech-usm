package org.motechproject.mapper.domain;

import org.junit.Test;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.mapper.util.CommcareFormSegment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MRSMappingVersionTest {
    
    @Test
    public void shouldNotMatchVersionIfFieldDoesNotExist() {
        CommcareFormSegment commcareFormSegment = mock(CommcareFormSegment.class);
        
        MappingVersion mrsMappingVersion = new MappingVersion("fieldName", "fieldValue");
        
        assertFalse(mrsMappingVersion.matches(commcareFormSegment));
    }

    @Test
    public void shouldNotMatchVersionIfFieldHasNullValue() {
        CommcareFormSegment commcareFormSegment = mock(CommcareFormSegment.class);
        FormNode formNode = mock(FormNode.class);
        when(commcareFormSegment.search("fieldName")).thenReturn(formNode);

        MappingVersion mrsMappingVersion = new MappingVersion("fieldName", "fieldValue");
        
        assertFalse(mrsMappingVersion.matches(commcareFormSegment));
    }

    @Test
    public void shouldNotMatchVersionIfFieldHasDifferentValue() {
        CommcareFormSegment commcareFormSegment = mock(CommcareFormSegment.class);
        FormNode formNode = mock(FormNode.class);
        when(commcareFormSegment.search("fieldName")).thenReturn(formNode);
        when(formNode.getValue()).thenReturn("fieldValueNot");

        MappingVersion mrsMappingVersion = new MappingVersion("fieldName", "fieldValue");

        assertFalse(mrsMappingVersion.matches(commcareFormSegment));
    }

    @Test
    public void shouldMatchVersionIfFieldHasSameValue() {
        CommcareFormSegment commcareFormSegment = mock(CommcareFormSegment.class);
        FormNode formNode = mock(FormNode.class);
        when(commcareFormSegment.search("fieldName")).thenReturn(formNode);
        when(formNode.getValue()).thenReturn("fieldValue");

        MappingVersion mrsMappingVersion = new MappingVersion("fieldName", "fieldValue");

        assertTrue(mrsMappingVersion.matches(commcareFormSegment));
    }
    
    @Test
    public void shouldCheckIfVersionCheckIsWildcard() {
        assertFalse(new MappingVersion("fieldName", "fieldValue").isWildcard());

        assertFalse(new MappingVersion("*", "fieldValue").isWildcard());
        assertFalse(new MappingVersion(null, "fieldValue").isWildcard());
        assertFalse(new MappingVersion("", "fieldValue").isWildcard());
        assertFalse(new MappingVersion("  ", "fieldValue").isWildcard());

        assertTrue(new MappingVersion("fieldName", "*").isWildcard());
        assertTrue(new MappingVersion("fieldName", null).isWildcard());
        assertFalse(new MappingVersion("fieldName", "").isWildcard());
        assertFalse(new MappingVersion("fieldName", " ").isWildcard());
    }
}
