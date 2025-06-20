package com.flab.ecommerce.global.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.ProducerListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaProducerListenerConfig implements ProducerListener {

    @Override
    public void onSuccess(ProducerRecord producerRecord, RecordMetadata recordMetadata) {
        ProducerListener.super.onSuccess(producerRecord, recordMetadata);
        log.info("message value -> {}", producerRecord.value());
        log.info("message header -> {}", producerRecord.headers());
    }

    @Override
    public void onError(ProducerRecord producerRecord, RecordMetadata recordMetadata, Exception exception) {
        ProducerListener.super.onError(producerRecord, recordMetadata, exception);
        log.warn("message body -> {}", producerRecord.value());
        log.warn("message header -> {}", producerRecord.headers());
        log.warn("message exception -> {}", exception);
    }
}
