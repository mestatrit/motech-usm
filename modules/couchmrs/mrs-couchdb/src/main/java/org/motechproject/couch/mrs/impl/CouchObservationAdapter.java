package org.motechproject.couch.mrs.impl;


import org.motechproject.couch.mrs.model.CouchObservation;
import org.motechproject.couch.mrs.model.CouchObservationImpl;
import org.motechproject.couch.mrs.repository.AllCouchObservations;
import org.motechproject.mrs.domain.Observation;
import org.motechproject.mrs.exception.ObservationNotFoundException;
import org.motechproject.mrs.services.ObservationAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Component
public class CouchObservationAdapter implements ObservationAdapter {

    @Autowired
    private AllCouchObservations allCouchObservations;

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
        return returnObs(allCouchObservations.findByMotechIdAndConceptName(patientMotechId, conceptName));
    }

    private Observation returnObs(List<CouchObservationImpl> couchObs) {

        if (couchObs != null && couchObs.size() > 0) {
            CouchObservationImpl obs = couchObs.get(0);
            return convertImplToCouchObs(obs);
        }

        return null;
    }

    private Observation convertImplToCouchObs(CouchObservationImpl obs) {
        Set<Observation> dependantObs = new HashSet<Observation>();
        if (obs.getDependantObservationIds() != null && obs.getDependantObservationIds().size() > 1) {
            dependantObs = getDependantObsById(obs.getDependantObservationIds());
        }

        CouchObservation couchObservation = new CouchObservation(obs.getObservationId(), obs.getDate(), obs.getConceptName(), obs.getValue());
        couchObservation.setDependantObservations(dependantObs);

        return couchObservation;
    }

    private Set<Observation> getDependantObsById(Set<String> dependantObservationIds) {
        Set<Observation> dependantObs = new HashSet<Observation>();

        Iterator<String> iterator = dependantObservationIds.iterator();

        while (iterator.hasNext()) {
            String obsId = iterator.next();
            Observation obs = getObservationById(obsId);
            if (obs != null) {
                dependantObs.add(obs);
            }
        }

        return dependantObs;

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
                observations.add(convertImplToCouchObs(obs));
            }
        }

        return observations;
    }

    @Override
    public Observation getObservationById(String observationId) {
        return returnObs(allCouchObservations.findByObservationId(observationId));
    }
}
