package org.motechproject.mapper.adapters;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.domain.FormMapperProperties;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.util.CommcareMappingHelper;
import org.motechproject.mapper.util.SearchStrategyChooser;
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

    private Multimap<String, FormValueElement> getTopFormElements(MRSActivity activity, FormValueElement rootElement) {
        Multimap<String, FormValueElement> rootElementMap = new LinkedHashMultimap<>();
        if (activity.getFormMapperProperties().getMultiple()) {
            rootElementMap.putAll(rootElement.getSubElements());
        } else {
            rootElementMap.put(rootElement.getElementName(), rootElement);
        }
        return rootElementMap;
    }

    protected List<CommcareMappingHelper> getAllMappingHelpers(CommcareForm form, MRSActivity activity) {
        List<CommcareMappingHelper> mappingHelpers = new ArrayList<>();
        FormMapperProperties formMapperProperties = activity.getFormMapperProperties();
        String startElementName = formMapperProperties.getStartElement();
        FormValueElement rootElement = form.getForm();
        FormValueElement startElement = (FormValueElement) SearchStrategyChooser.getFor(formMapperProperties.getStartElement()).search(rootElement, rootElement, formMapperProperties.getRestrictedElements());
        if (startElement == null) {
            logger.error("Cannot find the start node in the form: " + startElementName);
            return mappingHelpers;
        }

        for (Map.Entry<String, FormValueElement> topFormElements : getTopFormElements(activity, startElement).entries()) {
            mappingHelpers.add(new CommcareMappingHelper(form, topFormElements.getValue(), formMapperProperties.getRestrictedElements()));

        }
        return mappingHelpers;
    }
}
