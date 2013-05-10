package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.FormValueElement;

public interface SearchStrategy {
    FormValueElement search(FormValueElement formValueElement);
}
