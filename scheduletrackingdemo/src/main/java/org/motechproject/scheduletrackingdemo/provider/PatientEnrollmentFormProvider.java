package org.motechproject.scheduletrackingdemo.provider;

import org.motechproject.mobileforms.api.service.FormProvider;
import org.motechproject.scheduletrackingdemo.beans.PatientEnrollmentBean;
import org.motechproject.scheduletrackingdemo.validator.PatientEnrollmentValidator;
import org.springframework.stereotype.Component;

@Component("patientEnrollmentFormProvider")
public class PatientEnrollmentFormProvider implements FormProvider<PatientEnrollmentBean, PatientEnrollmentValidator> {

    @Override
    public PatientEnrollmentBean makeInstance() {
        return new PatientEnrollmentBean();
    }

    @Override
    public PatientEnrollmentValidator getValidator() {
        return new PatientEnrollmentValidator();
    }

    @Override
    public boolean isFormProviderFor(String formBeanName) {
        return PatientEnrollmentBean.class.getName().equals(formBeanName);
    }

    @Override
    public boolean isValidatorFor(String validatorName) {
        return PatientEnrollmentValidator.class.getName().equals(validatorName);
    }
}
