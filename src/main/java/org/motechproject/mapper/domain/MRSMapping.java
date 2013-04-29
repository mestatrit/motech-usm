package org.motechproject.mapper.domain;

import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.List;

@TypeDiscriminator("doc.type === 'MRSMapping'")
public class MRSMapping extends MotechBaseDataObject {

    private String formName;
    private String xmlns;
    private List<MRSActivity> activities;

    public String getFormName() {
        return formName;
    }

    public void setFormName(String formName) {
        this.formName = formName;
    }

    public List<MRSActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<MRSActivity> activities) {
        this.activities = activities;
    }

    public String getXmlns() {
        return xmlns;
    }

    public void setXmlns(String xmlns) {
        this.xmlns = xmlns;
    }
}
