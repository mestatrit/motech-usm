package org.motechproject.openmrs.atomfeed.repository;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.motechproject.model.MotechBaseDataObject;

@TypeDiscriminator("doc.type === 'AtomFeedUpdate'")
public class AtomFeedUpdate extends MotechBaseDataObject {

    private static final long serialVersionUID = -3867362351258037767L;

    @JsonProperty
    private String lastUpdateTime;
    
    @JsonProperty
    private String lastId;
    
    public AtomFeedUpdate(String lastUpdateTime, String lastId) {
        this.lastUpdateTime = lastUpdateTime;
        this.lastId = lastId;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastId() {
        return lastId;
    }

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }
}
