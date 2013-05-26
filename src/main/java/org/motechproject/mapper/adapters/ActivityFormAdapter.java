package org.motechproject.mapper.adapters;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.domain.FormMapperProperties;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.util.FormTraversalProperty;
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

    private Multimap<String, FormValueElement> getTopFormElements(FormValueElement rootElement) {
        Multimap<String, FormValueElement> rootElementMap = new LinkedHashMultimap<>();
        List<FormValueElement> childElements = rootElement.getChildElements(rootElement.getElementName());
        rootElementMap.putAll(rootElement.getElementName(), childElements);
        if (rootElementMap.size() == 0) {
            rootElementMap.put(rootElement.getElementName(), rootElement);
        }

        return rootElementMap;
    }

    protected List<FormTraversalProperty> getAllFormTraversalProperty(CommcareForm form, MRSActivity activity) {
        List<FormTraversalProperty> formTraversalProperties = new ArrayList<>();
        FormMapperProperties formMapperProperties = activity.getFormMapperProperties();
        String startElementName = formMapperProperties.getStartElement();
        FormValueElement rootElement = form.getForm();
        FormValueElement startElement = (FormValueElement) SearchStrategyChooser.getFor(formMapperProperties.getStartElement()).search(rootElement, rootElement, formMapperProperties.getRestrictedElements());
        if (startElement == null) {
            logger.warn(String.format("Cannot find the start node(%s) in the form(%s)", startElementName, form.getId()));
            return formTraversalProperties;
        }
        for (Map.Entry<String, FormValueElement> topFormElements : getTopFormElements(startElement).entries()) {
            formTraversalProperties.add(new FormTraversalProperty(form, topFormElements.getValue(), formMapperProperties.getRestrictedElements()));

        }
        return formTraversalProperties;
    }
}
