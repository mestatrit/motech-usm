package org.motechproject.scheduletrackingdemo.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.motechproject.mobileforms.api.validator.annotations.ValidationMarker;

@Target({ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@ValidationMarker(handler = MinimumLengthValidator.class)
public @interface MinimumLength {
    int size();
}
