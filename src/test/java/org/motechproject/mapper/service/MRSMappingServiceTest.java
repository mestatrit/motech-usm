package org.motechproject.mapper.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mapper.repository.AllMRSMappings;

import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class MRSMappingServiceTest {

    @Mock
    private AllMRSMappings allMRSMappings;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldDeleteMappings() {
        String xmlnsValue = "xmlnsValue";
        MRSMappingService mrsMappingService = new MRSMappingService(allMRSMappings);

        mrsMappingService.deleteMapping(xmlnsValue);

        verify(allMRSMappings).removeAll("xmlns", xmlnsValue);
    }

}
