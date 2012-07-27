package org.motechproject.CampaignDemo.dao;

import java.util.List;

import org.motechproject.CampaignDemo.model.Patient;
import org.motechproject.dao.BaseDao;

/**
 * DAO for adding, updating, removing, and accessing patients in CouchDB
 * Patients associate an external ID with a phone number in order for Voxeo IVR
 * calls to be placed for campaign messages
 */

public interface PatientDAO extends BaseDao<Patient> {

    void addPatient(Patient patient);

    void updatePatient(Patient patient);

    Patient getPatient(String externalid);

    void removePatient(String externalid);

    void removePatient(Patient patient);

    List<Patient> findByExternalid(String externalid);

    List<Patient> findAllPatients();

}
