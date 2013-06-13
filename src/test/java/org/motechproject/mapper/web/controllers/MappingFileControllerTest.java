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
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.server.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.server.result.MockMvcResultMatchers.status;

public class MappingFileControllerTest {

    @Mock
    private MappingsReader mappingsReader;
    @Mock
    private MRSMappingService mappingService;

    private MappingFileController mappingFileController;
    @Before
    public void setUp() {
        initMocks(this);
        mappingFileController = new MappingFileController(mappingsReader, mappingService);
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

        String redirectUrl = mappingFileController.updateMappingFile(uploadRequest, request);

        verify(mappingService).addOrUpdate(mappings);
        assertEquals("redirect:url", redirectUrl);

    }

    @Test
    public void shouldDeleteMappings() throws Exception {
        final String id = "myId";
        when(mappingService.deleteMapping(id)).thenReturn(true);
        MockMvcBuilders.standaloneSetup(mappingFileController).build().perform(delete("/mapping/" + id))
                .andExpect(status().isOk())
                .andExpect(content().string("Mapping deleted successfully"));

        verify(mappingService).deleteMapping(id);
    }

    @Test
    public void shouldThrowResouceNotFoundExceptionIfMappingNotFound() throws Exception {
        final String id = "myId";
        when(mappingService.deleteMapping(id)).thenReturn(false);
        MockMvcBuilders.standaloneSetup(mappingFileController).build().perform(delete("/mapping/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().string(""));

        verify(mappingService).deleteMapping(id);
    }

    @Test
    public void shouldSortTheListWhileReturingAllMappings() {
        MRSMapping mrsMapping1 = new MRSMapping();
        mrsMapping1.setXmlns("xmlns2");
        mrsMapping1.setVersion("version1");

        MRSMapping mrsMapping2 = new MRSMapping();
        mrsMapping2.setXmlns("xmlns2");
        mrsMapping2.setVersion("version2");

        MRSMapping mrsMapping3 = new MRSMapping();
        mrsMapping3.setXmlns("xmlns1");
        mrsMapping3.setVersion("version1");

        List<MRSMapping> mappings = Arrays.asList(mrsMapping1, mrsMapping3, mrsMapping2);
        when(mappingService.getAllMappings()).thenReturn(mappings);

        List<MRSMapping> actualList = mappingFileController.getAllMappings();

        verify(mappingService, times(1)).getAllMappings();

        assertEquals(mrsMapping3, actualList.get(0));
        assertEquals(mrsMapping1, actualList.get(1));
        assertEquals(mrsMapping2, actualList.get(2));
    }
}
