package org.motechproject.mapper.domain;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.List;

@TypeDiscriminator("doc.type === 'MRSMapping'")
public class MRSMapping extends MotechBaseDataObject implements Comparable<MRSMapping> {

    public static final String WILDCARD_VERSION = "*";

    private String formName;
    private String xmlns;
    private String version;

    private List<MRSActivity> activities;

    public MRSMapping() {
        this.version = WILDCARD_VERSION;
    }

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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public int compareTo(MRSMapping o) {
        if(o == null) {
            return 1;
        }
        return new CompareToBuilder()
                .append(this.xmlns, o.xmlns)
                .append(this.version, o.version)
                .toComparison();
    }

    public boolean hasWildcardVersion() {
        return version == null || WILDCARD_VERSION.equals(version);
    }

    public boolean matchesVersion(String toMatch) {
        return hasWildcardVersion() || version.equals(toMatch);
    }
}
