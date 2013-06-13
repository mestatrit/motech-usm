package org.motechproject.mapper.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.mapper.domain.MRSMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "by_form_name_and_xmlns_and_version", map = "function(doc) {if(doc.type == 'MRSMapping') { emit([doc.formName, doc.xmlns, doc.version], doc._id); }}")
public class AllMRSMappings extends MotechBaseRepository<MRSMapping> {

    @Autowired
    protected AllMRSMappings(@Qualifier("mapperDbConnector") CouchDbConnector db) {
        super(MRSMapping.class, db);
        initStandardDesignDocument();
    }

    @View(name = "by_xmlns_and_version", map = "function(doc) { if(doc.type === 'MRSMapping') { emit([doc.xmlns, doc.version], doc._id); }}")
    public MRSMapping findByXmlnsAndVersion(String xmlns, String version) {
        return singleResult(queryView("by_xmlns_and_version", ComplexKey.of(xmlns, version)));
    }

    @View(name = "by_xmlns", map = "function(doc) { if(doc.type === 'MRSMapping'){ emit(doc.xmlns, doc._id); }}")
    public List<MRSMapping> findByXmlns(String xmlns) {
        return queryView("by_xmlns", xmlns);
    }

    public boolean deleteMapping(String id) {
        if(contains(id)) {
            remove(get(id));
            return true;
        }
        return false;
    }
}
