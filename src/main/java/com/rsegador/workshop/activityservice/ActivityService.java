package com.rsegador.workshop.activityservice;

import com.rsegador.workshop.activityservice.domain.TestersServiceLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ActivityService {

    public void logActivity(TestersServiceLog testersServiceLog) {
        log.info("Received message with Payload: [{}] from topic: [{}]",
                testersServiceLog.getPath(), "testers-service-activity");
    }
}
