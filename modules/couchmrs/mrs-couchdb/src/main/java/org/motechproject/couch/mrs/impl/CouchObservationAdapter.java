package org.motechproject.couch.mrs.impl;


import org.motechproject.couch.mrs.model.CouchObservationImpl;
import org.motechproject.couch.mrs.repository.AllCouchObservations;
import org.motechproject.couch.mrs.util.CouchDAOBroker;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.services.ObservationAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class CouchObservationAdapter implements ObservationAdapter {

    @Autowired
    private AllCouchObservations allCouchObservations;

    @Autowired
    private CouchDAOBroker daoBroker;

    @Override
    public void voidObservation(Observation mrsObservation, String reason, String mrsUserMotechId)
            throws ObservationNotFoundException {

        Observation obs = getObservationById(mrsObservation.getObservationId());

        if (obs == null) {
            throw new ObservationNotFoundException("The observation with id: " + mrsObservation.getObservationId() + " was not found in the Couch database");
        }

        allCouchObservations.removeObservation(obs);
    }

    @Override
    public Observation findObservation(String patientMotechId, String conceptName) {
        return daoBroker.returnObs(allCouchObservations.findByMotechIdAndConceptName(patientMotechId, conceptName));
    }

    @Override
    public List<Observation> findObservations(String patientMotechId, String conceptName) {
        List<CouchObservationImpl> obsList = allCouchObservations.findByMotechIdAndConceptName(patientMotechId, conceptName);

        return generateObsList(obsList);
    }

    private List<Observation> generateObsList(List<CouchObservationImpl> obsList) {

        List<Observation> observations = new ArrayList<Observation>();

        if (obsList != null && obsList.size() > 0) {
            for (CouchObservationImpl obs : obsList) {
                observations.add(daoBroker.buildFullObservation(obs));
            }
        }

        return observations;
    }

    @Override
    public Observation getObservationById(String observationId) {
        return daoBroker.returnObs(allCouchObservations.findByObservationId(observationId));
    }
}
