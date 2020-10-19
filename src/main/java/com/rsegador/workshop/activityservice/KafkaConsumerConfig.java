package com.rsegador.workshop.activityservice;

import com.rsegador.workshop.activityservice.domain.TestersServiceLog;
import lombok.AllArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;


@EnableKafka
@Configuration
@AllArgsConstructor
public class KafkaConsumerConfig {


    private KafkaProperties kafkaProperties;

    @Bean
    public ConsumerFactory<String, TestersServiceLog> testersServiceConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(),
                new StringDeserializer(),
                new JsonDeserializer<>(TestersServiceLog.class, false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TestersServiceLog> kafkaListenerTestersServiceContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TestersServiceLog> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(testersServiceConsumerFactory());
        return factory;
    }
}
