package org.motechproject.mapper.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.motechproject.mrs.domain.MRSPatient;

public final class CouchDBPatientValidator {

    public static List<ValidationError> validatePatientRegistration(MRSPatient patient) {
        List<ValidationError> validationErrors = new ArrayList<ValidationError>();

        if (patient.getMotechId() == null || patient.getMotechId().trim().length() == 0) {
           validationErrors.add(new ValidationError("PatientIdError", "The patient's Motech ID cannot be null"));
        }
        return (validationErrors.size() > 0) ? validationErrors : Collections.<ValidationError>emptyList();
    }
}
