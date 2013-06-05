package org.motechproject.mapper.adapters;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.domain.FormMapperProperties;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.util.AllElementSearchStrategies;
import org.motechproject.mapper.util.CommcareFormBeneficiarySegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapts a particular form by an activity type, such as encounters, registrations, drugs orders, etc.
 */
public abstract class ActivityFormAdapter {

    protected Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");
    private AllElementSearchStrategies allElementSearchStrategies;

    public ActivityFormAdapter(AllElementSearchStrategies allElementSearchStrategies) {
        this.allElementSearchStrategies = allElementSearchStrategies;
    }

    public abstract void adaptForm(CommcareForm form, MRSActivity activity);

    protected List<CommcareFormBeneficiarySegment> getAllBeneficiarySegments(CommcareForm form, MRSActivity activity) {
        List<CommcareFormBeneficiarySegment> beneficiarySegments = new ArrayList<>();
        FormMapperProperties formMapperProperties = activity.getFormMapperProperties();
        String startElementPath = formMapperProperties.getStartElement();

        FormValueElement rootElement = form.getForm();
        List<FormNode> startElements = allElementSearchStrategies.search(startElementPath, rootElement, rootElement, null);

        if (startElements.size() == 0) {
            logger.warn(String.format("Cannot find the start node(%s) in the form(%s)", startElementPath, form.getId()));
            return beneficiarySegments;
        }

        for(FormNode startElement : startElements) {
            beneficiarySegments.add(new CommcareFormBeneficiarySegment(form, (FormValueElement) startElement, formMapperProperties.getRestrictedElements(), allElementSearchStrategies));
        }

        return beneficiarySegments;
    }
}
