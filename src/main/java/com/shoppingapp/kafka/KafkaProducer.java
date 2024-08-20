package com.shoppingapp.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaProducer {
	
	@Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
	
	public void sendOrderMessage(String key, String message) {
        kafkaTemplate.send("orders", key, message);
        log.info("order message sent to kafka topic 'orders'");
    }

	
}
