package org.motechproject.mapper.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mrs.domain.MRSEncounter;
import org.motechproject.mrs.domain.MRSPatient;

public final class CouchDBEntityValidator {

    public static List<ValidationError> validatePatientRegistration(MRSPatient patient) {
        List<ValidationError> validationErrors = new ArrayList<ValidationError>();

        if (StringUtils.isBlank(patient.getMotechId())) {
            validationErrors.add(new ValidationError("PatientIdError", "The patient's Motech ID cannot be null or empty"));
        }

        return (validationErrors.size() > 0) ? validationErrors : Collections.<ValidationError>emptyList();
    }

    public static List<ValidationError> validateEncounter(MRSEncounter mrsEncounter) {
        List<ValidationError> validationErrors = new ArrayList<ValidationError>();

        if (mrsEncounter.getPatient() == null) {
            validationErrors.add(new ValidationError("PatientDoesNotExist", "The patient cannot be null"));
        } else if (StringUtils.isBlank(mrsEncounter.getPatient().getMotechId())) {
            validationErrors.add(new ValidationError("PatientIdError", "The patient's Motech ID cannot be null or empty"));
        }

        return (validationErrors.size() > 0) ? validationErrors : Collections.<ValidationError>emptyList();

    }
}
