package com.shoppingapp.kafka;

import java.util.ArrayList;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
	
	private final List<String> messages = new ArrayList<>();

    @KafkaListener(topics = "orders", groupId = "admin")
    public void consume(ConsumerRecord<String, String> record) {
        messages.add(record.value());
    }

    public List<String> getMessages() {
        return new ArrayList<>(messages);
    }

}
