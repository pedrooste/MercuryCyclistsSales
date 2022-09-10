package com.mercuryCyclists.Sales.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListeners {

    @KafkaListener(topics = "pedro", groupId = "ID1")
    void listener(String data) {
        System.out.println("Listener received \n" + data);
    }
}
