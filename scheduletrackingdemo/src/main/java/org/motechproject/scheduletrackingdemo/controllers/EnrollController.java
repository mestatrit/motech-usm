package org.motechproject.scheduletrackingdemo.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.motechproject.scheduletrackingdemo.OpenMrsClient;
import org.motechproject.scheduletrackingdemo.PatientScheduler;
import org.motechproject.scheduletrackingdemo.DAO.MRSPatientDAO;
import org.motechproject.scheduletrackingdemo.model.Patient;
import org.motechproject.scheduletracking.api.domain.exception.InvalidEnrollmentException;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class EnrollController extends MultiActionController {

    @Autowired
    MRSPatientDAO patientDAO;

    @Autowired
    private ScheduleTrackingService scheduleTrackingService;

    @Autowired
    private OpenMrsClient openMrsClient;

    @Autowired
    private PatientScheduler patientSchedule;

    public ModelAndView start(HttpServletRequest request,
            HttpServletResponse response) {

        String externalID = request.getParameter("externalID");
        String scheduleName = request.getParameter("scheduleName");

        patientSchedule.enrollIntoSchedule(externalID, scheduleName);

        List<Patient> patientList = patientDAO.findAllPatients();

        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only

        ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);

        return mv;

    }

    public ModelAndView stop(HttpServletRequest request,
            HttpServletResponse response) {

        String externalID = request.getParameter("externalID");
        String scheduleName = request.getParameter("scheduleName");
        List<String> scheduleNames = new ArrayList<String>();
        scheduleNames.add(scheduleName);
        try {
            scheduleTrackingService.unenroll(externalID, scheduleNames);
        } catch (InvalidEnrollmentException e) {
            logger.warn("Could not unenroll externalId=" + externalID
                    + ", scheduleName=" + scheduleName);
        }

        List<Patient> patientList = patientDAO.findAllPatients();

        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only

        ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);

        return mv;
    }

    /**
     * For testing purposes only
     * 
     * @param request
     * @param response
     * @return
     */
    /*
     * public ModelAndView fulfill(HttpServletRequest request,
     * HttpServletResponse response) {
     * 
     * String externalID = request.getParameter("externalID"); String
     * scheduleName = request.getParameter("scheduleName");
     * 
     * EnrollmentRequest enrollmentRequest = new EnrollmentRequest(externalID,
     * scheduleName, new Time(LocalTime.now()), DateUtil.today(), new
     * Time(LocalTime.now()), DateUtil.today(), new Time(LocalTime.now()),
     * schedule.getNextMilestoneName(lastConceptFulfilled), null);
     * 
     * scheduleTrackingService.fulfillCurrentMilestone(externalID, scheduleName,
     * DateUtil.today());
     * 
     * List<Patient> patientList = patientDAO.findAllPatients();
     * 
     * Map<String, Object> modelMap = new TreeMap<String, Object>();
     * modelMap.put("patients", patientList); //List of patients is for display
     * purposes only
     * 
     * ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);
     * 
     * return mv;
     * 
     * }
     * 
     * /** For testing purposes
     * 
     * @param request
     * 
     * @param response
     * 
     * @return
     */
    /*
     * public ModelAndView obs(HttpServletRequest request, HttpServletResponse
     * response) { String externalID = request.getParameter("externalID");
     * String conceptName = request.getParameter("conceptName");
     * 
     * openMrsClient.printValues(externalID, conceptName);
     * 
     * openMrsClient.lastTimeFulfilledDateTimeObs(externalID, conceptName);
     * 
     * List<Patient> patientList = patientDAO.findAllPatients();
     * 
     * Map<String, Object> modelMap = new TreeMap<String, Object>();
     * modelMap.put("patients", patientList); //List of patients is for display
     * purposes only
     * 
     * ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);
     * 
     * return mv; }
     */

    public ModelAndView scheduleTracking(HttpServletRequest request,
            HttpServletResponse response) {

        List<Patient> patientList = patientDAO.findAllPatients();

        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only

        ModelAndView mv = new ModelAndView("scheduleTrackingPage", modelMap);

        return mv;
    }
}
