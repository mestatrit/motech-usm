package org.motechproject.CampaignDemo.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.UrlFilenameViewController;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;


//Spring controller for displaying the initial demo form page

@Controller
public class FormController extends MultiActionController {

    @RequestMapping(value = "/form/jsp", method = RequestMethod.GET)
    public ModelAndView cronCampaign(HttpServletRequest request,
            HttpServletResponse response) {
        return new ModelAndView("cronFormPage");
    }

    @RequestMapping(value = "/form/offsetForm", method = RequestMethod.GET)
    public ModelAndView offsetCampaign(HttpServletRequest request,
            HttpServletResponse response) {
        return new ModelAndView("formPage");
    }

    @RequestMapping(value = "/form/patients", method = RequestMethod.GET)
    public ModelAndView openMRSPatients(HttpServletRequest request,
            HttpServletResponse response) {
        return new ModelAndView("patients");
    }
    
    @RequestMapping(value = "/form/test", method = RequestMethod.GET)
    public ModelAndView test(HttpServletRequest request,
            HttpServletResponse response) {
        return new ModelAndView("testForm");
    }
}
