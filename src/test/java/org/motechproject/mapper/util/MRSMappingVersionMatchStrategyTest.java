package org.motechproject.mapper.util;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mapper.domain.MRSMapping;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MRSMappingVersionMatchStrategyTest {

    private MRSMappingVersionMatchStrategy versionMatchStrategy;

    @Before
    public void setUp() throws Exception {
        versionMatchStrategy = new MRSMappingVersionMatchStrategy();
    }

    @Test
    public void shouldMatchMappingBasedOnVersion() {
        String version = "myversion";

        MRSMapping mrsMapping1 = mock(MRSMapping.class);
        MRSMapping mrsMapping2 = mock(MRSMapping.class);
        MRSMapping mrsMapping3 = mock(MRSMapping.class);
        MRSMapping mrsMapping4 = mock(MRSMapping.class);
        when(mrsMapping3.matchesVersion(version)).thenReturn(true);
        List<MRSMapping> mrsMappings = Arrays.asList(mrsMapping1, mrsMapping2, mrsMapping3, mrsMapping4);

        MRSMapping actualMatchedMapping = versionMatchStrategy.findBestMatch(mrsMappings, version);

        assertEquals(mrsMapping3, actualMatchedMapping);
    }

    @Test
    public void shouldReturnNullIfNoMatchingMappingIsFound() {
        String version = "myversion";

        MRSMapping mrsMapping1 = mock(MRSMapping.class);
        MRSMapping mrsMapping2 = mock(MRSMapping.class);
        MRSMapping mrsMapping3 = mock(MRSMapping.class);
        MRSMapping mrsMapping4 = mock(MRSMapping.class);
        List<MRSMapping> mappings = Arrays.asList(mrsMapping1, mrsMapping2, mrsMapping3, mrsMapping4);

        assertNull(versionMatchStrategy.findBestMatch(mappings, version));
        assertNull(versionMatchStrategy.findBestMatch(mappings, "*"));
        assertNull(versionMatchStrategy.findBestMatch(mappings, null));
        assertNull(versionMatchStrategy.findBestMatch(mappings, ""));
    }

    @Test
    public void shouldReturnWildcardMappingIfNoMatchingMappingIsFound() {
        String version = "myversion";

        MRSMapping mrsMapping1 = mock(MRSMapping.class);
        MRSMapping mrsMapping2 = mock(MRSMapping.class);
        MRSMapping mrsMapping3 = mock(MRSMapping.class);
        MRSMapping mrsMapping4 = mock(MRSMapping.class);
        List<MRSMapping> mappings = Arrays.asList(mrsMapping1, mrsMapping2, mrsMapping3, mrsMapping4);

        when(mrsMapping2.hasWildcardVersion()).thenReturn(true);

        assertEquals(mrsMapping2, versionMatchStrategy.findBestMatch(mappings, version));
    }

    @Test
    public void shouldMatchForBestVersion() {
        String formVersion = "myversion";

        MRSMapping mrsMapping1 = mock(MRSMapping.class);
        MRSMapping mrsMapping2 = mock(MRSMapping.class);
        MRSMapping mrsMapping3 = mock(MRSMapping.class);
        MRSMapping mrsMapping4 = mock(MRSMapping.class);
        MRSMapping mrsMapping5 = mock(MRSMapping.class);

        when(mrsMapping2.matchesVersion(formVersion)).thenReturn(true);
        when(mrsMapping3.matchesVersion(formVersion)).thenReturn(true);
        when(mrsMapping4.matchesVersion(formVersion)).thenReturn(true);

        when(mrsMapping2.getVersion()).thenReturn("for-3.1");
        when(mrsMapping3.getVersion()).thenReturn("for-3.1.1.1");
        when(mrsMapping4.getVersion()).thenReturn("for-3.1.1");

        List<MRSMapping> mrsMappings = Arrays.asList(mrsMapping1, mrsMapping2, mrsMapping3, mrsMapping4, mrsMapping5);

        MRSMapping actualMatchedMapping = versionMatchStrategy.findBestMatch(mrsMappings, formVersion);

        assertEquals(mrsMapping3, actualMatchedMapping);
    }

}
