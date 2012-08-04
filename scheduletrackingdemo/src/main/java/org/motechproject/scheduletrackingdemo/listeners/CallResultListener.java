package org.motechproject.scheduletrackingdemo.listeners;

import org.motechproject.ivr.event.IVREventDelegate;
import org.motechproject.ivr.model.CallDetailRecord;
import org.motechproject.outbox.api.service.VoiceOutboxService;
import org.motechproject.outbox.api.domain.OutboundVoiceMessage;
import org.motechproject.outbox.api.domain.VoiceMessageType;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.annotations.MotechListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class CallResultListener {

    private static Logger logger = LoggerFactory
            .getLogger(CallResultListener.class);

    @Autowired
    private VoiceOutboxService outboxService;

    @MotechListener(subjects = { "CALL_BUSY", "CALL_FAIL", "CALL_NO_ANSWER",
            "CALL_SUCCESS" })
    public void execute(MotechEvent event) {
        System.out.println("Handling event: " + event.getSubject());
        logger.debug("Handled call event");

        String phoneNumber = event.getParameters().get(IVREventDelegate.CALL_DETAIL_RECORD_KEY).toString();

        OutboundVoiceMessage outboundVoiceMessage = new OutboundVoiceMessage();
        outboundVoiceMessage.setId(phoneNumber); // Content is
                                                          // identified by phone
                                                          // number for now

        VoiceMessageType voiceMessageType = new VoiceMessageType();
        voiceMessageType.setCanBeReplayed(true);
        voiceMessageType.setCanBeSaved(true);

        String content = (String) event.getParameters().get("CallContent");
        String template = content.replace(".xml", "");
        voiceMessageType.setTemplateName(template);

        outboundVoiceMessage.setVoiceMessageType(voiceMessageType);

        outboxService.addMessage(outboundVoiceMessage);

    }
}
