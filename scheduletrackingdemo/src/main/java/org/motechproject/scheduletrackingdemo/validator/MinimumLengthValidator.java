package org.motechproject.scheduletrackingdemo.validator;

import org.motechproject.mobileforms.api.domain.FormError;
import org.motechproject.mobileforms.api.validator.FieldValidator;

public class MinimumLengthValidator implements FieldValidator<MinimumLength> {

    @Override
    public FormError validate(Object fieldValue, String fieldName, Class fieldType, MinimumLength annotation) {
        if (fieldValue != null) {
            String value = fieldValue.toString();
            if (value.length() < annotation.size()) {
                return new FormError(fieldName, "This field must be at least " + annotation.size() + " characters long");
            }
        }

        return null;
    }

}
