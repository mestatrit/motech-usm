package org.motechproject.mapper.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.repository.AllMRSMappings;

import static junit.framework.Assert.assertEquals;
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
    public void shouldDeleteMappings() {
        String xmlnsValue = "xmlnsValue";
        mappingService.deleteMapping(xmlnsValue);

        verify(allMRSMappings).removeAll("xmlns", xmlnsValue);
    }

    @Test
    public void shouldFindMappingByXmlns() {
        String xmlnsValue = "xmlnsValue";
        MRSMapping expectedMapping = new MRSMapping();

        when(allMRSMappings.findByXmlns(xmlnsValue)).thenReturn(expectedMapping);

        MRSMapping actualMapping = mappingService.findByXmlns(xmlnsValue);

        assertEquals(expectedMapping, actualMapping);
    }

}
