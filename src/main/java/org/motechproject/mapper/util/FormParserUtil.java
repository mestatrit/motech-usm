//package org.motechproject.mapper.util;
//
//import java.util.Collection;
//import java.util.List;
//import org.motechproject.commcare.domain.FormValueElement;
//
//public final class FormParserUtil {
//
//    public static String processElementValue(Collection<FormValueElement> collection, String elementName) {
//        List<FormValueElement> list = (List<FormValueElement>) collection;
//
//        if (list.size() == 0) {
//            return null;
//        }
//
//        FormValueElement firstElement = list.get(0);
//
//        String value = firstElement.getValue();
//
//        if (value == null || value.trim().length() == 0) {
//            return null;
//        }
//
//        return value;
//    }
//
//    public static String getConceptId(Collection<FormValueElement> collection, String elementName) {
//        List<FormValueElement> list = (List<FormValueElement>) collection;
//
//        if (list.size() == 0) {
//            return null;
//        }
//
//        FormValueElement firstElement = list.get(0);
//
//        String conceptId = firstElement.getAttributes().get("@concept_id");
//
//        if (conceptId == null || conceptId.trim().length() == 0) {
//            return null;
//        }
//
//        return conceptId;
//    }
//
//    private FormParserUtil() {
//    }
//}
