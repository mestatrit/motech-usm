package org.motechproject.scheduletrackingdemo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.motechproject.mrs.exception.MRSException;
import org.motechproject.mrs.model.MRSEncounter;
import org.motechproject.mrs.model.MRSFacility;
import org.motechproject.mrs.model.MRSObservation;
import org.motechproject.mrs.model.MRSPatient;
import org.motechproject.mrs.services.MRSEncounterAdapter;
import org.motechproject.mrs.services.MRSFacilityAdapter;
import org.motechproject.mrs.services.MRSObservationAdapter;
import org.motechproject.mrs.services.MRSPatientAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OpenMrsClientImpl implements OpenMrsClient {
    private static Logger logger = LoggerFactory.getLogger(OpenMrsClientImpl.class);
    private MRSEncounterAdapter encounterAdapter;
    private MRSPatientAdapter patientAdapter;
    private MRSObservationAdapter observationAdapter;
    private MRSFacilityAdapter facilityAdapter;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    // TODO: Lookup provider id using the MRSUser service
    private static final String PROVIDER_UUID = "0d64f93e-e25f-4766-ac9e-59023135523c";
    
    @Autowired
    public OpenMrsClientImpl(MRSEncounterAdapter encounterAdapter, MRSPatientAdapter patientAdapter,
            MRSObservationAdapter observationAdapter, MRSFacilityAdapter facilityAdapter) {
        this.encounterAdapter = encounterAdapter;
        this.patientAdapter = patientAdapter;
        this.observationAdapter = observationAdapter;
        this.facilityAdapter = facilityAdapter;
    }

    public boolean hasConcept(String patientId, String conceptName) {
        List<MRSObservation> observationList = null;
        try {
            observationList = observationAdapter.findObservations(patientId, conceptName);
        } catch (MRSException e) {
            logger.error("Could not retrieve observations for patient while looking for concept");
            return false;
        }

        boolean found = false;
        if (observationList.size() > 0) {
            found = true;
        }
        
        return found;
    }

    public DateTime lastTimeFulfilledDateTimeObs(String patientId, String conceptName) {
        List<MRSObservation> mrsObservations = null;
        try {
            mrsObservations = observationAdapter.findObservations(patientId, conceptName);
        } catch(MRSException e) {
            logger.error("Could not retrieve observations for patient while looking for last fulfillment date");
            return new DateTime();
        }
        
        // observations values are returned as string types, need to convert to dates
        List<MRSObservation<Date>> convertedObs = new ArrayList<>();
        for (MRSObservation obs : mrsObservations) {
            try {
                convertedObs.add(new MRSObservation<Date>(obs.getId(), obs.getDate(), obs.getConceptName(), DATE_FORMAT
                        .parse(obs.getValue().toString())));
            } catch (ParseException e) {
                logger.warn("Found an observation that did not have a date as it's value");
            }
        }
        Collections.sort(convertedObs, new DateComparator());

        if (convertedObs.size() > 0) {
            return new DateTime(convertedObs.get(0).getValue());
        }
        return new DateTime();

    }

    public MRSPatient getPatientByMotechId(String patientId) {
        try {
            return patientAdapter.getPatientByMotechId(patientId);
        } catch (MRSException e) {
            logger.error("There was a problem getting an OpenMRS patient by motech id");
        }
        return null;
    }

    private class DateComparator implements Comparator<MRSObservation<Date>> {

        @Override
        public int compare(MRSObservation<Date> o1, MRSObservation<Date> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }

    }

    public void savePatient(MRSPatient patient) {
        try {
            patientAdapter.savePatient(patient);
        } catch(MRSException e) {
            logger.error("There was a problem saving patient to OpenMRS");
        }
    }

    public void addEncounterForPatient(String motechId, String conceptName, String facilityName, Date observedDate) {
        MRSObservation<String> observation = new MRSObservation<String>(observedDate, conceptName,
                DATE_FORMAT.format(observedDate));
        Set<MRSObservation> observations = new HashSet<MRSObservation>();
        observations.add(observation);
        MRSPatient patient = null;
        try {
            patient = patientAdapter.getPatientByMotechId(motechId);
        } catch(MRSException e) {
            logger.error("Failed to retrieve patient while adding an encounter");
            return;
        }
        
        MRSFacility facility = findFacility(facilityName);
        
        if (facility == null) {
            logger.error("Couldn't create encounter because could not find facility");
            return;
        }
        
        MRSEncounter encounter = new MRSEncounter(PROVIDER_UUID, null, facility.getId(), observedDate, patient.getId(), observations,
                "ADULTRETURN");
        try {
            encounterAdapter.createEncounter(encounter);
        } catch(MRSException e) {
            logger.error("Could not create an encounter");
        }
    }

    @Override
    public MRSFacility findFacility(String location) {
        List<MRSFacility> facilities = null;
        try {
            facilities = facilityAdapter.getFacilities(location);
        } catch(MRSException e) {
            logger.error("Failed to retrieve facilities");
            return null;
        }
        if (facilities.size() == 0) {
            return null;
        } else if (facilities.size() > 1) {
            logger.warn("Multiple OpenMRS facilities found with name: " + location);
            logger.warn("Using first facility");
        }
        
        return facilities.get(0);
    }
}
