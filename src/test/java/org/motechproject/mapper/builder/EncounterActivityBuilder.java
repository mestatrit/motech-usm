package org.motechproject.mapper.builder;

import org.motechproject.mapper.domain.FormMapperProperties;
import org.motechproject.mapper.domain.MRSEncounterActivity;
import org.motechproject.mapper.domain.ObservationMapping;

import java.util.ArrayList;

public class EncounterActivityBuilder {

    private MRSEncounterActivity activity;

    public EncounterActivityBuilder() {
        activity = new MRSEncounterActivity();
        activity.setObservationMappings(new ArrayList<ObservationMapping>());
    }

    public MRSEncounterActivity getActivity() {
        return activity;
    }

    public EncounterActivityBuilder withFormMapperProperties(FormMapperProperties formMapperProperties) {
        activity.setFormMapperProperties(formMapperProperties);
        return this;
    }

    public EncounterActivityBuilder withObservationMappings(ObservationMapping observationMappings) {
        activity.getObservationMappings().add(observationMappings);
        return this;
    }

}
