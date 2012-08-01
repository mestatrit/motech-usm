package org.motechproject.mobileforms.api.osgi;

import org.motechproject.mobileforms.api.domain.FormBean;
import org.motechproject.mobileforms.api.validator.FormValidator;

public interface FormsProvider {

    <T extends FormBean> T makeInstance(String formBeanName);
    
    <T extends FormValidator<? extends FormBean>> T getValidator(String formBean);
    
    boolean isFormProviderFor(String formBeanName);
    
    boolean hasValidator(String validatorName);
}
