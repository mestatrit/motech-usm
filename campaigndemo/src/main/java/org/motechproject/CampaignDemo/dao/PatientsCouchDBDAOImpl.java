package org.motechproject.CampaignDemo.dao;

import java.util.Collections;
import java.util.List;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.GenerateView;
import org.ektorp.support.View;
import org.motechproject.CampaignDemo.model.Patient;
import org.motechproject.dao.MotechBaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * See platform documentation for couch set up
 * 
 */
@Component
public class PatientsCouchDBDAOImpl extends MotechBaseRepository<Patient>
        implements PatientDAO {

    @Autowired
    public PatientsCouchDBDAOImpl(
            @Qualifier("patientDatabase") CouchDbConnector db) {
        super(Patient.class, db);
    }

    @Override
    public void addPatient(Patient patient) {
        db.create(patient);
    }

    @Override
    public void updatePatient(Patient patient) {
        db.update(patient);
    }

    @Override
    public Patient getPatient(String externalid) {
        Patient patient = db.get(Patient.class, externalid);
        return patient;
    }

    @Override
    public void removePatient(String externalid) {
        List<Patient> patientList = findByExternalid(externalid);
        if (patientList.size() == 0) {
            return;
        }
        Patient patient = patientList.get(0);
        if (patient == null) {
            return;
        }
        removePatient(patient);
    }

    @Override
    @GenerateView
    public List<Patient> findByExternalid(String externalid) {
        List<Patient> ret = queryView("by_externalid", externalid);
        if (null == ret) {
            ret = Collections.<Patient> emptyList();
        }

        return ret;
    }

    @Override
    public void removePatient(Patient patient) {
        db.delete(patient);
    }

    @Override
    @View(name = "findAllPatients", map = "function(doc) {if (doc.type == 'PATIENT') {emit(null, doc._id);}}")
    public List<Patient> findAllPatients() {
        List<Patient> ret = queryView("findAllPatients");
        if (null == ret) {
            ret = Collections.<Patient> emptyList();
        }
        return ret;
    }

}
