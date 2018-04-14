package br.com.emersonborges.kafkaavro

import br.com.emersonborges.CustomerCreated
import org.apache.avro.generic.GenericRecord
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class MultipleSchemasConsumer {

    val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @KafkaListener(topics = ["customer"], containerFactory = "specificKafkaListenerContainerFactory")
    fun specificConsumer(event: CustomerCreated) {
        logger.info("SPECIFIC RECORD")
        logger.info("Event: $event")
        logger.info("Event customer fullName: ${event.getFullName()}")
    }

    @KafkaListener(topics = ["customer"], containerFactory = "genericKafkaListenerContainerFactory")
    fun genericConsumer(event: GenericRecord) {
        logger.info("GENERIC RECORD")
        logger.info("Event: $event")
        logger.info("Event customer fullName: ${event.get("fullName")}")
    }
}