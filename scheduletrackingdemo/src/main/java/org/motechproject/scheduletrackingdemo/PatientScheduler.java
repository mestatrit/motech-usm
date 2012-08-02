package org.motechproject.scheduletrackingdemo;

import java.util.ArrayList;
import java.util.List;
import org.joda.time.LocalTime;
import org.motechproject.model.Time;
import org.motechproject.scheduletrackingdemo.DAO.MRSPatientDAO;
import org.motechproject.scheduletrackingdemo.model.Patient;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.Schedule;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * This class provides an API for tasks relating to MoTeCH patients and schedule
 * tracking
 */
@Component
public class PatientScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PatientScheduler.class);

    @Autowired
    private MRSPatientDAO patientDAO;

    @Autowired
    private OpenMrsClient openMrsClient;

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    private AllSchedules allSchedules;

    public void enrollIntoSchedule(String externalID, String scheduleName) {
        if (!patientDAO.findByExternalid(externalID).isEmpty()
                && openMrsClient.getPatientByMotechId(externalID) != null) { // do
            // not
            // let
            // users
            // that
            // aren't
            // in
            // both
            // databases
            // register
            Schedule schedule = scheduleTrackingService.getSchedule(scheduleName);

            if (schedule == null)
                throw new RuntimeException("No schedule found with name: " + scheduleName);

            String lastConceptFulfilled = "";
            String checkConcept;

            for (Milestone milestone : schedule.getMilestones()) {
                checkConcept = milestone.getData().get("conceptName");
                if (checkConcept != null) {
                    if (openMrsClient.hasConcept(externalID, checkConcept)) {
                        System.out.println(lastConceptFulfilled);
                        lastConceptFulfilled = checkConcept;
                        System.out.println(lastConceptFulfilled);
                    }
                }
            }

            EnrollmentRequest enrollmentRequest;

            if (lastConceptFulfilled.equals("")) { // enroll in new schedule
                enrollmentRequest = new EnrollmentRequest();
                enrollmentRequest.setExternalId(externalID);
                enrollmentRequest.setScheduleName(scheduleName);
                enrollmentRequest.setEnrollmentDate(DateUtil.today());
                enrollmentRequest.setEnrollmentTime(new Time(LocalTime.now()));
                enrollmentRequest.setReferenceDate(DateUtil.today());
                enrollmentRequest.setReferenceTime(new Time(LocalTime.now()));
                enrollmentRequest.setStartingMilestoneName(schedule.getNextMilestoneName(lastConceptFulfilled));
            } else { // start at the next milestone
                EnrollmentsQuery query = new EnrollmentsQuery().havingSchedule(scheduleName).havingState(EnrollmentStatus.ACTIVE).havingExternalId(externalID);
                List<EnrollmentRecord> enrollmentRecords = scheduleTrackingService.search(query);
                if (enrollmentRecords.size() == 0) {
                    enrollmentRequest = new EnrollmentRequest();
                    enrollmentRequest.setExternalId(externalID);
                    enrollmentRequest.setScheduleName(scheduleName);
                    enrollmentRequest.setEnrollmentDate(DateUtil.today());
                    enrollmentRequest.setEnrollmentTime(new Time(LocalTime.now()));
                    enrollmentRequest.setReferenceDate(DateUtil.today());
                    enrollmentRequest.setReferenceTime(new Time(LocalTime.now()));
                    enrollmentRequest.setStartingMilestoneName(schedule.getNextMilestoneName(lastConceptFulfilled));
                } else { // Enrollment already exists, but now re-enrolling to
                    // whatever their latest last milestone fulfillment
                    // was, based on OpenMRS
                    List<String> scheduleNames = new ArrayList<String>();
                    scheduleNames.add(scheduleName);
                    scheduleTrackingService.unenroll(externalID, scheduleNames);
                    enrollmentRequest = new EnrollmentRequest();
                    enrollmentRequest.setExternalId(externalID);
                    enrollmentRequest.setScheduleName(scheduleName);
                    enrollmentRequest.setEnrollmentDate(DateUtil.today());
                    enrollmentRequest.setEnrollmentTime(new Time(LocalTime.now()));
                    enrollmentRequest.setReferenceDate(DateUtil.today());
                    enrollmentRequest.setReferenceTime(new Time(LocalTime.now()));
                    enrollmentRequest.setStartingMilestoneName(schedule.getNextMilestoneName(lastConceptFulfilled));
                }
            }

            scheduleTrackingService.enroll(enrollmentRequest);
        }
    }

    public void saveMotechPatient(String externalID, String phoneNum) {
        List<Patient> patientList = null;

        if (externalID.length() == 0 || externalID.equals("") || externalID.trim().length() == 0) {
            // Don't register empty string IDs
        } else {
            patientList = patientDAO.findByExternalid(externalID); // Only one
            // patient
            // should be
            // returned
            // if ID is
            // unique,
            // but it is
            // returned
            // as list
            if (patientList.size() > 0) { // Patient already exists, so it is
                // updated
                Patient thePatient = patientList.get(0);
                thePatient.setPhoneNum(phoneNum);
                patientDAO.update(thePatient);
            } else {
                patientDAO.add(new Patient(externalID, phoneNum));
            }
        }
    }
}
