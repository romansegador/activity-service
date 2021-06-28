package com.rsegador.workshop.activityservice;

import au.com.dius.pact.consumer.MessagePactBuilder;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.consumer.junit5.ProviderType;
import au.com.dius.pact.core.model.annotations.Pact;
import au.com.dius.pact.core.model.messaging.Message;
import au.com.dius.pact.core.model.messaging.MessagePact;
import com.rsegador.workshop.activityservice.domain.TestersServiceLog;
import com.rsegador.workshop.activityservice.testersService.TestersServiceListener;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rsegador.workshop.activityservice.TestersServiceListenerPactTest.TestData.expectedMessage;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "testers-service", providerType = ProviderType.ASYNCH)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@EmbeddedKafka(topics = "testers-service-activity",
        bootstrapServersProperty = "spring.kafka.bootstrap-servers")
public class TestersServiceListenerPactTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @MockBean
    ActivityService activityService;

    @SpyBean
    TestersServiceListener testersServiceListener;

    @Pact(consumer = "activity-service")
    public MessagePact validateTestersServiceListener(MessagePactBuilder builder) {
        PactDslJsonBody body = new PactDslJsonBody();
        body.stringType("path", "path/example");
        return builder
                .expectsToReceive("valid activity from testers service")
                .withContent(body)
                .toPact();
    }

    @Test
    @SneakyThrows
    @PactTestFor(pactMethod = "validateTestersServiceListener")
    public void validTestersServiceMessageIsConsumed(List<Message> messages) {
        messages.forEach(message -> sendMessage(message.uniqueKey(), message.contentsAsString()));

        // Verify that the expected message is consumed by our listener
        verify(testersServiceListener, timeout(6000)).handle(expectedMessage);

        // Verify that the service is invoked by the listener with the expected logic.
        verify(activityService).logActivity(expectedMessage);
    }

    // Message from Pact is send to the kafka topic as string. The app TestersServiceListener will then consume it with
    // the same configuration as in the running application.
    private void sendMessage(String key, String message) {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        Producer<String, String> producer = new DefaultKafkaProducerFactory<>(configs, new StringSerializer(), new StringSerializer()).createProducer();

        producer.send(new ProducerRecord<>("testers-service-activity", key, message));
        producer.flush();
    }

    interface TestData {
        TestersServiceLog expectedMessage = TestersServiceLog.builder().path("path/example").build();
    }

}

