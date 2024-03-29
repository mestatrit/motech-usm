package org.motechproject.mobileforms.api.web;

import static ch.lambdaj.Lambda.collect;
import static ch.lambdaj.Lambda.on;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fcitmuk.epihandy.EpihandyXformSerializer;
import org.motechproject.mobileforms.api.domain.Form;
import org.motechproject.mobileforms.api.domain.FormGroup;
import org.motechproject.mobileforms.api.service.MobileFormsService;
import org.motechproject.mobileforms.api.service.UsersService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jcraft.jzlib.JZlib;
import com.jcraft.jzlib.ZOutputStream;

@Controller
public class FormDownloadServlet extends BaseFormServlet {
    private final Logger log = LoggerFactory.getLogger(FormUploadServlet.class);

    public static final byte ACTION_DOWNLOAD_STUDY_LIST = 2;
    public static final byte ACTION_DOWNLOAD_USERS_AND_FORMS = 11;

    private final UsersService usersService;
    private final MobileFormsService mobileFormsService;

    @Autowired
    public FormDownloadServlet(UsersService usersService, MobileFormsService mobileFormsService) {
        this.usersService = usersService;
        this.mobileFormsService = mobileFormsService;
    }

    @RequestMapping(value = "/formdownload", method = RequestMethod.POST)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        ZOutputStream zOutput = new ZOutputStream(response.getOutputStream(), JZlib.Z_BEST_COMPRESSION);
        DataInputStream dataInput = new DataInputStream(request.getInputStream());
        DataOutputStream dataOutput = new DataOutputStream(zOutput);
        try {
            readParameters(dataInput);
            byte action = readActionByte(dataInput);
            ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            if (action == ACTION_DOWNLOAD_STUDY_LIST) {
                handleDownloadStudies(byteStream);

            } else if (action == ACTION_DOWNLOAD_USERS_AND_FORMS) {
                handleDownloadUsersAndForms(byteStream, dataInput);
            }

            dataOutput.writeByte(RESPONSE_SUCCESS);
            dataOutput.write(byteStream.toByteArray());
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("successfully downloaded the xforms");

        } catch (Exception e) {
            dataOutput.writeByte(RESPONSE_ERROR);
            throw new ServletException(FAILED_TO_SERIALIZE_DATA, e);
        } finally {
            dataOutput.flush();
            zOutput.finish();
            response.flushBuffer();
        }
    }

    private void handleDownloadStudies(ByteArrayOutputStream byteStream) throws Exception {
        EpihandyXformSerializer serializer = serializer();
        serializer.serializeStudies(byteStream, mobileFormsService.getAllFormGroups());
    }

    private void handleDownloadUsersAndForms(ByteArrayOutputStream byteStream, DataInputStream dataInput)
            throws Exception {
        EpihandyXformSerializer epiSerializer = serializer();
        epiSerializer.serializeUsers(byteStream, usersService.getUsers());
        int studyIndex = dataInput.readInt();
        FormGroup groupNameAndForms = mobileFormsService.getForms(studyIndex);
        List<String> formsXmlContent = collect(groupNameAndForms.getForms(), on(Form.class).content());
        epiSerializer.serializeForms(byteStream, formsXmlContent, studyIndex, groupNameAndForms.getName());
    }
}
