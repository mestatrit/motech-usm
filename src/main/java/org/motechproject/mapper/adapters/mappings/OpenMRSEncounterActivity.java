package org.motechproject.mapper.adapters.mappings;

import java.util.List;
import java.util.Map;

public class OpenMRSEncounterActivity extends MRSActivity {

    private String encounterType;
    private String facilityName;

    private List<ObservationMapping> observationMappings;

    private Map<String, String> encounterMappings;

    public List<ObservationMapping> getObservationMappings() {
        return observationMappings;
    }

    public void setObservationMappings(List<ObservationMapping> observationMappings) {
        this.observationMappings = observationMappings;
    }

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public Map<String, String> getEncounterMappings() {
        return encounterMappings;
    }

    public void setEncounterMappings(Map<String, String> encounterMappings) {
        this.encounterMappings = encounterMappings;
    }
}
