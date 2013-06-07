package org.motechproject.mapper.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.mapper.domain.MRSMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@View(name = "by_form_name_and_xmlns", map = "function(doc) {if(doc.type == 'MRSMapping') {emit(doc.formName, doc.xmlns)}}")
public class AllMRSMappings extends MotechBaseRepository<MRSMapping> {

    @Autowired
    protected AllMRSMappings(@Qualifier("mapperDbConnector") CouchDbConnector db) {
        super(MRSMapping.class, db);
        initStandardDesignDocument();
    }

    public void addOrUpdate(MRSMapping mrsMapping) {
        String fieldName = "xmlns";
        addOrReplace(mrsMapping, fieldName, mrsMapping.getXmlns());
    }

    @View(name = "by_xmlns", map = "function(doc) { if(doc.type === 'MRSMapping'){ emit(doc.xmlns,doc._id); }}")
    public MRSMapping findByXmlns(String xmlns) {
        ViewQuery viewQuery = createQuery("by_xmlns").key(xmlns).includeDocs(true);
        List<MRSMapping> mrsMappings = db.queryView(viewQuery, MRSMapping.class);
        return mrsMappings.size() == 0 ? null : mrsMappings.get(0);
    }
}
