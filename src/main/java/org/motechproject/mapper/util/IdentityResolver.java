package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CommcareForm;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.commcare.service.CommcareUserService;
import org.motechproject.mapper.constants.FormMappingConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IdentityResolver {

    @Autowired
    private CommcareCaseService caseService;
    @Autowired
    private CommcareUserService userService;

    public String getCaseId(FormValueElement formValueElement, String openMrsPatientIdentifier) {

        String caseId = formValueElement.getAttributes().get(FormMappingConstants.CASE_ID_ATTRIBUTE);

        CaseInfo caseInfo = caseService.getCaseByCaseId(caseId);

        return caseInfo.getFieldValues().get(openMrsPatientIdentifier);
    }

    public String retrieveId(Map<String, String> idScheme, CommcareForm form) {
        String id = null;
        FormValueElement element = form.getForm();

        if (idScheme != null) {
            String idSchemeType = idScheme.get(FormMappingConstants.ID_SCHEME_TYPE);
            String idFieldName = idScheme.get(FormMappingConstants.ID_SCHEME_FIELD);
            String idAttributeName = idScheme.get(FormMappingConstants.ID_SCHEME_ATTRIBUTE);

            if (FormMappingConstants.ID_FROM_FORM_SCHEME.equals(idSchemeType) && idAttributeName != null) {
                id = element.getElementByName(idFieldName).getAttributes().get(idAttributeName);
            } else if (FormMappingConstants.ID_FROM_FORM_SCHEME.equals(idSchemeType)) {
                id = element.getElementByName(idFieldName).getValue();
            } else if (FormMappingConstants.ID_FROM_COMMCARE_CASE_SCHEME.equals(idSchemeType)) {
                id = getCaseId(element.getElementByName(FormMappingConstants.CASE_ELEMENT), idFieldName);
            } else if (FormMappingConstants.ID_FROM_USER_DATA_SCHEME.equals(idSchemeType)) {
                id = getIdFromUser(idFieldName, form.getMetadata().get(FormMappingConstants.USER_ID));
            } else if (FormMappingConstants.ID_FROM_USER_ID_SCHEME.equals(idSchemeType)) {
                id = form.getMetadata().get(FormMappingConstants.USER_ID);
            } else if (FormMappingConstants.ID_FROM_USERNAME_SCHEME.equals(idSchemeType)) {
                id = form.getMetadata().get(FormMappingConstants.FORM_USERNAME);
            }
        }

        return id;
    }

    public String getIdFromUser(String idFieldName, String userId) {
        CommcareUser user = userService.getCommcareUserById(userId);
        return user.getUserData().get(idFieldName);
    }
}
