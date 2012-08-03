package org.motechproject.scheduletrackingdemo.provider;

import org.motechproject.mobileforms.api.service.FormProvider;
import org.motechproject.scheduletrackingdemo.OpenMrsClient;
import org.motechproject.scheduletrackingdemo.beans.PatientEncounterBean;
import org.motechproject.scheduletrackingdemo.validator.PatientEncounterValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("patientEncounterFormProvider")
public class PatientEncounterFormProvider implements FormProvider<PatientEncounterBean, PatientEncounterValidator> {

    private PatientEncounterValidator patientEncounterValidator;

    @Autowired
    public PatientEncounterFormProvider(PatientEncounterValidator patientEncounterValidator) {
        this.patientEncounterValidator = patientEncounterValidator;
    }

    @Override
    public PatientEncounterBean makeInstance() {
        return new PatientEncounterBean();
    }

    @Override
    public PatientEncounterValidator getValidator() {
        return patientEncounterValidator;
    }

    @Override
    public boolean isFormProviderFor(String formBeanName) {
        return PatientEncounterBean.class.getName().equals(formBeanName);
    }

    @Override
    public boolean isValidatorFor(String validatorName) {
        return PatientEncounterValidator.class.getName().equals(validatorName);
    }
}
