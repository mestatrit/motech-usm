package org.motechproject.mapper.adapters;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.util.CommcareMappingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adapts a particular form by an activity type, such as encounters, registrations, drugs orders, etc.
 */
public abstract class ActivityFormAdapter {

    protected Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

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

    protected List<CommcareMappingHelper> allStartElements(CommcareForm form, MRSActivity activity) {
        String startElementName = activity.getFormMapperProperties().getStartElement();
        FormValueElement rootElement = form.getForm();
        FormValueElement startElement = rootElement.getElementByName(startElementName);
        if (startElement == null) {
            logger.error("Cannot find the start node in the form: " + startElementName);
            return null;
        }

        List<CommcareMappingHelper> mappingHelpers = new ArrayList<>();
        for (Map.Entry<String, FormValueElement> topFormElements : getTopFormElements(activity, startElement).entries()) {
            mappingHelpers.add(new CommcareMappingHelper(form, topFormElements.getValue(), activity.getFormMapperProperties().getRestrictedElements()));

        }
        return mappingHelpers;
    }
}
