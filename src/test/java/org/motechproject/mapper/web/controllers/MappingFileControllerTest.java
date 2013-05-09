package org.motechproject.mapper.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.model.UploadRequest;
import org.motechproject.mapper.service.MRSMappingService;
import org.motechproject.mapper.service.MappingsReader;
import org.springframework.test.web.server.setup.MockMvcBuilders;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class MappingFileControllerTest {

    @Mock
    private MappingsReader mappingsReader;
    @Mock
    private MRSMappingService mappingService;

    @Before
    public void setUp() {
        initMocks(this);
    }

    @Test
    public void shouldAddingMrsMappingAndRedirect() throws IOException {
        byte[] contentAsByte = new byte[1];
        CommonsMultipartFile jsonFile = mock(CommonsMultipartFile.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        UploadRequest uploadRequest = new UploadRequest();
        uploadRequest.setJsonFile(jsonFile);
        when(jsonFile.getBytes()).thenReturn(contentAsByte);
        when(request.getHeader("Referer")).thenReturn("url");
        List<MRSMapping> mappings = new ArrayList<>();
        when(mappingsReader.readJson(new String(contentAsByte))).thenReturn(mappings);
        MappingFileController mappingFileController = new MappingFileController(mappingsReader, mappingService);

        String redirectUrl = mappingFileController.updateMappingFile(uploadRequest, request);

        verify(mappingService).addOrUpdate(mappings);
        assertEquals("redirect:url", redirectUrl);

    }

    @Test
    public void shouldDeleteMappings() throws Exception {
        String xmlns = "http://bihar.commcarehq.org/pregnancy/registration";
        MappingFileController mappingFileController = new MappingFileController(mappingsReader, mappingService);
        MockMvcBuilders.standaloneSetup(mappingFileController).build().perform(delete("/deleteMapping").param("xmlns", xmlns))
                .andExpect(status().isOk())
                .andExpect(content().string("Mapping deleted successfully"));

        verify(mappingService).deleteMapping(xmlns);
    }
}
