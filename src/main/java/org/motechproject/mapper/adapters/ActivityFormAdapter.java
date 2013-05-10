package org.motechproject.mapper.adapters;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.domain.MRSActivity;

/**
 * Adapts a particular form by an activity type, such as encounters, registrations, drugs orders, etc.
 */
public abstract class ActivityFormAdapter {

    public abstract void adaptForm(CommcareForm form, MRSActivity activity);

    public Multimap<String, FormValueElement> getTopFormElements(MRSActivity activity, FormValueElement rootElement) {
        Multimap<String, FormValueElement> rootElementMap = new LinkedHashMultimap<>();
        if (activity.getFormMapperProperties().getMultiple()) {
            rootElementMap.putAll(rootElement.getSubElements());
        } else {
            rootElementMap.put(rootElement.getElementName(), rootElement);
        }
        return rootElementMap;
    }
}
