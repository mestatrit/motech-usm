package org.motechproject.mapper.model;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class UploadRequest {
    CommonsMultipartFile jsonFile;

    public CommonsMultipartFile getJsonFile() {
        return jsonFile;
    }

    public void setJsonFile(CommonsMultipartFile jsonFile) {
        this.jsonFile = jsonFile;
    }
}
