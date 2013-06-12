package org.motechproject.mapper.util;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mapper.domain.MRSEncounterActivity;

import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;
import static org.mockito.MockitoAnnotations.initMocks;

public class ObservationIdGenerationStrategyTest {
    @Mock
    private CommcareFormSegment beneficiarySegment;
    @Mock
    private MRSEncounterActivity encounterActivity;
    @Mock
    private IdentityResolver identityResolver;

    private ObservationIdGenerationStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGenerateIdAsNullIfObservationIdSchemeNotPresent() throws Exception {
        strategy = new ObservationIdGenerationStrategy(beneficiarySegment, encounterActivity, identityResolver);

        String observationId = strategy.generate("concept");
        assertNull(observationId);
    }

    @Test
    public void shouldGenerateIdAsObservationIdAndConceptNameWhenSchemeIsPresent() throws Exception {
        HashMap<String, String> idScheme = new HashMap<>();
        when(encounterActivity.getEncounterIdScheme()).thenReturn(idScheme);
        when(identityResolver.retrieveId(idScheme, beneficiarySegment)).thenReturn("myId");

        strategy = new ObservationIdGenerationStrategy(beneficiarySegment, encounterActivity, identityResolver);
        String observationId = strategy.generate("concept");
        assertEquals("myId-concept", observationId);
    }


}
