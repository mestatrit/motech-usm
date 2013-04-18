package org.motechproject.mapper.validation;

import java.util.Collections;
import java.util.List;

import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class ValidationManager {

    @Autowired
    private SettingsFacade settings;

    public List<ValidationError> validatePatient(MRSPatient patient) {
        String destination = settings.getProperties(FormMappingConstants.MAPPING_CONFIGURATION_FILE_NAME).getProperty(FormMappingConstants.DESTINATION);
        if (FormMappingConstants.DESTINATION_COUCHDB.equals(destination)) {
            return CouchDBPatientValidator.validatePatientRegistration(patient);
        } else if (FormMappingConstants.DESTINATION_OPENMRS.equals(destination)) {
            return OpenMRSPatientValidator.validatePatientRegistration(patient);
        }

        return Collections.<ValidationError>emptyList();
    }
}
