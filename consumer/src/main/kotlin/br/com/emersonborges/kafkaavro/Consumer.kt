package br.com.emersonborges.kafkaavro

import br.com.emersonborges.CustomerCreated
import org.apache.avro.generic.GenericRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class Consumer {

    @KafkaListener(topics = ["customer"], containerFactory = "specificKafkaListenerContainerFactory")
    fun specificConsumer(event: CustomerCreated) {
        println("SPECIFIC RECORD")
        println(event)
        println(event.getFullName())
    }

    @KafkaListener(topics = ["customer"], containerFactory = "genericKafkaListenerContainerFactory")
    fun genericConsumer(event: GenericRecord) {
        println("GENERIC RECORD")
        println(event)
        println(event.get("fullName"))
    }
}