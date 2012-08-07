package org.motechproject.CampaignDemo.controllers;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.motechproject.CampaignDemo.dao.PatientDAO;
import org.motechproject.CampaignDemo.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

/* Spring controller for adding and removing users from a patient database using
 * Couch Patients minimally need a phone number and external id in order to make
 * calls from campaign messages.
 */

@Controller
public class UserController extends MultiActionController {

    @Autowired
    private PatientDAO patientDAO;

    public UserController() {
    }

    public UserController(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
    }

    @RequestMapping(value = "user/add", method = RequestMethod.POST)
    private ModelAndView add(String returnPage, HttpServletRequest request) {
        List<Patient> patientList = null;

        String phoneNum = request.getParameter("phoneNum");
        String externalID = request.getParameter("externalId");

        if (externalID.length() == 0 || externalID.equals("")
                || externalID.trim().length() == 0) {
        } else {
            patientList = patientDAO.findByExternalid(externalID); // Only one
                                                                   // patient
                                                                   // should be
                                                                   // returned
                                                                   // if ID is
                                                                   // unique,
                                                                   // but it is
                                                                   // still
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

        patientList = patientDAO.findAllPatients();

        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only

        ModelAndView mv = new ModelAndView(returnPage, modelMap);

        return mv;
    }

    @RequestMapping(value = "user/remove", method = RequestMethod.POST)
    private ModelAndView remove(String returnPage, HttpServletRequest request) {

       try{
    	String externalID = request.getParameter("externalId");

        patientDAO.removePatient(externalID);

        List<Patient> patientList = patientDAO.findAllPatients();

        Map<String, Object> modelMap = new TreeMap<String, Object>();
        modelMap.put("patients", patientList); // List of patients is for
                                               // display purposes only
                
              
       ModelAndView mv = new ModelAndView(returnPage, modelMap);
       return mv;
       }
       catch(Exception e){
    	   ModelAndView mv = new ModelAndView(returnPage);
           return mv;
       }
        

        
    }

    @RequestMapping(value = "user/addCronUser", method = RequestMethod.POST)
    public ModelAndView addCronUser(HttpServletRequest request,
            HttpServletResponse response) {
        return add("cronFormPage", request);
    }

    @RequestMapping(value = "user/removeCronUser", method = RequestMethod.POST)
    public ModelAndView removeCronUser(HttpServletRequest request,
            HttpServletResponse response) {
        return remove("cronFormPage", request);
    }

    @RequestMapping(value = "user/addOffsetUser", method = RequestMethod.POST)
    public ModelAndView addOffsetUser(HttpServletRequest request,
            HttpServletResponse response) {
        return add("formPage", request);
    }

    @RequestMapping(value = "user/removeOffsetUser", method = RequestMethod.POST)
    public ModelAndView removeOffsetUser(HttpServletRequest request,
            HttpServletResponse response) {
        return remove("formPage", request);
    }

}
