package org.motechproject.mapper.util;

import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.UUID;

public class EncounterIdGenerationStrategy {

    private String instanceId;
    private boolean random;
    private String patientId;

    public EncounterIdGenerationStrategy(IdentityResolver idResolver, Map<String, String> encounterIdScheme, CommcareFormSegment beneficiarySegment, String patientId) {
        instanceId =idResolver.retrieveId(encounterIdScheme, beneficiarySegment);
        if(StringUtils.isBlank(instanceId)) {
            random = true;
        }
        this.patientId = patientId;
    }

    public String getEncounterId() {
        if(random) {
            return UUID.randomUUID().toString();
        }
        return String.format("%s-%s", patientId, instanceId);
    }


    public String generateObservationId(String conceptName) {
        if(random) {
            return UUID.randomUUID().toString();
        }
        return String.format("%s-%s-%s", patientId, instanceId, conceptName);
    }

    public String generateObservationId(String conceptName, int counter) {
        if(random) {
            return UUID.randomUUID().toString();
        }
        return String.format("%s-%s-%s-%s", patientId, instanceId, conceptName, counter);
    }
}
