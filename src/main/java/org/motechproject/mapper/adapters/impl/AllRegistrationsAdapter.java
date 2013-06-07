package org.motechproject.mapper.adapters.impl;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.mapper.adapters.ActivityFormAdapter;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.domain.MRSRegistrationActivity;
import org.motechproject.mapper.util.AllElementSearchStrategies;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.motechproject.mapper.util.IdentityResolver;
import org.motechproject.mapper.util.MRSUtil;
import org.motechproject.mapper.validation.ValidationError;
import org.motechproject.mapper.validation.ValidationManager;
import org.motechproject.mrs.domain.MRSFacility;
import org.motechproject.mrs.domain.MRSPatient;
import org.motechproject.mrs.domain.MRSPerson;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSPatientDto;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.motechproject.mapper.constants.FormMappingConstants.DEFAULT_FACILITY;
import static org.motechproject.mapper.constants.FormMappingConstants.FACILITY_NAME_FIELD;

@Component
public class AllRegistrationsAdapter extends ActivityFormAdapter {

    private MRSUtil mrsUtil;
    private IdentityResolver idResolver;
    private MRSPatientAdapter mrsPatientAdapter;
    private ValidationManager validator;
    private PersonAdapter personAdapter;

    @Autowired
    public AllRegistrationsAdapter(MRSUtil mrsUtil, IdentityResolver idResolver, MRSPatientAdapter mrsPatientAdapter,
                                   ValidationManager validator, AllElementSearchStrategies allElementSearchStrategies,
                                   PersonAdapter personAdapter
    ) {
        super(allElementSearchStrategies);
        this.mrsUtil = mrsUtil;
        this.idResolver = idResolver;
        this.mrsPatientAdapter = mrsPatientAdapter;
        this.validator = validator;
        this.personAdapter = personAdapter;
    }


    @Override
    public void adaptForm(CommcareForm form, MRSActivity activity) {
        MRSRegistrationActivity registrationActivity = (MRSRegistrationActivity) activity;
        for (CommcareFormSegment beneficiarySegment : getAllBeneficiarySegments(form, activity)) {
            Map<String, String> patientIdScheme = registrationActivity.getPatientIdScheme();
            String motechId = idResolver.retrieveId(patientIdScheme, beneficiarySegment);
            if (motechId == null) {
                logger.error("MotechId could not be obtained");
                return;
            }
            MRSPatient patient = mrsPatientAdapter.getPatientByMotechId(motechId);
            String facilityName = registrationActivity.getValueFor(FACILITY_NAME_FIELD, beneficiarySegment, String.class);
            MRSFacility facility = getMRSFacility(registrationActivity.getFacilityScheme(), facilityName, beneficiarySegment);
            addOrUpdatePatient(facility, motechId, patient, registrationActivity, beneficiarySegment);
        }
    }

    private void addOrUpdatePatient(MRSFacility facility, String motechId, MRSPatient patient, MRSRegistrationActivity activity, CommcareFormSegment beneficiarySegment) {
        MRSPerson person;
        if (patient == null) {
            String id = UUID.randomUUID().toString();
            person = personAdapter.createPerson(activity, beneficiarySegment);
            person.setPersonId(id);

            patient = new MRSPatientDto(id, facility, person, motechId);
            try {
                List<ValidationError> validationErrors = validator.validatePatient(patient);
                if (validationErrors.size() == 0) {
                    mrsPatientAdapter.savePatient(patient);
                    logger.info(String.format("Registered new patient by MotechId(%s) and PatientId(%s)", motechId, id));
                } else {
                    logger.error("Could not save patient due to validation errors");
                }
            } catch (MRSException e) {
                logger.info("Could not save patient: " + e.getMessage());
            }
        } else {
            person = patient.getPerson();
            patient.setFacility(facility);
            updatePatient(patient, person, activity, beneficiarySegment);
        }
    }



    private MRSFacility getMRSFacility(Map<String, String> facilityIdScheme, String facilityName, CommcareFormSegment beneficiarySegment) {
        if (facilityName == null) {
            facilityName = idResolver.retrieveId(facilityIdScheme, beneficiarySegment);
        }

        if (facilityName == null) {
            logger.warn("No facility name provided, using " + DEFAULT_FACILITY);
            facilityName = DEFAULT_FACILITY;
        }

        MRSFacility facility = mrsUtil.findFacility(facilityName);

        logger.info("Facility name: " + facilityName);
        return facility;
    }

    private void updatePatient(MRSPatient patient, MRSPerson person, MRSRegistrationActivity activity, CommcareFormSegment beneficiarySegment) {
        personAdapter.updatePerson(person, activity, beneficiarySegment);
        List<ValidationError> validationErrors = validator.validatePatient(patient);
        if (validationErrors.size() == 0) {
            mrsPatientAdapter.updatePatient(patient);
            logger.info("Patient already exists, updated patient: " + patient.getMotechId());
        } else {
            logger.error("Could not update patient due to validation errors");
        }
    }
}
