package org.motechproject.mapper.web.controllers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.mapper.domain.MRSMapping;
import org.motechproject.mapper.model.UploadRequest;
import org.motechproject.mapper.service.MRSMappingService;
import org.motechproject.mapper.service.MappingsReader;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

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
}
