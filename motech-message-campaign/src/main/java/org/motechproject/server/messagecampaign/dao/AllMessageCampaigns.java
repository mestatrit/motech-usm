package org.motechproject.server.messagecampaign.dao;

import com.google.gson.reflect.TypeToken;
import org.motechproject.dao.MotechJsonReader;
import org.motechproject.server.messagecampaign.userspecified.CampaignRecord;
import org.motechproject.server.messagecampaign.domain.campaign.Campaign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Properties;

@Component
public class AllMessageCampaigns {

    public static final String MESSAGECAMPAIGN_DEFINITION_FILE = "messagecampaign.definition.file";

    private Properties properties;
    private MotechJsonReader motechJsonReader;

    @Autowired
    public AllMessageCampaigns(@Qualifier(value = "messageCampaignProperties") Properties properties, MotechJsonReader motechJsonReader) {
        this.properties = properties;
        this.motechJsonReader = motechJsonReader;
    }

    public Campaign get(String name) {
        List<CampaignRecord> campaigns =
                (List<CampaignRecord>) motechJsonReader.readFromFile(definitionFile(),
                        new TypeToken<List<CampaignRecord>>() {
                        }.getType());

        for (CampaignRecord campaign : campaigns) {
            if (campaign.name().equals(name)) return campaign.build();
        }
        return null;
    }

    private String definitionFile() {
        return this.properties.getProperty(MESSAGECAMPAIGN_DEFINITION_FILE);
    }
}