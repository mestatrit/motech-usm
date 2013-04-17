package org.motechproject.mapper.adapters.mappings;

import java.util.Map;

public class MRSActivity {

    private String type;
    private Map<String, String> patientIdScheme;
    private Map<String, String> facilityScheme;
    private Map<String, String> providerScheme;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getPatientIdScheme() {
        return patientIdScheme;
    }

    public void setPatientIdScheme(Map<String, String> patientIdScheme) {
        this.patientIdScheme = patientIdScheme;
    }

    public Map<String, String> getFacilityScheme() {
        return facilityScheme;
    }

    public void setFacilityScheme(Map<String, String> facilityScheme) {
        this.facilityScheme = facilityScheme;
    }

    public Map<String, String> getProviderScheme() {
        return providerScheme;
    }

    public void setProviderScheme(Map<String, String> providerScheme) {
        this.providerScheme = providerScheme;
    }
}
