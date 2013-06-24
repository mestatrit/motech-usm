package org.motechproject.mapper.util;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mapper.domain.MRSEncounterActivity;

import java.util.HashMap;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ObservationIdGenerationStrategyTest {
    @Mock
    private CommcareFormSegment beneficiarySegment;
    @Mock
    private MRSEncounterActivity encounterActivity;
    @Mock
    private IdentityResolver identityResolver;

    private EncounterIdGenerationStrategy strategy;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }


    @Test
    public void shouldGenerateSpecificIdsWhenEncounterIdSchemeIsPresent() throws Exception {
        String patientId = "patientId";

        HashMap<String, String> idScheme = new HashMap<>();
        when(encounterActivity.getEncounterIdScheme()).thenReturn(idScheme);
        when(identityResolver.retrieveId(idScheme, beneficiarySegment)).thenReturn("myId");
        strategy = new EncounterIdGenerationStrategy(identityResolver, idScheme, beneficiarySegment, patientId);

        assertEquals("patientId-myId", strategy.getEncounterId());
        assertEquals("patientId-myId-concept", strategy.generateConceptId("concept"));
        assertEquals("patientId-myId-concept-5", strategy.generateConceptId("concept", 5));
    }

    @Test
    public void shouldGenerateIdRandomIdsWhenEncounterIdSchemeIsNotPresent() throws Exception {
        String patientId = "patientId";

        HashMap<String, String> idScheme = new HashMap<>();
        when(encounterActivity.getEncounterIdScheme()).thenReturn(idScheme);
        when(identityResolver.retrieveId(idScheme, beneficiarySegment)).thenReturn(null);
        strategy = new EncounterIdGenerationStrategy(identityResolver, idScheme, beneficiarySegment, patientId);

        UUID.fromString(strategy.getEncounterId());
        UUID.fromString(strategy.generateConceptId("concept"));
        UUID.fromString(strategy.generateConceptId("concept", 0));
    }
}
