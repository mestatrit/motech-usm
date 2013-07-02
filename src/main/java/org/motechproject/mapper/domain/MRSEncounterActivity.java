package org.motechproject.mapper.domain;

import org.joda.time.DateTime;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.util.CommcareFormSegment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MRSEncounterActivity extends MRSActivity {

    private String encounterType;
    private String facilityName;
    private List<ObservationMapping> observationMappings = new ArrayList<>();
    private Map<String, String> encounterMappings = new HashMap<>();
    private Map<String, String> encounterIdScheme = new HashMap<>();

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

    public Map<String, String> getEncounterIdScheme() {
        return encounterIdScheme;
    }

    public void setEncounterIdScheme(Map<String, String> encounterIdScheme) {
        this.encounterIdScheme = encounterIdScheme;
    }

    public DateTime getActivityDate(CommcareFormSegment beneficiarySegment) {
        return getActivityDate(beneficiarySegment, encounterMappings, FormMappingConstants.ENCOUNTER_DATE_FIELD);
    }
}
