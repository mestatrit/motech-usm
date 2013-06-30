package org.motechproject.mapper.util;

import org.motechproject.commcare.domain.CaseInfo;
import org.motechproject.commcare.domain.CommcareUser;
import org.motechproject.commcare.domain.FormNode;
import org.motechproject.commcare.domain.FormValueElement;
import org.motechproject.commcare.service.CommcareCaseService;
import org.motechproject.commcare.service.CommcareUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.motechproject.mapper.constants.FormMappingConstants.*;

@Component
public class IdentityResolver {

    private CommcareCaseService caseService;
    private CommcareUserService userService;

    @Autowired
    public IdentityResolver(CommcareCaseService caseService, CommcareUserService userService) {
        this.caseService = caseService;
        this.userService = userService;
    }

    public String getCaseId(FormValueElement formValueElement, String openMrsPatientIdentifier) {
        String caseId = formValueElement.getAttributes().get(CASE_ID_ATTRIBUTE);
        CaseInfo caseInfo = caseService.getCaseByCaseId(caseId);
        return caseInfo.getFieldValues().get(openMrsPatientIdentifier);
    }

    public String retrieveId(Map<String, String> idScheme, CommcareFormSegment beneficiarySegment) {
        String id = null;

        if (idScheme != null) {
            String idSchemeType = idScheme.get(ID_SCHEME_TYPE);
            String idFieldName = idScheme.get(ID_SCHEME_FIELD);
            String idAttributeName = idScheme.get(ID_SCHEME_ATTRIBUTE);

            if (ID_FROM_FORM_SCHEME.equals(idSchemeType) && idAttributeName != null) {
                FormNode element = beneficiarySegment.search(idFieldName);
                if(element == null) {
                    return null;
                }
                id = ((FormValueElement) element).getAttributes().get(idAttributeName);
            } else if (ID_FROM_FORM_SCHEME.equals(idSchemeType)) {
                id = beneficiarySegment.search(idFieldName).getValue();
            } else if (ID_FROM_COMMCARE_CASE_SCHEME.equals(idSchemeType)) {
                id = getCaseId((FormValueElement) beneficiarySegment.search(CASE_ELEMENT), idFieldName);
            } else if (ID_FROM_USER_DATA_SCHEME.equals(idSchemeType)) {
                id = getIdFromUser(idFieldName, beneficiarySegment.getMetadata(USER_ID));
            } else if (ID_FROM_USER_ID_SCHEME.equals(idSchemeType)) {
                id = beneficiarySegment.getMetadata(USER_ID);
            } else if (ID_FROM_USERNAME_SCHEME.equals(idSchemeType)) {
                id = beneficiarySegment.getMetadata(FORM_USERNAME);
            }
        }
        return id;
    }

    public String getIdFromUser(String idFieldName, String userId) {
        CommcareUser user = userService.getCommcareUserById(userId);
        return user == null ? null : user.getUserData().get(idFieldName);
    }
}
