package org.motechproject.mobileforms.api.repository;

import java.util.List;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.mobileforms.api.domain.Form;
import org.motechproject.mobileforms.api.domain.FormGroup;
import org.springframework.stereotype.Repository;

@Repository
public class AllMobileForms extends MotechBaseRepository<FormGroup> {

    protected AllMobileForms(Class<FormGroup> type, CouchDbConnector db) {
        super(type, db);
    }

    public List<FormGroup> getAllFormGroups() {
        return getAll();
    }

    public FormGroup getFormGroup(Integer index) {
        return formGroups.get(index);
    }

    public Form getFormByName(String formName) {
        for (FormGroup formGroup : getAllFormGroups())
            for (Form form : formGroup.getForms())
                if (form.name().equalsIgnoreCase(formName)) return form;
        return null;
    }
}
