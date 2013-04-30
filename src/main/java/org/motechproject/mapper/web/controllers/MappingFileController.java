package org.motechproject.mapper.web.controllers;

import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.model.UploadRequest;
import org.motechproject.mapper.service.MRSMappingService;
import org.motechproject.mapper.service.MappingsReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class MappingFileController {

    private MappingsReader mappingsReader;
    private MRSMappingService mrsMappingService;
    private Logger logger = LoggerFactory.getLogger("commcare-mrs-mapper");

    @Autowired
    public MappingFileController(MappingsReader mappingsReader, MRSMappingService mrsMappingService) {
        this.mappingsReader = mappingsReader;
        this.mrsMappingService = mrsMappingService;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String updateMappingFile(@ModelAttribute("uploadRequest") UploadRequest uploadRequest, HttpServletRequest httpServletRequest) throws IOException {
        String mappingJson = new String(uploadRequest.getJsonFile().getBytes());
        logger.info("Importing JSON Mapper file : " + mappingJson);
        List<MRSMapping> mrsMappings = mappingsReader.readJson(mappingJson);
        mrsMappingService.addOrUpdate(mrsMappings);
        return "redirect:" + httpServletRequest.getHeader("Referer");
    }
}