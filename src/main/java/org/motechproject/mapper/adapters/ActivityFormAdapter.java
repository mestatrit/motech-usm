package org.motechproject.mapper.adapters;

import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.motechproject.mapper.domain.FormMapperProperties;
import org.motechproject.mapper.domain.MRSActivity;
import org.motechproject.mapper.util.AllElementSearchStrategies;
import org.motechproject.mapper.util.CommcareFormSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adapts a particular form by an activity type, such as encounters, registrations, drugs orders, etc.
 */
public abstract class ActivityFormAdapter {

    private static final Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

    private AllElementSearchStrategies allElementSearchStrategies;

    public ActivityFormAdapter(AllElementSearchStrategies allElementSearchStrategies) {
        this.allElementSearchStrategies = allElementSearchStrategies;
    }

    public abstract void adaptForm(CommcareForm form, MRSActivity activity);

    protected List<CommcareFormSegment> getAllBeneficiarySegments(CommcareForm form, MRSActivity activity) {
        List<CommcareFormSegment> beneficiarySegments = new ArrayList<>();
        FormMapperProperties formMapperProperties = activity.getFormMapperProperties();
        String startElementPath = formMapperProperties.getStartElement();

        FormValueElement rootElement = form.getForm();
        List<FormNode> startElements = allElementSearchStrategies.search(startElementPath, rootElement, rootElement, null);

        if (startElements.size() == 0) {
            logger.info(String.format("Cannot find the start node(%s) in the form(%s). Ignoring this form.", startElementPath, form.getId()));
            return beneficiarySegments;
        }

        for(FormNode startElement : startElements) {
            beneficiarySegments.add(new CommcareFormSegment(form, (FormValueElement) startElement, formMapperProperties.getRestrictedElements(), allElementSearchStrategies));
        }

        return beneficiarySegments;
    }

    protected  void handleEmptyMotechId(CommcareForm form, Map<String, String> patientIdScheme) {
        String ignoreMessage = String.format("Motech id is empty for form(%s). Ignoring this form.", form.getId());
        if(shouldReportMissingId(patientIdScheme)) {
            logger.error(ignoreMessage);
            return;
        }
        logger.info(ignoreMessage);
    }

    private boolean shouldReportMissingId(Map<String, String> patientIdScheme) {
        if(patientIdScheme != null && "false".equalsIgnoreCase(patientIdScheme.get(FormMappingConstants.REPORT_MISSING_ID))) {
            return false;
        }
        return true;
    }
}
