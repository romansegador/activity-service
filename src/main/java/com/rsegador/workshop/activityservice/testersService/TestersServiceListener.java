package com.rsegador.workshop.activityservice.testersService;

import com.rsegador.workshop.activityservice.ActivityService;
import com.rsegador.workshop.activityservice.domain.TestersServiceLog;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class TestersServiceListener {

	ActivityService activityService;

	@KafkaListener(topics = "testers-service-activity",
                   containerFactory = "kafkaListenerTestersServiceContainerFactory")
	public void handle(@Payload TestersServiceLog testersServiceLog) {
		activityService.logActivity(testersServiceLog);

	}
}
