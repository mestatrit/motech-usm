package org.motechproject.mapper.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.repository.AllMRSMappings;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class MRSMappingServiceTest {

    @Mock
    private AllMRSMappings allMRSMappings;

    private MRSMappingService mappingService;

    @Before
    public void setUp() {
        initMocks(this);
        mappingService = new MRSMappingService(allMRSMappings);
    }

    @Test
    public void shouldAddMappings() {
        MRSMapping mrsMapping1  = new MRSMapping();
        mrsMapping1.setXmlns("ns1");
        mrsMapping1.setVersion("version1");

        MRSMapping mrsMapping2  = new MRSMapping();
        mrsMapping2.setXmlns("ns2");
        mrsMapping2.setVersion("version2");

        mappingService.addOrUpdate(Arrays.asList(mrsMapping1, mrsMapping2));

        verify(allMRSMappings).add(mrsMapping1);
        verify(allMRSMappings).add(mrsMapping2);
    }

    @Test
    public void shouldDeleteSimilarMappingBeforeAddingMappings() {
        MRSMapping mrsMapping1  = new MRSMapping();
        mrsMapping1.setXmlns("ns1");
        mrsMapping1.setVersion("version1");

        MRSMapping mrsMapping2  = new MRSMapping();
        mrsMapping2.setXmlns("ns2");
        mrsMapping2.setVersion("version2");

        MRSMapping existingMRSMapping1 = new MRSMapping();
        MRSMapping existingMRSMapping2 = new MRSMapping();

        when(allMRSMappings.findByXmlnsAndVersion("ns1", "version1")).thenReturn(existingMRSMapping1);
        when(allMRSMappings.findByXmlnsAndVersion("ns2", "version2")).thenReturn(existingMRSMapping2);

        mappingService.addOrUpdate(Arrays.asList(mrsMapping1, mrsMapping2));

        verify(allMRSMappings).remove(existingMRSMapping1);
        verify(allMRSMappings).add(mrsMapping1);
        verify(allMRSMappings).remove(existingMRSMapping2);
        verify(allMRSMappings).add(mrsMapping2);
    }

    @Test
    public void shouldRemoveById() {
        String idValue1 = "idValue1";
        String idValue2 = "idValue2";

        when(allMRSMappings.deleteMapping(idValue1)).thenReturn(true);
        when(allMRSMappings.deleteMapping(idValue2)).thenReturn(false);

        assertTrue(mappingService.deleteMapping(idValue1));
        assertFalse(mappingService.deleteMapping(idValue2));

        verify(allMRSMappings).deleteMapping(idValue1);
        verify(allMRSMappings).deleteMapping(idValue2);
    }

    @Test
    public void shouldMatchMappingBasedOnVerson() {
        String xmlNs = "myxmlns";
        String version = "myversion";

        MRSMapping mrsMapping1 = mock(MRSMapping.class);
        MRSMapping mrsMapping2 = mock(MRSMapping.class);
        MRSMapping mrsMapping3 = mock(MRSMapping.class);
        MRSMapping mrsMapping4 = mock(MRSMapping.class);

        when(mrsMapping3.matchesVersion(version)).thenReturn(true);
        when(allMRSMappings.findByXmlns(xmlNs)).thenReturn(Arrays.asList(mrsMapping1, mrsMapping2, mrsMapping3, mrsMapping4));

        assertEquals(mrsMapping3, mappingService.findMatchingMappingFor(xmlNs, version));
    }

    @Test
    public void shouldReturnNullIfNoMatchingMappingIsFound() {
        String xmlNs = "myxmlns";
        String version = "myversion";

        MRSMapping mrsMapping1 = mock(MRSMapping.class);
        MRSMapping mrsMapping2 = mock(MRSMapping.class);
        MRSMapping mrsMapping3 = mock(MRSMapping.class);
        MRSMapping mrsMapping4 = mock(MRSMapping.class);

        when(allMRSMappings.findByXmlns(xmlNs)).thenReturn(Arrays.asList(mrsMapping1, mrsMapping2, mrsMapping3, mrsMapping4));

        assertNull(mappingService.findMatchingMappingFor(xmlNs, version));
        assertNull(mappingService.findMatchingMappingFor(xmlNs, "*"));
        assertNull(mappingService.findMatchingMappingFor(xmlNs, null));
        assertNull(mappingService.findMatchingMappingFor(xmlNs, ""));
    }

    @Test
    public void shouldReturnWildcardMappingIfNoMatchingMappingIsFound() {
        String xmlNs = "myxmlns";
        String version = "myversion";

        MRSMapping mrsMapping1 = mock(MRSMapping.class);
        MRSMapping mrsMapping2 = mock(MRSMapping.class);
        MRSMapping mrsMapping3 = mock(MRSMapping.class);
        MRSMapping mrsMapping4 = mock(MRSMapping.class);

        when(mrsMapping2.hasWildcardVersion()).thenReturn(true);
        when(allMRSMappings.findByXmlns(xmlNs)).thenReturn(Arrays.asList(mrsMapping1, mrsMapping2, mrsMapping3, mrsMapping4));

        assertEquals(mrsMapping2, mappingService.findMatchingMappingFor(xmlNs, version));
    }

}
