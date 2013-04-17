package org.motechproject.mapper.adapters.mappings;

import java.util.Map;

public class OpenMRSRegistrationActivity extends MRSActivity {

    private Map<String, String> attributes;
    private Map<String, String> registrationMappings;
    private Map<String, String> staticMappings;

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public Map<String, String> getRegistrationMappings() {
        return registrationMappings;
    }

    public void setRegistrationMappings(Map<String, String> registrationMappings) {
        this.registrationMappings = registrationMappings;
    }

    public Map<String, String> getStaticMappings() {
        return staticMappings;
    }

    public void setStaticMappings(Map<String, String> staticMappings) {
        this.staticMappings = staticMappings;
    }
}
