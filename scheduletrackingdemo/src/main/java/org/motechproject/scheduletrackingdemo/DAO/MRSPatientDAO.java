package org.motechproject.scheduletrackingdemo.DAO;

import org.motechproject.dao.BaseDao;
import org.motechproject.scheduletrackingdemo.model.Patient;

import java.util.List;

/*
 * DAO for adding, updating, removing, and accessing patients in CouchDB
 * Patients associate an external ID with a phone number in order for Voxeo IVR
 * calls to be placed for campaign messages.
 */

public interface MRSPatientDAO extends BaseDao<Patient> {

    void addPatient(Patient patient);

    void updatePatient(Patient patient);

    Patient getPatient(String externalid);

    void removePatient(String externalid);

    void removePatient(Patient patient);

    List<Patient> findByExternalid(String externalid);

    List<Patient> findAllPatients();

}
