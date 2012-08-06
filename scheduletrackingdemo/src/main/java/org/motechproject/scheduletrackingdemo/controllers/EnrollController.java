package org.motechproject.scheduletrackingdemo.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.scheduletrackingdemo.DemoConstants;
import org.motechproject.scheduletrackingdemo.PatientScheduler;
import org.motechproject.scheduletrackingdemo.DAO.MRSPatientDAO;
import org.motechproject.scheduletrackingdemo.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EnrollController {

    private static final Logger LOGGER = Logger.getLogger(EnrollController.class);
    private static final String MAPPING_FILE_NAME = "simple-schedule.json";
    
    @Autowired
    MRSPatientDAO patientDAO;

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    private PatientScheduler patientSchedule;
    
    @RequestMapping("/enroll/start")
    public ModelAndView start(HttpServletRequest request, HttpServletResponse response) {
        String externalID = request.getParameter("externalID");
        String scheduleName = request.getParameter("scheduleName");

        patientSchedule.enrollIntoSchedule(externalID, scheduleName);

        return indexPage();
    }

    @RequestMapping("/enroll/stop")
    public ModelAndView stop(HttpServletRequest request, HttpServletResponse response) {
        String externalID = request.getParameter("externalID");
        String scheduleName = request.getParameter("scheduleName");
        
        List<String> scheduleNames = new ArrayList<String>();
        scheduleNames.add(scheduleName);
        try {
            scheduleTrackingService.unenroll(externalID, scheduleNames);
        } catch (InvalidEnrollmentException e) {
            LOGGER.warn("Could not unenroll externalId=" + externalID + ", scheduleName=" + scheduleName);
        }

        return indexPage();
    }

    @RequestMapping("/patient/add")
    public ModelAndView addScheduleUser(HttpServletRequest request, HttpServletResponse response) {
        String phoneNum = request.getParameter("phoneNum");
        String externalID = request.getParameter("externalId");

        patientSchedule.saveMotechPatient(externalID, phoneNum);
        
        return indexPage();
    }

    @RequestMapping("/patient/remove")
    public ModelAndView removeScheduleUser(HttpServletRequest request, HttpServletResponse response) {
        String externalID = request.getParameter("externalId");
        patientDAO.removePatient(externalID);

        return indexPage();
    }

    private ModelAndView indexPage() {
        return new ModelAndView("redirect:/scheduletrackingdemo/");
    }

    @RequestMapping({ "", "/" })
    public ModelAndView scheduleTracking(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView mv = new ModelAndView("scheduleTrackingPage");
        mv.addObject("patientsList", getPatientBeans());

        return mv;
    }

    private List<PatientBean> getPatientBeans() {
        List<Patient> patientList = patientDAO.findAllPatients();
        List<PatientBean> beans = new ArrayList<>();

        for (Patient patient : patientList) {
            PatientBean bean = new PatientBean();
            bean.setPatient(patient);

            EnrollmentRecord record = scheduleTrackingService.getEnrollment(patient.getExternalid(),
                    DemoConstants.DEMO_SCHEDULE_NAME);
            if (record != null) {
                bean.setCurrentMilestone(record.getCurrentMilestoneName());
                EnrollmentsQuery query = new EnrollmentsQuery().havingExternalId(patient.getExternalid())
                        .havingState(EnrollmentStatus.ACTIVE).havingSchedule(DemoConstants.DEMO_SCHEDULE_NAME);
                List<EnrollmentRecord> enrollments = scheduleTrackingService.search(query);
                if (enrollments.size() == 0) {
                    bean.setCurrentlyEnrolled(false);
                } else {
                    bean.setCurrentlyEnrolled(true);
                }
            } else {
                bean.setCurrentlyEnrolled(false);
                bean.setCurrentMilestone("Not currently enrolled");
            }

            beans.add(bean);
        }

        return beans;
    }

    @RequestMapping("/addschedules")
    public ModelAndView addschedules(HttpServletRequest request, HttpServletResponse response) {

        InputStream is = getClass().getClassLoader().getResourceAsStream(MAPPING_FILE_NAME);

        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(is, writer, "UTF-8");
        } catch (IOException e) {
        }

        scheduleTrackingService.add(writer.toString());

        return null;
    }

    public static class PatientBean {
        private Patient patient;
        private String currentMilestone;
        private Boolean currentlyEnrolled;

        public Patient getPatient() {
            return patient;
        }

        public void setPatient(Patient patient) {
            this.patient = patient;
        }

        public String getCurrentMilestone() {
            return currentMilestone;
        }

        public void setCurrentMilestone(String currentMilestone) {
            this.currentMilestone = currentMilestone;
        }

        public Boolean getCurrentlyEnrolled() {
            return currentlyEnrolled;
        }

        public void setCurrentlyEnrolled(Boolean currentlyEnrolled) {
            this.currentlyEnrolled = currentlyEnrolled;
        }
    }
}
