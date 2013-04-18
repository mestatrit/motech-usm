package org.motechproject.mapper.validation;

import java.util.Collections;
import java.util.List;

import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSPatient;

public final class OpenMRSEntityValidator {

    public static List<ValidationError> validatePatientRegistration(MRSPatient patient) {
        return Collections.<ValidationError>emptyList();

    }

    public static List<ValidationError> validateEncounter(MRSEncounter mrsEncounter) {
        return Collections.<ValidationError>emptyList();
    }

}
