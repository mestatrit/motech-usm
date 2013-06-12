package org.motechproject.mapper.util;

import org.motechproject.mapper.domain.MRSEncounterActivity;

public class ObservationIdGenerationStrategy {

    private final String encounterId;

    public ObservationIdGenerationStrategy(CommcareFormSegment beneficiarySegment, MRSEncounterActivity encounterActivity, IdentityResolver identityResolver) {
        this.encounterId = identityResolver.retrieveId(encounterActivity.getEncounterIdScheme(), beneficiarySegment);
    }

    public String generate(String conceptName) {
        if(encounterId == null) {
            return null;
        }
        return String.format("%s-%s", encounterId, conceptName);
    }

    public String generate(String conceptName, int counter) {
        if(encounterId == null) {
            return null;
        }
        return String.format("%s-%s-%s", encounterId, conceptName, counter);
    }
}
