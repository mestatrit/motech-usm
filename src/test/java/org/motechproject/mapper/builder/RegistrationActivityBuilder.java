package org.motechproject.mapper.builder;

import org.motechproject.mapper.domain.FormMapperProperties;
import org.motechproject.mapper.domain.MRSRegistrationActivity;

import java.util.HashMap;

public class RegistrationActivityBuilder {

    private MRSRegistrationActivity activity;

    public RegistrationActivityBuilder() {
        activity = new MRSRegistrationActivity();
        activity.setAttributes(new HashMap<String, String>());
        activity.setRegistrationMappings(new HashMap<String, String>());
        activity.setStaticMappings(new HashMap<String, String>());
    }

    public RegistrationActivityBuilder withRegistrationMapping(String fieldToBeMapped, String fieldNameInForm) {
        activity.getRegistrationMappings().put(fieldToBeMapped, fieldNameInForm);
        return this;
    }

    public MRSRegistrationActivity getActivity() {
        return activity;
    }

    public RegistrationActivityBuilder withFormMapperProperties(FormMapperProperties formMapperProperties) {
        activity.setFormMapperProperties(formMapperProperties);
        return this;
    }
}
