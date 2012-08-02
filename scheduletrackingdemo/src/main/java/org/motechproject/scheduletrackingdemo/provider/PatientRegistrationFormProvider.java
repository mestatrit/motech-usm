package org.motechproject.scheduletrackingdemo.provider;

import org.motechproject.mobileforms.api.service.FormProvider;
import org.motechproject.scheduletrackingdemo.beans.PatientRegistrationBean;
import org.motechproject.scheduletrackingdemo.validator.PatientRegistrationValidator;
import org.springframework.stereotype.Component;

@Component("patientRegistrationFormProvider")
public class PatientRegistrationFormProvider implements
        FormProvider<PatientRegistrationBean, PatientRegistrationValidator> {

    @Override
    public PatientRegistrationBean makeInstance() {
        return new PatientRegistrationBean();
    }

    @Override
    public PatientRegistrationValidator getValidator() {
        return new PatientRegistrationValidator();
    }

    @Override
    public boolean isFormProviderFor(String formBeanName) {
        return PatientRegistrationBean.class.getName().equals(formBeanName);
    }

    @Override
    public boolean isValidatorFor(String validatorName) {
        return PatientRegistrationValidator.class.getName().equals(validatorName);
    }

}
