package org.motechproject.openmrs.atomfeed.repository;

import java.util.List;

import org.apache.log4j.Logger;
import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AtomFeedDaoImpl extends MotechBaseRepository<AtomFeedUpdate> implements AtomFeedDao {
    private static final Logger LOGGER = Logger.getLogger(AtomFeedDaoImpl.class);
    
    @Autowired
    protected AtomFeedDaoImpl(CouchDbConnector db) {
        super(AtomFeedUpdate.class, db);
    }

    @Override
    public void setLastUpdateTime(String id, String lastUpdateTime) {
        LOGGER.debug("Atom Feed Update: [id=" + id + ", lastUpdateTime=" + lastUpdateTime +"]");
        removeAll();
        add(new AtomFeedUpdate(lastUpdateTime, id));
    }

    @Override
    public String getLastUpdateTime() {
        List<AtomFeedUpdate> updates = getAll(1);
        if (updates.isEmpty()) {
            return null;
        }
        
        return updates.get(0).getLastUpdateTime();
    }

    @Override
    public String getLastId() {
        List<AtomFeedUpdate> updates = getAll(1);
        if (updates.isEmpty()) {
            return null;
        }
        
        return updates.get(0).getLastId();
    }

    
}
